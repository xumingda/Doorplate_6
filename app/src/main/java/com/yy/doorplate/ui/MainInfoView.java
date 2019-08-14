package com.yy.doorplate.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.activity.AttendInfoActivity;
import com.yy.doorplate.activity.ClassInfoActivity;
import com.yy.doorplate.activity.NoticeDetailsActivity;
import com.yy.doorplate.database.CurriculumInfoDatabase;
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.database.SectionInfoDatabase;
import com.yy.doorplate.model.CurriculumInfoModel;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.model.SectionInfoModel;
import com.yy.doorplate.view.CustomTextView;

public class MainInfoView extends AppCodeView implements OnClickListener {

	private LinearLayout ly_main_schedule, ly_main_class;
	private TextView txt_main_kc, txt_main_time, txt_main_classnew,
			txt_main_nonschedule, txt_main_noclassnew, txt_main_nextschedule,
			txt_main_cc;
	private CustomTextView txt_main_class, txt_main_bj, txt_main_teacher;

	private NoticeInfoModel noticeInfoModel = null;

	public MainInfoView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
		updata_equ_info();
		updata_new_notice();
		updata_now_curriculum();
	}

	@Override
	public void initView() {
		View.inflate(context, R.layout.ly_main_info, this);
		ly_main_schedule = (LinearLayout) findViewById(R.id.ly_main_schedule);
		ly_main_class = (LinearLayout) findViewById(R.id.ly_main_class);
		txt_main_class = (CustomTextView) findViewById(R.id.txt_main_class);
		txt_main_bj = (CustomTextView) findViewById(R.id.txt_main_bj);
		txt_main_kc = (TextView) findViewById(R.id.txt_main_kc);
		txt_main_time = (TextView) findViewById(R.id.txt_main_time);
		txt_main_teacher = (CustomTextView) findViewById(R.id.txt_main_teacher);
		txt_main_classnew = (TextView) findViewById(R.id.txt_main_classnew);
		txt_main_nonschedule = (TextView) findViewById(R.id.txt_main_nonschedule);
		txt_main_noclassnew = (TextView) findViewById(R.id.txt_main_noclassnew);
		txt_main_nextschedule = (TextView) findViewById(R.id.txt_main_nextschedule);
		txt_main_cc = (TextView) findViewById(R.id.txt_main_cc);

		txt_main_classnew.setOnClickListener(this);
		ly_main_schedule.setOnClickListener(this);
		ly_main_class.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.txt_main_classnew: {
			Intent intent = new Intent(context, NoticeDetailsActivity.class);
			intent.putExtra("top", "班级通知");
			intent.putExtra("from", "PjMainActivity");
			intent.putExtra("type", 3);
			intent.putExtra("id", noticeInfoModel.id);
			context.startActivity(intent);
			break;
		}
		case R.id.ly_main_class: {
			Intent intent = new Intent(context, ClassInfoActivity.class);
			context.startActivity(intent);
			break;
		}
		case R.id.ly_main_schedule: {
			Intent intent = new Intent(context, AttendInfoActivity.class);
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
		}
	}

	private void updata_equ_info() {
		if (application.classInfoModel != null) {
			txt_main_class.setText(application.classInfoModel.bjmc);
		}
		if (application.equInfoModel != null) {
			txt_main_bj.setText(application.equInfoModel.jssysmc);
		}
	}

	private void updata_now_curriculum() {
		if (application.curriculumInfos_today != null
				&& application.curriculumInfos_today.size() > 0) {
			if (application.curriculumInfoModel_now != null) {
				txt_main_cc.setText("当前课程");
				txt_main_nonschedule.setVisibility(View.INVISIBLE);
				ly_main_schedule.setVisibility(View.VISIBLE);
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
					txt_main_nextschedule.setVisibility(View.VISIBLE);
					txt_main_nextschedule.setText("下一节  : "
							+ application.curriculumInfoModel_next.kcmc);
				} else {
					txt_main_nextschedule.setVisibility(View.INVISIBLE);
				}
			} else {
				application.curriculumInfoModel_next = null;
				try {
					application.curriculumInfoModel_next = findNextCurriculum();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (application.curriculumInfoModel_next != null) {
					txt_main_cc.setText("下一节课程");
					txt_main_nextschedule.setVisibility(View.INVISIBLE);
					txt_main_nonschedule.setVisibility(View.INVISIBLE);
					ly_main_schedule.setVisibility(View.VISIBLE);
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
					txt_main_cc.setText("当前课程");
					ly_main_schedule.setVisibility(View.INVISIBLE);
					txt_main_nonschedule.setVisibility(View.VISIBLE);
					txt_main_nextschedule.setVisibility(View.INVISIBLE);
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
				txt_main_cc.setText("下一节课程");
				txt_main_nextschedule.setVisibility(View.INVISIBLE);
				txt_main_nonschedule.setVisibility(View.INVISIBLE);
				ly_main_schedule.setVisibility(View.VISIBLE);
				txt_main_kc.setText(application.curriculumInfoModel_next.kcmc);
				txt_main_time
						.setText(application.curriculumInfoModel_next.skrq
								+ "\n"
								+ MyApplication
										.jcToTime(application.curriculumInfoModel_next.jc));
				txt_main_teacher
						.setText(application.curriculumInfoModel_next.skjsxm);
			} else {
				txt_main_cc.setText("当前课程");
				ly_main_schedule.setVisibility(View.INVISIBLE);
				txt_main_nonschedule.setVisibility(View.VISIBLE);
				txt_main_nextschedule.setVisibility(View.INVISIBLE);
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

	private void updata_new_notice() {
		NoticeInfoDatabase database = new NoticeInfoDatabase();
		List<NoticeInfoModel> list = database.query_by_lmdm("bpbjtz", 0, 1);
		if (list != null && list.size() == 1) {
			noticeInfoModel = list.get(0);
			txt_main_classnew.setText("");
			if (!TextUtils.isEmpty(noticeInfoModel.xxnr)) {
				txt_main_classnew.setVisibility(View.VISIBLE);
				txt_main_noclassnew.setVisibility(View.INVISIBLE);
				Document document = Jsoup.parse(noticeInfoModel.xxnr);
				Elements p = document.select("p");
				if (p != null && p.size() > 0) {
					String new_rl = "";
					for (Element element : p) {
						if (TextUtils.isEmpty(new_rl)) {
							new_rl = element.text();
						} else {
							new_rl = new_rl + "\n" + element.text();
						}
						if (!TextUtils.isEmpty(new_rl) && new_rl.length() > 100) {
							break;
						}
					}
					txt_main_classnew.setText(new_rl);
				}
			} else {
				txt_main_classnew.setVisibility(View.INVISIBLE);
				txt_main_noclassnew.setVisibility(View.VISIBLE);
			}
		} else {
			txt_main_classnew.setVisibility(View.INVISIBLE);
			txt_main_noclassnew.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void update(int type) {
		// TODO Auto-generated method stub
		if (type == APPCODE_UPDATA_EQU) {
			updata_equ_info();
			updata_new_notice();
			updata_now_curriculum();
		} else if (type == APPCODE_UPDATA_NOTIC) {
			updata_new_notice();
		} else if (type == APPCODE_UPDATA_CURRICULUM) {
			updata_now_curriculum();
		}
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
