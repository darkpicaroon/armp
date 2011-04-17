package com.android.armp.model.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.android.armp.model.ObjectResponse;

public class MessageXMLHandler extends MyDefaultHandler {

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean inMessage = false;

	private String message = null;
	
	@Override
	public ObjectResponse getParsedData() {
		ObjectResponse r = super.getParsedData();
		r.setObject(message);
		return r;
	}

	/**
	 * Gets be called on opening Tags like: <Tag> Can provide attribute(s), when
	 * xml was like: <Tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		super.startElement(namespaceURI, localName, qName, atts);
		if (localName.equals("message")) {
			this.inMessage = true;
		}
	}

	/**
	 * Gets be called on closing Tags like: </Tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		super.endElement(namespaceURI, localName, qName);
		if (localName.equals("message")) {
			this.inMessage = false;
		}
	}

	/**
	 * Gets be called on the following structure: <Tag>characters</Tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		super.characters(ch, start, length);
		if (this.inRootTag && this.inMessage) {
			this.message = new String(ch, start, length);
		}
	}
}