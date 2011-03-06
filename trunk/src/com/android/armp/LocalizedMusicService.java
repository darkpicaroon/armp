package com.android.armp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
	private ArrayList<Spot> mSpots;
	private static final String TAG = "LocalizedMusicService";
	private static LocalizedMusicService mInstance = null;
	
	public class Spot extends Object {
		private double lat;
		private double lon;
		
		public Spot() {
		}
		
		public Spot(double lat, double lon) {
			this.lat = lat;
			this.lon = lon;
		}

		public void setLat(double lat) {
			this.lat = lat;
		}

		public double getLat() {
			return lat;
		}

		public void setLon(double lon) {
			this.lon = lon;
		}

		public double getLon() {
			return lon;
		}
	};
	
	public static Spot createSpot() {
		return mInstance.new Spot();
	}
	
	public class Channel extends Object {
		
	};
	
	public class Music extends Object {
		
	};
	
	/**
	 * Messages
	 */
	static final int MSG_SPOTS_UPDATE = 1;
    

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
        mSpots = new ArrayList<Spot>();
        
        mInstance = this;
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
	
	private static final String baseUrl = "http://abarreir.com/";
	private static final String userAgent = "";
	
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
						requestURL += "dbg.php";
						
						// Reset the current spots
						mSpots.clear();
						
						// Update the spots with the result of the response handler
						HttpGet httpget = new HttpGet(requestURL);
						response = mSpots = httpclient.execute(httpget, spotsResponseHandler);
						
						break;
					}
					
					// Send the answer to the activity
					Message msg = Message.obtain(null, 
										LocalizedMusicService.MSG_SPOTS_UPDATE, 
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
	
	private ResponseHandler<ArrayList<Spot> > spotsResponseHandler = new ResponseHandler<ArrayList<Spot> >() {
		//@Override
		public ArrayList<Spot> handleResponse(HttpResponse r) {
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
			
			ArrayList<Spot> res = null;
			
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