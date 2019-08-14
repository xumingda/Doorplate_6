package com.cx.doorplate.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.advertisement.activity.DisplayManager;
import com.cx.doorplate.activity.CXNoticeDetailsActivity;
import com.cx.doorplate.activity.CXNoticeListActivity;
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
import com.yy.doorplate.ui.AppCodeView;

public class CXMainNewView extends AppCodeView implements OnClickListener {

	private ImageLoader imageLoader;
	private String activity;

	private RelativeLayout ly_main_new1, ly_main_new2, ly_banner,
			ly_xxtz_title, ly_emergency_notice;
	private TextView txt_bjtz_more, txt_main_nonew1, txt_main_new_title1,
			txt_main_new_nr1, txt_main_new_time1, txt_xxtz_more,
			txt_main_nonew2, txt_main_new_title2, txt_main_new_nr2,
			txt_main_new_time2, txt_main_new_title3, txt_main_new_nr3,
			txt_main_new_time3;
	private ImageSwitcher img_main_banner;

	private NoticeInfoModel notice_bjtz = null, notice_xxtz = null,
			notice_jjtz = null;

	private List<String> pics;
	private int pics_i = 0;
	private List<Map<String, String>> new_pics = null;
	private int new_pics_i = 0;
	private DisplayManager displayManager_media = null;

	public CXMainNewView(Context context, MyApplication application,
			String nextPath, ImageLoader imageLoader, String activity) {
		super(context, application, nextPath);
		this.imageLoader = imageLoader;
		this.activity = activity;
		initView();
	}

	@Override
	public void initView() {
		View.inflate(context, R.layout.cx_ly_main_new, this);
		ly_main_new1 = (RelativeLayout) findViewById(R.id.ly_main_new1);
		ly_main_new2 = (RelativeLayout) findViewById(R.id.ly_main_new2);
		ly_banner = (RelativeLayout) findViewById(R.id.ly_banner);
		ly_xxtz_title = (RelativeLayout) findViewById(R.id.ly_xxtz_title);
		ly_emergency_notice = (RelativeLayout) findViewById(R.id.ly_emergency_notice);
		txt_bjtz_more = (TextView) findViewById(R.id.txt_bjtz_more);
		txt_main_nonew1 = (TextView) findViewById(R.id.txt_main_nonew1);
		txt_main_new_title1 = (TextView) findViewById(R.id.txt_main_new_title1);
		txt_main_new_nr1 = (TextView) findViewById(R.id.txt_main_new_nr1);
		txt_main_new_time1 = (TextView) findViewById(R.id.txt_main_new_time1);
		txt_xxtz_more = (TextView) findViewById(R.id.txt_xxtz_more);
		txt_main_nonew2 = (TextView) findViewById(R.id.txt_main_nonew2);
		txt_main_new_title2 = (TextView) findViewById(R.id.txt_main_new_title2);
		txt_main_new_nr2 = (TextView) findViewById(R.id.txt_main_new_nr2);
		txt_main_new_time2 = (TextView) findViewById(R.id.txt_main_new_time2);
		txt_main_new_title3 = (TextView) findViewById(R.id.txt_main_new_title3);
		txt_main_new_nr3 = (TextView) findViewById(R.id.txt_main_new_nr3);
		txt_main_new_time3 = (TextView) findViewById(R.id.txt_main_new_time3);
		img_main_banner = (ImageSwitcher) findViewById(R.id.img_main_banner);

		img_main_banner.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				ImageView imageView = new ImageView(context);
				ImageSwitcher.LayoutParams params = new ImageSwitcher.LayoutParams(
						ImageSwitcher.LayoutParams.MATCH_PARENT,
						ImageSwitcher.LayoutParams.MATCH_PARENT);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setLayoutParams(params);
				return imageView;
			}
		});
		img_main_banner.setInAnimation(AnimationUtils.loadAnimation(context,
				R.anim.enteralpha));
		img_main_banner.setOutAnimation(AnimationUtils.loadAnimation(context,
				R.anim.exitalpha));

		ly_main_new1.setOnClickListener(this);
		ly_main_new2.setOnClickListener(this);
		ly_emergency_notice.setOnClickListener(this);
		txt_bjtz_more.setOnClickListener(this);
		txt_xxtz_more.setOnClickListener(this);
		img_main_banner.setOnClickListener(this);

		updata();
	}

	private void updata() {
		updata_new_notice();
		updata_jjtz();
		updata_banner();
	}

	private void updata_new_notice() {
		NoticeInfoDatabase database = new NoticeInfoDatabase();
		List<NoticeInfoModel> list = database.query_by_lmdm("bpbjtz", 0, 1);
		if (list != null && list.size() == 1) {
			notice_bjtz = list.get(0);
			ly_main_new1.setVisibility(View.VISIBLE);
			txt_main_nonew1.setVisibility(View.INVISIBLE);
			txt_main_new_title1.setText(notice_bjtz.xxzt);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			txt_main_new_time1.setText(formatter.format(Long
					.parseLong(notice_bjtz.fbsj)));
			txt_main_new_nr1.setText("");
			if (!TextUtils.isEmpty(notice_bjtz.xxnr)) {
				Document document = Jsoup.parse(notice_bjtz.xxnr);
				Elements p = document.select("p");
				if (p != null && p.size() > 0) {
					String new_rl_1 = "";
					for (Element e : p) {
						if (TextUtils.isEmpty(new_rl_1)) {
							new_rl_1 = e.text();
						} else {
							new_rl_1 = new_rl_1 + "\n" + e.text();
						}
						if (!TextUtils.isEmpty(new_rl_1)
								&& new_rl_1.length() > 100) {
							break;
						}
					}
					txt_main_new_nr1.setText(new_rl_1);
				}
			}
		} else {
			ly_main_new1.setVisibility(View.INVISIBLE);
			txt_main_nonew1.setVisibility(View.VISIBLE);
		}

		list = database.query_by_lmdm("bpxxtz", 0, 1);
		if (list != null && list.size() == 1) {
			notice_xxtz = list.get(0);
			ly_main_new2.setVisibility(View.VISIBLE);
			txt_main_nonew2.setVisibility(View.INVISIBLE);
			txt_main_new_title2.setText(notice_xxtz.xxzt);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			txt_main_new_time2.setText(formatter.format(Long
					.parseLong(notice_xxtz.fbsj)));
			txt_main_new_nr2.setText("");
			if (!TextUtils.isEmpty(notice_xxtz.xxnr)) {
				Document document = Jsoup.parse(notice_xxtz.xxnr);
				Elements p = document.select("p");
				if (p != null && p.size() > 0) {
					String new_rl_1 = "";
					for (Element e : p) {
						if (TextUtils.isEmpty(new_rl_1)) {
							new_rl_1 = e.text();
						} else {
							new_rl_1 = new_rl_1 + "\n" + e.text();
						}
						if (!TextUtils.isEmpty(new_rl_1)
								&& new_rl_1.length() > 100) {
							break;
						}
					}
					txt_main_new_nr2.setText(new_rl_1);
				}
			}
		} else {
			ly_main_new2.setVisibility(View.INVISIBLE);
			txt_main_nonew2.setVisibility(View.VISIBLE);
		}
	}

	private void updata_jjtz() {
		notice_jjtz = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		NoticeInfoDatabase database = new NoticeInfoDatabase();
		List<NoticeInfoModel> list = database.query_by_lmdm("bpjjtz", 0, 1);
		if (list != null && list.size() == 1) {
			try {
				NoticeInfoModel model = list.get(0);
				long now = System.currentTimeMillis();
				long star = Long.parseLong(model.fbsj);
				long end = Long.parseLong(model.endTime);
				if (star <= now && now < end) {
					notice_jjtz = model;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (notice_jjtz == null) {
			ly_xxtz_title.setVisibility(View.VISIBLE);
			// ly_main_new2.setVisibility(View.VISIBLE);
			ly_emergency_notice.setVisibility(View.INVISIBLE);
		} else {
			ly_xxtz_title.setVisibility(View.INVISIBLE);
			// ly_main_new2.setVisibility(View.INVISIBLE);
			ly_emergency_notice.setVisibility(View.VISIBLE);

			txt_main_new_title3.setText(notice_jjtz.xxzt);
			format = new SimpleDateFormat("yyyy-MM-dd");
			txt_main_new_time3.setText(format.format(Long
					.parseLong(notice_jjtz.fbsj)));
			txt_main_new_nr3.setText("");
			if (!TextUtils.isEmpty(notice_jjtz.xxnr)) {
				Document document = Jsoup.parse(notice_jjtz.xxnr);
				Elements p = document.select("p");
				if (p != null && p.size() > 0) {
					String new_rl_1 = "";
					for (Element e : p) {
						if (TextUtils.isEmpty(new_rl_1)) {
							new_rl_1 = e.text();
						} else {
							new_rl_1 = new_rl_1 + "\n" + e.text();
						}
						if (!TextUtils.isEmpty(new_rl_1)
								&& new_rl_1.length() > 100) {
							break;
						}
					}
					txt_main_new_nr3.setText(new_rl_1);
				}
			}
		}
	}

	private void updata_banner() {
		handler.removeMessages(3);
		handler.removeMessages(4);
		ly_banner.removeAllViews();
		ly_banner.setVisibility(View.INVISIBLE);
		img_main_banner.setVisibility(View.VISIBLE);

		CarouselDatabase carouselDatabase = new CarouselDatabase();
		List<CarouselModel> carouselModels = carouselDatabase.query(
				"modelType = ?", new String[] { CarouselModel.TYPE_MAIN });
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
						new String[] { "REGION_MEDIA", "01", "TEMPLATE" });
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
					ly_banner.removeAllViews();
					ly_banner.setVisibility(View.VISIBLE);
					img_main_banner.setVisibility(View.INVISIBLE);
					if (displayManager_media == null) {
						displayManager_media = new DisplayManager(context,
								application, ly_banner);
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
									img_main_banner
											.setImageDrawable(new BitmapDrawable(
													bitmap));
								else
									updata_banner_default();
							}
						}, 900, 300);
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
					handler.sendEmptyMessage(3);
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
				handler.sendEmptyMessage(4);
			} else {
				updata_banner_default();
			}
		} else {
			updata_banner_default();
		}
	}

	private void updata_banner_default() {
		imageLoader.getBitmapFormRes(R.drawable.cx_ly_banner,
				new OnImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						img_main_banner.setImageDrawable(new BitmapDrawable(
								bitmap));
					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ly_main_new1: {
			Intent intent = new Intent(context, CXNoticeDetailsActivity.class);
			// intent.putExtra("top", "班级通知");
			intent.putExtra("from", "CXMainActivity");
			intent.putExtra("type", 2);
			intent.putExtra("id", notice_bjtz.id);
			context.startActivity(intent);
			break;
		}
		case R.id.ly_main_new2: {
			if (ly_emergency_notice.getVisibility() != View.VISIBLE) {
				Intent intent = new Intent(context,
						CXNoticeDetailsActivity.class);
				// intent.putExtra("top", "学校通知");
				intent.putExtra("from", "CXMainActivity");
				intent.putExtra("type", 1);
				intent.putExtra("id", notice_xxtz.id);
				context.startActivity(intent);
			}
			break;
		}
		case R.id.ly_emergency_notice: {
			if (notice_jjtz != null) {
				Intent intent = new Intent(context,
						CXNoticeDetailsActivity.class);
				// intent.putExtra("top", "学校通知");
				intent.putExtra("from", "CXMainActivity");
				intent.putExtra("type", 6);
				intent.putExtra("id", notice_jjtz.id);
				context.startActivity(intent);
			}
			break;
		}
		case R.id.txt_bjtz_more: {
			Intent intent = new Intent(context, CXNoticeListActivity.class);
			intent.putExtra("type", 2);
			context.startActivity(intent);
			break;
		}
		case R.id.txt_xxtz_more: {
			Intent intent = new Intent(context, CXNoticeListActivity.class);
			intent.putExtra("type", 1);
			context.startActivity(intent);
			break;
		}
		case R.id.img_main_banner: {
			if (new_pics != null && new_pics.size() > 0) {
				int i;
				if (new_pics_i == 0) {
					i = new_pics.size() - 1;
				} else {
					i = new_pics_i - 1;
				}
				CarouselDatabase carouselDatabase = new CarouselDatabase();
				List<CarouselModel> carouselModels = carouselDatabase.query(
						"modelType = ?",
						new String[] { CarouselModel.TYPE_MAIN });
				if (carouselModels != null && carouselModels.size() > 0) {
					CarouselModel model = carouselModels.get(0);
					Intent intent = new Intent(context,
							CXNoticeDetailsActivity.class);
					intent.putExtra("from", "CXjMainActivity");
					if (model.dataSource
							.equals(CarouselModel.SOURCE_CLASS_NEWS)) {
						intent.putExtra("type", 4);
					} else if (model.dataSource
							.equals(CarouselModel.SOURCE_SCHOOL_NEWS)) {
						intent.putExtra("type", 3);
					}
					intent.putExtra("id", new_pics.get(i).get("id"));
					context.startActivity(intent);
				}
			}
			break;
		}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 3: {
				imageLoader.getBitmapFormUrl(pics.get(pics_i),
						new OnImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								if (bitmap != null)
									img_main_banner
											.setImageDrawable(new BitmapDrawable(
													bitmap));
								if (pics_i == pics.size() - 1) {
									pics_i = 0;
								} else {
									pics_i++;
								}
								handler.sendEmptyMessageDelayed(3, 5 * 1000);
							}
						}, 900, 300);
				break;
			}
			case 4: {
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
									img_main_banner
											.setImageDrawable(new BitmapDrawable(
													bitmap));
							}
						}, 900, 300);
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
									img_main_banner
											.setImageDrawable(new BitmapDrawable(
													bitmap));
								if (new_pics_i == new_pics.size() - 1) {
									new_pics_i = 0;
								} else {
									new_pics_i++;
								}
								handler.sendEmptyMessageDelayed(4, 5 * 1000);
							}
						}, 900, 300);
					}
				}
				break;
			}
			}
		}
	};

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
							handler.sendEmptyMessage(4);
						} else {
							if (new_pics.size() > new_pics_i) {
								new_pics.remove(new_pics_i);
								new_pics_i = 0;
								handler.sendEmptyMessage(4);
							}
						}
					} else {
						if (new_pics.size() > new_pics_i) {
							new_pics.remove(new_pics_i);
							new_pics_i = 0;
							handler.sendEmptyMessage(4);
						}
					}
				} else {
					if (application.httpDownLoad(urlStr, path)) {
						handler.sendEmptyMessage(4);
					} else {
						if (new_pics.size() > new_pics_i) {
							new_pics.remove(new_pics_i);
							new_pics_i = 0;
							handler.sendEmptyMessage(4);
						}
					}
				}
			}
		});
	}

	@Override
	public void update(int type) {
		// TODO 自动生成的方法存根
		if (type == APPCODE_UPDATA_NOTIC || type == APPCODE_UPDATA_EQU) {
			updata();
		} else if (type == APPCODE_UPDATA_REGION_MEDIA) {
			updata_banner();
		}
	}

	@Override
	public void pause() {
		// TODO 自动生成的方法存根
		handler.removeMessages(3);
		handler.removeMessages(4);
	}

	@Override
	public void resume() {
		// TODO 自动生成的方法存根
		updata_banner();
	}

	@Override
	public void stop() {
		// TODO 自动生成的方法存根
		handler.removeMessages(3);
		handler.removeMessages(4);
		new_pics.clear();
		new_pics_i = 0;
		if (displayManager_media != null) {
			displayManager_media.stop();
		}
	}

	@Override
	public void destroy() {
		// TODO 自动生成的方法存根
		handler.removeMessages(3);
		handler.removeMessages(4);
		if (displayManager_media != null) {
			displayManager_media.stop();
			displayManager_media.destroy();
		}
	}
}
