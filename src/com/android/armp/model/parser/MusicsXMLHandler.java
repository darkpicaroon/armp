package com.android.armp.model.parser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.android.armp.localized.MusicSourceSolver;
import com.android.armp.model.Music;

public class MusicsXMLHandler extends MyDefaultHandler {

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean inRootTag = false;
	private boolean inMusicTag = false;
	private boolean inMetadataTag = false;
	private boolean inTitleTag = false;
	private boolean inArtistTag = false;
	private boolean inAlbumTag = false;
	private boolean inYearTag = false;
	private boolean inTrackTag = false;
	private boolean inGenreTag = false;
	private boolean inDurationTag = false;
	private boolean inSourceTag = false;
	private boolean gotSourceTag = false;

	private List<Music> mParsed = null;
	private Music mMusic = null;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	@Override
	public List<Music> getParsedData() {
		return this.mParsed;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException {
		this.mParsed = new ArrayList<Music>();
	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
	}

	/**
	 * Gets be called on opening Tags like: <Tag> Can provide attribute(s), when
	 * xml was like: <Tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (localName.equals("root")) {
			this.inRootTag = true;
		} else if (localName.equals("music")) {
			this.inMusicTag = true;
			String attr = atts.getValue("id");
			if (attr != null) {
				mMusic = new Music(Integer.parseInt(attr));
			}
		} else if (localName.equals("metadata")) {
			this.inMetadataTag = true;
			String attr = atts.getValue("id");
			if (attr != null) {
				mMusic.setMetadataId(Integer.parseInt(attr));
			}
		} else if (localName.equals("title")) {
			this.inTitleTag = true;
		} else if (localName.equals("artist")) {
			this.inArtistTag = true;
		} else if (localName.equals("album")) {
			this.inAlbumTag = true;
		} else if (localName.equals("year")) {
			this.inYearTag = true;
		} else if (localName.equals("track")) {
			this.inTrackTag = true;
		} else if (localName.equals("genre")) {
			this.inGenreTag = true;
		} else if (localName.equals("duration")) {
			this.inDurationTag = true;
		} else if (localName.equals("source")) {
			this.inSourceTag = this.gotSourceTag = true;
		}
	}

	/**
	 * Gets be called on closing Tags like: </Tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("root")) {
			this.inRootTag = false;
		} else if (localName.equals("music")) {
			this.inMusicTag = false;
			if (!this.gotSourceTag) {
				MusicSourceSolver.solveMusicSource(mMusic);
			}
			this.gotSourceTag = false;
			mParsed.add(mMusic);
		} else if (localName.equals("metadata")) {
			this.inMetadataTag = false;
		} else if (localName.equals("title")) {
			this.inTitleTag = false;
		} else if (localName.equals("artist")) {
			this.inArtistTag = false;
		} else if (localName.equals("album")) {
			this.inAlbumTag = false;
		} else if (localName.equals("year")) {
			this.inYearTag = false;
		} else if (localName.equals("track")) {
			this.inTrackTag = false;
		} else if (localName.equals("genre")) {
			this.inGenreTag = false;
		} else if (localName.equals("duration")) {
			this.inDurationTag = false;
		} else if (localName.equals("source")) {
			this.inSourceTag = false;
		}
	}

	/**
	 * Gets be called on the following structure: <Tag>characters</Tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		if (this.inRootTag && this.inMusicTag) {
			if (this.inMetadataTag) {
				if (this.inTitleTag) {
					mMusic.setTitle(new String(ch, start, length));
				} else if (this.inArtistTag) {
					mMusic.setArtist(new String(ch, start, length));
				} else if (this.inAlbumTag) {
					mMusic.setAlbum(new String(ch, start, length));
				} else if (this.inYearTag) {
					mMusic.setYear(Integer.parseInt(new String(ch, start,
							length)));
				} else if (this.inTrackTag) {
					mMusic.setTrackNumber(Integer.parseInt(new String(ch,
							start, length)));
				} else if (this.inGenreTag) {
					mMusic.setGenre(new String(ch, start, length));
				} else if (this.inDurationTag) {
					mMusic.setDuration(Integer.parseInt(new String(ch, start,
							length)));
				}
			}
			if (this.inSourceTag) {
				mMusic.setSource(new String(ch, start, length));
				mMusic.setIsPlayable(true);
			}
		}
	}
}