package com.cx.doorplate.ui;

import android.content.Context;
import android.view.View;
import android.widget.TextClock;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.ui.AppCodeView;

public class CXTimeView extends AppCodeView {

	private TextClock textClock;

	public CXTimeView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
	}

	@Override
	public void initView() {
		View.inflate(context, R.layout.cx_ly_time, this);
		textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("yyyy年MM月dd日\nEEEE      HH : mm");
	}

	@Override
	public void update(int type) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void pause() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void resume() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void stop() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void destroy() {
		// TODO 自动生成的方法存根

	}

}
