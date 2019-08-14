package com.yy.doorplate.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.adapter.QuestAdapter;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.OptionDatabase;
import com.yy.doorplate.database.QuestionDatabase;
import com.yy.doorplate.database.QuestionNaireDatabase;
import com.yy.doorplate.model.AnswerModel;
import com.yy.doorplate.model.OptionModel;
import com.yy.doorplate.model.QuestionModel;
import com.yy.doorplate.model.QuestionNaireModel;

public class QuestActivity extends Activity implements OnClickListener,
		OnItemClickListener, RadioGroup.OnCheckedChangeListener {

	private final String TAG = "QuestActivity";

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private RelativeLayout ly_quest_naire, ly_quest;
	private Button btn_quest_back, btn_quest_start, btn_quest_next;
	private TextView txt_quest_title, txt_quest_desc, txt_quest_question;
	private ListView list_quest;
	private EditText edt_quest_question;
	private RadioGroup rg_quest_question;
	private LinearLayout ly_quest_question;

	private List<QuestionNaireModel> questionNaireModels = null;
	private QuestionNaireModel questionNaireModel;
	private List<QuestionModel> questionModels = null;

	private List<CheckBox> checkBoxs = new ArrayList<CheckBox>();

	private QuestAdapter adapter;

	private int quest_i = 0;

	private ProgressDialog progressDialog = null;
	private boolean isInputMethod = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quest);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initView();
	}

	private void initView() {
		ly_quest_naire = (RelativeLayout) findViewById(R.id.ly_quest_naire);
		ly_quest = (RelativeLayout) findViewById(R.id.ly_quest);
		btn_quest_back = (Button) findViewById(R.id.btn_quest_back);
		btn_quest_start = (Button) findViewById(R.id.btn_quest_start);
		btn_quest_next = (Button) findViewById(R.id.btn_quest_next);
		txt_quest_title = (TextView) findViewById(R.id.txt_quest_title);
		txt_quest_desc = (TextView) findViewById(R.id.txt_quest_desc);
		txt_quest_question = (TextView) findViewById(R.id.txt_quest_question);
		list_quest = (ListView) findViewById(R.id.list_quest);
		edt_quest_question = (EditText) findViewById(R.id.edt_quest_question);
		rg_quest_question = (RadioGroup) findViewById(R.id.rg_quest_question);
		ly_quest_question = (LinearLayout) findViewById(R.id.ly_quest_question);

		btn_quest_back.setOnClickListener(this);
		btn_quest_start.setOnClickListener(this);
		btn_quest_next.setOnClickListener(this);
		list_quest.setOnItemClickListener(this);
//		edt_quest_question.setOnClickListener(this);
		rg_quest_question.setOnCheckedChangeListener(this);

		QuestionNaireDatabase database = new QuestionNaireDatabase();
		questionNaireModels = database.query_all();
		if (questionNaireModels != null && questionNaireModels.size() > 0) {
			adapter = new QuestAdapter(application, questionNaireModels,
					list_quest);
			adapter.setSelect(0);
			list_quest.setAdapter(adapter);
			updata_naire(0);
		} else {
			list_quest.setAdapter(null);
			ly_quest_naire.setVisibility(View.INVISIBLE);
			ly_quest.setVisibility(View.INVISIBLE);
		}
	}

	private void updata_naire(int i) {
		if (questionNaireModels != null && questionNaireModels.size() > i) {
			questionNaireModel = questionNaireModels.get(i);
			ly_quest_naire.setVisibility(View.VISIBLE);
			ly_quest.setVisibility(View.INVISIBLE);
			txt_quest_title.setText(questionNaireModel.title);
			txt_quest_desc.setText(questionNaireModel.questionnaireDesc);
		}
	}

	private void updata_quest(int i) {
		quest_i = i;
		if (questionModels != null && questionModels.size() > quest_i) {
			ly_quest_naire.setVisibility(View.INVISIBLE);
			ly_quest.setVisibility(View.VISIBLE);
			txt_quest_question.setText(questionModels.get(quest_i).question);
			if (quest_i == questionModels.size() - 1) {
				btn_quest_next.setText("提交");
			} else {
				btn_quest_next.setText("下一题");
			}
			if (questionModels.get(quest_i).answerType.equals("RADIO")) {
				edt_quest_question.setVisibility(View.INVISIBLE);
				rg_quest_question.setVisibility(View.VISIBLE);
				ly_quest_question.setVisibility(View.INVISIBLE);

				rg_quest_question.removeAllViews();
				OptionDatabase database = new OptionDatabase();
				List<OptionModel> optionModels = database
						.query_by_questionCode(questionModels.get(quest_i).questionCode);
				if (optionModels != null && optionModels.size() > 0) {
					questionModels.get(quest_i).optionList = optionModels;
					for (OptionModel model : optionModels) {
						RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
								RadioGroup.LayoutParams.WRAP_CONTENT,
								RadioGroup.LayoutParams.WRAP_CONTENT);
						params.setMargins(0, 30, 0, 0);
						RadioButton tempButton = new RadioButton(this);
						tempButton.setLayoutParams(params);
						tempButton.setPadding(20, 0, 0, 0);
						tempButton.setText(model.optionName);
						tempButton.setTextSize(30);
						rg_quest_question.addView(tempButton, params);
					}
				}
			} else if (questionModels.get(quest_i).answerType
					.equals("MULTISELECT")) {
				edt_quest_question.setVisibility(View.INVISIBLE);
				rg_quest_question.setVisibility(View.INVISIBLE);
				ly_quest_question.setVisibility(View.VISIBLE);

				checkBoxs.clear();
				ly_quest_question.removeAllViews();
				OptionDatabase database = new OptionDatabase();
				List<OptionModel> optionModels = database
						.query_by_questionCode(questionModels.get(quest_i).questionCode);
				if (optionModels != null && optionModels.size() > 0) {
					questionModels.get(quest_i).optionList = optionModels;
					for (OptionModel model : optionModels) {
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.WRAP_CONTENT);
						params.setMargins(0, 30, 0, 0);
						CheckBox checkBox = new CheckBox(this);
						checkBox.setLayoutParams(params);
						checkBox.setPadding(20, 0, 0, 0);
						checkBox.setText(model.optionName);
						checkBox.setTextSize(30);
						ly_quest_question.addView(checkBox);
						checkBoxs.add(checkBox);
					}
				}
			} else if (questionModels.get(quest_i).answerType.equals("TEXT")) {
				edt_quest_question.setText("");
				edt_quest_question.setVisibility(View.VISIBLE);
				rg_quest_question.setVisibility(View.INVISIBLE);
				ly_quest_question.setVisibility(View.INVISIBLE);
				edt_quest_question.setFocusable(true);
				edt_quest_question.setFocusableInTouchMode(true);
				edt_quest_question.requestFocus();
				edt_quest_question.requestFocusFromTouch();
				InputMethodManager inputManager = (InputMethodManager) edt_quest_question
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				if (inputManager != null)
					inputManager.showSoftInput(edt_quest_question, 0);
				isInputMethod = true;
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_quest_back:
			finish();
			break;
		case R.id.btn_quest_start:
			QuestionDatabase database = new QuestionDatabase();
			questionModels = database
					.query_by_questionnaireCode(questionNaireModel.questionnaireCode);
			if (questionModels != null && questionModels.size() > 0) {
				questionNaireModel.questionList = questionModels;
				updata_quest(0);
			}
			break;
		case R.id.btn_quest_next:
			// Log.d(TAG, "questionModels.size():" + questionModels.size()
			// + " quest_i:" + quest_i);
			if (quest_i < questionModels.size() - 1) {
				if (questionModels.get(quest_i).answerType
						.equals("MULTISELECT")) {
					if (checkBoxs.size() > 0) {
						AnswerModel answerModel = new AnswerModel();
						answerModel.questionCode = questionModels.get(quest_i).questionCode;
						answerModel.answer = "";
						for (int i = 0; i < checkBoxs.size(); i++) {
							if (checkBoxs.get(i).isChecked()) {
								if (TextUtils.isEmpty(answerModel.answer)) {
									answerModel.answer = questionModels
											.get(quest_i).optionList.get(i).optionName;
								} else {
									answerModel.answer = answerModel.answer
											+ ","
											+ questionModels.get(quest_i).optionList
													.get(i).optionName;
								}
							}
						}
						questionModels.get(quest_i).answerInfo = answerModel;
					}
				} else if (questionModels.get(quest_i).answerType
						.equals("TEXT")) {
					AnswerModel answerModel = new AnswerModel();
					answerModel.questionCode = questionModels.get(quest_i).questionCode;
					answerModel.answer = edt_quest_question.getText()
							.toString();
					questionModels.get(quest_i).answerInfo = answerModel;
				}
				updata_quest(++quest_i);
			} else {
				if (questionModels.get(quest_i).answerType
						.equals("MULTISELECT")) {
					if (checkBoxs.size() > 0) {
						AnswerModel answerModel = new AnswerModel();
						answerModel.questionCode = questionModels.get(quest_i).questionCode;
						answerModel.answer = "";
						for (int i = 0; i < checkBoxs.size(); i++) {
							if (checkBoxs.get(i).isChecked()) {
								if (TextUtils.isEmpty(answerModel.answer)) {
									answerModel.answer = questionModels
											.get(quest_i).optionList.get(i).optionName;
								} else {
									answerModel.answer = answerModel.answer
											+ ","
											+ questionModels.get(quest_i).optionList
													.get(i).optionName;
								}
							}
						}
						questionModels.get(quest_i).answerInfo = answerModel;
					}
				} else if (questionModels.get(quest_i).answerType
						.equals("TEXT")) {
					AnswerModel answerModel = new AnswerModel();
					answerModel.questionCode = questionModels.get(quest_i).questionCode;
					answerModel.answer = edt_quest_question.getText()
							.toString();
					questionModels.get(quest_i).answerInfo = answerModel;
				}
				application.httpProcess.CommitQuest(
						application.equInfoModel.jssysdm, questionNaireModel);
				if (progressDialog == null) {
					progressDialog = ProgressDialog.show(this, null, "认证中，请稍后",
							false, false);
				}
			}
			break;
		case R.id.edt_quest_question:
			if (isInputMethod) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null)
					imm.hideSoftInputFromWindow(
							edt_quest_question.getWindowToken(), 0);
				isInputMethod = false;
			} else {
				InputMethodManager inputManager = (InputMethodManager) edt_quest_question
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				if (inputManager != null)
					inputManager.showSoftInput(edt_quest_question, 0);
				isInputMethod = true;
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		adapter.updataSelectedView(arg2);
		updata_naire(arg2);
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		if (questionModels.get(quest_i).optionList != null
				&& questionModels.get(quest_i).optionList.size() > 0) {
			arg1 = arg1 % questionModels.get(quest_i).optionList.size();
			if (arg1 == 0) {
				arg1 = questionModels.get(quest_i).optionList.size();
			}
			Log.d(TAG,
					"RADIO "
							+ questionModels.get(quest_i).optionList
									.get(arg1 - 1).optionName);
			AnswerModel answerModel = new AnswerModel();
			answerModel.questionCode = questionModels.get(quest_i).questionCode;
			answerModel.answer = questionModels.get(quest_i).optionList
					.get(arg1 - 1).optionName;
			questionModels.get(quest_i).answerInfo = answerModel;
		}
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals(HttpProcess.COMMIT_QUEST)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					application.showToast("问卷提交成功");
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("问卷提交失败，原因：" + msg);
				}
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
					progressDialog = null;
				}
			} else if (tag.equals("permission_finish")) {
				finish();
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
