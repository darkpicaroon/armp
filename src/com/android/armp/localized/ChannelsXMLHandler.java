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

import com.android.armp.localized.MusicChannel;

public class ChannelsXMLHandler extends MyDefaultHandler {

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean inChannelsTag = false;
	private boolean inChannelTag = false;
	private boolean inSpotIdTag = false;
	private boolean inNameTag = false;
	private boolean inGenreTag = false;
	private boolean inGenreNameTag = false;
	private boolean inCreationTag = false;
	private boolean inLastUpdateTag = false;
	private boolean inNbMusicTag = false;

	private List<MusicChannel> mParsed = null;
	private MusicChannel mChannel = null;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	@Override
	public List<MusicChannel> getParsedData() {
		return this.mParsed;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException {
		this.mParsed = new ArrayList<MusicChannel>();
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
		if (localName.equals("Channels")) {
			this.inChannelsTag = true;
		} else if (localName.equals("Channel")) {
			this.inChannelTag = true;
			String attrValue = atts.getValue("id");
			int id = Integer.parseInt(attrValue);
			mChannel = new MusicChannel(id);
		} else if (localName.equals("SpotId")) {
			this.inSpotIdTag = true;
		} else if (localName.equals("Name") && !this.inGenreTag) {
			this.inNameTag = true;
		} else if (localName.equals("Genre")) {
			this.inGenreTag = true;
			String attrValue = atts.getValue("id");
			mChannel.setGenreId(new Integer(attrValue));
		} else if (localName.equals("Name") && this.inGenreTag) {
			this.inGenreNameTag = true;
		} else if (localName.equals("CreationTime")) {
			this.inCreationTag = true;
		} else if (localName.equals("LastUpdate")) {
			this.inLastUpdateTag = true;
		} else if (localName.equals("NbMusic")) {
			this.inNbMusicTag = true;
		}

	}

	/**
	 * Gets be called on closing Tags like: </Tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("Channels")) {
			this.inChannelsTag = false;
		} else if (localName.equals("Channel")) {
			this.inChannelTag = false;
			mParsed.add(mChannel);
		} else if (localName.equals("SpotId")) {
			this.inSpotIdTag = false;
		} else if (localName.equals("Name") && !this.inGenreTag) {
			this.inNameTag = false;
		} else if (localName.equals("Genre")) {
			this.inGenreTag = false;
		} else if (localName.equals("Name") && this.inGenreTag) {
			this.inGenreNameTag = false;
		} else if (localName.equals("CreationTime")) {
			this.inCreationTag = false;
		} else if (localName.equals("LastUpdate")) {
			this.inLastUpdateTag = false;
		} else if (localName.equals("NbMusic")) {
			this.inNbMusicTag = false;
		}
	}

	/**
	 * Gets be called on the following structure: <Tag>characters</Tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		if (this.inSpotIdTag) {
			int spotId = Integer.parseInt(new String(ch, start, length));
			mChannel.setSpotId(spotId);
		} else if (this.inNameTag && !this.inGenreTag) {
			String name = new String(ch, start, length);
			mChannel.setName(name);
		} else if (this.inGenreNameTag) {
			String genre = new String(ch, start, length);
			mChannel.setGenre(genre);
		} else if (this.inCreationTag) {
			String dateStr = new String(ch, start, length);
			SimpleDateFormat curFormater = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			try {
				Date dateObj = curFormater.parse(dateStr);
				mChannel.setCreationTime(dateObj);
			} catch (Exception e) {

			}
		} else if (this.inLastUpdateTag) {
			String dateStr = new String(ch, start, length);
			SimpleDateFormat curFormater = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			try {
				Date dateObj = curFormater.parse(dateStr);
				mChannel.setLastUpdate(dateObj);
			} catch (Exception e) {

			}
		} else if (this.inNbMusicTag) {
			String str = new String(ch, start, length);
			mChannel.setNbMusic(new Integer(str));
		}
	}
}