package com.mau.tdclient;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WaitingRoomFragment extends Fragment{
	MainActivity ma;
	public WaitingRoomFragment(MainActivity ma){
		super();
		this.ma=ma;
	}
	public void onPause(){
		super.onPause();
	}
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	public void onResume(){
		super.onResume();
		ma.screenNo=MainActivity.WAIT_SCREEN;
		ma.updateWaitRoom();
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.waiting_room_fragment, container, false);
	}
}
