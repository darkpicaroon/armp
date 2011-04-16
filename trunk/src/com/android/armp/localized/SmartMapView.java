package com.android.armp.localized;

import android.content.Context;
import android.graphics.Canvas;
import android.location.Location;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class SmartMapView extends MapView {
	private static final String TAG = "SmartMapView";
	private static final long CHECK_DELAY = 2000;
	private static final double MOTION_TRESHOLD = 3.0;

	private GeoPoint mPrevCenter;
	private boolean inMotion;
	private boolean isTouching;
	private static OnAreaChangedListener mListener;

	public SmartMapView(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
		mPrevCenter = new GeoPoint(0, 0);
	}

	public interface OnAreaChangedListener {
		void onAreaChanged(GeoPoint topLeft, GeoPoint bottomRight);
	}

	public void setOnAreaChangedListener(OnAreaChangedListener l) {
		mListener = l;
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		GeoPoint p = getMapCenter();
		double dist = distanceBetween(p, mPrevCenter);

		// If the user stopped moving around the map
		if (dist < MOTION_TRESHOLD && inMotion && !isTouching) {
			inMotion = false;
			int latSpanCenter = getLatitudeSpan() / 2;
			int lonSpanCenter = getLongitudeSpan() / 2;
			int lat = p.getLatitudeE6();
			int lon = p.getLongitudeE6();

			GeoPoint ne = new GeoPoint(lat + latSpanCenter, lon + lonSpanCenter);
			GeoPoint sw = new GeoPoint(lat - latSpanCenter, lon - lonSpanCenter);

			// Notify the listener
			mListener.onAreaChanged(ne, sw);
		} else if (dist >= MOTION_TRESHOLD) {
			mPrevCenter = p;
			inMotion = true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN)
			isTouching = true;
		else if (e.getAction() == MotionEvent.ACTION_UP)
			isTouching = false;

		return super.onTouchEvent(e);
	}

	private double distanceBetween(GeoPoint p1, GeoPoint p2) {
		float[] res = { 0.0f };
		double lat1 = p1.getLatitudeE6() / 1E6;
		double lat2 = p2.getLatitudeE6() / 1E6;
		double lon1 = p1.getLongitudeE6() / 1E6;
		double lon2 = p2.getLongitudeE6() / 1E6;

		Location.distanceBetween(lat1, lon1, lat2, lon2, res);

		return (res[0] * 1E4) / getLatitudeSpan();
	}
}
