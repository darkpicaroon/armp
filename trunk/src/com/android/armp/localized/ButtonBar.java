package com.android.armp.localized;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabWidget;

import java.io.IOException;
import java.util.List;

import com.android.armp.R;

public class ButtonBar extends TabWidget implements SurfaceHolder.Callback {
    private final String TAG = "BtnBar";

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;

    public ButtonBar(Context context) {
        super(context);

        //mSurfaceView = new SurfaceView(context);
        //addView(mSurfaceView);
        mSurfaceView = (SurfaceView)findViewById(R.id.camera_surface);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    public ButtonBar(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	
    	
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();
        }
    }

    public void switchCamera(Camera camera) {
       setCamera(camera);
       try {
           camera.setPreviewDisplay(mHolder);
       } catch (IOException exception) {
           Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
       }
       Camera.Parameters parameters = camera.getParameters();
       parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
       requestLayout();

       camera.setParameters(parameters);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
       final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        //setMeasuredDimension(width, height);

        //if (mSupportedPreviewSizes != null) {
          //  mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, getMeasuredWidth(), getMeasuredHeight());
        //}
        
        int nbTabs = getChildCount();
        int rWidth = 0;
        
        for(int i = 1; i < nbTabs; ++i) {
        	View v = getChildAt(i);
        	rWidth += v.getWidth();
        }
        
        if(rWidth > 0) {
        	setMeasuredDimension(rWidth, getMeasuredHeight());
        }
        
        Log.d(TAG, "Height: "+getMeasuredHeight() + " - Width: "+rWidth);
        
        if(mSurfaceView == null) {
	        mSurfaceView = (SurfaceView)findViewById(R.id.camera_surface);
	        Log.d(TAG, "YEPA");
	
	        // Install a SurfaceHolder.Callback so we get notified when the
	        // underlying surface is created and destroyed.
	        try {
	        mHolder = mSurfaceView.getHolder();
	        mHolder.addCallback(this);
	        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	        } catch(Exception e) {
	        	Log.e(TAG, e.getMessage());
	        }
        }
        
    }
    
    private long i = 0;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	Log.d(TAG, "??????????????????????????");
    	//if(++i%15 == 0)
    	super.onLayout(changed, l, t, r, b);
    	
    	if(mSurfaceView != null) {
        	LayoutParams lp = new LayoutParams(768, 432);
        	lp.setMargins(0, -385, 0, 0);
        	mSurfaceView.setLayoutParams(lp);
        	View child = getChildAt(1);
        	lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        	lp.setMargins(-768, 0, 0, 0);
        	child.setLayoutParams(lp);
    	}
    	
    	
        /*if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = 300;//getMeasuredWidth();
            int previewHeight = 97;//getMeasuredHeight();
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }*/
        
        //super.onLayout(changed, l, t, r, b);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
    	Log.d(TAG, "OKI???");
        try {
            if (mCamera != null) {
            	Log.d(TAG, "OKI!!!!");
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }


    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 3;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
    	
    	if(mCamera != null) {
    		int height=768, width=450;
    		mCamera.stopPreview();
    		 Parameters parameters = mCamera.getParameters();

    	        parameters.setPreviewSize(width, height);                           
	            mCamera.setDisplayOrientation(90);    
	        mCamera.setParameters(parameters);
	        mCamera.startPreview();
	        //requestLayout();
    	}
    }

}
