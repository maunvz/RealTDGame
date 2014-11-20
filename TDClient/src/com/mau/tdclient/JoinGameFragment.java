package com.mau.tdclient;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import me.dm7.barcodescanner.zbar.ZBarScannerView.ResultHandler;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class JoinGameFragment extends Fragment implements ResultHandler{
	MainActivity ma;
	private ZBarScannerView mScannerView;

	public JoinGameFragment(MainActivity ma){
		this.ma = ma;
	}
	public void onPause(){
		super.onPause();
		if(mScannerView != null)
			mScannerView.stopCamera();
	}
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.join_game_fragment, container, false);
		mScannerView = (ZBarScannerView)(v.findViewById(R.id.zbarscan));
		return v;
	}
	@Override
	public void handleResult(Result rawResult) {
		ma.setQRId(rawResult.getContents());
	}
	public void onResume(){
		super.onResume();
		mScannerView.setResultHandler(this);
        mScannerView.startCamera(); 
	}
}
