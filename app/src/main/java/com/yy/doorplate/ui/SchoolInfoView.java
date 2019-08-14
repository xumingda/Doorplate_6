package com.yy.doorplate.ui;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.activity.PjNoticeListActivity;
import com.yy.doorplate.activity.SchoolDetailsActivity;
import com.yy.doorplate.database.SchoolInfoDatabase;
import com.yy.doorplate.model.SchoolInfoModel;

public class SchoolInfoView extends AppCodeView implements OnClickListener {

	private Button btn_schoolnew_1, btn_schoolnew_2, btn_schoolnew_3,
			btn_schoolnew_4;

	private List<SchoolInfoModel> schoolInfoModels = null;

	public SchoolInfoView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
		updata_school();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		View.inflate(context, R.layout.ly_school_info, this);
		btn_schoolnew_1 = (Button) findViewById(R.id.btn_schoolnew_1);
		btn_schoolnew_2 = (Button) findViewById(R.id.btn_schoolnew_2);
		btn_schoolnew_3 = (Button) findViewById(R.id.btn_schoolnew_3);
		btn_schoolnew_4 = (Button) findViewById(R.id.btn_schoolnew_4);

		btn_schoolnew_1.setOnClickListener(this);
		btn_schoolnew_2.setOnClickListener(this);
		btn_schoolnew_3.setOnClickListener(this);
		btn_schoolnew_4.setOnClickListener(this);
	}

	private void updata_school() {
		SchoolInfoDatabase database = new SchoolInfoDatabase();
		schoolInfoModels = database.query_all();
		if (schoolInfoModels != null && schoolInfoModels.size() > 0) {
			btn_schoolnew_1.setVisibility(View.VISIBLE);
			btn_schoolnew_1.setText(schoolInfoModels.get(0).infoName);
			if (schoolInfoModels.size() > 1) {
				btn_schoolnew_2.setVisibility(View.VISIBLE);
				btn_schoolnew_2.setText(schoolInfoModels.get(1).infoName);
			} else {
				btn_schoolnew_2.setVisibility(View.INVISIBLE);
				btn_schoolnew_3.setVisibility(View.INVISIBLE);
				btn_schoolnew_4.setVisibility(View.INVISIBLE);
			}
			if (schoolInfoModels.size() > 2) {
				btn_schoolnew_3.setVisibility(View.VISIBLE);
				btn_schoolnew_3.setText(schoolInfoModels.get(2).infoName);
			} else {
				btn_schoolnew_3.setVisibility(View.INVISIBLE);
				btn_schoolnew_4.setVisibility(View.INVISIBLE);
			}
			if (schoolInfoModels.size() == 4) {
				btn_schoolnew_4.setVisibility(View.VISIBLE);
				btn_schoolnew_4.setText(schoolInfoModels.get(3).infoName);
			} else if (schoolInfoModels.size() > 4) {
				btn_schoolnew_4.setVisibility(View.VISIBLE);
				btn_schoolnew_4.setText("¸ü¶à");
			} else {
				btn_schoolnew_4.setVisibility(View.INVISIBLE);
			}
		} else {
			btn_schoolnew_1.setVisibility(View.INVISIBLE);
			btn_schoolnew_2.setVisibility(View.INVISIBLE);
			btn_schoolnew_3.setVisibility(View.INVISIBLE);
			btn_schoolnew_4.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void update(int type) {
		// TODO Auto-generated method stub
		if (type == APPCODE_UPDATA_SCHOOL || type == APPCODE_UPDATA_EQU) {
			updata_school();
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
		case R.id.btn_schoolnew_1: {
			Intent intent = new Intent(context, SchoolDetailsActivity.class);
			intent.putExtra("infoCode", schoolInfoModels.get(0).infoCode);
			context.startActivity(intent);
			break;
		}
		case R.id.btn_schoolnew_2: {
			Intent intent = new Intent(context, SchoolDetailsActivity.class);
			intent.putExtra("infoCode", schoolInfoModels.get(1).infoCode);
			context.startActivity(intent);
			break;
		}
		case R.id.btn_schoolnew_3: {
			Intent intent = new Intent(context, SchoolDetailsActivity.class);
			intent.putExtra("infoCode", schoolInfoModels.get(2).infoCode);
			context.startActivity(intent);
			break;
		}
		case R.id.btn_schoolnew_4: {
			if (schoolInfoModels.size() == 4) {
				Intent intent = new Intent(context, SchoolDetailsActivity.class);
				intent.putExtra("infoCode", schoolInfoModels.get(3).infoCode);
				context.startActivity(intent);
			} else if (schoolInfoModels.size() > 4) {
				Intent intent = new Intent(context, PjNoticeListActivity.class);
				intent.putExtra("type", 5);
				context.startActivity(intent);
			}
			break;
		}
		}
	}

}
