package com.advertisement.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher.ViewFactory;

import com.advertisement.system.ConstData;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.FTPManager;
import com.yy.doorplate.database.CarouselDatabase;
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.database.PicDreOrActivityDatabase;
import com.yy.doorplate.database.PlayTaskDatabase;
import com.yy.doorplate.database.ScreensaverDatabase;
import com.yy.doorplate.model.CarouselModel;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.model.PicDreOrActivityModel;
import com.yy.doorplate.model.PlayTaskModel;
import com.yy.doorplate.model.ScreensaverModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class DisplayActivity extends Activity {

	private final String TAG = "DisplayActivity";

	private MyApplication application;

	private RelativeLayout ly_display;
	private ImageSwitcher imageSwitcher;

	private ImageLoader imageLoader;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private List<String> pics;
	private int pics_i = 0;
	private List<Map<String, String>> new_pics = null;
	private int new_pics_i = 0;
	private DisplayManager displayManager_media = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		application.handler_touch.removeMessages(0);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BROADCAST);
		filter.addAction(ConstData.ACTIVITY_UPDATE);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		ly_display = (RelativeLayout) findViewById(R.id.ly_display);
		ly_display.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				finish();
			}
		});
		imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
		imageSwitcher.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				ImageView imageView = new ImageView(DisplayActivity.this);
				ImageSwitcher.LayoutParams params = new ImageSwitcher.LayoutParams(
						ImageSwitcher.LayoutParams.MATCH_PARENT,
						ImageSwitcher.LayoutParams.MATCH_PARENT);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setLayoutParams(params);
				return imageView;
			}
		});
		imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.enteralpha));
		imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.exitalpha));
		imageLoader = new ImageLoader(application);
		updata_banner();
	}

	private void updata_banner() {
		handler.removeMessages(0);
		handler.removeMessages(1);

		CarouselDatabase carouselDatabase = new CarouselDatabase();
		List<CarouselModel> carouselModels = carouselDatabase.query(
				"modelType = ?", new String[] { CarouselModel.TYPE_SCREEN });
		pics = new ArrayList<String>();
		pics_i = 0;
		new_pics = new ArrayList<Map<String, String>>();
		new_pics_i = 0;
		if (carouselModels != null && carouselModels.size() > 0) {
			CarouselModel model = carouselModels.get(0);
			if (CarouselModel.SOURCE_SX.equals(model.dataSource)) {
				PlayTaskDatabase database = new PlayTaskDatabase();
				List<PlayTaskModel> list = database.query(
						"playType = ? and srcType = ?", new String[] {
								"SCREEN_SAVER", "TEMPLATE" });
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
					ly_display.removeAllViews();
					if (displayManager_media == null) {
						displayManager_media = new DisplayManager(this,
								application, ly_display);
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
				String objectId = database.queryObjectIdbyObjectName(model.photos, "1");
				List<PicDreOrActivityModel> list = database.query("objectType = ? and kind = ? and parentId = ?",
						new String[] { "1", "1", objectId });
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
									imageSwitcher
											.setImageDrawable(new BitmapDrawable(
													bitmap));
								else
									updata_banner_default();
							}
						}, 1920, 1080);
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
		imageLoader.getBitmapFormRes(R.drawable.cx,
				new OnImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						imageSwitcher.setImageDrawable(new BitmapDrawable(
								bitmap));
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

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				imageLoader.getBitmapFormUrl(pics.get(pics_i),
						new OnImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								if (bitmap != null)
									imageSwitcher
											.setImageDrawable(new BitmapDrawable(
													bitmap));
								if (pics_i == pics.size() - 1) {
									pics_i = 0;
								} else {
									pics_i++;
								}
								handler.sendEmptyMessageDelayed(0, 10 * 1000);
							}
						}, 1920, 1080);
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
									imageSwitcher
											.setImageDrawable(new BitmapDrawable(
													bitmap));
							}
						}, 1920, 1080);
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
									imageSwitcher
											.setImageDrawable(new BitmapDrawable(
													bitmap));
								if (new_pics_i == new_pics.size() - 1) {
									new_pics_i = 0;
								} else {
									new_pics_i++;
								}
								handler.sendEmptyMessageDelayed(1, 10 * 1000);
							}
						}, 1920, 1080);
					}
				}
				break;
			}
			}
		};
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeMessages(0);
		handler.removeMessages(1);
		imageLoader.clearCache();
		imageLoader.cancelTask();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
		if (displayManager_media != null) {
			displayManager_media.stop();
			displayManager_media.destroy();
		}
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals("PlayActivity_finish")) {
				finish();
			}
		}
	}
}
