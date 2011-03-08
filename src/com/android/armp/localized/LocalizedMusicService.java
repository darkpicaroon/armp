package com.android.armp.localized;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import android.util.Log;

import com.android.armp.R;
import com.google.android.maps.GeoPoint;

public class LocalizedMusicService extends Service implements LocationListener {
	private LocationManager mLocationMng;
	private Messenger mClient = null;
	private static final String TAG = "LocalizedMusicService";

	private static final String userAgent = "";

	/**
	 * Messages
	 */
	public static final int MSG_SPOTS = 1;
	public static final int MSG_CHANNELS = 2;
	public static final int MSG_MUSICS = 3;

	/**
	 * Handler of incoming messages from clients.
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (mClient == null) {
				mClient = msg.replyTo;
			}
			switch (msg.what) {
			case MSG_SPOTS:
				requestMusicSpots((GeoPoint) msg.obj);
				break;
			case MSG_CHANNELS:
				MusicSpot spot = (MusicSpot) msg.obj;
				if (spot != null) {
					// If we don't have any channel for this spot, retrieve them
					if (spot.getChannels().size() == 0) {
						requestMusicChannels(spot);
					} else { // Else just return the channels hash
						try {
							Message answ = Message.obtain(null,
									LocalizedMusicService.MSG_CHANNELS,
									spot.getChannels());
							mClient.send(answ);
						} catch (Exception e) {
							Log.e(TAG, e.getMessage());
						}
					}
				}
				break;
			case MSG_MUSICS:
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	private Resources mContext;

	@Override
	public void onCreate() {
		// Retrieve the LocationManager to enable listening to location updates
		mLocationMng = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationMng.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1,
				this);

		// To get WS urls
		mContext = getResources();
	}

	@Override
	public void onDestroy() {
	}

	/**
	 * When binding to the service, we return an interface to our messenger for
	 * sending messages to the service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "Service bound");
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
	 * @param reqId
	 *            The kind of request we are performing
	 */
	private void requestMusicSpots(GeoPoint location) {
		if (location == null) {
			killRequest(MSG_SPOTS);
		}
		// double latitude = (double)(location.getLatitudeE6() / 1000000.0);
		// double longitude = (double)(location.getLongitudeE6() / 1000000.0);

		// Metz, GTL
		double latitude = 49.102097604636;
		double longitude = 6.2149304151535;
		String url = mContext.getString(R.string.ws_spots) + "?";
		url += "lat=" + latitude + "&";
		url += "long=" + longitude;

		Thread t = new Thread(new HttpGetRequest(MSG_SPOTS, url,
				new SpotsXMLHandler()));
		t.start();
	}

	private void requestMusicChannels(MusicSpot spot) {
		if (spot == null) {
			killRequest(MSG_CHANNELS);
		}
		String url = mContext.getString(R.string.ws_channels) + "?";
		url += "spot_id=" + spot.getId();
		Thread t = new Thread(new HttpGetRequest(MSG_CHANNELS, url,
				new ChannelsXMLHandler()));
		t.start();
		// spot.setChannels((List<MusicChannel>) response);
	}

	private void requestMusicItems(MusicChannel channel) {

	}

	private void killRequest(int requestId) {
		if (mClient != null) {
			try {
				mClient.send(Message.obtain(null, requestId, null));
			} catch (RemoteException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	private class HttpGetRequest implements Runnable {
		private String mUrl;
		private MyDefaultHandler mXmlHandler;
		private int mRequestId;

		public HttpGetRequest(int requestId, String url,
				MyDefaultHandler xmlHandler) {
			this.mRequestId = requestId;
			this.mUrl = url;
			this.mXmlHandler = xmlHandler;
		}

		public void run() {
			AndroidHttpClient httpclient = null;
			try {
				httpclient = AndroidHttpClient.newInstance(userAgent);

				Log.d(TAG, "Sending request: " + mUrl);
				HttpGet httpget = new HttpGet(mUrl);
				Object response = httpclient.execute(httpget,
						new CommonResponseHandler<Object>(mXmlHandler));

				// Send the answer to the activity
				Message msg = Message.obtain(null, mRequestId, response);
				mClient.send(msg);
			} catch (Exception e) {
				String msg = e != null && e.getMessage() != null ? e
						.getMessage() : "Fatal error!";
				Log.e(TAG, msg);
			} finally {
				if (httpclient != null) {
					httpclient.close();
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

				/* Parse the xml-data from our string */
				xr.parse(new InputSource(new StringReader(result)));

				/* Our ExampleHandler now provides the parsed data to us. */
				res = (T) mXmlHandler.getParsedData();
			} catch (Exception e) {
				Log.d(TAG, e.getMessage());
			}

			return res;
		}
	};
}