package com.android.armp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.armp.MusicUtils.ServiceToken;
import com.android.armp.facebook.SessionStore;
import com.android.armp.localized.ArmpApp;
import com.android.armp.localized.ArmpApp.OnChannelsReceivedListener;
import com.android.armp.localized.ArmpApp.OnMusicsReceivedListener;
import com.android.armp.localized.ArmpApp.OnSpotsReceivedListener;
import com.android.armp.localized.LocalizedMusicService;
import com.android.armp.localized.LocalizedPreferencesActivity;
import com.android.armp.localized.MusicChannelView;
import com.android.armp.localized.SmartMapView;
import com.android.armp.localized.SmartMapView.OnAreaChangedListener;
import com.android.armp.localized.SpotOverlay;
import com.android.armp.localized.SpotOverlayAdapter;
import com.android.armp.model.Channel;
import com.android.armp.model.Music;
import com.android.armp.model.Spot;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import com.facebook.android.*;
import com.facebook.android.Facebook.*;

public class LocalizedMusicActivity extends MapActivity implements ServiceConnection {
	private final static String TAG = "LMA"; // Debug TAG
	
	/**
	 * Music spots and spots overlays
	 */
	private List<Overlay> mSpotOverlays = new ArrayList<Overlay>();

	/**
	 * Map view references
	 */
	private MyLocationOverlay mLocation;
	private SmartMapView mMapView;
	private MapController mMapCtrl;

	/**
	 * Progress dialogs for data loading
	 */
	private ProgressDialog mProgressSpot = null;
	private ProgressDialog mProgressChannel = null;
	private ProgressDialog mProgressMusic = null;
	
	/**
	 * Objects tags
	 */
	public static final String channelsTag = "com.android.armp.Channels";
	public static final String spotTag = "com.android.armp.Spot";
	public static final String channelsId = "01";
	
	/**
	 * Current spots browsing state
	 */
	private int mCurrSpotId;
	private int mCurrChanId;
	private ArrayList<Channel> mCurrChans = null;
	private ArrayList<Music> mCurrMusics = null;
	private ArrayList<Spot> mCurrSpots = null;
	
	/** 
	 * Messenger to communicate with the service. 
	 */
	private Messenger mService = null;
	private final IncomingHandler mHandler = new IncomingHandler();
	private final Messenger mMessenger = new Messenger(mHandler);
	private boolean mIsBound;
	
	/**
	 * Token to communicate with the mediaplayback service
	 */
	private ServiceToken mToken;
	
	private static final int GOT_SPOTS = 10;
	private static final int GOT_CHANNELS = 11;
	private static final int GOT_MUSICS = 12;

	/**
	 * View flipper handling
	 */
	public final static int MAP_VIEW = 0;
	public final static int CHANNELS_VIEW = 1;
	public final static int MUSICS_VIEW = 2;
	private static int mCurrView; // Used to load displayed view on resume
	private ViewFlipper mFlipper;
	
	private static ArmpApp theApp;
	
	private static final String FB_APP_ID = "193388247366826";
	private final Facebook mFacebook = new Facebook(FB_APP_ID);
	
	/**
	 * Events listeners
	 */
	private OnSpotsReceivedListener mSpotsListener = new OnSpotsReceivedListener() {
		public void onSpotsReceived(ArrayList<Spot> ms) {			
			// First, refresh the spots display
			mCurrSpots = ms;
			
			mHandler.sendEmptyMessage(GOT_SPOTS);	
		}
	};
	
	private OnChannelsReceivedListener mChanListener = new OnChannelsReceivedListener() {
		public void onChannelsReceived(ArrayList<Channel> mc) {
			// The view must be refreshed in the main thread
			mCurrChans = mc;
				
			mHandler.sendEmptyMessage(GOT_CHANNELS);
			
			dismissProgress(mProgressChannel);
		}		
	};
	
	private OnMusicsReceivedListener mMusicsListener = new OnMusicsReceivedListener() {
		public void onMusicsReceived(ArrayList<Music> mi) {
			// The view must be refreshed in the main thread
			mCurrMusics = mi;
			
			mHandler.sendEmptyMessage(GOT_MUSICS);
			
			dismissProgress(mProgressMusic);
		}
	};
	
	private OnAreaChangedListener mMapAreaListener = new OnAreaChangedListener() {
		public void onAreaChanged(GeoPoint ne, GeoPoint sw) {
			getSpots(mMapView.getZoomLevel(), ne, sw);
		}
	};
	
	private void updateNowPlaying() {
		MusicUtils.updateNowPlaying(this);
	}
	
	private BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MediaPlaybackService.META_CHANGED)) {
            	updateNowPlaying();                
            } else if (action.equals(MediaPlaybackService.PLAYBACK_COMPLETE)) {               
            } else if (action.equals(MediaPlaybackService.PLAYSTATE_CHANGED)) {
            }
        }
    };
	
	private final int ANIM_DURATION = 500;
	
	private Animation fadeInAnimation() {
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(ANIM_DURATION);
		
		return animation;
	}
	
	private Animation fadeOutAnimation() {
		Animation animation = new AlphaAnimation(1.0f, 0.0f);
		animation.setDuration(ANIM_DURATION);
		
		return animation;
	}
	

	public LocalizedMusicActivity() {
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		// Retrieve our application context
		theApp = (ArmpApp)getApplicationContext();
		
		// Bind the receive listener
		theApp.setOnSpotsReceivedListener(mSpotsListener);
		theApp.setOnChannelsReceivedListener(mChanListener);
		theApp.setOnMusicsReceivedListener(mMusicsListener);
		
		// Set basic properties
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.localized_music_activity);        
		setTitle(R.string.maps_title);
		
		// Get the maps references
		mMapView = (SmartMapView) findViewById(R.id.mapview);
		mMapCtrl = mMapView.getController();
		
		// Set the map listener
		mMapView.setOnAreaChangedListener(mMapAreaListener);
		
		// Get a reference to the view flipper
		mFlipper = (ViewFlipper) findViewById(R.id.flipper);		
		mFlipper.setInAnimation(fadeInAnimation());
		mFlipper.setOutAnimation(fadeOutAnimation());
        
        // Display the user's location on the map view
		mLocation = new MyLocationOverlay(mMapView.getContext(), mMapView);
		mMapView.getOverlays().add(mLocation);
		mLocation.enableMyLocation();
		
		// Display the zoom buttons
		mMapView.setBuiltInZoomControls(true);
		
		// Set the selected tab to the current view
		MusicUtils.updateButtonBar(this, R.id.maptab);
		
		// Update the now playing bar
		MusicUtils.setSpinnerState(this);
		MusicUtils.updateNowPlaying(this);

		// Bind the service (for background work and notifications)
		doBindService();
		
		// Bind the mediaplayback service
		mToken = MusicUtils.bindToService(this, this);
		
		if(SessionStore.restore(mFacebook, this))
			Log.d(TAG, "Facebook token: "+mFacebook.getAccessToken());
	}

	@Override
	public void onResume() {
		super.onResume();
		// Update the now playing bar
		MusicUtils.setSpinnerState(this);
		MusicUtils.updateNowPlaying(this);
		
		mCurrView = MAP_VIEW;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		mToken = MusicUtils.bindToService(this, this);
        
		IntentFilter f = new IntentFilter();
        f.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
        f.addAction(MediaPlaybackService.META_CHANGED);
        f.addAction(MediaPlaybackService.PLAYBACK_COMPLETE);
        registerReceiver(mStatusListener, new IntentFilter(f));
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		unregisterReceiver(mStatusListener);
		// Unbind the mediaplayback service
		MusicUtils.unbindFromService(mToken);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Unbind the service
		doUnbindService();
		
		// Unbind the mediaplayback service
		MusicUtils.unbindFromService(mToken);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.localized_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		switch(mCurrView) {
		case MAP_VIEW:
			menu.findItem(R.id.channels_specific).setVisible(false);
			menu.findItem(R.id.mapview_specific).setVisible(true);
			break;
		case CHANNELS_VIEW:
			menu.findItem(R.id.channels_specific).setVisible(true);
			menu.findItem(R.id.mapview_specific).setVisible(false);
			break;
		case MUSICS_VIEW:
			break;
		default:
			return super.onPrepareOptionsMenu(menu);
		}
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.localized_settings:
	    	// Start the preferences activity
	    	Intent i = new Intent(this, LocalizedPreferencesActivity.class);
	    	i.putExtra("FB_APP_ID", FB_APP_ID);
	    	startActivity(i);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static int getCurrentView() {
		return mCurrView;
	}

	/**
	 * Handler of incoming messages from the content provider
	 */
	private class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LocalizedMusicService.MSG_ENTERED:
				break;
			case LocalizedMusicService.MSG_LEFT:
				break;
			case GOT_SPOTS:
				refreshMusicSpots();
				mMapView.invalidate();
			case GOT_CHANNELS:
				displayMusicChannels();
				break;
			case GOT_MUSICS:
				displayMusics();
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}
	
	/**
	 * Helper function to dismiss a progress dialog
	 * @param theProgress the progress dialog to dismiss
	 */
	private void dismissProgress(ProgressDialog theProgress) {
		if(theProgress != null && theProgress.isShowing()){
			theProgress.dismiss();
		}
	}

	private void refreshMusicSpots() {
			if (mCurrSpots != null && mCurrSpots.size() > 0) {
				// First, clear the displayed overlays
				mMapView.getOverlays().clear();
				mSpotOverlays.clear();
				
				// Display the spots
				for (Spot ms : mCurrSpots) {
					// Create bitmap
					Bitmap bmp = BitmapFactory.decodeResource(
							getResources(), R.drawable.spot_pin);
					
					SpotOverlay moc = new SpotOverlay(ms, bmp);
					moc.addListener(new SpotOverlayAdapter(ms) {
						public void onTouchEvent(MotionEvent e, MapView mapView) {
							mCurrSpotId = mSpot.getId();
							Log.d(TAG, "Retrieving channels for spot #"+mCurrSpotId);						
							getChannels(mCurrSpotId);
						}
					});
					
					
					mMapView.getOverlays().add(moc);
					mSpotOverlays.add(moc);
				}
				
				// Finally, add the user's position
				mMapView.getOverlays().add(mLocation);
			}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	// If the current view is the base view, just let super handle it
	    	if (mFlipper.getDisplayedChild() == MAP_VIEW)
	    		return super.onKeyDown(keyCode, event);
	    	
	    	// Else, display the previous view and update the current view
			mFlipper.showPrevious();
			mCurrView = mFlipper.getDisplayedChild();
			
			// Update the current ids
			switch(mCurrView) {
			case MAP_VIEW:
				mCurrSpotId = 0;
				break;
			case CHANNELS_VIEW:
				mCurrChanId = 0;
				break;
			default:
				break;
			}
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	private void displayMusicChannels() {
		if (mCurrSpotId != 0 && mCurrChans != null && mCurrChans.size() > 0) {				
			for (Channel mc : mCurrChans) {
				Log.d(TAG, "Music channel #" + mc.getId() + " - "
						+ mc.getName());
			}

			ListView lv = (ListView) this.findViewById(R.id.channel_list_view);
			lv.setAdapter(
					new MusicChannelAdapter(LocalizedMusicActivity.this,
							R.layout.localized_music_channel_item, mCurrChans)
			);

			lv.setOnItemClickListener(mChannelClickedHandler); 

			mFlipper.showNext();
			mCurrView = CHANNELS_VIEW;
		}			

	}
	
	private void displayMusics() {
		ListView lv = (ListView) findViewById(R.id.music_list_view);
		lv.setAdapter(new MusicAdapter(LocalizedMusicActivity.this,
					R.layout.localized_music_item, mCurrMusics)
		);
		
		// The the on click listener		
		lv.setOnItemClickListener(mMusicClickedHandler);
		
		mFlipper.showNext();
		mCurrView = MUSICS_VIEW;
	}
	
	// Create a message handling object as an anonymous class.
	private OnItemClickListener mChannelClickedHandler = new OnItemClickListener() {
	    public void onItemClick(AdapterView<?> parent, View v, int position, long id)
	    {
	    	Channel mc = mCurrChans.get(position);
	    	
	    	if(mc != null) {
	    		mCurrChanId = mc.getId();
	    	    getMusics(mCurrChanId);
	    	}	    	
	    }
	};
	
	private OnItemClickListener mMusicClickedHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View v, int position, long id) 
		{
			if(LocalizedMusicService.getCurrentSpotId() == mCurrSpotId) {
				if(mCurrMusics.get(position).isPlayable()) {
					LocalizedMusicService.playMusic(mCurrChanId, position);
				}
				else {
					Toast.makeText(LocalizedMusicActivity.this, R.string.not_playable,
							Toast.LENGTH_SHORT).show();
				}
			}
			else {
				Toast.makeText(LocalizedMusicActivity.this, R.string.not_in_spot,
								Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private class MusicAdapter extends ArrayAdapter<Music> {

		private ArrayList<Music> items;

		public MusicAdapter(Context context, int textViewResourceId, 
				ArrayList<Music> items) {
			super(context, textViewResourceId, items);
			this.items = (ArrayList<Music>) items.clone();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.localized_music_item, null);
			}
			Music mc = items.get(position);
			if (mc != null) {
				TextView tt = (TextView) v.findViewById(R.id.music_item_line1);
				TextView bt = (TextView) v.findViewById(R.id.music_item_line2);
				if (tt != null) {
					tt.setText(mc.getTitle());  
					tt.setVisibility(TextView.VISIBLE);
				}
				if(bt != null){                        	
					bt.setText(mc.getArtist());
				}
			}
			return v;
		}
	}	
	
	
	private class MusicChannelAdapter extends ArrayAdapter<Channel> {
        private ArrayList<Channel> items;

        public MusicChannelAdapter(Context context, int textViewResourceId, 
        							ArrayList<Channel> items) {
                super(context, textViewResourceId, items);
                this.items = (ArrayList<Channel>) items.clone();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.localized_music_channel_item, null);
                }
                Channel mc = items.get(position);
                if (mc != null) {
                        TextView tt = (TextView) v.findViewById(R.id.channel_item_line1);
                        TextView bt = (TextView) v.findViewById(R.id.channel_item_line2);
                        if (tt != null) {
                              tt.setText(mc.getName());                            
                        }
                        if(bt != null){
                              bt.setText(mc.getGenre());
                        }
                }
                return v;
       	}
	}

	private void getSpots(int zoom, GeoPoint p1, GeoPoint p2) {
		/*if (mProgressSpot != null && mProgressSpot.isShowing()) {
			return;
		}

		mProgressSpot = ProgressDialog.show(LocalizedMusicActivity.this, "",
				"Retrieving spots...", true, false);*/

		theApp.getMusicSpots(zoom, p1, p2);
	}

	private void getChannels(int spotId) {
		if (mProgressChannel != null && mProgressChannel.isShowing()) {
			return;
		}
		// Show dialog
		mProgressChannel = ProgressDialog.show(LocalizedMusicActivity.this, "",
				"Retrieving channels...", true, false);

		theApp.getMusicChannels(spotId);
	}

	private void getMusics(int channelId) {
		if (mProgressMusic != null && mProgressMusic.isShowing()) {
			return;
		}
		// Show dialog
		mProgressMusic = ProgressDialog.show(LocalizedMusicActivity.this, "",
				"Retrieving musics...", true, false);

		theApp.getMusicItems(mCurrSpotId, channelId);
	}
	
	private Spot findSpot(int spotId) {		
		if (mCurrSpots != null && mCurrSpots.size() > 0 && spotId > 0) {
			for (Spot s : mCurrSpots) {
				if (s.getId() == spotId) {
					return s;
				}
			}
		}		
		return null;
	}

	private Channel findChannel(int channelId) {
		if (mCurrChans != null && mCurrChans.size() > 0 && channelId > 0) {
			for (Channel c : mCurrChans) {
				if (c.getId() == channelId) {
					return c;
				}
			}
		}		
		return null;
	}

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = new Messenger(service);
			
			// Register the activity as the service client
			Message msg = Message.obtain(null, LocalizedMusicService.MSG_REGISTER);
			msg.replyTo = mMessenger;
			
			try {
				mService.send(msg);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
			
			// Unregister the activity as the service client
			Message msg = Message.obtain(null, LocalizedMusicService.MSG_UREGISTER);
			msg.replyTo = mMessenger;
			
			try {
				mService.send(msg);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}		
		}
	};

	private void doBindService() {
		bindService(new Intent(LocalizedMusicActivity.this,
				LocalizedMusicService.class), mConnection,
				Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	private void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	public void onServiceConnected(ComponentName name, IBinder service) {
		MusicUtils.updateNowPlaying(this);		
	}

	public void onServiceDisconnected(ComponentName name) {
		finish();
	}
}
