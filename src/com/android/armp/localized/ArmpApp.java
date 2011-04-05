package com.android.armp.localized;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Application;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.android.armp.model.Channel;
import com.android.armp.model.Music;
import com.android.armp.model.Spot;
import com.android.armp.model.parser.ChannelsXMLHandler;
import com.android.armp.model.parser.MusicsXMLHandler;
import com.android.armp.model.parser.MyDefaultHandler;
import com.android.armp.model.parser.SpotsXMLHandler;
import com.google.android.maps.GeoPoint;

public class ArmpApp extends Application {
	private static final String TAG = "ArmpApp"; // DEBUG TAG

	/**
	 * Music spots buffer
	 */
	private static ArrayList<Spot> mCloseMusicSpots;
	private static Object mLock = new Object();

	/**
	 * Http requests parameters
	 */
	private static final String userAgent      = "";
	private static final String rootUrl        = "http://fabienrenaud.com/armp/www/";
	private static final String SPOTS_REQ      = rootUrl + "getSpots.php";
	private static final String CHANNELS_REQ   = rootUrl + "getChannels.php";
	private static final String MUSICS_REQ     = rootUrl + "getMusics.php";
	private static final String CHANNEL_ADD    = rootUrl + "createChannel.php";
	private static final int SPOTS_REQ_T       = 0;
	private static final int CHANNELS_REQ_T    = 1;
	private static final int MUSICS_REQ_T      = 2;
	private static final int CLOSE_SPOTS_REQ_T = 3;
	private static final int CHANNEL_ADD_REQ_T = 4;

	/**
	 * Response listeners
	 */
	private OnSpotsReceivedListener mSpotsListener;
	private OnChannelsReceivedListener mChanListener;
	private OnMusicsReceivedListener mMusicsListener;

	/**
	 * This method retrieves a set of spots queried by the activtiy, locally or
	 * from the server, and returns it asynchronously through a callback method
	 * 
	 * @param zoomLvl
	 *            The zoom level on the map
	 * @param g1
	 *            The top left point of the area to look for spots
	 * @param g2
	 *            The bottom right point of the area to look for spots
	 */
	public void getMusicSpots(int zoomLevel, GeoPoint ne, GeoPoint sw) {
		getMusicSpots(zoomLevel, ne, sw, SPOTS_REQ_T);
	}
	
	public void updateCloseMusicSpots(int zoomLevel, GeoPoint ne, GeoPoint sw) {
		Log.d(TAG, "Updating closest spots...");
		getMusicSpots(zoomLevel, ne, sw, CLOSE_SPOTS_REQ_T);
	}
	
	public final ArrayList<Spot> getCloseMusicSpots() {
		return mCloseMusicSpots;
	}
	
	private void getMusicSpots(int zoomLevel, GeoPoint ne, GeoPoint sw, int reqType) {
		double lat1 = (double) (ne.getLatitudeE6() / 1E6);
		double lng1 = (double) (ne.getLongitudeE6() / 1E6);
		double lat2 = (double) (sw.getLatitudeE6() / 1E6);
		double lng2 = (double) (sw.getLongitudeE6() / 1E6);

		String url = SPOTS_REQ + "?";
		url += "latne=" + lat1 + "&";
		url += "lngne=" + lng1 + "&";
		url += "latsw=" + lat2 + "&";
		url += "lngsw=" + lng2 + "&";
		url += "zoom="  + zoomLevel + "&";
		url += "heavy=" + ((reqType == SPOTS_REQ_T) ? 0 : 1);
		
		Thread t = new Thread(new HttpGetRequest(reqType, url,
				new SpotsXMLHandler()));
		t.start();
	}

	/**
	 * This method async returns the channels associated with a given spot,
	 * locally or from the server.
	 * 
	 * @param spotId
	 *            The id of the spot
	 */
	public void getMusicChannels(int spotId) {
		String url = CHANNELS_REQ + "?";
		url += "spotId=" + spotId;
		//url += "start=0" + "&";
		//url += "limit=10";
		Thread t = new Thread(new HttpGetRequest(CHANNELS_REQ_T, url,
				new ChannelsXMLHandler()));
		t.start();
	}
	
	/**
	 * This method async add the previously submitted channel on the server
	 * @param c the channel to save
	 */
	public void saveMusicChannel(Channel c){
		String name = c.getName();
		int spotId = c.getSpotId();
		String url = CHANNEL_ADD;
		
		HttpParams params = new BasicHttpParams();
		params.setIntParameter("spotId", spotId);
		params.setParameter("name", name);
		Thread t = new Thread(new HttpPostRequest(url, CHANNEL_ADD_REQ_T, params, new ChannelsXMLHandler()));
		t.start();
		
	}
	
	/**
	 * This method async returns the musics associated with a given channel of a
	 * given spot, locally or from the server.
	 * 
	 * @param spotId
	 *            The id of the spot
	 * @param channelId
	 *            The id of the channel
	 */
	public void getMusicItems(int spotId, int channelId) {
		String url = MUSICS_REQ + "?";
		url += "channelId=" + channelId;
		//url += "start=0" + "&";
		//url += "limit=10";
		Thread t = new Thread(new HttpGetRequest(MUSICS_REQ_T, url,
				new MusicsXMLHandler()));
		t.start();
	}

	/**
	 * Callback interfaces and setters
	 */
	public interface OnSpotsReceivedListener {
		void onSpotsReceived(ArrayList<Spot> ms);
	}

	public interface OnChannelsReceivedListener {
		void onChannelsReceived(ArrayList<Channel> mc);
	}

	public interface OnMusicsReceivedListener {
		void onMusicsReceived(ArrayList<Music> mi);
	}

	public void setOnSpotsReceivedListener(OnSpotsReceivedListener l) {
		this.mSpotsListener = l;
	}

	public void setOnChannelsReceivedListener(OnChannelsReceivedListener l) {
		this.mChanListener = l;
	}

	public void setOnMusicsReceivedListener(OnMusicsReceivedListener l) {
		this.mMusicsListener = l;
	}

	/**
	 * HTTP REQUESTS FUNCTIONS
	 */
	private class HttpGetRequest implements Runnable {
		private String mUrl;
		private MyDefaultHandler mXmlHandler;
		private int mReqType;

		public HttpGetRequest(int req, String url, MyDefaultHandler xmlHandler) {
			this.mUrl = url;
			this.mXmlHandler = xmlHandler;
			this.mReqType = req;
		}

		@SuppressWarnings("unchecked")
		public void run() {
			AndroidHttpClient httpclient = null;
			try {
				httpclient = AndroidHttpClient.newInstance(userAgent);

				Log.d(TAG, "Sending request: " + mUrl);
				HttpGet httpget = new HttpGet(mUrl);
				Object res = httpclient.execute(httpget,
						new CommonResponseHandler<Object>(mXmlHandler));

				synchronized(mLock) {
					// Send the answer to the listener
					switch (mReqType) {
					case SPOTS_REQ_T:
						if(mSpotsListener != null) {							
							mSpotsListener.onSpotsReceived((ArrayList<Spot>) res);
						}						
						break;
					case CHANNELS_REQ_T:
						if(mChanListener != null) {
							mChanListener.onChannelsReceived((ArrayList<Channel>) res);
						}						
						break;
					case MUSICS_REQ_T:
						if(mMusicsListener != null) {
							mMusicsListener.onMusicsReceived((ArrayList<Music>) res);							
						}
						break;
					case CLOSE_SPOTS_REQ_T:
						// Update the buffer of close music spots
						mCloseMusicSpots = (ArrayList<Spot>) res;
						break;
					}
				}

			} catch (Exception e) {
				String msg = e != null && e.getMessage() != null ? e
						.getMessage() : "Fatal error!";
				Log.e(TAG, msg);
				e.printStackTrace();
			} finally {
				if (httpclient != null) {
					httpclient.close();
				}
			}
		}
	}

	/**
	 * HTTP POST REQUESTS FUNCTIONS
	 */
	private class HttpPostRequest implements Runnable {
		private String mUrl;
		private HttpParams params;
		private int mReqType;
		private MyDefaultHandler mXmlHandler;

		public HttpPostRequest(String url, int req, HttpParams params, MyDefaultHandler xmlHandler) {
			this.mUrl = url;
			this.mReqType = req;
			this.params = params;
			this.mXmlHandler = xmlHandler;
		}

		@SuppressWarnings("unchecked")
		public void run() {
			AndroidHttpClient httpclient = null;
			try {
				httpclient = AndroidHttpClient.newInstance(userAgent);

				Log.d(TAG, "Sending request: " + mUrl);
				HttpPost httppost = new HttpPost(mUrl);
				httppost.setParams(params);
				
				Object res = httpclient.execute(httppost,
						new CommonResponseHandler<Object>(mXmlHandler));
				Log.d(TAG, "response = " + res.toString());

			} catch (Exception e) {
				String msg = e != null && e.getMessage() != null ? e
						.getMessage() : "Fatal error!";
				Log.e(TAG, msg);
				e.printStackTrace();
			} finally {
				if (httpclient != null) {
					httpclient.close();
					Log.d(TAG, "Request over: " + mUrl);
				}
			}
		}
	}
	
	private class CommonResponseHandler<T> implements ResponseHandler<T> {
		private MyDefaultHandler mXmlHandler;

		public CommonResponseHandler(MyDefaultHandler xmlHandler) {
			this.mXmlHandler = xmlHandler;
		}

		// @Override
		public T handleResponse(HttpResponse r) {
			String result = "";
			try {
				// First, we retrieve the xml string from the server
				BufferedReader br = new BufferedReader(new InputStreamReader(r
						.getEntity().getContent()));
				String line;
				while ((line = br.readLine()) != null) {
					result += line;
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}

			T res = null;

			// Then, we parse the retrieved string
			try {
				/* Get a SAXParser from the SAXPArserFactory. */
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();

				/* Get the XMLReader of the SAXParser we created. */
				XMLReader xr = sp.getXMLReader();

				/* Create a new ContentHandler and apply it to the XML-Reader */
				xr.setContentHandler(mXmlHandler);
				
				Log.d(TAG, result);

				/* Parse the xml-data from our string */
				xr.parse(new InputSource(new StringReader(result)));

				/* Our ExampleHandler now provides the parsed data to us. */
				res = (T) mXmlHandler.getParsedData();
			} catch (Exception e) {
				Log.d(TAG, e.getMessage());
				e.printStackTrace();
			}

			return res;
		}
	};
	
	
}
