package com.yy.doorplate.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.activity.QuestActivity;
import com.yy.doorplate.database.QuestionNaireDatabase;
import com.yy.doorplate.model.QuestionNaireModel;
import com.yy.doorplate.view.CustomTextView;

public class QuestView extends AppCodeView {

	private Button btn_schoolnew_quest_start;
	private TextView txt_schoolnew_quest_rl, txt_schoolnew_noquest;
	private CustomTextView txt_schoolnew_quest_title;

	public QuestView(Context context, MyApplication application, String nextPath) {
		super(context, application, nextPath);
		initView();
		updata_quest();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		View.inflate(context, R.layout.ly_quest, this);
		btn_schoolnew_quest_start = (Button) findViewById(R.id.btn_schoolnew_quest_start);
		txt_schoolnew_quest_rl = (TextView) findViewById(R.id.txt_schoolnew_quest_rl);
		txt_schoolnew_noquest = (TextView) findViewById(R.id.txt_schoolnew_noquest);
		txt_schoolnew_quest_title = (CustomTextView) findViewById(R.id.txt_schoolnew_quest_title);

		btn_schoolnew_quest_start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, QuestActivity.class);
				context.startActivity(intent);
			}
		});
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

	@Override
	public void update(int type) {
		// TODO Auto-generated method stub
		if (type == APPCODE_UPDATA_QUEST || type == APPCODE_UPDATA_EQU) {
			updata_quest();
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
