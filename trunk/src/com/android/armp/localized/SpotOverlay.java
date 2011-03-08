package com.android.armp.localized;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class SpotOverlay extends Overlay {
	private static final float factor = 500000.0f; // meters for 1 radius unit
	
	private MusicSpot mSpot;
	private Paint mPaint;
	private float top, right, bottom, left;
	
	private List<SpotOverlayAdapter> listeners = new ArrayList<SpotOverlayAdapter>();

	public SpotOverlay(MusicSpot spot) {
		super();
		this.mSpot = spot;
		this.mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(0x06C);
		mPaint.setAlpha(127);
		mPaint.setStrokeWidth(2.0f);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		Point center = new Point();

		// Transform real coordinates and distances to points and pixels
		mapView.getProjection().toPixels(getGeoPoint(), center);
		float pixels = mapView.getProjection().metersToEquatorPixels(
				factor * mSpot.getRadius());

		// Define a circle by its outside box
		left = center.x - pixels;
		top = center.y - pixels;
		right = center.x + pixels;
		bottom = center.y + pixels;
		RectF oval = new RectF(left, top, right, bottom);
		canvas.drawOval(oval, mPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {
		if (clickedIn(e)) {
			for (SpotOverlayAdapter a : listeners) {
				a.onTouchEvent(e, mapView);
			}
		}
		return super.onTouchEvent(e, mapView);
	}

	private boolean clickedIn(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		return left <= x && x <= right
		   &&  top <= y && y <= bottom;
	}

	private GeoPoint getGeoPoint() {
		int latitude = (int) (1000000.0f * mSpot.getLatitude());
		int longitude = (int) (1000000.0f * mSpot.getLongitude());
		return new GeoPoint(latitude, longitude);
	}

	public void addListener(SpotOverlayAdapter l) {
		this.listeners.add(l);
	}
}
