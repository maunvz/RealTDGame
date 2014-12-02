package com.mau.tdclient;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;


/**
 * an animation for resizing the view.
 */
public class ResizeAnimation extends Animation {
    private View mView;
    private float mToWeight;
    private float mFromWeight;

    public ResizeAnimation(View v, float fromWeight,  float toWeight) {
    	mToWeight = toWeight;
    	mFromWeight = fromWeight;
        mView = v;
        setDuration(500);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float weight =
                (mToWeight - mFromWeight) * interpolatedTime + mFromWeight;
        LayoutParams p = (LayoutParams) mView.getLayoutParams();
        p.weight = weight;
        mView.requestLayout();
    }
}