package com.cx.doorplate.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.advertisement.activity.ActivityAppcodeo;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.ui.AppCodeView;

public class CXBtnBackView extends AppCodeView implements OnClickListener {

	private Activity activity;

	private Button button;

	public CXBtnBackView(Context context, MyApplication application,
			Activity activity, String nextPath) {
		super(context, application, nextPath);
		this.activity = activity;
		initView();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		View.inflate(context, R.layout.cx_ly_btn_back, this);
		button = (Button) findViewById(R.id.btn_back);
		button.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_back: {
			if (!TextUtils.isEmpty(nextPath)) {
				Intent intent = new Intent(context, ActivityAppcodeo.class);
				intent.putExtra("ThemePolicyPath", nextPath);
				context.startActivity(intent);
			} else {
				activity.finish();
			}
			break;
		}
		}
	}

	@Override
	public void update(int type) {
		// TODO Auto-generated method stub
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
