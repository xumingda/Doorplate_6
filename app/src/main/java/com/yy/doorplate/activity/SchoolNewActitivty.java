package com.yy.doorplate.activity;

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
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.database.PageLayoutDatabase;
import com.yy.doorplate.database.PrizeInfoDatabase;
import com.yy.doorplate.database.QuestionNaireDatabase;
import com.yy.doorplate.database.SchoolInfoDatabase;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.model.PageLayoutModel;
import com.yy.doorplate.model.PlayTaskModel;
import com.yy.doorplate.model.PrizeInfoModel;
import com.yy.doorplate.model.QuestionNaireModel;
import com.yy.doorplate.model.SchoolInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;
import com.yy.doorplate.ui.AppCodeView;
import com.yy.doorplate.view.CustomTextView;

public class SchoolNewActitivty extends Activity implements OnClickListener,
		OnItemClickListener {

	private final String TAG = "SchoolNewActitivty";

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private RelativeLayout ly_schoolnew, ly_schoolnew_honor_,
			ly_schoolnew_new_, ly_schoolnew_banner;
	private Button btn_schoolnew_back, btn_schoolnew_left, btn_schoolnew_right,
			btn_schoolnew_1, btn_schoolnew_2, btn_schoolnew_3, btn_schoolnew_4,
			btn_schoolnew_quest_start;
	private TextView txt_schoolnew_new_rl, txt_schoolnew_new_time,
			txt_schoolnew_honor1, txt_schoolnew_honor2, txt_schoolnew_nohonor,
			txt_schoolnew_quest_rl, txt_schoolnew_noquest;
	private CustomTextView txt_schoolnew_new_title, txt_schoolnew_quest_title;
	private ImageView img_schoolnew_honor, img_class_new;
	private ListView list_schoolnew_new;
	private SurfaceView sv_schoolnew;
	private ImageSwitcher img_schoolnew_banner;

	private ImageLoader imageLoader = null;

	private List<NoticeInfoModel> noticeInfoModels = null;
	private List<PrizeInfoModel> prizeInfoModels = null;
	private List<SchoolInfoModel> schoolInfoModels = null;

	private int prize = 0;

	private SurfaceHolder surfaceHolder = null;
	private PlayTaskModel region_media_video = null;
	private List<PlayTaskModel> region_media_img = null;
	private int region_media_img_i = 0;

	private String new_rl_1, new_url_1, new_path_1;

	private DisplayManager displayManager = null;
	private DisplayManager displayManager_media = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schoolnew);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		imageLoader = new ImageLoader(application);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		ly_schoolnew = (RelativeLayout) findViewById(R.id.ly_schoolnew);
		String ThemePolicyPath = getIntent().getStringExtra("ThemePolicyPath");
		PageLayoutDatabase database = new PageLayoutDatabase();
		PageLayoutModel model = database.query_by_pageCode("SCHOOL");
		if (model == null && TextUtils.isEmpty(ThemePolicyPath)) {
			initView();
		} else if (!TextUtils.isEmpty(ThemePolicyPath)) {
			ly_schoolnew.removeAllViews();
			displayManager = new DisplayManager(this, application, ly_schoolnew);
			displayManager.setImageLoader(imageLoader);
			displayManager
					.setActivity("com.yy.doorplate.activity.SchoolNewActitivty");
			displayManager.setActivity(this);
			displayManager.start(ThemePolicyPath);
		} else if (model != null) {
			ly_schoolnew.removeAllViews();
			displayManager = new DisplayManager(this, application, ly_schoolnew);
			displayManager.setImageLoader(imageLoader);
			displayManager
					.setActivity("com.yy.doorplate.activity.SchoolNewActitivty");
			displayManager.setActivity(this);
			displayManager.start(application.getFtpPath(model.resPath));
		}
	}

	private void initView() {
		ly_schoolnew_new_ = (RelativeLayout) findViewById(R.id.ly_schoolnew_new_);
		ly_schoolnew_honor_ = (RelativeLayout) findViewById(R.id.ly_schoolnew_honor_);
		ly_schoolnew_banner = (RelativeLayout) findViewById(R.id.ly_schoolnew_banner);
		btn_schoolnew_back = (Button) findViewById(R.id.btn_schoolnew_back);
		btn_schoolnew_left = (Button) findViewById(R.id.btn_schoolnew_left);
		btn_schoolnew_right = (Button) findViewById(R.id.btn_schoolnew_right);
		btn_schoolnew_1 = (Button) findViewById(R.id.btn_schoolnew_1);
		btn_schoolnew_2 = (Button) findViewById(R.id.btn_schoolnew_2);
		btn_schoolnew_3 = (Button) findViewById(R.id.btn_schoolnew_3);
		btn_schoolnew_4 = (Button) findViewById(R.id.btn_schoolnew_4);
		btn_schoolnew_quest_start = (Button) findViewById(R.id.btn_schoolnew_quest_start);
		txt_schoolnew_new_rl = (TextView) findViewById(R.id.txt_schoolnew_new_rl);
		txt_schoolnew_new_time = (TextView) findViewById(R.id.txt_schoolnew_new_time);
		txt_schoolnew_honor1 = (TextView) findViewById(R.id.txt_schoolnew_honor1);
		txt_schoolnew_honor2 = (TextView) findViewById(R.id.txt_schoolnew_honor2);
		txt_schoolnew_nohonor = (TextView) findViewById(R.id.txt_schoolnew_nohonor);
		txt_schoolnew_quest_rl = (TextView) findViewById(R.id.txt_schoolnew_quest_rl);
		txt_schoolnew_new_title = (CustomTextView) findViewById(R.id.txt_schoolnew_new_title);
		txt_schoolnew_quest_title = (CustomTextView) findViewById(R.id.txt_schoolnew_quest_title);
		txt_schoolnew_noquest = (TextView) findViewById(R.id.txt_schoolnew_noquest);
		img_schoolnew_honor = (ImageView) findViewById(R.id.img_schoolnew_honor);
		list_schoolnew_new = (ListView) findViewById(R.id.list_schoolnew_new);
		sv_schoolnew = (SurfaceView) findViewById(R.id.sv_schoolnew);
		img_schoolnew_banner = (ImageSwitcher) findViewById(R.id.img_schoolnew_banner);
		img_class_new = (ImageView) findViewById(R.id.img_class_new);

		img_schoolnew_banner.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				ImageView imageView = new ImageView(SchoolNewActitivty.this);
				ImageSwitcher.LayoutParams params = new ImageSwitcher.LayoutParams(
						ImageSwitcher.LayoutParams.MATCH_PARENT,
						ImageSwitcher.LayoutParams.MATCH_PARENT);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setLayoutParams(params);
				return imageView;
			}
		});
		img_schoolnew_banner.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.enteralpha));
		img_schoolnew_banner.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.exitalpha));

		ly_schoolnew_new_.setOnClickListener(this);
		ly_schoolnew_honor_.setOnClickListener(this);
		btn_schoolnew_back.setOnClickListener(this);
		btn_schoolnew_left.setOnClickListener(this);
		btn_schoolnew_right.setOnClickListener(this);
		btn_schoolnew_1.setOnClickListener(this);
		btn_schoolnew_2.setOnClickListener(this);
		btn_schoolnew_3.setOnClickListener(this);
		btn_schoolnew_4.setOnClickListener(this);
		btn_schoolnew_quest_start.setOnClickListener(this);
		list_schoolnew_new.setOnItemClickListener(this);
		// sv_schoolnew.setOnClickListener(this);

		surfaceHolder = sv_schoolnew.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.setKeepScreenOn(true);
		surfaceHolder.addCallback(new surFaceView());

		updata_news();
		updata_honor();
		updata_school();
		updata_quest();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ly_schoolnew_new_: {
			Intent intent = new Intent(this, NoticeDetailsActivity.class);
			intent.putExtra("top", "学校新闻");
			intent.putExtra("id", noticeInfoModels.get(0).id);
			startActivity(intent);
			break;
		}
		case R.id.ly_schoolnew_honor_: {
			Intent intent = new Intent(this, HonorActivity.class);
			intent.putExtra("prize", prize);
			intent.putExtra("prizeType", "SCHOOL");
			intent.putExtra("prizeCode", prizeInfoModels.get(prize).prizeCode);
			startActivity(intent);
			break;
		}
		case R.id.btn_schoolnew_back:
			finish();
			break;
		case R.id.btn_schoolnew_left:
			if (prize == 0) {
				prize = prizeInfoModels.size() - 1;
				updata_honor(prize);
			} else {
				updata_honor(--prize);
			}
			break;
		case R.id.btn_schoolnew_right:
			if (prize == prizeInfoModels.size() - 1) {
				prize = 0;
				updata_honor(prize);
			} else {
				updata_honor(++prize);
			}
			break;
		case R.id.btn_schoolnew_1: {
			Intent intent = new Intent(this, SchoolDetailsActivity.class);
			intent.putExtra("infoCode", schoolInfoModels.get(0).infoCode);
			startActivity(intent);
			break;
		}
		case R.id.btn_schoolnew_2: {
			Intent intent = new Intent(this, SchoolDetailsActivity.class);
			intent.putExtra("infoCode", schoolInfoModels.get(1).infoCode);
			startActivity(intent);
			break;
		}
		case R.id.btn_schoolnew_3: {
			Intent intent = new Intent(this, SchoolDetailsActivity.class);
			intent.putExtra("infoCode", schoolInfoModels.get(2).infoCode);
			startActivity(intent);
			break;
		}
		case R.id.btn_schoolnew_4: {
			if (schoolInfoModels.size() == 4) {
				Intent intent = new Intent(this, SchoolDetailsActivity.class);
				intent.putExtra("infoCode", schoolInfoModels.get(3).infoCode);
				startActivity(intent);
			} else if (schoolInfoModels.size() > 4) {
				Intent intent = new Intent(this, PjNoticeListActivity.class);
				intent.putExtra("type", 5);
				startActivity(intent);
			}
			break;
		}
		case R.id.btn_schoolnew_quest_start: {
			Intent intent = new Intent(this, QuestActivity.class);
			startActivity(intent);
			break;
		}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(this, NoticeDetailsActivity.class);
		intent.putExtra("top", "学校新闻");
		intent.putExtra("id", noticeInfoModels.get(arg2 + 1).id);
		startActivity(intent);
	}

	private void updata_news() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		NoticeInfoDatabase database = new NoticeInfoDatabase();
		noticeInfoModels = database.query_by_lmdm("bpxxxw", 0, 6);
		if (noticeInfoModels != null && noticeInfoModels.size() > 0) {
			ly_schoolnew_new_.setVisibility(View.VISIBLE);
			txt_schoolnew_new_title.setText(noticeInfoModels.get(0).xxzt);
			txt_schoolnew_new_time.setText(formatter.format(Long
					.parseLong(noticeInfoModels.get(0).fbsj)));
			txt_schoolnew_new_rl.setText("");
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
					txt_schoolnew_new_rl.setText(new_rl_1);
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
									}, 230, 200);
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
				list_schoolnew_new.setAdapter(new SimpleAdapter(application,
						list, R.layout.item_schoolnew, new String[] { "title",
								"time" }, new int[] {
								R.id.txt_item_schoolnew_title,
								R.id.txt_item_schoolnew_time }));
			} else {
				list_schoolnew_new.setAdapter(null);
			}
		} else {
			ly_schoolnew_new_.setVisibility(View.INVISIBLE);
			list_schoolnew_new.setAdapter(null);
		}
	}

	private void updata_honor() {
		prize = 0;
		PrizeInfoDatabase database = new PrizeInfoDatabase();
		prizeInfoModels = database.query_by_prizeType("SCHOOL");
		if (prizeInfoModels != null && prizeInfoModels.size() > 0) {
			ly_schoolnew_honor_.setVisibility(View.VISIBLE);
			txt_schoolnew_nohonor.setVisibility(View.INVISIBLE);
			if (prizeInfoModels.size() > 1) {
				btn_schoolnew_left.setVisibility(View.VISIBLE);
				btn_schoolnew_right.setVisibility(View.VISIBLE);
			}
			txt_schoolnew_honor1.setText(prizeInfoModels.get(0).ranking);
			txt_schoolnew_honor2.setText(prizeInfoModels.get(0).prizeName);
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
									img_schoolnew_honor.setImageBitmap(bitmap);
								}
							}, 160, 160);
				}
			} else {
				img_schoolnew_honor.setImageBitmap(null);
			}
		} else {
			ly_schoolnew_honor_.setVisibility(View.INVISIBLE);
			btn_schoolnew_left.setVisibility(View.INVISIBLE);
			btn_schoolnew_right.setVisibility(View.INVISIBLE);
			txt_schoolnew_nohonor.setVisibility(View.VISIBLE);
		}
	}

	private void updata_honor(int i) {
		if (prizeInfoModels != null && prizeInfoModels.size() > i) {
			txt_schoolnew_honor1.setText(prizeInfoModels.get(i).ranking);
			txt_schoolnew_honor2.setText(prizeInfoModels.get(i).awardsUnit);
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
									img_schoolnew_honor.setImageBitmap(bitmap);
								}
							}, 160, 160);
				} else {
					img_schoolnew_honor.setImageBitmap(null);
				}
			} else {
				img_schoolnew_honor.setImageBitmap(null);
			}
		}
	}

	private void updata_school() {
		SchoolInfoDatabase database = new SchoolInfoDatabase();
		schoolInfoModels = database.query_all();
		if (schoolInfoModels != null && schoolInfoModels.size() > 0) {
			btn_schoolnew_1.setVisibility(View.VISIBLE);
			btn_schoolnew_1.setText(schoolInfoModels.get(0).infoName);
			if (schoolInfoModels.size() > 1) {
				btn_schoolnew_2.setVisibility(View.VISIBLE);
				btn_schoolnew_2.setText(schoolInfoModels.get(1).infoName);
			} else {
				btn_schoolnew_2.setVisibility(View.INVISIBLE);
				btn_schoolnew_3.setVisibility(View.INVISIBLE);
				btn_schoolnew_4.setVisibility(View.INVISIBLE);
			}
			if (schoolInfoModels.size() > 2) {
				btn_schoolnew_3.setVisibility(View.VISIBLE);
				btn_schoolnew_3.setText(schoolInfoModels.get(2).infoName);
			} else {
				btn_schoolnew_3.setVisibility(View.INVISIBLE);
				btn_schoolnew_4.setVisibility(View.INVISIBLE);
			}
			if (schoolInfoModels.size() == 4) {
				btn_schoolnew_4.setVisibility(View.VISIBLE);
				btn_schoolnew_4.setText(schoolInfoModels.get(3).infoName);
			} else if (schoolInfoModels.size() > 4) {
				btn_schoolnew_4.setVisibility(View.VISIBLE);
				btn_schoolnew_4.setText("更多");
			} else {
				btn_schoolnew_4.setVisibility(View.INVISIBLE);
			}
		} else {
			btn_schoolnew_1.setVisibility(View.INVISIBLE);
			btn_schoolnew_2.setVisibility(View.INVISIBLE);
			btn_schoolnew_3.setVisibility(View.INVISIBLE);
			btn_schoolnew_4.setVisibility(View.INVISIBLE);
		}
	}

	private void updata_quest() {
		QuestionNaireDatabase database = new QuestionNaireDatabase();
		QuestionNaireModel model = database.query_first();
		if (model != null) {
			txt_schoolnew_quest_title.setVisibility(View.VISIBLE);
			txt_schoolnew_quest_rl.setVisibility(View.VISIBLE);
			btn_schoolnew_quest_start.setVisibility(View.VISIBLE);
			txt_schoolnew_noquest.setVisibility(View.INVISIBLE);
			txt_schoolnew_quest_title.setText(model.title);
			txt_schoolnew_quest_rl.setText(model.questionnaireDesc);
		} else {
			txt_schoolnew_quest_title.setVisibility(View.INVISIBLE);
			txt_schoolnew_quest_rl.setVisibility(View.INVISIBLE);
			btn_schoolnew_quest_start.setVisibility(View.INVISIBLE);
			txt_schoolnew_noquest.setVisibility(View.VISIBLE);
		}

	}

	private void updata_banner() {
		region_media_video = null;
		region_media_img = new ArrayList<PlayTaskModel>();
		region_media_img_i = 0;
		handler.removeMessages(3);
		handler.removeMessages(7);
		img_schoolnew_banner.setVisibility(View.VISIBLE);
		sv_schoolnew.setVisibility(View.INVISIBLE);
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
						&& model.position.equals("02")
						&& model.srcType.equals("TEMPLATE")) {
					String policyPath = application.getFtpPath(model.srcPath);
					File file = new File(policyPath);
					if (file.exists()) {
						ly_schoolnew_banner.removeAllViews();
						if (displayManager_media == null) {
							displayManager_media = new DisplayManager(this,
									application, ly_schoolnew_banner);
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
						&& model.position.equals("02")) {
					Log.d(TAG, model.toString());
					if (model.srcType.equals("VIDEO")) {
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
				img_schoolnew_banner.setVisibility(View.INVISIBLE);
				sv_schoolnew.setVisibility(View.VISIBLE);
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
		img_schoolnew_banner.setVisibility(View.VISIBLE);
		sv_schoolnew.setVisibility(View.INVISIBLE);
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
										img_schoolnew_banner
												.setImageDrawable(new BitmapDrawable(
														bitmap));
								}
							}, 1210, 477);
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
				handler.sendEmptyMessage(3);
			} else {
				updata_banner_default();
			}
		}
	}

	private void updata_banner_default() {
		img_schoolnew_banner.setVisibility(View.VISIBLE);
		sv_schoolnew.setVisibility(View.INVISIBLE);
		imageLoader.getBitmapFormRes(R.drawable.ly_schoolnew_banner,
				new OnImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						img_schoolnew_banner
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
			handler.sendEmptyMessageDelayed(7, 3000);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.d(TAG, "---surfaceDestroyed---");
			handler.removeMessages(7);
			application.regionIsPlaying = false;
			application.mediaPlayer_region.stop();
			application.mediaPlayer_region.reset();
		}
	}

	private void mediaPlayer_region_play() {
		if (!application.getRunningActivityName().equals(
				"com.yy.doorplate.activity.SchoolNewActitivty")
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

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: {
				Bitmap bitmap = (Bitmap) msg.obj;
				if (bitmap != null) {
					img_class_new.setVisibility(View.VISIBLE);
					img_class_new.setImageBitmap(bitmap);
					LayoutParams params = txt_schoolnew_new_title
							.getLayoutParams();
					params.width = 250;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_schoolnew_new_title.setLayoutParams(params);
					params = txt_schoolnew_new_rl.getLayoutParams();
					params.width = 250;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_schoolnew_new_rl.setLayoutParams(params);
					params = txt_schoolnew_new_time.getLayoutParams();
					params.width = 250;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_schoolnew_new_time.setLayoutParams(params);
				} else {
					img_class_new.setVisibility(View.INVISIBLE);
					LayoutParams params = txt_schoolnew_new_title
							.getLayoutParams();
					params.width = 500;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_schoolnew_new_title.setLayoutParams(params);
					params = txt_schoolnew_new_rl.getLayoutParams();
					params.width = 500;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_schoolnew_new_rl.setLayoutParams(params);
					params = txt_schoolnew_new_time.getLayoutParams();
					params.width = 500;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_schoolnew_new_time.setLayoutParams(params);
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
			case 3: {
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
										img_schoolnew_banner
												.setImageDrawable(new BitmapDrawable(
														bitmap));
									if (region_media_img_i == region_media_img
											.size() - 1) {
										region_media_img_i = 0;
									} else {
										region_media_img_i++;
									}
									handler.sendEmptyMessageDelayed(3, 5 * 1000);
								}
							}, 1210, 477);
				} else {
					if (region_media_img_i == region_media_img.size() - 1) {
						region_media_img_i = 0;
					} else {
						region_media_img_i++;
					}
					handler.sendEmptyMessageDelayed(3, 1000);
				}
				break;
			}
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
						}, 230, 200);
				break;
			case 7:
				mediaPlayer_region_play();
				break;
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
			} else if (tag.equals(HttpProcess.QRY_SCHOOL)
					&& !application.isUpdateEQ) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					if (displayManager == null) {
						updata_school();
					} else {
						displayManager
								.updata(AppCodeView.APPCODE_UPDATA_SCHOOL);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_QUEST)
					&& !application.isUpdateEQ) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					if (displayManager == null) {
						updata_quest();
					} else {
						displayManager.updata(AppCodeView.APPCODE_UPDATA_QUEST);
					}
				}
			} else if (tag.equals("updataEQ")) {
				if (displayManager == null) {
					updata_news();
					updata_honor();
					updata_school();
					updata_quest();
				} else {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_EQU);
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
		handler.removeMessages(3);
		handler.removeMessages(7);
		imageLoader.cancelTask();
		imageLoader.clearCache();
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
		handler.removeMessages(3);
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
