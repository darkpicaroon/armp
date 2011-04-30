package com.android.armp.localized;

import com.android.armp.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TabWidget;
import android.widget.TextView;

public class TabLooper extends HorizontalScrollView {
	private boolean oneShot = false;
	private static final String TAG = "TabLooper";
	
	
	public TabLooper(Context context) {
		super(context);		
	}
	
	public TabLooper(Context context, AttributeSet attrs) {
		super(context, attrs);		
	}


	public TabLooper(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	private TabWidget mTw = null;
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if(mTw == null) {
			mTw = (TabWidget) findViewById(R.id.buttonbar);
			//mTw.getBackground().setAlpha(160);
		}
		
	}
	
	
	@Override
	public void onDraw(Canvas canvas) {
		//Log.d(TAG, "Drawiiiing");
		/*TabWidget tw = (TabWidget) findViewById(R.id.buttonbar);
		
		TextView v = (TextView)tw.getChildAt(0);
		TextView vv = (TextView)tw.getChildAt(4);
		
		Rect r = new Rect();
		//tw.getGlobalVisibleRect(r);
		v.getLocalVisibleRect(r);
		int vDelta = computeScrollDeltaToGetChildRectOnScreen(r);
		Rect rr = new Rect();
		
		vv.getLocalVisibleRect(rr);
		int vvDelta = computeScrollDeltaToGetChildRectOnScreen(rr);
		//Log.d(TAG,""+vDelta+ " - "+getMaxScrollAmount());
		
		//tw.
		
		if(Math.abs(vDelta) >= getMaxScrollAmount()-30 && !oneShot) {
			Log.d(TAG, "ONE SHOT");
			int prevOffset = computeHorizontalScrollOffset();
			tw.removeViewAt(0);
			scrollTo(prevOffset-r.width(), 0);
			tw.addView(v, tw.getChildCount());
			//fling(-3);
			//oneShot = true;
		} else if(Math.abs(vvDelta) >= getMaxScrollAmount()-30) {
			int prevOffset = computeHorizontalScrollOffset();
			tw.removeViewAt(4);
			scrollTo(prevOffset+r.width(), 0);
			tw.addView(vv, 0);
			//fling(3);
		}*/
	}

}
