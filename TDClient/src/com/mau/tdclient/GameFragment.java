package com.mau.tdclient;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import me.dm7.barcodescanner.zbar.ZBarScannerView.ResultHandler;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
		if(ma.getPlayer().getTeam()==0){
			if(rawResult.getContents().equals("base_0")&&ma.getPlayer().hasFlag){
				ma.capture(null);
			}
			else if(rawResult.getContents().equals("base_1")){
				ma.capture(null);
			}
		}
		else if(ma.getPlayer().getTeam()==1){
			if(rawResult.getContents().equals("base_1")&&ma.getPlayer().hasFlag){
				ma.capture(null);
			}
			else if(rawResult.getContents().equals("base_0")){
				ma.capture(null);
			}
		}
		System.out.println(rawResult.getContents());
		ma.capture(null);
		onResume();
	}
	public void onResume(){
		super.onResume();
		mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera(); 
	}
}
