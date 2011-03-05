package com.android.armp;

import com.android.armp.R;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.os.Bundle;
import android.view.Window;

public class LocalizedMusicActivity extends MapActivity {
	private MyLocationOverlay mLocation;
	private MapView mMapView;

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
        
        mMapView = (MapView) findViewById(R.id.mapview);
        mLocation = new MyLocationOverlay(mMapView.getContext(), mMapView);        
        
        MusicUtils.updateButtonBar(this, R.id.maptab);
        MusicUtils.updateNowPlaying(LocalizedMusicActivity.this);  
    }
    
    @Override
    public void onResume() {
        super.onResume();

        MusicUtils.setSpinnerState(this);
        MusicUtils.updateNowPlaying(LocalizedMusicActivity.this);
        
        mLocation.enableCompass();
        mLocation.enableMyLocation();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	mLocation.disableCompass();
    	mLocation.disableMyLocation();
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}    
}
