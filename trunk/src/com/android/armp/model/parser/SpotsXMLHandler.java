package com.android.armp.model.parser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.android.armp.model.Spot;

public class SpotsXMLHandler extends MyDefaultHandler {

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean inRootTag = false;
	private boolean inSpotTag = false;
	private boolean inLatitudeTag = false;
	private boolean inUserTag = false;
	private boolean inNameTag = false;
	private boolean inLongitudeTag = false;
	private boolean inRadiusTag = false;
	private boolean inColorTag = false;
	private boolean inCreationTimeTag = false;
	private boolean inLastUpdateTag = false;

	private List<Spot> mParsed = null;
	private Spot mSpot = null;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	@Override
	public List<Spot> getParsedData() {
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
		} else if (localName.equals("user")) {
			this.inUserTag = true;
		} else if (localName.equals("name")) {
			this.inNameTag = true;
		} else if (localName.equals("latitude")) {
			this.inLatitudeTag = true;
		} else if (localName.equals("longitude")) {
			this.inLongitudeTag = true;
		} else if (localName.equals("radius")) {
			this.inRadiusTag = true;
		} else if (localName.equals("color")) {
			this.inColorTag = true;
		} else if (localName.equals("creation")) {
			this.inCreationTimeTag = true;
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
		if (localName.equals("root")) {
			this.inRootTag = false;
		} else if (localName.equals("spot")) {
			this.inSpotTag = false;
			mParsed.add(mSpot);
		} else if (localName.equals("user")) {
			this.inUserTag = false;
		} else if (localName.equals("name")) {
			this.inNameTag = false;
		} else if (localName.equals("latitude")) {
			this.inLatitudeTag = false;
		} else if (localName.equals("longitude")) {
			this.inLongitudeTag = false;
		} else if (localName.equals("radius")) {
			this.inRadiusTag = false;
		} else if (localName.equals("color")) {
			this.inColorTag = false;
		} else if (localName.equals("creation")) {
			this.inCreationTimeTag = false;
		} else if (localName.equals("update")) {
			this.inLastUpdateTag = false;
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
				mSpot.setColor(new String(ch, start, length));
			} else if (this.inCreationTimeTag) {
				mSpot.setCreationTime(Integer.parseInt(new String(ch, start,
						length)));
			} else if (this.inLastUpdateTag) {
				mSpot.setLastUpdate(Integer.parseInt(new String(ch, start,
						length)));
			}
		}
	}
}