package com.mau.tdclient;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;
import android.widget.TextView;

public class ShakeListener implements SensorEventListener{
	float x,y,z;
	float fx,fy,fz = 0;
	float filter = 0.3f;
	float last_x, last_y, last_z;
	long lastUpdate;
	int timesOverThreshold = 0;
	boolean wasTriggeredBefore = false;
	MainActivity ma;
	static private boolean listenerOn = true;
	public ShakeListener(MainActivity ma){
		this.ma=ma;
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		if(listenerOn){
			long curTime = System.currentTimeMillis();
			// only allow one update every 100ms.
			if ((curTime - lastUpdate) > 50) {
				long diffTime = (curTime - lastUpdate);
				lastUpdate = curTime;
	
				x = event.values[0];
				y = event.values[1];
				z = event.values[2];
				
				float gX = x/SensorManager.GRAVITY_EARTH;
				float gY = y/SensorManager.GRAVITY_EARTH;
				float gZ = z/SensorManager.GRAVITY_EARTH;
				float speed = FloatMath.sqrt(gX * gX + gY * gY + gZ * gZ)*SensorManager.GRAVITY_EARTH;
				speed -= SensorManager.GRAVITY_EARTH;
				speed = Math.abs(speed);
	
			    //float speed = Math.abs(x+y+z-last_x-last_y-last_z) / diffTime * 10000;
			    float fspeed = 0;
			    //fspeed = speed*filter + fspeed*(1-filter);
			    float max = ma.getGameState().gameSensitivity*ma.getPlayer().getSensitivity();
			    if(ma.getPlayer().stronger){
			    	max*=3;
			    	((TextView)ma.findViewById(R.id.status_message_textview)).setText("SUPER SNEAKY "+max);
			    }
			    else{
			    	((TextView)ma.findViewById(R.id.status_message_textview)).setText("weak.");
			    }
			    if (speed > max) {
			    		ma.youDie();
			    }
			    last_x = x;
			    last_y = y;
			    last_z = z;
			    if(ma.screenNo==MainActivity.GAME_SCREEN){
//			    	max = ma.getGameState().gameSensitivity*ma.getPlayer().getSensitivity();
//			    	if(ma.getPlayer().stronger)max*=3;
//			    	((TextView)ma.findViewById(R.id.status_message_textview)).setText(speed+" "+max);
			    	((ShakeMeter)ma.findViewById(R.id.shake_meter_1)).updateValue(speed, max,Color.GREEN,-1);
//			    	((ShakeMeter)ma.findViewById(R.id.shake_meter_2)).updateValue(speed, max);
			    }
			}
		}
		else{
			timesOverThreshold = 0;
			wasTriggeredBefore = false;
			((ShakeMeter)ma.findViewById(R.id.shake_meter_1)).updateValue(0.4f, ma.getGameState().gameSensitivity*ma.getPlayer().getSensitivity(),Color.RED,255);
//	    	((ShakeMeter)ma.findViewById(R.id.shake_meter_2)).updateValue(0, ma.getGameState().gameSensitivity*ma.getPlayer().getSensitivity());
		}
	}
	public void turnOffListener(){
		listenerOn = false;
	}
	public void turnOnListener(){
		listenerOn = true;
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}
