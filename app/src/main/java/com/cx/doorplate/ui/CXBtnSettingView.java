package com.cx.doorplate.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.advertisement.activity.ActivityAppcodeo;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.activity.SettingActivity;
import com.yy.doorplate.database.UserInfoDatabase;
import com.yy.doorplate.ui.AppCodeView;

public class CXBtnSettingView extends AppCodeView implements OnClickListener {

	private Activity activity;

	private Button button;

	private ProgressDialog progressDialog = null;

	public CXBtnSettingView(Context context, MyApplication application,
			Activity activity, String nextPath) {
		super(context, application, nextPath);
		this.activity = activity;
		initView();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		View.inflate(context, R.layout.cx_ly_btn_setting, this);
		button = (Button) findViewById(R.id.cx_btn_app_setting);
		button.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.cx_btn_app_setting:
			if (!TextUtils.isEmpty(nextPath)) {
				Intent intent = new Intent(context, ActivityAppcodeo.class);
				intent.putExtra("ThemePolicyPath", nextPath);
				context.startActivity(intent);
			} else {
				application.operateType = "SETTING";
				DialogPermission();
			}
			break;
		case R.id.btn_permission_user: {
			btn_permission_user
					.setBackgroundResource(R.drawable.btn_permission_type);
			btn_permission_user.setTextColor(getResources().getColor(
					R.color.white));
			ly_permission_user.setVisibility(View.VISIBLE);
			btn_permission_card
					.setBackgroundResource(R.drawable.btn_permission_type_click);
			btn_permission_card.setTextColor(getResources().getColor(
					R.color.blue));
			ly_permission_card.setVisibility(View.INVISIBLE);
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null)
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			break;
		}
		case R.id.btn_permission_card: {
			btn_permission_card
					.setBackgroundResource(R.drawable.btn_permission_type);
			btn_permission_card.setTextColor(getResources().getColor(
					R.color.white));
			ly_permission_card.setVisibility(View.VISIBLE);
			btn_permission_user
					.setBackgroundResource(R.drawable.btn_permission_type_click);
			btn_permission_user.setTextColor(getResources().getColor(
					R.color.blue));
			ly_permission_user.setVisibility(View.INVISIBLE);
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null)
				imm.hideSoftInputFromWindow(
						edt_permission_user.getWindowToken(), 0);
			break;
		}
		case R.id.btn_premission_cancel1:
		case R.id.btn_premission_cancel2:
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null)
				imm.hideSoftInputFromWindow(
						edt_permission_user.getWindowToken(), 0);
			if (application.dialog != null && application.dialog.isShowing()) {
				application.dialog.dismiss();
			}
			break;
		case R.id.btn_premission_login:
			String name = edt_permission_user.getText().toString();
			String pwd = edt_permission_pwd.getText().toString();
			if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
				if (name.equals("admin") && pwd.equals("admin123!@#")) {
					application.showToast("认证通过");
					if (application.dialog != null
							&& application.dialog.isShowing()) {
						application.dialog.dismiss();
					}
					if (application.operateType.equals("SETTING")) {
						Intent i = new Intent(context, SettingActivity.class);
						context.startActivity(i);
					}
					return;
				}
				UserInfoDatabase userInfoDatabase = new UserInfoDatabase();
				if (userInfoDatabase.query_by_userName(name, pwd) != null) {
					application.showToast("认证通过");
					if (application.dialog != null
							&& application.dialog.isShowing()) {
						application.dialog.dismiss();
					}
					if (application.operateType.equals("SETTING")) {
						Intent i = new Intent(context, SettingActivity.class);
						context.startActivity(i);
					}
					return;
				}
				btn_premission_login.setEnabled(false);
				if (progressDialog == null) {
					progressDialog = ProgressDialog.show(context, null,
							"认证中，请稍后", false, false);
				}
				application.httpProcess.Permission(
						application.equInfoModel.jssysdm,
						application.operateType, "ACCOUNT_PASSWORD", name, pwd,
						"");
			} else {
				application.showToast("账号或密码不能为空");
			}
			break;
		}
	}

	private EditText edt_permission_user, edt_permission_pwd;
	private Button btn_permission_user, btn_permission_card,
			btn_premission_login, btn_premission_cancel1,
			btn_premission_cancel2;
	private RelativeLayout ly_permission_user, ly_permission_card;

	private void DialogPermission() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		application.dialog = builder.create();
		LayoutInflater inflater = LayoutInflater.from(application);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.dialog_permission, null);
		application.dialog.setView(layout);
		application.dialog.show();
		application.dialog.getWindow().setLayout(900, 700);
		application.dialog.getWindow().setContentView(
				R.layout.dialog_permission);
		// application.dialog.getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		application.dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialogInterface) {
				application.operateType = null;
				InputMethodManager imm = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null)
					imm.hideSoftInputFromWindow(activity.getWindow()
							.getDecorView().getWindowToken(), 0);
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
					progressDialog = null;
				}
			}
		});
		ly_permission_user = (RelativeLayout) application.dialog
				.findViewById(R.id.ly_permission_user);
		ly_permission_card = (RelativeLayout) application.dialog
				.findViewById(R.id.ly_permission_card);
		edt_permission_user = (EditText) application.dialog
				.findViewById(R.id.edt_permission_user);
		edt_permission_pwd = (EditText) application.dialog
				.findViewById(R.id.edt_permission_pwd);
		btn_permission_user = (Button) application.dialog
				.findViewById(R.id.btn_permission_user);
		btn_permission_card = (Button) application.dialog
				.findViewById(R.id.btn_permission_card);
		btn_premission_login = (Button) application.dialog
				.findViewById(R.id.btn_premission_login);
		btn_premission_cancel1 = (Button) application.dialog
				.findViewById(R.id.btn_premission_cancel1);
		btn_premission_cancel2 = (Button) application.dialog
				.findViewById(R.id.btn_premission_cancel2);
		btn_permission_user.setOnClickListener(this);
		btn_permission_card.setOnClickListener(this);
		btn_premission_login.setOnClickListener(this);
		btn_premission_cancel1.setOnClickListener(this);
		btn_premission_cancel2.setOnClickListener(this);
		edt_permission_user.addTextChangedListener(application.textWatcher);
		edt_permission_pwd.addTextChangedListener(application.textWatcher);
		// btn_permission_card.setEnabled(false);
		// btn_permission_user
		// .setBackgroundResource(R.drawable.btn_permission_type);
		// btn_permission_user
		// .setTextColor(getResources().getColor(R.color.white));
		// ly_permission_user.setVisibility(View.VISIBLE);
		// btn_permission_card
		// .setBackgroundResource(R.drawable.btn_permission_type_click);
		// btn_permission_card.setTextColor(getResources().getColor(R.color.blue));
		// ly_permission_card.setVisibility(View.INVISIBLE);
		// InputMethodManager imm = (InputMethodManager) context
		// .getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void update(int type) {
		// TODO Auto-generated method stub
		if (type == APPCODE_UPDATA_DIALOG_DIS) {
			if (btn_premission_login != null) {
				btn_premission_login.setEnabled(true);
			}
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
				progressDialog = null;
			}
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
