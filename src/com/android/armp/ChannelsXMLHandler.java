package com.android.armp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.net.ParseException;

import com.android.armp.LocalizedMusicSpot.MusicChannel;
 
public class ChannelsXMLHandler extends DefaultHandler{
 
        // ===========================================================
        // Fields
        // ===========================================================
       
        private boolean in_channelstag = false;
        private boolean in_channeltag = false;
        private boolean in_nametag = false;
        private boolean in_genretag = false;
        private boolean in_genrenametag = false;
        private boolean in_creationtag = false;
        private boolean in_lastupdatetag = false;
        private boolean in_nbmusictag = false;
       
        private HashMap<Integer, MusicChannel> mParsed = null;
        private MusicChannel mChannel = null;
        private int mCurrChannelId;
 
        // ===========================================================
        // Getter & Setter
        // ===========================================================
 
        public HashMap<Integer, MusicChannel> getParsedData() {
                return this.mParsed;
        }
 
        // ===========================================================
        // Methods
        // ===========================================================
        @Override
        public void startDocument() throws SAXException {
                this.mParsed = new HashMap<Integer, MusicChannel>();
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
                if (localName.equals("Channels")) {
                        this.in_channelstag = true;
                }else if (localName.equals("Channel")) {
                        this.in_channeltag = true;
                        String attrValue = atts.getValue("id");
                        mCurrChannelId = Integer.parseInt(attrValue);
                        mChannel = LocalizedMusicSpot.newMusicChannel();
                }else if (localName.equals("Name") && !this.in_genretag) {
                        this.in_nametag = true;
                }else if (localName.equals("Genre")) {
                        this.in_genretag = true;
                        String attrValue = atts.getValue("id");
                        mChannel.setmGenreId(new Integer(attrValue));
                }else if (localName.equals("Name") && this.in_genretag) {
                    this.in_genrenametag = true;
                }else if (localName.equals("CreationTime")) {
                    this.in_creationtag = true;
                }else if (localName.equals("LastUpdate")) {
                    this.in_lastupdatetag = true;
                }else if (localName.equals("NbMusic")) {
                    this.in_nbmusictag = true;
                }
                
        }
       
        /** Gets be called on closing tags like:
         * </tag> */
        @Override
        public void endElement(String namespaceURI, String localName, String qName)
                        throws SAXException {
                if (localName.equals("Channels")) {
                        this.in_channelstag = false;
                }else if (localName.equals("Channel")) {
                        this.in_channeltag = false;
                        mParsed.put(mCurrChannelId, mChannel);
                }else if (localName.equals("Name") && !this.in_genretag) {
                        this.in_nametag = false;
                }else if (localName.equals("Genre")) {
                	this.in_genretag = false;
                }else if (localName.equals("Name") && this.in_genretag) {
                	this.in_genrenametag = false;
                }else if (localName.equals("CreationTime")) {
                	this.in_creationtag = false;
                }else if (localName.equals("LastUpdate")) {
                	this.in_lastupdatetag = false;
                }else if (localName.equals("NbMusic")) {
                	this.in_nbmusictag = false;
                }
        }
       
        /** Gets be called on the following structure:
         * <tag>characters</tag> */
        @Override
        public void characters(char ch[], int start, int length) {
        	if(this.in_nametag && !this.in_genretag){
        		String name = new String(ch, start, length);
        		mChannel.setmName(name);
        	} else if(this.in_genrenametag) {
        		// TODO Genre name
        		//String name = new String(ch, start, length);
        		//mChannel.setmName(name);
        	}else if(this.in_creationtag) {
        		String dateStr = new String(ch, start, length);
        		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        		try{
        			Date dateObj = curFormater.parse(dateStr); 
            		mChannel.setmCreationTime(dateObj);
        		} catch (Exception e) {
        			
        		}        		
        	}else if(this.in_lastupdatetag) {
        		String dateStr = new String(ch, start, length);
        		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        		try{
        			Date dateObj = curFormater.parse(dateStr); 
            		mChannel.setmLastUpdate(dateObj);
        		} catch (Exception e) {
        			
        		}        		
        	}else if(this.in_nbmusictag) {
        		String str = new String(ch, start, length);
        		mChannel.setNbMusic(new Integer(str));
        	}
        }
}