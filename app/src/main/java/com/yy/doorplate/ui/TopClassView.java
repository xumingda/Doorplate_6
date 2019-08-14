package com.yy.doorplate.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;

public class TopClassView extends AppCodeView {

	private Activity activity;

	public TopClassView(Context context, MyApplication application,
			Activity activity, String nextPath) {
		super(context, application, nextPath);
		this.activity = activity;
		initView();
	}

	@Override
	public void initView() {
		View.inflate(context, R.layout.ly_top_class, this);
		Button button = (Button) findViewById(R.id.btn_classnew_back);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				activity.finish();
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
