package com.mau.tdclient;

import android.app.Fragment;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlayerStateDisplayFragment extends Fragment {
	private static View view;
	public void onPause(){
		super.onPause();
	}
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		if (view != null) {
	        ViewGroup parent = (ViewGroup) view.getParent();
	        if (parent != null)
	            parent.removeView(view);
	    }
	    try {
	        view = inflater.inflate(R.layout.player_state_display_fragment, container, false);
	    } catch (InflateException e) {
	        /* map is already there, just return view as it is */
	    }
	    return view;
	}
}
