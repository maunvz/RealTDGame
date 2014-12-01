package com.mau.tdclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Shader.TileMode;
import android.graphics.*;

public class ShakeMeter extends View{
	Paint paint;
	float value;
	float max;
	float screenMin = 0.2f;
	float screenMax = 1.0f;
	float screenPercent;
	float margins = 0;
	float ratio;
	int alpha = 255;
	int color;
	public ShakeMeter(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
	}
	public void updateValue(float value, float max,int color,int alpha){
		this.value = value;
		this.max=max;
		screenPercent = 0.5f*(value/max);
		this.color = color;
		this.alpha = alpha;
		invalidate();
	}
	@SuppressLint("DrawAllocation")
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		paint.setColor(Color.GREEN);
		int finalAlpha = alpha;
		if(alpha == -1){
			finalAlpha = (int)((screenPercent*1.5)*255.0f+90.0f); 
		}
//		paint.setShader(new LinearGradient(0, 0, screenPercent*getWidth(), 0, Color.GREEN, Color.TRANSPARENT, TileMode.MIRROR));
		setFramePaint(paint,1,getWidth(),getHeight(),screenPercent,color,finalAlpha);
		canvas.drawRect(margins,margins,getWidth(),getHeight(),paint);
		setFramePaint(paint,2,getWidth(),getHeight(),screenPercent,color,finalAlpha);
		canvas.drawRect(margins,margins,getWidth(),getHeight(),paint);
		setFramePaint(paint,3,getWidth(),getHeight(),screenPercent,color,finalAlpha);
		canvas.drawRect(margins,margins,getWidth(),getHeight(),paint);
		setFramePaint(paint,4,getWidth(),getHeight(),screenPercent,color,finalAlpha);
		canvas.drawRect(margins,margins,getWidth(),getHeight(),paint);
	}
	private void setFramePaint(Paint p, int side, float iw, float ih,float value,int color,int alpha){
        // paint, side of rect, image width, image height

        p.setShader(null);
//        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        float borderSize = value; //relative size of border
        //use the smaller image size to calculate the actual border size
        float bSize = (iw > ih)? ih * borderSize: ih * borderSize; 
        float g1x = 0;
        float g1y = 0;
        float g2x = 0;
        float g2y = 0;
        int c1 = 0, c2 = 0;

        if (side == 1){
            //left
            g1x = 0;
            g1y = ih/2;
            g2x = bSize;
            g2y = ih/2;
            c1 = Color.argb(alpha,Color.red(color),Color.green(color),Color.blue(color));
            c2 = Color.TRANSPARENT;

        }else if(side == 2){
            //top
            g1x = iw/2;
            g1y = 0;
            g2x = iw/2;
            g2y = bSize;
            c1 = Color.argb(alpha,Color.red(color),Color.green(color),Color.blue(color));
            c2 = Color.TRANSPARENT;


        }else if(side == 3){
            //right
            g1x = iw;
            g1y = ih/2;
            g2x = iw - bSize;
            g2y = ih/2;
            c1 = Color.argb(alpha,Color.red(color),Color.green(color),Color.blue(color));
            c2 = Color.TRANSPARENT;


        }else if(side == 4){
            //bottom
            g1x = iw/2;
            g1y = ih;
            g2x = iw/2;
            g2y = ih - bSize;
            c1 = Color.argb(alpha,Color.red(color),Color.green(color),Color.blue(color));
            c2 = Color.TRANSPARENT;
        }

        p.setShader(new LinearGradient(g1x, g1y, g2x, g2y, c1, c2, Shader.TileMode.CLAMP));

    }
}
