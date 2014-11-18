package me.dm7.barcodescanner.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class CameraPreview extends TextureView implements TextureView.SurfaceTextureListener,Camera.PreviewCallback{
	public CameraPreview(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	static Camera mCamera;
    private Handler mAutoFocusHandler;
    private boolean mPreviewing = false;
    private boolean mAutoFocus = true;
    private boolean mSurfaceCreated = false;
    private Camera.PreviewCallback mPreviewCallback;
    public void setCamera(Camera camera, Camera.PreviewCallback previewCallback) {
        mCamera = camera;
        mPreviewCallback = previewCallback;
        mAutoFocusHandler = new Handler();
    }
    public void initCameraPreview() {
    	
        if(mCamera != null) {
        	System.out.println("I'm trying");
            if(mPreviewing) {
                requestLayout();
            } else {
            	
                showCameraPreview();
            }
            
        }
    }
    public void showCameraPreview() {
    	onSurfaceTextureAvailable(this.getSurfaceTexture(),this.getWidth(),this.getHeight());
        if(mCamera != null) {
            try {
                mPreviewing = true;
                mCamera.setOneShotPreviewCallback(mPreviewCallback);
                mCamera.startPreview();
            	System.out.println("I'm showing a preview");
            } catch (Exception e) {
                System.out.println("YOU DUN FUCKED UP");
            }
        }
    }


    public void stopCameraPreview() {
        if(mCamera != null) {
            try {
                mPreviewing = false;
                mCamera.cancelAutoFocus();
                mCamera.setOneShotPreviewCallback(null);
                mCamera.stopPreview();
            } catch(Exception e) {
                Log.e("PREVIW", e.toString(), e);
            }
        }
    }
    public void setAutoFocus(boolean state) {
        if(mCamera != null && mPreviewing) {
            if(state == mAutoFocus) {
                return;
            }
            mAutoFocus = state;
            if(mAutoFocus) {
                mCamera.autoFocus(null);
            } else {
                mCamera.cancelAutoFocus();
            }
        }
    }
    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }
    
	private Camera.Size getBestPreviewSize(int width, int height)
	{
	        Camera.Size result=null;    
	        Camera.Parameters p = mCamera.getParameters();
	        double ratio = height/width;
	        double currentBest = 100;
	        for (Camera.Size size : p.getSupportedPreviewSizes()) {
	            if (Math.abs(size.height/size.width - ratio)<currentBest ) {
	                result = size;
	            }
	        }
	    return result;

	}
	private static Pair<Integer, Integer> getMaxSize(List<Camera.Size> list)
	{
	    int width = 0;
	    int height = 0;

	    for (Camera.Size size : list) {
	        if (size.width * size.height > width * height)
	        {
	            width = size.width;
	            height = size.height;
	        }
	    }

	    return new Pair<Integer, Integer>(width, height);
	}
	private void initPreview(SurfaceTexture surface, int width, int height) {
	    try {
	        mCamera.setPreviewTexture(surface);
	    } catch (Throwable t) {
	        Log.e("CameraManager", "Exception in setPreviewTexture()", t);
	    }
	    WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
	    Camera.Parameters parameters = mCamera.getParameters();
	    Size previewSize = parameters.getSupportedPreviewSizes().get(0);

	    float ratioSurface = width > height ? (float) width / height : (float) height / width;
	    float ratioPreview = (float) previewSize.width / previewSize.height;

	    int scaledHeight = 0;
	    int scaledWidth = 0;
	    float scaleX = 1f;
	    float scaleY = 1f;

	    boolean isPortrait = false;

	    if (previewSize != null) {
	        parameters.setPreviewSize(previewSize.width, previewSize.height);
	        if (display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180) {
	            mCamera.setDisplayOrientation(display.getRotation() == Surface.ROTATION_0 ? 90 : 270);
	            System.out.println("Is portrait");
	            parameters.setRotation(90);
	            isPortrait = true;
	        } else if (display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270) {
	            mCamera.setDisplayOrientation(display.getRotation() == Surface.ROTATION_90 ? 0 : 180);
	            System.out.println("Is landscape");
	            parameters.setRotation(0);
	            isPortrait = false;
	        }
	        if (isPortrait && ratioPreview > ratioSurface) {
	            scaledWidth = width;
	            scaledHeight = (int) (((float) previewSize.width / previewSize.height) * width);
	            scaleX = 1f;
	            scaleY = (float) scaledHeight / height;
	        } else if (isPortrait && ratioPreview < ratioSurface) {
	            scaledWidth = (int) (height / ((float) previewSize.width / previewSize.height));
	            scaledHeight = height;
	            scaleX = (float) scaledWidth / width;
	            scaleY = 1f;
	        } else if (!isPortrait && ratioPreview < ratioSurface) {
	            scaledWidth = width;
	            scaledHeight = (int) (width / ((float) previewSize.width / previewSize.height));
	            scaleX = 1f;
	            scaleY = (float) scaledHeight / height;
	        } else if (!isPortrait && ratioPreview > ratioSurface) {
	            scaledWidth = (int) (((float) previewSize.width / previewSize.height) * width);
	            scaledHeight = height;
	            scaleX = (float) scaledWidth / width;
	            scaleY = 1f;
	        }           
	        mCamera.setParameters(parameters);
	    }

	    // calculate transformation matrix
	    Matrix matrix = new Matrix();

	    matrix.setScale(scaleX, scaleY);
	    this.setTransform(matrix);
	}
	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
			int height) {
		// TODO Auto-generated method stub
		System.out.println("YOU");
        try {
      	  //mCamera.setDisplayOrientation(90);
      	  
		      	Camera.Parameters parameters = mCamera.getParameters();
		      	parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		
		      	Pair<Integer, Integer> size = getMaxSize(parameters.getSupportedPreviewSizes());
		      	parameters.setPreviewSize(size.first, size.second);
		
		      	mCamera.setParameters(parameters);


				mCamera.setPreviewTexture(surface);
				mCamera.startPreview();
        } catch (IOException ioe) {
            // Something bad happened
        }
        initPreview(surface,width,height);
	}
	
	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
		// TODO Auto-generated method stub
		mCamera.stopPreview(); 
        mCamera.setPreviewCallback(null);
        mCamera.release();
        
        mCamera = null;
		return false;
	}
	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
			int arg2) {
		System.out.println("Changed");
		
	}
	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
		System.out.println("Updated");
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {}
	
}