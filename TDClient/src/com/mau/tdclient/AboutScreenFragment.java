package com.mau.tdclient;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AboutScreenFragment extends Fragment{
	MainActivity ma;
	public AboutScreenFragment(MainActivity ma){
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
		MainActivity.screenNo=MainActivity.ABOUT_SCREEN;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.about_screen_fragment, container, false);
		((TextView)(v.findViewById(R.id.title))).setTypeface(Typeface.createFromAsset(ma.getAssets(), "fonts/LCD Display Grid.ttf"));
		((TextView)(v.findViewById(R.id.credits))).setTypeface(Typeface.createFromAsset(ma.getAssets(), "fonts/LCD Display Grid.ttf"));
		((Button)(v.findViewById(R.id.back_button))).setTypeface(Typeface.createFromAsset(ma.getAssets(), "fonts/LCD Display Grid.ttf"));
		((Button)(v.findViewById(R.id.back_button))).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStackImmediate();
			}
			
		});
		return v;
	}
}
