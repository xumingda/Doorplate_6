package com.cx.doorplate.ui;

import android.content.Context;
import android.view.View;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.ui.AppCodeView;
import com.yy.doorplate.view.CustomTextView;

public class CXClassNameView extends AppCodeView {

	private CustomTextView txt_main_class;

	public CXClassNameView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
	}

	@Override
	public void initView() {
		View.inflate(context, R.layout.cx_ly_classname, this);
		txt_main_class = (CustomTextView) findViewById(R.id.txt_main_class);
		if (application.classInfoModel != null) {
			txt_main_class.setText(application.classInfoModel.bjmc);
		}
	}

	@Override
	public void update(int type) {
		// TODO �Զ����ɵķ������
		if (type == APPCODE_UPDATA_EQU) {
			if (application.classInfoModel != null) {
				txt_main_class.setText(application.classInfoModel.bjmc);
			}
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
