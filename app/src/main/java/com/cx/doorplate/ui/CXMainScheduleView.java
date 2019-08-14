package com.cx.doorplate.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cx.doorplate.activity.CXAttendActivity;
import com.cx.doorplate.activity.CXCurriculumActivity;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.database.CurriculumInfoDatabase;
import com.yy.doorplate.database.SectionInfoDatabase;
import com.yy.doorplate.model.CurriculumInfoModel;
import com.yy.doorplate.model.SectionInfoModel;
import com.yy.doorplate.ui.AppCodeView;
import com.yy.doorplate.view.CustomTextView;

public class CXMainScheduleView extends AppCodeView implements OnClickListener {

	private RelativeLayout ly_cxmain_schedule;
	private LinearLayout ly_cxmain_schedule1, ly_cxmain_schedule2,
			ly_cxmain_schedule3;
	private Button btn_main_schedule;
	private TextView txt_main_kc, txt_main_time, cx_txt_schedule_next1,
			cx_txt_schedule_next2;
	private CustomTextView txt_main_teacher;

	public CXMainScheduleView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
	}

	@Override
	public void initView() {
		View.inflate(context, R.layout.cx_ly_main_schedule, this);
		ly_cxmain_schedule = (RelativeLayout) findViewById(R.id.ly_cxmain_schedule);
		ly_cxmain_schedule1 = (LinearLayout) findViewById(R.id.ly_cxmain_schedule1);
		ly_cxmain_schedule2 = (LinearLayout) findViewById(R.id.ly_cxmain_schedule2);
		ly_cxmain_schedule3 = (LinearLayout) findViewById(R.id.ly_cxmain_schedule3);
		btn_main_schedule = (Button) findViewById(R.id.btn_main_schedule);
		txt_main_kc = (TextView) findViewById(R.id.txt_main_kc);
		txt_main_time = (TextView) findViewById(R.id.txt_main_time);
		cx_txt_schedule_next1 = (TextView) findViewById(R.id.cx_txt_schedule_next1);
		cx_txt_schedule_next2 = (TextView) findViewById(R.id.cx_txt_schedule_next2);
		txt_main_teacher = (CustomTextView) findViewById(R.id.txt_main_teacher);

		ly_cxmain_schedule1.setOnClickListener(this);
		ly_cxmain_schedule2.setOnClickListener(this);
		ly_cxmain_schedule3.setOnClickListener(this);
		btn_main_schedule.setOnClickListener(this);

		updata_now_curriculum();
	}

	// 更新当前课程信息
	private void updata_now_curriculum() {
		if (application.curriculumInfos_today != null
				&& application.curriculumInfos_today.size() > 0) {
			if (application.curriculumInfoModel_now != null) {
				// txt_main_cc.setText("当前课程");
				// txt_main_nonschedule.setVisibility(View.INVISIBLE);
				// ly_main_schedule.setVisibility(View.VISIBLE);
				ly_cxmain_schedule
						.setBackgroundResource(R.drawable.cx_ly_schedule);
				ly_cxmain_schedule1.setVisibility(View.VISIBLE);
				ly_cxmain_schedule2.setVisibility(View.VISIBLE);
				ly_cxmain_schedule3.setVisibility(View.VISIBLE);
				txt_main_kc.setText(application.curriculumInfoModel_now.kcmc);
				txt_main_time
						.setText(application.curriculumInfoModel_now.skrq
								+ "\n"
								+ MyApplication
										.jcToTime(application.curriculumInfoModel_now.jc));
				txt_main_teacher
						.setText(application.curriculumInfoModel_now.skjsxm);

				application.curriculumInfoModel_next = null;
				for (int i = 0; i < application.curriculumInfos_today.size(); i++) {
					if (application.curriculumInfos_today.get(i).id
							.equals(application.curriculumInfoModel_now.id)
							&& application.curriculumInfos_today.size() > (i + 1)) {
						application.curriculumInfoModel_next = application.curriculumInfos_today
								.get(i + 1);
						break;
					}
				}
				if (application.curriculumInfoModel_next != null) {
					// txt_main_nextschedule.setVisibility(View.VISIBLE);
					// txt_main_nextschedule.setText("下一节  : "
					// + application.curriculumInfoModel_next.kcmc);
					cx_txt_schedule_next1.setVisibility(View.VISIBLE);
					cx_txt_schedule_next2.setVisibility(View.VISIBLE);
					cx_txt_schedule_next2
							.setText("下一节  : "+application.curriculumInfoModel_next.kcmc);
				} else {
					// txt_main_nextschedule.setVisibility(View.INVISIBLE);
					cx_txt_schedule_next1.setVisibility(View.INVISIBLE);
					cx_txt_schedule_next2.setVisibility(View.INVISIBLE);
				}
			} else {
				application.curriculumInfoModel_next = null;
				try {
					application.curriculumInfoModel_next = findNextCurriculum();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (application.curriculumInfoModel_next != null) {
					// txt_main_cc.setText("下一节课程");
					// txt_main_nextschedule.setVisibility(View.INVISIBLE);
					// txt_main_nonschedule.setVisibility(View.INVISIBLE);
					// ly_main_schedule.setVisibility(View.VISIBLE);
					ly_cxmain_schedule
							.setBackgroundResource(R.drawable.cx_ly_schedule_next);
					ly_cxmain_schedule1.setVisibility(View.VISIBLE);
					ly_cxmain_schedule2.setVisibility(View.VISIBLE);
					ly_cxmain_schedule3.setVisibility(View.VISIBLE);
					txt_main_kc
							.setText(application.curriculumInfoModel_next.kcmc);
					txt_main_time
							.setText(application.curriculumInfoModel_next.skrq
									+ "\n"
									+ MyApplication
											.jcToTime(application.curriculumInfoModel_next.jc));
					txt_main_teacher
							.setText(application.curriculumInfoModel_next.skjsxm);
				} else {
					// txt_main_cc.setText("当前课程");
					// ly_main_schedule.setVisibility(View.INVISIBLE);
					// txt_main_nonschedule.setVisibility(View.VISIBLE);
					// txt_main_nextschedule.setVisibility(View.INVISIBLE);
					ly_cxmain_schedule
							.setBackgroundResource(R.drawable.cx_ly_schedule);
					ly_cxmain_schedule1.setVisibility(View.INVISIBLE);
					ly_cxmain_schedule2.setVisibility(View.INVISIBLE);
					ly_cxmain_schedule3.setVisibility(View.INVISIBLE);
				}
			}
		} else {
			application.curriculumInfoModel_next = null;
			try {
				application.curriculumInfoModel_next = findNextCurriculum();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (application.curriculumInfoModel_next != null) {
				// txt_main_cc.setText("下一节课程");
				// txt_main_nextschedule.setVisibility(View.INVISIBLE);
				// txt_main_nonschedule.setVisibility(View.INVISIBLE);
				// ly_main_schedule.setVisibility(View.VISIBLE);
				ly_cxmain_schedule
						.setBackgroundResource(R.drawable.cx_ly_schedule_next);
				ly_cxmain_schedule1.setVisibility(View.VISIBLE);
				ly_cxmain_schedule2.setVisibility(View.VISIBLE);
				ly_cxmain_schedule3.setVisibility(View.VISIBLE);
				txt_main_kc.setText(application.curriculumInfoModel_next.kcmc);
				txt_main_time
						.setText(application.curriculumInfoModel_next.skrq
								+ "\n"
								+ MyApplication
										.jcToTime(application.curriculumInfoModel_next.jc));
				txt_main_teacher
						.setText(application.curriculumInfoModel_next.skjsxm);
			} else {
				// txt_main_cc.setText("当前课程");
				// ly_main_schedule.setVisibility(View.INVISIBLE);
				// txt_main_nonschedule.setVisibility(View.VISIBLE);
				// txt_main_nextschedule.setVisibility(View.INVISIBLE);
				ly_cxmain_schedule
						.setBackgroundResource(R.drawable.cx_ly_schedule);
				ly_cxmain_schedule1.setVisibility(View.INVISIBLE);
				ly_cxmain_schedule2.setVisibility(View.INVISIBLE);
				ly_cxmain_schedule3.setVisibility(View.INVISIBLE);
			}
		}
	}

	private CurriculumInfoModel findNextCurriculum() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
		if (application.curriculumInfos_today != null) {
			if (application.curriculumInfoModel_now == null) {
				SectionInfoDatabase database = new SectionInfoDatabase();
				String HHmmss = formatter.format(System.currentTimeMillis());
				Date time = formatter.parse(HHmmss);
				for (CurriculumInfoModel model : application.curriculumInfos_today) {
					String[] jcs = model.jc.split("-");
					SectionInfoModel sectionInfoModel = database
							.query_by_jcdm(jcs[0]);
					if (sectionInfoModel != null
							&& time.getTime() < Long
									.parseLong(sectionInfoModel.jcskkssj)) {
						return model;
					}
				}
			}
		}
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		CurriculumInfoDatabase database = new CurriculumInfoDatabase();
		List<CurriculumInfoModel> list = database.query_all();
		if (list != null && list.size() > 0) {
			for (CurriculumInfoModel model : list) {
				if (formatter.parse(model.skrq).getTime() > System
						.currentTimeMillis()) {
					return model;
				}
			}
		}
		return null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ly_cxmain_schedule1:
		case R.id.ly_cxmain_schedule2:
		case R.id.ly_cxmain_schedule3: {
			Intent intent = new Intent(context, CXAttendActivity.class);
			if (application.curriculumInfoModel_now != null) {
				intent.putExtra("skjhid",
						application.curriculumInfoModel_now.id);
			} else if (application.curriculumInfoModel_next != null) {
				intent.putExtra("skjhid",
						application.curriculumInfoModel_next.id);
			}
			context.startActivity(intent);
			break;
		}
		case R.id.btn_main_schedule: {
			Intent intent = new Intent(context, CXCurriculumActivity.class);
			context.startActivity(intent);
			break;
		}
		}
	}

	@Override
	public void update(int type) {
		// TODO 自动生成的方法存根
		if (type == APPCODE_UPDATA_CURRICULUM || type == APPCODE_UPDATA_EQU) {
			updata_now_curriculum();
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
