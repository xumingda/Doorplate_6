package com.cx.doorplate.ui;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.database.AttendInfoDatabase;
import com.yy.doorplate.model.AttendInfoModel;
import com.yy.doorplate.ui.AppCodeView;

public class CXMainAttendView extends AppCodeView {

	private TextView txt_cxmain_attend;

	public CXMainAttendView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
	}

	@Override
	public void initView() {
		View.inflate(context, R.layout.cx_ly_main_attend, this);
		txt_cxmain_attend = (TextView) findViewById(R.id.txt_cxmain_attend);
		updata_attend();
	}

	private void updata_attend() {
		String attend = "";
		int yd = 0, cq = 0, qq = 0, cd = 0;
		List<AttendInfoModel> list = null;
		AttendInfoDatabase database = new AttendInfoDatabase();
		if (application.curriculumInfoModel_now != null) {
			list = database
					.query_by_skjhid(application.curriculumInfoModel_now.id);
		} else if (application.curriculumInfoModel_next != null) {
			list = database
					.query_by_skjhid(application.curriculumInfoModel_next.id);
		}
		if (list != null) {
			yd = list.size();
			for (AttendInfoModel model : list) {
				if (model.kqzt.equals("0")) {
					cq++;
				} else if (model.kqzt.equals("3") || model.kqzt.equals("5")) {
					cq++;
					cd++;
				} else {
					qq++;
				}
			}
			attend = "应到 : " + yd + "人\t\t\t出勤 : " + cq + "人\t\t\t出勤率 : " + cq
					* 100 / yd + "%\n缺勤 : " + qq + "人\t\t\t迟到早退 : " + cd + "人";
		}
		if (TextUtils.isEmpty(attend)) {
			txt_cxmain_attend.setVisibility(View.INVISIBLE);
		} else {
			txt_cxmain_attend.setVisibility(View.VISIBLE);
			txt_cxmain_attend.setText(attend);
		}
	}

	@Override
	public void update(int type) {
		// TODO 自动生成的方法存根
		if (type == APPCODE_UPDATA_EQU || type == APPCODE_UPDATA_ATTEND
				|| type == APPCODE_UPDATA_CURRICULUM) {
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
