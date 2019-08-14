package com.cx.doorplate.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.cx.doorplate.fragment.PhotoFragment;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.adapter.MyViewPagerAdapter;
import com.yy.doorplate.database.PicDreOrActivityDatabase;
import com.yy.doorplate.model.PicDreOrActivityModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;
import com.yy.doorplate.view.CustomViewpager;

public class CXPhotoActivity extends FragmentActivity implements
		OnClickListener {

	private final String TAG = "CXPhotoActivity";

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private RelativeLayout ly_cx_photo;
	private Button btn_cx_back, btn_photo_left, btn_photo_right;
	private TextView txt_weather,txt_photo_title;
	private TextClock textClock;
	private ImageView img_photo;
	private CustomViewpager vp_photo;

	private ImageLoader imageLoader = null;

	private List<Fragment> fragments;
	private int fragment_total, fragment_i;

	// 1学校相册；2班级相册
	private int type = 1;

	private int layer = 1;

	private List<String> parentIds = new ArrayList<String>();
	private List<PicDreOrActivityModel> dreOrActivityModels = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_activity_photo);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		imageLoader = new ImageLoader(application);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		type = getIntent().getIntExtra("type", 1);

		initView();
	}

	private void initView() {
		ly_cx_photo = (RelativeLayout) findViewById(R.id.ly_cx_photo);
		btn_cx_back = (Button) findViewById(R.id.btn_cx_back);
		btn_photo_left = (Button) findViewById(R.id.btn_photo_left);
		btn_photo_right = (Button) findViewById(R.id.btn_photo_right);
		txt_photo_title = (TextView) findViewById(R.id.txt_photo_title);
		img_photo = (ImageView) findViewById(R.id.img_photo);
		txt_weather = (TextView) findViewById(R.id.txt_weather);
		vp_photo = (CustomViewpager) findViewById(R.id.vp_photo);

		textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("yyyy年MM月dd日\nEEEE      HH : mm");
		txt_weather.setText(application.currentCity.trim() + "   "
				+ application.temperature.trim() + "\n"
				+ application.weather.trim());

		btn_cx_back.setOnClickListener(this);
		btn_photo_left.setOnClickListener(this);
		btn_photo_right.setOnClickListener(this);
		img_photo.setOnClickListener(this);

		vp_photo.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				fragment_i = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		if (type == 1) {
//			img_photo_title.setImageResource(R.drawable.cx_btn_school_photo);
			txt_photo_title.setText("校园相册");
		} else if (type == 2) {
//			img_photo_title.setImageResource(R.drawable.cx_img_photo_title2);
			txt_photo_title.setText("班级相册");
		}

		updata(layer, "");
	}

	private void updata(int layer, String parentId) {
		PicDreOrActivityDatabase database = new PicDreOrActivityDatabase();
		if (layer == 1) {
			dreOrActivityModels = database.query("objectType = ? and kind = ?",
					new String[] { "3", type + "" });
		} else if (layer > 1) {
			dreOrActivityModels = database.query("parentId = ? and kind = ?",
					new String[] { parentId, type + "" });
		}
		fragment_i = 0;
		fragment_total = 0;
		fragments = new ArrayList<Fragment>();
		if (dreOrActivityModels != null && dreOrActivityModels.size() > 0) {
			parentIds.add(dreOrActivityModels.get(0).parentId);
			if (dreOrActivityModels.size() % 8 == 0) {
				fragment_total = dreOrActivityModels.size() / 8;
			} else {
				fragment_total = dreOrActivityModels.size() / 8 + 1;
			}
		}
		if (fragment_total > 0) {
			for (int i = 0; i < fragment_total; i++) {
				PhotoFragment fragment = new PhotoFragment();
				fragment.setImageLoader(imageLoader);
				fragment.setLayer(layer);
				if (i == fragment_total - 1) {
					fragment.setList(dreOrActivityModels.subList(8 * i,
							dreOrActivityModels.size()));
				} else {
					fragment.setList(dreOrActivityModels.subList(8 * i,
							8 * i + 8));
				}
				fragments.add(fragment);
			}
			MyViewPagerAdapter adapter = new MyViewPagerAdapter(
					getSupportFragmentManager(), fragments);
			vp_photo.setAdapter(adapter);
			btn_photo_left.setVisibility(View.VISIBLE);
			btn_photo_right.setVisibility(View.VISIBLE);
		} else {
			vp_photo.setAdapter(null);
			btn_photo_left.setVisibility(View.INVISIBLE);
			btn_photo_right.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cx_back:
			if (layer == 1) {
				finish();
			} else if (layer > 1) {
				layer--;
				if (parentIds.size() > layer - 1) {
					updata(layer, parentIds.get(layer - 1));
				}
			}
			break;
		case R.id.btn_photo_left:
			if (fragment_i > 0) {
				fragment_i--;
				vp_photo.setCurrentItem(fragment_i);
			}
			break;
		case R.id.btn_photo_right:
			if (fragment_i < fragment_total - 1) {
				fragment_i++;
				vp_photo.setCurrentItem(fragment_i);
			}
			break;
		case R.id.img_photo:
			ly_cx_photo.setVisibility(View.INVISIBLE);
			break;
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
			} else if (tag.equals("photo_next")) {
				layer++;
				updata(layer, intent.getStringExtra("objectId"));
			} else if (tag.equals("photo_show")) {
				ly_cx_photo.setVisibility(View.VISIBLE);
				String objectEntityAddress = intent
						.getStringExtra("objectEntityAddress");
				String[] s = objectEntityAddress.split("/");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PHOTO + "/" + s[s.length - 1];
				imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						if (bitmap != null) {
							img_photo.setImageBitmap(bitmap);
						}
					}
				}, 1920, 1080);
			}
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
	protected void onDestroy() {
		super.onDestroy();
		imageLoader.cancelTask();
		imageLoader.clearCache();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
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
}
