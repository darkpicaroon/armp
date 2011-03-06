package com.android.armp;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class LocalizedMusicService extends Service implements LocationListener {	
	private LocationManager mLocationMng;
	private Messenger mClient = null;
	private ArrayList mSpots;
	
	public class Spot extends Object {
		private int i;
		
		public Spot(int val) {
			i = val;
		}
		
		public int geti() {
			return i;
		}
	};
	
	public class Channel {
		
	};
	
	public class Music {
		
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
                	
                	mSpots.add(new Spot(0));
                	mSpots.add(new Spot(0));
                	mSpots.add(new Spot(1));
                	mSpots.add(new Spot(2));
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
    	System.out.println("Service created");
    	
    	// Retrieve the LocationManager to enable listening to location updates
        mLocationMng = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationMng.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, this);
        
        mSpots = new ArrayList<Spot>();
        
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        //Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
        System.out.println("Service stopped");
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
		// TODO Auto-generated method stub
		try {
			mClient.send(Message.obtain(null, MSG_SPOTS_UPDATE, (Object) mSpots));	
		}
		catch(RemoteException e) {
			
		}		
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		try {
			mClient.send(Message.obtain(null, MSG_SPOTS_UPDATE, (Object) mSpots));	
		}
		catch(RemoteException e) {
			
		}
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}