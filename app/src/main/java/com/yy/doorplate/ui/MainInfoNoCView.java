package com.yy.doorplate.ui;

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
import com.yy.doorplate.activity.ClassInfoActivity;
import com.yy.doorplate.activity.NoticeDetailsActivity;
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.view.CustomTextView;

public class MainInfoNoCView extends AppCodeView implements OnClickListener {

	private LinearLayout ly_main_class;
	private TextView txt_main_classnew, txt_main_xxxw, txt_main_nonschedule,
			txt_main_noclassnew;
	private CustomTextView txt_main_class, txt_main_bj;

	private NoticeInfoModel noticeInfoModel = null, xxxw = null;

	public MainInfoNoCView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
		updata_equ_info();
		updata_new_notice();
	}

	@Override
	public void initView() {
		View.inflate(context, R.layout.ly_main_info_noc, this);
		ly_main_class = (LinearLayout) findViewById(R.id.ly_main_class);
		txt_main_class = (CustomTextView) findViewById(R.id.txt_main_class);
		txt_main_bj = (CustomTextView) findViewById(R.id.txt_main_bj);
		txt_main_classnew = (TextView) findViewById(R.id.txt_main_classnew);
		txt_main_xxxw = (TextView) findViewById(R.id.txt_main_xxxw);
		txt_main_nonschedule = (TextView) findViewById(R.id.txt_main_nonschedule);
		txt_main_noclassnew = (TextView) findViewById(R.id.txt_main_noclassnew);

		txt_main_classnew.setOnClickListener(this);
		txt_main_xxxw.setOnClickListener(this);
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
		case R.id.txt_main_xxxw: {
			Intent intent = new Intent(context, NoticeDetailsActivity.class);
			intent.putExtra("top", "学校新闻");
			intent.putExtra("id", xxxw.id);
			context.startActivity(intent);
			break;
		}
		case R.id.ly_main_class: {
			Intent intent = new Intent(context, ClassInfoActivity.class);
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

		list = database.query_by_lmdm("bpxxxw", 0, 1);
		if (list != null && list.size() == 1) {
			xxxw = list.get(0);
			txt_main_xxxw.setText("");
			if (!TextUtils.isEmpty(xxxw.xxnr)) {
				txt_main_xxxw.setVisibility(View.VISIBLE);
				txt_main_nonschedule.setVisibility(View.INVISIBLE);
				Document document = Jsoup.parse(xxxw.xxnr);
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
					txt_main_xxxw.setText(new_rl);
				}
			} else {
				txt_main_xxxw.setVisibility(View.INVISIBLE);
				txt_main_nonschedule.setVisibility(View.VISIBLE);
			}
		} else {
			txt_main_xxxw.setVisibility(View.INVISIBLE);
			txt_main_nonschedule.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void update(int type) {
		// TODO Auto-generated method stub
		if (type == APPCODE_UPDATA_EQU) {
			updata_equ_info();
			updata_new_notice();
		} else if (type == APPCODE_UPDATA_NOTIC) {
			updata_new_notice();
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
