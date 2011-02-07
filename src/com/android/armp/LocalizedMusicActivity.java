package com.android.armp;

import com.android.armp.R;
import com.google.android.maps.MapActivity;

import android.os.Bundle;
import android.view.Window;

public class LocalizedMusicActivity extends MapActivity {    

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
        
        MusicUtils.updateButtonBar(this, R.id.maptab);
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}    
}
