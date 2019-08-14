package com.yy.doorplate.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Button;

import com.yy.doorplate.R;

public class FloatView extends Button {

	private int lastX;
	private int lastY;
	private long lastTime;
	private int mStartX;
	private int mStartY;
	private OnClickListener mClickListener;

	private WindowManager windowManager;
	private WindowManager.LayoutParams windowManagerParams;

	public FloatView(Context context, WindowManager windowManager,
			WindowManager.LayoutParams windowManagerParams) {
		super(context);
		this.windowManager = windowManager;
		this.windowManagerParams = windowManagerParams;
	}

	public FloatView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FloatView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void setOnClickListener(OnClickListener onClickListener) {
		this.mClickListener = onClickListener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int) event.getX();
			lastY = (int) event.getY();
			mStartX = (int) event.getRawX();
			mStartY = (int) event.getRawY();
			lastTime = System.currentTimeMillis();
			this.setBackgroundResource(R.drawable.float_back);
			break;
		case MotionEvent.ACTION_MOVE:
			windowManagerParams.x = (int) event.getRawX() - lastX;
			windowManagerParams.y = (int) event.getRawY() - lastY;
			windowManager.updateViewLayout(this, windowManagerParams);
			break;
		case MotionEvent.ACTION_UP:
			if (mClickListener != null && (int) event.getRawX() - mStartX < 5
					&& (int) event.getRawY() - mStartY < 5
					&& System.currentTimeMillis() - lastTime < 500) {
				mClickListener.onClick(this);
			}
			this.setBackgroundResource(R.drawable.float_back);
			break;
		}
		return true;
	}
}
