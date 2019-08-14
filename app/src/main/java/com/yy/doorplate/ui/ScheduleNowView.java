package com.yy.doorplate.ui;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.activity.AttendInfoActivity;
import com.yy.doorplate.activity.ClassInfoActivity;
import com.yy.doorplate.activity.CurriculumActivity;
import com.yy.doorplate.database.AttendInfoDatabase;
import com.yy.doorplate.model.AttendInfoModel;
import com.yy.doorplate.view.CustomTextView;

public class ScheduleNowView extends AppCodeView {

	private RelativeLayout ly_main_class;
	private Button btn_main_class_ly;
	private CustomTextView txt_main_class, txt_main_banji, txt_classroom;
	private TextView txt_main_time, txt_main_teacher, txt_main_solid_arrive,
			txt_main_arrive, txt_main_1, txt_main_2, txt_main_3, txt_main_4,
			txt_main_5, txt_main_6, txt_class_noclass;

	private int arrive = 0;

	public ScheduleNowView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
	}

	@Override
	public void initView() {
		// TODO 自动生成的方法存根
		View.inflate(context, R.layout.ly_schedule_now, this);
		ly_main_class = (RelativeLayout) findViewById(R.id.ly_main_class);
		btn_main_class_ly = (Button) findViewById(R.id.btn_main_class_ly);
		txt_main_class = (CustomTextView) findViewById(R.id.txt_main_class);
		txt_main_banji = (CustomTextView) findViewById(R.id.txt_main_banji);
		txt_classroom = (CustomTextView) findViewById(R.id.txt_classroom);
		txt_main_time = (TextView) findViewById(R.id.txt_main_time);
		txt_main_teacher = (TextView) findViewById(R.id.txt_main_teacher);
		txt_main_solid_arrive = (TextView) findViewById(R.id.txt_main_solid_arrive);
		txt_main_arrive = (TextView) findViewById(R.id.txt_main_arrive);
		txt_main_1 = (TextView) findViewById(R.id.txt_main_1);
		txt_main_2 = (TextView) findViewById(R.id.txt_main_2);
		txt_main_3 = (TextView) findViewById(R.id.txt_main_3);
		txt_main_4 = (TextView) findViewById(R.id.txt_main_4);
		txt_main_5 = (TextView) findViewById(R.id.txt_main_5);
		txt_main_6 = (TextView) findViewById(R.id.txt_main_6);
		txt_class_noclass = (TextView) findViewById(R.id.txt_class_noclass);

		ly_main_class.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (application.curriculumInfoModel_now != null) {
					Intent intent = new Intent(context,
							AttendInfoActivity.class);
					intent.putExtra("skjhid",
							application.curriculumInfoModel_now.id);
					context.startActivity(intent);
				}
			}
		});
		btn_main_class_ly.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ClassInfoActivity.class);
				context.startActivity(intent);
			}
		});

		if (application.equInfoModel != null
				&& application.classRoomInfoModel != null) {
			txt_classroom.setText(application.equInfoModel.jssysmc);
		}
		updata_now_curriculum();
		updata_attend();
	}

	private void updata_attend() {
		if (application.curriculumInfos_today != null
				&& application.curriculumInfos_today.size() > 0
				&& application.curriculumInfoModel_now != null) {
			AttendInfoDatabase database = new AttendInfoDatabase();
			List<AttendInfoModel> list = database
					.query_by_skjhid(application.curriculumInfoModel_now.id);
			if (list != null && list.size() > 0) {
				arrive = 0;
				for (AttendInfoModel model : list) {
					if (model.kqzt.equals("0") || model.kqzt.equals("3")) {
						arrive++;
					}
				}
				txt_main_solid_arrive.setText("" + list.size());
				txt_main_arrive.setText("" + arrive);
			} else {
				txt_main_solid_arrive.setText("- -");
				txt_main_arrive.setText("- -");
			}
		}
	}

	private void updata_now_curriculum() {
		if (application.curriculumInfos_today != null
				&& application.curriculumInfos_today.size() > 0) {
			if (application.curriculumInfoModel_now != null) {
				txt_class_noclass.setVisibility(View.INVISIBLE);
				txt_main_1.setVisibility(View.VISIBLE);
				txt_main_2.setVisibility(View.VISIBLE);
				txt_main_3.setVisibility(View.VISIBLE);
				txt_main_4.setVisibility(View.VISIBLE);
				txt_main_5.setVisibility(View.VISIBLE);
				txt_main_6.setVisibility(View.VISIBLE);
				txt_main_class.setVisibility(View.VISIBLE);
				txt_main_banji.setVisibility(View.VISIBLE);
				txt_main_time.setVisibility(View.VISIBLE);
				txt_main_teacher.setVisibility(View.VISIBLE);
				txt_main_solid_arrive.setVisibility(View.VISIBLE);
				txt_main_arrive.setVisibility(View.VISIBLE);
				txt_main_class
						.setText(application.curriculumInfoModel_now.kcmc);
				txt_main_banji
						.setText(application.curriculumInfoModel_now.skbjmc);
				txt_main_time.setText(MyApplication
						.jcToTime(application.curriculumInfoModel_now.jc));
				txt_main_teacher
						.setText(application.curriculumInfoModel_now.skjsxm);
			} else {
				txt_main_1.setVisibility(View.INVISIBLE);
				txt_main_2.setVisibility(View.INVISIBLE);
				txt_main_3.setVisibility(View.INVISIBLE);
				txt_main_4.setVisibility(View.INVISIBLE);
				txt_main_5.setVisibility(View.INVISIBLE);
				txt_main_6.setVisibility(View.INVISIBLE);
				txt_main_class.setVisibility(View.INVISIBLE);
				txt_main_banji.setVisibility(View.INVISIBLE);
				txt_main_time.setVisibility(View.INVISIBLE);
				txt_main_teacher.setVisibility(View.INVISIBLE);
				txt_main_solid_arrive.setVisibility(View.INVISIBLE);
				txt_main_arrive.setVisibility(View.INVISIBLE);
				txt_class_noclass.setVisibility(View.VISIBLE);
				txt_class_noclass.setText(getResources().getString(
						R.string.noclass_));
			}
		} else {
			txt_main_1.setVisibility(View.INVISIBLE);
			txt_main_2.setVisibility(View.INVISIBLE);
			txt_main_3.setVisibility(View.INVISIBLE);
			txt_main_4.setVisibility(View.INVISIBLE);
			txt_main_5.setVisibility(View.INVISIBLE);
			txt_main_6.setVisibility(View.INVISIBLE);
			txt_main_class.setVisibility(View.INVISIBLE);
			txt_main_banji.setVisibility(View.INVISIBLE);
			txt_main_time.setVisibility(View.INVISIBLE);
			txt_main_teacher.setVisibility(View.INVISIBLE);
			txt_main_solid_arrive.setVisibility(View.INVISIBLE);
			txt_main_arrive.setVisibility(View.INVISIBLE);
			txt_class_noclass.setVisibility(View.VISIBLE);
			txt_class_noclass.setText(getResources()
					.getString(R.string.noclass));
		}
	}

	@Override
	public void update(int type) {
		// TODO 自动生成的方法存根
		if (type == APPCODE_UPDATA_CURRICULUM || type == APPCODE_UPDATA_EQU) {
			updata_now_curriculum();
			updata_attend();
		} else if (type == APPCODE_UPDATA_ATTEND) {
			updata_attend();
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
