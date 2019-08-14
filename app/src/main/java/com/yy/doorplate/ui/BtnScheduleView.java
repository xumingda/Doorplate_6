package com.yy.doorplate.ui;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.advertisement.activity.ActivityAppcodeo;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.activity.CurriculumActivity;
import com.yy.doorplate.database.CurriculumInfoDatabase;
import com.yy.doorplate.model.CurriculumInfoModel;

public class BtnScheduleView extends AppCodeView {

	private Button button;

	public BtnScheduleView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		View.inflate(context, R.layout.ly_btn_schedule, this);
		button = (Button) findViewById(R.id.btn_main_schedule);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (!TextUtils.isEmpty(nextPath)) {
					Intent intent = new Intent(context, ActivityAppcodeo.class);
					intent.putExtra("ThemePolicyPath", nextPath);
					context.startActivity(intent);
				} else {
					CurriculumInfoDatabase database = new CurriculumInfoDatabase();
					List<CurriculumInfoModel> list = database.query_all();
					if (list != null && list.size() > 0) {
						Intent intent = new Intent(context,
								CurriculumActivity.class);
						context.startActivity(intent);
					} else {
						application.showToast(getResources().getString(
								R.string.noclass));
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
