package com.android.armp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
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
import android.view.MotionEvent;
import android.view.Window;

import com.android.armp.localized.LocalizedMusicService;
import com.android.armp.localized.MusicChannel;
import com.android.armp.localized.MusicChannelView;
import com.android.armp.localized.MusicSpot;
import com.android.armp.localized.SpotOverlay;
import com.android.armp.localized.SpotOverlayAdapter;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class LocalizedMusicActivity extends MapActivity {

	private List<Overlay> mSpotOverlays = new ArrayList<Overlay>();
	private List<MusicSpot> mMusicSpots;

	private MyLocationOverlay mLocation;
	private MapView mMapView;

	private ProgressDialog mProgressSpot = null;
	private ProgressDialog mProgressChannel = null;
	private ProgressDialog mProgressMusic = null;

	/** Messenger for communicating with service. */
	Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
	boolean mIsBound;

	final Messenger mMessenger = new Messenger(new IncomingHandler());
	private final static String TAG = "LMA";

	public LocalizedMusicActivity() {
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.localized_music_activity);
		setTitle(R.string.maps_title);

		// Display the user's location on the map view
		mMapView = (MapView) findViewById(R.id.mapview);
		mLocation = new MyLocationOverlay(mMapView.getContext(), mMapView);
		mMapView.getOverlays().add(mLocation);
		mLocation.enableCompass();
		mLocation.enableMyLocation();

		// Display the "now playing" bar
		MusicUtils.updateButtonBar(this, R.id.maptab);
		MusicUtils.updateNowPlaying(LocalizedMusicActivity.this);

		// Bind the service
		doBindService();

		MusicSpot s = new MusicSpot(1, 37.0625, -95.677068, 1, new Date());
		List<MusicSpot> list = new ArrayList<MusicSpot>();
		list.add(s);
		refreshMusicSpots(list);
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
			case LocalizedMusicService.MSG_SPOTS:
				mProgressSpot.dismiss(); // Close loading progress dialog
				mProgressSpot = null;
				refreshMusicSpots((List<MusicSpot>) msg.obj);
				break;
			case LocalizedMusicService.MSG_CHANNELS:
				mProgressChannel.dismiss(); // Close loading progress dialog
				mProgressChannel = null; 
				refreshMusicChannels((List<MusicChannel>) msg.obj);
				break;
			case LocalizedMusicService.MSG_MUSICS:
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}

	private void refreshMusicSpots(List<MusicSpot> list) {
		mMusicSpots = list;
		if (list != null && list.size() > 0) {
			for (MusicSpot ms : list) {
				SpotOverlay moc = new SpotOverlay(ms);
				moc.addListener(new SpotOverlayAdapter(ms) {
					public void onTouchEvent(MotionEvent e, MapView mapView) {
						Log.d(TAG,
								"Downloading channels information for spot #"
										+ this.mSpot.getId());
						getChannels(this.mSpot);
					}
				});
				mMapView.getOverlays().add(moc);
				mSpotOverlays.add(moc);
			}
		}
	}

	private void refreshMusicChannels(List<MusicChannel> list) {
		Log.d(TAG, "refreshMusicChannels");
		if (list != null && list.size() > 0) {
			MusicSpot spot = findSpot(list.get(0).getSpotId());
			if (spot != null) {
				spot.setChannels(list);
				for (MusicChannel mc : list) {
					Log.d(TAG, "Music channel #" + mc.getId() + " - " + mc.getName());
				}

				// Marche pas!
				Log.d(TAG, "Ca va bogguer...");
				Activity a = new MusicChannelView(spot, list);
				Intent intent = new Intent(LocalizedMusicActivity.this, MusicChannelView.class);
				a.startActivity(intent);
			}
		}
	}

	private MusicSpot findSpot(int spotId) {
		if (mMusicSpots != null && mMusicSpots.size() > 0 && spotId > 0) {
			for (MusicSpot s : mMusicSpots) {
				if (s.getId() == spotId) {
					return s;
				}
			}
		}
		return null;
	}

	private void getSpots() {
		if (mProgressSpot != null) {
			return;
		}
		GeoPoint me = mLocation.getMyLocation();

		mProgressSpot = ProgressDialog.show(LocalizedMusicActivity.this, "",
				"Retrieving spots...", true, false);

		// Create and send the message
		Message msg = Message.obtain(null, LocalizedMusicService.MSG_SPOTS);
		msg.replyTo = mMessenger;
		msg.obj = me;

		try {
			mService.send(msg);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	private void getChannels(MusicSpot spot) {
		if (mProgressChannel != null) {
			return;
		}
		// Show dialog
		mProgressChannel = ProgressDialog.show(LocalizedMusicActivity.this, "",
				"Retrieving channels...", true, false);

		// Create and send the message
		Message msg = Message.obtain(null, LocalizedMusicService.MSG_CHANNELS);
		msg.replyTo = mMessenger;
		msg.obj = spot;

		try {
			mService.send(msg);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	private void getMusics(int channelId) {
		if (mProgressMusic != null) {
			return;
		}
		// Show dialog
		mProgressMusic = ProgressDialog.show(LocalizedMusicActivity.this, "",
				"Retrieving musics...", true, false);

		// Create and send the message
		Message msg = Message.obtain(null, LocalizedMusicService.MSG_MUSICS);
		msg.replyTo = mMessenger;
		msg.obj = channelId;

		try {
			mService.send(msg);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = new Messenger(service);
//			getSpots();
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
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
}
