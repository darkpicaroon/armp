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
	private Location mPreviousLoc = null;
	private Messenger mClient = null;
	private static final String TAG = "LocalizedMusicService";
	
	private static final float SPOTS_REFRESH_TRESHOLD = 1000.0f;

	/**
	 * Messages
	 */
	public static final int MSG_REGISTER = 0;
	public static final int MSG_UREGISTER = 1;

	/**
	 * Handler of incoming messages from clients.
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REGISTER:
				mClient = msg.replyTo;
				break;
			case MSG_UREGISTER:
				mClient = null;
				break;
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
		// Retrieve the LocationManager to listen to location updates
		mLocationMng = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationMng.requestLocationUpdates(LocationManager.GPS_PROVIDER,
											1, 1, this);
		mLocationMng.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
											1, 1, this);

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
		/*if(mPreviousLoc == null || 
				location.distanceTo(mPreviousLoc) > SPOTS_REFRESH_TRESHOLD) {
			if(mPreviousLoc != null)
				Log.d(TAG, String.valueOf(location.distanceTo(mPreviousLoc)));
			mPreviousLoc = location;
			
			// Tell the activity that the user moved
			try {
				Message alert = Message.obtain(null,
						LocalizedMusicService.MSG_USR_MOVE);
				mClient.send(alert);
			} catch(Exception e) {
				//Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}
		}	*/
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}