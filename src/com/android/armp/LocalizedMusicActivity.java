package com.android.armp;

import java.util.ArrayList;

import com.android.armp.R;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Window;
import android.widget.Toast;

public class LocalizedMusicActivity extends MapActivity {
	
	private MyLocationOverlay mLocation;
	private MapView mMapView;
	
	/** Messenger for communicating with service. */
	Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
	boolean mIsBound;
	
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	
	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	            case LocalizedMusicService.MSG_SPOTS_UPDATE:
	            	ArrayList<LocalizedMusicService.Spot> p = (ArrayList<LocalizedMusicService.Spot>)msg.obj;
	            	
	            	for (int i = 0; i<p.size(); ++i)
	            	{
	            		System.out.println("Spot: " + p.get(i).geti());
	            	}
	                break;
	            default:
	                super.handleMessage(msg);
	        }
	    }
	}


    public LocalizedMusicActivity()
    {
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.localized_music_activity);
        setTitle(R.string.maps_title);
        
        // Display the user's location on the map view        
        mMapView = (MapView) findViewById(R.id.mapview);
        mLocation = new MyLocationOverlay(mMapView.getContext(), mMapView);
        
        // Display the "now playing" bar
        MusicUtils.updateButtonBar(this, R.id.maptab);
        MusicUtils.updateNowPlaying(LocalizedMusicActivity.this); 
        
        // Bind the service
        doBindService();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // Update the now playing bar
        MusicUtils.setSpinnerState(this);
        MusicUtils.updateNowPlaying(LocalizedMusicActivity.this);
        
        // Enable compass and location display
        mLocation.enableCompass();
        mLocation.enableMyLocation();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	// Disable compass and location display
    	mLocation.disableCompass();
    	mLocation.disableMyLocation();
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	// Unbind the service
    	doUnbindService();
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className,
	            IBinder service) {
	        mService = new Messenger(service);

	        // Start by querying for spots
	        try {
	            Message msg = Message.obtain(null,
	                    LocalizedMusicService.MSG_SPOTS_UPDATE);
	            msg.replyTo = mMessenger;
	            mService.send(msg);

	        } catch (RemoteException e) {
	            
	        }
	        
	        System.out.println("Service connected");
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        mService = null;

	        System.out.println("Service disconnected");
	    }
	};

	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because there is no reason to be able to let other
	    // applications replace our component.	
		
	    bindService(new Intent(LocalizedMusicActivity.this, 
	    		LocalizedMusicService.class), mConnection, Context.BIND_AUTO_CREATE);
	    mIsBound = true;
	}

	void doUnbindService() {
	    if (mIsBound) {
	        // Detach our existing connection.
	        unbindService(mConnection);
	        mIsBound = false;
	    }
	}
}
