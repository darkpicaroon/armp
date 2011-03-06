package com.android.armp;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.android.armp.LocalizedMusicService.Spot; 
 
public class SpotsXMLHandler extends DefaultHandler{
 
        // ===========================================================
        // Fields
        // ===========================================================
       
        private boolean in_foldertag = false;
        private boolean in_placemarktag = false;
        private boolean in_pointtag = false;
        private boolean in_coordinatestag = false;
       
        private ArrayList<LocalizedMusicService.Spot> mParsed = null;
        private LocalizedMusicService.Spot mSpot = null;
 
        // ===========================================================
        // Getter & Setter
        // ===========================================================
 
        public ArrayList<LocalizedMusicService.Spot> getParsedData() {
                return this.mParsed;
        }
 
        // ===========================================================
        // Methods
        // ===========================================================
        @Override
        public void startDocument() throws SAXException {
                this.mParsed = new ArrayList<LocalizedMusicService.Spot>();
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
                        mSpot = LocalizedMusicService.createSpot();
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
                        mParsed.add(mSpot);
                }
        }
       
        /** Gets be called on the following structure:
         * <tag>characters</tag> */
        @Override
        public void characters(char ch[], int start, int length) {
        	if(this.in_coordinatestag){
        		String coords = new String(ch, start, length);
        		int fc = coords.indexOf(','), lc = coords.lastIndexOf(',');
        		mSpot.setLat(new Double(coords.substring(0, fc)));
        		mSpot.setLon(new Double(coords.substring(fc+1, lc)));
        	}
        }
}