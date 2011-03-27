package com.android.armp.localized;

import java.util.ArrayList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.android.armp.MediaPlaybackService;
import com.android.armp.MusicUtils;
import com.android.armp.MusicUtils.ServiceToken;
import com.android.armp.model.Music;
import com.android.armp.model.Spot;
import com.android.armp.model.Channel;
import com.google.android.maps.GeoPoint;

public class LocalizedMusicService extends Service implements LocationListener, ServiceConnection {
	private static final String TAG = "LocalizedMusicService";
	
	private LocationManager mLocationMng;
	
	private Messenger mClient = null;	
	private static ArmpApp theApp = null;
	
	private ServiceToken mToken = null;
	
	private static final int CLOSE_SPOTS_ZOOM = 16;
	private static final int UPDATE_TRESHOLD = 200;
	private static final int DIST_AROUND = 400;
	private static final int DETECTION_DELTA = 5;
	private long mAverageSpeed = 0;
	private Location mPreviousLoc = null;
	private Location mLastUpdateLoc = null;
	private boolean mIsInASpot = false;
	private static Spot mCurrSpot = null;
	private static int mCurrChanIdx = 0;
	private static int mPlaybackPos = 0;
	private static ArrayList<Music> mCurrMusics = null;

	/**
	 * Messages
	 */
	public static final int MSG_REGISTER = 0;
	public static final int MSG_UREGISTER = 1;
	public static final int MSG_ENTERED = 2;
	public static final int MSG_LEFT = 3;

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
	
	private BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MediaPlaybackService.META_CHANGED)) {
                
            } else if (action.equals(MediaPlaybackService.PLAYBACK_COMPLETE)) {
               
            } else if (action.equals(MediaPlaybackService.PLAYSTATE_CHANGED)) {
            }
        }
    };

	@Override
	public void onCreate() {
		super.onCreate();
		
		theApp = (ArmpApp)getApplicationContext();
		// Retrieve the LocationManager to listen to location updates
		mLocationMng = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationMng.requestLocationUpdates(LocationManager.GPS_PROVIDER,
											1, 1, this);
		mLocationMng.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
											1, 1, this);

		// To get WS urls
		mContext = getResources();
		
		mToken = MusicUtils.bindToService(this.getApplicationContext(), this);
		
		IntentFilter f = new IntentFilter();
        f.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
        f.addAction(MediaPlaybackService.META_CHANGED);
        f.addAction(MediaPlaybackService.PLAYBACK_COMPLETE);
        registerReceiver(mStatusListener, new IntentFilter(f));
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mStatusListener);
		MusicUtils.unbindFromService(mToken);
	}

	/**
	 * When binding to the service, we return an interface to our messenger for
	 * sending messages to the service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}
	
	public static int getCurrentSpotId() {
		return mCurrSpot != null ? mCurrSpot.getId() : -1;
	}
	
	private boolean needSpotsUpdate(Location currLoc) {
		// If we've never updated the spots, do it!
		if(mLastUpdateLoc == null) {
			mLastUpdateLoc = currLoc;
			return true;
		}
		
		boolean res = (currLoc.distanceTo(mLastUpdateLoc) > UPDATE_TRESHOLD);
		mLastUpdateLoc.set(currLoc);
		
		return res;
	}
	
	private int hasEnteredSpot(Location currLoc) {
		ArrayList<Spot> mSpots = theApp.getCloseMusicSpots();
		
		if(mSpots == null)
			return -1;
		
		float [] results = new float[1];
		double lat = currLoc.getLatitude();
		double lon = currLoc.getLongitude();
		
		for(Spot s : mSpots) {
			Location.distanceBetween(lat, lon, s.getLatitude(),
					s.getLongitude(), results
			);
			
			if(results[0] < s.getRadius()-DETECTION_DELTA)
				return s.getId();
		}		
		
		return -1;
	}
	
	private boolean hasExitedSpot(Location currLoc) {
		ArrayList<Spot> mSpots = theApp.getCloseMusicSpots();
		
		if(mSpots == null)
			return false;
		
		float [] results = new float[1];
		double lat = currLoc.getLatitude();
		double lon = currLoc.getLongitude();
		
		for(Spot s : mSpots) {			
			Location.distanceBetween(lat, lon, s.getLatitude(),
					s.getLongitude(), results
			);
			
			if(results[0] > s.getRadius()+DETECTION_DELTA)
				return true;
		}		
		
		return false;
	}
	
	private final static Spot findSpot(int spotId, ArrayList<Spot> spots) {		
		if (spots != null && spots.size() > 0 && spotId > 0) {
			for (Spot s : spots) {
				if (s.getId() == spotId) {
					return s;
				}
			}
		}
		return null;
	}
	
	private final static Channel findChannel(int channelId, ArrayList<Channel> chans) {		
		if (chans != null && chans.size() > 0 && channelId > 0) {
			for (Channel c : chans) {
				if (c.getId() == channelId) {
					return c;
				}
			}
		}
		return null;
	}
	
	private final static void loadMusics(Spot s) {
		if(s == null)
			return;
		
		ArrayList<Channel> chans = s.getChannels();
		int chanIdx = 0, musicIdx = 0;
		
		mCurrMusics = null;
		
		if(chans != null && chans.size() > 0) {
			for(Channel c : chans) {
				ArrayList<Music> musics = c.getMusics();
				musicIdx = 0;
				if(musics != null && musics.size() > 0) {
					for(Music m : musics) {
						if(m.isPlayable()) {
							mCurrMusics = musics;
							mCurrChanIdx = chanIdx;
							mPlaybackPos = musicIdx;
							break;
						}
						++musicIdx;
					}
				}
				// Playable channel has been found, return
				if(mCurrMusics != null)
					break;
				++chanIdx;
			}
		}
	}

	public void onLocationChanged(Location location) {	
		// Check if we need to update the spots around the user
		if(needSpotsUpdate(location)) {
			GeoPoint ne = Spatial.getLocationAt(location, DIST_AROUND, Math.toRadians(-45));
			GeoPoint sw = Spatial.getLocationAt(location, DIST_AROUND, Math.toRadians(135));
			theApp.updateCloseMusicSpots(CLOSE_SPOTS_ZOOM, ne, sw);
		}
		
		// Check if the user has entered a spot
		
		if(!mIsInASpot) {
			int spotId = hasEnteredSpot(location);
			if(spotId >= 0) {
				mIsInASpot = true;
				mCurrSpot= findSpot(spotId, theApp.getCloseMusicSpots());
				
				// Load the first playable channel on this spot
				loadMusics(mCurrSpot);
				
				// If some music has been loaded, play it!
				if(mCurrMusics != null) {
					MusicUtils.playLocalized(mPlaybackPos);
				}
				
				// Tell the activity
				Message msg = Message.obtain(null, MSG_ENTERED);
				msg.arg1 = spotId;
				
				try {
					mClient.send(msg);
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
				}				
			}
		}
		// Check if the user has left a spot
		else if(mIsInASpot && hasExitedSpot(location)){
			mIsInASpot = false;
			MusicUtils.stopLocalized();
			
			mCurrSpot = null;
			mCurrMusics = null;
			
			// Tell the activity
			Message msg = Message.obtain(null, MSG_LEFT);
			
			try {
				mClient.send(msg);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
		
		mPreviousLoc = location;
	}
	
	public static ArrayList<Music> getCurrentMusics() {
		return mCurrMusics;
	}
	
	public static void playMusic(int chanId, int position) {
		Channel c = findChannel(chanId, mCurrSpot.getChannels());
		
		if(c != null) {
			mCurrMusics = c.getMusics();
			mPlaybackPos = position;
			
			// If some music has been loaded, play it!
			if(mCurrMusics != null) {
				MusicUtils.playLocalized(mPlaybackPos);
			}
		}
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public void onServiceConnected(ComponentName name, IBinder service) {
	}

	public void onServiceDisconnected(ComponentName name) {
	}
	
	private static final class Spatial {
		// distance from the center to the equator (meters)
		private static final double eq = 6378137.0; 
		// distance from the center to the north/south pole (meters)
		private static final double ns = 6356752.3; 

		private static final GeoPoint getLocationAt(Location from, int distance,
				double bearing) {
			double lat1 = Math.toRadians(from.getLatitude());
			double lon1 = Math.toRadians(from.getLongitude());
			double r = getRadiusOfEarth(lat1);
			double dr = distance/r;

			double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dr) + Math.cos(lat1) 
										* Math.sin(dr) * Math.cos(bearing));
			double lon2 = lon1 + Math.atan2(Math.sin(bearing)*Math.sin(dr)*Math.cos(lat1),
											Math.cos(dr)-Math.sin(lat1)*Math.sin(lat2));
			lon2 = (lon2+3*Math.PI)%(2*Math.PI) - Math.PI;

			return new GeoPoint((int)(Math.toDegrees(lat2)*1E6), 
								(int)(Math.toDegrees(lon2)*1E6));
		}

		/**
		 * Get the radius of the earth, in meters, depending on the latitude
		 * @param type $phi The latitude in radians
		 * @return type The radius of the earth in meters.
		 * @see Formula here: http://en.wikipedia.org/wiki/Earth_radius
		 */
		private static final double getRadiusOfEarth(double phi) {
			double cPhi   = Math.cos(phi);
			double sPhi   = Math.sin(phi);
			double acPhi = eq * cPhi;
			double bsPhi = ns* sPhi;
			double aacPhi = eq * acPhi;
			double bbsPhi = ns * bsPhi;

			double num  = Math.pow(aacPhi,2) + Math.pow(bbsPhi,2);
			double den  = Math.pow(acPhi,2)  + Math.pow(bsPhi,2);
			return Math.sqrt(num/den);
		}
	}
}