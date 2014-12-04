package com.mau.tdclient;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeScreenFragment extends Fragment{
	MainActivity ma;
	public HomeScreenFragment(MainActivity ma){
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
		MainActivity.screenNo=MainActivity.HOME_SCREEN;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.home_screen_fragment, container, false); 
		((TextView)(v.findViewById(R.id.title))).setTypeface(Typeface.createFromAsset(ma.getAssets(), "fonts/LCD Display Grid.ttf"));
		return v;
	}
}
