package com.cx.doorplate.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.advertisement.activity.DisplayManager;
import com.cx.doorplate.fragment.StudentFragment;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.adapter.MyViewPagerAdapter;
import com.yy.doorplate.communication.FTPManager;
import com.yy.doorplate.database.CarouselDatabase;
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.database.PicDreOrActivityDatabase;
import com.yy.doorplate.database.PlayTaskDatabase;
import com.yy.doorplate.database.ScreensaverDatabase;
import com.yy.doorplate.database.StudentInfoDatabase;
import com.yy.doorplate.model.CarouselModel;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.model.PicDreOrActivityModel;
import com.yy.doorplate.model.PlayTaskModel;
import com.yy.doorplate.model.ScreensaverModel;
import com.yy.doorplate.model.StudentInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;
import com.yy.doorplate.view.CustomTextView;
import com.yy.doorplate.view.CustomViewpager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CXClassNewFullScreenActivity extends FragmentActivity implements
		OnClickListener {

	private final String TAG = "CXClassNewActivity";

	private MyApplication application;

	private RelativeLayout ly_classnew_banner;
	private Button btn_classnew_back;
	private TextView txt_weather;
	private CustomTextView txt_main_class;
	private TextClock textClock;
	private ImageSwitcher img_classnew_banner;

	private ImageLoader imageLoader = null;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private List<String> pics;
	private int pics_i = 0;
	private List<Map<String, String>> new_pics = null;
	private int new_pics_i = 0;
	private DisplayManager displayManager_media = null;

	private List<StudentInfoModel> studentInfoModels = null;
	private List<Fragment> fragments;
	private int fragment_total, fragment_i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_activity_classnew_full);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		imageLoader = new ImageLoader(application);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initView();
	}

	private void initView() {
		ly_classnew_banner = (RelativeLayout) findViewById(R.id.ly_classnew_banner);
		btn_classnew_back = (Button) findViewById(R.id.btn_classnew_back);

		txt_weather = (TextView) findViewById(R.id.txt_weather);
		txt_main_class = (CustomTextView) findViewById(R.id.txt_main_class);
		img_classnew_banner = (ImageSwitcher) findViewById(R.id.img_classnew_banner);

		textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("yyyyƒÍMM‘¬dd»’\nEEEE      HH : mm");

		img_classnew_banner.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				ImageView imageView = new ImageView(CXClassNewFullScreenActivity.this);
				ImageSwitcher.LayoutParams params = new ImageSwitcher.LayoutParams(
						ImageSwitcher.LayoutParams.MATCH_PARENT,
						ImageSwitcher.LayoutParams.MATCH_PARENT);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setLayoutParams(params);
				return imageView;
			}
		});
		img_classnew_banner.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.enteralpha));
		img_classnew_banner.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.exitalpha));

		btn_classnew_back.setOnClickListener(this);




		if (application.classInfoModel != null) {
			txt_main_class.setText(application.classInfoModel.bjmc);
		}
		txt_weather.setText(application.currentCity.trim() + "   "
				+ application.temperature.trim() + "\n"
				+ application.weather.trim());

	}



	private void updata_banner() {
		handler.removeMessages(0);
		handler.removeMessages(1);
		ly_classnew_banner.removeAllViews();
		ly_classnew_banner.setVisibility(View.INVISIBLE);
		img_classnew_banner.setVisibility(View.VISIBLE);

		CarouselDatabase carouselDatabase = new CarouselDatabase();
		List<CarouselModel> carouselModels = carouselDatabase.query(
				"modelType = ?", new String[] { CarouselModel.TYPE_CLASS });
		pics = new ArrayList<String>();
		pics_i = 0;
		new_pics = new ArrayList<Map<String, String>>();
		new_pics_i = 0;
		if (carouselModels != null && carouselModels.size() > 0) {
			CarouselModel model = carouselModels.get(0);
			if (CarouselModel.SOURCE_SX.equals(model.dataSource)) {
				PlayTaskDatabase database = new PlayTaskDatabase();
				List<PlayTaskModel> list = database.query(
						"playType = ? and position = ? and srcType = ?",
						new String[] { "REGION_MEDIA", "03", "TEMPLATE" });
				String policyPath = null;
				if (list != null && list.size() > 0) {
					if (TextUtils.isEmpty(list.get(0).srcPath)) {
						File file = new File(
								application.getFtpPath(list.get(0).srcPath));
						if (file.exists()) {
							policyPath = application
									.getFtpPath(list.get(0).srcPath);
						}
					}
				}
				if (!TextUtils.isEmpty(policyPath)) {
					ly_classnew_banner.removeAllViews();
					ly_classnew_banner.setVisibility(View.VISIBLE);
					img_classnew_banner.setVisibility(View.INVISIBLE);
					if (displayManager_media == null) {
						displayManager_media = new DisplayManager(this,
								application, ly_classnew_banner);
						displayManager_media.start(policyPath);
					} else {
						displayManager_media.stop();
						displayManager_media.destroy();
						displayManager_media.start(policyPath);
					}
				} else {
					updata_banner_default();
				}
			} else if (CarouselModel.SOURCE_CLASS_PHOTO
					.equals(model.dataSource)) {
				PicDreOrActivityDatabase database = new PicDreOrActivityDatabase();
				List<PicDreOrActivityModel> list = database.query(
						"objectType = ? and kind = ?",
						new String[] { "1", "2" });
				if (list != null && list.size() > 0) {
					for (PicDreOrActivityModel picModel : list) {
						if (!TextUtils.isEmpty(picModel.objectEntityAddress)) {
							String[] s = picModel.objectEntityAddress
									.split("/");
							String path = MyApplication.PATH_ROOT
									+ MyApplication.PATH_PHOTO + "/"
									+ s[s.length - 1];
							pics.add(path);
						}
					}
				}
				updata_banner_pic();
			} else if (CarouselModel.SOURCE_SCHOOL_PHOTO
					.equals(model.dataSource)) {
				PicDreOrActivityDatabase database = new PicDreOrActivityDatabase();
				List<PicDreOrActivityModel> list = database.query(
						"objectType = ? and kind = ?",
						new String[] { "1", "1" });
				if (list != null && list.size() > 0) {
					for (PicDreOrActivityModel picModel : list) {
						if (!TextUtils.isEmpty(picModel.objectEntityAddress)) {
							String[] s = picModel.objectEntityAddress
									.split("/");
							String path = MyApplication.PATH_ROOT
									+ MyApplication.PATH_PHOTO + "/"
									+ s[s.length - 1];
							pics.add(path);
						}
					}
				}
				updata_banner_pic();
			} else if (CarouselModel.SOURCE_CLASS_NEWS.equals(model.dataSource)) {
				updata_banner_notice("bpbjxw");
			} else if (CarouselModel.SOURCE_SCHOOL_NEWS
					.equals(model.dataSource)) {
				updata_banner_notice("bpxxxw");
			} else if (CarouselModel.SOURCE_CXXXT.equals(model.dataSource)) {
				ScreensaverDatabase database = new ScreensaverDatabase();
				List<ScreensaverModel> list = database.query_all();
				if (list != null && list.size() > 0) {
					for (ScreensaverModel screensaverModel : list) {
						if (!TextUtils.isEmpty(screensaverModel.url)) {
							String[] s = screensaverModel.url.split("/");
							String path = MyApplication.PATH_ROOT
									+ MyApplication.PATH_SCREENSAVER + "/"
									+ s[s.length - 1];
							pics.add(path);
						}
					}
				}
				updata_banner_pic();
			} else {
				updata_banner_default();
			}
		} else {
			updata_banner_default();
		}
	}

	private void updata_banner_pic() {
		if (pics != null && pics.size() > 0) {
			if (pics.size() == 1) {
				imageLoader.getBitmapFormUrl(pics.get(0),
						new OnImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								if (bitmap != null)
									img_classnew_banner
											.setImageDrawable(new BitmapDrawable(
													bitmap));
								else
									updata_banner_default();
							}
						}, 732, 302);
			} else if (pics.size() > 1) {
				boolean b = false;
				for (String s : pics) {
					File file = new File(s);
					if (file.exists()) {
						b = true;
						break;
					}
				}
				if (b) {
					handler.sendEmptyMessage(0);
				} else {
					updata_banner_default();
				}
			}
		} else {
			updata_banner_default();
		}
	}

	private void updata_banner_notice(String lmdm) {
		NoticeInfoDatabase database = new NoticeInfoDatabase();
		List<NoticeInfoModel> models = database.query_by_lmdm(lmdm);
		if (models != null && models.size() > 0) {
			for (NoticeInfoModel model : models) {
				if (!TextUtils.isEmpty(model.xxnr)) {
					Document document = Jsoup.parse(model.xxnr);
					Element element = document.select("img").first();
					if (element != null) {
						String src = element.attr("src");
						if (!TextUtils.isEmpty(src)) {
							String[] s = src.split("/");
							String path = MyApplication.PATH_ROOT
									+ MyApplication.PATH_NOTICE + "/"
									+ s[s.length - 1];
							Map<String, String> map = new HashMap<String, String>();
							map.put("url", src);
							map.put("path", path);
							map.put("id", model.id);
							new_pics.add(map);
						}
					}
				}
				if (new_pics.size() >= 5) {
					break;
				}
			}
			if (new_pics != null && new_pics.size() > 0) {
				handler.sendEmptyMessage(1);
			} else {
				updata_banner_default();
			}
		} else {
			updata_banner_default();
		}
	}

	private void updata_banner_default() {
		imageLoader.getBitmapFormRes(R.drawable.cx_ly_classnew_banner,
				new OnImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						img_classnew_banner
								.setImageDrawable(new BitmapDrawable(bitmap));
					}
				});
	}

	private void downLoadImg(final String urlStr, final String path) {
		if (TextUtils.isEmpty(urlStr)) {
			return;
		}
		final File file = new File(path);
		if (file.exists()) {
			return;
		}
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				if (urlStr.startsWith("ftp")) {
					FTPManager ftpManager = new FTPManager();
					if (ftpManager.connect(urlStr, path)) {
						if (ftpManager.download()) {
							handler.sendEmptyMessage(1);
						} else {
							if (new_pics.size() > new_pics_i) {
								new_pics.remove(new_pics_i);
								new_pics_i = 0;
								handler.sendEmptyMessage(1);
							}
						}
					} else {
						if (new_pics.size() > new_pics_i) {
							new_pics.remove(new_pics_i);
							new_pics_i = 0;
							handler.sendEmptyMessage(1);
						}
					}
				} else {
					if (application.httpDownLoad(urlStr, path)) {
						handler.sendEmptyMessage(1);
					} else {
						if (new_pics.size() > new_pics_i) {
							new_pics.remove(new_pics_i);
							new_pics_i = 0;
							handler.sendEmptyMessage(1);
						}
					}
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_classnew_back:
			finish();
			break;
		case R.id.btn_classnew_honor: {
			Intent intent = new Intent(this, CXHonorActivity.class);
			intent.putExtra("type", 2);
			startActivity(intent);
			break;
		}
		case R.id.btn_classnew_star: {
			Intent intent = new Intent(this, CXHonorActivity.class);
			intent.putExtra("type", 3);
			startActivity(intent);
			break;
		}
		case R.id.btn_classnew_photo: {
			Intent intent = new Intent(this, CXPhotoActivity.class);
			intent.putExtra("type", 2);
			startActivity(intent);
			break;
		}
		case R.id.btn_classnew_grxx: {
			Intent intent = new Intent(this, CXGrxxActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.btn_classnew_activity: {
			Intent intent = new Intent(this, CXVoteActivity.class);
			intent.putExtra("type", 2);
			startActivity(intent);
			break;
		}
		case R.id.btn_classnew_more: {
			Intent intent = new Intent(this, CXAppActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.btn_classnew_notice: {
			Intent intent = new Intent(this, CXNoticeListActivity.class);
			intent.putExtra("type", 2);
			startActivity(intent);
			break;
		}
		case R.id.btn_classnew_news: {
			Intent intent = new Intent(this, CXNoticeListActivity.class);
			intent.putExtra("type", 4);
			startActivity(intent);
			break;
		}
		case R.id.btn_bjcy_all: {
			Intent intent = new Intent(this, CXStudentActivity.class);
			startActivity(intent);
			break;
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
			if (tag.equals("updataEQ")) {
				if (application.classInfoModel != null) {
					txt_main_class.setText(application.classInfoModel.bjmc);
				}
				txt_weather.setText(application.currentCity.trim() + "   "
						+ application.temperature.trim() + "\n"
						+ application.weather.trim());
			} else if (tag.equals("playTask")) {
				Log.d(TAG, "------playTask------");
				updata_banner();
			} else if (tag.equals("permission_finish")) {
				finish();
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				imageLoader.getBitmapFormUrl(pics.get(pics_i),
						new OnImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								if (bitmap != null)
									img_classnew_banner
											.setImageDrawable(new BitmapDrawable(
													bitmap));
								if (pics_i == pics.size() - 1) {
									pics_i = 0;
								} else {
									pics_i++;
								}
								handler.sendEmptyMessageDelayed(0, 5 * 1000);
							}
						}, 732, 302);
				break;
			}
			case 1: {
				if (new_pics.size() == 1) {
					File file = new File(new_pics.get(0).get("path"));
					if (!file.exists()) {
						downLoadImg(new_pics.get(0).get("url"), new_pics.get(0)
								.get("path"));
					} else {
						imageLoader.getBitmapFormUrl(new_pics.get(0)
								.get("path"), new OnImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								if (bitmap != null)
									img_classnew_banner
											.setImageDrawable(new BitmapDrawable(
													bitmap));
							}
						}, 732, 302);
					}
				} else if (new_pics.size() > 1) {
					File file = new File(new_pics.get(new_pics_i).get("path"));
					if (!file.exists()) {
						downLoadImg(new_pics.get(new_pics_i).get("url"),
								new_pics.get(new_pics_i).get("path"));
					} else {
						imageLoader.getBitmapFormUrl(new_pics.get(new_pics_i)
								.get("path"), new OnImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								if (bitmap != null)
									img_classnew_banner
											.setImageDrawable(new BitmapDrawable(
													bitmap));
								if (new_pics_i == new_pics.size() - 1) {
									new_pics_i = 0;
								} else {
									new_pics_i++;
								}
								handler.sendEmptyMessageDelayed(1, 5 * 1000);
							}
						}, 732, 302);
					}
				}
				break;
			}
			}
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		updata_banner();
		application.handler_touch.removeMessages(0);
		application.handler_touch.sendEmptyMessageDelayed(0,
				application.screensaver_time * 1000);
	}

	@Override
	protected void onStop() {
		super.onStop();
		handler.removeMessages(0);
		handler.removeMessages(1);
		if (displayManager_media != null) {
			displayManager_media.stop();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageLoader.cancelTask();
		imageLoader.clearCache();
		handler.removeMessages(0);
		handler.removeMessages(1);
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
		if (displayManager_media != null) {
			displayManager_media.stop();
			displayManager_media.destroy();
		}
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
