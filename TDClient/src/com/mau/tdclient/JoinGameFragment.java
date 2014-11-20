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
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

public class JoinGameFragment extends Fragment implements ResultHandler{
	boolean QREnabled;
	int QR_time = 5000;
	
	MainActivity ma;
	private ZBarScannerView mScannerView;
	
	public JoinGameFragment(MainActivity ma){
		this.ma = ma;
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
		View v = inflater.inflate(R.layout.join_game_fragment, container, false);
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
		ma.setQRId(rawResult.getContents());
	}
	public void onResume(){
		super.onResume();
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
