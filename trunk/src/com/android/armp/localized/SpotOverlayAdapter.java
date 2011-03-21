package com.android.armp.localized;

import android.view.MotionEvent;

import com.android.armp.model.Spot;
import com.google.android.maps.MapView;

public class SpotOverlayAdapter {
	
	protected Spot mSpot;
	
	public SpotOverlayAdapter(Spot spot) {
		this.mSpot = spot;
	}
	
	public void onTouchEvent(MotionEvent e, MapView mapView) {
	}
}
