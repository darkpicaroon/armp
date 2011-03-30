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
	private static final int UPDATE_TRESHOLD = 50;
	private static final int DIST_AROUND = 1000;
	private static final int DETECTION_DELTA = 5;
	
	//private long mAverageSpeed = 0;
	
	private Location mPreviousLoc = null;
	private Location mLastUpdateLoc = null;
	private boolean mIsInASpot = false;
	private static Spot mCurrSpot = null;
	private static int mCurrChanIdx = 0;
	private static int mPlaybackPos = 0;
	private static int mIncoherentTreshold = 30;
	
	/**
	 * Musics currently loaded to be played
	 */
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
		
		// Save the application context
		theApp = (ArmpApp)getApplicationContext();
		
		// Retrieve the LocationManager to listen to location updates
		mLocationMng = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationMng.requestLocationUpdates(LocationManager.GPS_PROVIDER,
											1, 1, this);
		mLocationMng.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
											1, 1, this);
		
		// Bind the MediaPlaybackService to thos service
		mToken = MusicUtils.bindToService(this.getApplicationContext(), this);
		
		// Start listening to messages sent by the MediaPlaybackService
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
	 * Retrive the id of the spot the user is currently in
	 * @return The spot id
	 */
	public static int getCurrentSpotId() {
		return mCurrSpot != null ? mCurrSpot.getId() : -1;
	}
	
	/**
	 * Helper function to determine if the spots buffer needs to be updated
	 * or not
	 * @param currLoc The user current location
	 * @return True if the spots need to be updated, false if not
	 * TODO: Implement a better "heuristic"
	 */
	private boolean needSpotsUpdate(Location currLoc) {
		// If we've never updated the spots, do it!
		if(mLastUpdateLoc == null) {
			mLastUpdateLoc = currLoc;
			return true;
		}
		
		/* Compute speed and distance from the location where we last 
		 * updated the spots */		
		float dist = currLoc.distanceTo(mLastUpdateLoc);
		float speed = dist/(currLoc.getTime()-mLastUpdateLoc.getTime());
		
		//TODO: Track the speed of the user
		
		/**
		 * If the distance is greater than the update treshold or if the speed
		 * is incoherent and the accuracy of the current spot is worse than
		 * the one of the previous spot where we did an update
		 * TODO: update the incoherent treshold and work on these conditions
		 */
		boolean res = (dist > UPDATE_TRESHOLD ||
						(speed >= mIncoherentTreshold) 
						&& currLoc.getAccuracy() > mLastUpdateLoc.getAccuracy()
					);
		
		if(res)
			mLastUpdateLoc.set(currLoc);
		
		return res;
	}
	
	/**
	 * Helper function indicating if we have entered a spot
	 * @param currLoc The location of the user
	 * @return The id of the entered spot if any, -1 otherwise
	 */
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
			//Log.d(TAG, "Distance to spot #"+s.getId()+" = "+results[0]);
			//TODO: Maybe change this idea of detection delta
			if(results[0] < s.getRadius()-DETECTION_DELTA)
				return s.getId();
		}		
		
		return -1;
	}
	
	/**
	 * Helper function indicating if we have exited a spot
	 * @param currLoc The location of the user
	 * @return True if we left a spot, false otherwise
	 */	
	private boolean hasExitedSpot(Location currLoc) {
		ArrayList<Spot> mSpots = theApp.getCloseMusicSpots();
		
		if(mSpots == null) {
			return false;
		}
			
		
		float [] results = new float[1];
		double lat = currLoc.getLatitude();
		double lon = currLoc.getLongitude();
		
		Location.distanceBetween(lat, lon, mCurrSpot.getLatitude(),
					mCurrSpot.getLongitude(), results
		);
		
		return (results[0] > mCurrSpot.getRadius()+DETECTION_DELTA);
	}
	
	/**
	 * Helper function to retrieve a spot in a given list from its id
	 * @param spotId The spot id
	 * @param spots The list of spots to search in
	 * @return The spot if found, null otherwise
	 */
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
	
	/**
	 * Helper function to retrieve a channel in a given list from its id
	 * @param channelId The channel id
	 * @param chans The list of channels to search in
	 * @return The channel if found, null otherwise
	 */
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
	
	/**
	 * Helper function to load a music from a spot
	 * @param s The spot to load the music from
	 */
	private final static void loadMusics(Spot s) {
		if(s == null)
			return;
		
		// Retrieve the channels of the spot
		ArrayList<Channel> chans = s.getChannels();
		int chanIdx = 0, musicIdx = 0;
		
		// Reset the current musics
		mCurrMusics = null;
		
		// Look for the first playable channel (that's to say the first
		// channel with at least one playbale music)
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
		
		// If the accuracy is not good enough, return
		//TODO: Change the variable (incoherent treshold shouldn't be used for 
		// this purpose
		//Log.d(TAG, "Accuracy: "+location.getAccuracy());
		if(location.getAccuracy() > mIncoherentTreshold)
			return;
		
		// Check if the user has entered a spot		
		if(!mIsInASpot) {
			int spotId = hasEnteredSpot(location);
			
			// The user entered a spot
			if(spotId >= 0) {
				Log.d(TAG, "Has entered spot #"+spotId);
				mIsInASpot = true;
				mCurrSpot = findSpot(spotId, theApp.getCloseMusicSpots());
				
				// Load the first playable channel on this spot
				loadMusics(mCurrSpot);
				
				// If some music has been loaded, play it!
				if(mCurrSpot != null && mCurrMusics != null) {
					MusicUtils.playLocalized(mPlaybackPos);
					
					// Tell the activity
					Message msg = Message.obtain(null, MSG_ENTERED);
					msg.arg1 = spotId;
					
					try {
						mClient.send(msg);
					} catch (Exception e) {
						Log.e(TAG, e.getMessage());
					}
				}
				else {
					mIsInASpot = false;
				}								
			}
		}
		// Check if the user has left a spot
		else if(mIsInASpot && hasExitedSpot(location)){
			mIsInASpot = false;
			Log.d(TAG, "Has exited spot #"+getCurrentSpotId());
			
			// Stop the music playback
			MusicUtils.stopLocalized();
			
			// Reset the current spot and musics
			int spId = getCurrentSpotId();
			mCurrSpot = null;
			mCurrMusics = null;
			
			// Tell the activity
			Message msg = Message.obtain(null, MSG_LEFT);
			msg.arg1 = spId;
			
			try {
				mClient.send(msg);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
		// Save the location
		mPreviousLoc = location;
	}
	
	public static ArrayList<Music> getCurrentMusics() {
		return mCurrMusics;
	}
	
	/**
	 * Start the playback of a given music from a given channel
	 * The playback will be launched if the user is currently in this channel
	 * @param chanId The id of the channel
	 * @param position The position in the channel (which music)
	 */
	public static void playMusic(int chanId, int position) {
		Channel c = findChannel(chanId, mCurrSpot.getChannels());
		
		// If the channel has been found (it belongs to the channel)
		if(c != null) {
			//TODO: maybe call loadmusics helper function..?
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
	
	/**
	 * Helper class to find a point at a given distance and bearing from
	 * @author abarreir
	 *
	 */
	private static final class Spatial {
		// distance from the center to the equator (meters)
		private static final double eq = 6378137.0; 
		// distance from the center to the north/south pole (meters)
		private static final double ns = 6356752.3; 

		/**
		 * Function used to retrieve a point away from an other one
		 * @param from The reference point
		 * @param distance The distance from the reference point
		 * @param bearing The bearing from the reference point
		 * @return The location at a distance distance and bearing bearing
		 */
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
	
	/**
	 * When binding to the service, we return an interface to our messenger for
	 * sending messages to the service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}
}