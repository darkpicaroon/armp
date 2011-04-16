package com.android.armp.localized;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Application;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.android.armp.model.Channel;
import com.android.armp.model.HttpHeader;
import com.android.armp.model.Music;
import com.android.armp.model.Spot;
import com.android.armp.model.parser.ChannelsXMLHandler;
import com.android.armp.model.parser.MusicsXMLHandler;
import com.android.armp.model.parser.MyDefaultHandler;
import com.android.armp.model.parser.SpotsXMLHandler;
import com.facebook.android.Facebook;
import com.google.android.maps.GeoPoint;

public class ArmpApp extends Application {
	private static final String TAG = "ArmpApp"; // DEBUG TAG

	public static final String facebookApplicationId = "193388247366826";

	/**
	 * Music spots buffer
	 */
	private List<HttpHeader> cookies = new ArrayList<HttpHeader>();
	private static List<Spot> mCloseMusicSpots;
	private static Object mLock = new Object();

	private static final Facebook mFacebook = new Facebook(
			facebookApplicationId);

	/**
	 * Http requests parameters
	 */
	private static final String userAgent = "";
	private static final String rootUrl = "http://www.fabienrenaud.com/armp/www/";
	private static final String SPOTS_REQ = rootUrl + "getSpots.php";
	private static final String CHANNELS_REQ = rootUrl + "getChannels.php";
	private static final String MUSICS_REQ = rootUrl + "getMusics.php";
	private static final String LOGIN_REQ = rootUrl + "loginUser.php";
	private static final String SPOT_ADD = rootUrl + "createSpot.php";
	private static final String CHANNEL_ADD = rootUrl + "createChannel.php";
	private static final String SPOT_CHANNEL_ADD = rootUrl + "createSC.php";
	private static final int SPOTS_REQ_T = 0;
	private static final int CHANNELS_REQ_T = 1;
	private static final int MUSICS_REQ_T = 2;
	private static final int CLOSE_SPOTS_REQ_T = 3;
	private static final int CHANNEL_ADD_REQ_T = 4;
	private static final int SPOT_ADD_REQ_T = 5;
	private static final int LOGIN_REQ_T = 6;

	/**
	 * Response listeners
	 */
	private OnSpotsReceivedListener mSpotsListener;
	private OnChannelsReceivedListener mChanListener;
	private OnMusicsReceivedListener mMusicsListener;

	public Facebook getFacebook() {
		return mFacebook;
	}

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

	public final List<Spot> getCloseMusicSpots() {
		return mCloseMusicSpots;
	}

	private void getMusicSpots(int zoomLevel, GeoPoint ne, GeoPoint sw,
			int reqType) {
		double lat1 = (double) (ne.getLatitudeE6() / 1E6);
		double lng1 = (double) (ne.getLongitudeE6() / 1E6);
		double lat2 = (double) (sw.getLatitudeE6() / 1E6);
		double lng2 = (double) (sw.getLongitudeE6() / 1E6);

		String url = SPOTS_REQ + "?";
		url += "latne=" + lat1 + "&";
		url += "lngne=" + lng1 + "&";
		url += "latsw=" + lat2 + "&";
		url += "lngsw=" + lng2 + "&";
		url += "zoom=" + zoomLevel + "&";
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
		// url += "start=0" + "&";
		// url += "limit=10";
		Thread t = new Thread(new HttpGetRequest(CHANNELS_REQ_T, url,
				new ChannelsXMLHandler()));
		t.start();
	}

	private void loginUser() {
		if (mFacebook == null || !mFacebook.isSessionValid())
			return;

		HttpParams params = new BasicHttpParams();

		params.setParameter("pseudo", "");
		Thread t = new Thread(new HttpPostRequest(LOGIN_REQ_T, LOGIN_REQ,
				params, new MyDefaultHandler()));
		t.start();
	}

	/**
	 * This method async add the previously submitted channel on the server
	 * 
	 * @param c
	 *            the channel to save
	 */
	public void saveMusicChannel(Channel c) {
		String name = c.getName();
		int spotId = c.getSpotId();
		String url = CHANNEL_ADD;

		HttpParams params = new BasicHttpParams();
		params.setIntParameter("spotId", spotId);
		params.setParameter("name", name);
		Thread t = new Thread(new HttpPostRequest(CHANNEL_ADD_REQ_T, url,
				params, new ChannelsXMLHandler()));
		t.start();

	}

	/**
	 * This method async add the previously submitted channel on the server
	 * 
	 * @param c
	 *            the channel to save
	 */
	public void saveMusicSpot(Spot s) {
		double lat = s.getLatitude();
		double lng = s.getLongitude();
		String name = s.getName();
		int color = s.getColor();
		float radius = s.getRadius();
		String url = SPOT_ADD;

		HttpParams params = new BasicHttpParams();

		params.setDoubleParameter("lat", lat);
		params.setDoubleParameter("lng", lng);
		params.setParameter("name", name);
		params.setIntParameter("color", color);
		params.setDoubleParameter("radius", radius);
		Thread t = new Thread(new HttpPostRequest(SPOT_ADD_REQ_T, url, params,
				new SpotsXMLHandler()));
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
		// url += "start=0" + "&";
		// url += "limit=10";
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

	public void updateFacebookCookie() {
		String name = "fbs_" + mFacebook.getAppId();
		if (mFacebook == null || !mFacebook.isSessionValid()) {
			removeCookie(name);
		} else {
			String payload = "access_token=" + mFacebook.getAccessToken();
			payload += "expires=" + mFacebook.getAccessExpires();
			payload += "e922ce02199db7799b613cbdb14c1e7e";

			String value = "access_token=" + mFacebook.getAccessToken();
			value += "&expires=" + mFacebook.getAccessExpires();
			value += "&sig=" + md5(payload);

			updateCookie(name, value);
			loginUser();
		}
	}

	private void saveCookies(HttpResponse r) {
		Header[] headers = r.getHeaders("Set-Cookie");
		for (Header h : headers) {
			String[] cc = h.getValue().split(";");
			for (String c : cc) {
				String[] nv = c.split("=");
				if (nv.length == 2) {
					updateCookie(nv[0], nv[1]);
				}
			}
		}
	}

	private void updateCookie(String name, String value) {
		HttpHeader co = getCookie(name);
		if (co == null)
			cookies.add(new HttpHeader(name, value));
		else
			co.setValue(value);
	}

	private boolean removeCookie(String name) {
		String n = name.toLowerCase();
		for (int i = 0; i < cookies.size(); i++) {
			if (cookies.get(i).getName().toLowerCase().equals(n)) {
				cookies.remove(i);
				return true;
			}
		}
		return false;
	}

	private HttpHeader getCookie(String name) {
		String n = name.toLowerCase();
		for (HttpHeader c : cookies)
			if (c.getName().toLowerCase().equals(n))
				return c;
		return null;
	}

	private void setHeaders(HttpRequest r) {
		StringBuilder sb = new StringBuilder();
		for (HttpHeader c : cookies) {
			if (sb.length() > 0)
				sb.append("; ");
			sb.append(c.getName() + "=" + c.getValue());
		}
		if (sb.length() > 0) {
			Log.d("REQ", "Cookie:" + sb.toString());
			r.addHeader("Cookie", sb.toString());
		}
	}

	private String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * HTTP REQUESTS FUNCTIONS
	 */
	private class HttpGetRequest implements Runnable {
		private String mUrl;
		private CommonResponseHandler<Object> mXmlHandler;
		private int mReqType;

		public HttpGetRequest(int req, String url, MyDefaultHandler xmlHandler) {
			this.mUrl = url;
			this.mReqType = req;
			this.mXmlHandler = new CommonResponseHandler<Object>(xmlHandler);
		}

		@SuppressWarnings("unchecked")
		public void run() {
			AndroidHttpClient httpclient = null;
			try {
				httpclient = AndroidHttpClient.newInstance(userAgent);

				Log.d(TAG, "Sending request: " + mUrl);
				HttpGet httpget = new HttpGet(mUrl);
				setHeaders(httpget);

				HttpResponse response = httpclient.execute(httpget);
				Object res = mXmlHandler.handleResponse(response);
				saveCookies(response);

				synchronized (mLock) {
					// Send the answer to the listener
					switch (mReqType) {
					case SPOTS_REQ_T:
						if (mSpotsListener != null) {
							mSpotsListener
									.onSpotsReceived((ArrayList<Spot>) res);
						}
						break;
					case CHANNELS_REQ_T:
						if (mChanListener != null) {
							mChanListener
									.onChannelsReceived((ArrayList<Channel>) res);
						}
						break;
					case MUSICS_REQ_T:
						if (mMusicsListener != null) {
							mMusicsListener
									.onMusicsReceived((ArrayList<Music>) res);
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
		private CommonResponseHandler<Object> mXmlHandler;

		public HttpPostRequest(int req, String url, HttpParams params,
				MyDefaultHandler xmlHandler) {
			this.mUrl = url;
			this.mReqType = req;
			this.params = params;
			this.mXmlHandler = new CommonResponseHandler<Object>(xmlHandler);
		}

		public void run() {
			AndroidHttpClient httpclient = null;
			try {
				httpclient = AndroidHttpClient.newInstance(userAgent);

				Log.d(TAG, "Sending request: " + mUrl);
				HttpPost httppost = new HttpPost(mUrl);
				httppost.setParams(params);
				setHeaders(httppost);

				HttpResponse response = httpclient.execute(httppost);
				Object res = mXmlHandler.handleResponse(response);
				saveCookies(response);
				Log.d("LOG", "POST request");

				synchronized (mLock) {
					switch (mReqType) {
					case LOGIN_REQ_T:
						Object[] arr = (Object[]) res;
						Log.d("LOGIN", "Status: " + arr[0]);
						Log.d("LOGIN", "Logged: " + arr[1]);
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
	}
}
