package com.mau.tdclient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class ShakeMeter extends View{
	Paint paint;
	float value;
	float max;
	float margins = 15;
	float x;
	float y;
	public ShakeMeter(Context context) {
		super(context);
		paint = new Paint();
		x = 340;
		y = 920;
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
		canvas.drawRect(getLeft(), getTop(), getWidth(), getHeight(), paint);
		//Draw inner background
		paint.setColor(Color.GREEN);
		canvas.drawRect(getLeft()+margins, getTop()+margins, getWidth()-margins, getHeight()-margins, paint);
		//Draw bar
		paint.setColor(Color.RED);
		canvas.drawRect(getLeft()+margins, getTop()+margins+((max-value)/max)*getHeight(), getWidth()-margins, getHeight()-margins, paint);
	}
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(400, 1000);
	}
}
