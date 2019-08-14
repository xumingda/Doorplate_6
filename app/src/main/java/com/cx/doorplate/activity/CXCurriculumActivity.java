package com.cx.doorplate.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.communication.HttpUtils;
import com.yy.doorplate.database.CurriculumInfoDatabase;
import com.yy.doorplate.database.SectionInfoDatabase;
import com.yy.doorplate.database.StudentInfoDatabase;
import com.yy.doorplate.model.CurriculumInfoModel;
import com.yy.doorplate.model.SectionInfoModel;
import com.yy.doorplate.model.StudentInfoModel;
import com.yy.doorplate.view.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

public class CXCurriculumActivity extends Activity implements
        OnItemClickListener {

    private final String TAG = "CXCurriculumActivity";

    private MyApplication application;

    private LinearLayout ly_curriculum_week, ly_curriculum_ampm;
    private Button btn_cx_back;
    private TextView txt_weather;
    private TextClock textClock;
    private GridView gv_curriculum;

    private LocalBroadcastManager broadcastManager;
    private MyBroadcastReceiver myBroadcastReceiver;

    private ProgressDialog progressDialog = null;

    private SimpleDateFormat format;
    private Calendar calendar = Calendar.getInstance();

    private Date date_1, date_2, date_3, date_4, date_5, date_6, date_7,
            date_click;
    private int week = 5, curriculum_num =9, curriculum_num_am = 5,
            curriculum_num_pm = 4;

    private List<SectionInfoModel> sectionModels;
    private List<CurriculumInfoModel> curriculumInfoModels = new ArrayList<CurriculumInfoModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cx_activity_curriculum);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        application = (MyApplication) getApplicationContext();

        broadcastManager = LocalBroadcastManager.getInstance(this);
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
        broadcastManager.registerReceiver(myBroadcastReceiver, filter);

        initView();

        // if (progressDialog == null) {
        // progressDialog = ProgressDialog.show(this, null, "加载中...", false,
        // false);
        // }
        // format = new SimpleDateFormat("yyyy-MM-dd");
        // application.httpProcess.QryCurriculum_week(
        // application.equInfoModel.jssysdm, format.format(date_1)
        // + " 00:00:00", format.format(date_7) + " 23:59:59");
    }

    private void initView() {
        ly_curriculum_week = (LinearLayout) findViewById(R.id.ly_curriculum_week);
        ly_curriculum_ampm = (LinearLayout) findViewById(R.id.ly_curriculum_ampm);
        btn_cx_back = (Button) findViewById(R.id.btn_cx_back);
        txt_weather = (TextView) findViewById(R.id.txt_weather);
        gv_curriculum = (GridView) findViewById(R.id.gv_curriculum);

        textClock = (TextClock) findViewById(R.id.textClock);
        textClock.setFormat12Hour(null);
        textClock.setFormat24Hour("yyyy年MM月dd日\nEEEE      HH : mm");
        txt_weather.setText(application.currentCity.trim() + "   "
                + application.temperature.trim() + "\n"
                + application.weather.trim());
        btn_cx_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        gv_curriculum.setOnItemClickListener(this);

        application.executoService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    initData(System.currentTimeMillis());
                    handler.sendEmptyMessage(0);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            updataView();


        };
    };

    private void initData(long currentTimeMillis) throws ParseException {
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date_click = new Date(currentTimeMillis);
        calendar.setTime(date_click);
        if (calendar.get(Calendar.DAY_OF_WEEK) == 2) {
            date_1 = date_click;
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date_2 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            date_3 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 3);
            date_4 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 4);
            date_5 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 5);
            date_6 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 6);
            date_7 = calendar.getTime();
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == 3) {
            date_2 = date_click;
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            date_1 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date_3 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            date_4 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 3);
            date_5 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 4);
            date_6 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 5);
            date_7 = calendar.getTime();
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == 4) {
            date_3 = date_click;
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -2);
            date_1 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            date_2 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date_4 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            date_5 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 3);
            date_6 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 4);
            date_7 = calendar.getTime();
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == 5) {
            date_4 = date_click;
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -3);
            date_1 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -2);
            date_2 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            date_3 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date_5 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            date_6 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 3);
            date_7 = calendar.getTime();
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == 6) {
            date_5 = date_click;
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -4);
            date_1 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -3);
            date_2 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -2);
            date_3 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            date_4 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date_6 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            date_7 = calendar.getTime();
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == 7) {
            date_6 = date_click;
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -5);
            date_1 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -4);
            date_2 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -3);
            date_3 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -2);
            date_4 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            date_5 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date_7 = calendar.getTime();
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
            date_7 = date_click;
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -6);
            date_1 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -5);
            date_2 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -4);
            date_3 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -3);
            date_4 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -2);
            date_5 = calendar.getTime();
            calendar.setTime(date_click);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            date_6 = calendar.getTime();
        }
        format = new SimpleDateFormat("yyyy-MM-dd");
        List<CurriculumInfoModel> list1, list2, list3, list4, list5, list6, list7, curriculums;
        curriculums = new ArrayList<CurriculumInfoModel>();
        CurriculumInfoDatabase curriculumInfoDatabase = new CurriculumInfoDatabase();
        list1 = curriculumInfoDatabase.query_by_skrq(format.format(date_1));
        list2 = curriculumInfoDatabase.query_by_skrq(format.format(date_2));
        list3 = curriculumInfoDatabase.query_by_skrq(format.format(date_3));
        list4 = curriculumInfoDatabase.query_by_skrq(format.format(date_4));
        list5 = curriculumInfoDatabase.query_by_skrq(format.format(date_5));
        list6 = curriculumInfoDatabase.query_by_skrq(format.format(date_6));
        list7 = curriculumInfoDatabase.query_by_skrq(format.format(date_7));
        Log.e("时间","时间："+format.format(date_1)+"  list1:"+list1.size());

        if (list1 != null)
            curriculums.addAll(list1);
        if (list2 != null)
            curriculums.addAll(list2);
        if (list3 != null)
            curriculums.addAll(list3);
        if (list4 != null)
            curriculums.addAll(list4);
        if (list5 != null)
            curriculums.addAll(list5);
        if (list6 != null)
            curriculums.addAll(list6);
        if (list7 != null)
            curriculums.addAll(list7);
        week = 5;
        if (list7 != null && list7.size() > 0) {
            week += 2;
        } else if (list6 != null && list6.size() > 0) {
            week++;
        }

        curriculum_num_am = 0;
        curriculum_num_pm = 0;
        SectionInfoDatabase sectionInfoDatabase = new SectionInfoDatabase();
        sectionModels = null;
        List<SectionInfoModel> sectionInfoModels = sectionInfoDatabase
                .query_all();
        if (sectionInfoModels != null && sectionInfoModels.size() > 0) {
            sectionModels = new ArrayList<SectionInfoModel>();
            long noon = 14400000, night = 36000000;
            for (SectionInfoModel model : sectionInfoModels) {
                if (Long.parseLong(model.jcskkssj) <= noon) {
                    sectionModels.add(model);
                    curriculum_num_am++;

                } else if (noon < Long.parseLong(model.jcskkssj)
                        ) {
                    sectionModels.add(model);
                    curriculum_num_pm++;

                }
            }

        }
        curriculum_num = curriculum_num_am + curriculum_num_pm;

        curriculumInfoModels.clear();
        for (int i = 0; i < curriculum_num; i++) {
            for (int j = 1; j <= week; j++) {
                int week_j = j + 1;
                if (j == 7) {
                    week_j = 1;
                }
                CurriculumInfoModel curriculumInfoModel = new CurriculumInfoModel();
                if (sectionModels != null && sectionModels.size() > i
                        && curriculums != null && curriculums.size() > 0) {
                    for (CurriculumInfoModel model : curriculums) {
                        calendar.setTime(format.parse(model.skrq));
                        String[] jcs = model.jc.split("-");
                        for (String jc : jcs) {
                            if (sectionModels.get(i).jcdm.equals(jc)
                                    && calendar.get(Calendar.DAY_OF_WEEK) == week_j) {
                                curriculumInfoModel = model;
                                break;
                            }
                        }
                        if (!TextUtils.isEmpty(curriculumInfoModel.id)) {
                            break;
                        }
                    }
                }
                if (TextUtils.isEmpty(curriculumInfoModel.skrq)) {
                    calendar.setTime(date_click);
                    if (calendar.get(Calendar.DAY_OF_WEEK) == week_j) {
                        curriculumInfoModel.skrq = MyApplication
                                .getTime("yyyy-MM-dd");
                    }
                }
                curriculumInfoModels.add(curriculumInfoModel);
            }
        }
    }

    private void updataView() {
        ly_curriculum_week.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                1658 / week, 90);
        TextView textView1 = new TextView(this);
        textView1.setText("星期一");
        textView1.setTextSize(36);
        textView1.setTextColor(getResources().getColor(
                R.color.cx_txt_curriculum_1));
        textView1.setBackgroundResource(R.drawable.cx_ly_curriculum_1);
        textView1.setGravity(Gravity.CENTER);
        ly_curriculum_week.addView(textView1, params);
        TextView textView2 = new TextView(this);
        textView2.setText("星期二");
        textView2.setTextSize(36);
        textView2.setTextColor(getResources().getColor(
                R.color.cx_txt_curriculum_2));
        textView2.setBackgroundResource(R.drawable.cx_ly_curriculum_2);
        textView2.setGravity(Gravity.CENTER);
        ly_curriculum_week.addView(textView2, params);
        TextView textView3 = new TextView(this);
        textView3.setText("星期三");
        textView3.setTextSize(36);
        textView3.setTextColor(getResources().getColor(
                R.color.cx_txt_curriculum_3));
        textView3.setBackgroundResource(R.drawable.cx_ly_curriculum_3);
        textView3.setGravity(Gravity.CENTER);
        ly_curriculum_week.addView(textView3, params);
        TextView textView4 = new TextView(this);
        textView4.setText("星期四");
        textView4.setTextSize(36);
        textView4.setTextColor(getResources().getColor(
                R.color.cx_txt_curriculum_4));
        textView4.setBackgroundResource(R.drawable.cx_ly_curriculum_4);
        textView4.setGravity(Gravity.CENTER);
        ly_curriculum_week.addView(textView4, params);
        TextView textView5 = new TextView(this);
        textView5.setText("星期五");
        textView5.setTextSize(36);
        textView5.setTextColor(getResources().getColor(
                R.color.cx_txt_curriculum_5));
        textView5.setBackgroundResource(R.drawable.cx_ly_curriculum_5);
        textView5.setGravity(Gravity.CENTER);
        ly_curriculum_week.addView(textView5, params);
        if (week >= 6) {
            TextView textView6 = new TextView(this);
            textView6.setText("星期六");
            textView6.setTextSize(36);
            textView6.setTextColor(getResources().getColor(
                    R.color.cx_txt_curriculum_1));
            textView6.setBackgroundResource(R.drawable.cx_ly_curriculum_1);
            textView6.setGravity(Gravity.CENTER);
            ly_curriculum_week.addView(textView6, params);
        }
        if (week >= 7) {
            TextView textView7 = new TextView(this);
            textView7.setText("星期日");
            textView7.setTextSize(36);
            textView7.setTextColor(getResources().getColor(
                    R.color.cx_txt_curriculum_2));
            textView7.setBackgroundResource(R.drawable.cx_ly_curriculum_2);
            textView7.setGravity(Gravity.CENTER);
            ly_curriculum_week.addView(textView7, params);
        }

        ly_curriculum_ampm.removeAllViews();
        if (curriculum_num_am > 0) {
            params = new LinearLayout.LayoutParams(142, 660 / curriculum_num
                    * curriculum_num_am);
            TextView textViewam = new TextView(this);
            textViewam.setText("上午");
            textViewam.setTextSize(36);
            textViewam.setTextColor(getResources().getColor(R.color.black));
            textViewam.setBackgroundResource(R.drawable.cx_ly_curriculum_ampm);
            textViewam.setGravity(Gravity.CENTER);
            ly_curriculum_ampm.addView(textViewam, params);
        }
        if (curriculum_num_pm > 0) {
            params = new LinearLayout.LayoutParams(142, 660 / curriculum_num
                    * curriculum_num_pm);
            TextView textViewpm = new TextView(this);
            textViewpm.setText("下午");
            textViewpm.setTextSize(36);
            textViewpm.setTextColor(getResources().getColor(R.color.black));
            textViewpm.setBackgroundResource(R.drawable.cx_ly_curriculum_ampm);
            textViewpm.setGravity(Gravity.CENTER);
            ly_curriculum_ampm.addView(textViewpm, params);
        }

        gv_curriculum.setNumColumns(week);
        MyAdapter adapter = new MyAdapter();
        gv_curriculum.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (!TextUtils.isEmpty(curriculumInfoModels.get(arg2).id)) {
            Intent intent = new Intent(CXCurriculumActivity.this, CXAttendActivity.class);

            intent.putExtra("skjhid", curriculumInfoModels.get(arg2).id);
            intent.putExtra("kcmc", curriculumInfoModels.get(arg2).kcmc);
            startActivity(intent);
//			QryStudentbykc(curriculumInfoModels.get(arg2).id,curriculumInfoModels.get(arg2).skcdmc,"2");
        }
    }

    private class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return curriculumInfoModels.size();
        }

        @Override
        public Object getItem(int position) {
            return curriculumInfoModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            private RelativeLayout ly_item_curriculm;
            private ImageView img_item_curriculm;
            private CustomTextView txt_item_curriculm;
        }

        @SuppressLint("LongLogTag")
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Log.e("curriculumInfoModels.size()","curriculumInfoModels.size()"+curriculumInfoModels.size());
            CurriculumInfoModel model = curriculumInfoModels.get(position);
            // Log.d(TAG, model.toString());
            ViewHolder viewHolder = null;
            if (viewHolder == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(application).inflate(
                        R.layout.cx_item_curriculm, null);
                viewHolder.img_item_curriculm = (ImageView) view
                        .findViewById(R.id.img_item_curriculm);
                viewHolder.txt_item_curriculm = (CustomTextView) view
                        .findViewById(R.id.txt_item_curriculm);
                viewHolder.ly_item_curriculm = (RelativeLayout) view
                        .findViewById(R.id.ly_item_curriculm);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            view.setLayoutParams(new GridView.LayoutParams(1658 / week,
                    660 / curriculum_num));
            if (MyApplication.getTime("yyyy-MM-dd").equals(model.skrq)) {
                viewHolder.ly_item_curriculm
                        .setBackgroundResource(R.drawable.cx_ly_curriculum_red);
            } else {
                viewHolder.ly_item_curriculm
                        .setBackgroundResource(R.drawable.cx_ly_curriculum_white);
            }
            if (!TextUtils.isEmpty(model.id)) {
                viewHolder.txt_item_curriculm.setText(model.kcmc);
                // AttendInfoDatabase attendInfoDatabase = new
                // AttendInfoDatabase();
                // if (attendInfoDatabase.query_by_skjhid(model.id) != null) {
                viewHolder.img_item_curriculm.setVisibility(View.VISIBLE);
                // } else {
                // viewHolder.img_item_curriculm.setVisibility(View.INVISIBLE);
                // }
                if (application.curriculumInfoModel_now != null
                        && application.curriculumInfoModel_now.id
                        .equals(model.id)) {
                    viewHolder.txt_item_curriculm.setTextColor(getResources()
                            .getColor(R.color.cx_txt_curriculum_red));
                } else {
                    try {
                        int i = 0;
                        if ((position + 1) % week == 0) {
                            i = (position + 1) / week - 1;
                        } else {
                            i = (position + 1) / week;
                        }
                        format = new SimpleDateFormat("HH:mm:ss");
                        String timeStr = format.format(new Date(Long
                                .parseLong(sectionModels.get(i).jcskkssj)));
                        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        long time = format.parse(model.skrq + " " + timeStr)
                                .getTime();
                        if (time < System.currentTimeMillis()) {
                            viewHolder.txt_item_curriculm
                                    .setTextColor(getResources().getColor(
                                            R.color.cx_txt_curriculum_gray));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return view;
        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(MyApplication.BROADCAST)) {
                return;
            }
            String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
            if (tag.equals(HttpProcess.QRY_CURRICULUM + "week")
                    && !application.isGrxx) {
                boolean result = intent.getBooleanExtra(
                        MyApplication.CMD_RESULT, false);
                String msg = intent.getStringExtra(MyApplication.CMD_MSG);
                Log.d(TAG, tag + " " + msg);
                if (result) {
                } else {
                    application.showToast(msg);
                }
                try {
                    initData(System.currentTimeMillis());
                    updataView();
                } catch (ParseException e) {
                    e.printStackTrace();
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
