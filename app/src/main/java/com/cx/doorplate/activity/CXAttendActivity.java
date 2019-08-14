package com.cx.doorplate.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.adapter.StudentXKXXAdapter;
import com.yy.doorplate.communication.FTPManager;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.communication.HttpUtils;
import com.yy.doorplate.database.AttendInfoDatabase;
import com.yy.doorplate.database.CoursewareDatabase;
import com.yy.doorplate.database.CurriculumInfoDatabase;
import com.yy.doorplate.database.PersonnelAttendanceDatabase;
import com.yy.doorplate.database.StudentInfoDatabase;
import com.yy.doorplate.database.TeacherInfoDatabase;
import com.yy.doorplate.model.AnswerModel;
import com.yy.doorplate.model.AttendInfoModel;
import com.yy.doorplate.model.CoursewareModel;
import com.yy.doorplate.model.CurriculumInfoModel;
import com.yy.doorplate.model.OptionModel;
import com.yy.doorplate.model.PersonnelAttendanceModel;
import com.yy.doorplate.model.QuestionModel;
import com.yy.doorplate.model.QuestionNaireModel;
import com.yy.doorplate.model.StudentInfoModel;
import com.yy.doorplate.model.TeacherInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

import org.json.JSONException;
import org.json.JSONObject;

public class CXAttendActivity extends Activity implements OnClickListener, RadioGroup.OnCheckedChangeListener {

	private final String TAG = "CXAttendActivity";

	private MyApplication application;

	private ImageLoader imageLoader;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;
	private LinearLayout ly_courseware;
	private RelativeLayout ly_attend;
	private WebView wv_attend;
	private LinearLayout ly_attend_btn, ly_attend_courseware, ly_classfeedback;
	private Button btn_cx_back, btn_attend, btn_courseware, btn_classfeedback;
	private TextView txt_weather, txt_attend_js, txt_attend_yd, txt_attend_courseware2, txt_attend_courseware4,
			txt_attend_teacher2, txt_attend_teacher4;
	private WebView webView;
	private ImageView img_attend_teacher;
	private TextClock textClock;
	private TextView tv1,tv2,tv3,tv4,tv5,tv6;
	private GridView gv_attend1,gv_attend2,gv_attend3,gv_attend4,gv_attend5,gv_attend6,gv_student;
	private PieChart chart;
	private HorizontalScrollView sv_classfeedback;

	private ProgressDialog progressDialog = null;

	private String skjhid;
	private String kcmc;

	private CurriculumInfoModel curriculumInfoModel = null;
	private List<PersonnelAttendanceModel> attendInfoModels = null;
	private TeacherInfoModel teacherInfoModel = null;
	private List<CoursewareModel> coursewareModels;
	private String sknl = "", zynr = "";

	private List<QuestionNaireModel> questionNaireModels;
	private List<CheckBox> checkBoxs = new ArrayList<CheckBox>();
	private List<EditText> editTexts = new ArrayList<EditText>();
	private List<AnswerModel> answerModels = new ArrayList<AnswerModel>(), answerModelsCommit;

	private boolean isInputMethod = false;
	private EditText editTextInputMethod;
	private HttpUtils httpUtils;
	private final String QRY_STUDENTBYKC="qryStudentByKc";
	private List<StudentInfoModel> studentInfoModelList;
	private int type=0;
	private boolean isgo=false;


	//未签到
	private List<PersonnelAttendanceModel> list1;
	//请假
	private List<PersonnelAttendanceModel> list2;
	//迟到
	private List<PersonnelAttendanceModel> list3;
	//旷课
	private List<PersonnelAttendanceModel> list4;
    //早退
	private List<PersonnelAttendanceModel> list5;
	//出勤
	private List<PersonnelAttendanceModel> list6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_activity_attend);
		list1=new ArrayList<>();
		list2=new ArrayList<>();
		list3=new ArrayList<>();
		list4=new ArrayList<>();
		list5=new ArrayList<>();
		list6=new ArrayList<>();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();
		httpUtils = new HttpUtils(application);
		imageLoader = new ImageLoader(application);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initData();
		if (curriculumInfoModel != null) {
			initView();
		}
	}

	private void initData() {
		type= getIntent().getIntExtra("type",0);
		skjhid = getIntent().getStringExtra("skjhid");
		kcmc= getIntent().getStringExtra("kcmc");
		Log.e("我的Id:","我的Id:"+skjhid+"  名:"+kcmc);
		CurriculumInfoDatabase curriculumInfoDatabase = new CurriculumInfoDatabase();
		curriculumInfoModel = curriculumInfoDatabase.query_by_id(skjhid);

		// AttendInfoDatabase attendInfoDatabase = new AttendInfoDatabase();
		// attendInfoModels = attendInfoDatabase.query_by_skjhid(skjhid);

		TeacherInfoDatabase teacherInfoDatabase = new TeacherInfoDatabase();
		if (!TextUtils.isEmpty(curriculumInfoModel.skjs)) {
			teacherInfoModel = teacherInfoDatabase.query_by_id(curriculumInfoModel.skjs);
		}

	}

	private void initView() {
		wv_attend=(WebView)findViewById(R.id.wv_attend);
		ly_attend = (RelativeLayout) findViewById(R.id.ly_attend);
		ly_courseware = (LinearLayout) findViewById(R.id.ly_courseware);
		sv_classfeedback = (HorizontalScrollView) findViewById(R.id.sv_classfeedback);
		ly_attend_btn = (LinearLayout) findViewById(R.id.ly_attend_btn);
		ly_attend_courseware = (LinearLayout) findViewById(R.id.ly_attend_courseware);
		ly_classfeedback = (LinearLayout) findViewById(R.id.ly_classfeedback);
		btn_cx_back = (Button) findViewById(R.id.btn_cx_back);
		btn_attend = (Button) findViewById(R.id.btn_attend);
		btn_courseware = (Button) findViewById(R.id.btn_courseware);
		btn_classfeedback = (Button) findViewById(R.id.btn_classfeedback);
		txt_weather = (TextView) findViewById(R.id.txt_weather);
        tv1= (TextView) findViewById(R.id.tv1);
        tv2= (TextView) findViewById(R.id.tv2);
        tv3= (TextView) findViewById(R.id.tv3);
        tv4= (TextView) findViewById(R.id.tv4);
        tv5= (TextView) findViewById(R.id.tv5);
        tv6= (TextView) findViewById(R.id.tv6);
		txt_attend_js = (TextView) findViewById(R.id.txt_attend_js);
		txt_attend_yd = (TextView) findViewById(R.id.txt_attend_yd);
		txt_attend_courseware2 = (TextView) findViewById(R.id.txt_attend_courseware2);
		txt_attend_courseware4 = (TextView) findViewById(R.id.txt_attend_courseware4);
		txt_attend_teacher2 = (TextView) findViewById(R.id.txt_attend_teacher2);
		txt_attend_teacher4 = (TextView) findViewById(R.id.txt_attend_teacher4);
		webView = (WebView) findViewById(R.id.webView);
		img_attend_teacher = (ImageView) findViewById(R.id.img_attend_teacher);
		gv_attend1 = (GridView) findViewById(R.id.gv_attend1);
		gv_attend2 = (GridView) findViewById(R.id.gv_attend2);
		gv_attend3 = (GridView) findViewById(R.id.gv_attend3);
		gv_attend4 = (GridView) findViewById(R.id.gv_attend4);
		gv_attend5 = (GridView) findViewById(R.id.gv_attend5);
		gv_attend6= (GridView) findViewById(R.id.gv_attend6);
		gv_student = (GridView) findViewById(R.id.gv_student);
		chart = (PieChart) findViewById(R.id.chart);

		textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("yyyy年MM月dd日\nEEEE      HH : mm");
		txt_weather.setText(application.currentCity.trim() + "   " + application.temperature.trim() + "\n"
				+ application.weather.trim());

		btn_cx_back.setOnClickListener(this);
		btn_attend.setOnClickListener(this);
		btn_courseware.setOnClickListener(this);
		btn_classfeedback.setOnClickListener(this);
		if (attendInfoModels == null) {
			Log.e("查询","查询QryAttend");
			application.httpProcess.QryPersonnelAttendanceList(skjhid);
			application.isQryAttend = true;
		} else {
			application.httpProcess.QryClassInfo(skjhid, null);
		}
		showProgressDialog();




//		wv_attend.loadDataWithBaseURL(null, "https://admin.gamma-demo.suanzi.ai/#/teacher/details",
//				"text/html", "utf-8", null);
		WebSettings webSettings = wv_attend.getSettings();
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

		wv_attend.loadUrl("https://admin.gamma-demo.suanzi.ai/#/teacher/details");
        wv_attend.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
	}

	private void updata() {
		ly_attend_btn.removeAllViews();
		if(type==1){
			gv_student.setVisibility(View.VISIBLE);
			ly_attend_btn.addView(btn_attend);
			ly_attend_btn.addView(btn_courseware);
			ly_attend_btn.addView(btn_classfeedback);
			updataCoursewareView();
			updataAttend();
		}else{
			Log.e("查询","查询这里");
			if (attendInfoModels == null||attendInfoModels .size()==0) {
				gv_student.setVisibility(View.VISIBLE);
				ly_attend_btn.addView(btn_courseware);
				ly_attend_btn.addView(btn_classfeedback);
				updataCoursewareView();

			} else {
				Log.e("查询","查询有进来");
				gv_student.setVisibility(View.INVISIBLE);
				ly_attend_btn.addView(btn_attend);
				ly_attend_btn.addView(btn_courseware);
				ly_attend_btn.addView(btn_classfeedback);
				updataAttendView();
				updataAttend();
			}
		}

		getStudentInfo();
		updataTeacher();

	}

	private void updataAttendView() {
		ly_attend.setVisibility(View.VISIBLE);
		ly_courseware.setVisibility(View.INVISIBLE);
		sv_classfeedback.setVisibility(View.INVISIBLE);
		btn_attend.setBackgroundResource(R.drawable.cx_btn_attend_click);
		btn_courseware.setBackgroundResource(R.drawable.cx_btn_attend);
		btn_classfeedback.setBackgroundResource(R.drawable.cx_btn_attend);
	}

	private void updataCoursewareView() {
		ly_attend.setVisibility(View.INVISIBLE);
		ly_courseware.setVisibility(View.VISIBLE);
		sv_classfeedback.setVisibility(View.INVISIBLE);
		btn_attend.setBackgroundResource(R.drawable.cx_btn_attend);
		btn_courseware.setBackgroundResource(R.drawable.cx_btn_attend_click);
		btn_classfeedback.setBackgroundResource(R.drawable.cx_btn_attend);
	}

	private void updataClassfeedbackView() {
		ly_attend.setVisibility(View.INVISIBLE);
		ly_courseware.setVisibility(View.INVISIBLE);
		sv_classfeedback.setVisibility(View.VISIBLE);
		btn_attend.setBackgroundResource(R.drawable.cx_btn_attend);
		btn_courseware.setBackgroundResource(R.drawable.cx_btn_attend);
		btn_classfeedback.setBackgroundResource(R.drawable.cx_btn_attend_click);
	}
	// 查询学生选课信息
	public void QryStudentbykc(String skjhid,String jssysdm,String orgId) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {

				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_STUDENTBYKC, httpUtils.getJsonStudentByKc(skjhid,jssysdm,orgId));
					if (!TextUtils.isEmpty(result)) {
						try {
							Log.e("message","messageresult:"+result);
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								return;
							}else{
								JSONObject data = new JSONObject(result).getJSONObject("data");
								JSONObject error = data.getJSONObject("error");
								String error_id = error.getString("id");
								String error_message = error.getString("message");
								if (!error_id.equals("0000") && !error_id.equals("9001")) {
									return;
								}
								try {
									String studentList = data.getString("studentList");

									studentInfoModelList = JSONArray.parseArray(studentList, StudentInfoModel.class);
									Log.e("message","message:"+studentInfoModelList);
									if (studentInfoModelList != null && studentInfoModelList.size() > 0) {
										handler.sendEmptyMessage(200);
									}else{
										handler.sendEmptyMessage(520);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} catch (JSONException e) {
						}
					} else {

					}
				} else {
				}
			}
		});
	}


	private void getStudentInfo() {
//		if(kcmc.equals("语文")||kcmc.equals("英语")||kcmc.equals("数学")){
//			Log.e("message","messagejin1111");
//		}else{
//		    Log.e("message","messagejin");
		QryStudentbykc(skjhid,application.equInfoModel.jssysdm,application.equInfoModel.orgId);
//		}

	}
	private void updataAttend() {
		if (attendInfoModels != null&&attendInfoModels .size()>0) {
			MyAdapter adapter = new MyAdapter(list1);
			gv_attend1.setAdapter(adapter);
			MyAdapter adapter1 = new MyAdapter(list4);
			gv_attend2.setAdapter(adapter1);
			MyAdapter adapter2 = new MyAdapter(list3);
			gv_attend3.setAdapter(adapter2);
			MyAdapter adapter3 = new MyAdapter(list5);
			gv_attend4.setAdapter(adapter3);
			MyAdapter adapter4 = new MyAdapter(list2);
			gv_attend5.setAdapter(adapter4);
			MyAdapter adapter5 = new MyAdapter(list6);
			gv_attend6.setAdapter(adapter5);
			int count = 0;

			for (PersonnelAttendanceModel model : attendInfoModels) {
				String kqzt = model.status.split(",")[0];
				if (kqzt.equals("0") ||kqzt.equals("3")) {
					count++;
				}
			}
			txt_attend_js.setText("授课教师 : " + curriculumInfoModel.skjsxm);
			txt_attend_yd.setText("应到人数 : " + attendInfoModels.size() + "\t\t\t本节课出勤率 : "
					+ (float) (count * 100 / attendInfoModels.size()) + "%");
		} else {
			gv_attend1.setAdapter(null);
			gv_attend2.setAdapter(null);
			gv_attend3.setAdapter(null);
			gv_attend4.setAdapter(null);
			gv_attend5.setAdapter(null);
			gv_attend6.setAdapter(null);
			txt_attend_js.setText("");
			txt_attend_yd.setText("");
		}
		initChart();
		setPieData();
	}

	private void updataCourseware() {
		txt_attend_courseware2.setText(sknl);
		txt_attend_courseware4.setText(zynr);
		ly_attend_courseware.removeAllViews();
		if (coursewareModels != null && coursewareModels.size() > 0) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			for (CoursewareModel model : coursewareModels) {
				TextView textView = new TextView(this);
				textView.setTextSize(30);
				textView.setTextColor(getResources().getColor(R.color.black));
				textView.setText(model.fileName);
				textView.setLineSpacing(7, 1);
				textView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						TextView textView = (TextView) v;
						for (CoursewareModel model : coursewareModels) {
							if (model.fileName.equals(textView.getText().toString())) {
								downLoadoffice(model.filePath);
								break;
							}
						}
					}
				});
				ly_attend_courseware.addView(textView, params);
			}
		}
	}

	private void updataTeacher() {
		String xm = "", zc = "";
		if (teacherInfoModel != null) {
			if (!TextUtils.isEmpty(teacherInfoModel.zp)) {
				String[] s = teacherInfoModel.zp.split("\\.");
				String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE + "/teacher_" + teacherInfoModel.rybh
						+ "." + s[s.length - 1];
				imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						if (bitmap != null) {
							img_attend_teacher.setImageBitmap(bitmap);
						}
					}
				}, 200, 200);
			}
			if (!TextUtils.isEmpty(teacherInfoModel.xm)) {
				xm = teacherInfoModel.xm;
			}
			if (!TextUtils.isEmpty(teacherInfoModel.jgzc)) {
				zc = teacherInfoModel.jgzc;
			}
			if (!TextUtils.isEmpty(teacherInfoModel.kyqk)) {
				WebSettings webSettings = webView.getSettings();
				webSettings.setDefaultFontSize(30);
				webView.setBackgroundColor(0);
				webView.loadDataWithBaseURL(null, teacherInfoModel.kyqk, "text/html", "utf-8", null);
			}
		}
		txt_attend_teacher2.setText(xm);
		txt_attend_teacher4.setText(zc);
	}

	private void updataClassfeedback() {
		sv_classfeedback.scrollTo(0, 0);
		ly_classfeedback.removeAllViews();
		checkBoxs.clear();
		editTexts.clear();
		answerModels.clear();
		if (questionNaireModels != null && questionNaireModels.size() > 0) {
			for (int i = 0; i < questionNaireModels.size(); i++) {
				addQuestNaire(i);
			}
		}
	}

	private void addQuestNaire(int i) {
		QuestionNaireModel questionNaireModel = questionNaireModels.get(i);
		LayoutInflater inflater = LayoutInflater.from(application);
		RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.cx_ly_quset_class, null);
		Button btn_quset_commit = (Button) relativeLayout.findViewById(R.id.btn_quset_commit);
		TextView txt_quset_title = (TextView) relativeLayout.findViewById(R.id.txt_quset_title);
		ScrollView sv_quset = (ScrollView) relativeLayout.findViewById(R.id.sv_quset);
		LinearLayout ly_quset_option = (LinearLayout) relativeLayout.findViewById(R.id.ly_quset_option);
		View ly_quest_line = relativeLayout.findViewById(R.id.ly_quest_line);
		if (i == 0) {
			ly_quest_line.setVisibility(View.INVISIBLE);
		}
		btn_quset_commit.setTag(questionNaireModel);
		sv_quset.scrollTo(0, 0);
		txt_quset_title.setText(questionNaireModel.title);
		List<QuestionModel> questionList = questionNaireModel.questionList;
		if (questionList != null && questionList.size() > 0) {
			for (QuestionModel questionModel : questionList) {
				addQuest(ly_quset_option, questionModel, questionNaireModel);
			}
		}
		btn_quset_commit.setOnClickListener(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(626, 822);
		ly_classfeedback.addView(relativeLayout, params);
	}

	private void addQuest(LinearLayout linearLayout, final QuestionModel questionModel,
						  final QuestionNaireModel questionNaireModel) {
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params1.setMargins(0, 10, 0, 0);
		TextView textView = new TextView(this);
		textView.setTextColor(Color.parseColor("#FF7015"));
		textView.setTextSize(30);
		textView.setText(questionModel.question);
		linearLayout.addView(textView, params1);
		if (questionModel.answerType.equals("RADIO")) {
			List<OptionModel> optionModels = questionModel.optionList;
			if (optionModels != null && optionModels.size() > 0) {
				RadioGroup radioGroup = new RadioGroup(this);
				radioGroup.setTag(questionModel);
				radioGroup.setOnCheckedChangeListener(this);
				for (OptionModel optionModel : optionModels) {
					RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
							RadioGroup.LayoutParams.WRAP_CONTENT);
					params.setMargins(0, 10, 0, 0);
					RadioButton tempButton = new RadioButton(this);
					tempButton.setTag(optionModel);
					tempButton.setPadding(20, 0, 0, 0);
					tempButton.setText(optionModel.optionName);
					tempButton.setTextSize(30);
					tempButton.setTextColor(Color.parseColor("#666666"));
					radioGroup.addView(tempButton, params);
				}
				params1.topMargin = 10;
				linearLayout.addView(radioGroup, params1);
			}
		} else if (questionModel.answerType.equals("MULTISELECT")) {
			List<OptionModel> optionModels = questionModel.optionList;
			if (optionModels != null && optionModels.size() > 0) {
				for (OptionModel optionModel : optionModels) {
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					params.setMargins(0, 10, 0, 0);
					CheckBox checkBox = new CheckBox(this);
					checkBox.setTag(questionModel);
					checkBox.setPadding(20, 0, 0, 0);
					checkBox.setText(optionModel.optionName);
					checkBox.setTextSize(30);
					checkBox.setTextColor(Color.parseColor("#666666"));
					linearLayout.addView(checkBox, params);
					checkBoxs.add(checkBox);
				}
			}
		} else if (questionModel.answerType.equals("TEXT")) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					200);
			params.setMargins(0, 10, 0, 0);
			final EditText editText = new EditText(this);
			editText.setTag(questionModel);
			editText.setPadding(20, 20, 20, 20);
			editText.setTextSize(30);
			editText.setTextColor(Color.parseColor("#666666"));
			editText.setGravity(Gravity.TOP | Gravity.LEFT);
			editText.addTextChangedListener(application.textWatcher);
			editText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isInputMethod) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						if (imm != null)
							imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
						isInputMethod = false;
					} else {
						InputMethodManager imm = (InputMethodManager) editText.getContext()
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						if (imm != null)
							imm.showSoftInput(editText, 0);
						editTextInputMethod = editText;
						isInputMethod = true;
					}
				}
			});
			linearLayout.addView(editText, params);
			editTexts.add(editText);
		}
	}

	private void initChart() {
		// 显示百分比
		chart.setUsePercentValues(true);
		// 描述信息
		chart.setDescription("");
		// 设置偏移量
		// chart.setExtraOffsets(0, 0, 0, 0);
		// 设置滑动减速摩擦系数
		// chart.setDragDecelerationFrictionCoef(0.95f);

		// chart.setCenterText("测试饼图，中间文字");
		// 设置是否显示中间的文字
		// chart.setDrawCenterText(true);
		// 设置饼图中心是否是空心的 true 中间是空心的，环形图 false 中间是实心的 饼图
		chart.setDrawHoleEnabled(true);
		// 设置中间空心圆孔的颜色是否透明 true 透明的 false 非透明的
		chart.setHoleColorTransparent(true);
		// 设置环形图和中间空心圆之间的圆环的颜色
		// chart.setTransparentCircleColor(Color.WHITE);
		// 设置环形图和中间空心圆之间的圆环的透明度
		// chart.setTransparentCircleAlpha(110);
		// 设置圆孔半径
		chart.setHoleRadius(70);
		// 设置空心圆的半径
		// chart.setTransparentCircleRadius(60);

		// 初始旋转角度
		chart.setRotationAngle(0);
		// 可以手动旋转
		chart.setRotationEnabled(false);

		// 设置隐藏饼图上文字，只显示百分比
		chart.setDrawSliceText(true);

		// 如果没有数据的时候，会显示这个，类似ListView的EmptyView
		chart.setNoDataText("");

		// 设置动画
		// chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

		Legend mLegend = chart.getLegend(); // 设置比例图
		mLegend.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT); // 左上边显示
		mLegend.setFormSize(25f);// 比例块大小
		mLegend.setTextSize(20f);
		mLegend.setXEntrySpace(30);// 设置距离饼图的距离，防止与饼图重合
		mLegend.setYEntrySpace(20);
		// 设置比例块换行...
		mLegend.setWordWrapEnabled(true);
		// mLegend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
		mLegend.setTextColor(getResources().getColor(R.color.black));
		mLegend.setForm(Legend.LegendForm.SQUARE);// 设置比例块形状，默认为方块
		// mLegend.setEnabled(false);// 设置禁用比例块
	}

	private ArrayList<String> xVals = new ArrayList<String>();

	private void setPieData() {
		if (attendInfoModels != null && attendInfoModels.size() > 0) {
			float total = attendInfoModels.size();
			float c0 = 0, c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0, c7 = 0, c8 = 0, c9 = 0, c10 = 0, c11 = 0;
			float quarterly0, quarterly1, quarterly2, quarterly3, quarterly4, quarterly5, quarterly6, quarterly7,
					quarterly8, quarterly9, quarterly10, quarterly11;
			for (PersonnelAttendanceModel model : attendInfoModels) {
				String kqzt = model.status.split(",")[0];
				//0出勤
				//1事假
				//2病假
				//3迟到
				//4旷课
				//5早退
				if (kqzt.equals("0")) {
					c0++;
				} else if (kqzt.equals("1")||kqzt.equals("2")) {
					c1++;
				} else if (kqzt.equals("3")) {
					c3++;
				} else if (kqzt.equals("4")) {
					c4++;
				} else if (kqzt.equals("5")) {
					c5++;
				} else if (kqzt.equals("6")) {
					c6++;
				} else if (kqzt.equals("7")) {
					c7++;
				} else if (kqzt.equals("8")) {
					c8++;
				} else if (kqzt.equals("13")) {
					c9++;
				} else if (kqzt.equals("10")) {
					c10++;
				} else if (kqzt.equals("11")) {
					c11++;
				}
			}
			quarterly0 = c0 / total * 100;
			quarterly1 = c1 / total * 100;
			quarterly2 = c2 / total * 100;
			quarterly3 = c3 / total * 100;
			quarterly4 = c4 / total * 100;
			quarterly5 = c5 / total * 100;
			quarterly6 = c6 / total * 100;
			quarterly7 = c7 / total * 100;
			quarterly8 = c8 / total * 100;
			quarterly9 = c9 / total * 100;
			quarterly10 = c10 / total * 100;
			quarterly11 = c11 / total * 100;

			xVals = new ArrayList<String>();
			ArrayList<Entry> yVals1 = new ArrayList<Entry>();
			ArrayList<Integer> colors = new ArrayList<Integer>();
			int i = 0;
			if (quarterly0 != 0) {
				xVals.add("已签到");
				yVals1.add(new Entry(quarterly0, i++));
				colors.add(getResources().getColor(R.color.attend_0));
			}
			if (quarterly1 != 0) {
				xVals.add("请假");
				yVals1.add(new Entry(quarterly1, i++));
				colors.add(getResources().getColor(R.color.attend_1));
			}
//			if (quarterly2 != 0) {
//				xVals.add("病假");
//				yVals1.add(new Entry(quarterly2, i++));
//				colors.add(getResources().getColor(R.color.attend_2));
//			}
			if (quarterly3 != 0) {
				xVals.add("迟到");
				yVals1.add(new Entry(quarterly3, i++));
				colors.add(getResources().getColor(R.color.attend_3));
			}
			if (quarterly4 != 0) {
				xVals.add("旷课");
				yVals1.add(new Entry(quarterly4, i++));
				colors.add(getResources().getColor(R.color.attend_4));
			}
			if (quarterly5 != 0) {
				xVals.add("早退");
				yVals1.add(new Entry(quarterly5, i++));
				colors.add(getResources().getColor(R.color.attend_5));
			}
			if (quarterly6 != 0) {
				xVals.add("公假");
				yVals1.add(new Entry(quarterly6, i++));
				colors.add(getResources().getColor(R.color.attend_6));
			}
			if (quarterly7 != 0) {
				xVals.add("实习");
				yVals1.add(new Entry(quarterly7, i++));
				colors.add(getResources().getColor(R.color.attend_7));
			}
			if (quarterly8 != 0) {
				xVals.add("集训");
				yVals1.add(new Entry(quarterly8, i++));
				colors.add(getResources().getColor(R.color.attend_8));
			}
			if (quarterly9 != 0) {
				xVals.add("其它");
				yVals1.add(new Entry(quarterly9, i++));
				colors.add(getResources().getColor(R.color.attend_9));
			}
			if (quarterly10 != 0) {
				xVals.add("缺席");
				yVals1.add(new Entry(quarterly10, i++));
				colors.add(getResources().getColor(R.color.attend_10));
			}
			if (quarterly11 != 0) {
				xVals.add("未签到");
				yVals1.add(new Entry(quarterly11, i++));
				colors.add(getResources().getColor(R.color.attend_11));
			}
			PieDataSet dataSet = new PieDataSet(yVals1, "");
			// 设置饼图区块之间的距离
			dataSet.setSliceSpace(1);
			if (i > 1) {
				dataSet.setSelectionShift(30f); // 选中态多出的长度
				chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

					@Override
					public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
						String kqzt = "";
						if (xVals.get(e.getXIndex()).equals("已签到")) {
							kqzt = "0";
						} else if (xVals.get(e.getXIndex()).equals("请假")) {
							kqzt = "1";
						}
//						else if (xVals.get(e.getXIndex()).equals("病假")) {
//							kqzt = "2";
//						}
						else if (xVals.get(e.getXIndex()).equals("迟到")) {
							kqzt = "3";
						} else if (xVals.get(e.getXIndex()).equals("旷课")) {
							kqzt = "4";
						} else if (xVals.get(e.getXIndex()).equals("早退")) {
							kqzt = "5";
						} else if (xVals.get(e.getXIndex()).equals("公假")) {
							kqzt = "6";
						} else if (xVals.get(e.getXIndex()).equals("实习")) {
							kqzt = "7";
						} else if (xVals.get(e.getXIndex()).equals("集训")) {
							kqzt = "8";
						} else if (xVals.get(e.getXIndex()).equals("其它")) {
							kqzt = "13";
						} else if (xVals.get(e.getXIndex()).equals("缺席")) {
							kqzt = "10";
						} else if (xVals.get(e.getXIndex()).equals("未签到")) {
							kqzt = "11";
						}
						List<PersonnelAttendanceModel> list = new ArrayList<PersonnelAttendanceModel>();
						if (attendInfoModels != null && attendInfoModels.size() > 0) {
							for (PersonnelAttendanceModel model : attendInfoModels) {
								if (model.status.equals(kqzt)) {
									list.add(model);
								}
							}
						}
						MyAdapter adapter = new MyAdapter(list1);
						gv_attend1.setAdapter(adapter);
						MyAdapter adapter1 = new MyAdapter(list4);
						gv_attend2.setAdapter(adapter1);
						MyAdapter adapter2 = new MyAdapter(list3);
						gv_attend3.setAdapter(adapter2);
						MyAdapter adapter3 = new MyAdapter(list5);
						gv_attend4.setAdapter(adapter3);
						MyAdapter adapter4 = new MyAdapter(list2);
						gv_attend5.setAdapter(adapter4);
						MyAdapter adapter5 = new MyAdapter(list6);
						gv_attend6.setAdapter(adapter5);
					}

					@Override
					public void onNothingSelected() {
						MyAdapter adapter = new MyAdapter(list1);
						gv_attend1.setAdapter(adapter);
						MyAdapter adapter1 = new MyAdapter(list4);
						gv_attend2.setAdapter(adapter1);
						MyAdapter adapter2 = new MyAdapter(list3);
						gv_attend3.setAdapter(adapter2);
						MyAdapter adapter3 = new MyAdapter(list5);
						gv_attend4.setAdapter(adapter3);
						MyAdapter adapter4 = new MyAdapter(list2);
						gv_attend5.setAdapter(adapter4);
						MyAdapter adapter5 = new MyAdapter(list6);
						gv_attend6.setAdapter(adapter5);
					}
				});
			} else {
				dataSet.setSelectionShift(0f);
				chart.setOnChartValueSelectedListener(null);
			}

			// for (int c : ColorTemplate.VORDIPLOM_COLORS)
			// colors.add(c);
			// for (int c : ColorTemplate.JOYFUL_COLORS)
			// colors.add(c);
			// for (int c : ColorTemplate.COLORFUL_COLORS)
			// colors.add(c);
			// for (int c : ColorTemplate.LIBERTY_COLORS)
			// colors.add(c);
			// for (int c : ColorTemplate.PASTEL_COLORS)
			// colors.add(c);
			colors.add(ColorTemplate.getHoloBlue());
			dataSet.setColors(colors);

			PieData pieData = new PieData(xVals, dataSet);
			pieData.setValueFormatter(new PercentFormatter());
			pieData.setValueTextSize(20f);
			pieData.setValueTextColor(Color.WHITE);

			chart.setData(pieData);
			chart.highlightValues(null);
			chart.invalidate();
		} else {
			chart.setData(null);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_cx_back:
				if (editTextInputMethod != null) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm != null)
						imm.hideSoftInputFromWindow(editTextInputMethod.getWindowToken(), 0);
				}
				finish();
				break;
			case R.id.btn_attend:
				gv_student.setVisibility(View.INVISIBLE);
				updataAttendView();
				break;
			case R.id.btn_courseware:
				gv_student.setVisibility(View.VISIBLE);
				updataCoursewareView();
				break;

			case R.id.btn_classfeedback:
				gv_student.setVisibility(View.INVISIBLE);
				updataClassfeedbackView();
				break;
			case R.id.btn_premission_cancel2:
				if (application.dialog != null && application.dialog.isShowing()) {
					application.dialog.dismiss();
				}
				break;
			default: {
				QuestionNaireModel naireModel = (QuestionNaireModel) v.getTag();
				if (naireModel == null) {
					break;
				}
				if (editTexts != null && editTexts.size() > 0) {
					for (EditText editText : editTexts) {
						QuestionModel questionModel = (QuestionModel) editText.getTag();
						if (questionModel == null) {
							continue;
						}
						if (questionModel.questionnaireCode.equals(naireModel.questionnaireCode)
								&& !TextUtils.isEmpty(editText.getText().toString().trim())) {
							AnswerModel answerModel = new AnswerModel();
							answerModel.questionCode = questionModel.questionCode;
							answerModel.answer = editText.getText().toString().trim();
							answerModel.questionnaireCode = questionModel.questionnaireCode;
							answerModel.skjhid = skjhid;
							setAnswerModels(answerModel);
						}
					}
				}
				if (checkBoxs != null && checkBoxs.size() > 0) {
					for (CheckBox checkBox : checkBoxs) {
						QuestionModel questionModel = (QuestionModel) checkBox.getTag();
						if (questionModel == null) {
							continue;
						}
						if (questionModel.questionnaireCode.equals(naireModel.questionnaireCode) && checkBox.isChecked()) {
							if (answerModels != null && answerModels.size() > 0) {
								int temp = -1;
								AnswerModel answerModel = null;
								for (int i = 0; i < answerModels.size(); i++) {
									if (answerModels.get(i).questionCode.equals(questionModel.questionCode)) {
										temp = i;
										answerModel = answerModels.get(i);
										break;
									}
								}
								if (answerModel == null) {
									answerModel = new AnswerModel();
									answerModel.questionCode = questionModel.questionCode;
									answerModel.answer = checkBox.getText().toString();
									answerModel.questionnaireCode = questionModel.questionnaireCode;
									answerModel.skjhid = skjhid;
									answerModels.add(answerModel);
								} else {
									answerModel.answer = answerModel.answer + "," + checkBox.getText().toString();
									answerModels.set(temp, answerModel);
								}
							} else {
								AnswerModel answerModel = new AnswerModel();
								answerModel.questionCode = questionModel.questionCode;
								answerModel.answer = checkBox.getText().toString();
								answerModel.questionnaireCode = questionModel.questionnaireCode;
								answerModel.skjhid = skjhid;
								answerModels.add(answerModel);
							}
						}
					}
				}
				answerModelsCommit = new ArrayList<AnswerModel>();
				if (answerModels != null && answerModels.size() > 0) {
					for (AnswerModel answerModel : answerModels) {
						if (answerModel.questionnaireCode.equals(naireModel.questionnaireCode)) {
							answerModelsCommit.add(answerModel);
						}
					}
				}
				if (answerModelsCommit.size() > 0) {
					application.cardType = "QRY_PERSON_CLASS";
					DialogPermission();
				} else {
					application.showToast("请选择");
				}
				break;
			}
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		RadioButton rb = (RadioButton) findViewById(checkedId);
		if (rb == null) {
			return;
		}
		OptionModel optionModel = (OptionModel) rb.getTag();
		if (optionModel == null) {
			return;
		}
		QuestionModel questionModel = (QuestionModel) group.getTag();
		if (questionModel == null) {
			return;
		}
		AnswerModel answerModel = new AnswerModel();
		answerModel.questionCode = optionModel.questionCode;
		answerModel.answer = optionModel.optionName;
		answerModel.questionnaireCode = questionModel.questionnaireCode;
		answerModel.skjhid = skjhid;
		setAnswerModels(answerModel);
	}

	private Button btn_premission_cancel2;

	private void DialogPermission() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		application.dialog = builder.create();
		LayoutInflater inflater = LayoutInflater.from(application);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.dialog_permission_onlycard, null);
		application.dialog.setView(layout);
		application.dialog.show();
		application.dialog.getWindow().setLayout(900, 700);
		application.dialog.getWindow().setContentView(R.layout.dialog_permission_onlycard);
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
		btn_premission_cancel2 = (Button) application.dialog.findViewById(R.id.btn_premission_cancel2);
		btn_premission_cancel2.setOnClickListener(this);
	}

	private void setAnswerModels(AnswerModel answerModel) {
		if (answerModels != null && answerModels.size() > 0) {
			int temp = -1;
			for (int i = 0; i < answerModels.size(); i++) {
				if (answerModels.get(i).questionCode.equals(answerModel.questionCode)) {
					temp = i;
					break;
				}
			}
			if (temp == -1) {
				answerModels.add(answerModel);
			} else {
				answerModels.set(temp, answerModel);
			}
		} else {
			answerModels.add(answerModel);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case -1:
					application.showToast("加载失败");
					break;
				case 0:
					showProgressDialog();
					break;
				case 1:
					closeProgressDialog();
					break;
				case 2:
					application.createFloating();
					break;
				case 200:{
					StudentXKXXAdapter adapter = new StudentXKXXAdapter(CXAttendActivity.this,studentInfoModelList);
					gv_student.setAdapter(adapter);
					break;
				}
				case 520:{
					Toast.makeText(CXAttendActivity.this, "查无数据",Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
	};

	private void downLoadoffice(final String urlStr) {
		if (TextUtils.isEmpty(urlStr)) {
			return;
		}
		if (urlStr.endsWith(".doc") || urlStr.endsWith(".docx") || urlStr.endsWith(".xls") || urlStr.endsWith(".xlsx")
				|| urlStr.endsWith(".ppt") || urlStr.endsWith(".pptx") || urlStr.endsWith(".pdf")
				|| urlStr.endsWith(".txt")) {
			String[] s = urlStr.split("/");
			String name = s[s.length - 1];
			final String path = MyApplication.PATH_ROOT + MyApplication.PATH_NOTICE + "/" + name;
			final File file = new File(path);
			if (file.exists()) {
				openWPS(path);
				return;
			}
			showProgressDialog();
			application.executoService.execute(new Runnable() {
				@Override
				public void run() {
					if (urlStr.startsWith("http")) {
						if (application.httpDownLoad(urlStr, path)) {
							handler.sendEmptyMessage(1);
							openWPS(path);
						} else {
							handler.sendEmptyMessage(-1);
							handler.sendEmptyMessage(1);
						}
					} else if (urlStr.startsWith("ftp")) {
						FTPManager ftpManager = new FTPManager();
						if (ftpManager.connect(urlStr, path)) {
							if (ftpManager.download()) {
								handler.sendEmptyMessage(1);
								openWPS(path);
							} else {
								handler.sendEmptyMessage(-1);
								handler.sendEmptyMessage(1);
							}
							ftpManager.disConnect();
						} else {
							handler.sendEmptyMessage(-1);
							handler.sendEmptyMessage(1);
						}
					} else {
						handler.sendEmptyMessage(-1);
						handler.sendEmptyMessage(1);
					}
				}
			});
		}
	}

	private void openWPS(String path) {
		handler.sendEmptyMessage(2);
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("OpenMode", "ReadOnly");
		bundle.putBoolean("SendCloseBroad", true);
		bundle.putString("ThirdPackage", "com.yy.doorplate");
		bundle.putBoolean("ClearBuffer", true);
		bundle.putBoolean("ClearTrace", true);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setClassName(MyApplication.WPS, "cn.wps.moffice.documentmanager.PreStartActivity2");
		File file = new File(path);
		Uri uri = Uri.fromFile(file);
		intent.setData(uri);
		intent.putExtras(bundle);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(this, null, "加载中", false, false);
		}
	}

	private void closeProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			Log.e("查询可惜","查询可惜"+tag+"     getRunningActivityName:"+application.getRunningActivityName());
			if (tag.equals("permission_finish")) {
				finish();
			}

//			else if (tag.equals(HttpProcess.QRY_PERSONATTEND3) && !application.isUpdateEQ && application.isQryAttend
//					&& !application.isQryAttendTCP) {
//				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
//				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
//				application.isQryAttend = false;
//				if (!result) {
//					Log.e(TAG, tag + " " + msg);
//					application.showToast("获取考勤信息失败，原因：" + msg);
//				}
//				AttendInfoDatabase attendInfoDatabase = new AttendInfoDatabase();
//				attendInfoModels = attendInfoDatabase.query_by_skjhid(skjhid);
//				updata();
//				application.httpProcess.QryClassInfo(skjhid, null);
//			}
			else if (!isgo&&tag.equals(HttpProcess.QRY_PERSONATTEND)) {
				isgo=true;
				String personnelAttendanceList = intent.getStringExtra("personnelAttendanceList");
				attendInfoModels = JSONArray.parseArray(personnelAttendanceList, PersonnelAttendanceModel.class);
				//这里一会排序
				attendInfoModels=sortList(attendInfoModels);
				Log.e("查询","查询得到:"+attendInfoModels.size());
				//直接获取
				updata();
//				updataAttend();
				closeProgressDialog();
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (!result) {
					application.showToast("查询考勤失败，原因 : " + msg);
				}
			}
			else if (tag.equals(HttpProcess.QRY_CLASSINFO)) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (!result) {
					Log.e(TAG, tag + " " + msg);
					application.showToast("获取课程信息失败，原因：" + msg);
				}
				if (!TextUtils.isEmpty(intent.getStringExtra("sknl"))) {
					sknl = intent.getStringExtra("sknl");
				}
				if (!TextUtils.isEmpty(intent.getStringExtra("zynr"))) {
					zynr = intent.getStringExtra("zynr");
				}
				CoursewareDatabase coursewareDatabase = new CoursewareDatabase();
				coursewareModels = coursewareDatabase.query_by_skjhid(skjhid);
				updataCourseware();
				application.httpProcess.QryQuestClass(application.equInfoModel.jssysdm, skjhid);
			} else if (tag.equals(HttpProcess.QRY_QUSET_CLASS)) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (!result) {
					Log.e(TAG, tag + " " + msg);
					application.showToast("获取课后信息失败，原因：" + msg);
				}
				questionNaireModels = JSONArray.parseArray(intent.getStringExtra("classQuestionnaireList"),
						QuestionNaireModel.class);
				updataClassfeedback();
				closeProgressDialog();
			} else if (tag.equals("QRY_PERSON_CLASS")) {
				if (progressDialog == null) {
					progressDialog = ProgressDialog.show(CXAttendActivity.this, null, "提交中，请稍后", false, false);
				}
				String mCardNum = intent.getStringExtra("mCardNum");
				application.httpProcess.QryPerson(application.equInfoModel.jssysdm, mCardNum);
			} else if (tag.equals(HttpProcess.QRY_PERSON) && "QRY_PERSON_CLASS".equals(application.cardType)) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					String rybh = intent.getStringExtra("rybh");
					if ("1".equals(intent.getStringExtra("js"))) {
						application.showToast("教师不可操作");
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
							progressDialog = null;
						}
						return;
					}
					if (TextUtils.isEmpty(rybh)) {
						application.showToast("未找到该人员");
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
							progressDialog = null;
						}
					} else {
						for (int i = 0; i < answerModelsCommit.size(); i++) {
							AnswerModel answerModel = answerModelsCommit.get(i);
							answerModel.replyPerson = rybh;
							answerModelsCommit.set(i, answerModel);
						}
						application.httpProcess.commitQuestClass(application.equInfoModel.jssysdm, answerModelsCommit);
					}
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("未找到该人员");
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
						progressDialog = null;
					}
				}
			} else if (tag.equals(HttpProcess.COMMIT_QUEST_CLASS)) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					application.showToast("提交成功");
					if (application.dialog != null && application.dialog.isShowing()) {
						application.dialog.dismiss();
					}
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

	private class MyAdapter extends BaseAdapter {

		private List<PersonnelAttendanceModel> attendInfoModels = null;

		public MyAdapter(List<PersonnelAttendanceModel> attendInfoModels ) {
			this.attendInfoModels = attendInfoModels;
		}

		@Override
		public int getCount() {
			return attendInfoModels.size();
		}

		@Override
		public Object getItem(int position) {
			return attendInfoModels.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			private RelativeLayout ly_item_attend;
			private ImageView img_item_attend;
			private TextView txt_item_attend;

		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (viewHolder == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(application).inflate(R.layout.cx_item_attend, null);
				viewHolder.ly_item_attend = (RelativeLayout) view.findViewById(R.id.ly_item_attend);
				viewHolder.img_item_attend = (ImageView) view.findViewById(R.id.img_item_attend);
				viewHolder.txt_item_attend = (TextView) view.findViewById(R.id.txt_item_attend);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
//			view.setLayoutParams(new GridView.LayoutParams(90, 90));
			viewHolder.txt_item_attend.setText(attendInfoModels.get(position).xm);
//			if ("2".equals(personnelAttendanceModelList.get(position).xb)) {
			viewHolder.img_item_attend.setImageResource(R.drawable.img_people);
//			} else {
//				viewHolder.img_item_attend.setImageResource(R.drawable.cx_img_man);
//			}
			String kqzt = attendInfoModels.get(position).status.split(",")[0];
			if ("0".equals(kqzt)) {
				viewHolder.txt_item_attend.setBackgroundResource(R.drawable.cx_btn_attend_0);
			} else if ("1".equals(kqzt)||"2".equals((kqzt))) {
				viewHolder.txt_item_attend.setBackgroundResource(R.drawable.cx_btn_attend_1);
			}
//			else if ("2".equals(kqzt)) {
//				viewHolder.txt_item_attend.setBackgroundResource(R.drawable.cx_btn_attend_2);
//			}
			else if ("3".equals(kqzt)) {
				viewHolder.txt_item_attend.setBackgroundResource(R.drawable.cx_btn_attend_3);
			} else if ("4".equals(kqzt)) {
				viewHolder.txt_item_attend.setBackgroundResource(R.drawable.cx_btn_attend_4);
			} else if ("5".equals(kqzt)) {
				viewHolder.txt_item_attend.setBackgroundResource(R.drawable.cx_btn_attend_5);
			} else if ("6".equals(kqzt)) {
				viewHolder.txt_item_attend.setBackgroundResource(R.drawable.cx_btn_attend_6);
			} else if ("7".equals(kqzt)) {
				viewHolder.txt_item_attend.setBackgroundResource(R.drawable.cx_btn_attend_7);
			} else if ("8".equals(kqzt)) {
				viewHolder.txt_item_attend.setBackgroundResource(R.drawable.cx_btn_attend_8);
			} else if ("13".equals(kqzt)) {
				viewHolder.txt_item_attend.setBackgroundResource(R.drawable.cx_btn_attend_9);
			} else if ("10".equals(kqzt)) {
				viewHolder.txt_item_attend.setBackgroundResource(R.drawable.cx_btn_attend_10);
			} else if ("11".equals(kqzt)) {
				viewHolder.txt_item_attend.setBackgroundResource(R.drawable.cx_btn_attend_11);
			}
			return view;
		}
	}


	@Override
	protected void onStart() {
		super.onStart();
		application.handler_touch.removeMessages(0);
		application.handler_touch.sendEmptyMessageDelayed(0, application.screensaver_time * 1000);
	}

	@Override
	protected void onResume() {
		super.onResume();
		application.destroyFloating();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageLoader.clearCache();
		imageLoader.cancelTask();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
		application.destroyFloating();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getAction()) {
			case KeyEvent.ACTION_DOWN:
				application.handler_touch.removeMessages(0);
				break;
			case KeyEvent.ACTION_UP:
				application.handler_touch.sendEmptyMessageDelayed(0, application.screensaver_time * 1000);
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
				application.handler_touch.sendEmptyMessageDelayed(0, application.screensaver_time * 1000);
				break;
		}
		return super.dispatchTouchEvent(ev);
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

	public List<PersonnelAttendanceModel> sortList(List<PersonnelAttendanceModel> personnelAttendanceModelList){
		for(int i=0;i<personnelAttendanceModelList.size();i++){
			String kqzt = personnelAttendanceModelList.get(i).status.split(",")[0];
			Log.e("查询","查询得到:"+kqzt);
			if ("1".equals(kqzt)||"2".equals(kqzt)) {
				//请假
				list2.add(personnelAttendanceModelList.get(i));
			}  else if ("3".equals(kqzt)) {
				//迟到
				list3.add(personnelAttendanceModelList.get(i));
			} else if ("4".equals(kqzt)) {
				//旷课
				list4.add(personnelAttendanceModelList.get(i));
			} else if ("5".equals(kqzt)) {
				//早退
				list5.add(personnelAttendanceModelList.get(i));
			} else 	if ("0".equals(kqzt)) {
				//出勤
				list6.add(personnelAttendanceModelList.get(i));
			}else{
				//未签到
				list1.add(personnelAttendanceModelList.get(i));
			}
		}
		attendInfoModels.clear();
		attendInfoModels.addAll(list1);
		attendInfoModels.addAll(list2);
		attendInfoModels.addAll(list3);
		attendInfoModels.addAll(list4);
		attendInfoModels.addAll(list5);
		attendInfoModels.addAll(list6);
		if(list1.size()==0){
		    tv1.setVisibility(View.GONE);
		    gv_attend1.setVisibility(View.GONE);
        }
        if(list2.size()==0){
            tv5.setVisibility(View.GONE);
            gv_attend5.setVisibility(View.GONE);
        }
        if(list3.size()==0){
            tv3.setVisibility(View.GONE);
            gv_attend3.setVisibility(View.GONE);
        }
        if(list4.size()==0){
            tv2.setVisibility(View.GONE);
            gv_attend2.setVisibility(View.GONE);
        }
        if(list5.size()==0){
            tv4.setVisibility(View.GONE);
            gv_attend4.setVisibility(View.GONE);
        }
        if(list6.size()==0){
            tv6.setVisibility(View.GONE);
            gv_attend6.setVisibility(View.GONE);
        }
		return attendInfoModels;
	}
}
