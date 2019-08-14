package com.cx.doorplate.ui;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.database.BlackboardInfoDatabase;
import com.yy.doorplate.model.BlackboardInfoModel;
import com.yy.doorplate.ui.AppCodeView;

public class CXMainClassMottoView extends AppCodeView {

	private TextView txt_cxmain_classmotto;

	public CXMainClassMottoView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
	}

	@Override
	public void initView() {
		View.inflate(context, R.layout.cx_ly_main_classmotto, this);
		txt_cxmain_classmotto = (TextView) findViewById(R.id.txt_cxmain_classmotto);
		updata_classmotto();
	}

	private void updata_classmotto() {
		BlackboardInfoDatabase database = new BlackboardInfoDatabase();
		BlackboardInfoModel model = database.query();
		if (model == null) {
			txt_cxmain_classmotto.setText("");
		} else {
			txt_cxmain_classmotto.setText(model.content);
		}
	}

	@Override
	public void update(int type) {
		// TODO �Զ����ɵķ������
		if (type == APPCODE_UPDATA_EQU || type == APPCODE_UPDATA_ENGLISH) {
			updata_classmotto();
		}
	}

	@Override
	public void pause() {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void resume() {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void stop() {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void destroy() {
		// TODO �Զ����ɵķ������

	}

}
