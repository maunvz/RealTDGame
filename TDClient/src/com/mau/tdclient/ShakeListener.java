package com.mau.tdclient;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class ShakeListener implements SensorEventListener{
	float x,y,z;
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
	
			    float speed = Math.abs(x+y+z-last_x-last_y-last_z) / diffTime * 10000;
	
			    if (speed > ma.getGameState().gameSensitivity*ma.getPlayer().getSensitivity()) {
			    	wasTriggeredBefore = true;
			    	timesOverThreshold ++;
			    	if(timesOverThreshold > 1){
			    		ma.youDie();
			    		timesOverThreshold = 0;
			    	}
			    }
			    else{
			    	if(wasTriggeredBefore){
			    		wasTriggeredBefore = false;
			    		timesOverThreshold = 0;
			    	}
			    }
			    last_x = x;
			    last_y = y;
			    last_z = z;
			    if(ma.screenNo==MainActivity.GAME_SCREEN){
			    	float max = ma.getGameState().gameSensitivity*ma.getPlayer().getSensitivity();
			    	if(ma.getPlayer().stronger)max*=1.5;
			    	((ShakeMeter)ma.findViewById(R.id.shake_meter_1)).updateValue(speed, max);
			    	((ShakeMeter)ma.findViewById(R.id.shake_meter_2)).updateValue(speed, max);
			    }
			}
		}
		else{
			timesOverThreshold = 0;
			wasTriggeredBefore = false;
			((ShakeMeter)ma.findViewById(R.id.shake_meter_1)).updateValue(0, ma.getGameState().gameSensitivity*ma.getPlayer().getSensitivity());
	    	((ShakeMeter)ma.findViewById(R.id.shake_meter_2)).updateValue(0, ma.getGameState().gameSensitivity*ma.getPlayer().getSensitivity());
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
