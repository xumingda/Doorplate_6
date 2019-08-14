package com.yy.doorplate.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.yy.doorplate.communication.FTPManager;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.AttendInfoDatabase;
import com.yy.doorplate.database.CurriculumInfoDatabase;
import com.yy.doorplate.model.AttendInfoModel;
import com.yy.doorplate.model.CurriculumInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;
import com.yy.doorplate.view.CustomTextView;

public class AttendInfoActivity extends Activity {

	private final String TAG = "AttendInfoActivity";

	private MyApplication application;

	private Button btn_classinfo_back;
	private ImageView img_atteninfo_photo;
	private TextView txt_atteninfo_banji, txt_atteninfo_jc, txt_atteninfo_time,
			txt_atteninfo_addr, txt_atteninfo_jryd, txt_atteninfo_yqd;
	private CustomTextView txt_atteninfo_kcmc, txt_atteninfo_jsxm;
	private HorizontalScrollView sv_attend;
	private LinearLayout ly_attend;
	private RelativeLayout ly_attend_teacher;
	private PieChart chart;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private CurriculumInfoModel curriculumInfoModel = null;
	private List<AttendInfoModel> attendInfoModels = null;

	private ImageLoader imageLoader = null;
	private Map<String, ImageView> map_img = new HashMap<String, ImageView>();
	private List<TextView> buttons = new ArrayList<TextView>();

	private ProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendinfo);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		imageLoader = new ImageLoader(application);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initData();
		initView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageLoader.cancelTask();
		imageLoader.clearCache();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
	}

	@Override
	protected void onStart() {
		super.onStart();
		application.handler_touch.removeMessages(0);
		application.handler_touch.sendEmptyMessageDelayed(0,
				application.screensaver_time * 1000);
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

	private void initData() {
		CurriculumInfoDatabase curriculumInfoDatabase = new CurriculumInfoDatabase();
		curriculumInfoModel = curriculumInfoDatabase.query_by_id(getIntent()
				.getStringExtra("skjhid"));

		AttendInfoDatabase attendInfoDatabase = new AttendInfoDatabase();
		attendInfoModels = attendInfoDatabase.query_by_skjhid(getIntent()
				.getStringExtra("skjhid"));
	}

	private void initView() {
		btn_classinfo_back = (Button) findViewById(R.id.btn_classinfo_back);
		img_atteninfo_photo = (ImageView) findViewById(R.id.img_atteninfo_photo);
		txt_atteninfo_jsxm = (CustomTextView) findViewById(R.id.txt_atteninfo_jsxm);
		txt_atteninfo_kcmc = (CustomTextView) findViewById(R.id.txt_atteninfo_kcmc);
		txt_atteninfo_banji = (TextView) findViewById(R.id.txt_atteninfo_banji);
		txt_atteninfo_jc = (TextView) findViewById(R.id.txt_atteninfo_jc);
		txt_atteninfo_time = (TextView) findViewById(R.id.txt_atteninfo_time);
		txt_atteninfo_addr = (TextView) findViewById(R.id.txt_atteninfo_addr);
		txt_atteninfo_jryd = (TextView) findViewById(R.id.txt_atteninfo_jryd);
		txt_atteninfo_yqd = (TextView) findViewById(R.id.txt_atteninfo_yqd);
		sv_attend = (HorizontalScrollView) findViewById(R.id.sv_attend);
		ly_attend = (LinearLayout) findViewById(R.id.ly_attend);
		ly_attend_teacher = (RelativeLayout) findViewById(R.id.ly_attend_teacher);
		chart = (PieChart) findViewById(R.id.chart);
		initChart();
		setPieData();

		btn_classinfo_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				finish();
			}
		});
		ly_attend_teacher.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (curriculumInfoModel != null) {
					Intent intent = new Intent(AttendInfoActivity.this,
							TeacherActivity.class);
					intent.putExtra("skjs", curriculumInfoModel.skjs);
					startActivity(intent);
				}
			}
		});

		if (curriculumInfoModel != null) {
			if (!TextUtils.isEmpty(curriculumInfoModel.skjsxm)) {
				txt_atteninfo_jsxm.setText(curriculumInfoModel.skjsxm);
			}
			if (!TextUtils.isEmpty(curriculumInfoModel.kcmc)) {
				txt_atteninfo_kcmc.setText(curriculumInfoModel.kcmc);
			}
			if (!TextUtils.isEmpty(curriculumInfoModel.skbjmc)) {
				txt_atteninfo_banji
						.setText("班级: " + curriculumInfoModel.skbjmc);
			}
			String atteninfo_jc = "节次: ";
			if (!TextUtils.isEmpty(curriculumInfoModel.jc)
					&& !TextUtils.isEmpty(curriculumInfoModel.jcshow)) {
				String str = "第 ";
				String[] js;
				js = curriculumInfoModel.jcshow.split("-");
				for (int i = 0; i < js.length; i++) {
					try {
						if (Integer.parseInt(js[i]) > 100) {
							js[i] = (Integer.parseInt(js[i]) - 100) + "";
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
						str = curriculumInfoModel.jcshow;
						break;
					}
					if (i == js.length - 1) {
						str = str + js[i] + " 节";
					} else {
						str = str + js[i] + ",";
					}
				}
				atteninfo_jc = atteninfo_jc + str;
				atteninfo_jc = atteninfo_jc + " ("
						+ MyApplication.jcToTime(curriculumInfoModel.jc) + ")";
			}
			txt_atteninfo_jc.setText(atteninfo_jc);
			if (!TextUtils.isEmpty(curriculumInfoModel.skrq)) {
				txt_atteninfo_time.setText("时间: " + curriculumInfoModel.skrq);
			}
			if (!TextUtils.isEmpty(curriculumInfoModel.skcdmc)) {
				txt_atteninfo_addr.setText("地点: " + curriculumInfoModel.skcdmc);
			}
			if (!TextUtils.isEmpty(curriculumInfoModel.skjszp)) {
				String[] s = curriculumInfoModel.skjszp.split("\\.");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PICTURE + "/teacher_"
						+ curriculumInfoModel.skjs + "." + s[s.length - 1];
				File file = new File(path);
				if (file.exists()) {
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {
								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (img_atteninfo_photo != null) {
										img_atteninfo_photo
												.setImageBitmap(bitmap);
									}
								}
							}, 200, 200);
				} else {
					downLoadImg(curriculumInfoModel.skjszp, path,
							img_atteninfo_photo, 200, 200);
				}
			}
		}

		if (attendInfoModels != null && attendInfoModels.size() > 0) {
			int count = 0;
			for (AttendInfoModel model : attendInfoModels) {
				if (model.kqzt.equals("0") || model.kqzt.equals("3")) {
					count++;
				}
				addAttendView(model.xsxm, model.kqzt, model.zp, model.id);
			}
			for (AttendInfoModel model : attendInfoModels) {
				if (!TextUtils.isEmpty(model.zp)) {
					String[] s = model.zp.split("\\.");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/attend_"
							+ model.id + "." + s[s.length - 1];
					File file = new File(path);
					if (file.exists()) {
						imageLoader.getBitmapFormUrl(path,
								new OnImageLoaderListener() {
									@Override
									public void onImageLoader(Bitmap bitmap,
											String url) {
										ImageView imageView = map_img.get(url);
										if (imageView != null && bitmap != null) {
											imageView.setImageBitmap(bitmap);
										}
									}
								}, 200, 200);
					} else {
						ImageView imageView = map_img.get(path);
						downLoadImg(model.zp, path, imageView, 200, 200);
					}
				}
			}
			txt_atteninfo_jryd.setText("" + attendInfoModels.size());
			txt_atteninfo_yqd.setText("" + count);
		} else {
			txt_atteninfo_jryd.setText("- -");
			txt_atteninfo_yqd.setText("- -");
			application.httpProcess.QryAttend(application.equInfoModel.jssysdm,
					getIntent().getStringExtra("skjhid"), null);
			if (progressDialog == null) {
				progressDialog = ProgressDialog.show(this, null, "加载中...",
						false, false);
			}
			application.isQryAttend = true;
		}
	}

	private void initChart() {
		// 显示百分比
		chart.setUsePercentValues(true);
		// 描述信息
		chart.setDescription("");
		// 设置偏移量
		chart.setExtraOffsets(20, 10, 20, 20);
		// 设置滑动减速摩擦系数
		// chart.setDragDecelerationFrictionCoef(0.95f);

		// chart.setCenterText("测试饼图，中间文字");
		// 设置是否显示中间的文字
		// chart.setDrawCenterText(true);
		// 设置饼图中心是否是空心的 true 中间是空心的，环形图 false 中间是实心的 饼图
		chart.setDrawHoleEnabled(false);
		// 设置中间空心圆孔的颜色是否透明 true 透明的 false 非透明的
		// chart.setHoleColorTransparent(true);
		// 设置环形图和中间空心圆之间的圆环的颜色
		// chart.setTransparentCircleColor(Color.WHITE);
		// 设置环形图和中间空心圆之间的圆环的透明度
		// chart.setTransparentCircleAlpha(110);
		// 设置圆孔半径
		// chart.setHoleRadius(58f);
		// 设置空心圆的半径
		// chart.setTransparentCircleRadius(61f);

		// 初始旋转角度
		chart.setRotationAngle(0);
		// 可以手动旋转
		chart.setRotationEnabled(false);

		// 设置隐藏饼图上文字，只显示百分比
		chart.setDrawSliceText(false);

		// 如果没有数据的时候，会显示这个，类似ListView的EmptyView
		chart.setNoDataText("");

		// 设置动画
		// chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

		Legend mLegend = chart.getLegend(); // 设置比例图
		mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT); // 左下边显示
		mLegend.setFormSize(20f);// 比例块大小
		mLegend.setTextSize(15f);
		mLegend.setXEntrySpace(20f);// 设置距离饼图的距离，防止与饼图重合
		mLegend.setYEntrySpace(5f);
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
			float quarterly0, quarterly1, quarterly2, quarterly3, quarterly4, quarterly5, quarterly6, quarterly7, quarterly8, quarterly9, quarterly10, quarterly11;
			for (AttendInfoModel model : attendInfoModels) {
				if (model.kqzt.equals("0")) {
					c0++;
				} else if (model.kqzt.equals("1")) {
					c1++;
				} else if (model.kqzt.equals("2")) {
					c2++;
				} else if (model.kqzt.equals("3")) {
					c3++;
				} else if (model.kqzt.equals("4")) {
					c4++;
				} else if (model.kqzt.equals("5")) {
					c5++;
				} else if (model.kqzt.equals("6")) {
					c6++;
				} else if (model.kqzt.equals("7")) {
					c7++;
				} else if (model.kqzt.equals("8")) {
					c8++;
				} else if (model.kqzt.equals("9")) {
					c9++;
				} else if (model.kqzt.equals("10")) {
					c10++;
				} else if (model.kqzt.equals("11")) {
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
				xVals.add("事假");
				yVals1.add(new Entry(20, i++));
				colors.add(getResources().getColor(R.color.attend_1));
			}
			if (quarterly2 != 0) {
				xVals.add("病假");
				yVals1.add(new Entry(quarterly2, i++));
				colors.add(getResources().getColor(R.color.attend_2));
			}
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
			// dataSet.setSliceSpace(1f);
			if (i > 1) {
				dataSet.setSelectionShift(30f); // 选中态多出的长度
				chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

					@Override
					public void onValueSelected(Entry e, int dataSetIndex,
							Highlight h) {
						String kqzt = "";
						if (xVals.get(e.getXIndex()).equals("已签到")) {
							kqzt = "0";
						} else if (xVals.get(e.getXIndex()).equals("事假")) {
							kqzt = "1";
						} else if (xVals.get(e.getXIndex()).equals("病假")) {
							kqzt = "2";
						} else if (xVals.get(e.getXIndex()).equals("迟到")) {
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
							kqzt = "9";
						} else if (xVals.get(e.getXIndex()).equals("缺席")) {
							kqzt = "10";
						} else if (xVals.get(e.getXIndex()).equals("未签到")) {
							kqzt = "11";
						}
						List<AttendInfoModel> list = new ArrayList<AttendInfoModel>();
						if (attendInfoModels != null
								&& attendInfoModels.size() > 0) {
							for (AttendInfoModel model : attendInfoModels) {
								if (model.kqzt.equals(kqzt)) {
									list.add(model);
								}
							}
						}
						updataList(list);
					}

					@Override
					public void onNothingSelected() {
						updataList(attendInfoModels);
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
			pieData.setValueTextSize(25f);
			pieData.setValueTextColor(Color.WHITE);

			chart.setData(pieData);
			chart.highlightValues(null);
			chart.invalidate();
		} else {
			chart.setData(null);
		}
	}

	private void updataList(List<AttendInfoModel> attendInfoModels) {
		sv_attend.scrollTo(0, 0);
		ly_attend.removeAllViews();
		if (attendInfoModels != null && attendInfoModels.size() > 0) {
			for (AttendInfoModel model : attendInfoModels) {
				addAttendView(model.xsxm, model.kqzt, model.zp, model.id);
			}
			for (AttendInfoModel model : attendInfoModels) {
				if (!TextUtils.isEmpty(model.zp)) {
					String[] s = model.zp.split("\\.");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/attend_"
							+ model.id + "." + s[s.length - 1];
					if (imageLoader.getBitmapFromMemCache(path) != null) {
						ImageView imageView = map_img.get(path);
						if (imageView != null) {
							imageView.setImageBitmap(imageLoader
									.getBitmapFromMemCache(path));
						}
					} else {
						imageLoader.getBitmapFormUrl(path,
								new OnImageLoaderListener() {
									@Override
									public void onImageLoader(Bitmap bitmap,
											String url) {
										ImageView imageView = map_img.get(url);
										if (imageView != null && bitmap != null) {
											imageView.setImageBitmap(bitmap);
										}
									}
								}, 200, 200);
					}
				}
			}
		}
	}

	private void addAttendView(String name, String kqzt, String zp, String id) {
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(250,
				420);
		params1.topMargin = 40;
		params1.leftMargin = 20;
		params1.rightMargin = 20;
		LinearLayout layout = new LinearLayout(this);
		layout.setGravity(Gravity.CENTER_HORIZONTAL);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundColor(getResources().getColor(R.color.ly_attend_bg));

		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(200,
				200);
		params2.topMargin = 20;
		ImageView imageView = new ImageView(this);
		imageView.setImageResource(R.drawable.user_icon);
		layout.addView(imageView, params2);

		params2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params2.topMargin = 20;
		TextView textView = new TextView(this);
		textView.setTextSize(48);
		textView.setTextColor(getResources().getColor(R.color.black));
		textView.setText(name);
		layout.addView(textView, params2);

		params2 = new LinearLayout.LayoutParams(207, 63);
		params2.topMargin = 20;
		TextView button = new TextView(this);
		button.setTextSize(44);
		button.setTextColor(getResources().getColor(R.color.white));
		button.setGravity(Gravity.CENTER);
		if (kqzt.equals("11")) {
			button.setText("未签到");
			button.setBackgroundResource(R.drawable.btn_attend_2);
		} else if (kqzt.equals("0")) {
			button.setText("已签到");
			button.setBackgroundResource(R.drawable.btn_attend_4);
		} else if (kqzt.equals("1")) {
			button.setText("事假");
			button.setBackgroundResource(R.drawable.btn_attend_2);
		} else if (kqzt.equals("2")) {
			button.setText("病假");
			button.setBackgroundResource(R.drawable.btn_attend_2);
		} else if (kqzt.equals("3")) {
			button.setText("迟到");
			button.setBackgroundResource(R.drawable.btn_attend_1);
		} else if (kqzt.equals("4")) {
			button.setText("旷课");
			button.setBackgroundResource(R.drawable.btn_attend_3);
		} else if (kqzt.equals("5")) {
			button.setText("早退");
			button.setBackgroundResource(R.drawable.btn_attend_1);
		} else if (kqzt.equals("6")) {
			button.setText("公假");
			button.setBackgroundResource(R.drawable.btn_attend_2);
		} else if (kqzt.equals("7")) {
			button.setText("实习");
			button.setBackgroundResource(R.drawable.btn_attend_2);
		} else if (kqzt.equals("8")) {
			button.setText("集训");
			button.setBackgroundResource(R.drawable.btn_attend_2);
		} else if (kqzt.equals("9")) {
			button.setText("其它");
			button.setBackgroundResource(R.drawable.btn_attend_2);
		} else if (kqzt.equals("10")) {
			button.setText("缺席");
			button.setBackgroundResource(R.drawable.btn_attend_3);
		}
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
			}
		});
		buttons.add(button);
		layout.addView(button, params2);

		if (ly_attend.getChildCount() == 0) {
			params1.leftMargin = 0;
		}
		ly_attend.addView(layout, params1);

		if (!TextUtils.isEmpty(zp)) {
			String[] s = zp.split("\\.");
			String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE
					+ "/attend_" + id + "." + s[s.length - 1];
			map_img.put(path, imageView);
		}
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals("commotAttend_ui") && attendInfoModels != null
					&& attendInfoModels.size() > 0) {
				String id = intent.getStringExtra("attend_id");
				String kqzt = intent.getStringExtra("attend_kqzt");
				AttendInfoDatabase attendInfoDatabase = new AttendInfoDatabase();
				attendInfoModels = attendInfoDatabase
						.query_by_skjhid(attendInfoModels.get(0).skjhid);
				int count = 0;
				for (int i = 0; i < attendInfoModels.size(); i++) {
					Log.d(TAG, attendInfoModels.get(i).toString());
					if (attendInfoModels.get(i).kqzt.equals("0")
							|| attendInfoModels.get(i).kqzt.equals("3")) {
						count++;
					}
					if (attendInfoModels.get(i).id.equals(id)) {
						if (kqzt.equals("0")) {
							buttons.get(i).setText("已签到");
							buttons.get(i).setBackgroundResource(
									R.drawable.btn_attend_4);
						} else if (kqzt.equals("3")) {
							buttons.get(i).setText("迟到");
							buttons.get(i).setBackgroundResource(
									R.drawable.btn_attend_1);
						}
					}
				}
				txt_atteninfo_yqd.setText("" + count);
				setPieData();
			} else if (tag.equals("absenteeism_ui") && attendInfoModels != null
					&& attendInfoModels.size() > 0) {
				AttendInfoDatabase attendInfoDatabase = new AttendInfoDatabase();
				attendInfoModels = attendInfoDatabase
						.query_by_skjhid(attendInfoModels.get(0).skjhid);
				int count = 0;
				for (int i = 0; i < attendInfoModels.size(); i++) {
					if (attendInfoModels.get(i).kqzt.equals("11")) {
						buttons.get(i).setText("旷课");
						buttons.get(i).setBackgroundResource(
								R.drawable.btn_attend_3);
					}
					if (attendInfoModels.get(i).kqzt.equals("0")
							|| attendInfoModels.get(i).kqzt.equals("3")) {
						count++;
					}
				}
				txt_atteninfo_yqd.setText("" + count);
				setPieData();
			} else if (tag.equals(HttpProcess.QRY_ATTEND)
					&& !application.isUpdateEQ && application.isQryAttend) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				application.isQryAttend = false;
				if (result) {
					Log.d(TAG, tag + " " + msg);
					AttendInfoDatabase attendInfoDatabase = new AttendInfoDatabase();
					attendInfoModels = attendInfoDatabase
							.query_by_skjhid(getIntent().getStringExtra(
									"skjhid"));
					setPieData();
					if (attendInfoModels != null && attendInfoModels.size() > 0) {
						int count = 0;
						for (AttendInfoModel model : attendInfoModels) {
							if (model.kqzt.equals("0")
									|| model.kqzt.equals("3")) {
								count++;
							}
							addAttendView(model.xsxm, model.kqzt, model.zp,
									model.id);
						}
						for (AttendInfoModel model : attendInfoModels) {
							if (!TextUtils.isEmpty(model.zp)) {
								String[] s = model.zp.split("\\.");
								String path = MyApplication.PATH_ROOT
										+ MyApplication.PATH_PICTURE
										+ "/attend_" + model.id + "."
										+ s[s.length - 1];
								File file = new File(path);
								if (file.exists()) {
									imageLoader.getBitmapFormUrl(path,
											new OnImageLoaderListener() {
												@Override
												public void onImageLoader(
														Bitmap bitmap,
														String url) {
													ImageView imageView = map_img
															.get(url);
													if (imageView != null
															&& bitmap != null) {
														imageView
																.setImageBitmap(bitmap);
													}
												}
											}, 200, 200);
								} else {
									ImageView imageView = map_img.get(path);
									downLoadImg(model.zp, path, imageView, 200,
											200);
								}
							}
						}
						txt_atteninfo_jryd
								.setText("" + attendInfoModels.size());
						txt_atteninfo_yqd.setText("" + count);
					} else {
						txt_atteninfo_jryd.setText("- -");
						txt_atteninfo_yqd.setText("- -");
					}
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("获取考勤信息失败，原因：" + msg);
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

	private Handler handler = new Handler() {
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case 0:
				List<Object> list = (List<Object>) msg.obj;
				String path = (String) list.get(0);
				final ImageView imageView = (ImageView) list.get(1);
				imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						if (imageView != null) {
							imageView.setImageBitmap(bitmap);
						}
					}
				}, msg.arg1, msg.arg2);
				break;
			}
		};
	};

	private void downLoadImg(final String urlStr, final String path,
			final ImageView imageView, final int width, final int height) {
		if (TextUtils.isEmpty(urlStr)) {
			return;
		}
		final File file = new File(path);
		if (file.exists()) {
			return;
		}
		Log.d(TAG, "start downLoad Img urlStr:" + urlStr + "\npath:" + path);
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				if (urlStr.startsWith("ftp")) {
					FTPManager ftpManager = new FTPManager();
					if (ftpManager.connect(urlStr, path)) {
						if (ftpManager.download()) {
							List<Object> list = new ArrayList<Object>();
							list.add(path);
							list.add(imageView);
							Message msg = Message.obtain();
							msg.what = 0;
							msg.obj = list;
							msg.arg1 = width;
							msg.arg2 = height;
							handler.sendMessage(msg);
						}
						ftpManager.disConnect();
					}
				} else {
					if (application.httpDownLoad(urlStr, path)) {
						List<Object> list = new ArrayList<Object>();
						list.add(path);
						list.add(imageView);
						Message msg = Message.obtain();
						msg.what = 0;
						msg.obj = list;
						msg.arg1 = width;
						msg.arg2 = height;
						handler.sendMessage(msg);
					}
				}
			}
		});
	}
}
