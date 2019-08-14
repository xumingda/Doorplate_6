package com.cx.doorplate.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextClock;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.FTPManager;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.VoteOptionDatabase;
import com.yy.doorplate.database.VoteThemeDatabase;
import com.yy.doorplate.model.VoteInfoModel;
import com.yy.doorplate.model.VoteOptionModel;
import com.yy.doorplate.model.VoteThemeModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class CXVoteActivity extends Activity implements OnClickListener,
		RadioGroup.OnCheckedChangeListener, CheckBox.OnCheckedChangeListener {

	private final String TAG = "CXVoteActivity";

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private ImageLoader imageLoader = null;

	private RelativeLayout ly_vote_left, ly_vote_right, ly_vote_sv;
	private LinearLayout ly_vote_list, ly_vote_option;
	private Button btn_cx_back, btn_vote_commit, btn_vote_register,
			btn_vote_details;
	private TextView txt_weather,txt_vote_title;
	private TextClock textClock;
	private ImageView img_vote_left, img_vote_right;
	private WebView web_vote;
	private ScrollView sv_vote;
	private ProgressDialog progressDialog = null;

	// 标记是否在加载详细内容
	private int loading = 0;

	private List<VoteThemeModel> list, list_ing, list_soon;
	private List<VoteOptionModel> voteOptionModels;
	private VoteThemeModel voteThemeModel;
	private List<VoteInfoModel> voteInfoModels = new ArrayList<VoteInfoModel>();
	private List<CheckBox> checkBoxs = new ArrayList<CheckBox>();

	// 1学校活动 2班级活动 3学校投票 4班级投票
	private int type = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_activity_vote);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		imageLoader = new ImageLoader(application);

		type = getIntent().getIntExtra("type", 1);

		initView();
	}

	private void initView() {
		ly_vote_left = (RelativeLayout) findViewById(R.id.ly_vote_left);
		ly_vote_right = (RelativeLayout) findViewById(R.id.ly_vote_right);
		ly_vote_sv = (RelativeLayout) findViewById(R.id.ly_vote_sv);
		ly_vote_list = (LinearLayout) findViewById(R.id.ly_vote_list);
		ly_vote_option = (LinearLayout) findViewById(R.id.ly_vote_option);
		btn_cx_back = (Button) findViewById(R.id.btn_cx_back);
		btn_vote_commit = (Button) findViewById(R.id.btn_vote_commit);
		btn_vote_register = (Button) findViewById(R.id.btn_vote_register);
		btn_vote_details = (Button) findViewById(R.id.btn_vote_details);
		txt_weather = (TextView) findViewById(R.id.txt_weather);
//		img_vote_title = (ImageView) findViewById(R.id.img_vote_title);
		txt_vote_title=(TextView) findViewById(R.id.txt_vote_title);
		img_vote_left = (ImageView) findViewById(R.id.img_vote_left);
		img_vote_right = (ImageView) findViewById(R.id.img_vote_right);
		web_vote = (WebView) findViewById(R.id.web_vote);
		sv_vote = (ScrollView) findViewById(R.id.sv_vote);

		textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("yyyy年MM月dd日\nEEEE      HH : mm");
		txt_weather.setText(application.currentCity.trim() + "   "
				+ application.temperature.trim() + "\n"
				+ application.weather.trim());
		if (type == 1) {
//			img_vote_title.setImageResource(R.drawable.cx_btn_school_activity);
			txt_vote_title.setText("校园活动");
		} else if (type == 2) {
//			img_vote_title.setImageResource(R.drawable.cx_img_activity_title2);
			txt_vote_title.setText("班级活动");
		} else if (type == 3) {
		} else if (type == 4) {
		}

		btn_cx_back.setOnClickListener(this);
		btn_vote_commit.setOnClickListener(this);
		btn_vote_register.setOnClickListener(this);
		btn_vote_details.setOnClickListener(this);

		try {
			initDate();
			updata_list();
			updata_details();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void initDate() throws ParseException {
		VoteThemeDatabase database = new VoteThemeDatabase();
		if (type == 1) {
			list = database.query("activityKind = ? and belongTo = ?",
					new String[] { "activity", "1" });
		} else if (type == 2) {
			list = database.query("activityKind = ? and belongTo = ?",
					new String[] { "activity", "2" });
		} else if (type == 3) {
			list = database.query("activityKind = ? and belongTo = ?",
					new String[] { "vote", "1" });
		} else if (type == 4) {
			list = database.query("activityKind = ? and belongTo = ?",
					new String[] { "vote", "2" });
		}
		if (list != null && list.size() > 0) {
			list_ing = new ArrayList<VoteThemeModel>();
			list_soon = new ArrayList<VoteThemeModel>();
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			long now = System.currentTimeMillis();
			for (VoteThemeModel model : list) {
				long startTime = format.parse(model.startTime).getTime();
				long endTime = format.parse(model.endTime).getTime();
				if (startTime <= now && now <= endTime) {
					list_ing.add(model);
				} else if (now < startTime) {
					list_soon.add(model);
				}
			}
		}
	}

	private void updata_list() {
		if (list_ing != null && list_ing.size() > 0 && list_soon != null
				&& list_soon.size() > 0) {
			ly_vote_left.setVisibility(View.VISIBLE);
			ly_vote_right.setVisibility(View.VISIBLE);
			voteThemeModel = list_ing.get(0);
			updata_list_details("报名正在火热进行...", 110, list_ing, false);
			updata_list_details("报名预热中...", 110, list_soon, true);
		} else if (list_ing != null && list_ing.size() > 0) {
			ly_vote_left.setVisibility(View.VISIBLE);
			ly_vote_right.setVisibility(View.VISIBLE);
			voteThemeModel = list_ing.get(0);
			updata_list_details("报名正在火热进行...",
					LinearLayout.LayoutParams.MATCH_PARENT, list_ing, false);
		} else if (list_soon != null && list_soon.size() > 0) {
			ly_vote_left.setVisibility(View.VISIBLE);
			ly_vote_right.setVisibility(View.VISIBLE);
			voteThemeModel = list_soon.get(0);
			updata_list_details("报名预热中...",
					LinearLayout.LayoutParams.MATCH_PARENT, list_soon, false);
		} else {
			ly_vote_left.setVisibility(View.INVISIBLE);
			ly_vote_right.setVisibility(View.INVISIBLE);
		}
	}

	private void updata_list_details(String txt, int height,
			final List<VoteThemeModel> list, boolean isSecond) {
		TextView textView = new TextView(this);
		textView.setTextColor(getResources().getColor(R.color.YELLOW));
		textView.setTextSize(36);
		textView.setText(txt);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		if (isSecond) {
			params.topMargin = 15;
		}
		ly_vote_list.addView(textView, params);
		ListView listView = new ListView(this);
		listView.setDivider(null);
		listView.setDividerHeight(5);
		listView.setVerticalScrollBarEnabled(false);
		listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, height);
		params.topMargin = 15;
		ly_vote_list.addView(listView, params);
		List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
		for (VoteThemeModel model : list) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("themeName", model.themeName);
			maps.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, maps,
				R.layout.cx_item_vote, new String[] { "themeName" },
				new int[] { R.id.txt_item_vote_title });
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				voteThemeModel = list.get(arg2);
				updata_details();
			}
		});
	}

	private void updata_details() {
		sv_vote.scrollTo(0, 0);
		if (voteThemeModel == null) {
			return;
		}
		if (progressDialog == null) {
			loading = 0;
			progressDialog = ProgressDialog.show(CXVoteActivity.this, null,
					"加载中", false, false);
		}
		ly_vote_sv.removeView(web_vote);
		web_vote.destroy();
		web_vote = new WebView(this);
		initWeb();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		ly_vote_sv.addView(web_vote, params);
		ly_vote_option.setVisibility(View.INVISIBLE);
		if (!TextUtils.isEmpty(voteThemeModel.themeIcon)) {
			String[] s = voteThemeModel.themeIcon.split("/");
			String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE
					+ "/vote_" + s[s.length - 1];
			imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					if (bitmap != null) {
						img_vote_left.setImageBitmap(bitmap);
					} else {
						img_vote_left
								.setImageResource(R.drawable.img_fragment_book);
					}
					handler.sendEmptyMessage(0);
				}
			}, 612, 400);
		} else {
			img_vote_left.setImageResource(R.drawable.img_fragment_book);
			handler.sendEmptyMessage(0);
		}
		if (!TextUtils.isEmpty(voteThemeModel.themeIconTitle)) {
			String[] s = voteThemeModel.themeIconTitle.split("/");
			String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE
					+ "/vote_" + s[s.length - 1];
			imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					if (bitmap != null) {
						img_vote_right.setImageBitmap(bitmap);
					} else {
						img_vote_right
								.setImageResource(R.drawable.img_fragment_book);
					}
					handler.sendEmptyMessage(0);
				}
			}, 1172, 400);
		} else {
			img_vote_right.setImageResource(R.drawable.img_fragment_book);
			handler.sendEmptyMessage(0);
		}
		if (!TextUtils.isEmpty(voteThemeModel.themeValue)) {
			web_vote.loadDataWithBaseURL(null, voteThemeModel.themeValue,
					"text/html", "utf-8", null);
		} else {
			handler.sendEmptyMessage(0);
		}
		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			long now = System.currentTimeMillis();
			long startTime = format.parse(voteThemeModel.startTime).getTime();
			long endTime = format.parse(voteThemeModel.endTime).getTime();
			if (startTime <= now && now <= endTime) {
				VoteOptionDatabase database = new VoteOptionDatabase();
				voteOptionModels = database
						.query_by_voteThemeId(voteThemeModel.voteThemeId);
				if (voteOptionModels != null && voteOptionModels.size() > 0) {
					btn_vote_register.setVisibility(View.VISIBLE);
					btn_vote_details.setVisibility(View.VISIBLE);
				} else {
					btn_vote_register.setVisibility(View.INVISIBLE);
					btn_vote_details.setVisibility(View.INVISIBLE);
				}
			} else {
				btn_vote_register.setVisibility(View.INVISIBLE);
				btn_vote_details.setVisibility(View.INVISIBLE);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void updata_option() {
		if (voteThemeModel == null) {
			return;
		}
		if (voteOptionModels == null) {
			return;
		}
		checkBoxs.clear();
		voteInfoModels.clear();
		ly_vote_option.removeAllViews();
		if ("1".equals(voteThemeModel.voteKinds)) {
			RadioGroup radioGroup = new RadioGroup(this);
			radioGroup.setOnCheckedChangeListener(this);
			ly_vote_option.addView(radioGroup, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			for (VoteOptionModel model : voteOptionModels) {
				RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
						RadioGroup.LayoutParams.WRAP_CONTENT,
						RadioGroup.LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 10, 0, 0);
				RadioButton tempButton = new RadioButton(this);
				tempButton.setLayoutParams(params);
				tempButton.setText(model.optionTitle);
				tempButton.setPadding(20, 0, 0, 0);
				tempButton.setTextColor(getResources().getColor(R.color.black));
				tempButton.setTextSize(30);
				radioGroup.addView(tempButton, params);
			}
		} else if ("2".equals(voteThemeModel.voteKinds)) {
			for (VoteOptionModel model : voteOptionModels) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 10, 0, 0);
				CheckBox checkBox = new CheckBox(this);
				checkBox.setLayoutParams(params);
				checkBox.setPadding(20, 0, 0, 0);
				checkBox.setText(model.optionTitle);
				checkBox.setTextColor(getResources().getColor(R.color.black));
				checkBox.setTextSize(30);
				ly_vote_option.addView(checkBox);
				checkBoxs.add(checkBox);
				checkBox.setOnCheckedChangeListener(this);
			}
		}
	}

	private void commit() {
		if (voteThemeModel == null) {
			return;
		}
		if ("2".equals(voteThemeModel.voteKinds) && checkBoxs.size() > 0) {
			for (int i = 0; i < checkBoxs.size(); i++) {
				if (checkBoxs.get(i).isChecked()) {
					VoteInfoModel voteInfoModel = new VoteInfoModel();
					voteInfoModel.voteThemeId = voteThemeModel.voteThemeId;
					voteInfoModel.voteOptionId = voteOptionModels.get(i).voteOptionId;
					voteInfoModels.add(voteInfoModel);
				}
			}
		}
		if (voteInfoModels.size() > 0) {
			application.cardType = "QRY_PERSON_VOTE";
			DialogPermission();
		} else {
			application.showToast("请选择");
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				loading++;
				if (loading == 3) {
					loading = 0;
					handler.sendEmptyMessageDelayed(1, 300);
				}
				break;
			case 1:
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				break;
			}
		};
	};

	private void initWeb() {
		WebSettings webSettings = web_vote.getSettings();
		// 设置缓存模式
		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		// // 设置默认字体大小
		webSettings.setDefaultFontSize(35);
		// 告诉WebView启用JavaScript的执行
		webSettings.setJavaScriptEnabled(true);
		// // 设置是否在概述模式，即WebView加载页面， 缩小到适合屏幕的内容
		// // webSettings.setLoadWithOverviewMode(true);
		// // 设置渲染优先级最高
		// webSettings.setRenderPriority(RenderPriority.HIGH);
		// 设置WebView是否应该启用“viewport” HTML标签的支持还是应该使用各种视图。
		webSettings.setUseWideViewPort(true);
		// // 图片加载放在最后渲染
		// webSettings.setBlockNetworkImage(true);
		// 支持自动加载图片
		webSettings.setLoadsImagesAutomatically(true);
		// 多窗口
		webSettings.supportMultipleWindows();
		// // 设置WebView是否应该使用其内置的放大机制
		// webSettings.setBuiltInZoomControls(false);
		// // 设置WebView是否应该显示屏幕缩放控件时， 使用内置的放大机制
		// webSettings.setDisplayZoomControls(false);
		// // 设置WebView是否需要用户手势播放媒体
		// webSettings.setMediaPlaybackRequiresUserGesture(true);
		// // 设置WebView是否应该支持缩放使用屏幕上的变焦 控制和手势
		// webSettings.setSupportZoom(true);
		// // 开启 DOM storage API 功能
		// webSettings.setDomStorageEnabled(true);
		// // 开启 database storage API 功能
		// webSettings.setDatabaseEnabled(true);
		// // 开启 Application Caches 功能
		// webSettings.setAppCacheEnabled(true);

		web_vote.setWebViewClient(new WebViewClient() {

			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view,
					String url) {
				Log.d(TAG, "shouldInterceptRequest " + url);
				if (url.startsWith("ftp")) {
					String[] s = url.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_NOTICE + "/" + s[s.length - 1];
					File file = new File(path);
					try {
						if (file.exists()) {
							InputStream is = new FileInputStream(path);
							WebResourceResponse response = new WebResourceResponse(
									"image/png", "UTF-8", is);
							return response;
						} else {
							FTPManager ftpManager = new FTPManager();
							if (ftpManager.connect(url, path)) {
								if (ftpManager.download()) {
									InputStream is = new FileInputStream(path);
									WebResourceResponse response = new WebResourceResponse(
											"image/png", "UTF-8", is);
									return response;
								}
							}
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				return super.shouldInterceptRequest(view, url);
			}
		});

		web_vote.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				return true;
			}
		});

		web_vote.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress == 100) {
					handler.sendEmptyMessage(0);
				}
			}
		});
	}

	private Button btn_premission_cancel2;

	private void DialogPermission() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				CXVoteActivity.this);
		application.dialog = builder.create();
		LayoutInflater inflater = LayoutInflater.from(application);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.dialog_permission_onlycard, null);
		application.dialog.setView(layout);
		application.dialog.show();
		application.dialog.getWindow().setLayout(900, 700);
		application.dialog.getWindow().setContentView(
				R.layout.dialog_permission_onlycard);
		application.dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialogInterface) {
				application.cardType = null;
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
					progressDialog = null;
				}
			}
		});
		btn_premission_cancel2 = (Button) application.dialog
				.findViewById(R.id.btn_premission_cancel2);
		btn_premission_cancel2.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cx_back:
			finish();
			break;
		case R.id.btn_vote_commit:
			commit();
			break;
		case R.id.btn_vote_register:
			sv_vote.scrollTo(0, 0);
			web_vote.setVisibility(View.INVISIBLE);
			ly_vote_option.setVisibility(View.VISIBLE);
			btn_vote_commit.setVisibility(View.VISIBLE);
			updata_option();
			break;
		case R.id.btn_vote_details:
			sv_vote.scrollTo(0, 0);
			web_vote.setVisibility(View.VISIBLE);
			ly_vote_option.setVisibility(View.INVISIBLE);
			btn_vote_commit.setVisibility(View.INVISIBLE);
			break;
		case R.id.btn_premission_cancel2:
			if (application.dialog != null && application.dialog.isShowing()) {
				application.dialog.dismiss();
			}
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		voteInfoModels.clear();
		VoteInfoModel voteInfoModel = new VoteInfoModel();
		voteInfoModel.voteThemeId = voteThemeModel.voteThemeId;
		String voteOptionId = "";
		RadioButton rb = (RadioButton) CXVoteActivity.this
				.findViewById(checkedId);
		for (VoteOptionModel model : voteOptionModels) {
			if (model.optionTitle.equals(rb.getText())) {
				voteOptionId = model.voteOptionId;
				break;
			}
		}
		voteInfoModel.voteOptionId = voteOptionId;
		voteInfoModels.add(voteInfoModel);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			try {
				int allowNum = Integer.parseInt(voteThemeModel.allowNum);
				int checkNum = 0;
				for (CheckBox checkBox : checkBoxs) {
					if (checkBox.isChecked()) {
						checkNum++;
					}
				}
				if (checkNum > allowNum) {
					application.showToast("最多选择" + allowNum + "项");
					buttonView.setChecked(false);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
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
			} else if (tag.equals("QRY_PERSON_VOTE")) {
				if (progressDialog == null) {
					progressDialog = ProgressDialog.show(CXVoteActivity.this,
							null, "提交中，请稍后", false, false);
				}
				String mCardNum = intent.getStringExtra("mCardNum");
				application.httpProcess.QryPerson(
						application.equInfoModel.jssysdm, mCardNum);
			} else if (tag.equals(HttpProcess.QRY_PERSON)
					&& "QRY_PERSON_VOTE".equals(application.cardType)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					String rybh = intent.getStringExtra("rybh");
					if ("1".equals(intent.getStringExtra("js"))) {
						application.showToast("教师不可操作");
						if (progressDialog != null
								&& progressDialog.isShowing()) {
							progressDialog.dismiss();
							progressDialog = null;
						}
						return;
					}
					if (TextUtils.isEmpty(rybh)) {
						application.showToast("未找到该人员");
						if (progressDialog != null
								&& progressDialog.isShowing()) {
							progressDialog.dismiss();
							progressDialog = null;
						}
					} else {
						for (int i = 0; i < voteInfoModels.size(); i++) {
							VoteInfoModel model = voteInfoModels.get(i);
							model.rybh = rybh;
							voteInfoModels.set(i, model);
						}
						application.httpProcess.commitVote(voteInfoModels);
					}
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("未找到该人员");
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
						progressDialog = null;
					}
				}
			} else if (tag.equals(HttpProcess.COMMIT_VOTE)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					application.showToast("提交成功");
					if (application.dialog != null
							&& application.dialog.isShowing()) {
						application.dialog.dismiss();
					}
					web_vote.setVisibility(View.VISIBLE);
					ly_vote_option.setVisibility(View.INVISIBLE);
					btn_vote_commit.setVisibility(View.INVISIBLE);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("提交失败，原因:" + msg);
				}
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
					progressDialog = null;
				}
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
		imageLoader.clearCache();
		imageLoader.cancelTask();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
		web_vote.removeAllViews();
		web_vote.destroy();
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
