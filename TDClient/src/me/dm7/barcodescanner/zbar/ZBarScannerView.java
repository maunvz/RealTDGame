package me.dm7.barcodescanner.zbar;

import java.util.List;

import com.mau.tdclient.GameFragment;
import com.mau.tdclient.MainActivity;
import com.mau.tdclient.R;

import me.dm7.barcodescanner.core.BarcodeScannerView;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class ZBarScannerView extends BarcodeScannerView {
	static int result = 0;
	LiveScanThread lst = new LiveScanThread();
    public interface ResultHandler {
        public void handleResult(Result rawResult);
    }

    static {
        System.loadLibrary("iconv");
    }

//    private ImageScanner mScanner;
    static ResultHandler mResultHandler;

    public ZBarScannerView(Context context) {
        super(context);
//        setupScanner();
    }

    public ZBarScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
//        setupScanner();
    }

    public void setFormats(List<BarcodeFormat> formats) {
//        mFormats = formats;
//        setupScanner();
    }

    public void setResultHandler(ResultHandler resultHandler) {
        mResultHandler = resultHandler;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
      	lst = new LiveScanThread(data,camera,getContext());
    	lst.start();
    	if(MainActivity.screenNo == MainActivity.JOIN_SCREEN)
    		camera.setOneShotPreviewCallback(this);
    	if(!GameFragment.QREnabled&&MainActivity.screenNo==MainActivity.GAME_SCREEN){
    		camera.setOneShotPreviewCallback(this);

        	GameFragment.QREnabled=true;
        	Animation myFadeOutAnimation = AnimationUtils.loadAnimation(GameFragment.ma, R.anim.fade_out);
    		final ImageView v = (ImageView) GameFragment.ma.findViewById(R.id.fader);
    		v.startAnimation(myFadeOutAnimation);
    		
    		myFadeOutAnimation.setAnimationListener(new AnimationListener(){

    			@Override
    			public void onAnimationEnd(Animation animation) {
    				v.setBackgroundColor(Color.TRANSPARENT);
    			}

    			@Override
    			public void onAnimationRepeat(Animation animation) {}

    			@Override
    			public void onAnimationStart(Animation animation) {}
          		
          	});
        }
    }
}