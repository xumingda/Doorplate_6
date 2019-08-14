package com.yy.doorplate.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.adapter.HonorAdapter;
import com.yy.doorplate.database.PrizeInfoDatabase;
import com.yy.doorplate.model.PrizeInfoModel;
import com.yy.doorplate.tool.ImageLoader;

public class HonorActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private final String TAG = "HonorActivity";

	private MyApplication application;

	private RelativeLayout ly_honor;
	private Button btn_honor_back, btn_honor_left, btn_honor_right;
	private TextView txt_honor_total, txt_honor_1, txt_honor_2, txt_honor_3;
	private ImageView img_honor;
	private GridView grid_honor;

	private ImageLoader imageLoader;

	private List<PrizeInfoModel> prizeInfoModels = null;
	private PrizeInfoModel prizeInfoModel;
	private int prize;

	private String prizeType;

	private HonorAdapter honorAdapter;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_honor);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		imageLoader = new ImageLoader(application);

		prizeType = getIntent().getStringExtra("prizeType");
		prize = getIntent().getIntExtra("prize", 0);

		initView();
	}

	private void initView() {
		ly_honor = (RelativeLayout) findViewById(R.id.ly_honor);
		btn_honor_back = (Button) findViewById(R.id.btn_honor_back);
		btn_honor_left = (Button) findViewById(R.id.btn_honor_left);
		btn_honor_right = (Button) findViewById(R.id.btn_honor_right);
		txt_honor_1 = (TextView) findViewById(R.id.txt_honor_1);
		txt_honor_2 = (TextView) findViewById(R.id.txt_honor_2);
		txt_honor_3 = (TextView) findViewById(R.id.txt_honor_3);
		txt_honor_total = (TextView) findViewById(R.id.txt_honor_total);
		img_honor = (ImageView) findViewById(R.id.img_honor);
		grid_honor = (GridView) findViewById(R.id.grid_honor);

		btn_honor_back.setOnClickListener(this);
		btn_honor_left.setOnClickListener(this);
		btn_honor_right.setOnClickListener(this);
		grid_honor.setOnItemClickListener(this);

		PrizeInfoDatabase database = new PrizeInfoDatabase();
		prizeInfoModels = database.query_by_prizeType(prizeType);
		prizeInfoModel = database.query_by_code(getIntent().getStringExtra(
				"prizeCode"));
		if (prizeInfoModels != null && prizeInfoModels.size() > 0) {
			txt_honor_total.setText("截至目前，我们共获得了 " + prizeInfoModels.size()
					+ " 项荣誉");
			if (prizeInfoModels.size() > 6) {
				btn_honor_left.setVisibility(View.VISIBLE);
				btn_honor_right.setVisibility(View.VISIBLE);
			} else {
				btn_honor_left.setVisibility(View.INVISIBLE);
				btn_honor_right.setVisibility(View.INVISIBLE);
			}
			updata_honor();
			updata_grid();
		} else {
			txt_honor_total.setText("暂无荣誉");
			ly_honor.setVisibility(View.INVISIBLE);
			btn_honor_left.setVisibility(View.INVISIBLE);
			btn_honor_right.setVisibility(View.INVISIBLE);
			grid_honor.setAdapter(null);
		}
	}

	private void updata_honor() {
		if (prizeInfoModel != null) {
			ly_honor.setVisibility(View.VISIBLE);
			txt_honor_1.setText(prizeInfoModel.ranking);
			txt_honor_2.setText(prizeInfoModel.prizeName);
			txt_honor_3.setText("获奖日期 : " + prizeInfoModel.prizeDate
					+ "\n\n获奖人员 : " + prizeInfoModel.prizePerson
					+ "\n\n获奖单位 : " + prizeInfoModel.prizeUnit);
			if (!TextUtils.isEmpty(prizeInfoModel.iconUrl)) {
				String[] s = prizeInfoModel.iconUrl.split("/");
				final String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PICTURE + "/prizeIcon_"
						+ s[s.length - 1];
				File file = new File(path);
				if (file.exists()) {
					application.executoService.execute(new Runnable() {
						@Override
						public void run() {
							Bitmap bitmap = imageLoader
									.decodeSampledBitmapFromPath(path, 410, 546);
							Message msg = Message.obtain();
							msg.what = 0;
							msg.obj = bitmap;
							handler.sendMessage(msg);
						}
					});
				} else {
					img_honor.setImageBitmap(null);
				}
			} else {
				img_honor.setImageBitmap(null);
			}
		} else {
			img_honor.setImageBitmap(null);
			ly_honor.setVisibility(View.INVISIBLE);
		}
	}

	private void updata_grid() {
		List<PrizeInfoModel> list = new ArrayList<PrizeInfoModel>();
		if (prizeInfoModels != null && prizeInfoModels.size() > 0) {
			int page = prize / 6;
			for (int i = page * 6; i < page * 6 + 6; i++) {
				if (i < prizeInfoModels.size()) {
					list.add(prizeInfoModels.get(i));
				}
			}
		}
		if (list.size() > 0 && list.size() <= 6) {
			honorAdapter = new HonorAdapter(application, list, grid_honor,
					imageLoader);
			honorAdapter.setSelect(prize % 6);
			grid_honor.setAdapter(honorAdapter);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int page = prize / 6;
		prize = arg2 + page * 6;
		prizeInfoModel = prizeInfoModels.get(prize);
		updata_honor();
		if (honorAdapter != null) {
			honorAdapter.updataSelectedView(prize % 6);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_honor_back:
			finish();
			break;
		case R.id.btn_honor_left: {
			int page = prize / 6;
			if (page > 0) {
				page--;
				prize = page * 6;
				prizeInfoModel = prizeInfoModels.get(prize);
				updata_honor();
				updata_grid();
			}
			break;
		}
		case R.id.btn_honor_right: {
			int page = prize / 6;
			if (prizeInfoModels != null && prizeInfoModels.size() > 0
					&& page < prizeInfoModels.size() / 6) {
				page++;
				prize = page * 6;
				prizeInfoModel = prizeInfoModels.get(prize);
				updata_honor();
				updata_grid();
			}
			break;
		}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				img_honor.setImageBitmap((Bitmap) msg.obj);
				break;
			}
		};
	};

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
		imageLoader.cancelTask();
		imageLoader.clearCache();
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

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals("permission_finish")) {
				finish();
			}
		}
	}
}
