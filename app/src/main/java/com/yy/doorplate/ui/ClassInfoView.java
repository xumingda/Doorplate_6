package com.yy.doorplate.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.activity.CurriculumActivity;
import com.yy.doorplate.activity.ZhiriActivity;
import com.yy.doorplate.database.CurriculumInfoDatabase;
import com.yy.doorplate.database.OnDutyInfoDatabase;
import com.yy.doorplate.database.SectionInfoDatabase;
import com.yy.doorplate.model.CurriculumInfoModel;
import com.yy.doorplate.model.OnDutyInfoModel;
import com.yy.doorplate.model.SectionInfoModel;
import com.yy.doorplate.view.CustomTextView;

public class ClassInfoView extends AppCodeView implements OnClickListener {

	private LinearLayout ly_classnew_zhiri;
	private RelativeLayout ly_classnew_info_right, ly_classnew_attend;
	private TextView txt_classnew_bzr, txt_classnew_zrcm, txt_classnew_zr,
			txt_classnew_kq1, txt_classnew_kq2, txt_classnew_kq3,
			txt_classnew_kq4, txt_classnew_kq5, txt_classnew_kq6,
			txt_classnew_noke1, txt_classnew_noke2, txt_classnew_ke1,
			txt_classnew_ke2, txt_classnew_ke3, txt_classnew_ke4;
	private CustomTextView txt_classnew_bjmc, txt_classnew_bzrcm;

	private List<OnDutyInfoModel> dutyInfoModels = null;
	private List<CurriculumInfoModel> today, tomorrow;

	public ClassInfoView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
		updata();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		View.inflate(context, R.layout.ly_class_info, this);
		ly_classnew_zhiri = (LinearLayout) findViewById(R.id.ly_classnew_zhiri);
		ly_classnew_info_right = (RelativeLayout) findViewById(R.id.ly_classnew_info_right);
		ly_classnew_attend = (RelativeLayout) findViewById(R.id.ly_classnew_attend);
		txt_classnew_bzrcm = (CustomTextView) findViewById(R.id.txt_classnew_bzrcm);
		txt_classnew_bzr = (TextView) findViewById(R.id.txt_classnew_bzr);
		txt_classnew_zrcm = (TextView) findViewById(R.id.txt_classnew_zrcm);
		txt_classnew_zr = (TextView) findViewById(R.id.txt_classnew_zr);
		txt_classnew_kq1 = (TextView) findViewById(R.id.txt_classnew_kq1);
		txt_classnew_kq2 = (TextView) findViewById(R.id.txt_classnew_kq2);
		txt_classnew_kq3 = (TextView) findViewById(R.id.txt_classnew_kq3);
		txt_classnew_kq4 = (TextView) findViewById(R.id.txt_classnew_kq4);
		txt_classnew_kq5 = (TextView) findViewById(R.id.txt_classnew_kq5);
		txt_classnew_kq6 = (TextView) findViewById(R.id.txt_classnew_kq6);
		txt_classnew_noke1 = (TextView) findViewById(R.id.txt_classnew_noke1);
		txt_classnew_noke2 = (TextView) findViewById(R.id.txt_classnew_noke2);
		txt_classnew_ke1 = (TextView) findViewById(R.id.txt_classnew_ke1);
		txt_classnew_ke2 = (TextView) findViewById(R.id.txt_classnew_ke2);
		txt_classnew_ke3 = (TextView) findViewById(R.id.txt_classnew_ke3);
		txt_classnew_ke4 = (TextView) findViewById(R.id.txt_classnew_ke4);
		txt_classnew_bjmc = (CustomTextView) findViewById(R.id.txt_classnew_bjmc);

		ly_classnew_zhiri.setOnClickListener(this);
		ly_classnew_info_right.setOnClickListener(this);
		ly_classnew_attend.setOnClickListener(this);
	}

	private void updata() {
		if (application.classInfoModel != null) {
			txt_classnew_bjmc.setText(application.classInfoModel.bjmc);
			txt_classnew_bzrcm.setText(application.classInfoModel.fdyxm);
			txt_classnew_bzr.setVisibility(View.VISIBLE);
		} else {
			txt_classnew_bzr.setVisibility(View.INVISIBLE);
		}
		updata_zr();
		updata_attend();
		updata_curriculum();
	}

	private void updata_zr() {
		OnDutyInfoDatabase database = new OnDutyInfoDatabase();
		dutyInfoModels = database.query_by_date(MyApplication
				.getTime("yyyy-MM-dd"));
		if (dutyInfoModels != null && dutyInfoModels.size() > 0) {
			txt_classnew_zrcm.setVisibility(View.VISIBLE);
			txt_classnew_zr.setVisibility(View.VISIBLE);
			String s = "";
			for (int i = 0; i < dutyInfoModels.size(); i++) {
				if (!TextUtils.isEmpty(dutyInfoModels.get(i).xm)) {
					if (i == 0) {
						s = s + dutyInfoModels.get(i).xm;
					} else {
						s = s + "、" + dutyInfoModels.get(i).xm;
					}
				}
			}
			txt_classnew_zrcm.setText(s);
		} else {
			txt_classnew_zrcm.setText("暂无值日");
		}
	}

	private void updata_attend() {
		// TODO
	}

	private void updata_curriculum() {
		long noon = 14400000;
		CurriculumInfoDatabase curriculumInfoDatabase = new CurriculumInfoDatabase();
		SectionInfoDatabase sectionInfoDatabase = new SectionInfoDatabase();
		today = curriculumInfoDatabase.query_by_skrq(MyApplication
				.getTime("yyyy-MM-dd"));
		if (today != null && today.size() > 0) {
			String s1 = "上午 :", s2 = "下午 :";
			for (CurriculumInfoModel model : today) {
				String[] js = model.jc.split("-");
				SectionInfoModel sectionInfoModel = sectionInfoDatabase
						.query_by_jcdm(js[0]);
				if (sectionInfoModel != null) {
					if (Long.parseLong(sectionInfoModel.jcskkssj) <= noon) {
						s1 = s1 + " " + model.kcmc;
					} else {
						s2 = s2 + " " + model.kcmc;
					}
				}
			}
			txt_classnew_ke1.setText(s1);
			txt_classnew_ke2.setText(s2);
			txt_classnew_ke1.setVisibility(View.VISIBLE);
			txt_classnew_ke2.setVisibility(View.VISIBLE);
			txt_classnew_noke1.setVisibility(View.INVISIBLE);
		} else {
			txt_classnew_ke1.setVisibility(View.INVISIBLE);
			txt_classnew_ke2.setVisibility(View.INVISIBLE);
			txt_classnew_noke1.setVisibility(View.VISIBLE);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(System.currentTimeMillis()));
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date date = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		tomorrow = curriculumInfoDatabase.query_by_skrq(format.format(date));
		if (tomorrow != null && tomorrow.size() > 0) {
			String s1 = "上午 :", s2 = "下午 :";
			for (CurriculumInfoModel model : tomorrow) {
				String[] js = model.jc.split("-");
				SectionInfoModel sectionInfoModel = sectionInfoDatabase
						.query_by_jcdm(js[0]);
				if (sectionInfoModel != null) {
					if (Long.parseLong(sectionInfoModel.jcskkssj) <= noon) {
						s1 = s1 + " " + model.kcmc;
					} else {
						s2 = s2 + " " + model.kcmc;
					}
				}
			}
			txt_classnew_ke3.setText(s1);
			txt_classnew_ke4.setText(s2);
			txt_classnew_ke3.setVisibility(View.VISIBLE);
			txt_classnew_ke4.setVisibility(View.VISIBLE);
			txt_classnew_noke2.setVisibility(View.INVISIBLE);
		} else {
			txt_classnew_ke3.setVisibility(View.INVISIBLE);
			txt_classnew_ke4.setVisibility(View.INVISIBLE);
			txt_classnew_noke2.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void update(int type) {
		// TODO Auto-generated method stub
		if (type == APPCODE_UPDATA_EQU) {
			updata();
		} else if (type == APPCODE_UPDATA_CURRICULUM) {
			updata_curriculum();
			updata_attend();
		} else if (type == APPCODE_UPDATA_ZHIRI) {
			updata_zr();
		} else if (type == APPCODE_UPDATA_ATTEND) {
			updata_attend();
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

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.ly_classnew_zhiri: {
			Intent intent = new Intent(context, ZhiriActivity.class);
			context.startActivity(intent);
			break;
		}
		case R.id.ly_classnew_info_right: {
			CurriculumInfoDatabase database = new CurriculumInfoDatabase();
			List<CurriculumInfoModel> list = database.query_all();
			if (list != null && list.size() > 0) {
				Intent intent = new Intent(context, CurriculumActivity.class);
				context.startActivity(intent);
			} else {
				application.showToast(getResources()
						.getString(R.string.noclass));
			}
			break;
		}
		case R.id.ly_classnew_attend: {
			break;
		}
		}
	}

}
