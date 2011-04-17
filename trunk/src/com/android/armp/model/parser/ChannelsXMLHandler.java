package com.android.armp.model.parser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.android.armp.model.Channel;
import com.android.armp.model.ObjectResponse;

public class ChannelsXMLHandler extends MyDefaultHandler {

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean inChannelTag = false;
	private boolean inNameTag = false;
	private boolean inUserTag = false;
	private boolean inGenreTag = false;
	private boolean inCreationTag = false;
	private boolean inLastUpdateTag = false;
	private boolean inMusicsTag = false;

	private List<Channel> mParsed = null;
	private Channel mChannel = null;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	@Override
	public ObjectResponse getParsedData() {
		ObjectResponse r = super.getParsedData();
		r.setObject(this.mParsed);
		return r;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException {
		this.mParsed = new ArrayList<Channel>();
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
		super.startElement(namespaceURI, localName, qName, atts);
		if (localName.equals("channel")) {
			this.inChannelTag = true;
			String id = atts.getValue("id");
			String sid = atts.getValue("spotId");
			if (id != null) {
				mChannel = new Channel(Integer.parseInt(id));
				if (sid != null) {
					mChannel.setSpotId(Integer.parseInt(sid));
				}
			}
		} else if (localName.equals("name")) {
			this.inNameTag = true;
		} else if (localName.equals("user")) {
			this.inUserTag = true;
		} else if (localName.equals("genre")) {
			this.inGenreTag = true;
		} else if (localName.equals("musics")) {
			this.inMusicsTag = true;
		} else if (localName.equals("creation")) {
			this.inCreationTag = true;
		} else if (localName.equals("update")) {
			this.inLastUpdateTag = true;
		}
	}

	/**
	 * Gets be called on closing Tags like: </Tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		super.endElement(namespaceURI, localName, qName);
		if (localName.equals("channel")) {
			this.inChannelTag = false;
			mParsed.add(mChannel);
		} else if (localName.equals("name")) {
			this.inNameTag = false;
		} else if (localName.equals("user")) {
			this.inUserTag = false;
		} else if (localName.equals("genre")) {
			this.inGenreTag = false;
		} else if (localName.equals("creation")) {
			this.inCreationTag = false;
		} else if (localName.equals("update")) {
			this.inLastUpdateTag = false;
		} else if (localName.equals("musics")) {
			this.inMusicsTag = false;
		}
	}

	/**
	 * Gets be called on the following structure: <Tag>characters</Tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		super.characters(ch, start, length);
		if (this.inRootTag && this.inChannelTag) {
			if (this.inNameTag) {
				mChannel.setName(new String(ch, start, length));
			} else if (this.inUserTag) {
				mChannel.setUser(new String(ch, start, length));
			} else if (this.inGenreTag) {
				mChannel.setGenre(new String(ch, start, length));
			} else if (this.inCreationTag) {
				mChannel.setCreationTime(new Integer(new String(ch, start,
						length)));
			} else if (this.inLastUpdateTag) {
				mChannel.setLastUpdate(new Integer(
						new String(ch, start, length)));
			} else if (this.inMusicsTag) {
				mChannel.setCountOfMusics(new Integer(new String(ch, start,
						length)));
			}
		}
	}
}