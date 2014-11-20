package com.mau.tdclient;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import me.dm7.barcodescanner.zbar.ZBarScannerView.ResultHandler;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mau.tdgame.models.Event;

public class GameFragment extends Fragment implements ResultHandler{
	MainActivity ma;
	private ZBarScannerView mScannerView;
	public GameFragment(MainActivity ma){
		super();
		this.ma=ma;
	}
	public void onPause(){
		super.onPause();
		if(mScannerView != null)
			mScannerView.stopCamera();
	}
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		ma.screenNo = MainActivity.GAME_SCREEN;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.game_fragment,container,false);
		mScannerView = (ZBarScannerView)(v.findViewById(R.id.zbarscan));
        return v;
	}
	@Override
	public void handleResult(Result rawResult) {
		ma.getNC().sendEvent(new Event(Event.QR_EVENT, ma.getPlayer().getName(), rawResult.getContents()));
	}
	public void onResume(){
		super.onResume();
		mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera(); 
	}
}
