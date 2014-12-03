package com.mau.tdclient;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GameEndedFragment extends Fragment{
	MainActivity ma;
	public GameEndedFragment(MainActivity ma){
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
		MainActivity.screenNo=MainActivity.GAME_OVER_SCREEN;
		ma.updateGameOverScreen();
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.game_list_fragment, container, false);
	}
}
