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
		textClock.setFormat24Hour("yyyy��MM��dd��\nEEEE      HH : mm");
	}

	@Override
	public void update(int type) {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void pause() {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void resume() {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void stop() {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void destroy() {
		// TODO �Զ����ɵķ������

	}

}
