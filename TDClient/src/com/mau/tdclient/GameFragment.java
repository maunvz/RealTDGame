package com.mau.tdclient;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import me.dm7.barcodescanner.zbar.ZBarScannerView.ResultHandler;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.mau.tdgame.models.Event;

public class GameFragment extends Fragment implements ResultHandler{
	public static boolean QREnabled;
	int QR_time = 5000;
	
	public static MainActivity ma;
	private ZBarScannerView mScannerView;
	public GameFragment(MainActivity ma){
		super();
		this.ma=ma;
		QREnabled=false;
	}
	public void onPause(){
		super.onPause();
		disableQR();
	}
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.game_fragment,container,false);
		mScannerView = (ZBarScannerView)(v.findViewById(R.id.zbarscan));
		mScannerView.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					enableQR();
				}
				return false;
			}	
		});
		return v;
	}
	@Override
	public void handleResult(Result rawResult) {
		ma.getNC().sendEvent(new Event(Event.QR_EVENT, ma.getPlayer().getName(), rawResult.getContents(), 0));
	}
	public void onResume(){
		super.onResume();
		ma.screenNo = MainActivity.GAME_SCREEN;
		ma.updateStateDisplay();
		enableQR();
	}
	public void enableQR(){
		if(QREnabled)return;Log.d("CAM", "Enabling Camera");
		mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
		mScannerView.startCamera();
//        QREnabled = true;
//        Animation myFadeOutAnimation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.fade_out);
//		final ImageView v = (ImageView) ma.findViewById(R.id.fader);
//		v.startAnimation(myFadeOutAnimation);
//		
//		myFadeOutAnimation.setAnimationListener(new AnimationListener(){
//
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				v.setBackgroundColor(Color.TRANSPARENT);
//			}
//
//			@Override
//			public void onAnimationRepeat(Animation animation) {}
//
//			@Override
//			public void onAnimationStart(Animation animation) {}
//      		
//      	});
        new Thread(){
        	public void run(){
        		try {
					Thread.sleep(QR_time);
					ma.runOnUiThread(new Runnable(){
						@Override
						public void run() {
							
							disableQR();
						}					
					});
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        }.start();
	}
	public synchronized void disableQR(){
	
		Animation myFadeOutAnimation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.fade_in);
		final ImageView v = (ImageView) ma.findViewById(R.id.fader);
		v.setBackgroundColor(Color.BLACK);
		v.startAnimation(myFadeOutAnimation);
		
		myFadeOutAnimation.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				if(!QREnabled)return;
				Thread t = new Thread(){
					public void run(){
						if(mScannerView != null)
							mScannerView.stopCamera();
					}
				};
				t.start();
				QREnabled=false;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}
      		
      	});
		
		
	}
}
