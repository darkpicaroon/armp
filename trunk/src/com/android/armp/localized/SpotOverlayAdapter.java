package com.android.armp.localized;

import android.view.MotionEvent;

import com.google.android.maps.MapView;

public class SpotOverlayAdapter {
	
	protected MusicSpot mSpot;
	
	public SpotOverlayAdapter(MusicSpot spot) {
		this.mSpot = spot;
	}
	
	public void onTouchEvent(MotionEvent e, MapView mapView) {
	}
}
