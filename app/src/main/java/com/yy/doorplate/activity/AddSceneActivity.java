package com.yy.doorplate.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.database.DeviceDatabase;
import com.yy.doorplate.database.SceneDatabase;
import com.yy.doorplate.model.DeviceModel;
import com.yy.doorplate.model.SceneModel;

public class AddSceneActivity extends Activity implements OnClickListener {

	private final String TAG = "ControlActivity";

	private MyApplication application;

	private int sceneOrDevice = 1;

	private Button btn_addscene_back, btn_addscene_save;
	private TextView txt_addscene_title, txt_addscnen_1, txt_addscnen_2,
			txt_addscnen_3, txt_addscnen_4, txt_addscnen_5, txt_addscnen_6,
			txt_addscnen_7, txt_addscnen_8, txt_addscnen_9, txt_addscnen_edit;
	private ImageView img_addscnen_1, img_addscnen_2, img_addscnen_3,
			img_addscnen_4, img_addscnen_5, img_addscnen_6, img_addscnen_7,
			img_addscnen_8, img_addscnen_9;
	private EditText edt_addscnen;

	private boolean isClick_1 = false, isClick_2 = false, isClick_3 = false,
			isClick_4 = false, isClick_5 = false, isClick_6 = false,
			isClick_7 = false, isClick_8 = false, isClick_9 = false;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addscene);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		sceneOrDevice = getIntent().getIntExtra("sceneOrDevice", 1);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
	}

	private void initView() {
		btn_addscene_back = (Button) findViewById(R.id.btn_addscene_back);
		btn_addscene_save = (Button) findViewById(R.id.btn_addscene_save);
		txt_addscene_title = (TextView) findViewById(R.id.txt_addscene_title);
		txt_addscnen_1 = (TextView) findViewById(R.id.txt_addscnen_1);
		txt_addscnen_2 = (TextView) findViewById(R.id.txt_addscnen_2);
		txt_addscnen_3 = (TextView) findViewById(R.id.txt_addscnen_3);
		txt_addscnen_4 = (TextView) findViewById(R.id.txt_addscnen_4);
		txt_addscnen_5 = (TextView) findViewById(R.id.txt_addscnen_5);
		txt_addscnen_6 = (TextView) findViewById(R.id.txt_addscnen_6);
		txt_addscnen_7 = (TextView) findViewById(R.id.txt_addscnen_7);
		txt_addscnen_8 = (TextView) findViewById(R.id.txt_addscnen_8);
		txt_addscnen_9 = (TextView) findViewById(R.id.txt_addscnen_9);
		txt_addscnen_edit = (TextView) findViewById(R.id.txt_addscnen_edit);
		img_addscnen_1 = (ImageView) findViewById(R.id.img_addscnen_1);
		img_addscnen_2 = (ImageView) findViewById(R.id.img_addscnen_2);
		img_addscnen_3 = (ImageView) findViewById(R.id.img_addscnen_3);
		img_addscnen_4 = (ImageView) findViewById(R.id.img_addscnen_4);
		img_addscnen_5 = (ImageView) findViewById(R.id.img_addscnen_5);
		img_addscnen_6 = (ImageView) findViewById(R.id.img_addscnen_6);
		img_addscnen_7 = (ImageView) findViewById(R.id.img_addscnen_7);
		img_addscnen_8 = (ImageView) findViewById(R.id.img_addscnen_8);
		img_addscnen_9 = (ImageView) findViewById(R.id.img_addscnen_9);
		edt_addscnen = (EditText) findViewById(R.id.edt_addscnen);
		edt_addscnen.addTextChangedListener(application.textWatcher);
		btn_addscene_back.setOnClickListener(this);
		btn_addscene_save.setOnClickListener(this);
		img_addscnen_1.setOnClickListener(this);
		img_addscnen_2.setOnClickListener(this);
		img_addscnen_3.setOnClickListener(this);
		img_addscnen_4.setOnClickListener(this);
		img_addscnen_5.setOnClickListener(this);
		img_addscnen_6.setOnClickListener(this);
		img_addscnen_7.setOnClickListener(this);
		img_addscnen_8.setOnClickListener(this);
		img_addscnen_9.setOnClickListener(this);

		if (sceneOrDevice == 1) {
			txt_addscene_title.setText("添加场景");
			txt_addscnen_1.setText("上课");
			txt_addscnen_2.setText("下课");
			txt_addscnen_3.setText("自习");
			txt_addscnen_4.setText("考试");
			txt_addscnen_5.setText("会议");
			txt_addscnen_6.setText("清洁");
			txt_addscnen_7.setText("放假");
			txt_addscnen_8.setText("检修");
			txt_addscnen_9.setText("其他");
			txt_addscnen_edit.setText("添加其它场景模式");
			edt_addscnen.setHint("请输入场景名称");
		} else {
			txt_addscene_title.setText("添加设备");
			txt_addscnen_1.setText("灯");
			txt_addscnen_2.setText("电脑");
			txt_addscnen_3.setText("投影机");
			txt_addscnen_4.setText("幕布");
			txt_addscnen_5.setText("空调");
			txt_addscnen_6.setText("对讲机");
			txt_addscnen_7.setText("话筒");
			txt_addscnen_8.setText("音响");
			txt_addscnen_9.setText("其他");
			txt_addscnen_edit.setText("添加设备");
			edt_addscnen.setHint("请输入设备名称");
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		application.handler_touch.removeMessages(0);
		application.handler_touch.sendEmptyMessageDelayed(0,
				application.screensaver_time * 1000);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getAction()) {
		case KeyEvent.ACTION_DOWN:
			application.handler_touch.removeMessages(0);
			break;
		case KeyEvent.ACTION_UP:
			application.handler_touch.sendEmptyMessageDelayed(0,
					application.screensaver_time * 1000);
			break;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View view = getCurrentFocus();
			if (isShouldHideSoftKeyBoard(view, ev)) {
				hideSoftKeyBoard(view.getWindowToken());
			}
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			application.handler_touch.removeMessages(0);
			break;
		case MotionEvent.ACTION_UP:
			application.handler_touch.sendEmptyMessageDelayed(0,
					application.screensaver_time * 1000);
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	private void updataImg() {
		if (isClick_1) {
			img_addscnen_1.setImageResource(R.drawable.addscene_click);
		} else {
			img_addscnen_1.setImageBitmap(null);
		}
		if (isClick_2) {
			img_addscnen_2.setImageResource(R.drawable.addscene_click);
		} else {
			img_addscnen_2.setImageBitmap(null);
		}
		if (isClick_3) {
			img_addscnen_3.setImageResource(R.drawable.addscene_click);
		} else {
			img_addscnen_3.setImageBitmap(null);
		}
		if (isClick_4) {
			img_addscnen_4.setImageResource(R.drawable.addscene_click);
		} else {
			img_addscnen_4.setImageBitmap(null);
		}
		if (isClick_5) {
			img_addscnen_5.setImageResource(R.drawable.addscene_click);
		} else {
			img_addscnen_5.setImageBitmap(null);
		}
		if (isClick_6) {
			img_addscnen_6.setImageResource(R.drawable.addscene_click);
		} else {
			img_addscnen_6.setImageBitmap(null);
		}
		if (isClick_7) {
			img_addscnen_7.setImageResource(R.drawable.addscene_click);
		} else {
			img_addscnen_7.setImageBitmap(null);
		}
		if (isClick_8) {
			img_addscnen_8.setImageResource(R.drawable.addscene_click);
		} else {
			img_addscnen_8.setImageBitmap(null);
		}
		if (isClick_9) {
			img_addscnen_9.setImageResource(R.drawable.addscene_click);
		} else {
			img_addscnen_9.setImageBitmap(null);
		}
		edt_addscnen.setSelection(edt_addscnen.getText().toString().length());
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_addscene_back:
			finish();
			break;
		case R.id.btn_addscene_save:
			save();
			break;
		case R.id.img_addscnen_1: {
			if (isClick_1) {
				isClick_1 = false;
				edt_addscnen.setText("");
			} else {
				isClick_1 = true;
				if (sceneOrDevice == 1) {
					edt_addscnen.setText("上课");
				} else if (sceneOrDevice == 2) {
					edt_addscnen.setText("灯");
				}
			}
			isClick_2 = false;
			isClick_3 = false;
			isClick_4 = false;
			isClick_5 = false;
			isClick_6 = false;
			isClick_7 = false;
			isClick_8 = false;
			isClick_9 = false;
			updataImg();
			break;
		}
		case R.id.img_addscnen_2: {
			isClick_1 = false;
			if (isClick_2) {
				isClick_2 = false;
				edt_addscnen.setText("");
			} else {
				isClick_2 = true;
				if (sceneOrDevice == 1) {
					edt_addscnen.setText("下课");
				} else if (sceneOrDevice == 2) {
					edt_addscnen.setText("电脑");
				}
			}
			isClick_3 = false;
			isClick_4 = false;
			isClick_5 = false;
			isClick_6 = false;
			isClick_7 = false;
			isClick_8 = false;
			isClick_9 = false;
			updataImg();
			break;
		}
		case R.id.img_addscnen_3: {
			isClick_1 = false;
			isClick_2 = false;
			if (isClick_3) {
				isClick_3 = false;
				edt_addscnen.setText("");
			} else {
				isClick_3 = true;
				if (sceneOrDevice == 1) {
					edt_addscnen.setText("自习");
				} else if (sceneOrDevice == 2) {
					edt_addscnen.setText("投影机");
				}
			}
			isClick_4 = false;
			isClick_5 = false;
			isClick_6 = false;
			isClick_7 = false;
			isClick_8 = false;
			isClick_9 = false;
			updataImg();
			break;
		}
		case R.id.img_addscnen_4: {
			isClick_1 = false;
			isClick_2 = false;
			isClick_3 = false;
			if (isClick_4) {
				isClick_4 = false;
				edt_addscnen.setText("");
			} else {
				isClick_4 = true;
				if (sceneOrDevice == 1) {
					edt_addscnen.setText("考试");
				} else if (sceneOrDevice == 2) {
					edt_addscnen.setText("幕布");
				}
			}
			isClick_5 = false;
			isClick_6 = false;
			isClick_7 = false;
			isClick_8 = false;
			isClick_9 = false;
			updataImg();
			break;
		}
		case R.id.img_addscnen_5: {
			isClick_1 = false;
			isClick_2 = false;
			isClick_3 = false;
			isClick_4 = false;
			if (isClick_5) {
				isClick_5 = false;
				edt_addscnen.setText("");
			} else {
				isClick_5 = true;
				if (sceneOrDevice == 1) {
					edt_addscnen.setText("会议");
				} else if (sceneOrDevice == 2) {
					edt_addscnen.setText("空调");
				}
			}
			isClick_6 = false;
			isClick_7 = false;
			isClick_8 = false;
			isClick_9 = false;
			updataImg();
			break;
		}
		case R.id.img_addscnen_6: {
			isClick_1 = false;
			isClick_2 = false;
			isClick_3 = false;
			isClick_4 = false;
			isClick_5 = false;
			if (isClick_6) {
				isClick_6 = false;
				edt_addscnen.setText("");
			} else {
				isClick_6 = true;
				if (sceneOrDevice == 1) {
					edt_addscnen.setText("清洁");
				} else if (sceneOrDevice == 2) {
					edt_addscnen.setText("对讲机");
				}
			}
			isClick_7 = false;
			isClick_8 = false;
			isClick_9 = false;
			updataImg();
			break;
		}
		case R.id.img_addscnen_7: {
			isClick_1 = false;
			isClick_2 = false;
			isClick_3 = false;
			isClick_4 = false;
			isClick_5 = false;
			isClick_6 = false;
			if (isClick_7) {
				isClick_7 = false;
				edt_addscnen.setText("");
			} else {
				isClick_7 = true;
				if (sceneOrDevice == 1) {
					edt_addscnen.setText("放假");
				} else if (sceneOrDevice == 2) {
					edt_addscnen.setText("话筒");
				}
			}
			isClick_8 = false;
			isClick_9 = false;
			updataImg();
			break;
		}
		case R.id.img_addscnen_8: {
			isClick_1 = false;
			isClick_2 = false;
			isClick_3 = false;
			isClick_4 = false;
			isClick_5 = false;
			isClick_6 = false;
			isClick_7 = false;
			if (isClick_8) {
				isClick_8 = false;
				edt_addscnen.setText("");
			} else {
				isClick_8 = true;
				if (sceneOrDevice == 1) {
					edt_addscnen.setText("检修");
				} else if (sceneOrDevice == 2) {
					edt_addscnen.setText("音响");
				}
			}
			isClick_9 = false;
			updataImg();
			break;
		}
		case R.id.img_addscnen_9: {
			isClick_1 = false;
			isClick_2 = false;
			isClick_3 = false;
			isClick_4 = false;
			isClick_5 = false;
			isClick_6 = false;
			isClick_7 = false;
			isClick_8 = false;
			if (isClick_9) {
				isClick_9 = false;
				edt_addscnen.setText("");
			} else {
				isClick_9 = true;
				edt_addscnen.setText("其它");
			}
			updataImg();
			break;
		}
		}
	}

	private void save() {
		int img = 0;
		if (isClick_1) {
			img = 1;
		} else if (isClick_2) {
			img = 2;
		} else if (isClick_3) {
			img = 3;
		} else if (isClick_4) {
			img = 4;
		} else if (isClick_5) {
			img = 5;
		} else if (isClick_6) {
			img = 6;
		} else if (isClick_7) {
			img = 7;
		} else if (isClick_8) {
			img = 8;
		} else if (isClick_9) {
			img = 9;
		}
		if (img == 0) {
			application.showToast("请选择一个图标");
		} else {
			String name = edt_addscnen.getText().toString();
			if (!TextUtils.isEmpty(name)) {
				if (sceneOrDevice == 1) {
					SceneDatabase database = new SceneDatabase();
					if (database.query_by_name(name) == null) {
						SceneModel model = new SceneModel();
						model.name = name;
						model.img = img;
						database.insert(model);
						application.showToast("保存成功");
					} else {
						application.showToast("该场景已存在");
					}
				} else if (sceneOrDevice == 2) {
					DeviceDatabase database = new DeviceDatabase();
					if (database.query_by_name(name) == null) {
						DeviceModel model = new DeviceModel();
						model.name = name;
						model.img = img;
						database.insert(model);
						application.showToast("保存成功");
					} else {
						application.showToast("该设备已存在");
					}
				}
			} else {
				if (sceneOrDevice == 1) {
					application.showToast("请输入场景名称");
				} else if (sceneOrDevice == 2) {
					application.showToast("请输入设备名称");
				}
			}
		}
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals("permission_finish")) {
				finish();
			}
		}
	}
	
	private boolean isShouldHideSoftKeyBoard(View view, MotionEvent event) {
		if (view != null && (view instanceof EditText)) {
			int[] l = { 0, 0 };
			view.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + view.getHeight(), right = left + view.getWidth();
			if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	private void hideSoftKeyBoard(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
