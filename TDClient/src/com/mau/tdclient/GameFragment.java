package com.mau.tdclient;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import me.dm7.barcodescanner.zbar.ZBarScannerView.ResultHandler;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;

import com.mau.tdgame.models.Event;

public class GameFragment extends Fragment implements ResultHandler{
	boolean QREnabled;
	int QR_time = 5000;
	
	MainActivity ma;
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
		ma.getNC().sendEvent(new Event(Event.QR_EVENT, ma.getPlayer().getName(), rawResult.getContents()));
	}
	public void onResume(){
		super.onResume();
		ma.screenNo = MainActivity.GAME_SCREEN;
		ma.updateScoreDisplay();
		enableQR();
	}
	public void enableQR(){
		if(QREnabled)return;Log.d("CAM", "Enabling Camera");
		mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();
        QREnabled=true;
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
		if(!QREnabled)return;
		if(mScannerView != null)
			mScannerView.stopCamera();
		QREnabled=false;
	}
}
