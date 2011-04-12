package com.android.armp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.HeaderGroup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewManager;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.android.armp.localized.ColorPickerDialog;
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
	 * Dialogs for the creation of spots/channels
	 */
	private Dialog mAddSpotDialog = null;
	private Dialog mAddPictureDialog = null;
	private Dialog mAddChannelDialog = null;
	private ColorPickerDialog mColorDialog = null;
	
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
	
	/**
	 * Dialog ids
	 */
	private final static int DIALOG_CREATE_SPOT = 0;
	private final static int DIALOG_CREATE_CHANNEL = 1;
	private final static int DIALOG_SELECT_COLOR = 2;
	private final static int DIALOG_SELECT_PICTURE = 3;
	
	/**
	 * Creation members
	 */
	private Spot mNewSpot = null;
	private Channel mNewChannel = null;
	private SpotOverlay mNewSpotOverlay = null;
//	private mNewSpotColor = null;
	
	/**
	 * Reference to the application context
	 */
	private static ArmpApp theApp;
	
	/**
	 * Facebok variables
	 */
	private static final String FB_APP_ID = "193388247366826";
	private final Facebook mFacebook = new Facebook(FB_APP_ID);
	
	/**
	 * Discovery Mode option
	 */
	private static boolean discoveryMode = false;
	
	// variables for activity response (Camera & Gallery)
	private final static int TAKE_PICTURE = 0;
	private final static int PICK_IMAGE = 1;
	private static Bitmap currentImage;
	
	/**
	 * Event received sent by the application when spots are received after
	 * an async request to the server.
	 */
	private OnSpotsReceivedListener mSpotsListener = new OnSpotsReceivedListener() {
		public void onSpotsReceived(ArrayList<Spot> ms) {			
			// Save the currently displayed spot
			mCurrSpots = ms;
			
			// Send a message to the activity to update the view (on the UI thread)
			mHandler.sendEmptyMessage(GOT_SPOTS);	
		}
	};
	
	/**
	 * Event received sent by the application when channels are received after
	 * an async request to the server.
	 */
	private OnChannelsReceivedListener mChanListener = new OnChannelsReceivedListener() {
		public void onChannelsReceived(ArrayList<Channel> mc) {
			// Save the currently displayed channels
			mCurrChans = mc;
			
			// Send a message to the activity to update the view (on the UI thread)
			mHandler.sendEmptyMessage(GOT_CHANNELS);
			
			// Close the progress dialog
			dismissProgress(mProgressChannel);
		}		
	};
	
	/**
	 * Event received from the application when musics are received after
	 * an async request to the server.
	 */
	private OnMusicsReceivedListener mMusicsListener = new OnMusicsReceivedListener() {
		public void onMusicsReceived(ArrayList<Music> mi) {
			// Save the currently displayed musics
			mCurrMusics = mi;
			
			// Send a message to the activity to update the view (on the UI thread)
			mHandler.sendEmptyMessage(GOT_MUSICS);
			
			// Close the progress dialog
			dismissProgress(mProgressMusic);
		}
	};
	
	/**
	 * Event received from the SmartMapView telling the activity to update
	 * the displayed spots
	 */
	private OnAreaChangedListener mMapAreaListener = new OnAreaChangedListener() {
		public void onAreaChanged(GeoPoint ne, GeoPoint sw) {
			// Call a helper function to retrieve the spots from the server
			getSpots(mMapView.getZoomLevel(), ne, sw);
		}
	};
	
	/**
	 * Helper function to update the now playing widget from the UI thread
	 */
	private final void updateNowPlaying() {
		MusicUtils.updateNowPlaying(this);
	}
	
	/**
	 * Listener listening to changes on the MediaPlaybackService
	 */
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
	
    /**
     * Transition animations between the views (map, channels, musics)
     */
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
		
		// Restore the facebook session if there is a valid one
		if(SessionStore.restore(mFacebook, this))
			Log.d(TAG, "Facebook token: "+mFacebook.getAccessToken());
		
		// Start tracing 
	    // Debug.startMethodTracing("LocalizedMusic");	    
		
			
	}

	@Override
	public void onResume() {
		super.onResume();
		// Update the now playing bar
		MusicUtils.setSpinnerState(this);
		MusicUtils.updateNowPlaying(this);
		
		// Update the currently displayed view
		mCurrView = MAP_VIEW;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		// Bind the activity to the MediaPlaybackService
		mToken = MusicUtils.bindToService(this, this);
        
		// Register intent receiver from the MediaPlaybackService
		// on music playback state changed
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
		
		// stop tracing
	    //Debug.stopMethodTracing();	    
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflates and displays the menu view
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.localized_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		/**
		 * Set the menu view content regarding the currently displayed
		 * view
		 */
		switch(mCurrView) {
		case MAP_VIEW:
			menu.findItem(R.id.add_channel).setVisible(false);
			menu.findItem(R.id.add_musics).setVisible(false);
			menu.findItem(R.id.add_spot).setVisible(true);
			menu.findItem(R.id.discovery_mode).setVisible(true);
			break;
		case CHANNELS_VIEW:
			menu.findItem(R.id.add_channel).setVisible(true);
			menu.findItem(R.id.add_musics).setVisible(false);
			menu.findItem(R.id.add_spot).setVisible(false);
			menu.findItem(R.id.discovery_mode).setVisible(false);
			break;
		case MUSICS_VIEW:
			menu.findItem(R.id.add_channel).setVisible(false);
			menu.findItem(R.id.add_musics).setVisible(true);
			menu.findItem(R.id.add_spot).setVisible(false);
			menu.findItem(R.id.discovery_mode).setVisible(false);
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
	    	i.putExtra("FB_APP_ID", FB_APP_ID); // Pass the FB_ID as an argument
	    	startActivity(i);
	        return true;
	    case R.id.add_spot:
	    	//this.showDialog(DIALOG_CREATE_SPOT);
	    	Log.d(TAG, "Create SPOT");
	    	mNewSpot = new Spot(-1);
	    	setMarkerDialog();
	    	return true;
	    case R.id.add_channel:
	    	//this.showDialog(DIALOG_CREATE_SPOT);
	    	Log.d(TAG, "Create CHANNEL");
	    	mNewChannel = new Channel(-1);
	    	LocalizedMusicActivity.this.showDialog(DIALOG_CREATE_CHANNEL);
	    	return true;
	    case R.id.discovery_mode:
	    	discoveryMode = (discoveryMode) ? false : true;
	    	String debug = (discoveryMode) ? "on" : "off";
	    	Log.d(TAG, "discoveryMode = " + debug);
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	// TODO: Finish implementation of the spots/channels creation
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	    switch(id) {
	    case DIALOG_CREATE_SPOT:
	    	mAddSpotDialog = dialog = addSpotDialog();
	        break;
	    case DIALOG_CREATE_CHANNEL:
	    	mAddChannelDialog = dialog = addChannelDialog();
	        break;
	    case DIALOG_SELECT_COLOR:
	    	dialog = new ColorPickerDialog(this,
	    			mColorListener, Color.BLUE);
	    	break;
	    case DIALOG_SELECT_PICTURE:
	    	mAddPictureDialog = dialog = addPictureDialog();
	    	break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	
	/**
	 * Handles events received from the ColorPickerDialog indicating the color
	 * selected
	 */
	private ColorPickerDialog.OnColorChangedListener mColorListener = 
		new ColorPickerDialog.OnColorChangedListener() {
			public void colorChanged(int color) {
				// Set the color of the spot being created
				mNewSpot.setColor(color);
				
				// Update the color on the view
				mAddSpotDialog.findViewById(R.id.color_preview).setBackgroundColor(color);
				
				// Go back to the spot creation dialog
				LocalizedMusicActivity.this.showDialog(DIALOG_CREATE_SPOT);
			}		
	};
	
	/**
	 * Handles the display of the spot being created, with drag n drop
	 * around the map, and buttons to validate the creation, cancel the process
	 * TODO: Finish the drag n drop on the SpotOverlay class
	 * TODO: Close the button view on OK or cancel
	 */
	private void setMarkerDialog() {
		View layout = this.findViewById(R.id.footer_btn);
		if(layout == null){
			Log.d(TAG, "footer_btn est null!");
			LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.ok_cancel_btn,
			                               (ViewGroup)findViewById(R.id.footer_btn));
			
			// Add the buttons view
			this.addContentView(layout, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
											LayoutParams.WRAP_CONTENT));
		}else{
			layout.setVisibility(View.VISIBLE);
		}
		
		
		// Create bitmap
		Bitmap bmp = BitmapFactory.decodeResource(
				getResources(), R.drawable.spot_pin);
		Log.d(TAG, "before set lat and long");
		// fake position for debugging purposes
		mNewSpot.setLatitude(49.10223849249091);
		mNewSpot.setLongitude(6.232860658932102);
		// Set the initial position of the marker
//		mNewSpot.setLatitude(mLocation.getMyLocation().getLatitudeE6()/1E6);
//		mNewSpot.setLongitude(mLocation.getMyLocation().getLongitudeE6()/1E6);
		mNewSpot.setRadius(100);
		
		// Create a spot overlay, with the draggable flag set at true
		mNewSpotOverlay = new SpotOverlay(mNewSpot, bmp, true);
		mMapView.getOverlays().add(mNewSpotOverlay);
		mSpotOverlays.add(mNewSpotOverlay);
//		Log.d(TAG, "Added the new spot to the map: " + mNewSpot.toString());
		
		// Set the click listener of the ok button
		Button b = (Button)layout.findViewById(R.id.set_spot_position);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mNewSpot = mNewSpotOverlay.getSpot();
				Log.d(TAG, "About to complete form: " + mNewSpot.toString());
				LocalizedMusicActivity.this.showDialog(DIALOG_CREATE_SPOT);	
			}
		});
		
		//TODO: Set the click listener of the cancel button?
		Button c = (Button)layout.findViewById(R.id.cancel_spot_creation);
		c.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				View parentView = (View) v.getParent();
				parentView.setVisibility(View.GONE);
			}
		});
	}
	
	/**
	 * Helper function creating the dialog to add information about a spot
	 * @return The dialog "Add spot" dialog
	 */
	private Dialog addSpotDialog() {
		AlertDialog.Builder builder;
		AlertDialog alertDialog;

		Context mContext = this;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.create_spot_dialog,
		                               (ViewGroup) findViewById(R.id.create_spot_dialog));
		
		builder = new AlertDialog.Builder(mContext);
		
		// Set the click listener of the color selection item
		layout.findViewById(R.id.select_color).setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// Display the color picker dialog
				LocalizedMusicActivity.this.showDialog(DIALOG_SELECT_COLOR);
			}			
		});
		
		// Set the click listener of the picture selection item
		layout.findViewById(R.id.select_picture).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LocalizedMusicActivity.this.showDialog(DIALOG_SELECT_PICTURE);
			}			
		});
		/**
		 * Set the dialog title, the cancel button handler, the next 
		 * button handler and the cancel listener
		 */
		builder.setView(layout)
			   .setTitle(R.string.localized_add_spot)
			   .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				        mNewSpot = null;
				    }
				}).setPositiveButton(R.string.next_step, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	// Null pointer !!!!
				    	EditText e = (EditText)mAddSpotDialog.findViewById(R.id.spot_name_value);
						mNewSpot.setName(e.getEditableText().toString());
						saveSpot(mNewSpot);
				    }
				}).setOnCancelListener(new DialogInterface.OnCancelListener() {					
					public void onCancel(DialogInterface dialog) {
						mNewSpot = null;
					}
				});
		
		alertDialog = builder.create();
		
		return alertDialog;
	}
	
	/**
	 * Helper function creating the dialog to add a picture to a spot
	 * @return The dialog "Add picture" dialog
	 */
	private Dialog addPictureDialog() {
		AlertDialog.Builder builder;
		AlertDialog alertDialog;

		Context mContext = this;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.add_picture_dialog,
		                               (ViewGroup) findViewById(R.id.add_picture_dialog));
		
		builder = new AlertDialog.Builder(mContext); 
		
		// Set the click listener of the 'take picture' item
		layout.findViewById(R.id.take_picture).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "clicked on take_picture");
				SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
		        String date = timeStampFormat.format(new Date());
		        String filepath = Environment.getExternalStorageDirectory() + "spot_"+ date +".jpg";
		        ContentValues values = new ContentValues();
		        values.put(MediaStore.Images.Media.TITLE, filepath);
		        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Log.d(TAG, "URI for file = "+imageUri.toString());
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
						
				startActivityForResult(cameraIntent, TAKE_PICTURE); 
			}			
		});
		
		// Set the click listener of the 'upload picture' item
		layout.findViewById(R.id.upload_picture).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, PICK_IMAGE);
			}			
		});
		/**
		 * Set the dialog title, the cancel button handler, the next 
		 * button handler and the cancel listener
		 */
		builder.setView(layout)
			   .setTitle(R.string.localized_add_picture)
			   .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    }
				}).setPositiveButton(R.string.next_step, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    }
				}).setOnCancelListener(new DialogInterface.OnCancelListener() {					
					public void onCancel(DialogInterface dialog) {
						// TODO
					}
				});
		
		alertDialog = builder.create();
		
		return alertDialog;
	}
	
	/**
	 * Catches the result back from the camera activity
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		
		Log.d(TAG, "onActivityResult");
		
		switch (requestCode) {
		case PICK_IMAGE:
			Uri _uri = data.getData();
			Log.d(TAG, "L'URI est : " + _uri);
			if (_uri != null) {
	            Cursor cursor = getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
	            cursor.moveToFirst();
	            String imageFilePath = cursor.getString(0);
	            Log.d(TAG, "Le FILEPATH de l'image est : " + imageFilePath);
	            ImageView img1 = (ImageView) mAddPictureDialog.findViewById(R.id.gallery_picture_preview);
	            ImageView img2 = (ImageView) mAddSpotDialog.findViewById(R.id.picture_preview);
	            Bitmap myBitmap = BitmapFactory.decodeFile(imageFilePath);
	            img1.setImageBitmap(myBitmap);
	            img2.setImageBitmap(myBitmap);
	            cursor.close();
	            Log.d(TAG, "image selected is:" + imageFilePath);
	        }
			
			break;
		case TAKE_PICTURE:
			if (resultCode == Activity.RESULT_OK) {
				Log.d(TAG, "the resultCode is OK = " + resultCode);
				Uri uri = data.getData();
//				String extras = data.getDataString();
//				Bitmap pic = (Bitmap) extras.get("data");
				Log.d(TAG,"le type du retour est = " +uri);
//				 if (pic != null) {
//					 // Display the picture in the preview_preview icon
//                     ImageView img1 = (ImageView)  mAddPictureDialog.findViewById(R.id.camera_picture_preview);
//                     ImageView img2 = (ImageView)  mAddSpotDialog.findViewById(R.id.picture_preview);
//                    	 img1.setImageBitmap(pic);
//                    	 img2.setImageBitmap(pic);
//                 } else {
//                	 Log.d(TAG, "image is null !!");
//                 }
				}
			break;

		default:
			break;
		}
	}
	
	/**
	 * Helper function creating the dialog to add a new channel to a spot
	 * @return The dialog "Add channel" dialog
	 */
	private Dialog addChannelDialog() {
		Log.d(TAG, "addchanneldialog()");
		AlertDialog.Builder builder;
		AlertDialog alertDialog;

		Context mContext = this;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.create_channel_dialog,
		                               (ViewGroup) findViewById(R.id.create_channel_dialog)); 
		
		builder = new AlertDialog.Builder(mContext); 
		
		/**
		 * Set the dialog title, the cancel button handler, the next 
		 * button handler and the cancel listener
		 */
		builder.setView(layout)
			   .setTitle(R.string.localized_add_channel)
			   .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				        mNewChannel = null;
				    }
				}).setPositiveButton(R.string.next_step, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	// Null pointer !!!!
//				    	EditText e = (EditText)findViewById(R.id.channel_name_value);
//						String channelName = e.getEditableText().toString()
//				    	Log.d(TAG, "channel name set to: " + e.getText().toString());
				    	
				    	// As EditText e is at null set manually a name to complete form test
				    	String channelName = "NewChannelFromAndroid";
				    	mNewChannel.setName(channelName);
				    	mNewChannel.setSpotId(mCurrSpotId);
				    	LocalizedMusicActivity.this.saveChannel(mNewChannel);
				    }
				}).setOnCancelListener(new DialogInterface.OnCancelListener() {					
					public void onCancel(DialogInterface dialog) {
						// TODO
						mNewChannel = null;
					}
				});
		
		alertDialog = builder.create();
		
		return alertDialog;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	/**
	 * Static method used to restore the view
	 * @return The id of the current view (map,channels or musics)
	 */
	public static int getCurrentView() {
		return mCurrView;
	}

	/**
	 * Handler of incoming messages from the Service
	 */
	private class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LocalizedMusicService.MSG_ENTERED:
				Toast.makeText(LocalizedMusicActivity.this, "Entered spot #"+msg.arg1,
						Toast.LENGTH_SHORT).show();
				break;
			case LocalizedMusicService.MSG_LEFT:
				Toast.makeText(LocalizedMusicActivity.this, "Exited spot #"+msg.arg1,
						Toast.LENGTH_SHORT).show();
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
	
	/**
	 * Refresh the display of the music spots
	 */
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

				SpotOverlay moc = new SpotOverlay(ms, bmp, false);
				moc.addListener(new SpotOverlayAdapter(ms) {
					public void onTouchEvent(MotionEvent e, MapView mapView) {
						mCurrSpotId = mSpot.getId();
						//Log.d(TAG, "Retrieving channels for spot #"+mCurrSpotId);						
						getChannels(mCurrSpotId);
					}
				});

				// Add the spot to the view
				mMapView.getOverlays().add(moc);
				mSpotOverlays.add(moc);
			}

			// If the user is creating a spot, display it
			if(mNewSpotOverlay != null) {
				mMapView.getOverlays().add(mNewSpotOverlay);
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
	
	/**
	 * Helper function displaying the music channels view, typically
	 * when the activity receives the result of an async request to the server
	 */
	private void displayMusicChannels() {
		if (mCurrSpotId != 0 && mCurrChans != null && mCurrChans.size() > 0) {				
			// Set the content of the channels list view
			ListView lv = (ListView) this.findViewById(R.id.channel_list_view);
			lv.setAdapter(
					new MusicChannelAdapter(LocalizedMusicActivity.this,
							R.layout.localized_music_channel_item, mCurrChans)
			);

			lv.setOnItemClickListener(mChannelClickedHandler); 
			
			// Go from the map view to the channels view
			mFlipper.showNext();
			mCurrView = CHANNELS_VIEW;
			
			/*for (Channel mc : mCurrChans) {
			Log.d(TAG, "Music channel #" + mc.getId() + " - "
					+ mc.getName());
			}*/
		}			

	}
	
	/**
	 * Helper function displaying the musics view, typically when the activity
	 * receives the result of an async request to the server
	 */
	private void displayMusics() {
		// Set the content of the musics view
		ListView lv = (ListView) findViewById(R.id.music_list_view);
		lv.setAdapter(new MusicAdapter(LocalizedMusicActivity.this,
					R.layout.localized_music_item, mCurrMusics)
		);
		
		lv.setOnItemClickListener(mMusicClickedHandler);
		
		// Go from the channels view to the musics view
		mFlipper.showNext();
		mCurrView = MUSICS_VIEW;
	}

	
	/**
	 *  Listens to clicks on a channel list item
	 */
	private OnItemClickListener mChannelClickedHandler = new OnItemClickListener() {
	    public void onItemClick(AdapterView<?> parent, View v, int position, long id)
	    {
	    	Channel mc = mCurrChans.get(position);
	    	
	    	if(mc != null) {
	    		// Retrieve the musics from the clicked channel
	    		mCurrChanId = mc.getId();
	    	    getMusics(mCurrChanId);
	    	}	    	
	    }
	};
	
	/**
	 * Listens to clicks on a music list item
	 */
	private OnItemClickListener mMusicClickedHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View v, int position, long id) 
		{
			// If we are on the spot of the music, play it if we can
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
	
	/**
	 * View adapter for the display of channels
	 * @author abarreir
	 *
	 */
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
	
	/**
	 * View adapter for the display of musics
	 * @author abarreir
	 */
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
				// set icon image
				if(mc.getSource().contains("http")){
					ImageView iv = (ImageView) v.findViewById(R.id.music_icon);
					iv.setImageResource(R.drawable.itunes_music);
				}
			}
			return v;
		}
	}	
	

	/**
	 * Helper function to retrieve spots from the content provider
	 * @param zoom The zoom level of the map
	 * @param p1 The NE point delimiting the displayed map area
	 * @param p2 The SW point delimiting the displayed map area
	 */
	private void getSpots(int zoom, GeoPoint p1, GeoPoint p2) {
		theApp.getMusicSpots(zoom, p1, p2);
	}

	/**
	 * Helper function to retrieve channels from the content provider
	 * @param spotId The id of the spot we want to retrieve the channels
	 */
	private void getChannels(int spotId) {
		if (mProgressChannel != null && mProgressChannel.isShowing()) {
			return;
		}
		// Show dialog
		mProgressChannel = ProgressDialog.show(LocalizedMusicActivity.this, "",
				"Retrieving channels...", true, false);
		
		// Retrieve the channels from the content provider
		theApp.getMusicChannels(spotId);
	}
	
	/**
	 * Helper function to save a channel from the content provider
	 * @param spotId The id of the spot we want to retrieve the channels
	 */
	private void saveChannel(Channel c) {
		// Show dialog
		mProgressChannel = ProgressDialog.show(LocalizedMusicActivity.this, "",
				"Saving Channel...", true, false);
		
		// Retrieve the channels from the content provider
		theApp.saveMusicChannel(c);
	}
	
	/**
	 * Helper function to save a channel from the content provider
	 * @param spotId The id of the spot we want to retrieve the channels
	 */
	private void saveSpot(Spot s) {
		// Show dialog
		mProgressSpot = ProgressDialog.show(LocalizedMusicActivity.this, "",
				"Saving Spot...", true, false);
		
		// Retrieve the channels from the content provider
		theApp.saveMusicSpot(s);
	}

	/**
	 * Helper function to retrieve musics from the content provider
	 * @param channelId The id of the channel we want to retrive the musics
	 */
	private void getMusics(int channelId) {
		if (mProgressMusic != null && mProgressMusic.isShowing()) {
			return;
		}
		// Show dialog
		mProgressMusic = ProgressDialog.show(LocalizedMusicActivity.this, "",
				"Retrieving musics...", true, false);
		
		// Retrieve the musics from the content provider
		theApp.getMusicItems(mCurrSpotId, channelId);
	}

	/**
	 * Class for interacting with the main interface of the background service.
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
			// Unregister the activity as the service client
			Message msg = Message.obtain(null, LocalizedMusicService.MSG_UREGISTER);
			msg.replyTo = mMessenger;
			
			try {
				mService.send(msg);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
			
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

	public void onServiceConnected(ComponentName name, IBinder service) {
		MusicUtils.updateNowPlaying(this);		
	}

	public void onServiceDisconnected(ComponentName name) {
		finish();
	}
}
