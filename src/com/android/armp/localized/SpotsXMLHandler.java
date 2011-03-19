package com.android.armp.localized;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class SpotsXMLHandler extends MyDefaultHandler {

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean inFolderTag = false;
	private boolean inPlacemarkTag = false;
	private boolean inPointTag = false;
	private boolean inCoordinatesTag = false;
	private boolean inRadiusTag = false;

	private List<MusicSpot> mParsed = null;
	private MusicSpot mSpot = null;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	@Override
	public List<MusicSpot> getParsedData() {
		return this.mParsed;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException {
		this.mParsed = new ArrayList<MusicSpot>();
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
		if (localName.equals("Folder")) {
			this.inFolderTag = true;
		} else if (localName.equals("Placemark")) {
			this.inPlacemarkTag = true;
			String attrValue = atts.getValue("id");
			int id = Integer.parseInt(attrValue);
			mSpot = new MusicSpot(id);
		} else if (localName.equals("Radius")) {
			this.inRadiusTag = true;
		} else if (localName.equals("Point")) {
			this.inPointTag = true;
		} else if (localName.equals("coordinates")) {
			this.inCoordinatesTag = true;
			/*
			 * String attrValue = atts.getValue("thenumber"); int i =
			 * Integer.parseInt(attrValue);
			 * myParsedExampleDataSet.setExtractedInt(i);
			 */
		}
	}

	/**
	 * Gets be called on closing Tags like: </Tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("Folder")) {
			this.inFolderTag = false;
		} else if (localName.equals("Placemark")) {
			this.inPlacemarkTag = false;
		} else if (localName.equals("Radius")) {
			this.inRadiusTag = false;
		} else if (localName.equals("Point")) {
			this.inPointTag = false;
		} else if (localName.equals("coordinates")) {
			mParsed.add(mSpot);
			this.inCoordinatesTag = false;
		}
	}

	/**
	 * Gets be called on the following structure: <Tag>characters</Tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		if (this.inRadiusTag) {
			String str = new String(ch, start, length);
			mSpot.setRadius((float)Double.parseDouble(str)/100.0f);
		} else if (this.inCoordinatesTag) {
			String coords = new String(ch, start, length);
			int fc = coords.indexOf(','), lc = coords.lastIndexOf(',');
			mSpot.setLongitude(new Double(coords.substring(0, fc)));
			mSpot.setLatitude(new Double(coords.substring(fc + 1, lc)));
		}
	}
}