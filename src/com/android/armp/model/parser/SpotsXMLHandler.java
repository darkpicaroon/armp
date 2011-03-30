package com.android.armp.model.parser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.graphics.Color;
import android.util.Log;

import com.android.armp.localized.MusicSourceSolver;
import com.android.armp.model.Channel;
import com.android.armp.model.Music;
import com.android.armp.model.Spot;

public class SpotsXMLHandler extends MyDefaultHandler {
	private final static String TAG = "SpotsXMLHandler";

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean inRootTag = false;
	
	/**
	 * Spots fields
	 */
	private boolean inSpotTag = false;
	private boolean inLatitudeTag = false;
	private boolean inUserTag = false;
	private boolean inNameTag = false;
	private boolean inLongitudeTag = false;
	private boolean inRadiusTag = false;
	private boolean inColorTag = false;
	private boolean inCreationTag = false;
	private boolean inLastUpdateTag = false;
	
	/**
	 * Channels fields
	 */
	private boolean inChannelsTag = false;
	private boolean inChannelTag = false;
	private boolean inChannelNameTag = false;
	private boolean inChannelUserTag = false;
	private boolean inGenreTag = false;
	private boolean inChannelCreationTag = false;
	private boolean inChannelLastUpdateTag = false;
	
	
	/**
	 * Musics fields
	 */
	private boolean inMusicsTag = false;
	private boolean inMusicTag = false;
	private boolean inMetadataTag = false;
	private boolean inTitleTag = false;
	private boolean inArtistTag = false;
	private boolean inAlbumTag = false;
	private boolean inYearTag = false;
	private boolean inTrackTag = false;
	private boolean inMusicGenreTag = false;
	private boolean inDurationTag = false;
	private boolean inSourceTag = false;
	private boolean gotSourceTag = false;

	private ArrayList<Spot> mParsed = null;
	private ArrayList<Channel> mChannels = null;
	private ArrayList<Music> mMusics = null;
	private Spot mSpot = null;
	private Channel mChannel = null;
	private Music mMusic = null;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	@Override
	public ArrayList<Spot> getParsedData() {
		return this.mParsed;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException {
		this.mParsed = new ArrayList<Spot>();
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
		} else if (localName.equals("spot")) {
			this.inSpotTag = true;
			String attr = atts.getValue("id");
			if (attr != null) {
				mSpot = new Spot(Integer.parseInt(attr));
			}
		} else if (localName.equals("user") && !inChannelTag) {
			this.inUserTag = true;
		} else if (localName.equals("name") && !inChannelTag) {
			this.inNameTag = true;
		} else if (localName.equals("latitude")) {
			this.inLatitudeTag = true;
		} else if (localName.equals("longitude")) {
			this.inLongitudeTag = true;
		} else if (localName.equals("radius")) {
			this.inRadiusTag = true;
		} else if (localName.equals("color")) {
			this.inColorTag = true;
		} else if (localName.equals("creation") && !inChannelTag) {
			this.inCreationTag = true;
		} else if (localName.equals("update") && !inChannelTag) {
			this.inLastUpdateTag = true;
		} else if (localName.equals("channels")) {
			this.inChannelsTag = true;
			mChannels = new ArrayList<Channel>();
		} else if (localName.equals("channel")) {
			this.inChannelTag = true;
			String id = atts.getValue("id");
			String sid = atts.getValue("spotId");
			if (id != null) {
				mChannel = new Channel(Integer.parseInt(id));
				if (sid != null) {
					mChannel.setSpotId(Integer.parseInt(sid));
				}
			}
		} else if (localName.equals("name") && inChannelTag) {
			this.inChannelNameTag = true;
		} else if (localName.equals("user") && inChannelTag) {
			this.inChannelUserTag = true;
		} else if (localName.equals("genre") && !inMusicTag) {
			this.inGenreTag = true;
		} else if (localName.equals("creation") && inChannelTag) {
			this.inChannelCreationTag = true;
		} else if (localName.equals("update") && inChannelTag) {
			this.inChannelLastUpdateTag = true;
		}  else if (localName.equals("musics")) {
			this.inMusicsTag = true;
			mMusics = new ArrayList<Music>();
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
			this.inMusicGenreTag = true;
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
		} else if (localName.equals("spot")) {
			this.inSpotTag = false;
			mParsed.add(mSpot);
		} else if (localName.equals("user") && !inChannelTag) {
			this.inUserTag = false;
		} else if (localName.equals("name") && !inChannelTag) {
			this.inNameTag = false;
		} else if (localName.equals("latitude")) {
			this.inLatitudeTag = false;
		} else if (localName.equals("longitude")) {
			this.inLongitudeTag = false;
		} else if (localName.equals("radius")) {
			this.inRadiusTag = false;
		} else if (localName.equals("color")) {
			this.inColorTag = false;
		} else if (localName.equals("creation") && !inChannelTag) {
			this.inCreationTag = false;
		} else if (localName.equals("update") && !inChannelTag) {
			this.inLastUpdateTag = false;
		} else if (localName.equals("channels")) {
			this.inChannelsTag = false;
			mSpot.setChannels(mChannels);
		} else if (localName.equals("channel")) {
			this.inChannelTag = false;
			mChannels.add(mChannel);
		} else if (localName.equals("name") && inChannelTag) {
			this.inChannelNameTag = false;
		} else if (localName.equals("user") && inChannelTag) {
			this.inChannelUserTag = false;
		} else if (localName.equals("genre") && !inMusicTag) {
			this.inGenreTag = false;
		} else if (localName.equals("creation") && inChannelTag) {
			this.inChannelCreationTag = false;
		} else if (localName.equals("update") && inChannelTag) {
			this.inChannelLastUpdateTag = false;
		} else if (localName.equals("musics")) {
			this.inMusicsTag = false;
			mChannel.setMusics(mMusics);
			mChannel.setCountOfMusics(mMusics.size());
		} else if (localName.equals("music")) {
			this.inMusicTag = false;
			if (!this.gotSourceTag) {
				MusicSourceSolver.solveMusicSource(mMusic);
			}
			this.gotSourceTag = false;
			mMusics.add(mMusic);
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
			this.inMusicGenreTag = false;
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
		if (this.inRootTag && this.inSpotTag) {
			if (this.inUserTag) {
				mSpot.setUser(new String(ch, start, length));
			} else if (this.inNameTag) {
				mSpot.setName(new String(ch, start, length));
			} else if (this.inLatitudeTag) {
				mSpot.setLatitude(new Double(new String(ch, start, length)));
			} else if (this.inLongitudeTag) {
				mSpot.setLongitude(new Double(new String(ch, start, length)));
			} else if (this.inRadiusTag) {
				mSpot.setRadius(Integer.parseInt(new String(ch, start, length)));
			} else if (this.inColorTag) {
				mSpot.setColor(Color.parseColor("#"+new String(ch, start, length)));
				Log.d(TAG, "Color: "+mSpot.getColor());
			} else if (this.inCreationTag) {
				mSpot.setCreationTime(Integer.parseInt(new String(ch, start,
						length)));
			} else if (this.inLastUpdateTag) {
				mSpot.setLastUpdate(Integer.parseInt(new String(ch, start,
						length)));
			} else if (this.inChannelNameTag) {
				mChannel.setName(new String(ch, start, length));
			} else if (this.inChannelUserTag) {
				mChannel.setUser(new String(ch, start, length));
			} else if (this.inGenreTag) {
				mChannel.setGenre(new String(ch, start, length));
			} else if (this.inChannelCreationTag) {
				mChannel.setCreationTime(new Integer(new String(ch, start,
						length)));
			} else if (this.inChannelLastUpdateTag) {
				mChannel.setLastUpdate(new Integer(
						new String(ch, start, length)));
			} else if (this.inMetadataTag) {
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
				} else if (this.inMusicGenreTag) {
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