package com.mau.tdclient;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class ShakeListener implements SensorEventListener{
	float x,y,z;
	float last_x, last_y, last_z;
	long lastUpdate;
	int threshold = 650;
	MainActivity ma;
	
	public ShakeListener(MainActivity ma, int threshold){
		this.ma=ma;
		this.threshold=threshold;
	}
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

		    if (speed > threshold) {
		    	ma.youDie();
		    }
		    last_x = x;
		    last_y = y;
		    last_z = z;
		    ((ShakeMeter)ma.findViewById(R.id.shake_meter)).updateValue(speed, threshold);
		}
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}