package com.yy.doorplate.ui;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.database.BlackboardInfoDatabase;
import com.yy.doorplate.model.BlackboardInfoModel;

public class EnglishView extends AppCodeView {

	private TextView txt_classnew_english, txt_classnew_english_title;

	public EnglishView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
		updata_english();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		View.inflate(context, R.layout.ly_english, this);
		txt_classnew_english = (TextView) findViewById(R.id.txt_classnew_english);
		txt_classnew_english_title = (TextView) findViewById(R.id.txt_classnew_english_title);
	}

	private void updata_english() {
		BlackboardInfoDatabase database = new BlackboardInfoDatabase();
		BlackboardInfoModel model = database.query();
		if (model != null) {
			txt_classnew_english.setText(model.content);
			txt_classnew_english_title.setText(model.tagName);
		} else {
			txt_classnew_english.setText("");
			txt_classnew_english_title.setText("");
		}
	}

	@Override
	public void update(int type) {
		// TODO Auto-generated method stub
		if (type == APPCODE_UPDATA_ENGLISH || type == APPCODE_UPDATA_EQU) {
			updata_english();
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
