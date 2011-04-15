package com.android.armp.model.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MyDefaultHandler extends DefaultHandler {
	
	protected boolean inRootTag   = false;
	private boolean inStatusTag = false;
	private boolean inLoggedTag = false;

	private int status;
	private boolean isLogged;
	
	public Object getParsedData() {
		return new Object[] { status, isLogged };
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
		} else if (localName.equals("status")) {
			this.inStatusTag = true;
		} else if (localName.equals("logged")) {
			this.inLoggedTag = true;
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
		} else if (localName.equals("status")) {
			this.inStatusTag = false;
		} else if (localName.equals("logged")) {
			this.inLoggedTag = false;
		}
	}

	/**
	 * Gets be called on the following structure: <Tag>characters</Tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		if (this.inRootTag) {
			if (this.inStatusTag) {
				this.status = new Integer(new String(ch, start, length));
			} else if (this.inLoggedTag) {
				this.isLogged = new String(ch, start, length).equals("1");
			}
		}
	}
}
