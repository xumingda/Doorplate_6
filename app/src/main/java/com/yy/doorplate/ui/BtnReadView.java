package com.yy.doorplate.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.advertisement.activity.ActivityAppcodeo;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.activity.WebviewActivity;

public class BtnReadView extends AppCodeView {

	private Button button;

	public BtnReadView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		View.inflate(context, R.layout.ly_btn_read, this);
		button = (Button) findViewById(R.id.btn_main_read);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (!TextUtils.isEmpty(nextPath)) {
					Intent intent = new Intent(context, ActivityAppcodeo.class);
					intent.putExtra("ThemePolicyPath", nextPath);
					context.startActivity(intent);
				} else {
					try {
						application.openApp(context, MyApplication.READ);
					} catch (Exception e) {
						e.printStackTrace();
						Intent intent = new Intent(context,
								WebviewActivity.class);
						context.startActivity(intent);
					}
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
