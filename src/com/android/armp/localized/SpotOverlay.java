package com.android.armp.localized;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.location.Location;
import android.provider.Settings.System;
import android.util.Log;
import android.view.MotionEvent;

import com.android.armp.model.Spot;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class SpotOverlay extends Overlay {

	private static final String TAG = "SpotOverlay";

	private static final int ICON_TRESHOLD = 16;

	private Spot mSpot;
	private Bitmap mBmp;
	private float mTop, mRight, mBottom, mLeft;
	private CustomShapeDrawable mShapeDrawable;
	private boolean mDraggable = false;

	private boolean dragging = false;

	private List<SpotOverlayAdapter> listeners = new ArrayList<SpotOverlayAdapter>();

	public SpotOverlay(Spot spot, Bitmap bmp, boolean draggable) {
		super();
		this.mBmp = bmp;
		this.mSpot = spot;
		this.mDraggable = draggable;
		mShapeDrawable = new CustomShapeDrawable(new OvalShape());
		Log.d(TAG, "Displayed spot: " + spot.toString());
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		Point left = new Point();
		Point bottom = new Point();

		// TO DISPLAY BOTH CASES
		if (!mDraggable || mDraggable) {
			// Transform real coordinates and distances to points and pixels
			GeoPoint g1 = Spatial.getLocationAt(getGeoPoint(),
					(int) mSpot.getRadius(), -Math.PI / 2);// Math.toRadians(180));
			GeoPoint g2 = Spatial.getLocationAt(getGeoPoint(),
					(int) mSpot.getRadius(), Math.PI);// Math.toRadians(90));
			mapView.getProjection().toPixels(g1, left);
			mapView.getProjection().toPixels(g2, bottom);
			// float pixels = mapView.getProjection().metersToEquatorPixels(
			// factor * mSpot.getRadius());

			// Define a circle by its outside box
			mLeft = left.x;// center.x - pixels;
			mBottom = bottom.y;// center.y - pixels;

			mRight = left.x + 2 * (bottom.x - left.x);// center.x + pixels;
			mTop = bottom.y - 2 * (bottom.y - left.y);// center.y + pixels;
		}

		Rect oval = new Rect((int) mLeft, (int) mTop, (int) mRight,
				(int) mBottom);
		mShapeDrawable.setBounds(oval);

		if (mapView.getZoomLevel() > ICON_TRESHOLD) {
			mShapeDrawable.setStrokeColour(Color.argb(160, 0, 40, 255));
			mShapeDrawable.setFillColour(Color.argb(30, 0, 0, 0));
			mShapeDrawable.draw(canvas);

			// pixels = mapView.getProjection().metersToEquatorPixels(
			// factor * mBmp.getWidth());
			canvas.drawBitmap(mBmp, bottom.x - mBmp.getWidth() / 2, left.y
					- mBmp.getHeight(), null);
		} else {
			int red   = Color.red(this.mSpot.getColor());
			int green = Color.green(this.mSpot.getColor());
			int blue  = Color.blue(this.mSpot.getColor());
			mShapeDrawable.setStrokeColour(Color.argb(255, 255, 255, 255));
			if (dragging) {
				mShapeDrawable.setFillColour(Color.argb(80, 0, 255, 40));
			} else {
				mShapeDrawable.setFillColour(Color.argb(80, red, green, blue));
			}
			mShapeDrawable.draw(canvas);
		}
	}

	private int xDragImageOffset = 0;
	private int yDragImageOffset = 0;
	private int xDragTouchOffset = 0;
	private int yDragTouchOffset = 0;

	private boolean inDrag = false;

	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {
		if (mDraggable) {
			final int action = e.getAction();
			final int x = (int) e.getX();
			final int y = (int) e.getY();

			GeoPoint pt1 = mapView.getProjection().fromPixels(
					x - xDragTouchOffset, y - yDragTouchOffset);
			GeoPoint pt2 = this.getGeoPoint();
			float[] res = { 0.0f };
			Location.distanceBetween(pt1.getLatitudeE6() / 1E6,
					pt1.getLongitudeE6() / 1E6, pt2.getLatitudeE6() / 1E6,
					pt2.getLongitudeE6() / 1E6, res);
			double dist = (res[0] * 1E4) / mapView.getLatitudeSpan();

			if (action == MotionEvent.ACTION_DOWN) {
				Log.d(TAG, "in ACTION_DOWN");
				if (dist < mSpot.getRadius()) {
					inDrag = true;
				}
			} else if (action == MotionEvent.ACTION_MOVE && inDrag) {
				Log.d(TAG, "in ACTION_MOVE");
				this.dragging = true;
				mapView.getOverlays().remove(this);
				GeoPoint pt = mapView.getProjection().fromPixels(
						x - xDragTouchOffset, y - yDragTouchOffset);
				// setting the spot new coordinates
				mSpot.setLatitude(pt.getLatitudeE6() / 1E6);
				mSpot.setLongitude(pt.getLongitudeE6() / 1E6);
				mapView.getOverlays().add(this);
			} else if (action == MotionEvent.ACTION_UP && inDrag) {
				Log.d(TAG, "in ACTION_UP");
				this.dragging = false;
				GeoPoint pt = mapView.getProjection().fromPixels(
						x - xDragTouchOffset, y - yDragTouchOffset);
				// setting the spot new coordinates
				mSpot.setLatitude(pt.getLatitudeE6() / 1E6);
				mSpot.setLongitude(pt.getLongitudeE6() / 1E6);
				mShapeDrawable = new CustomShapeDrawable(new OvalShape());
				
				inDrag = false;
			}
		} else {
			if (e.getAction() == MotionEvent.ACTION_DOWN) {
				if (clickedIn(e)) {
					for (SpotOverlayAdapter a : listeners) {
						a.onTouchEvent(e, mapView);
					}
				}
			}
		}
		return super.onTouchEvent(e, mapView);
	}

	public Spot getSpot() {
		return this.mSpot;
	}

	private boolean clickedIn(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		return mLeft <= x && x <= mRight && mTop <= y && y <= mBottom;
	}

	private GeoPoint getGeoPoint() {
		int latitude = (int) (1000000.0f * mSpot.getLatitude());
		int longitude = (int) (1000000.0f * mSpot.getLongitude());
		return new GeoPoint(latitude, longitude);
	}

	public void addListener(SpotOverlayAdapter l) {
		this.listeners.add(l);
	}

	private class CustomShapeDrawable extends ShapeDrawable {
		Paint fillpaint, strokepaint;
		private static final int WIDTH = 1;

		public CustomShapeDrawable(Shape s) {
			super(s);
			fillpaint = this.getPaint();
			fillpaint.setARGB(0, 0, 0, 0);
			strokepaint = new Paint(fillpaint);
			strokepaint.setStyle(Paint.Style.STROKE);
			strokepaint.setStrokeWidth(WIDTH);
			strokepaint.setARGB(160, 0, 0, 0);
		}

		@Override
		protected void onDraw(Shape shape, Canvas canvas, Paint fillpaint) {
			shape.draw(canvas, fillpaint);
			shape.draw(canvas, strokepaint);
		}

		public void setStrokeColour(int c) {
			strokepaint.setColor(c);
		}

		public void setFillColour(int c) {
			fillpaint.setColor(c);
		}

	}

	private static final class Spatial {
		// distance from the center to the equator (meters)
		private static final double eq = 6378137.0;
		// distance from the center to the north/south pole (meters)
		private static final double ns = 6356752.3;

		private static final GeoPoint getLocationAt(GeoPoint from,
				int distance, double bearing) {
			double lat1 = Math.toRadians(from.getLatitudeE6() / 1E6);
			double lon1 = Math.toRadians(from.getLongitudeE6() / 1E6);
			double r = getRadiusOfEarth(lat1);
			double dr = distance / r;

			double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dr)
					+ Math.cos(lat1) * Math.sin(dr) * Math.cos(bearing));
			double lon2 = lon1
					+ Math.atan2(
							Math.sin(bearing) * Math.sin(dr) * Math.cos(lat1),
							Math.cos(dr) - Math.sin(lat1) * Math.sin(lat2));
			lon2 = (lon2 + 3 * Math.PI) % (2 * Math.PI) - Math.PI;

			return new GeoPoint((int) (Math.toDegrees(lat2) * 1E6),
					(int) (Math.toDegrees(lon2) * 1E6));
		}

		/**
		 * Get the radius of the earth, in meters, depending on the latitude
		 * 
		 * @param type
		 *            $phi The latitude in radians
		 * @return type The radius of the earth in meters.
		 * @see Formula here: http://en.wikipedia.org/wiki/Earth_radius
		 */
		private static final double getRadiusOfEarth(double phi) {
			double cPhi = Math.cos(phi);
			double sPhi = Math.sin(phi);
			double acPhi = eq * cPhi;
			double bsPhi = ns * sPhi;
			double aacPhi = eq * acPhi;
			double bbsPhi = ns * bsPhi;

			double num = Math.pow(aacPhi, 2) + Math.pow(bbsPhi, 2);
			double den = Math.pow(acPhi, 2) + Math.pow(bsPhi, 2);
			return Math.sqrt(num / den);
		}
	}
}
