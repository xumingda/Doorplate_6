package com.yy.doorplate.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.advertisement.activity.DisplayManager;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.FTPManager;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.BlackboardInfoDatabase;
import com.yy.doorplate.database.CurriculumInfoDatabase;
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.database.OnDutyInfoDatabase;
import com.yy.doorplate.database.PageLayoutDatabase;
import com.yy.doorplate.database.PrizeInfoDatabase;
import com.yy.doorplate.database.SectionInfoDatabase;
import com.yy.doorplate.database.VideoDatabase;
import com.yy.doorplate.model.BlackboardInfoModel;
import com.yy.doorplate.model.CurriculumInfoModel;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.model.OnDutyInfoModel;
import com.yy.doorplate.model.PageLayoutModel;
import com.yy.doorplate.model.PlayTaskModel;
import com.yy.doorplate.model.PrizeInfoModel;
import com.yy.doorplate.model.SectionInfoModel;
import com.yy.doorplate.model.VideoInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;
import com.yy.doorplate.ui.AppCodeView;
import com.yy.doorplate.view.CustomTextView;

public class ClassNewActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private final String TAG = "ClassNewActivity";

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private LinearLayout ly_classnew_zhiri;
	private RelativeLayout ly_classnew, ly_classnew_honor_,
			ly_classnew_info_right, ly_classnew_attend, ly_classnew_new_,
			ly_classnew_banner;
	private Button btn_classnew_back, btn_classnew_left, btn_classnew_right;
	private TextView txt_classnenw_rl, txt_classnenw_time, txt_classnew_nonews,
			txt_classnew_honor1, txt_classnew_honor2, txt_classnew_nohonor,
			txt_classnew_english, txt_classnew_english_title, txt_classnew_bzr,
			txt_classnew_zrcm, txt_classnew_zr, txt_classnew_kq1,
			txt_classnew_kq2, txt_classnew_kq3, txt_classnew_kq4,
			txt_classnew_kq5, txt_classnew_kq6, txt_classnew_noke1,
			txt_classnew_noke2, txt_classnew_ke1, txt_classnew_ke2,
			txt_classnew_ke3, txt_classnew_ke4, txt_classnew_novideo;
	private CustomTextView txt_classnenw_title, txt_classnew_bjmc,
			txt_classnew_bzrcm;
	private ImageView img_classnew_honor, img_class_new;
	private ListView list_classnew_new;
	private SurfaceView sv_classnew, sv_video;
	private ImageSwitcher img_classnew_banner;
	private ImageLoader imageLoader = null;

	private List<NoticeInfoModel> noticeInfoModels = null;
	private List<PrizeInfoModel> prizeInfoModels = null;
	private List<OnDutyInfoModel> dutyInfoModels = null;
	private List<CurriculumInfoModel> today, tomorrow;

	private int prize = 0;

	private SurfaceHolder surfaceHolder = null, surfaceHolder_video = null;
	private PlayTaskModel region_media_video = null;
	private List<PlayTaskModel> region_media_img = null;
	private int region_media_img_i = 0;

	private String new_rl_1, new_url_1, new_path_1;

	private DisplayManager displayManager = null;
	private DisplayManager displayManager_media = null;

	private List<VideoInfoModel> vedioInfoModels = null;
	private int vedioInfoModels_i = 0;
	public MediaPlayer mediaPlayer = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_classnew);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();
		broadcastManager = LocalBroadcastManager.getInstance(this);

		imageLoader = new ImageLoader(application);

		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		ly_classnew = (RelativeLayout) findViewById(R.id.ly_classnew);
		String ThemePolicyPath = getIntent().getStringExtra("ThemePolicyPath");
		PageLayoutDatabase database = new PageLayoutDatabase();
		PageLayoutModel model = database.query_by_pageCode("CLASS");
		if (model == null && TextUtils.isEmpty(ThemePolicyPath)) {
			initView();
		} else if (!TextUtils.isEmpty(ThemePolicyPath)) {
			ly_classnew.removeAllViews();
			displayManager = new DisplayManager(this, application, ly_classnew);
			displayManager.setImageLoader(imageLoader);
			displayManager
					.setActivity("com.yy.doorplate.activity.ClassNewActivity");
			displayManager.setActivity(this);
			displayManager.start(ThemePolicyPath);
		} else if (model != null) {
			ly_classnew.removeAllViews();
			displayManager = new DisplayManager(this, application, ly_classnew);
			displayManager.setImageLoader(imageLoader);
			displayManager
					.setActivity("com.yy.doorplate.activity.ClassNewActivity");
			displayManager.setActivity(this);
			displayManager.start(application.getFtpPath(model.resPath));
		}
	}

	private void initView() {
		ly_classnew_new_ = (RelativeLayout) findViewById(R.id.ly_classnew_new_);
		ly_classnew_zhiri = (LinearLayout) findViewById(R.id.ly_classnew_zhiri);
		ly_classnew_honor_ = (RelativeLayout) findViewById(R.id.ly_classnew_honor_);
		ly_classnew_info_right = (RelativeLayout) findViewById(R.id.ly_classnew_info_right);
		ly_classnew_attend = (RelativeLayout) findViewById(R.id.ly_classnew_attend);
		ly_classnew_banner = (RelativeLayout) findViewById(R.id.ly_classnew_banner);
		btn_classnew_back = (Button) findViewById(R.id.btn_classnew_back);
		btn_classnew_left = (Button) findViewById(R.id.btn_classnew_left);
		btn_classnew_right = (Button) findViewById(R.id.btn_classnew_right);
		txt_classnenw_rl = (TextView) findViewById(R.id.txt_classnenw_rl);
		txt_classnenw_time = (TextView) findViewById(R.id.txt_classnenw_time);
		txt_classnew_nonews = (TextView) findViewById(R.id.txt_classnew_nonews);
		txt_classnew_honor1 = (TextView) findViewById(R.id.txt_classnew_honor1);
		txt_classnew_honor2 = (TextView) findViewById(R.id.txt_classnew_honor2);
		txt_classnew_nohonor = (TextView) findViewById(R.id.txt_classnew_nohonor);
		txt_classnew_english = (TextView) findViewById(R.id.txt_classnew_english);
		txt_classnew_english_title = (TextView) findViewById(R.id.txt_classnew_english_title);
		txt_classnew_bzrcm = (CustomTextView) findViewById(R.id.txt_classnew_bzrcm);
		txt_classnew_bzr = (TextView) findViewById(R.id.txt_classnew_bzr);
		txt_classnew_zrcm = (TextView) findViewById(R.id.txt_classnew_zrcm);
		txt_classnew_zr = (TextView) findViewById(R.id.txt_classnew_zr);
		txt_classnew_kq1 = (TextView) findViewById(R.id.txt_classnew_kq1);
		txt_classnew_kq2 = (TextView) findViewById(R.id.txt_classnew_kq2);
		txt_classnew_kq3 = (TextView) findViewById(R.id.txt_classnew_kq3);
		txt_classnew_kq4 = (TextView) findViewById(R.id.txt_classnew_kq4);
		txt_classnew_kq5 = (TextView) findViewById(R.id.txt_classnew_kq5);
		txt_classnew_kq6 = (TextView) findViewById(R.id.txt_classnew_kq6);
		txt_classnew_noke1 = (TextView) findViewById(R.id.txt_classnew_noke1);
		txt_classnew_noke2 = (TextView) findViewById(R.id.txt_classnew_noke2);
		txt_classnew_ke1 = (TextView) findViewById(R.id.txt_classnew_ke1);
		txt_classnew_ke2 = (TextView) findViewById(R.id.txt_classnew_ke2);
		txt_classnew_ke3 = (TextView) findViewById(R.id.txt_classnew_ke3);
		txt_classnew_ke4 = (TextView) findViewById(R.id.txt_classnew_ke4);
		txt_classnenw_title = (CustomTextView) findViewById(R.id.txt_classnenw_title);
		txt_classnew_bjmc = (CustomTextView) findViewById(R.id.txt_classnew_bjmc);
		img_classnew_honor = (ImageView) findViewById(R.id.img_classnew_honor);
		list_classnew_new = (ListView) findViewById(R.id.list_classnew_new);
		sv_classnew = (SurfaceView) findViewById(R.id.sv_classnew);
		img_classnew_banner = (ImageSwitcher) findViewById(R.id.img_classnew_banner);
		img_class_new = (ImageView) findViewById(R.id.img_class_new);

		img_classnew_banner.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				ImageView imageView = new ImageView(ClassNewActivity.this);
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

		ly_classnew_new_.setOnClickListener(this);
		ly_classnew_zhiri.setOnClickListener(this);
		ly_classnew_honor_.setOnClickListener(this);
		ly_classnew_info_right.setOnClickListener(this);
		btn_classnew_back.setOnClickListener(this);
		btn_classnew_left.setOnClickListener(this);
		btn_classnew_right.setOnClickListener(this);
		ly_classnew_attend.setOnClickListener(this);
		list_classnew_new.setOnItemClickListener(this);

		surfaceHolder = sv_classnew.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.setKeepScreenOn(true);
		surfaceHolder.addCallback(new surFaceView());

		if (application.classInfoModel != null) {
			txt_classnew_bjmc.setText(application.classInfoModel.bjmc);
			txt_classnew_bzrcm.setText(application.classInfoModel.fdyxm);
			txt_classnew_bzr.setVisibility(View.VISIBLE);
		} else {
			txt_classnew_bzr.setVisibility(View.INVISIBLE);
		}
		updata_zr();
		updata_attend();
		updata_curriculum();
		updata_news();
		updata_honor();
		updata_english();

		initVideo();
	}

	private void initVideo() {
		txt_classnew_novideo = (TextView) findViewById(R.id.txt_classnew_novideo);
		sv_video = (SurfaceView) findViewById(R.id.sv_video);
		sv_video.setOnClickListener(this);

		surfaceHolder_video = sv_video.getHolder();
		surfaceHolder_video.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder_video.setKeepScreenOn(true);
		surfaceHolder_video.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				handler.removeMessages(7);
				if (mediaPlayer != null) {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.stop();
					}
					mediaPlayer.release();
					mediaPlayer = null;
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				handler.sendEmptyMessageDelayed(7, 3000);
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}
		});

		VideoDatabase database = new VideoDatabase();
		vedioInfoModels = database.query(
				"relaObjType = ? and relaObjValue = ?", new String[] { "bjdm",
						application.equInfoModel.bjdm });
		vedioInfoModels_i = 0;
		if (vedioInfoModels != null && vedioInfoModels.size() > 0) {
			sv_video.setVisibility(View.VISIBLE);
			txt_classnew_novideo.setVisibility(View.INVISIBLE);
		} else {
			sv_video.setVisibility(View.INVISIBLE);
			txt_classnew_novideo.setVisibility(View.VISIBLE);
		}
	}

	private void updata_zr() {
		OnDutyInfoDatabase database = new OnDutyInfoDatabase();
		dutyInfoModels = database.query_by_date(MyApplication
				.getTime("yyyy-MM-dd"));
		if (dutyInfoModels != null && dutyInfoModels.size() > 0) {
			txt_classnew_zrcm.setVisibility(View.VISIBLE);
			txt_classnew_zr.setVisibility(View.VISIBLE);
			String s = "";
			for (int i = 0; i < dutyInfoModels.size(); i++) {
				if (!TextUtils.isEmpty(dutyInfoModels.get(i).xm)) {
					if (i == 0) {
						s = s + dutyInfoModels.get(i).xm;
					} else {
						s = s + "、" + dutyInfoModels.get(i).xm;
					}
				}
			}
			txt_classnew_zrcm.setText(s);
		} else {
			txt_classnew_zrcm.setText("暂无值日");
		}
	}

	private void updata_attend() {
		// TODO
	}

	private void updata_curriculum() {
		long noon = 14400000;
		CurriculumInfoDatabase database = new CurriculumInfoDatabase();
		SectionInfoDatabase sectionInfoDatabase = new SectionInfoDatabase();
		today = database.query_by_skrq(MyApplication.getTime("yyyy-MM-dd"));
		if (today != null && today.size() > 0) {
			String s1 = "上午 :", s2 = "下午 :";
			for (CurriculumInfoModel model : today) {
				String[] js = model.jc.split("-");
				SectionInfoModel sectionInfoModel = sectionInfoDatabase
						.query_by_jcdm(js[0]);
				if (sectionInfoModel != null) {
					if (Long.parseLong(sectionInfoModel.jcskkssj) <= noon) {
						s1 = s1 + " " + model.kcmc;
					} else {
						s2 = s2 + " " + model.kcmc;
					}
				}
			}
			txt_classnew_ke1.setText(s1);
			txt_classnew_ke2.setText(s2);
			txt_classnew_ke1.setVisibility(View.VISIBLE);
			txt_classnew_ke2.setVisibility(View.VISIBLE);
			txt_classnew_noke1.setVisibility(View.INVISIBLE);
		} else {
			txt_classnew_ke1.setVisibility(View.INVISIBLE);
			txt_classnew_ke2.setVisibility(View.INVISIBLE);
			txt_classnew_noke1.setVisibility(View.VISIBLE);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(System.currentTimeMillis()));
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date date = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		tomorrow = database.query_by_skrq(format.format(date));
		if (tomorrow != null && tomorrow.size() > 0) {
			String s1 = "上午 :", s2 = "下午 :";
			for (CurriculumInfoModel model : tomorrow) {
				String[] js = model.jc.split("-");
				SectionInfoModel sectionInfoModel = sectionInfoDatabase
						.query_by_jcdm(js[0]);
				if (sectionInfoModel != null) {
					if (Long.parseLong(sectionInfoModel.jcskkssj) <= noon) {
						s1 = s1 + " " + model.kcmc;
					} else {
						s2 = s2 + " " + model.kcmc;
					}
				}
			}
			txt_classnew_ke3.setText(s1);
			txt_classnew_ke4.setText(s2);
			txt_classnew_ke3.setVisibility(View.VISIBLE);
			txt_classnew_ke4.setVisibility(View.VISIBLE);
			txt_classnew_noke2.setVisibility(View.INVISIBLE);
		} else {
			txt_classnew_ke3.setVisibility(View.INVISIBLE);
			txt_classnew_ke4.setVisibility(View.INVISIBLE);
			txt_classnew_noke2.setVisibility(View.VISIBLE);
		}
	}

	private void updata_news() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		NoticeInfoDatabase database = new NoticeInfoDatabase();
		noticeInfoModels = database.query_by_lmdm("bpbjtz", 0, 4);
		if (noticeInfoModels != null && noticeInfoModels.size() > 0) {
			txt_classnew_nonews.setVisibility(View.INVISIBLE);
			ly_classnew_new_.setVisibility(View.VISIBLE);
			txt_classnenw_title.setText(noticeInfoModels.get(0).xxzt);
			txt_classnenw_time.setText(formatter.format(Long
					.parseLong(noticeInfoModels.get(0).fbsj)));
			txt_classnenw_rl.setText("");
			if (!TextUtils.isEmpty(noticeInfoModels.get(0).xxnr)) {
				Document document = Jsoup.parse(noticeInfoModels.get(0).xxnr);
				Element element = document.select("img").first();
				Elements p = document.select("p");
				if (p != null && p.size() > 0) {
					new_rl_1 = "";
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
					txt_classnenw_rl.setText(new_rl_1);
				}
				if (element != null) {
					new_url_1 = element.attr("src");
					if (!TextUtils.isEmpty(new_url_1)) {
						String[] s = new_url_1.split("/");
						new_path_1 = MyApplication.PATH_ROOT
								+ MyApplication.PATH_NOTICE + "/"
								+ s[s.length - 1];
						File file = new File(new_path_1);
						if (file.exists()) {
							imageLoader.getBitmapFormUrl(new_path_1,
									new OnImageLoaderListener() {
										@Override
										public void onImageLoader(
												Bitmap bitmap, String url) {
											if (bitmap != null) {
												Message msg = Message.obtain();
												msg.what = 1;
												msg.obj = bitmap;
												handler.sendMessage(msg);
											} else {
												handler.sendEmptyMessage(1);
											}
										}
									}, 200, 160);
						} else {
							downLoadImg(new_url_1, new_path_1);
						}
					} else {
						handler.sendEmptyMessage(1);
					}
				} else {
					handler.sendEmptyMessage(1);
				}
			}
			if (noticeInfoModels.size() > 1) {
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				for (int i = 1; i < noticeInfoModels.size(); i++) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("title", noticeInfoModels.get(i).xxzt);
					map.put("time", formatter.format(Long
							.parseLong(noticeInfoModels.get(i).fbsj)));
					list.add(map);
				}
				list_classnew_new.setAdapter(new SimpleAdapter(application,
						list, R.layout.item_schoolnew, new String[] { "title",
								"time" }, new int[] {
								R.id.txt_item_schoolnew_title,
								R.id.txt_item_schoolnew_time }));
			} else {
				list_classnew_new.setAdapter(null);
			}
		} else {
			txt_classnew_nonews.setVisibility(View.VISIBLE);
			ly_classnew_new_.setVisibility(View.INVISIBLE);
			list_classnew_new.setAdapter(null);
		}
	}

	private void updata_honor() {
		prize = 0;
		PrizeInfoDatabase database = new PrizeInfoDatabase();
		prizeInfoModels = database.query_by_prizeType("CLASS");
		if (prizeInfoModels != null && prizeInfoModels.size() > 0) {
			ly_classnew_honor_.setVisibility(View.VISIBLE);
			txt_classnew_nohonor.setVisibility(View.INVISIBLE);
			if (prizeInfoModels.size() > 1) {
				btn_classnew_left.setVisibility(View.VISIBLE);
				btn_classnew_right.setVisibility(View.VISIBLE);
			}
			txt_classnew_honor1.setText(prizeInfoModels.get(0).ranking);
			txt_classnew_honor2.setText(prizeInfoModels.get(0).prizeName);
			if (!TextUtils.isEmpty(prizeInfoModels.get(0).iconUrl)) {
				String[] s = prizeInfoModels.get(0).iconUrl.split("/");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PICTURE + "/prizeIcon_"
						+ s[s.length - 1];
				File file = new File(path);
				if (file.exists()) {
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									img_classnew_honor.setImageBitmap(bitmap);
								}
							}, 160, 160);
				}
			} else {
				img_classnew_honor.setImageBitmap(null);
			}
		} else {
			ly_classnew_honor_.setVisibility(View.INVISIBLE);
			btn_classnew_left.setVisibility(View.INVISIBLE);
			btn_classnew_right.setVisibility(View.INVISIBLE);
			txt_classnew_nohonor.setVisibility(View.VISIBLE);
		}
	}

	private void updata_english() {
		BlackboardInfoDatabase database = new BlackboardInfoDatabase();
		BlackboardInfoModel model = database.query();
		if (model != null) {
			txt_classnew_english.setText(model.content);
			txt_classnew_english_title.setText(model.tagName);
		} else {
			txt_classnew_english.setText("");
			txt_classnew_english_title.setText("");
		}
	}

	private void updata_honor(int i) {
		if (prizeInfoModels != null && prizeInfoModels.size() > i) {
			txt_classnew_honor1.setText(prizeInfoModels.get(i).ranking);
			txt_classnew_honor2.setText(prizeInfoModels.get(i).awardsUnit);
			if (!TextUtils.isEmpty(prizeInfoModels.get(i).iconUrl)) {
				String[] s = prizeInfoModels.get(i).iconUrl.split("/");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PICTURE + "/prizeIcon_"
						+ s[s.length - 1];
				File file = new File(path);
				if (file.exists()) {
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									img_classnew_honor.setImageBitmap(bitmap);
								}
							}, 160, 160);
				} else {
					img_classnew_honor.setImageBitmap(null);
				}
			} else {
				img_classnew_honor.setImageBitmap(null);
			}
		}
	}

	private void updata_banner() {
		region_media_video = null;
		region_media_img = new ArrayList<PlayTaskModel>();
		region_media_img_i = 0;
		handler.removeMessages(4);
		handler.removeMessages(5);
		img_classnew_banner.setVisibility(View.VISIBLE);
		sv_classnew.setVisibility(View.INVISIBLE);
		application.regionIsPlaying = false;
		if (application.mediaPlayer_region != null) {
			application.mediaPlayer_region.stop();
			application.mediaPlayer_region.reset();
		} else {
			application.mediaPlayer_region = new MediaPlayer();
		}

		if (application.playTaskModels != null
				&& application.playTaskModels.size() > 0) {
			for (PlayTaskModel model : application.playTaskModels) {
				if (model.playType.equals("REGION_MEDIA")
						&& model.position.equals("03")
						&& model.srcType.equals("TEMPLATE")) {
					String policyPath = application.getFtpPath(model.srcPath);
					File file = new File(policyPath);
					if (file.exists()) {
						ly_classnew_banner.removeAllViews();
						if (displayManager_media == null) {
							displayManager_media = new DisplayManager(this,
									application, ly_classnew_banner);
							displayManager_media.start(policyPath);
						} else {
							displayManager_media.stop();
							displayManager_media.destroy();
							displayManager_media.start(policyPath);
						}
						return;
					}
				}
			}
			for (PlayTaskModel model : application.playTaskModels) {
				if (model.playType.equals("REGION_MEDIA")
						&& model.position.equals("03")) {
					Log.d(TAG, model.toString());
					if (model.srcType.equals("VIDEO")) {
						Log.d(TAG, model.toString());
						region_media_video = model;
						break;
					} else if (model.srcType.equals("IMAGE")) {
						region_media_img.add(model);
					}
				}
			}
		}
		if (region_media_video != null
				&& !TextUtils.isEmpty(region_media_video.srcPath)) {
			String[] s = region_media_video.srcPath.split("/");
			File file = new File(MyApplication.PATH_ROOT
					+ MyApplication.PATH_PLAYTASK + "/" + s[s.length - 1]);
			if (file.exists()) {
				img_classnew_banner.setVisibility(View.INVISIBLE);
				sv_classnew.setVisibility(View.VISIBLE);
				return;
			}
		}
		if (region_media_img.size() > 0) {
			play_img();
		} else {
			updata_banner_default();
		}
	}

	private void play_img() {
		img_classnew_banner.setVisibility(View.VISIBLE);
		sv_classnew.setVisibility(View.INVISIBLE);
		if (region_media_img.size() == 1) {
			if (!TextUtils.isEmpty(region_media_img.get(0).srcPath)) {
				String[] s = region_media_img.get(0).srcPath.split("/");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PLAYTASK + "/" + s[s.length - 1];
				File file = new File(path);
				if (file.exists()) {
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {
								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null)
										img_classnew_banner
												.setImageDrawable(new BitmapDrawable(
														bitmap));
								}
							}, 671, 416);
				} else {
					updata_banner_default();
				}
			} else {
				updata_banner_default();
			}
		} else if (region_media_img.size() > 1) {
			boolean b = false;
			for (PlayTaskModel model : region_media_img) {
				if (!TextUtils.isEmpty(model.srcPath)) {
					String[] s = model.srcPath.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PLAYTASK + "/"
							+ s[s.length - 1];
					File file = new File(path);
					if (file.exists()) {
						b = true;
						break;
					}
				}
			}
			if (b) {
				handler.sendEmptyMessage(4);
			} else {
				updata_banner_default();
			}
		}
	}

	private void updata_banner_default() {
		img_classnew_banner.setVisibility(View.VISIBLE);
		sv_classnew.setVisibility(View.INVISIBLE);
		imageLoader.getBitmapFormRes(R.drawable.ly_classnew_banner,
				new OnImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						img_classnew_banner
								.setImageDrawable(new BitmapDrawable(bitmap));
					}
				});
	}

	private class surFaceView implements Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Log.d(TAG, "---surfaceCreated---");
			handler.sendEmptyMessageDelayed(5, 3000);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.d(TAG, "---surfaceDestroyed---");
			handler.removeMessages(5);
			application.regionIsPlaying = false;
			application.mediaPlayer_region.stop();
			application.mediaPlayer_region.reset();
		}
	}

	private void mediaPlayer_region_play() {
		if (!application.getRunningActivityName().equals(
				"com.yy.doorplate.activity.ClassNewActivity")
				|| region_media_video == null) {
			return;
		}
		try {
			String[] s = region_media_video.srcPath.split("/");
			String path = MyApplication.PATH_ROOT + MyApplication.PATH_PLAYTASK
					+ "/" + s[s.length - 1];
			application.mediaPlayer_region.stop();
			application.mediaPlayer_region.reset();
			application.mediaPlayer_region
					.setAudioStreamType(AudioManager.STREAM_MUSIC);
			application.mediaPlayer_region.setDataSource(path);
			application.mediaPlayer_region.setDisplay(surfaceHolder);
			application.mediaPlayer_region
					.setOnErrorListener(new OnErrorListener() {

						@Override
						public boolean onError(MediaPlayer arg0, int arg1,
								int arg2) {
							updata_banner_default();
							return false;
						}
					});
			application.mediaPlayer_region
					.setOnPreparedListener(new OnPreparedListener() {

						@Override
						public void onPrepared(MediaPlayer mediaPlayer) {
							application.regionIsPlaying = true;
							if (!application.isGuangbo) {
								mediaPlayer.start();
							}
						}
					});
			application.mediaPlayer_region
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mediaPlayer) {
							mediaPlayer_region_play();
						}
					});
			application.mediaPlayer_region.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
			updata_banner_default();
		}
	}

	private void video_play() {
		if (vedioInfoModels.size() <= vedioInfoModels_i) {
			vedioInfoModels_i = 0;
		}
		String[] s = vedioInfoModels.get(vedioInfoModels_i).resPath.split("/");
		String path = MyApplication.PATH_ROOT + MyApplication.PATH_VIDEO + "/"
				+ s[s.length - 1];
		if (!new File(path).exists()) {
			return;
		}
		try {
			if (mediaPlayer == null) {
				mediaPlayer = new MediaPlayer();
			} else {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				mediaPlayer.reset();
			}
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setDataSource(path);
			mediaPlayer.setDisplay(surfaceHolder_video);
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mediaPlayer) {
					if (!application.isGuangbo) {
						mediaPlayer.start();
					}
				}
			});
			mediaPlayer
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mediaPlayer) {
							vedioInfoModels_i++;
							handler.sendEmptyMessage(7);
						}
					});
			mediaPlayer.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_classnew_back:
			finish();
			break;
		case R.id.btn_classnew_left:
			if (prize == 0) {
				prize = prizeInfoModels.size() - 1;
				updata_honor(prize);
			} else {
				updata_honor(--prize);
			}
			break;
		case R.id.btn_classnew_right:
			if (prize == prizeInfoModels.size() - 1) {
				prize = 0;
				updata_honor(prize);
			} else {
				updata_honor(++prize);
			}
			break;
		case R.id.ly_classnew_new_: {
			Intent intent = new Intent(this, NoticeDetailsActivity.class);
			intent.putExtra("top", "班级通知");
			intent.putExtra("id", noticeInfoModels.get(0).id);
			startActivity(intent);
			break;
		}
		case R.id.ly_classnew_zhiri: {
			Intent intent = new Intent(this, ZhiriActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.ly_classnew_honor_: {
			Intent intent = new Intent(this, HonorActivity.class);
			intent.putExtra("prize", prize);
			intent.putExtra("prizeType", "CLASS");
			intent.putExtra("prizeCode", prizeInfoModels.get(prize).prizeCode);
			startActivity(intent);
			break;
		}
		case R.id.ly_classnew_info_right: {
			CurriculumInfoDatabase database = new CurriculumInfoDatabase();
			List<CurriculumInfoModel> list = database.query_all();
			if (list != null && list.size() > 0) {
				Intent intent = new Intent(this, CurriculumActivity.class);
				startActivity(intent);
			} else {
				application.showToast(getResources()
						.getString(R.string.noclass));
			}
			break;
		}
		case R.id.ly_classnew_attend: {
			break;
		}
		case R.id.sv_video:
			Intent intent = new Intent(this, VideoListActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(this, NoticeDetailsActivity.class);
		intent.putExtra("top", "班级通知");
		intent.putExtra("id", noticeInfoModels.get(arg2 + 1).id);
		startActivity(intent);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: {
				Bitmap bitmap = (Bitmap) msg.obj;
				if (bitmap != null) {
					img_class_new.setVisibility(View.VISIBLE);
					img_class_new.setImageBitmap(bitmap);
					LayoutParams params = txt_classnenw_title.getLayoutParams();
					params.width = 390;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_classnenw_title.setLayoutParams(params);
					params = txt_classnenw_rl.getLayoutParams();
					params.width = 390;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_classnenw_rl.setLayoutParams(params);
					params = txt_classnenw_time.getLayoutParams();
					params.width = 390;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_classnenw_time.setLayoutParams(params);
				} else {
					img_class_new.setVisibility(View.INVISIBLE);
					LayoutParams params = txt_classnenw_title.getLayoutParams();
					params.width = 611;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_classnenw_title.setLayoutParams(params);
					params = txt_classnenw_rl.getLayoutParams();
					params.width = 611;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_classnenw_rl.setLayoutParams(params);
					params = txt_classnenw_time.getLayoutParams();
					params.width = 611;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_classnenw_time.setLayoutParams(params);
				}
				break;
			}
			case 2:
				if (displayManager == null) {
					updata_honor();
				} else {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_HONOR);
				}
				break;
			case 3:
				if (displayManager == null) {
					updata_curriculum();
				} else {
					displayManager
							.updata(AppCodeView.APPCODE_UPDATA_CURRICULUM);
				}
				break;
			case 4: {
				if (!TextUtils
						.isEmpty(region_media_img.get(region_media_img_i).srcPath)) {
					String[] s = region_media_img.get(region_media_img_i).srcPath
							.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PLAYTASK + "/"
							+ s[s.length - 1];
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {
								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null)
										img_classnew_banner
												.setImageDrawable(new BitmapDrawable(
														bitmap));
									if (region_media_img_i == region_media_img
											.size() - 1) {
										region_media_img_i = 0;
									} else {
										region_media_img_i++;
									}
									handler.sendEmptyMessageDelayed(4, 5 * 1000);
								}
							}, 671, 416);
				} else {
					if (region_media_img_i == region_media_img.size() - 1) {
						region_media_img_i = 0;
					} else {
						region_media_img_i++;
					}
					handler.sendEmptyMessageDelayed(4, 1000);
				}
				break;
			}
			case 5:
				mediaPlayer_region_play();
				break;
			case 6:
				if (TextUtils.isEmpty(new_path_1)) {
					break;
				}
				imageLoader.getBitmapFormUrl(new_path_1,
						new OnImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								if (bitmap != null) {
									Message msg = Message.obtain();
									msg.what = 1;
									msg.obj = bitmap;
									handler.sendMessage(msg);
								} else {
									handler.sendEmptyMessage(1);
								}
							}
						}, 200, 160);
				break;
			case 7: {
				video_play();
				break;
			}
			}
		}
	};

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals(HttpProcess.QRY_NOTICE) && !application.isUpdateEQ
					&& !application.isUpdataNoticeList) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					application.showToast("更新通知完成");
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("更新通知失败，原因：" + msg);
				}
				if (displayManager == null) {
					updata_news();
				} else {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_NOTIC);
				}
				application.isUpdateNotice = false;
			} else if (tag.equals(HttpProcess.QRY_PRIZE)
					&& !application.isUpdateEQ && !application.isGrxx) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					handler.sendEmptyMessageDelayed(2, 10 * 1000);
				}
			} else if (tag.equals(HttpProcess.QRY_BLACKBOARD)
					&& !application.isUpdateEQ) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					if (displayManager == null) {
						updata_english();
					} else {
						displayManager
								.updata(AppCodeView.APPCODE_UPDATA_ENGLISH);
					}
				}
			} else if (tag.equals("updataEQ")) {
				if (displayManager == null) {
					if (application.classInfoModel != null) {
						txt_classnew_bjmc
								.setText(application.classInfoModel.bjmc);
						txt_classnew_bzrcm
								.setText(application.classInfoModel.fdyxm);
						txt_classnew_bzr.setVisibility(View.VISIBLE);
					} else {
						txt_classnew_bzr.setVisibility(View.INVISIBLE);
					}
					updata_zr();
					updata_attend();
					updata_curriculum();
					updata_news();
					updata_honor();
					updata_english();
				} else {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_EQU);
				}
			} else if (tag.equals("commotAttend_ui")) {
				if (displayManager == null) {
					updata_attend();
				}
				// else {
				// displayManager.updata(AppCodeView.APPCODE_UPDATA_ATTEND);
				// }
			} else if (tag.equals(HttpProcess.QRY_CURRICULUM + "day")) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					handler.sendEmptyMessageDelayed(3, 3 * 1000);
				}
			} else if (tag.equals(HttpProcess.QRY_ONDUTY)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					if (displayManager == null) {
						updata_zr();
					} else {
						displayManager.updata(AppCodeView.APPCODE_UPDATA_ZHIRI);
					}
				}
			} else if (tag.equals("playTask")) {
				Log.d(TAG, "------playTask------");
				if (displayManager == null) {
					updata_banner();
				} else {
					displayManager
							.updata(AppCodeView.APPCODE_UPDATA_REGION_MEDIA);
				}
			} else if (tag.equals("permission_finish")) {
				finish();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageLoader.cancelTask();
		imageLoader.clearCache();
		handler.removeMessages(4);
		handler.removeMessages(5);
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
		if (displayManager != null) {
			displayManager.stop();
			displayManager.destroy();
		}
		if (displayManager_media != null) {
			displayManager_media.stop();
			displayManager_media.destroy();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (displayManager == null) {
			updata_banner();
		}
		application.handler_touch.removeMessages(0);
		application.handler_touch.sendEmptyMessageDelayed(0,
				application.screensaver_time * 1000);
	}

	@Override
	protected void onStop() {
		super.onStop();
		handler.removeMessages(4);
		if (displayManager_media != null) {
			displayManager_media.stop();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (displayManager != null) {
			displayManager.pause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (displayManager != null) {
			displayManager.resume();
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
							handler.sendEmptyMessage(6);
						}
						ftpManager.disConnect();
					}
				} else {
					if (application.httpDownLoad(urlStr, path)) {
						handler.sendEmptyMessage(6);
					}
				}
			}
		});
	}
}
