package com.yy.doorplate.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewpager extends ViewPager {

	private int preX = 0;

	public CustomViewpager(Context context) {
		super(context);
	}

	public CustomViewpager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			preX = (int) event.getX();
		} else {
			if (Math.abs((int) event.getX() - preX) > 10) {
				return true;
			} else {
				preX = (int) event.getX();
			}
		}
		return super.onInterceptTouchEvent(event);
	}
}
