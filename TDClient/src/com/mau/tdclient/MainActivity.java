package com.mau.tdclient;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {
	private SensorManager mSensorManager;
	private Sensor mSensor;
	SensorEventListener listener;
	ShakeMeter meter;
	TextView txt_num;
	MediaPlayer player;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RelativeLayout layout = new RelativeLayout(this);
		createUI(layout);
		prepareAudio();
		setContentView(layout);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		listener = new ShakeListener();
	}
	public void createUI(RelativeLayout layout){
		txt_num = new TextView(this);
		txt_num.setId(1);
		RelativeLayout.LayoutParams txt_params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		meter = new ShakeMeter(this);
		meter.setId(2);
		RelativeLayout.LayoutParams meter_params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		txt_params.addRule(RelativeLayout.BELOW, txt_num.getId());
		
		layout.addView(meter, meter_params);
		layout.addView(txt_num, txt_params);
	}
	public void prepareAudio(){
	    try {
			AssetFileDescriptor afd = getAssets().openFd("buzz.mp3");
			player = new MediaPlayer();
			player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
			player.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void youDie(){
		player.start();
	}
	public void onPause(){
		super.onPause();
		mSensorManager.unregisterListener(listener);
	}
	public void onResume(){
		super.onResume();
		mSensorManager.registerListener(listener, mSensor, SensorManager.SENSOR_DELAY_GAME);
	}
	public void onDestroy(){
		super.onDestroy();
		player.release();
	}
	private class ShakeListener implements SensorEventListener{
		float x,y,z;
		float last_x, last_y, last_z;
		long lastUpdate;
		int SHAKE_THRESHOLD = 650;

		@Override
		public void onSensorChanged(SensorEvent event) {
			long curTime = System.currentTimeMillis();
			// only allow one update every 100ms.
			if ((curTime - lastUpdate) > 50) {
				long diffTime = (curTime - lastUpdate);
				lastUpdate = curTime;

				x = event.values[0];
				y = event.values[1];
				z = event.values[2];

			    float speed = Math.abs(x+y+z-last_x-last_y-last_z) / diffTime * 10000;

			    if (speed > SHAKE_THRESHOLD) {
			    	youDie();
			    }
			    last_x = x;
			    last_y = y;
			    last_z = z;
			    meter.updateValue(speed, SHAKE_THRESHOLD);
			}
		}
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	}
}
