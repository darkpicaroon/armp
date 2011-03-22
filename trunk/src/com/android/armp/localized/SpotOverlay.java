package com.android.armp.localized;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.util.Log;
import android.view.MotionEvent;

import com.android.armp.R;
import com.android.armp.model.Spot;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class SpotOverlay extends Overlay {
	private static final float factor = 1.0f; // meters for 1 radius unit
	private static final String TAG = "SpotOverlay";
	
	private Spot mSpot;
	private Bitmap mBmp;
	private Paint mPaint;
	private float top, right, bottom, left;
	private CustomShapeDrawable mShapeDrawable;
	
	private List<SpotOverlayAdapter> listeners = new ArrayList<SpotOverlayAdapter>();

	public SpotOverlay(Spot spot, Bitmap bmp) {
		super();
		this.mBmp = bmp;
		this.mSpot = spot;
		mShapeDrawable = new CustomShapeDrawable(new OvalShape());
		Log.d(TAG, "Displayed spot: "+spot.toString());
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
		Rect oval = new Rect((int)left, (int)top, (int)right, (int)bottom);

		mShapeDrawable.setBounds(oval);
		mShapeDrawable.setStrokeColour(Color.argb(160, 220, 140, 0));
		mShapeDrawable.draw(canvas);
		
		pixels = mapView.getProjection().metersToEquatorPixels(
				factor * mBmp.getWidth());
		
		canvas.drawBitmap(mBmp, center.x-mBmp.getWidth()/2, 
				center.y-mBmp.getHeight(), null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {
		if (e.getAction() == MotionEvent.ACTION_DOWN)
		{
			if (clickedIn(e)) {
				for (SpotOverlayAdapter a : listeners) {
					a.onTouchEvent(e, mapView);
				}
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
	
	private class CustomShapeDrawable extends ShapeDrawable {
		Paint fillpaint, strokepaint;
		private static final int WIDTH = 2; 
		public CustomShapeDrawable(Shape s) {
		    super(s);
		    fillpaint = this.getPaint();
		    fillpaint.setARGB(10, 120, 120, 120);
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

		public void setStrokeColour(int c){
		strokepaint.setColor(c);
		}


		}
}
