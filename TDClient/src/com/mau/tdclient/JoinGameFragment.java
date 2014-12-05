package com.mau.tdclient;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import me.dm7.barcodescanner.zbar.ZBarScannerView.ResultHandler;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class JoinGameFragment extends Fragment implements ResultHandler{
	boolean QREnabled;
	int QR_time = 5000;
	private View v;
	MainActivity ma;
	private ZBarScannerView mScannerView;
	private RelativeLayout vs;
	
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
		ma.screenNo=MainActivity.JOIN_SCREEN;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		v = inflater.inflate(R.layout.join_game_fragment, container, false);
		((EditText)(v.findViewById(R.id.username_edit_text))).setTypeface(Typeface.createFromAsset(ma.getAssets(), "fonts/LCD Display Grid.ttf"));
		((TextView)(v.findViewById(R.id.qr_id_textview))).setTypeface(Typeface.createFromAsset(ma.getAssets(), "fonts/LCD Display Grid.ttf"));

		mScannerView = (ZBarScannerView)(v.findViewById(R.id.zbarscan));
		
		ViewTreeObserver vto = mScannerView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		  @Override
		  public void onGlobalLayout() {
			  RelativeLayout.LayoutParams ll = (RelativeLayout.LayoutParams)mScannerView.getLayoutParams();
			  ll.width = (int)(mScannerView.getHeight()/1.333333);
			  mScannerView.setLayoutParams(ll);
		    ViewTreeObserver obs = mScannerView.getViewTreeObserver();
		    obs.removeGlobalOnLayoutListener(this);
		  }
		});
		
		Button rescanButton = (Button)v.findViewById(R.id.buttonToRescan);
		((Button)v.findViewById(R.id.connect_button)).setTypeface(Typeface.createFromAsset(ma.getAssets(), "fonts/LCD Display Grid.ttf"));
		rescanButton.setTypeface(Typeface.createFromAsset(ma.getAssets(), "fonts/LCD Display Grid.ttf"));
		rescanButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(ma, "buttonclick", Toast.LENGTH_LONG);
				
				enableQR();
			}
			
		});
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
//		vs.setDisplayedChild(0);
//		disableQR();
		String playerNumber = rawResult.getContents();
		if(rawResult.getContents().contains("player")){
			disableQR();
		}
		ma.setQRId(playerNumber);
//		v.findViewById(R.id.buttonToRescan).setVisibility(View.VISIBLE);
	}
	public void onResume(){
		super.onResume();
		enableQR();
//		disableQR();
	}
	public void enableQR(){
		if(QREnabled)return;Log.d("CAM", "Enabling Camera");
		mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();
        QREnabled=true;
//        mScannerView.setVisibility(View.VISIBLE);
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
