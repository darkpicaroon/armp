package com.android.armp;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
 
public class SpotsXMLHandler extends DefaultHandler{
 
        // ===========================================================
        // Fields
        // ===========================================================
       
        private boolean in_foldertag = false;
        private boolean in_placemarktag = false;
        private boolean in_pointtag = false;
        private boolean in_coordinatestag = false;
       
        private HashMap<Integer, LocalizedMusicSpot> mParsed = null;
        private LocalizedMusicSpot mSpot = null;
        private int mCurrSpotId;
 
        // ===========================================================
        // Getter & Setter
        // ===========================================================
 
        public HashMap<Integer, LocalizedMusicSpot> getParsedData() {
                return this.mParsed;
        }
 
        // ===========================================================
        // Methods
        // ===========================================================
        @Override
        public void startDocument() throws SAXException {
                this.mParsed = new HashMap<Integer, LocalizedMusicSpot>();
        }
 
        @Override
        public void endDocument() throws SAXException {
                // Nothing to do
        }
 
        /** Gets be called on opening tags like:
         * <tag>
         * Can provide attribute(s), when xml was like:
         * <tag attribute="attributeValue">*/
        @Override
        public void startElement(String namespaceURI, String localName,
                        String qName, Attributes atts) throws SAXException {
                if (localName.equals("Folder")) {
                        this.in_foldertag = true;
                }else if (localName.equals("Placemark")) {
                        this.in_placemarktag = true;
                        String attrValue = atts.getValue("id");
                        mCurrSpotId = Integer.parseInt(attrValue);
                        mSpot = new LocalizedMusicSpot();
                }else if (localName.equals("Point")) {
                        this.in_pointtag = true;
                }else if (localName.equals("coordinates")) {
                        this.in_coordinatestag = true;
                        /*String attrValue = atts.getValue("thenumber");
                        int i = Integer.parseInt(attrValue);
                        myParsedExampleDataSet.setExtractedInt(i);*/
                }
        }
       
        /** Gets be called on closing tags like:
         * </tag> */
        @Override
        public void endElement(String namespaceURI, String localName, String qName)
                        throws SAXException {
                if (localName.equals("Folder")) {
                        this.in_foldertag = false;
                }else if (localName.equals("Placemark")) {
                        this.in_placemarktag = false;
                }else if (localName.equals("Point")) {
                        this.in_pointtag = false;
                }else if (localName.equals("coordinates")) {
                        mParsed.put(mCurrSpotId,mSpot);
                }
        }
       
        /** Gets be called on the following structure:
         * <tag>characters</tag> */
        @Override
        public void characters(char ch[], int start, int length) {
        	if(this.in_coordinatestag){
        		String coords = new String(ch, start, length);
        		int fc = coords.indexOf(','), lc = coords.lastIndexOf(',');
        		mSpot.setmLongitude(new Double(coords.substring(0, fc)));
        		mSpot.setmLattitude(new Double(coords.substring(fc+1, lc)));
        	}
        }
}