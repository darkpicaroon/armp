package com.android.armp;

import java.util.HashMap;
import java.util.Iterator;

import com.android.armp.LocalizedMusicSpot.MusicChannel;
import com.android.armp.R;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Window;

public class LocalizedMusicActivity extends MapActivity {
	
	private MyLocationOverlay mLocation;
	private MapView mMapView;
	
	private ProgressDialog mPD = null;
	
	/** Messenger for communicating with service. */
	Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
	boolean mIsBound;
	
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	private final static String TAG = "LMA";
	
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
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	            case LocalizedMusicService.MSG_SPOTS_UPDATE:
	            	// Close loading progress dialog
	    			mPD.dismiss();
	    			
	    			// Go through all the returned spots
	            	HashMap<Integer, LocalizedMusicSpot> map = (HashMap<Integer, LocalizedMusicSpot>)msg.obj;
	            	if(map == null)
	            		return;
	            	
	            	Iterator mIt = map.keySet().iterator();
	            	while(mIt.hasNext()) {
	            	    int key=(Integer)mIt.next();
	            	    LocalizedMusicSpot value=(LocalizedMusicSpot)map.get(key);
	            	    Log.d(TAG, "Spot #"+key+": "+value.getmLattitude()+" - "+value.getmLongitude()+
	            	    		" - "+value.getmCreationTime());
	            	}
	            	
	            	break;
	            case LocalizedMusicService.MSG_CHANNELS:
	            	// Close loading progress dialog
	            	mPD.dismiss();
	            	
	            	// Go through all the returned channels
	            	HashMap<Integer, MusicChannel> chans = (HashMap<Integer, MusicChannel>)msg.obj;
	            	if(chans == null)
	            		return;
	            	
	            	Iterator chansIt = chans.keySet().iterator();
	            	while(chansIt.hasNext()) {
	            	    int key=(Integer)chansIt.next();
	            	    MusicChannel value=(MusicChannel)chans.get(key);
	            	    Log.d(TAG, "Channel #"+key+": "+value.toString());
	            	}
	            	
	            	break;
	            default:
	                super.handleMessage(msg);
	        }
	    }
	}
	
	private void getSpots() {
		// Show dialog
        mPD = ProgressDialog.show(LocalizedMusicActivity.this, "", "Retrieving spots...", true, false);
        
        // Create and send the message
		Message msg = Message.obtain(null,
                LocalizedMusicService.MSG_SPOTS_UPDATE);
        msg.replyTo = mMessenger;
        
        try {
        	mService.send(msg);
        } catch (Exception e){
        	
        }
	}
	
	private void getChannels(int spotId) {
		// Show dialog
        mPD = ProgressDialog.show(LocalizedMusicActivity.this, "", "Retrieving channels...", true, false);
        
        // Create and send the message
		Message msg = Message.obtain(null, LocalizedMusicService.MSG_CHANNELS);
        msg.replyTo = mMessenger;
        msg.obj = spotId;
        
        try {
        	mService.send(msg);
        } catch (Exception e){
        	
        }
	}
	
	private void getMusics(int channelId) {
		// Show dialog
        mPD = ProgressDialog.show(LocalizedMusicActivity.this, "", "Retrieving musics...", true, false);
        
        // Create and send the message
		Message msg = Message.obtain(null, LocalizedMusicService.MSG_MUSICS);
        msg.replyTo = mMessenger;
        msg.obj = channelId;
        
        try {
        	mService.send(msg);
        } catch (Exception e){
        	
        }
	}
	
	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className,
	            IBinder service) {
	        mService = new Messenger(service);
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        mService = null;
	    }
	};

	void doBindService() {
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
