package com.mau.tdclient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ShakeMeter extends View{
	Paint paint;
	float value;
	float max;
	float margins = 15;
	public ShakeMeter(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
	}
	public void updateValue(float value, float max){
		this.value = value;
		this.max=max;
		invalidate();
	}
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		//Draw outer background
		paint.setColor(Color.BLACK);
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
		//Draw inner background
		paint.setColor(Color.GREEN);
		canvas.drawRect(margins, margins, getWidth()-margins, getHeight()-margins, paint);
		//Draw bar
		paint.setColor(Color.RED);
		canvas.drawRect(margins, margins+((max-value)/max)*getHeight(), getWidth()-margins, getHeight()-margins, paint);
	}
}
