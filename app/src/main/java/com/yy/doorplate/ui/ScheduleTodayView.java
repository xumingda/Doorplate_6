package com.yy.doorplate.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.activity.AttendInfoActivity;
import com.yy.doorplate.activity.CurriculumActivity;
import com.yy.doorplate.adapter.CurriculumTodayAdapter;

public class ScheduleTodayView extends AppCodeView {

	private Button btn_main_schedule_ly;
	private TextView txt_schedule_noclass;
	private ListView list_main_schedule;

	private CurriculumTodayAdapter curriculumTodayAdapter = null;

	public ScheduleTodayView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
	}

	@Override
	public void initView() {
		// TODO 自动生成的方法存根
		View.inflate(context, R.layout.ly_schedule_today, this);
		btn_main_schedule_ly = (Button) findViewById(R.id.btn_main_schedule_ly);
		txt_schedule_noclass = (TextView) findViewById(R.id.txt_schedule_noclass);
		list_main_schedule = (ListView) findViewById(R.id.list_main_schedule);

		btn_main_schedule_ly.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, CurriculumActivity.class);
				context.startActivity(intent);
			}
		});
		list_main_schedule.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(context, AttendInfoActivity.class);
				intent.putExtra("skjhid",
						application.curriculumInfos_today.get(arg2).id);
				context.startActivity(intent);
			}
		});

		updata_today_curriculum();
	}

	private void updata_today_curriculum() {
		if (application.curriculumInfos_today != null
				&& application.curriculumInfos_today.size() > 0) {
			curriculumTodayAdapter = new CurriculumTodayAdapter(application,
					application.curriculumInfos_today);
			list_main_schedule.setAdapter(curriculumTodayAdapter);
			txt_schedule_noclass.setVisibility(View.INVISIBLE);
		} else {
			list_main_schedule.setAdapter(null);
			txt_schedule_noclass.setVisibility(View.VISIBLE);
			txt_schedule_noclass.setText(getResources().getString(
					R.string.noclass));
		}
	}

	@Override
	public void update(int type) {
		// TODO 自动生成的方法存根
		if (type == APPCODE_UPDATA_CURRICULUM || type == APPCODE_UPDATA_EQU) {
			updata_today_curriculum();
		}
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
