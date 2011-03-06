package com.android.armp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.armp.LocalizedMusicSpot.MusicChannel;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.SyncStateContract.Constants;
import android.util.Log;

public class LocalizedMusicService extends Service implements LocationListener {	
	private LocationManager mLocationMng;
	private Messenger mClient = null;
	private HashMap<Integer, LocalizedMusicSpot> mSpots;
	private static final String TAG = "LocalizedMusicService";
	
	private static final String baseUrl = "http://abarreir.com/";
	private static final String userAgent = "";
	
	/**
	 * Messages
	 */
	static final int MSG_SPOTS_UPDATE = 1;
	static final int MSG_CHANNELS = 2;
	static final int MSG_MUSICS = 3;
    

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SPOTS_UPDATE:
                	if(mClient == null) mClient = msg.replyTo;
                	
                	// Request a spot update from the newtork
                	makeHTTPRequest(MSG_SPOTS_UPDATE, "");

                    break;
                case MSG_CHANNELS:
                	Integer spotKey = (Integer)msg.obj;
                	
                	// If we don't have any channel for this spot, retrieve them
                	if(mSpots.get(spotKey).getmChannels().size() == 0) {
                		makeHTTPRequest(MSG_CHANNELS, spotKey.toString());
                	}
                	// Else just return the channels hash
                	else {
                		try {
                			Message answ = Message.obtain(null, 
    								LocalizedMusicService.MSG_CHANNELS, 
    								mSpots.get(spotKey).getmChannels());
                    		mClient.send(answ);
                		}
                		catch(Exception e) {
                			
                		}                		
                	}
                	
                	break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public void onCreate() {    	
    	// Retrieve the LocationManager to enable listening to location updates
        mLocationMng = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationMng.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, this);
        
        // Initialize the spots array
        mSpots = new HashMap<Integer, LocalizedMusicSpot>();
    }

    @Override
    public void onDestroy() {
    }

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
    	System.out.println("Service bound");
    	   	
        return mMessenger.getBinder();
    }

	public void onLocationChanged(Location location) {
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	/**
	 * 
	 * @param reqId The kind of request we are performing
	 */
	private void makeHTTPRequest(final int reqId, final String params) {
		// Fire off a thread to do some work that we shouldn't do directly in the UI thread
		Thread t = new Thread() {
			public void run() {
				try {
					// Create an http client
					AndroidHttpClient httpclient =  AndroidHttpClient.newInstance(userAgent);
					
					// Generate the string regarding the reqId
					String requestURL = baseUrl;
					Object response = null;
					
					switch(reqId) {
					case MSG_SPOTS_UPDATE:
						Log.i(TAG, "Updating spots");
						requestURL +="?long=6.218146957908431&lat=49.10159292113388";
						
						// Reset the current spots
						mSpots.clear();
						
						// Update the spots with the result of the response handler
						HttpGet httpget = new HttpGet(requestURL);
						response = mSpots = httpclient.execute(httpget, spotsResponseHandler);
						
						break;					
					case MSG_CHANNELS:
						Log.i(TAG, "Updating channels for spot #"+params);
						requestURL += "dbg.php?spot_id="+params;
						
						int i = new Integer(params);
						
						HttpGet httpget2 = new HttpGet(requestURL);
						response = httpclient.execute(httpget2, channelsResponseHandler);
						mSpots.get(i).setmChannels((HashMap<Integer, MusicChannel>) response);
						
						break;
					}
					
					// Send the answer to the activity
					Message msg = Message.obtain(null, 
										reqId, 
										response);
					mClient.send(msg);
					
					// Finally, close the client to avoid any leak
					httpclient.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
		
	private ResponseHandler<HashMap<Integer, MusicChannel> > channelsResponseHandler = new ResponseHandler<HashMap<Integer, MusicChannel> >() {
		//@Override
		public HashMap<Integer, MusicChannel> handleResponse(HttpResponse r) {
			String result = "";
			try {
				// First, we retrieve the xml string from the server
				BufferedReader br = new BufferedReader(new InputStreamReader(r.getEntity().getContent()));
				String line;
				while ((line = br.readLine()) != null)
	               result += line;
				
				Log.d(TAG, result);
			}
			catch(IOException e){
				e.printStackTrace();
			}
			
			HashMap<Integer, MusicChannel> res = null;
			
			// Then, we parse the retrieved string
			try {
				/* Get a SAXParser from the SAXPArserFactory. */
	            SAXParserFactory spf = SAXParserFactory.newInstance();
	            SAXParser sp = spf.newSAXParser();

	            /* Get the XMLReader of the SAXParser we created. */
	            XMLReader xr = sp.getXMLReader();
	            
	            /* Create a new ContentHandler and apply it to the XML-Reader*/
	            ChannelsXMLHandler chanHandler = new ChannelsXMLHandler();
	            xr.setContentHandler(chanHandler);
	           
	            /* Parse the xml-data from our string*/
	            xr.parse(new InputSource(new StringReader(result)));

	            /* Our ExampleHandler now provides the parsed data to us. */
	            res = chanHandler.getParsedData();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			return res;
		}
	};
	
	private ResponseHandler<HashMap<Integer, LocalizedMusicSpot> > spotsResponseHandler = new ResponseHandler<HashMap<Integer, LocalizedMusicSpot> >() {
		//@Override
		public HashMap<Integer, LocalizedMusicSpot> handleResponse(HttpResponse r) {
			String result = "";
			try {
				// First, we retrieve the xml string from the server
				BufferedReader br = new BufferedReader(new InputStreamReader(r.getEntity().getContent()));
				String line;
				while ((line = br.readLine()) != null)
	               result += line;
				
				Log.d(TAG, result);
			}
			catch(IOException e){
				e.printStackTrace();
			}
			
			HashMap<Integer, LocalizedMusicSpot> res = null;
			
			// Then, we parse the retrieved string
			try {
				/* Get a SAXParser from the SAXPArserFactory. */
	            SAXParserFactory spf = SAXParserFactory.newInstance();
	            SAXParser sp = spf.newSAXParser();

	            /* Get the XMLReader of the SAXParser we created. */
	            XMLReader xr = sp.getXMLReader();
	            
	            /* Create a new ContentHandler and apply it to the XML-Reader*/
	            SpotsXMLHandler spotsHandler = new SpotsXMLHandler();
	            xr.setContentHandler(spotsHandler);
	           
	            /* Parse the xml-data from our string*/
	            xr.parse(new InputSource(new StringReader(result)));

	            /* Our ExampleHandler now provides the parsed data to us. */
	            res = spotsHandler.getParsedData();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			return res;
		}
	};
}