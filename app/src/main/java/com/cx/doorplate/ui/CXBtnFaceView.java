package com.cx.doorplate.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.advertisement.activity.ActivityAppcodeo;
import com.cx.doorplate.activity.CXFaceRegister;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.ui.AppCodeView;

public class CXBtnFaceView extends AppCodeView {

	private Button button;

	public CXBtnFaceView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		View.inflate(context, R.layout.cx_ly_btn_face, this);
		button = (Button) findViewById(R.id.cx_btn_app_face);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (!TextUtils.isEmpty(nextPath)) {
					Intent intent = new Intent(context, ActivityAppcodeo.class);
					intent.putExtra("ThemePolicyPath", nextPath);
					context.startActivity(intent);
				} else {
					Intent intent = new Intent(context, CXFaceRegister.class);
					context.startActivity(intent);
				}
			}
		});
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
