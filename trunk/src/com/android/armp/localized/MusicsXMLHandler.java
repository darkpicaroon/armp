package com.android.armp.localized;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.net.ParseException;
import android.util.Log;

import com.android.armp.localized.MusicChannel;

public class MusicsXMLHandler extends MyDefaultHandler {

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean inMusicsTag = false;
	private boolean inMusicTag = false;
	private boolean inMetadataTag = false;
	private boolean inTitleTag = false;
	private boolean inArtistTag = false;
	private boolean inAlbumTag = false;
	private boolean inYearTag = false;
	private boolean inTrackNbrTag = false;
	private boolean inGenreTag = false;
	private boolean inGenreNameTag = false;
	private boolean inDurationTag = false;
	private boolean inSourceTag = false;
	private boolean gotSourceTag = false;

	private List<MusicItem> mParsed = null;
	private MusicItem mMusic = null;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	@Override
	public List<MusicItem> getParsedData() {
		return this.mParsed;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException {
		this.mParsed = new ArrayList<MusicItem>();
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
		if (localName.equals("Musics")) {
			this.inMusicsTag = true;
		} else if (localName.equals("Music")) {
			this.inMusicTag = true;
			mMusic = new MusicItem(Integer.parseInt(atts.getValue("id")));
		} else if (localName.equals("Metadata")) {
			this.inMetadataTag = true;
		} else if (localName.equals("Title")) {
			this.inTitleTag = true;
		} else if (localName.equals("Artist")) {
			this.inArtistTag = true;
		} else if (localName.equals("Album")) {
			this.inAlbumTag = true;
		} else if (localName.equals("Year")) {
			this.inYearTag = true;
		} else if (localName.equals("Genre")) {
			this.inGenreTag = true;
			mMusic.setGenreId(Integer.parseInt(atts.getValue("id")));
		} else if (localName.equals("Name") && this.inGenreTag) {
			this.inGenreNameTag = true;
		} else if (localName.equals("Duration")) {
			this.inDurationTag = true;
		} else if (localName.equals("Source")) {
			this.inSourceTag = this.gotSourceTag = true;
		}
	}

	/**
	 * Gets be called on closing Tags like: </Tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("Musics")) {
			this.inMusicsTag = false;
		} else if (localName.equals("Music")) {
			this.inMusicTag = false;
			if(!this.gotSourceTag)
				MusicSourceSolver.solveMusicSource(mMusic);
			this.gotSourceTag = false;
			mParsed.add(mMusic);
		} else if (localName.equals("Metadata")) {
			this.inMetadataTag = false;
		} else if (localName.equals("Title")) {
			this.inTitleTag = false;
		} else if (localName.equals("Artist")) {
			this.inArtistTag = false;
		} else if (localName.equals("Album")) {
			this.inAlbumTag = false;
		} else if (localName.equals("Year")) {
			this.inYearTag = false;
		} else if (localName.equals("Genre")) {
			this.inGenreTag = false;
		} else if (localName.equals("Name") && this.inGenreTag) {
			this.inGenreNameTag = false;
		} else if (localName.equals("Duration")) {
			this.inDurationTag = false;
		} else if (localName.equals("Source")) {
			this.inSourceTag = false;
		}
	}

	/**
	 * Gets be called on the following structure: <Tag>characters</Tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		if (this.inTitleTag) {
			mMusic.setTitle(new String(ch, start, length));
		} else if (this.inArtistTag) {
			mMusic.setArtist(new String(ch, start, length));
		} else if (this.inAlbumTag) {
			mMusic.setAlbum(new String(ch, start, length));
		} else if (this.inYearTag) {
			mMusic.setYear(Integer.parseInt(new String(ch, start, length)));
		} else if (this.inTrackNbrTag) {
			mMusic.setTrackNumber(Integer.parseInt(new String(ch, start, length)));
		} else if (this.inGenreNameTag) {
			String genre = new String(ch, start, length);
			mMusic.setGenre(genre);
		} else if (this.inDurationTag) {
			mMusic.setDuration(Integer.parseInt(new String(ch, start, length)));
		} else if (this.inSourceTag) {
			Log.d("Parser", "Pas normal");
			mMusic.setSource(new String(ch, start, length));
		}
	}
}