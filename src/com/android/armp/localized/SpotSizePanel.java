/***
	Copyright (c) 2008-2011 CommonsWare, LLC
	Licensed under the Apache License, Version 2.0 (the "License"); you may not
	use this file except in compliance with the License. You may obtain	a copy
	of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
	by applicable law or agreed to in writing, software distributed under the
	License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
	OF ANY KIND, either express or implied. See the License for the specific
	language governing permissions and limitations under the License.
	
	From _The Busy Coder's Guide to Advanced Android Development_
		http://commonsware.com/AdvAndroid
*/

	 
package com.android.armp.localized;

import com.android.armp.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SpotSizePanel extends RelativeLayout {
	private int speed = 300;
	private boolean isOpen = false;
	private Context context = null;
	private float mHeight;
	
	public SpotSizePanel(final Context ctxt, AttributeSet attrs) {
		super(ctxt, attrs);
		context = ctxt;
		TypedArray a=ctxt.obtainStyledAttributes(attrs,R.styleable.SlidingPanel,
												0, 0);
		
		speed=a.getInt(R.styleable.SlidingPanel_speed, 300);
		
		a.recycle();
	}
	
	public void setViewHeight(float height) {
		mHeight = height;
		mHeight = .0f;
	}
	
	public void toggle() {
		TranslateAnimation anim=null;
		float sX = mHeight - 75.0f;

		isOpen=!isOpen;
		
		if (isOpen) {
			anim=new TranslateAnimation(0.0f, 0.0f, sX, mHeight);
		}
		else {
			anim=new TranslateAnimation(0.0f, 0.0f, mHeight, sX);
		}		
		
		anim.setAnimationListener(collapseListener);
		anim.setDuration(speed);
		anim.setInterpolator(new AccelerateInterpolator(1.0f));
		anim.setInterpolator(new BounceInterpolator());
		startAnimation(anim);
	}
	
	Animation.AnimationListener collapseListener=new Animation.AnimationListener() {
		public void onAnimationEnd(Animation animation) {
			if(!isOpen) {
				setVisibility(View.GONE);
			}
		}
		
		public void onAnimationRepeat(Animation animation) {
			// not needed
		}
		
		public void onAnimationStart(Animation animation) {
			// not needed
			if(isOpen){
				setVisibility(View.VISIBLE);
			}
		}
	};
}
