package com.cx.doorplate.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.OptionDatabase;
import com.yy.doorplate.database.QuestionDatabase;
import com.yy.doorplate.database.QuestionNaireDatabase;
import com.yy.doorplate.model.AnswerModel;
import com.yy.doorplate.model.OptionModel;
import com.yy.doorplate.model.QuestionModel;
import com.yy.doorplate.model.QuestionNaireModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;
import com.yy.doorplate.view.CustomTextView;

public class CXQuestActivity extends Activity implements OnClickListener,
		OnItemClickListener, RadioGroup.OnCheckedChangeListener {

	private final String TAG = "CXQuestActivity";

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private RelativeLayout ly_quset_list, ly_quset_details, ly_quset_details1,
			ly_quset_details2;
	private LinearLayout ly_quest_question1, ly_quest_question2;
	private Button btn_cx_back, btn_quest_start, btn_quest_next,
			btn_quest_cancel;
	private TextView txt_weather, txt_quest_title, txt_quest_desc,
			txt_details_title, txt_quest_question1, txt_quest_question11,
			txt_quest_question2, txt_quest_question22, txt_quest_list_title,img_quest_title;
	private EditText edt_quest_question1, edt_quest_question2;
	private RadioGroup rg_quest_question1, rg_quest_question2;
	private TextClock textClock;
	private ImageView  img_quest;
	
	private ListView list_quest;
	private ScrollView sv_quset_details1, sv_quset_details2;

	private List<QuestionNaireModel> questionNaireModels = null;
	private QuestionNaireModel questionNaireModel;
	private List<QuestionModel> questionModels = null;
	private int quest_total = 0, quest_page = 0, page_count = 2;

	private List<CheckBox> checkBoxs1 = new ArrayList<CheckBox>(),
			checkBoxs2 = new ArrayList<CheckBox>();

	private ImageLoader imageLoader = null;

	private ProgressDialog progressDialog = null;

	private boolean isInputMethod = false;

	// 1学校调查；2班级调查
	private int type = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_activity_quest);
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
		ly_quset_list = (RelativeLayout) findViewById(R.id.ly_quset_list);
		ly_quset_details = (RelativeLayout) findViewById(R.id.ly_quset_details);
		ly_quset_details1 = (RelativeLayout) findViewById(R.id.ly_quset_details1);
		ly_quset_details2 = (RelativeLayout) findViewById(R.id.ly_quset_details2);
		ly_quest_question1 = (LinearLayout) findViewById(R.id.ly_quest_question1);
		ly_quest_question2 = (LinearLayout) findViewById(R.id.ly_quest_question2);
		btn_cx_back = (Button) findViewById(R.id.btn_cx_back);
		btn_quest_start = (Button) findViewById(R.id.btn_quest_start);
		btn_quest_next = (Button) findViewById(R.id.btn_quest_next);
		btn_quest_cancel = (Button) findViewById(R.id.btn_quest_cancel);
		txt_weather = (TextView) findViewById(R.id.txt_weather);
		txt_quest_title = (TextView) findViewById(R.id.txt_quest_title);
		txt_quest_desc = (TextView) findViewById(R.id.txt_quest_desc);
		txt_details_title = (TextView) findViewById(R.id.txt_details_title);
		txt_quest_question1 = (TextView) findViewById(R.id.txt_quest_question1);
		txt_quest_question2 = (TextView) findViewById(R.id.txt_quest_question2);
		txt_quest_question11 = (TextView) findViewById(R.id.txt_quest_question11);
		txt_quest_question22 = (TextView) findViewById(R.id.txt_quest_question22);
		txt_quest_list_title = (TextView) findViewById(R.id.txt_quest_list_title);
		edt_quest_question1 = (EditText) findViewById(R.id.edt_quest_question1);
		edt_quest_question2 = (EditText) findViewById(R.id.edt_quest_question2);
		rg_quest_question1 = (RadioGroup) findViewById(R.id.rg_quest_question1);
		rg_quest_question2 = (RadioGroup) findViewById(R.id.rg_quest_question2);
		img_quest_title = (TextView) findViewById(R.id.img_quest_title);
		img_quest = (ImageView) findViewById(R.id.img_quest);
		list_quest = (ListView) findViewById(R.id.list_quest);
		sv_quset_details1 = (ScrollView) findViewById(R.id.sv_quset_details1);
		sv_quset_details2 = (ScrollView) findViewById(R.id.sv_quset_details2);

		textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("yyyy年MM月dd日\nEEEE      HH : mm");
		txt_weather.setText(application.currentCity.trim() + "   "
				+ application.temperature.trim() + "\n"
				+ application.weather.trim());
		if (type == 1) {
//			img_quest_title.setImageResource(R.drawable.cx_btn_school_quest);
			img_quest_title.setText("校园调查");
			txt_quest_list_title.setText("校园问卷档案库");
		} else if (type == 2) {
//			img_quest_title.setImageResource(R.drawable.cx_img_quest_title2);
			img_quest_title.setText("班级调查");
			txt_quest_list_title.setText("班级问卷档案库");
		}

		btn_cx_back.setOnClickListener(this);
		btn_quest_start.setOnClickListener(this);
		btn_quest_next.setOnClickListener(this);
		btn_quest_cancel.setOnClickListener(this);
//		edt_quest_question1.setOnClickListener(this);
//		edt_quest_question2.setOnClickListener(this);
		list_quest.setOnItemClickListener(this);
		rg_quest_question1.setOnCheckedChangeListener(this);
		rg_quest_question2.setOnCheckedChangeListener(this);

		QuestionNaireDatabase database = new QuestionNaireDatabase();
		questionNaireModels = database.query("questionnaireBelongKind = ?",
				new String[] { type + "" });
		if (questionNaireModels != null && questionNaireModels.size() > 0) {
			CXQuestAdapter adapter = new CXQuestAdapter();
			list_quest.setAdapter(adapter);
			updata_naire(0);
		} else {
			ly_quset_list.setVisibility(View.INVISIBLE);
			ly_quset_details.setVisibility(View.INVISIBLE);
		}
	}

	private void updata_naire(int i) {
		if (questionNaireModels != null && questionNaireModels.size() > i) {
			questionNaireModel = questionNaireModels.get(i);
			ly_quset_list.setVisibility(View.VISIBLE);
			ly_quset_details.setVisibility(View.INVISIBLE);
			txt_quest_title.setText(questionNaireModel.title);
			txt_quest_desc.setText(questionNaireModel.questionnaireDesc);
			if (!TextUtils.isEmpty(questionNaireModel.questionnaireIconAddress)) {
				String[] s = questionNaireModel.questionnaireIconAddress
						.split("/");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PICTURE + "/quest_"
						+ s[s.length - 1];
				imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						if (bitmap != null) {
							img_quest.setImageBitmap(bitmap);
						} else {
							img_quest
									.setImageResource(R.drawable.img_fragment_book);
						}
					}
				}, 612, 400);
			} else {
				img_quest.setImageResource(R.drawable.img_fragment_book);
			}
		}
	}

	private void updata_quest() {
		sv_quset_details1.scrollTo(0, 0);
		sv_quset_details2.scrollTo(0, 0);
		if (questionModels != null
				&& questionModels.size() > quest_page * page_count) {
			if (quest_page == quest_total - 1) {
				btn_quest_next
						.setBackgroundResource(R.drawable.cx_btn_quset_commit);
			} else {
				btn_quest_next
						.setBackgroundResource(R.drawable.cx_btn_quset_next);
			}
			ly_quset_details1.setVisibility(View.VISIBLE);
			txt_quest_question1.setText((quest_page * page_count + 1) + "");
			txt_quest_question11.setText(questionModels.get(quest_page
					* page_count).question);
			if (questionModels.get(quest_page * page_count).answerType
					.equals("RADIO")) {
				edt_quest_question1.setVisibility(View.INVISIBLE);
				rg_quest_question1.setVisibility(View.VISIBLE);
				ly_quest_question1.setVisibility(View.INVISIBLE);
				rg_quest_question1.removeAllViews();
				OptionDatabase database = new OptionDatabase();
				List<OptionModel> optionModels = database
						.query_by_questionCode(questionModels.get(quest_page
								* page_count).questionCode);
				if (optionModels != null && optionModels.size() > 0) {
					questionModels.get(quest_page * page_count).optionList = optionModels;
					for (OptionModel model : optionModels) {
						RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
								RadioGroup.LayoutParams.WRAP_CONTENT,
								RadioGroup.LayoutParams.WRAP_CONTENT);
						params.setMargins(0, 20, 0, 0);
						RadioButton tempButton = new RadioButton(this);
						tempButton.setLayoutParams(params);
						tempButton.setPadding(20, 0, 0, 0);
						tempButton.setText(model.optionName);
						tempButton.setTextSize(30);
						rg_quest_question1.addView(tempButton, params);
					}
				}
			} else if (questionModels.get(quest_page * page_count).answerType
					.equals("MULTISELECT")) {
				edt_quest_question1.setVisibility(View.INVISIBLE);
				rg_quest_question1.setVisibility(View.INVISIBLE);
				ly_quest_question1.setVisibility(View.VISIBLE);
				checkBoxs1.clear();
				ly_quest_question1.removeAllViews();
				OptionDatabase database = new OptionDatabase();
				List<OptionModel> optionModels = database
						.query_by_questionCode(questionModels.get(quest_page
								* page_count).questionCode);
				if (optionModels != null && optionModels.size() > 0) {
					questionModels.get(quest_page * page_count).optionList = optionModels;
					for (OptionModel model : optionModels) {
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.WRAP_CONTENT);
						params.setMargins(0, 20, 0, 0);
						CheckBox checkBox = new CheckBox(this);
						checkBox.setLayoutParams(params);
						checkBox.setPadding(20, 0, 0, 0);
						checkBox.setText(model.optionName);
						checkBox.setTextSize(30);
						ly_quest_question1.addView(checkBox);
						checkBoxs1.add(checkBox);
					}
				}
			} else if (questionModels.get(quest_page * page_count).answerType
					.equals("TEXT")) {
				edt_quest_question1.setText("");
				edt_quest_question1.setVisibility(View.VISIBLE);
				rg_quest_question1.setVisibility(View.INVISIBLE);
				ly_quest_question1.setVisibility(View.INVISIBLE);
				edt_quest_question1.setFocusable(true);
				edt_quest_question1.setFocusableInTouchMode(true);
				edt_quest_question1.requestFocus();
				edt_quest_question1.requestFocusFromTouch();
				InputMethodManager inputManager = (InputMethodManager) edt_quest_question1
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				if (inputManager != null)
					inputManager.showSoftInput(edt_quest_question1, 0);
				isInputMethod = true;
			}
		} else {
			ly_quset_details1.setVisibility(View.INVISIBLE);
		}
		if (questionModels != null
				&& questionModels.size() > (quest_page * page_count + 1)) {
			ly_quset_details2.setVisibility(View.VISIBLE);
			txt_quest_question2.setText((quest_page * page_count + 2) + "");
			txt_quest_question22.setText(questionModels.get(quest_page
					* page_count + 1).question);
			if (questionModels.get(quest_page * page_count + 1).answerType
					.equals("RADIO")) {
				edt_quest_question2.setVisibility(View.INVISIBLE);
				rg_quest_question2.setVisibility(View.VISIBLE);
				ly_quest_question2.setVisibility(View.INVISIBLE);

				rg_quest_question2.removeAllViews();
				OptionDatabase database = new OptionDatabase();
				List<OptionModel> optionModels = database
						.query_by_questionCode(questionModels.get(quest_page
								* page_count + 1).questionCode);
				if (optionModels != null && optionModels.size() > 0) {
					questionModels.get(quest_page * page_count + 1).optionList = optionModels;
					for (OptionModel model : optionModels) {
						RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
								RadioGroup.LayoutParams.WRAP_CONTENT,
								RadioGroup.LayoutParams.WRAP_CONTENT);
						params.setMargins(0, 20, 0, 0);
						RadioButton tempButton = new RadioButton(this);
						tempButton.setLayoutParams(params);
						tempButton.setPadding(20, 0, 0, 0);
						tempButton.setText(model.optionName);
						tempButton.setTextSize(30);
						rg_quest_question2.addView(tempButton, params);
					}
				}
			} else if (questionModels.get(quest_page * page_count + 1).answerType
					.equals("MULTISELECT")) {
				edt_quest_question2.setVisibility(View.INVISIBLE);
				rg_quest_question2.setVisibility(View.INVISIBLE);
				ly_quest_question2.setVisibility(View.VISIBLE);

				checkBoxs2.clear();
				ly_quest_question2.removeAllViews();
				OptionDatabase database = new OptionDatabase();
				List<OptionModel> optionModels = database
						.query_by_questionCode(questionModels.get(quest_page
								* page_count + 1).questionCode);
				if (optionModels != null && optionModels.size() > 0) {
					questionModels.get(quest_page * page_count + 1).optionList = optionModels;
					for (OptionModel model : optionModels) {
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.WRAP_CONTENT);
						params.setMargins(0, 20, 0, 0);
						CheckBox checkBox = new CheckBox(this);
						checkBox.setLayoutParams(params);
						checkBox.setPadding(20, 0, 0, 0);
						checkBox.setText(model.optionName);
						checkBox.setTextSize(30);
						ly_quest_question2.addView(checkBox);
						checkBoxs2.add(checkBox);
					}
				}
			} else if (questionModels.get(quest_page * page_count + 1).answerType
					.equals("TEXT")) {
				edt_quest_question2.setText("");
				edt_quest_question2.setVisibility(View.VISIBLE);
				rg_quest_question2.setVisibility(View.INVISIBLE);
				ly_quest_question2.setVisibility(View.INVISIBLE);
				edt_quest_question2.setFocusable(true);
				edt_quest_question2.setFocusableInTouchMode(true);
				edt_quest_question2.requestFocus();
				edt_quest_question2.requestFocusFromTouch();
				InputMethodManager inputManager = (InputMethodManager) edt_quest_question2
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				if (inputManager != null)
					inputManager.showSoftInput(edt_quest_question2, 0);
				isInputMethod = true;
			}
		} else {
			ly_quset_details2.setVisibility(View.INVISIBLE);
		}
	}

	private void next() {
		if (questionModels != null
				&& questionModels.size() > quest_page * page_count) {
			if (questionModels.get(quest_page * page_count).answerType
					.equals("MULTISELECT")) {
				if (checkBoxs1.size() > 0) {
					AnswerModel answerModel = new AnswerModel();
					answerModel.questionCode = questionModels.get(quest_page
							* page_count).questionCode;
					answerModel.answer = "";
					for (int i = 0; i < checkBoxs1.size(); i++) {
						if (checkBoxs1.get(i).isChecked()) {
							if (TextUtils.isEmpty(answerModel.answer)) {
								answerModel.answer = questionModels
										.get(quest_page * page_count).optionList
										.get(i).optionName;
							} else {
								answerModel.answer = answerModel.answer
										+ ","
										+ questionModels.get(quest_page
												* page_count).optionList.get(i).optionName;
							}
						}
					}
					questionModels.get(quest_page * page_count).answerInfo = answerModel;
				}
			} else if (questionModels.get(quest_page * page_count).answerType
					.equals("TEXT")) {
				AnswerModel answerModel = new AnswerModel();
				answerModel.questionCode = questionModels.get(quest_page
						* page_count).questionCode;
				answerModel.answer = edt_quest_question1.getText().toString();
				questionModels.get(quest_page * page_count).answerInfo = answerModel;
			}
		}
		if (questionModels != null
				&& questionModels.size() > (quest_page * page_count + 1)) {
			if (questionModels.get((quest_page * page_count + 1)).answerType
					.equals("MULTISELECT")) {
				if (checkBoxs2.size() > 0) {
					AnswerModel answerModel = new AnswerModel();
					answerModel.questionCode = questionModels.get((quest_page
							* page_count + 1)).questionCode;
					answerModel.answer = "";
					for (int i = 0; i < checkBoxs2.size(); i++) {
						if (checkBoxs2.get(i).isChecked()) {
							if (TextUtils.isEmpty(answerModel.answer)) {
								answerModel.answer = questionModels
										.get((quest_page * page_count + 1)).optionList
										.get(i).optionName;
							} else {
								answerModel.answer = answerModel.answer
										+ ","
										+ questionModels.get((quest_page
												* page_count + 1)).optionList
												.get(i).optionName;
							}
						}
					}
					questionModels.get((quest_page * page_count + 1)).answerInfo = answerModel;
				}
			} else if (questionModels.get((quest_page * page_count + 1)).answerType
					.equals("TEXT")) {
				AnswerModel answerModel = new AnswerModel();
				answerModel.questionCode = questionModels.get((quest_page
						* page_count + 1)).questionCode;
				answerModel.answer = edt_quest_question2.getText().toString();
				questionModels.get((quest_page * page_count + 1)).answerInfo = answerModel;
			}
		}
		if (quest_page == quest_total - 1) {
			application.httpProcess.CommitQuest(
					application.equInfoModel.jssysdm, questionNaireModel);
			if (progressDialog == null) {
				progressDialog = ProgressDialog.show(this, null, "提交中...",
						false, false);
			}
		} else {
			quest_page++;
			updata_quest();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cx_back: {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null) {
				imm.hideSoftInputFromWindow(
						edt_quest_question1.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(
						edt_quest_question2.getWindowToken(), 0);
			}
			finish();
			break;
		}
		case R.id.btn_quest_start: {
			QuestionDatabase database = new QuestionDatabase();
			questionModels = database
					.query_by_questionnaireCode(questionNaireModel.questionnaireCode);
			if (questionModels != null && questionModels.size() > 0) {
				questionNaireModel.questionList = questionModels;
				if (questionModels.size() % page_count == 0) {
					quest_total = questionModels.size() / page_count;
				} else {
					quest_total = questionModels.size() / page_count + 1;
				}
				quest_page = 0;
				ly_quset_list.setVisibility(View.INVISIBLE);
				ly_quset_details.setVisibility(View.VISIBLE);
				txt_details_title.setText(questionNaireModel.title);
				updata_quest();
			}
			break;
		}
		case R.id.btn_quest_next: {
			next();
			break;
		}
		case R.id.btn_quest_cancel: {
			ly_quset_list.setVisibility(View.VISIBLE);
			ly_quset_details.setVisibility(View.INVISIBLE);
			questionModels = null;
			quest_total = 0;
			quest_page = 0;
			if (isInputMethod) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(
							edt_quest_question1.getWindowToken(), 0);
					imm.hideSoftInputFromWindow(
							edt_quest_question2.getWindowToken(), 0);
				}
			}
			break;
		}
		case R.id.edt_quest_question1:
			if (isInputMethod) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null)
					imm.hideSoftInputFromWindow(
							edt_quest_question1.getWindowToken(), 0);
				isInputMethod = false;
			} else {
				InputMethodManager imm = (InputMethodManager) edt_quest_question1
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				if (imm != null)
					imm.showSoftInput(edt_quest_question1, 0);
				isInputMethod = true;
			}
			break;
		case R.id.edt_quest_question2:
			if (isInputMethod) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null)
					imm.hideSoftInputFromWindow(
							edt_quest_question2.getWindowToken(), 0);
				isInputMethod = false;
			} else {
				InputMethodManager imm = (InputMethodManager) edt_quest_question2
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				if (imm != null)
					imm.showSoftInput(edt_quest_question2, 0);
				isInputMethod = true;
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		updata_naire(arg2);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Log.d(TAG, checkedId + "");
		switch (group.getId()) {
		case R.id.rg_quest_question1: {
			if (questionModels.get(quest_page * page_count).optionList != null
					&& questionModels.get(quest_page * page_count).optionList
							.size() > 0) {
				RadioButton button = (RadioButton) CXQuestActivity.this
						.findViewById(checkedId);
				AnswerModel answerModel = new AnswerModel();
				answerModel.questionCode = questionModels.get(quest_page
						* page_count).questionCode;
				answerModel.answer = button.getText().toString();
				questionModels.get(quest_page * page_count).answerInfo = answerModel;
			}
			break;
		}
		case R.id.rg_quest_question2: {
			if (questionModels.get(quest_page * page_count + 1).optionList != null
					&& questionModels.get(quest_page * page_count + 1).optionList
							.size() > 0) {
				RadioButton button = (RadioButton) CXQuestActivity.this
						.findViewById(checkedId);
				AnswerModel answerModel = new AnswerModel();
				answerModel.questionCode = questionModels.get(quest_page
						* page_count + 1).questionCode;
				answerModel.answer = button.getText().toString();
				questionModels.get(quest_page * page_count + 1).answerInfo = answerModel;
			}
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
			if (tag.equals("permission_finish")) {
				finish();
			} else if (tag.equals(HttpProcess.COMMIT_QUEST)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					application.showToast("问卷提交成功");

					ly_quset_list.setVisibility(View.VISIBLE);
					ly_quset_details.setVisibility(View.INVISIBLE);
					questionModels = null;
					quest_total = 0;
					quest_page = 0;
					if (isInputMethod) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						if (imm != null) {
							imm.hideSoftInputFromWindow(
									edt_quest_question1.getWindowToken(), 0);
							imm.hideSoftInputFromWindow(
									edt_quest_question2.getWindowToken(), 0);
						}
					}
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("问卷提交失败，原因：" + msg);
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

	public class CXQuestAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return questionNaireModels.size();
		}

		@Override
		public Object getItem(int position) {
			return questionNaireModels.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			private TextView txt_item_quest_time;
			private CustomTextView txt_item_quest_title;
		}

		@Override
		public View getView(final int position, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder = null;
			if (view == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(application).inflate(
						R.layout.cx_item_quest, null);
				viewHolder.txt_item_quest_time = (TextView) view
						.findViewById(R.id.txt_item_quest_time);
				viewHolder.txt_item_quest_title = (CustomTextView) view
						.findViewById(R.id.txt_item_quest_title);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			if (!TextUtils
					.isEmpty(questionNaireModels.get(position).createTime)
					&& questionNaireModels.get(position).createTime.length() > 10) {
				viewHolder.txt_item_quest_time.setText(questionNaireModels
						.get(position).createTime.substring(0, 10));
			}
			viewHolder.txt_item_quest_title.setText(questionNaireModels
					.get(position).title);
			return view;
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
