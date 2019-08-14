package com.yy.doorplate.ui;

import java.text.SimpleDateFormat;
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
import com.yy.doorplate.activity.NoticeDetailsActivity;
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.model.NoticeInfoModel;

public class MainNoticeView extends AppCodeView implements OnClickListener {

	private LinearLayout ly_main_new1, ly_main_new2, ly_main_new3;
	private TextView txt_main_new_title1, txt_main_new_title2,
			txt_main_new_title3, txt_main_new_nr1, txt_main_new_nr2,
			txt_main_new_nr3, txt_main_new_time1, txt_main_new_time2,
			txt_main_new_time3, txt_main_nonew;

	private List<NoticeInfoModel> noticeInfoModels = null;

	public MainNoticeView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
	}

	@Override
	public void initView() {
		// TODO 自动生成的方法存根
		View.inflate(context, R.layout.ly_main_notice, this);
		ly_main_new1 = (LinearLayout) findViewById(R.id.ly_main_new1);
		ly_main_new2 = (LinearLayout) findViewById(R.id.ly_main_new2);
		ly_main_new3 = (LinearLayout) findViewById(R.id.ly_main_new3);
		txt_main_nonew = (TextView) findViewById(R.id.txt_main_nonew);
		txt_main_new_title1 = (TextView) findViewById(R.id.txt_main_new_title1);
		txt_main_new_title2 = (TextView) findViewById(R.id.txt_main_new_title2);
		txt_main_new_title3 = (TextView) findViewById(R.id.txt_main_new_title3);
		txt_main_new_nr1 = (TextView) findViewById(R.id.txt_main_new_nr1);
		txt_main_new_nr2 = (TextView) findViewById(R.id.txt_main_new_nr2);
		txt_main_new_nr3 = (TextView) findViewById(R.id.txt_main_new_nr3);
		txt_main_new_time1 = (TextView) findViewById(R.id.txt_main_new_time1);
		txt_main_new_time2 = (TextView) findViewById(R.id.txt_main_new_time2);
		txt_main_new_time3 = (TextView) findViewById(R.id.txt_main_new_time3);

		ly_main_new1.setOnClickListener(this);
		ly_main_new2.setOnClickListener(this);
		ly_main_new3.setOnClickListener(this);

		updata_new_notice();
	}

	private void updata_new_notice() {
		NoticeInfoDatabase database = new NoticeInfoDatabase();
		noticeInfoModels = database.query_limit(0, 3);
		if (noticeInfoModels != null && noticeInfoModels.size() > 0) {
			txt_main_nonew.setVisibility(View.INVISIBLE);
			ly_main_new1.setVisibility(View.VISIBLE);
			txt_main_new_title1.setText(noticeInfoModels.get(0).xxzt);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			txt_main_new_time1.setText(formatter.format(Long
					.parseLong(noticeInfoModels.get(0).fbsj)));
			txt_main_new_nr1.setText("");
			if (!TextUtils.isEmpty(noticeInfoModels.get(0).xxnr)) {
				Document document = Jsoup.parse(noticeInfoModels.get(0).xxnr);
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
					txt_main_new_nr1.setText(new_rl);
				}
			}
			if (noticeInfoModels.size() > 1) {
				ly_main_new2.setVisibility(View.VISIBLE);
				txt_main_new_title2.setText(noticeInfoModels.get(1).xxzt);
				txt_main_new_time2.setText(formatter.format(Long
						.parseLong(noticeInfoModels.get(1).fbsj)));
				txt_main_new_nr2.setText("");
				if (!TextUtils.isEmpty(noticeInfoModels.get(1).xxnr)) {
					Document document = Jsoup
							.parse(noticeInfoModels.get(1).xxnr);
					Elements p = document.select("p");
					if (p != null && p.size() > 0) {
						String new_rl = "";
						for (Element element : p) {
							if (TextUtils.isEmpty(new_rl)) {
								new_rl = element.text();
							} else {
								new_rl = new_rl + "\n" + element.text();
							}
							if (!TextUtils.isEmpty(new_rl)
									&& new_rl.length() > 100) {
								break;
							}
						}
						txt_main_new_nr2.setText(new_rl);
					}
				}
			}
			if (noticeInfoModels.size() > 2) {
				ly_main_new3.setVisibility(View.VISIBLE);
				txt_main_new_title3.setText(noticeInfoModels.get(2).xxzt);
				txt_main_new_time3.setText(formatter.format(Long
						.parseLong(noticeInfoModels.get(2).fbsj)));
				txt_main_new_nr3.setText("");
				if (!TextUtils.isEmpty(noticeInfoModels.get(2).xxnr)) {
					Document document = Jsoup
							.parse(noticeInfoModels.get(2).xxnr);
					Elements p = document.select("p");
					if (p != null && p.size() > 0) {
						String new_rl = "";
						for (Element element : p) {
							if (TextUtils.isEmpty(new_rl)) {
								new_rl = element.text();
							} else {
								new_rl = new_rl + "\n" + element.text();
							}
							if (!TextUtils.isEmpty(new_rl)
									&& new_rl.length() > 100) {
								break;
							}
						}
						txt_main_new_nr3.setText(new_rl);
					}
				}
			}
		} else {
			txt_main_nonew.setVisibility(View.VISIBLE);
			ly_main_new1.setVisibility(View.INVISIBLE);
			ly_main_new2.setVisibility(View.INVISIBLE);
			ly_main_new3.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View view) {
		// TODO 自动生成的方法存根
		switch (view.getId()) {
		case R.id.ly_main_new1: {
			Intent intent = new Intent(context, NoticeDetailsActivity.class);
			intent.putExtra("id", noticeInfoModels.get(0).id);
			context.startActivity(intent);
			break;
		}
		case R.id.ly_main_new2: {
			Intent intent = new Intent(context, NoticeDetailsActivity.class);
			intent.putExtra("id", noticeInfoModels.get(1).id);
			context.startActivity(intent);
			break;
		}
		case R.id.ly_main_new3: {
			Intent intent = new Intent(context, NoticeDetailsActivity.class);
			intent.putExtra("id", noticeInfoModels.get(2).id);
			context.startActivity(intent);
			break;
		}
		}
	}

	@Override
	public void update(int type) {
		// TODO 自动生成的方法存根
		if (type == APPCODE_UPDATA_NOTIC || type == APPCODE_UPDATA_EQU) {
			updata_new_notice();
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
