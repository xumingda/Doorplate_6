package com.cx.doorplate.activity;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.advertisement.activity.DisplayManager;
import com.advertisement.system.ConstData;
import com.alibaba.fastjson.JSONArray;
import com.android.xhapimanager.XHApiManager;
import com.common.AppLog;
import com.cx.doorplate.fragment.BookFragment;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.activity.ExamActivity;
import com.yy.doorplate.activity.SettingActivity;
import com.yy.doorplate.activity.VideoCallActivity;
import com.yy.doorplate.activity.WebviewActivity;
import com.yy.doorplate.activity.ZhiriActivity;
import com.yy.doorplate.adapter.MyViewPagerAdapter;
import com.yy.doorplate.communication.FTPManager;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.AttendInfoDatabase;
import com.yy.doorplate.database.BlackboardInfoDatabase;
import com.yy.doorplate.database.BooksInfoDatabase;
import com.yy.doorplate.database.CarouselDatabase;
import com.yy.doorplate.database.CurriculumInfoDatabase;
import com.yy.doorplate.database.EquModelTaskDatabase;
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.database.PageLayoutDatabase;
import com.yy.doorplate.database.PersonnelAttendanceDatabase;
import com.yy.doorplate.database.PicDreOrActivityDatabase;
import com.yy.doorplate.database.PlayTaskDatabase;
import com.yy.doorplate.database.ScreensaverDatabase;
import com.yy.doorplate.database.SectionInfoDatabase;
import com.yy.doorplate.database.StudentInfoDatabase;
import com.yy.doorplate.database.TeacherInfoDatabase;
import com.yy.doorplate.database.UserInfoDatabase;
import com.yy.doorplate.model.AttendInfoModel;
import com.yy.doorplate.model.BlackboardInfoModel;
import com.yy.doorplate.model.BooksInfoModel;
import com.yy.doorplate.model.CarouselModel;
import com.yy.doorplate.model.ClockRuleModel;
import com.yy.doorplate.model.CurriculumInfoModel;
import com.yy.doorplate.model.EquModelTaskModel;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.model.PageLayoutModel;
import com.yy.doorplate.model.PersonnelAttendanceModel;
import com.yy.doorplate.model.PicDreOrActivityModel;
import com.yy.doorplate.model.PlayTaskModel;
import com.yy.doorplate.model.ScreensaverModel;
import com.yy.doorplate.model.SectionInfoModel;
import com.yy.doorplate.model.StudentInfoModel;
import com.yy.doorplate.model.TeacherInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;
import com.yy.doorplate.ui.AppCodeView;
import com.yy.doorplate.view.CustomTextView;
import com.yy.doorplate.view.CustomViewpager;

public class CXMainActivity extends FragmentActivity implements OnClickListener {

    private final String TAG = "CXMainActivity";
    //有课的id
    private String kcid = null;
    private boolean isGo = false;
    private MyApplication application;

    private TextClock textClock;
    private RelativeLayout ly_activity_main, ly_main_new1, ly_main_new2, ly_banner, ly_cxmain_schedule, ly_xxtz_title,
            ly_emergency_notice, ly_cxmain_book, ly_cxmain_teacher;
    private LinearLayout ly_cxmain_schedule1, ly_cxmain_schedule2, ly_cxmain_schedule3, ly_main_currentTeacher, ly_main_classAdviser;
    private Button btn_main_schedule, btn_main_new_class, btn_main_read, btn_main_msg, btn_main_school, btn_main_more,
            btn_book_left, btn_book_right, btn_main_teacher_more, btn_jingbao, btn_zhiri;
    private ImageSwitcher img_schoolnew_banner;

    private TextView txt_weather, txt_bjtz_more, txt_main_nonew1, txt_main_new_title1, txt_main_new_nr1,
            txt_main_new_time1, txt_xxtz_more, txt_main_nonew2, txt_main_new_title2, txt_main_new_nr2,
            txt_main_new_time2, txt_cxmain_attend, txt_main_kc, txt_main_time, txt_main_bjxz, cx_txt_schedule_next1,
            cx_txt_schedule_next2, txt_main_new_title3, txt_main_new_nr3, txt_main_new_time3,
            ly_main_currentTeacherTitle, txt_main_teacher1,
            txt_main_teacher2, txt_main_teacher3, txt_main_teacher4, txt_main_teacher5, txt_main_teacher6,
            txt_cxmain_classmotto, txt_main_msg, txt_main_nextschedule,
            txt_main_classAdviser1, txt_main_classAdviser2, txt_main_classAdviser3,
            txt_main_classAdviser4, txt_main_classAdviser5, txt_main_classAdviser6,
            am_schedule1, am_schedule2, am_schedule3, am_schedule4, am_schedule5,
            pm_schedule1, pm_schedule2, pm_schedule3, pm_schedule4, pm_schedule5, ly_xxtz_title_txt,
            btn_main_new_class_bjrs;
    private ImageView img_main_teacher, img_mian_classAdviser, img_logo;
    private CustomTextView txt_main_class, txt_main_teacher;
    private ImageSwitcher img_main_banner;
    private CustomViewpager vp_book;

    private int curriculumInfos_today_id = 0;

    private boolean isThread = true;

    private LocalBroadcastManager broadcastManager;
    private MyBroadcastReceiver myBroadcastReceiver;

    private NoticeInfoModel notice_bjtz = null, notice_xxtz = null, notice_jjtz = null, notice_yyjz = null;
    private ProgressDialog progressDialog = null;

    private ImageLoader imageLoader = null;

    private DisplayManager displayManager = null;

    private List<PageLayoutModel> pageLayoutModels = null;

    private int fragment_total, fragment_i;

    private List<String> pics;
    private int pics_i = 0;
    private List<Map<String, String>> new_pics = null;
    private int new_pics_i = 0;
    private DisplayManager displayManager_media = null;
    private RelativeLayout rl_bjfc, ly_cxmain_attend, rl_bxxx, rl_more;
    private LinearLayout ll_xywh, ll_xytz;
    private List<PersonnelAttendanceModel> personnelAttendanceModelList;
    private boolean goAttend = false;
    private int item = 0;
    private List<NoticeInfoModel> xxtzList = new ArrayList<>();

    public void initPersonAttendance() {
        goAttend = false;
        if (application.curriculumInfoModel_now != null) {
            application.httpProcess.QryPersonnelAttendanceList(application.curriculumInfoModel_now.id);
            application.isQryAttend = true;
        } else if (kcid != null) {
            application.httpProcess.QryPersonnelAttendanceList(kcid);
            application.isQryAttend = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        application = (MyApplication) getApplicationContext();
        if ("02".equals(application.equInfoModel.equMode)) {
            setContentView(R.layout.cx_activity_main_zj);
        } else {
//			setContentView(R.layout.cx_activity_main);
//            setContentView(R.layout.lh_cx_activity_main);
            setContentView(R.layout.lh_cx_activity_main_new);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //lph20190328
//        application.initServer();
        application.readEvens();
        imageLoader = new ImageLoader(application);

        broadcastManager = LocalBroadcastManager.getInstance(this);
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyApplication.BROADCAST);
        filter.addAction(ConstData.ACTIVITY_UPDATE);
        broadcastManager.registerReceiver(myBroadcastReceiver, filter);

        ly_activity_main = (RelativeLayout) findViewById(R.id.ly_activity_main);
        PageLayoutDatabase database = new PageLayoutDatabase();
        PageLayoutModel model = database.query_by_pageCode("INDEX");
        if (model == null) {
            initView();
        } else {
            ly_activity_main.removeAllViews();
            displayManager = new DisplayManager(this, application, ly_activity_main);
            displayManager.setImageLoader(imageLoader);
            displayManager.setActivity("com.cx.doorplate.activity.CXMainActivity");
            displayManager.setActivity(this);
            displayManager.setFragmentActivity(this);
            displayManager.start(application.getFtpPath(model.resPath));
        }
        threadTime.start();
        //请求开关机时间
//		application.httpProcess.QryDataTime(null, application.equInfoModel.orgId);
        if (application.isSqlError) {
            application.isUpdateEQ = true;
            application.httpProcess.QryDataTime(null, application.equInfoModel.orgId);
            application.showDialog("正在更新设备信息...", 60);
            application.handler_touch.removeMessages(0);
        }

//        application.refreshFlag = true;
        application.refreshTimer = new Timer();
        application.refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (application.refreshFlag) {
                    if (!application.isUpdateCurriculum && !application.isUpdateEQ) {
                        application.isUpdateCurriculum = true;
                        application.httpProcess.QryCurriculum_day(application.equInfoModel.jssysdm,
                                MyApplication.getTime("yyyy-MM-dd") + " 00:00:00",
                                MyApplication.getTime("yyyy-MM-dd") + " 23:59:59", "", null);
                    }

                    if (!application.isUpdateNotice && !application.isUpdateEQ) {
                        application.isUpdateNotice = true;
                        application.httpProcess.QryNoticePJ(application.equInfoModel.jssysdm, "0", MyApplication.NOTICE_PAGE_COUNT,
                                application.equInfoModel.bjdm, true, null);
                    }
                }
            }

        }, 1000, 10 * 1000);
//		if(application.apimanager==null){
//			application.apimanager = new XHApiManager();
//			application.apimanager.XHSetPowerOffOnTime("2019-05-10-13-15","2019-05-10-13-19",true);
//		}else{
//			application.apimanager.XHSetPowerOffOnTime("2019-05-10-13-15","2019-05-10-13-19",true);
//		}

    }

    private void initView() {
        img_schoolnew_banner = (ImageSwitcher) findViewById(R.id.img_schoolnew_banner);
        btn_zhiri = (Button) findViewById(R.id.btn_zhiri);
        ll_xytz = (LinearLayout) findViewById(R.id.ll_xytz);
        ll_xywh = (LinearLayout) findViewById(R.id.ll_xywh);
        rl_bxxx = (RelativeLayout) findViewById(R.id.rl_bxxx);
        rl_more = (RelativeLayout) findViewById(R.id.rl_more);
        ly_cxmain_attend = (RelativeLayout) findViewById(R.id.ly_cxmain_attend);
        rl_bjfc = (RelativeLayout) findViewById(R.id.rl_bjfc);
        ly_main_new1 = (RelativeLayout) findViewById(R.id.ly_main_new1);
        ly_main_new2 = (RelativeLayout) findViewById(R.id.ly_main_new2);
        ly_banner = (RelativeLayout) findViewById(R.id.ly_banner);
        ly_cxmain_schedule = (RelativeLayout) findViewById(R.id.ly_cxmain_schedule);
        ly_xxtz_title = (RelativeLayout) findViewById(R.id.ly_xxtz_title);
        ly_emergency_notice = (RelativeLayout) findViewById(R.id.ly_emergency_notice);
        ly_cxmain_book = (RelativeLayout) findViewById(R.id.ly_cxmain_book);
        ly_cxmain_teacher = (RelativeLayout) findViewById(R.id.ly_cxmain_teacher);
        ly_main_currentTeacher = (LinearLayout) findViewById(R.id.ly_main_currentTeacher);
        ly_cxmain_schedule1 = (LinearLayout) findViewById(R.id.ly_cxmain_schedule1);
        ly_cxmain_schedule2 = (LinearLayout) findViewById(R.id.ly_cxmain_schedule2);
        ly_cxmain_schedule3 = (LinearLayout) findViewById(R.id.ly_cxmain_schedule3);
        btn_main_schedule = (Button) findViewById(R.id.btn_main_schedule);
        btn_jingbao = (Button) findViewById(R.id.btn_jingbao);
        btn_main_new_class = (Button) findViewById(R.id.btn_main_new_class);
        btn_main_new_class_bjrs = (TextView) findViewById(R.id.btn_main_new_class_bjrs);
        btn_main_read = (Button) findViewById(R.id.btn_main_read);
        btn_main_msg = (Button) findViewById(R.id.btn_main_msg);
        btn_main_school = (Button) findViewById(R.id.btn_main_school);
        btn_main_more = (Button) findViewById(R.id.btn_main_more);
        btn_book_left = (Button) findViewById(R.id.btn_book_left);
        btn_book_right = (Button) findViewById(R.id.btn_book_right);
        btn_main_teacher_more = (Button) findViewById(R.id.btn_main_teacher_more);
        txt_weather = (TextView) findViewById(R.id.txt_weather);
        txt_bjtz_more = (TextView) findViewById(R.id.txt_bjtz_more);
        txt_main_nonew1 = (TextView) findViewById(R.id.txt_main_nonew1);
        txt_main_new_title1 = (TextView) findViewById(R.id.txt_main_new_title1);
        txt_main_new_nr1 = (TextView) findViewById(R.id.txt_main_new_nr1);
        txt_main_new_time1 = (TextView) findViewById(R.id.txt_main_new_time1);
        txt_xxtz_more = (TextView) findViewById(R.id.txt_xxtz_more);
        txt_main_nonew2 = (TextView) findViewById(R.id.txt_main_nonew2);
        txt_main_new_title2 = (TextView) findViewById(R.id.txt_main_new_title2);
        txt_main_new_nr2 = (TextView) findViewById(R.id.txt_main_new_nr2);
        txt_main_new_time2 = (TextView) findViewById(R.id.txt_main_new_time2);
        txt_cxmain_attend = (TextView) findViewById(R.id.txt_cxmain_attend);
        txt_cxmain_classmotto = (TextView) findViewById(R.id.txt_cxmain_classmotto);
        txt_main_kc = (TextView) findViewById(R.id.txt_main_kc);
        txt_main_time = (TextView) findViewById(R.id.txt_main_time);
        txt_main_bjxz = (TextView) findViewById(R.id.txt_main_bjxz);
        ly_main_currentTeacherTitle = (TextView) findViewById(R.id.ly_main_currentTeacherTitle);
        txt_main_teacher1 = (TextView) findViewById(R.id.txt_main_teacher1);
        txt_main_teacher2 = (TextView) findViewById(R.id.txt_main_teacher2);
        txt_main_teacher3 = (TextView) findViewById(R.id.txt_main_teacher3);
        txt_main_teacher4 = (TextView) findViewById(R.id.txt_main_teacher4);
        txt_main_teacher5 = (TextView) findViewById(R.id.txt_main_teacher5);
        txt_main_teacher6 = (TextView) findViewById(R.id.txt_main_teacher6);
        txt_main_classAdviser1 = (TextView) findViewById(R.id.txt_main_classAdviser1);
        txt_main_classAdviser2 = (TextView) findViewById(R.id.txt_main_classAdviser2);
        txt_main_classAdviser3 = (TextView) findViewById(R.id.txt_main_classAdviser3);
        txt_main_classAdviser4 = (TextView) findViewById(R.id.txt_main_classAdviser4);
        txt_main_classAdviser5 = (TextView) findViewById(R.id.txt_main_classAdviser5);
        txt_main_classAdviser6 = (TextView) findViewById(R.id.txt_main_classAdviser6);

        am_schedule1 = (TextView) findViewById(R.id.am_schedule1);
        am_schedule2 = (TextView) findViewById(R.id.am_schedule2);
        am_schedule3 = (TextView) findViewById(R.id.am_schedule3);
        am_schedule4 = (TextView) findViewById(R.id.am_schedule4);
        am_schedule5 = (TextView) findViewById(R.id.am_schedule5);
        pm_schedule1 = (TextView) findViewById(R.id.pm_schedule1);
        pm_schedule2 = (TextView) findViewById(R.id.pm_schedule2);
        pm_schedule3 = (TextView) findViewById(R.id.pm_schedule3);
        pm_schedule4 = (TextView) findViewById(R.id.pm_schedule4);
        pm_schedule5 = (TextView) findViewById(R.id.pm_schedule5);
        ly_xxtz_title_txt = (TextView) findViewById(R.id.ly_xxtz_title_txt);
        txt_main_bjxz.setOnClickListener(this);

        cx_txt_schedule_next1 = (TextView) findViewById(R.id.cx_txt_schedule_next1);
        cx_txt_schedule_next2 = (TextView) findViewById(R.id.cx_txt_schedule_next2);
        txt_main_new_title3 = (TextView) findViewById(R.id.txt_main_new_title3);
        txt_main_new_nr3 = (TextView) findViewById(R.id.txt_main_new_nr3);
        txt_main_new_time3 = (TextView) findViewById(R.id.txt_main_new_time3);
        txt_main_class = (CustomTextView) findViewById(R.id.txt_main_class);
        txt_main_teacher = (CustomTextView) findViewById(R.id.txt_main_teacher);
        img_main_teacher = (ImageView) findViewById(R.id.img_main_teacher);
        img_mian_classAdviser = (ImageView) findViewById(R.id.img_main_classAdviser);
        ly_main_classAdviser = (LinearLayout) findViewById(R.id.ly_main_classAdviser);
        img_logo = (ImageView) findViewById(R.id.img_logo);
        img_main_banner = (ImageSwitcher) findViewById(R.id.img_main_banner);
        vp_book = (CustomViewpager) findViewById(R.id.vp_book);
        txt_main_msg = (TextView) findViewById(R.id.txt_main_msg);
        txt_main_nextschedule = (TextView) findViewById(R.id.txt_main_nextschedule);
        rl_bjfc.getBackground().setAlpha(220);
        ly_cxmain_schedule.getBackground().setAlpha(220);
        ly_cxmain_attend.getBackground().setAlpha(220);
        ll_xytz.getBackground().setAlpha(220);
        ll_xywh.getBackground().setAlpha(220);
        rl_more.getBackground().setAlpha(150);
        ly_main_currentTeacher.getBackground().setAlpha(150);
        ly_main_classAdviser.getBackground().setAlpha(150);
        rl_bxxx.getBackground().setAlpha(220);
        textClock = (TextClock) findViewById(R.id.textClock);
        textClock.setFormat12Hour(null);
        textClock.setFormat24Hour("yyyy年MM月dd日\nEEEE      HH : mm");

        img_schoolnew_banner.setFactory(new ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(CXMainActivity.this);
                ImageSwitcher.LayoutParams params = new ImageSwitcher.LayoutParams(
                        ImageSwitcher.LayoutParams.MATCH_PARENT, ImageSwitcher.LayoutParams.MATCH_PARENT);
                imageView.setScaleType(ScaleType.FIT_XY);
                imageView.setLayoutParams(params);
                return imageView;
            }
        });
        img_schoolnew_banner.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.enteralpha));
        img_schoolnew_banner.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.exitalpha));

        ly_main_new1.setOnClickListener(this);
        ly_main_new2.setOnClickListener(this);
        ly_main_currentTeacher.setOnClickListener(this);
        ly_main_classAdviser.setOnClickListener(this);
        ly_cxmain_schedule1.setOnClickListener(this);
        ly_cxmain_schedule2.setOnClickListener(this);
        ly_cxmain_schedule3.setOnClickListener(this);
        ly_emergency_notice.setOnClickListener(this);
        btn_main_schedule.setOnClickListener(this);
        btn_main_new_class_bjrs.setOnClickListener(this);
        btn_main_read.setOnClickListener(this);
        btn_zhiri.setOnClickListener(this);
        btn_main_msg.setOnClickListener(this);
        btn_main_school.setOnClickListener(this);
        btn_main_more.setOnClickListener(this);
        btn_book_left.setOnClickListener(this);
        btn_book_right.setOnClickListener(this);
        btn_main_teacher_more.setOnClickListener(this);
        txt_weather.setOnClickListener(this);
        txt_bjtz_more.setOnClickListener(this);
        btn_jingbao.setOnClickListener(this);
        txt_xxtz_more.setOnClickListener(this);
        txt_cxmain_attend.setOnClickListener(this);
        txt_main_class.setOnClickListener(this);
        img_schoolnew_banner.setOnClickListener(this);

        if (application.isAppInstalled(this, MyApplication.CHAOXING)) {
            btn_main_msg.setBackgroundResource(R.drawable.cx_btn_mian_cxxxt);
            txt_main_msg.setText("学习通");
        } else {
            btn_main_msg.setBackgroundResource(R.drawable.cx_img_app_control);
            txt_main_msg.setText("物联网");
        }

        vp_book.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                fragment_i = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        if (application.classInfoModel != null) {
            txt_main_class.setText(application.classInfoModel.bjmc);
        }
        application.httpProcess.getWeather(application.equInfoModel.jssysdm);
        updata_now_curriculum();
        updata_new_notice();
        update_new_class();
        updata_jjtz();
        updata_book();
        update_logo();
        updata_classmotto();
        updata_classAdviser();


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_main_bjxz: {
                if (application.curriculumInfoModel_now != null) {
                    Intent intent = new Intent(CXMainActivity.this, CXAttendActivity.class);
                    intent.putExtra("type", 1);
                    intent.putExtra("skjhid", application.curriculumInfoModel_now.id);
                    intent.putExtra("kcmc", application.curriculumInfoModel_now.kcmc);
                    startActivity(intent);
                }
                break;
            }
            case R.id.ly_main_new1: {
                if (notice_bjtz.xxzt.contains("调课")) {
                    //调课通知处理
                    if (MyApplication.tktzActiveAlive == false) {
                        Intent intent = new Intent(this, CXNoticeDetailsActivity.class);
                        intent.putExtra("from", "CXMainActivity");
                        intent.putExtra("type", 2);
                        intent.putExtra("id", notice_bjtz.id);
                        startActivity(intent);
                    }

                } else {
                    //普通通知处理
                    Intent intent = new Intent(this, CXNoticeDetailsActivity.class);
                    intent.putExtra("from", "CXMainActivity");
                    intent.putExtra("type", 2);
                    intent.putExtra("id", notice_bjtz.id);
                    startActivity(intent);
                }
                break;
            }
            case R.id.ly_main_new2: {
                if (ly_emergency_notice.getVisibility() != View.VISIBLE) {
                    Intent intent = new Intent(this, CXNoticeDetailsActivity.class);
                    intent.putExtra("from", "CXMainActivity");
                    intent.putExtra("type", 1);
                    intent.putExtra("id", notice_xxtz.id);
                    startActivity(intent);
                }
                break;
            }
            case R.id.ly_emergency_notice: {
                if (notice_jjtz != null) {
                    if (MyApplication.jjtzActiveAlive == false) {
                        Intent intent = new Intent(this, CXNoticeDetailsActivity.class);
                        intent.putExtra("from", "CXMainActivity");
                        intent.putExtra("type", 6);
                        intent.putExtra("id", notice_jjtz.id);
                        startActivity(intent);
                    }
                }
                break;
            }
            case R.id.ly_cxmain_schedule1:
            case R.id.ly_cxmain_schedule2:
            case R.id.ly_cxmain_schedule3: {
                Intent intent = new Intent(this, CXAttendActivity.class);
                if (application.curriculumInfoModel_now != null) {
                    intent.putExtra("skjhid", application.curriculumInfoModel_now.id);
                } else if (application.curriculumInfoModel_next != null) {
                    intent.putExtra("skjhid", application.curriculumInfoModel_next.id);
                }
                startActivity(intent);
                break;
            }

            case R.id.ly_main_currentTeacher: {
//                //当前教师单击
//                if (application.curriculumInfoModel_now != null && application.curriculumInfoModel_now.skjs != null) {
////                    Intent intent = new Intent(this, CXSingleTeacherActivity.class);
////                    intent.putExtra("id", application.curriculumInfoModel_now.skjs);
////                    startActivity(intent);
//
//                    TeacherInfoDatabase teacherInfoDatabase = new TeacherInfoDatabase();
//                    if(application.curriculumInfoModel_now.skjs!=null)
//                    {
//                        TeacherInfoModel teacherInfoModel = teacherInfoDatabase.query_by_id(application.curriculumInfoModel_now.skjs);
//                        if(teacherInfoModel!=null) {
//                            application.showTeacherDialog(teacherInfoModel);
//
//                        }
//                    }
//                }


                //展示教师单击
                if (curriculumInfoModelShow != null && curriculumInfoModelShow.skjs != null) {
                    TeacherInfoDatabase teacherInfoDatabase = new TeacherInfoDatabase();
                    if (curriculumInfoModelShow.skjs != null) {
                        TeacherInfoModel teacherInfoModel = teacherInfoDatabase.query_by_id(curriculumInfoModelShow.skjs);
                        if (teacherInfoModel != null) {
                            application.showTeacherDialog(teacherInfoModel);

                        }
                    }
                }

                break;
            }
            case R.id.ly_main_classAdviser: {
                //班主任单击
                if (application.classInfoModel != null && application.classInfoModel.fdy != null && (!application.classInfoModel.fdy.toLowerCase().equals("null"))) {
//                    Intent intent = new Intent(this, CXSingleTeacherActivity.class);
//                    intent.putExtra("id", application.classInfoModel.fdy);
//                    startActivity(intent);

                    TeacherInfoDatabase teacherInfoDatabase = new TeacherInfoDatabase();
                    if (application.classInfoModel.fdy != null) {
                        TeacherInfoModel teacherInfoModel = teacherInfoDatabase.query_by_id(application.classInfoModel.fdy);
                        if (teacherInfoModel != null) {
                            application.showTeacherDialog(teacherInfoModel);

                        }
                    }

                }
                break;
            }

            case R.id.btn_main_schedule: {
                Intent intent = new Intent(this, CXCurriculumActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_main_new_class_bjrs: {
                Intent intent = new Intent(this, CXClassNewActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_zhiri: {
                Intent intent = new Intent(this, ZhiriActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_main_read: {
                try {
                    application.openApp(this, MyApplication.READ);
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent intent = new Intent(this, WebviewActivity.class);
                    startActivity(intent);
                }
                break;
            }
            case R.id.btn_main_msg: {
                if (application.isAppInstalled(this, MyApplication.CHAOXING)) {
                    application.cardType = "QRY_PERSON_CHAOXING";
                    DialogPermissionOnlyCard();
                } else {
                    application.operateType = "CONTROL";
                    DialogPermission();
                }
                break;
            }
            case R.id.btn_main_school: {
                Intent intent = new Intent(this, CXSchoolNewActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_main_more: {
                Intent intent = new Intent(this, CXAppActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_book_left: {
                if (fragment_i > 0) {
                    fragment_i--;
                    vp_book.setCurrentItem(fragment_i);
                }
                break;
            }
            case R.id.btn_book_right: {
                if (fragment_i < fragment_total - 1) {
                    fragment_i++;
                    vp_book.setCurrentItem(fragment_i);
                }
                break;
            }
            case R.id.txt_weather: {
                break;
            }
            case R.id.btn_jingbao: {
                Intent intent = new Intent(this, CXJingBaoActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.txt_bjtz_more: {
                Intent intent = new Intent(this, CXNoticeListActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
                break;
            }
            case R.id.txt_xxtz_more: {
                Intent intent = new Intent(this, CXNoticeListActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
                break;
            }
            case R.id.txt_cxmain_attend: {
//                if(applicatitxt_main_teacher1on.curriculumInfoModel_now!=null){
//                    Intent intent = new Intent(this, CXAttendActivity.class);
//                    intent.putExtra("skjhid", application.curriculumInfoModel_now.id);
//                    startActivity(intent);
//                }
//                if (personnelAttendanceModelList != null) {
//                    Intent intent = new Intent(this, CXAttendBJActivity.class);
//                    startActivity(intent);
//                }

//                if (personnelAttendanceModelList != null) {
                Intent intent = new Intent(this, CXCurriculumActivity.class);
                startActivity(intent);
//                }
                break;
            }
            case R.id.txt_main_class: {
                // Intent intent = new Intent(this, ClassInfoActivity.class);
                // startActivity(intent);
                break;
            }
            case R.id.img_schoolnew_banner: {
//				if (new_pics != null && new_pics.size() > 0) {
//					int i;
//					if (new_pics_i == 0) {
//						i = new_pics.size() - 1;
//					} else {
//						i = new_pics_i - 1;
//					}
//					CarouselDatabase carouselDatabase = new CarouselDatabase();
//					List<CarouselModel> carouselModels = carouselDatabase.query("modelType = ?",
//							new String[]{CarouselModel.TYPE_SCHOOL});
//					if (carouselModels != null && carouselModels.size() > 0) {
//						CarouselModel model = carouselModels.get(0);
//						Intent intent = new Intent(this, CXNoticeDetailsActivity.class);
//						intent.putExtra("from", "CXjMainActivity");
//						if (model.dataSource.equals(CarouselModel.SOURCE_CLASS_NEWS)) {
//							intent.putExtra("type", 4);
//						} else if (model.dataSource.equals(CarouselModel.SOURCE_SCHOOL_NEWS)) {
//							intent.putExtra("type", 3);
//						}
//						intent.putExtra("id", new_pics.get(i).get("id"));
//						startActivity(intent);
//					}
//				}
                Intent intent = new Intent(this, CXSchoolNewActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_main_teacher_more: {
                Intent intent = new Intent(this, CXTeacherActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_permission_user: {
                btn_permission_user.setBackgroundResource(R.drawable.btn_permission_type);
                btn_permission_user.setTextColor(getResources().getColor(R.color.white));
                ly_permission_user.setVisibility(View.VISIBLE);
                btn_permission_card.setBackgroundResource(R.drawable.btn_permission_type_click);
                btn_permission_card.setTextColor(getResources().getColor(R.color.blue));
                ly_permission_card.setVisibility(View.INVISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            }
            case R.id.btn_permission_card: {
                btn_permission_card.setBackgroundResource(R.drawable.btn_permission_type);
                btn_permission_card.setTextColor(getResources().getColor(R.color.white));
                ly_permission_card.setVisibility(View.VISIBLE);
                btn_permission_user.setBackgroundResource(R.drawable.btn_permission_type_click);
                btn_permission_user.setTextColor(getResources().getColor(R.color.blue));
                ly_permission_user.setVisibility(View.INVISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && edt_permission_user != null)
                    imm.hideSoftInputFromWindow(edt_permission_user.getWindowToken(), 0);
                break;
            }
            case R.id.btn_premission_login:
                String name = edt_permission_user.getText().toString();
                String pwd = edt_permission_pwd.getText().toString();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
                    if (name.equals("admin") && pwd.equals("admin123!@#")) {
                        application.showToast("认证通过");
                        if (application.dialog != null && application.dialog.isShowing()) {
                            application.dialog.dismiss();
                        }
                        if (application.operateType.equals("CONTROL")) {
                            application.openOrvibo("3", null);
                        } else if (application.operateType.equals("VEDIO_CALL")) {
                            application.call_from_who = name;
                            Intent i = new Intent(CXMainActivity.this, VideoCallActivity.class);
                            startActivity(i);
                        } else if (application.operateType.equals("SETTING")) {
                            Intent i = new Intent(CXMainActivity.this, SettingActivity.class);
                            startActivity(i);
                        }
                        return;
                    }
                    UserInfoDatabase userInfoDatabase = new UserInfoDatabase();
                    if (userInfoDatabase.query_by_userName(name, pwd) != null) {
                        application.showToast("认证通过");
                        if (application.dialog != null && application.dialog.isShowing()) {
                            application.dialog.dismiss();
                        }
                        if (application.operateType.equals("CONTROL")) {
                            application.openOrvibo("1", userInfoDatabase.query_by_userName(name, pwd).userName);
                        } else if (application.operateType.equals("VEDIO_CALL")) {
                            application.call_from_who = name;
                            Intent i = new Intent(CXMainActivity.this, VideoCallActivity.class);
                            startActivity(i);
                        } else if (application.operateType.equals("SETTING")) {
                            Intent i = new Intent(CXMainActivity.this, SettingActivity.class);
                            startActivity(i);
                        }
                        return;
                    }
                    btn_premission_login.setEnabled(false);
                    if (progressDialog == null) {
                        progressDialog = ProgressDialog.show(CXMainActivity.this, null, "认证中，请稍后", false, false);
                    }
                    application.httpProcess.Permission(application.equInfoModel.jssysdm, application.operateType,
                            "ACCOUNT_PASSWORD", name, pwd, "");
                    if (application.operateType.equals("VEDIO_CALL")) {
                        application.call_from_who = name;
                    }
                } else {
                    application.showToast("账号或密码不能为空");
                }
                break;
            case R.id.btn_premission_cancel1:
            case R.id.btn_premission_cancel2:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && edt_permission_user != null)
                    imm.hideSoftInputFromWindow(edt_permission_user.getWindowToken(), 0);
                if (application.dialog != null && application.dialog.isShowing()) {
                    application.dialog.dismiss();
                }
                break;
        }
    }

    private EditText edt_permission_user, edt_permission_pwd;
    private Button btn_permission_user, btn_permission_card, btn_premission_login, btn_premission_cancel1,
            btn_premission_cancel2;
    private RelativeLayout ly_permission_user, ly_permission_card;

    private void DialogPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CXMainActivity.this);
        application.dialog = builder.create();
        LayoutInflater inflater = LayoutInflater.from(application);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.dialog_permission, null);
        application.dialog.setView(layout);
        application.dialog.show();
        application.dialog.getWindow().setLayout(900, 700);
        application.dialog.getWindow().setContentView(R.layout.dialog_permission);
        // application.dialog.getWindow().setSoftInputMode(
        // WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        application.dialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
                if (arg2.getAction() == KeyEvent.ACTION_UP && arg1 == KeyEvent.KEYCODE_MENU
                        && !TextUtils.isEmpty(application.operateType)
                        && application.operateType.equals("VEDIO_CALL")) {
                    if (application.dialog != null && application.dialog.isShowing()) {
                        application.dialog.dismiss();
                    }
                }
                return false;
            }
        });
        application.dialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                application.operateType = null;
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });
        ly_permission_user = (RelativeLayout) application.dialog.findViewById(R.id.ly_permission_user);
        ly_permission_card = (RelativeLayout) application.dialog.findViewById(R.id.ly_permission_card);
        edt_permission_user = (EditText) application.dialog.findViewById(R.id.edt_permission_user);
        edt_permission_pwd = (EditText) application.dialog.findViewById(R.id.edt_permission_pwd);
        btn_permission_user = (Button) application.dialog.findViewById(R.id.btn_permission_user);
        btn_permission_card = (Button) application.dialog.findViewById(R.id.btn_permission_card);
        btn_premission_login = (Button) application.dialog.findViewById(R.id.btn_premission_login);
        btn_premission_cancel1 = (Button) application.dialog.findViewById(R.id.btn_premission_cancel1);
        btn_premission_cancel2 = (Button) application.dialog.findViewById(R.id.btn_premission_cancel2);
        btn_permission_user.setOnClickListener(this);
        btn_permission_card.setOnClickListener(this);
        btn_premission_login.setOnClickListener(this);
        btn_premission_cancel1.setOnClickListener(this);
        btn_premission_cancel2.setOnClickListener(this);
        edt_permission_user.addTextChangedListener(application.textWatcher);
        edt_permission_pwd.addTextChangedListener(application.textWatcher);
        // btn_permission_card.setEnabled(false);
        // btn_permission_user
        // .setBackgroundResource(R.drawable.btn_permission_type);
        // btn_permission_user
        // .setTextColor(getResources().getColor(R.color.white));
        // ly_permission_user.setVisibility(View.VISIBLE);
        // btn_permission_card
        // .setBackgroundResource(R.drawable.btn_permission_type_click);
        // btn_permission_card.setTextColor(getResources().getColor(R.color.blue));
        // ly_permission_card.setVisibility(View.INVISIBLE);
        // InputMethodManager imm = (InputMethodManager)
        // getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void DialogPermissionOnlyCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CXMainActivity.this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageLoader.clearCache();
        imageLoader.cancelTask();
        handler.removeMessages(3);
        handler.removeMessages(4);
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
        if (!equModelTask()) {
            if (displayManager == null) {
                updata_banner();
                update_attend();
            }
            application.handler_touch.removeMessages(0);
            application.handler_touch.sendEmptyMessageDelayed(0, application.screensaver_time * 1000);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeMessages(3);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 更新当前课程信息
    private void updata_now_curriculum() {
//        am_schedule1.setText("----");
//        am_schedule2.setText("----");
//        am_schedule3.setText("----");
//        am_schedule4.setText("----");
//        am_schedule5.setText("----");
//        pm_schedule1.setText("----");
//        pm_schedule2.setText("----");
//        pm_schedule3.setText("----");
//        pm_schedule4.setText("----");
//        pm_schedule5.setText("----");
        am_schedule1.setText("");
        am_schedule2.setText("");
        am_schedule3.setText("");
        am_schedule4.setText("");
        am_schedule5.setText("");
        pm_schedule1.setText("");
        pm_schedule2.setText("");
        pm_schedule3.setText("");
        pm_schedule4.setText("");
        pm_schedule5.setText("");
        if ("02".equals(application.equInfoModel.equMode)) {
            updata_now_curriculum_zj();
            return;
        }

        if (application.curriculumInfos_today != null && application.curriculumInfos_today.size() > 0) {
            for (int i = 0; i < application.curriculumInfos_today.size(); i++) {
                Log.e("kcmc", "kcmc:" + application.curriculumInfos_today.get(i).kcmc + "  jc:" + application.curriculumInfos_today.get(i).skrq);
                int txtcolor;
                if (application.curriculumInfoModel_now != null && application.curriculumInfos_today.get(i).id == application.curriculumInfoModel_now.id) {
                    txtcolor = Color.GREEN;


                } else {
                    txtcolor = Color.BLACK;
                }
                Date d = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String dateNowStr = sdf.format(d);
                if (application.curriculumInfos_today.get(i).jc.equals("1")) {
                    am_schedule1.setText(application.curriculumInfos_today.get(i).kcmc);
                    am_schedule1.setTextColor(txtcolor);
                }
                if (application.curriculumInfos_today.get(i).jc.equals("2")) {
                    am_schedule2.setText(application.curriculumInfos_today.get(i).kcmc);
                    am_schedule2.setTextColor(txtcolor);
                }
                if (application.curriculumInfos_today.get(i).jc.equals("3")) {
                    am_schedule3.setText(application.curriculumInfos_today.get(i).kcmc);
                    am_schedule3.setTextColor(txtcolor);
                }
                if (application.curriculumInfos_today.get(i).jc.equals("4")) {
                    am_schedule4.setText(application.curriculumInfos_today.get(i).kcmc);
                    am_schedule4.setTextColor(txtcolor);
                }
                if (application.curriculumInfos_today.get(i).jc.equals("5")) {
                    am_schedule5.setText(application.curriculumInfos_today.get(i).kcmc);
                    am_schedule5.setTextColor(txtcolor);
                }
                if (application.curriculumInfos_today.get(i).jc.equals("6")) {
                    pm_schedule1.setText(application.curriculumInfos_today.get(i).kcmc);
                    pm_schedule1.setTextColor(txtcolor);
                }
                if (application.curriculumInfos_today.get(i).jc.equals("7")) {
                    pm_schedule2.setText(application.curriculumInfos_today.get(i).kcmc);
                    pm_schedule2.setTextColor(txtcolor);
                }
                if (application.curriculumInfos_today.get(i).jc.equals("8")) {
                    pm_schedule3.setText(application.curriculumInfos_today.get(i).kcmc);
                    pm_schedule3.setTextColor(txtcolor);
                }
                if (application.curriculumInfos_today.get(i).jc.equals("9")) {
                    pm_schedule4.setText(application.curriculumInfos_today.get(i).kcmc);
                    pm_schedule4.setTextColor(txtcolor);
                }

                if (application.curriculumInfos_today.get(i).jc.equals("10")) {
                    pm_schedule5.setText(application.curriculumInfos_today.get(i).kcmc);
                    pm_schedule5.setTextColor(txtcolor);
                }
            }

        }

//			if (application.curriculumInfos_today != null && application.curriculumInfos_today.size() > 0) {
//
//
//
//			if (application.curriculumInfoModel_now != null) {
//				// txt_main_cc.setText("当前课程");
//				// txt_main_nonschedule.setVisibility(View.INVISIBLE);
//				// ly_main_schedule.setVisibility(View.VISIBLE);
//				ly_cxmain_schedule.setBackgroundResource(R.drawable.cx_ly_schedule);
//				ly_cxmain_schedule1.setVisibility(View.VISIBLE);
//				ly_cxmain_schedule2.setVisibility(View.VISIBLE);
//				ly_cxmain_schedule3.setVisibility(View.VISIBLE);
//				txt_main_kc.setText(application.curriculumInfoModel_now.kcmc);
//				txt_main_time.setText(application.curriculumInfoModel_now.skrq + "\n"
//						+ MyApplication.jcToTime(application.curriculumInfoModel_now.jc));
//				txt_main_teacher.setText(application.curriculumInfoModel_now.skjsxm);
//
//				application.curriculumInfoModel_next = null;
//				for (int i = 0; i < application.curriculumInfos_today.size(); i++) {
//					if (application.curriculumInfos_today.get(i).id.equals(application.curriculumInfoModel_now.id)
//							&& application.curriculumInfos_today.size() > (i + 1)) {
//						application.curriculumInfoModel_next = application.curriculumInfos_today.get(i + 1);
//						break;
//					}
//				}
//				if (application.curriculumInfoModel_next != null) {
//					// txt_main_nextschedule.setVisibility(View.VISIBLE);
//					// txt_main_nextschedule.setText("下一节 : "
//					// + application.curriculumInfoModel_next.kcmc);
//					cx_txt_schedule_next1.setVisibility(View.VISIBLE);
//					cx_txt_schedule_next2.setVisibility(View.VISIBLE);
//					cx_txt_schedule_next2.setText("下一节  : " + application.curriculumInfoModel_next.kcmc);
//				} else {
//					// txt_main_nextschedule.setVisibility(View.INVISIBLE);
//					cx_txt_schedule_next1.setVisibility(View.INVISIBLE);
//					cx_txt_schedule_next2.setVisibility(View.INVISIBLE);
//				}
//			} else {
//				application.curriculumInfoModel_next = null;
//				try {
//					application.curriculumInfoModel_next = findNextCurriculum();
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//				if (application.curriculumInfoModel_next != null) {
//					 //txt_main_cc.setText("下一节课程");
//					// txt_main_nextschedule.setVisibility(View.INVISIBLE);
//					// txt_main_nonschedule.setVisibility(View.INVISIBLE);
//					// ly_main_schedule.setVisibility(View.VISIBLE);
//					ly_cxmain_schedule.setBackgroundResource(R.drawable.cx_ly_schedule_next);
//					ly_cxmain_schedule1.setVisibility(View.VISIBLE);
//					ly_cxmain_schedule2.setVisibility(View.VISIBLE);
//					ly_cxmain_schedule3.setVisibility(View.VISIBLE);
//					txt_main_kc.setText(application.curriculumInfoModel_next.kcmc);
//					txt_main_time.setText(application.curriculumInfoModel_next.skrq + "\n"
//							+ MyApplication.jcToTime(application.curriculumInfoModel_next.jc));
//					txt_main_teacher.setText(application.curriculumInfoModel_next.skjsxm);
//				} else {
//					// txt_main_cc.setText("当前课程");
//					// ly_main_schedule.setVisibility(View.INVISIBLE);
//					// txt_main_nonschedule.setVisibility(View.VISIBLE);
//					// txt_main_nextschedule.setVisibility(View.INVISIBLE);
//					ly_cxmain_schedule.setBackgroundResource(R.drawable.cx_ly_schedule);
//					ly_cxmain_schedule1.setVisibility(View.INVISIBLE);
//					ly_cxmain_schedule2.setVisibility(View.INVISIBLE);
//					ly_cxmain_schedule3.setVisibility(View.INVISIBLE);
//				}
//			}
//		} else {
//			application.curriculumInfoModel_next = null;
//			try {
//				application.curriculumInfoModel_next = findNextCurriculum();
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			if (application.curriculumInfoModel_next != null) {
//				// txt_main_cc.setText("下一节课程");
//				// txt_main_nextschedule.setVisibility(View.INVISIBLE);
//				// txt_main_nonschedule.setVisibility(View.INVISIBLE);
//				// ly_main_schedule.setVisibility(View.VISIBLE);
//				ly_cxmain_schedule.setBackgroundResource(R.drawable.cx_ly_schedule_next);
//				ly_cxmain_schedule1.setVisibility(View.VISIBLE);
//				ly_cxmain_schedule2.setVisibility(View.VISIBLE);
//				ly_cxmain_schedule3.setVisibility(View.VISIBLE);
//				txt_main_kc.setText(application.curriculumInfoModel_next.kcmc);
//				txt_main_time.setText(application.curriculumInfoModel_next.skrq + "\n"
//						+ MyApplication.jcToTime(application.curriculumInfoModel_next.jc));
//				txt_main_teacher.setText(application.curriculumInfoModel_next.skjsxm);
//			} else {
//				// txt_main_cc.setText("当前课程");
//				// ly_main_schedule.setVisibility(View.INVISIBLE);
//				// txt_main_nonschedule.setVisibility(View.VISIBLE);
//				// txt_main_nextschedule.setVisibility(View.INVISIBLE);
//				ly_cxmain_schedule.setBackgroundResource(R.drawable.cx_ly_schedule);
//				ly_cxmain_schedule1.setVisibility(View.INVISIBLE);
//				ly_cxmain_schedule2.setVisibility(View.INVISIBLE);
//				ly_cxmain_schedule3.setVisibility(View.INVISIBLE);
//			}
//		}
        update_attend();
        update_teacher();
    }

    // 更新当前课程信息
    private void updata_now_curriculum_zj() {
        if (application.curriculumInfos_today != null && application.curriculumInfos_today.size() > 0) {
            if (application.curriculumInfoModel_now != null) {
                // txt_main_cc.setText("当前课程");
                // txt_main_nonschedule.setVisibility(View.INVISIBLE);
                // ly_main_schedule.setVisibility(View.VISIBLE);
                ly_cxmain_schedule.setBackgroundResource(R.drawable.cx_ly_schedule_zj);
                ly_cxmain_schedule1.setVisibility(View.VISIBLE);
                ly_cxmain_schedule2.setVisibility(View.VISIBLE);
                ly_cxmain_schedule3.setVisibility(View.VISIBLE);
                txt_main_kc.setText(application.curriculumInfoModel_now.kcmc);
                txt_main_time.setText(application.curriculumInfoModel_now.skrq + "\n"
                        + MyApplication.jcToTime(application.curriculumInfoModel_now.jc));
                txt_main_teacher.setText(application.curriculumInfoModel_now.skjsxm);

                application.curriculumInfoModel_next = null;
                for (int i = 0; i < application.curriculumInfos_today.size(); i++) {
                    if (application.curriculumInfos_today.get(i).id.equals(application.curriculumInfoModel_now.id)
                            && application.curriculumInfos_today.size() > (i + 1)) {
                        application.curriculumInfoModel_next = application.curriculumInfos_today.get(i + 1);
                        break;
                    }
                }
                if (application.curriculumInfoModel_next != null) {
                    txt_main_nextschedule.setVisibility(View.VISIBLE);
                    txt_main_nextschedule.setText("下一节  : " + application.curriculumInfoModel_next.kcmc);
                    // cx_txt_schedule_next1.setVisibility(View.VISIBLE);
                    // cx_txt_schedule_next2.setVisibility(View.VISIBLE);
                    // cx_txt_schedule_next2
                    // .setText(application.curriculumInfoModel_next.kcmc);
                } else {
                    txt_main_nextschedule.setVisibility(View.INVISIBLE);
                    // cx_txt_schedule_next1.setVisibility(View.INVISIBLE);
                    // cx_txt_schedule_next2.setVisibility(View.INVISIBLE);
                }
            } else {
                application.curriculumInfoModel_next = null;
                try {
                    application.curriculumInfoModel_next = findNextCurriculum();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (application.curriculumInfoModel_next != null) {
                    // txt_main_cc.setText("下一节课程");
                    // txt_main_nextschedule.setVisibility(View.INVISIBLE);
                    // txt_main_nonschedule.setVisibility(View.INVISIBLE);
                    // ly_main_schedule.setVisibility(View.VISIBLE);
                    ly_cxmain_schedule.setBackgroundResource(R.drawable.cx_ly_schedule_next_zj);
                    ly_cxmain_schedule1.setVisibility(View.VISIBLE);
                    ly_cxmain_schedule2.setVisibility(View.VISIBLE);
                    ly_cxmain_schedule3.setVisibility(View.VISIBLE);
                    txt_main_kc.setText(application.curriculumInfoModel_next.kcmc);
                    txt_main_time.setText(application.curriculumInfoModel_next.skrq + "\n"
                            + MyApplication.jcToTime(application.curriculumInfoModel_next.jc));
                    txt_main_teacher.setText(application.curriculumInfoModel_next.skjsxm);
                } else {
                    // txt_main_cc.setText("当前课程");
                    // ly_main_schedule.setVisibility(View.INVISIBLE);
                    // txt_main_nonschedule.setVisibility(View.VISIBLE);
                    // txt_main_nextschedule.setVisibility(View.INVISIBLE);
                    ly_cxmain_schedule.setBackgroundResource(R.drawable.cx_ly_schedule_zj);
                    ly_cxmain_schedule1.setVisibility(View.INVISIBLE);
                    ly_cxmain_schedule2.setVisibility(View.INVISIBLE);
                    ly_cxmain_schedule3.setVisibility(View.INVISIBLE);
                }
            }
        } else {
            application.curriculumInfoModel_next = null;
            try {
                application.curriculumInfoModel_next = findNextCurriculum();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (application.curriculumInfoModel_next != null) {
                // txt_main_cc.setText("下一节课程");
                // txt_main_nextschedule.setVisibility(View.INVISIBLE);
                // txt_main_nonschedule.setVisibility(View.INVISIBLE);
                // ly_main_schedule.setVisibility(View.VISIBLE);
                ly_cxmain_schedule.setBackgroundResource(R.drawable.cx_ly_schedule_next_zj);
                ly_cxmain_schedule1.setVisibility(View.VISIBLE);
                ly_cxmain_schedule2.setVisibility(View.VISIBLE);
                ly_cxmain_schedule3.setVisibility(View.VISIBLE);
                txt_main_kc.setText(application.curriculumInfoModel_next.kcmc);
                txt_main_time.setText(application.curriculumInfoModel_next.skrq + "\n"
                        + MyApplication.jcToTime(application.curriculumInfoModel_next.jc));
                txt_main_teacher.setText(application.curriculumInfoModel_next.skjsxm);
            } else {
                // txt_main_cc.setText("当前课程");
                // ly_main_schedule.setVisibility(View.INVISIBLE);
                // txt_main_nonschedule.setVisibility(View.VISIBLE);
                // txt_main_nextschedule.setVisibility(View.INVISIBLE);
                ly_cxmain_schedule.setBackgroundResource(R.drawable.cx_ly_schedule_zj);
                ly_cxmain_schedule1.setVisibility(View.INVISIBLE);
                ly_cxmain_schedule2.setVisibility(View.INVISIBLE);
                ly_cxmain_schedule3.setVisibility(View.INVISIBLE);
            }
        }
        update_attend();
        update_teacher();
    }

    private CurriculumInfoModel findNextCurriculum() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
        if (application.curriculumInfos_today != null) {
            if (application.curriculumInfoModel_now == null) {
                SectionInfoDatabase database = new SectionInfoDatabase();
                String HHmmss = formatter.format(System.currentTimeMillis());
                Date time = formatter.parse(HHmmss);
                for (CurriculumInfoModel model : application.curriculumInfos_today) {
                    String[] jcs = model.jc.split("-");
                    SectionInfoModel sectionInfoModel = database.query_by_jcdm(jcs[0]);
                    if (sectionInfoModel != null && time.getTime() < Long.parseLong(sectionInfoModel.jcskkssj)) {
                        return model;
                    }
                }
            }
        }

        formatter = new SimpleDateFormat("yyyy-MM-dd");
        CurriculumInfoDatabase database = new CurriculumInfoDatabase();
        List<CurriculumInfoModel> list = database.query_all();
        if (list != null && list.size() > 0) {
            for (CurriculumInfoModel model : list) {
                if (formatter.parse(model.skrq).getTime() > System.currentTimeMillis()) {
                    return model;
                }
            }
        }

        return null;
    }


    private CurriculumInfoModel findLastCurriculum() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
        if (application.curriculumInfos_today != null) {
            if (application.curriculumInfoModel_now == null) {
                SectionInfoDatabase database = new SectionInfoDatabase();
                String HHmmss = formatter.format(System.currentTimeMillis());
                Date time = formatter.parse(HHmmss);

                for (int i = application.curriculumInfos_today.size() - 1; i >= 0; i--) {
                    CurriculumInfoModel model = application.curriculumInfos_today.get(i);
                    String[] jcs = model.jc.split("-");
                    SectionInfoModel sectionInfoModel = database.query_by_jcdm(jcs[0]);
                    if (sectionInfoModel != null && time.getTime() > Long.parseLong(sectionInfoModel.jcskjssj)) {
                        return model;
                    }
                }
            }
        }

        formatter = new SimpleDateFormat("yyyy-MM-dd");
        CurriculumInfoDatabase database = new CurriculumInfoDatabase();
        List<CurriculumInfoModel> list = database.query_all();
        if (list != null && list.size() > 0) {
            for (int i = list.size() - 1; i >= 0; i--) {
                CurriculumInfoModel model = list.get(i);
                if (formatter.parse(model.skrq).getTime() < System.currentTimeMillis()) {
                    return model;
                }
            }
        }
        return null;
    }


    private void update_new_class() {
        if (application.classInfoModel != null) {
            btn_main_new_class_bjrs.setText("班级人数：" + application.classInfoModel.bjzxrs + "人");
        } else {
            btn_main_new_class_bjrs.setText("");
        }
    }

    private void noticeBanner() {
        Log.e("通知", "通知");
        //轮播通知
        NoticeInfoDatabase database = new NoticeInfoDatabase();
        List<NoticeInfoModel> all_list = new ArrayList<>();
        if (database.query_by_lmdm("bpxxtz") != null && database.query_by_lmdm("bpxxtz").size() > 0) {
            all_list.addAll(database.query_by_lmdm("bpxxtz"));
        }
//		if(database.query_by_lmdm("bpbjtz")!=null&&database.query_by_lmdm("bpbjtz").size()>0){
//			all_list.addAll(database.query_by_lmdm("bpbjtz"));
//		}
//		if(database.query_by_lmdm("bpjjtz")!=null&&database.query_by_lmdm("bpjjtz").size()>0){
//			all_list.addAll(database.query_by_lmdm("bpjjtz"));
//		}
        if (all_list != null && all_list.size() > 0) {

            Intent intent = new Intent(this, CXNoticeDetailsBannerActivity.class);
            intent.putExtra("from", "CXMainActivity");
            Bundle b = new Bundle();
            b.putSerializable("NoticeInfoModelList", (Serializable) all_list);
            intent.putExtras(b);
            intent.putExtra("type", 520);
            startActivity(intent);
        }
    }

    private void updata_new_notice() {
        if(notice_jjtz==null) {
            xxtzList.clear();            ;
            NoticeInfoDatabase database = new NoticeInfoDatabase();
            List<NoticeInfoModel> list = database.query_by_lmdm("bpbjtz", 0, 1);
            if (list != null && list.size() == 1) {
                xxtzList.addAll(database.query_by_lmdm("bpbjtz"));
                notice_bjtz = list.get(0);
                ly_main_new1.setVisibility(View.VISIBLE);
                txt_main_nonew1.setVisibility(View.INVISIBLE);
                txt_main_new_title1.setText(notice_bjtz.xxzt);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                txt_main_new_time1.setText(formatter.format(Long.parseLong(notice_bjtz.fbsj)));
                txt_main_new_nr1.setText("");
                if (!TextUtils.isEmpty(notice_bjtz.xxnr)) {
                    Document document = Jsoup.parse(notice_bjtz.xxnr);
                    Elements p = document.select("p");
                    if (p != null && p.size() > 0) {
                        String new_rl_1 = "";
                        for (Element e : p) {
                            if (TextUtils.isEmpty(new_rl_1)) {
                                new_rl_1 = e.text();
                            } else {
                                new_rl_1 = new_rl_1 + "\n" + e.text();
                            }
                            if (!TextUtils.isEmpty(new_rl_1) && new_rl_1.length() > 100) {
                                break;
                            }
                        }
                        txt_main_new_nr1.setText(new_rl_1);
                    }
                    if (notice_bjtz.xxzt.contains("调课")) {

                        long now = System.currentTimeMillis();
                        long star = Long.parseLong(notice_bjtz.fbsj);
                        long end = Long.parseLong(notice_bjtz.endTime);


                        if (now > star && now < end) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
                            try {
                                Date nowTime = dateFormat.parse(dateFormat.format(now));
                                Date minTime = dateFormat.parse(dateFormat.format(now));
                                minTime.setHours(7);
                                minTime.setMinutes(20);
                                minTime.setSeconds(00);

                                Date maxTime = dateFormat.parse(dateFormat.format(now));
                                maxTime.setHours(7);
                                maxTime.setMinutes(30);
                                maxTime.setSeconds(00);

                                if (nowTime.getTime() > minTime.getTime() &&
                                        (nowTime.getTime() < maxTime.getTime())) {
                                    ly_main_new1.performClick();
                                } else if (nowTime.getTime() < minTime.getTime()) {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ly_main_new1.performClick();
                                        }
                                    }, minTime.getTime() - nowTime.getTime());
                                }
                            } catch (Exception ex) {

                            }
                        }
                    }
                }
            } else {
                ly_main_new1.setVisibility(View.INVISIBLE);
                txt_main_nonew1.setVisibility(View.VISIBLE);
            }

            list = database.query_by_lmdm("bpxxtz", 0, 1);
            //lph 20190514
            //xxtzList.addAll(database.query_by_lmdm("bpxxtz"));
            if (list != null && list.size() > 0) {
                //lph 20150514
                xxtzList.addAll(database.query_by_lmdm("bpxxtz"));
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        if (item < xxtzList.size()) {
                            notice_xxtz = xxtzList.get(item);
                            item++;
                        } else {
                            item = 0;
                            notice_xxtz = xxtzList.get(item);
                        }

                        handler.sendEmptyMessage(200);
                        Log.e("切换", "通知切换" + item + "  size" + xxtzList.size());


                    }
                }, 1000, 10000);

            } else {
                ly_main_new2.setVisibility(View.INVISIBLE);
                txt_main_nonew2.setVisibility(View.VISIBLE);
            }
        }

    }

    private void updata_jjtz() {
        notice_jjtz = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        NoticeInfoDatabase database = new NoticeInfoDatabase();
        List<NoticeInfoModel> list = database.query_by_lmdm("bpjjtz", 0, 1);
        if (list != null && list.size() == 1) {
            try {
                NoticeInfoModel model = list.get(0);
                long now = System.currentTimeMillis();
                long star = Long.parseLong(model.fbsj);
                long end = Long.parseLong(model.endTime);
                if (star <= now && now < end) {
                    notice_jjtz = model;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        notice_yyjz = null;
        list = database.query_by_lmdm("bpyyjz", 0, 1);
        if (list != null && list.size() == 1) {
            try {
                NoticeInfoModel model = list.get(0);
                long now = System.currentTimeMillis();
                long star = Long.parseLong(model.fbsj);
                long end = Long.parseLong(model.endTime);
                if (star <= now && now < end) {
                    notice_yyjz = model;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (notice_jjtz != null) {
            //ly_xxtz_title.setVisibility(View.INVISIBLE);
            ly_xxtz_title_txt.setText("紧急通知");
            // ly_main_new2.setVisibility(View.INVISIBLE);
            txt_main_nonew2.setVisibility(View.INVISIBLE);
            ly_main_new2.setVisibility(View.INVISIBLE);
            ly_emergency_notice.setVisibility(View.VISIBLE);
            //ly_emergency_notice.setBackgroundResource(R.drawable.cx_ly_emergency_notice);

            txt_main_new_title3.setText(notice_jjtz.xxzt);
            format = new SimpleDateFormat("yyyy-MM-dd");
            txt_main_new_time3.setText(format.format(Long.parseLong(notice_jjtz.fbsj)));
            txt_main_new_nr3.setText("");
            if (!TextUtils.isEmpty(notice_jjtz.xxnr)) {
                Document document = Jsoup.parse(notice_jjtz.xxnr);
                Elements p = document.select("p");
                if (p != null && p.size() > 0) {
                    String new_rl_1 = "";
                    for (Element e : p) {
                        if (TextUtils.isEmpty(new_rl_1)) {
                            new_rl_1 = e.text();
                        } else {
                            new_rl_1 = new_rl_1 + "\n" + e.text();
                        }
                        if (!TextUtils.isEmpty(new_rl_1) && new_rl_1.length() > 100) {
                            break;
                        }
                    }
                    txt_main_new_nr3.setText(new_rl_1);
                }
            }
            ly_emergency_notice.performClick();
        } else if (notice_yyjz != null) {
            //ly_xxtz_title.setVisibility(View.INVISIBLE);
            ly_xxtz_title_txt.setText("紧急通知");
            // ly_main_new2.setVisibility(View.INVISIBLE);
            txt_main_nonew2.setVisibility(View.INVISIBLE);
            ly_main_new2.setVisibility(View.INVISIBLE);
            ly_emergency_notice.setVisibility(View.VISIBLE);
            //ly_emergency_notice.setBackgroundResource(R.drawable.cx_ly_yyjz_notice);

            txt_main_new_title3.setText(notice_yyjz.xxzt);
            format = new SimpleDateFormat("yyyy-MM-dd");
            txt_main_new_time3.setText(format.format(Long.parseLong(notice_yyjz.fbsj)));
            txt_main_new_nr3.setText("");
            if (!TextUtils.isEmpty(notice_yyjz.xxnr)) {
                Document document = Jsoup.parse(notice_yyjz.xxnr);
                Elements p = document.select("p");
                if (p != null && p.size() > 0) {
                    String new_rl_1 = "";
                    for (Element e : p) {
                        if (TextUtils.isEmpty(new_rl_1)) {
                            new_rl_1 = e.text();
                        } else {
                            new_rl_1 = new_rl_1 + "\n" + e.text();
                        }
                        if (!TextUtils.isEmpty(new_rl_1) && new_rl_1.length() > 100) {
                            break;
                        }
                    }
                    txt_main_new_nr3.setText(new_rl_1);
                }
            }
        } else {
            //ly_xxtz_title.setVisibility(View.VISIBLE);
            ly_xxtz_title_txt.setText("校园通知");
            // ly_main_new2.setVisibility(View.VISIBLE);
            ly_emergency_notice.setVisibility(View.INVISIBLE);
            //ly_main_new2.setVisibility(View.VISIBLE);
            //txt_main_nonew2.setVisibility(View.VISIBLE);
        }
    }

    private void updata_book() {
        BooksInfoDatabase database = new BooksInfoDatabase();
        List<BooksInfoModel> list = database.query_all();
        fragment_i = 0;
        fragment_total = 0;
        List<Fragment> fragments = new ArrayList<Fragment>();
        if (list != null && list.size() > 0) {
            if (list.size() % 5 == 0) {
                fragment_total = list.size() / 5;
            } else {
                fragment_total = list.size() / 5 + 1;
            }
        }
        if (fragment_total > 0) {
            for (int i = 0; i < fragment_total; i++) {
                BookFragment fragment = new BookFragment();
                fragment.setImageLoader(imageLoader);
                if (i == fragment_total - 1) {
                    fragment.setList(list.subList(5 * i, list.size()));
                } else {
                    fragment.setList(list.subList(5 * i, 5 * i + 5));
                }
                fragments.add(fragment);
            }
            MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager(), fragments);
            vp_book.setAdapter(adapter);
            btn_book_left.setVisibility(View.VISIBLE);
            btn_book_right.setVisibility(View.VISIBLE);
        } else {
            vp_book.setAdapter(null);
            btn_book_left.setVisibility(View.INVISIBLE);
            btn_book_right.setVisibility(View.INVISIBLE);
        }
    }

    private void update_attend() {

        //修改增加首页当前班级考勤
//        initPersonAttendance();
        String attend = "";
        int yd = 0, cq = 0, qq = 0, cd = 0,qj=0,zt=0;
//        List<AttendInfoModel> list = null;
//        AttendInfoDatabase database = new AttendInfoDatabase();
//
//        if (application.curriculumInfoModel_now != null) {
//            list = database.query_by_skjhid(application.curriculumInfoModel_now.id);
//        } else if (application.curriculumInfoModel_next != null) {
//            list = database.query_by_skjhid(application.curriculumInfoModel_next.id);
//        }


        if (personnelAttendanceModelList != null && personnelAttendanceModelList.size() > 0) {
            yd = personnelAttendanceModelList.size();
            for (PersonnelAttendanceModel model : personnelAttendanceModelList) {
                // 考勤状态 0出勤 1事假 2病假 3迟到 4旷课 5早退 6公假 7实习 8集训 9其它 10缺席
                // 11未签到 12签退
                String kqzt = model.status.split(",")[0];
                Log.e("考勤", "我查询考勤状态:" + kqzt);
                if (kqzt.equals("3") ) {
                    cd++;
                }
                else if (kqzt.equals("5")) {
                    zt++;
                }
                else if (kqzt.equals("1") || kqzt.equals("2") ){
                    qj++;
                }
                    else if( kqzt.equals("4") || kqzt.equals("10") || kqzt.equals("11")) {
                    qq++;
                }
                if (kqzt.equals("0") || kqzt.equals("3") || kqzt.equals("5")) {
                    cq++;
                }
            }
            attend = "应到 : " + yd + "人\t\t\t出勤 : " + cq + "人\t\t\t出勤率 : " + cq * 100 / yd + "%\n缺勤 : " + qq
                    + "人\t\t\t迟到 : " + cd + "人\t\t\t早退："+zt+"人\t\t\t请假:"+qj+"人";
        } else {
            attend = "当前课程没有考勤信息";
        }


        if (TextUtils.isEmpty(attend)) {
            txt_cxmain_attend.setVisibility(View.INVISIBLE);
        } else {
            txt_cxmain_attend.setVisibility(View.VISIBLE);
            txt_cxmain_attend.setText(attend);
        }
    }

    CurriculumInfoModel curriculumInfoModelShow = null;

    private void update_teacher() {

        if (application.curriculumInfoModel_now == null) {
            ly_cxmain_book.setVisibility(View.VISIBLE);
            ly_cxmain_teacher.setVisibility(View.INVISIBLE);


            try {
                application.curriculumInfoModel_next = findNextCurriculum();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String DateNow = formatter.format(System.currentTimeMillis());

            if (application.curriculumInfoModel_next != null
                    && (application.curriculumInfoModel_next.skrq.equals(DateNow))) {
                //显示当天下一节课程
                ly_main_currentTeacherTitle.setText("下节课教师");
                curriculumInfoModelShow = application.curriculumInfoModel_next;
            } else {
                try {
                    application.curriculumInfoModel_last = findLastCurriculum();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //显示上一节课程
                if (application.curriculumInfoModel_last != null
                        && application.curriculumInfoModel_last.skrq.equals(DateNow)) {
                    ly_main_currentTeacherTitle.setText("上节课教师");
                    curriculumInfoModelShow = application.curriculumInfoModel_last;
                }
            }

        } else {
            ly_main_currentTeacherTitle.setText("当前教师");
            curriculumInfoModelShow = application.curriculumInfoModel_now;
        }

        if (curriculumInfoModelShow != null) {

            AppLog.d("首页显示课程老师：");
            AppLog.d(curriculumInfoModelShow.toString());

            ly_cxmain_book.setVisibility(View.INVISIBLE);
            ly_cxmain_teacher.setVisibility(View.VISIBLE);
            TeacherInfoDatabase database = new TeacherInfoDatabase();
            TeacherInfoModel model = database.query_by_id(curriculumInfoModelShow.skjs);
            String xm = "", byzy = "", zc = "", byxx = "", zgxlmc = "", jl = "", bjxz = "";
            if (model != null) {
                if (!TextUtils.isEmpty(model.zp)) {
                    String[] s = model.zp.split("\\.");
                    String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE + "/teacher_" + model.rybh + "."
                            + s[s.length - 1];
                    imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

                        @Override
                        public void onImageLoader(Bitmap bitmap, String url) {
                            if (bitmap != null) {
                                img_main_teacher.setImageBitmap(bitmap);
                            } else {
                                img_main_teacher.setImageResource(R.drawable.user_icon);
                            }
                        }
                    }, 188, 188);
                } else {
                    img_main_teacher.setImageResource(R.drawable.user_icon);
                }
                if (!TextUtils.isEmpty(model.xm)) {
                    xm = model.xm;
                }
                if (!TextUtils.isEmpty(model.byzy)) {
                    byzy = model.byzy;
                }
                if (!TextUtils.isEmpty(model.jgzc)) {
                    zc = model.jgzc;
                }
                if (!TextUtils.isEmpty(model.byxx)) {
                    byxx = model.byxx;
                }
                if (!TextUtils.isEmpty(model.zgxlmc)) {
                    zgxlmc = model.zgxlmc;
                }
                if (!TextUtils.isEmpty(model.jgjl)) {
                    jl = model.jgjl;
                }
            } else {
                img_main_teacher.setImageResource(R.drawable.user_icon);
            }

            if (curriculumInfoModelShow.kcmc != null) {
                if (curriculumInfoModelShow.kcmc.contains("语文")
                        || curriculumInfoModelShow.kcmc.contains("数学")
                        || curriculumInfoModelShow.kcmc.contains("英语")
                        || curriculumInfoModelShow.kcmc.contains("物理")) {
                    bjxz = "行政班";
                } else if (curriculumInfoModelShow.kcmc.length() > 0) {
                    bjxz = "教学班";
                }
            }
            txt_main_teacher1.setText("教师姓名 : " + xm);
            txt_main_teacher2.setText("所学专业 : " + byzy);
            txt_main_teacher3.setText("职称 : " + zc);
            txt_main_teacher4.setText("荣誉称号 : " + byxx);
            txt_main_teacher5.setText("学历学位 : " + zgxlmc);
            txt_main_teacher6.setText("教师教龄 : " + jl);
            txt_main_bjxz.setText(bjxz);
        }


    }


//    private void update_teacher() {
//        if (application.curriculumInfoModel_now == null) {
//            ly_cxmain_book.setVisibility(View.VISIBLE);
//            ly_cxmain_teacher.setVisibility(View.INVISIBLE);
//
//            try {
//                application.curriculumInfoModel_next = findNextCurriculum();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            if (application.curriculumInfoModel_next != null) {
//                //显示当天下一节课程
//
//            }
//            else {
//                //显示上一节课程
//                if (application.curriculumInfoModel_last != null) {
//
//                }
//            }
//
//        } else {
//            ly_cxmain_book.setVisibility(View.INVISIBLE);
//            ly_cxmain_teacher.setVisibility(View.VISIBLE);
//            TeacherInfoDatabase database = new TeacherInfoDatabase();
//            TeacherInfoModel model = database.query_by_id(application.curriculumInfoModel_now.skjs);
//            String xm = "", byzy = "", zc = "", byxx = "", zgxlmc = "", jl = "", bjxz = "";
//            if (model != null) {
//                if (!TextUtils.isEmpty(model.zp)) {
//                    String[] s = model.zp.split("\\.");
//                    String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE + "/teacher_" + model.rybh + "."
//                            + s[s.length - 1];
//                    imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {
//
//                        @Override
//                        public void onImageLoader(Bitmap bitmap, String url) {
//                            if (bitmap != null) {
//                                img_main_teacher.setImageBitmap(bitmap);
//                            } else {
//                                img_main_teacher.setImageResource(R.drawable.user_icon);
//                            }
//                        }
//                    }, 188, 188);
//                } else {
//                    img_main_teacher.setImageResource(R.drawable.user_icon);
//                }
//                if (!TextUtils.isEmpty(model.xm)) {
//                    xm = model.xm;
//                }
//                if (!TextUtils.isEmpty(model.byzy)) {
//                    byzy = model.byzy;
//                }
//                if (!TextUtils.isEmpty(model.jgzc)) {
//                    zc = model.jgzc;
//                }
//                if (!TextUtils.isEmpty(model.byxx)) {
//                    byxx = model.byxx;
//                }
//                if (!TextUtils.isEmpty(model.zgxlmc)) {
//                    zgxlmc = model.zgxlmc;
//                }
//                if (!TextUtils.isEmpty(model.jgjl)) {
//                    jl = model.jgjl;
//                }
//            } else {
//                img_main_teacher.setImageResource(R.drawable.user_icon);
//            }
//
//            if (application.curriculumInfoModel_now.kcmc.contains("语文")
//                    || application.curriculumInfoModel_now.kcmc.contains("数学")
//                    || application.curriculumInfoModel_now.kcmc.contains("英语")
//                    || application.curriculumInfoModel_now.kcmc.contains("物理")) {
//                bjxz = "行政班";
//            } else if (application.curriculumInfoModel_now.kcmc != null
//                    && application.curriculumInfoModel_now.kcmc.length() > 0) {
//                bjxz = "教学班";
//            }
//
//            ly_main_currentTeacherTitle.setText("当前教师");
//            txt_main_teacher1.setText("教师姓名 : " + xm);
//            txt_main_teacher2.setText("所学专业 : " + byzy);
//            txt_main_teacher3.setText("职称 : " + zc);
//            txt_main_teacher4.setText("荣誉称号 : " + byxx);
//            txt_main_teacher5.setText("学历学位 : " + zgxlmc);
//            txt_main_teacher6.setText("教师教龄 : " + jl);
//            txt_main_bjxz.setText(bjxz);
//        }
//    }


    private void updata_classAdviser() {
        TeacherInfoDatabase database = new TeacherInfoDatabase();
        TeacherInfoModel model = database.query_by_id(application.classInfoModel.fdy);
        String xm = "", byzy = "", zc = "", byxx = "", zgxlmc = "", jl = "";
        if (model != null) {
            if (!TextUtils.isEmpty(model.zp)) {
                String[] s = model.zp.split("\\.");
                String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE + "/teacher_" + model.rybh + "."
                        + s[s.length - 1];
                imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

                    @Override
                    public void onImageLoader(Bitmap bitmap, String url) {
                        if (bitmap != null) {
                            img_mian_classAdviser.setImageBitmap(bitmap);
                        } else {
                            img_mian_classAdviser.setImageResource(R.drawable.user_icon);
                        }
                    }
                }, 188, 188);
            } else {
                img_mian_classAdviser.setImageResource(R.drawable.user_icon);
            }
            if (!TextUtils.isEmpty(model.xm)) {
                xm = model.xm;
            }
            if (!TextUtils.isEmpty(model.byzy)) {
                byzy = model.byzy;
            }
            if (!TextUtils.isEmpty(model.jgzc)) {
                zc = model.jgzc;
            }
            if (!TextUtils.isEmpty(model.byxx)) {
                byxx = model.byxx;
            }
            if (!TextUtils.isEmpty(model.zgxlmc)) {
                zgxlmc = model.zgxlmc;
            }
            if (!TextUtils.isEmpty(model.jgjl)) {
                jl = model.jgjl;
            }
        } else {
            img_main_teacher.setImageResource(R.drawable.user_icon);
        }
        txt_main_classAdviser1.setText("教师姓名 : " + xm);
        txt_main_classAdviser2.setText("所学专业 : " + byzy);
        txt_main_classAdviser3.setText("职称 : " + zc);
        txt_main_classAdviser4.setText("荣誉称号 : " + byxx);
        txt_main_classAdviser5.setText("学历学位 : " + zgxlmc);
        txt_main_classAdviser6.setText("教师教龄 : " + jl);

    }


    private void update_logo() {
        if (TextUtils.isEmpty(application.logoUrl)) {
            img_logo.setImageResource(R.drawable.logo11);
        } else {
            imageLoader.getBitmapFormUrl(application.logoUrl, new OnImageLoaderListener() {
                @Override
                public void onImageLoader(Bitmap bitmap, String url) {
                    if (bitmap != null) {
                        img_logo.setImageBitmap(bitmap);
                    } else {
                        img_logo.setImageResource(R.drawable.logo11);
                    }
                }
            });
        }
    }

    private void updata_classmotto() {
        BlackboardInfoDatabase database = new BlackboardInfoDatabase();
        BlackboardInfoModel model = database.query();
        if (model == null) {
            txt_cxmain_classmotto.setText("");
        } else {
            txt_cxmain_classmotto.setText(model.content);
        }
    }

    private void updata_banner() {
        Log.e("图片", "图片");
        handler.removeMessages(3);
        handler.removeMessages(4);
        ly_banner.removeAllViews();
        ly_banner.setVisibility(View.INVISIBLE);
        btn_main_school.setVisibility(View.GONE);
        img_schoolnew_banner.setVisibility(View.VISIBLE);
        //xmd
        //img_schoolnew_banner
        CarouselDatabase carouselDatabase = new CarouselDatabase();
        List<CarouselModel> carouselModels = carouselDatabase.query("modelType = ?",
                new String[]{CarouselModel.TYPE_SCHOOL});
        pics = new ArrayList<String>();
        pics_i = 0;
        new_pics = new ArrayList<Map<String, String>>();
        new_pics_i = 0;
        Log.e("相册", "相册carouselDatabase:" + carouselModels);
        if (carouselModels != null && carouselModels.size() > 0) {
            CarouselModel model = carouselModels.get(0);
            Log.e("相册", "相册:" + model.dataSource);
            if (CarouselModel.SOURCE_SX.equals(model.dataSource)) {
                PlayTaskDatabase database = new PlayTaskDatabase();
                List<PlayTaskModel> list = database.query(
                        "playType = ? and position = ? and srcType = ?",
                        new String[]{"REGION_MEDIA", "02", "TEMPLATE"});
                String policyPath = null;
                if (list != null && list.size() > 0) {
                    if (TextUtils.isEmpty(list.get(0).srcPath)) {
                        File file = new File(application.getFtpPath(list.get(0).srcPath));
                        if (file.exists()) {
                            policyPath = application.getFtpPath(list.get(0).srcPath);
                        }
                    }
                }
                if (!TextUtils.isEmpty(policyPath)) {
                    ly_banner.removeAllViews();
                    ly_banner.setVisibility(View.VISIBLE);
                    btn_main_school.setVisibility(View.VISIBLE);
                    img_schoolnew_banner.setVisibility(View.INVISIBLE);
                    if (displayManager_media == null) {
                        displayManager_media = new DisplayManager(this, application, ly_banner);
                        displayManager_media.start(policyPath);
                    } else {
                        displayManager_media.stop();
                        displayManager_media.destroy();
                        displayManager_media.start(policyPath);
                    }
                } else {
                    updata_banner_default();
                }
            } else if (CarouselModel.SOURCE_CLASS_PHOTO
                    .equals(model.dataSource)) {
                PicDreOrActivityDatabase database = new PicDreOrActivityDatabase();
                List<PicDreOrActivityModel> list = database.query(
                        "objectType = ? and kind = ?",
                        new String[]{"1", "2"});
                if (list != null && list.size() > 0) {
                    for (PicDreOrActivityModel picModel : list) {
                        if (!TextUtils.isEmpty(picModel.objectEntityAddress)) {
                            String[] s = picModel.objectEntityAddress
                                    .split("/");
                            String path = MyApplication.PATH_ROOT
                                    + MyApplication.PATH_PHOTO + "/"
                                    + s[s.length - 1];
                            pics.add(path);
                        }
                    }
                }
                updata_banner_pic();
            } else if (CarouselModel.SOURCE_SCHOOL_PHOTO
                    .equals(model.dataSource)) {
                PicDreOrActivityDatabase database = new PicDreOrActivityDatabase();
                List<PicDreOrActivityModel> list = database.query(
                        "objectType = ? and kind = ?",
                        new String[]{"1", "1"});
                if (list != null && list.size() > 0) {
                    for (PicDreOrActivityModel picModel : list) {
                        if (!TextUtils.isEmpty(picModel.objectEntityAddress)) {
                            String[] s = picModel.objectEntityAddress
                                    .split("/");
                            String path = MyApplication.PATH_ROOT
                                    + MyApplication.PATH_PHOTO + "/"
                                    + s[s.length - 1];
                            pics.add(path);
                        }
                    }
                }
                updata_banner_pic();
            } else if (CarouselModel.SOURCE_CXXXT.equals(model.dataSource)) {
                ScreensaverDatabase database = new ScreensaverDatabase();
                List<ScreensaverModel> list = database.query_all();
                if (list != null && list.size() > 0) {
                    for (ScreensaverModel screensaverModel : list) {
                        if (!TextUtils.isEmpty(screensaverModel.url)) {
                            String[] s = screensaverModel.url.split("/");
                            String path = MyApplication.PATH_ROOT
                                    + MyApplication.PATH_SCREENSAVER + "/"
                                    + s[s.length - 1];
                            pics.add(path);
                        }
                    }
                }
                updata_banner_pic();
            } else {
                updata_banner_default();
            }
        } else {
            updata_banner_default();
        }
    }

    private void updata_banner_pic() {
        if (pics != null && pics.size() > 0) {
            if (pics.size() == 1) {
                imageLoader.getBitmapFormUrl(pics.get(0), new OnImageLoaderListener() {
                    @Override
                    public void onImageLoader(Bitmap bitmap, String url) {
                        if (bitmap != null)
                            img_schoolnew_banner.setImageDrawable(new BitmapDrawable(bitmap));
                        else
                            updata_banner_default();
                    }
                }, 900, 300);
            } else if (pics.size() > 1) {
                boolean b = false;
                for (String s : pics) {
                    File file = new File(s);
                    if (file.exists()) {
                        b = true;
                        break;
                    }
                }
                if (b) {
                    handler.sendEmptyMessage(3);
                } else {
                    updata_banner_default();
                }
            }
        } else {
            updata_banner_default();
        }
    }

    private void updata_banner_notice(String lmdm) {
        NoticeInfoDatabase database = new NoticeInfoDatabase();
        List<NoticeInfoModel> models = database.query_by_lmdm(lmdm);
        if (models != null && models.size() > 0) {
            for (NoticeInfoModel model : models) {
                if (!TextUtils.isEmpty(model.xxnr)) {
                    Document document = Jsoup.parse(model.xxnr);
                    Element element = document.select("img").first();
                    if (element != null) {
                        String src = element.attr("src");
                        if (!TextUtils.isEmpty(src)) {
                            String[] s = src.split("/");
                            String path = MyApplication.PATH_ROOT + MyApplication.PATH_NOTICE + "/" + s[s.length - 1];
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("url", src);
                            map.put("path", path);
                            map.put("id", model.id);
                            new_pics.add(map);
                        }
                    }
                }
                if (new_pics.size() >= 5) {
                    break;
                }
            }
            if (new_pics != null && new_pics.size() > 0) {
                handler.sendEmptyMessage(4);
            } else {
                updata_banner_default();
            }
        } else {
            updata_banner_default();
        }
    }

    private void updata_banner_default() {
        imageLoader.getBitmapFormRes(R.drawable.cx_ly_banner, new OnImageLoaderListener() {
            @Override
            public void onImageLoader(Bitmap bitmap, String url) {
                img_schoolnew_banner.setImageDrawable(new BitmapDrawable(bitmap));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            isGo = false;
            kcid = null;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200: {
                    if(notice_jjtz==null) {
                        ly_main_new2.setVisibility(View.VISIBLE);
                        txt_main_nonew2.setVisibility(View.INVISIBLE);
                        txt_main_new_title2.setText(notice_xxtz.xxzt);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        txt_main_new_time2.setText(formatter.format(Long.parseLong(notice_xxtz.fbsj)));
                        txt_main_new_nr2.setText("");
                        if (!TextUtils.isEmpty(notice_xxtz.xxnr)) {
                            Document document = Jsoup.parse(notice_xxtz.xxnr);
                            Elements p = document.select("p");
                            if (p != null && p.size() > 0) {
                                String new_rl_1 = "";
                                for (Element e : p) {
                                    if (TextUtils.isEmpty(new_rl_1)) {
                                        new_rl_1 = e.text();
                                    } else {
                                        new_rl_1 = new_rl_1 + "\n" + e.text();
                                    }
                                    if (!TextUtils.isEmpty(new_rl_1) && new_rl_1.length() > 100) {
                                        break;
                                    }
                                }
                                txt_main_new_nr2.setText(new_rl_1);
                            }
                        }
                    }
                    break;
                }
                case 0: {
                    application.createFloating();
                    break;
                }
                case 520: {
                    initPersonAttendance();
                    if (personnelAttendanceModelList != null && !isGo && personnelAttendanceModelList.size() > 0) {
                        isGo = true;
                        Intent intent1 = new Intent(CXMainActivity.this, CXFullAttendActivity.class);
                        Bundle b = new Bundle();
                        b.putSerializable("personnelAttendanceModelList", (Serializable) personnelAttendanceModelList);
                        intent1.putExtras(b);
                        intent1.putExtra("kcid", kcid);
                        startActivityForResult(intent1, 10);
                    }


                    break;
                }
                case 1: {
                    if (displayManager != null) {
                        displayManager.updata(AppCodeView.APPCODE_UPDATA_CURRICULUM);
                    } else {
                        updata_now_curriculum();
                    }
                    if (application.curriculumInfoModel_now != null
                            && application.getRunningActivityName().equals("com.advertisement.activity.DisplayActivity")) {
                        Intent intent = new Intent(MyApplication.BROADCAST);
                        intent.putExtra(MyApplication.BROADCAST_TAG, "PlayActivity_finish");
                        broadcastManager.sendBroadcast(intent);
                    }


                    break;
                }
                case 2: {
                    if (displayManager != null) {
                        displayManager.updata(AppCodeView.APPCODE_UPDATA_CURRICULUM);
                    } else {
                        updata_now_curriculum();
                    }
                    if (application.curriculumInfos_today != null && application.curriculumInfos_today.size() > 0) {
//                        curriculumInfos_today_id = 0;
//                        application.httpProcess.QryAttend(application.equInfoModel.jssysdm,
//                                application.curriculumInfos_today.get(curriculumInfos_today_id++).id, null);
                        application.isUpdateCurriculum = false;
                        application.showToast("更新课程完成");
                    } else {
                        application.isUpdateCurriculum = false;
                        application.showToast("更新课程完成");
                    }
                    break;
                }
                case 3: {
                    imageLoader.getBitmapFormUrl(pics.get(pics_i), new OnImageLoaderListener() {
                        @Override
                        public void onImageLoader(Bitmap bitmap, String url) {
                            if (bitmap != null)
                                img_schoolnew_banner.setImageDrawable(new BitmapDrawable(bitmap));
                            if (pics_i == pics.size() - 1) {
                                pics_i = 0;
                            } else {
                                pics_i++;
                            }
                            handler.sendEmptyMessageDelayed(3, 5 * 1000);
                        }
                    }, 900, 300);
                    break;
                }
                case 4: {
                    if (new_pics.size() == 1) {
                        File file = new File(new_pics.get(0).get("path"));
                        if (!file.exists()) {
                            downLoadImg(new_pics.get(0).get("url"), new_pics.get(0).get("path"));
                        } else {
                            imageLoader.getBitmapFormUrl(new_pics.get(0).get("path"), new OnImageLoaderListener() {
                                @Override
                                public void onImageLoader(Bitmap bitmap, String url) {
                                    if (bitmap != null)
                                        img_schoolnew_banner.setImageDrawable(new BitmapDrawable(bitmap));
                                }
                            }, 900, 300);
                        }
                    } else if (new_pics.size() > 1) {
                        File file = new File(new_pics.get(new_pics_i).get("path"));
                        if (!file.exists()) {
                            downLoadImg(new_pics.get(new_pics_i).get("url"), new_pics.get(new_pics_i).get("path"));
                        } else {
                            imageLoader.getBitmapFormUrl(new_pics.get(new_pics_i).get("path"), new OnImageLoaderListener() {
                                @Override
                                public void onImageLoader(Bitmap bitmap, String url) {
                                    if (bitmap != null)
                                        img_schoolnew_banner.setImageDrawable(new BitmapDrawable(bitmap));
                                    if (new_pics_i == new_pics.size() - 1) {
                                        new_pics_i = 0;
                                    } else {
                                        new_pics_i++;
                                    }
                                    handler.sendEmptyMessageDelayed(4, 5 * 1000);
                                }
                            }, 900, 300);
                        }
                    }
                    break;
                }
                case 11:
                    if (displayManager != null) {
                        displayManager.updata(AppCodeView.APPCODE_UPDATA_HONOR);
                    }
                    break;
                case 12:
                    application.showDialogOff();
                    break;
                case 13:
                    try {
                        jjtz();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private Thread threadTime = new Thread(new Runnable() {

        @Override
        public void run() {
            while (isThread) {
                // 根据时间更新当前课程
                String hhmmss = MyApplication.getTime("HHmmss");
                int time = Integer.parseInt(hhmmss);
                timingOFFON();
                if (application.curriculumInfos_today != null) {
                    if (time > 80000 && time < 84500) {
                        kcid = application.curriculumInfos_today.get(0).id;
                    } else if (time > 85000 && time < 93500) {
                        kcid = application.curriculumInfos_today.get(1).id;
                    } else if (time > 94000 && time < 102500) {
                        kcid = application.curriculumInfos_today.get(2).id;
                    } else if (time > 103000 && time < 111500) {
                        kcid = application.curriculumInfos_today.get(3).id;
                    } else if (time > 112000 && time < 120500) {
                        kcid = application.curriculumInfos_today.get(4).id;
                    } else if (time > 140000 && time < 145000) {
                        kcid = application.curriculumInfos_today.get(5).id;
                    } else if (time > 145000 && time < 155000) {
                        kcid = application.curriculumInfos_today.get(6).id;
                    } else if (time > 155000 && time < 163500) {
                        kcid = application.curriculumInfos_today.get(7).id;
                    }
                }
                //课间考勤跳转
                if (kcid != null && !isGo && (time > 75100 && time < 75500) || (time > 85200 && time < 85500) || (time > 94200 && time < 94500) || (time > 103200 && time < 103500) || (time > 112200 && time < 112500) || (time > 140700 && time < 141000) || (time > 145700 && time < 150000) || (time > 155200 && time < 155500)) {
                    handler.sendEmptyMessage(520);
                }
                if (hhmmss.endsWith("30") || hhmmss.endsWith("00")) {
                    initPersonAttendance();
                }
                if (hhmmss.endsWith("00")) {
                    equModelTask();
                    // playTask();
                    //更新紧急通知
                    handler.sendEmptyMessage(13);
                    // try {
                    // Absenteeism(MyApplication.getTime("HH:mm:ss"));
                    // } catch (ParseException e) {
                    // e.printStackTrace();
                    // }

                    CurriculumInfoModel model = application.findCurriculum(10);
                    if (model != null && application.curriculumInfoModel_now != null
                            && !model.id.equals(application.curriculumInfoModel_now.id)) {

                        application.curriculumInfoModel_now = model;
                        handler.sendEmptyMessage(1);
                    } else if (model != null && application.curriculumInfoModel_now == null) {
                        application.curriculumInfoModel_now = model;
                        handler.sendEmptyMessage(1);
                    } else if (model == null && application.curriculumInfoModel_now != null) {
                        application.curriculumInfoModel_now = model;
                        handler.sendEmptyMessage(1);
                    }
                }
                // 根据时间更新当天课程和天气
                if (hhmmss.equals("000000")) {
                    application.isUpdateCurriculum = true;
                    application.httpProcess.QryCurriculum_day(application.equInfoModel.jssysdm,
                            MyApplication.getTime("yyyy-MM-dd") + " 00:00:00",
                            MyApplication.getTime("yyyy-MM-dd") + " 23:59:59", "", null);
                    application.httpProcess.QryPersonAttend(null, MyApplication.getTime("yyyy-MM-dd"), "");
                    application.httpProcess.getWeather(application.equInfoModel.jssysdm);
                    // try {
                    // application.getOFFONTime();
                    // } catch (Exception e) {
                    // e.printStackTrace();
                    // }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConstData.ACTIVITY_UPDATE)) {
                if (application.pageLayoutTotal > 0) {
                    AppLog.d(TAG, "------------ConstData.ACTIVITY_UPDATE-----------");
                    boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                    // application.pageLayoutCount++;
                    // if (application.pageLayoutCount <
                    // application.pageLayoutTotal) {
                    // application.systemManager
                    // .downloadPolicy(pageLayoutModels
                    // .get(application.pageLayoutCount).resPath);
                    // } else {
                    application.pageLayoutTotal = 0;
                    application.pageLayoutCount = 0;
                    PageLayoutDatabase database = new PageLayoutDatabase();
                    if (result) {
                        PageLayoutModel model = database.query_by_pageCode("INDEX");
                        if (model != null) {
                            ly_activity_main.removeAllViews();
                            if (displayManager == null) {
                                displayManager = new DisplayManager(CXMainActivity.this, application, ly_activity_main);
                                displayManager.setImageLoader(imageLoader);
                                displayManager.setActivity("com.cx.doorplate.activity.CXMainActivity");
                                displayManager.setActivity(CXMainActivity.this);
                                displayManager.setFragmentActivity(CXMainActivity.this);
                            }
                            displayManager.start(application.getFtpPath(model.resPath));
                        }
                        application.httpProcess.commitBack(application.pageSn, HttpProcess.SUCESS,
                                HttpProcess.SUCESS_MSG);
                    } else {
                        database.delete_all();
                        application.showToast("页面更新失败");
                        application.httpProcess.commitBack(application.pageSn, HttpProcess.ERROR, "页面更新失败");
                    }
                    // }
                }
            }
            if (!intent.getAction().equals(MyApplication.BROADCAST)) {
                return;
            }
            String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
            Log.e("查询", "首页查询:tag" + tag);
            if (tag.equals(HttpProcess.QRY_ATTEND) && !application.isUpdateEQ && !application.isQryAttend
                    && !application.getRunningActivityName().equals("com.yy.doorplate.activity.EmptyActivity")
                    && !application.isQryAttendTCP) {
                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                String msg = intent.getStringExtra(MyApplication.CMD_MSG);
                if (result) {
                    if (application.curriculumInfos_today != null && application.curriculumInfos_today.size() > 0) {
                        AppLog.d(TAG, tag + " " + msg + " " + curriculumInfos_today_id);
                        if (curriculumInfos_today_id < application.curriculumInfos_today.size()) {
//                            application.httpProcess.QryAttend(application.equInfoModel.jssysdm,
//                                    application.curriculumInfos_today.get(curriculumInfos_today_id++).id, null);
                        } else {
                            application.showToast("更新课程完成");
                            application.isUpdateCurriculum = false;
                            updateImg();
                        }
                    }
                } else {
                    application.isUpdateCurriculum = false;
                    Log.e(TAG, tag + " " + msg);
                    application.showToast("更新课程失败，原因：" + msg);
                }
            } else if (tag.equals(HttpProcess.QRY_ATTEND) && !application.isUpdateEQ && !application.isQryAttend
                    && !application.getRunningActivityName().equals("com.yy.doorplate.activity.EmptyActivity")
                    && application.isQryAttendTCP) {
                application.isQryAttendTCP = false;
                update_attend();
            } else if (tag.equals(HttpProcess.OPERATE_AUTHORIZATION)) {
                if (TextUtils.isEmpty(application.operateType)) {
                    return;
                }
                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                String msg = intent.getStringExtra(MyApplication.CMD_MSG);
                if (result) {
                    AppLog.d(TAG, tag + " " + msg);
                    application.showToast("认证通过");
                    if (application.dialog != null && application.dialog.isShowing()) {
                        application.dialog.dismiss();
                    }
                    if (application.operateType.equals("CONTROL")) {
                        String userName = intent.getStringExtra("userName");
                        String rybm = intent.getStringExtra("rybm");
                        if (!TextUtils.isEmpty(userName)) {
                            application.openOrvibo("1", userName);
                        } else if (!TextUtils.isEmpty(rybm)) {
                            application.openOrvibo("2", rybm);
                        }
                    } else if (application.operateType.equals("VEDIO_CALL")) {
                        Intent i = new Intent(CXMainActivity.this, VideoCallActivity.class);
                        startActivity(i);
                    } else if (application.operateType.equals("SETTING")) {
                        Intent i = new Intent(CXMainActivity.this, SettingActivity.class);
                        startActivity(i);
                    }
                } else {
                    Log.e(TAG, tag + " " + msg);
                    application.showToast("认证失败，原因：" + msg);
                }
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                if (displayManager != null) {
                    displayManager.updata(AppCodeView.APPCODE_UPDATA_DIALOG_DIS);
                } else {
                    if (btn_premission_login != null)
                        btn_premission_login.setEnabled(true);
                }
            } else if (tag.equals("permission_ui")
                    && application.getRunningActivityName().equals("com.cx.doorplate.activity.CXMainActivity")) {
                if (progressDialog == null) {
                    progressDialog = ProgressDialog.show(CXMainActivity.this, null, "认证中，请稍后", false, false);
                }
            } else if (tag.equals("commotAttend_ui") || tag.equals("absenteeism_ui")) {
                if (displayManager != null) {
                    displayManager.updata(AppCodeView.APPCODE_UPDATA_ATTEND);
                } else {
                    update_attend();
                }
            } else if (tag.equals(HttpProcess.QRY_NOTICE) && !application.isUpdateEQ
                    && !application.isUpdataNoticeList) {
                Log.e("有通知", "有通知");
                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                String msg = intent.getStringExtra(MyApplication.CMD_MSG);
                String sn = intent.getStringExtra(MyApplication.CMD_SN);
                if (result) {
                    AppLog.d(TAG, tag + " " + msg);
                    application.showToast("更新通知完成");
                    application.httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
                } else {
                    Log.e(TAG, tag + " " + msg);
                    application.showToast("更新通知失败，原因：" + msg);
                    application.httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
                }
                if (displayManager == null) {
                    updata_new_notice();
                    updata_jjtz();
                    if (application.getRunningActivityName().equals("com.cx.doorplate.activity.CXMainActivity")) {
                        updata_banner();
                    }
                } else {
                    displayManager.updata(AppCodeView.APPCODE_UPDATA_NOTIC);
                }
                application.isUpdateNotice = false;
            } else if (tag.equals(HttpProcess.QRY_CURRICULUM + "day")) {
                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                String msg = intent.getStringExtra(MyApplication.CMD_MSG);
                String sn = intent.getStringExtra(MyApplication.CMD_SN);
                CurriculumInfoDatabase database = new CurriculumInfoDatabase();
                application.curriculumInfos_today = database.query_by_skrq(MyApplication.getTime("yyyy-MM-dd"));
                application.curriculumInfoModel_now = application.findCurriculum(10);
                if (result) {
                    AppLog.d(TAG, tag + " " + msg);
                    handler.sendEmptyMessage(2);
                    application.httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
                } else {
                    Log.e(TAG, tag + " " + msg);
                    application.showToast("更新课程失败，原因：" + msg);
                    application.httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
                }
            } else if (tag.equals(HttpProcess.QRY_CURRICULUM + "week") && !application.isGrxx) {
                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                if (result) {
                    if (displayManager != null) {
                        displayManager.updata(AppCodeView.APPCODE_UPDATA_CURRICULUM);
                    }
                }
            } else if (tag.equals("updataEQ")) {
                if (displayManager == null) {
                    if (application.classInfoModel != null) {
                        txt_main_class.setText(application.classInfoModel.bjmc);
                    }
                    updata_now_curriculum();
                    updata_new_notice();
                    updata_jjtz();
                    updata_banner();
                    updata_classmotto();
                } else {
                    displayManager.updata(AppCodeView.APPCODE_UPDATA_EQU);
                }
            } else if (tag.equals("playTask")) {
                AppLog.d(TAG, "------playTask------");
                // playTask();
                if (displayManager == null) {
                    // if (application.getRunningActivityName().equals(
                    // "com.cx.doorplate.activity.CXMainActivity")) {
                    // updata_banner();
                    // }
                } else {
                    displayManager.updata(AppCodeView.APPCODE_UPDATA_REGION_MEDIA);
                }
            } else if (tag.equals(HttpProcess.QRY_WEATHER)) {
                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                if (result) {
                    if (displayManager == null) {

                        txt_weather.setText(application.currentCity.trim() + "   " + application.temperature.trim()
                                + "\n" + application.weather.trim());
                    } else {
                        displayManager.updata(AppCodeView.APPCODE_UPDATA_WEATHER);
                    }
                }
            } else if (tag.equals(HttpProcess.QRY_EQUMODEL)) {
                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                String msg = intent.getStringExtra(MyApplication.CMD_MSG);
                String sn = intent.getStringExtra(MyApplication.CMD_SN);
                if (result) {
                    application.httpProcess.getExam(application.equInfoModel.jssysdm, null);
                    application.httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
                } else {
                    application.httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
                }
            } else if (tag.equals(HttpProcess.QRY_EXAM)) {
                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                String msg = intent.getStringExtra(MyApplication.CMD_MSG);
                String sn = intent.getStringExtra(MyApplication.CMD_SN);
                if (result) {
                    equModelTask();
                    application.httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
                } else {
                    application.httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
                }
            } else if (tag.equals(HttpProcess.QRY_PAGE) && !application.isUpdateEQ) {
                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                String msg = intent.getStringExtra(MyApplication.CMD_MSG);
                String sn = intent.getStringExtra(MyApplication.CMD_SN);
                if (result) {
                    PageLayoutDatabase database = new PageLayoutDatabase();
                    pageLayoutModels = database.query_all();
                    if (pageLayoutModels != null && pageLayoutModels.size() > 0
                            && !TextUtils.isEmpty(pageLayoutModels.get(0).resPath)) {
                        application.showToast("正在加载动态布局");
                        application.pageLayoutTotal = pageLayoutModels.size();
                        application.systemManager.downloadPolicy(pageLayoutModels.get(0).resPath);
                        Intent intent1 = new Intent(MyApplication.BROADCAST);
                        intent1.putExtra(MyApplication.BROADCAST_TAG, "permission_finish");
                        broadcastManager.sendBroadcast(intent1);
                    } else {
                        application.httpProcess.commitBack(sn, HttpProcess.NONE, HttpProcess.NONE_MSG);
                    }
                } else {
                    application.httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
                }
            } else if (tag.equals(HttpProcess.QRY_PRIZE) && !application.isUpdateEQ && !application.isGrxx) {
                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                if (result) {
                    handler.sendEmptyMessageDelayed(11, 10 * 1000);
                }
            } else if (tag.equals(HttpProcess.QRY_SCHOOL) && !application.isUpdateEQ) {
                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                if (result) {
                    if (displayManager != null) {
                        displayManager.updata(AppCodeView.APPCODE_UPDATA_SCHOOL);
                    }
                }
            } else if (tag.equals(HttpProcess.QRY_QUEST) && !application.isUpdateEQ) {
                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                if (result) {
                    if (displayManager != null) {
                        displayManager.updata(AppCodeView.APPCODE_UPDATA_QUEST);
                    }
                }
            } else if (tag.equals(HttpProcess.QRY_BLACKBOARD) && !application.isUpdateEQ) {
                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                if (result) {
                    if (displayManager != null) {
                        displayManager.updata(AppCodeView.APPCODE_UPDATA_ENGLISH);
                    } else {
                        updata_classmotto();
                    }
                }
            } else if (tag.equals(HttpProcess.QRY_ONDUTY)) {
                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                if (result) {
                    if (displayManager != null) {
                        displayManager.updata(AppCodeView.APPCODE_UPDATA_ZHIRI);
                    }
                }
            } else if (tag.equals(HttpProcess.QRY_BOOK)) {
                String msg = intent.getStringExtra(MyApplication.CMD_MSG);
                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                if (result) {
                    AppLog.d(TAG, tag + " " + msg);
                    if (displayManager != null) {
                        displayManager.updata(AppCodeView.APPCODE_UPDATA_BOOK);
                    } else {
                        updata_book();
                    }
                } else {
                    Log.e(TAG, tag + " " + msg);
                }
            } else if (tag.equals("open_book")) {
                try {
                    application.openApp(CXMainActivity.this, MyApplication.READ);
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent i = new Intent(CXMainActivity.this, WebviewActivity.class);
                    startActivity(i);
                }
            } else if (tag.equals("logo_update")) {
                if (displayManager == null) {
                    update_logo();
                }
            } else if (tag.equals("QRY_PERSON_CHAOXING")
                    && application.getRunningActivityName().equals("com.cx.doorplate.activity.CXMainActivity")) {
                String mCardNum = intent.getStringExtra("mCardNum");
                if (personnelAttendanceModelList != null && personnelAttendanceModelList.size() > 0) {
                    for (int i = 0; i < personnelAttendanceModelList.size(); i++) {
                        if (personnelAttendanceModelList.get(i).cardid.equals("mCardNum")) {
                            Log.e("打卡", "打卡：" + personnelAttendanceModelList.get(i).xm);
                        }
                    }
                }

                if (progressDialog == null) {
                    progressDialog = ProgressDialog.show(CXMainActivity.this, null, "加载中", false, false);
                }
                application.httpProcess.QryPerson(application.equInfoModel.jssysdm, mCardNum);
            } else if (!goAttend && tag.equals(HttpProcess.QRY_PERSONATTEND) && application.getRunningActivityName().equals("com.cx.doorplate.activity.CXMainActivity")) {
                Log.e("查询", "首页查询yuo");
                goAttend = true;
                if (personnelAttendanceModelList != null) {
                    personnelAttendanceModelList.clear();
                }
                String personnelAttendanceList = intent.getStringExtra("personnelAttendanceList");
                personnelAttendanceModelList = JSONArray.parseArray(personnelAttendanceList, PersonnelAttendanceModel.class);


                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                String msg = intent.getStringExtra(MyApplication.CMD_MSG);
                if (!result) {
                    application.showToast("查询考勤失败，原因 : " + msg);
                }
                update_attend();
            } else if (tag.equals(HttpProcess.QRY_PERSON) && "QRY_PERSON_CHAOXING".equals(application.cardType)
                    && application.getRunningActivityName().equals("com.cx.doorplate.activity.CXMainActivity")) {
                boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
                String msg = intent.getStringExtra(MyApplication.CMD_MSG);
                String chaoxingUser = null, fid = null;
                if (result) {
                    chaoxingUser = intent.getStringExtra("chaoxingUser");
                    fid = intent.getStringExtra("fid");
                }
                if (!TextUtils.isEmpty(chaoxingUser) && !TextUtils.isEmpty(fid)) {
                    application.launchStudy(fid, chaoxingUser);
                    handler.sendEmptyMessageDelayed(0, 1000 * 3);
                    if (application.dialog != null && application.dialog.isShowing()) {
                        application.dialog.dismiss();
                    }
                } else {
                    Log.e(TAG, tag + " " + msg);
                    application.showToast("查询人员失败，原因 : " + msg);
                }
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        }
    }

    private void jjtz() throws ParseException {
        if (notice_jjtz != null) {
            long now = System.currentTimeMillis();
            long end = Long.parseLong(notice_jjtz.endTime);
            if (now >= end) {
                notice_jjtz = null;
                yyjz();
                // if (displayManager != null) {
                // displayManager.updata(AppCodeView.APPCODE_UPDATA_JJTZ);
                // } else {
                // ly_xxtz_title.setVisibility(View.VISIBLE);
                // ly_emergency_notice.setVisibility(View.INVISIBLE);
                // }
            }
        } else {
            yyjz();
        }
    }

    private void yyjz() throws ParseException {
        if (notice_yyjz != null) {
            long now = System.currentTimeMillis();
            long end = Long.parseLong(notice_yyjz.endTime);
            if (now >= end) {
                notice_yyjz = null;
                if (displayManager != null) {
                    displayManager.updata(AppCodeView.APPCODE_UPDATA_JJTZ);
                } else {
                    ly_xxtz_title.setVisibility(View.VISIBLE);
                    ly_emergency_notice.setVisibility(View.INVISIBLE);
                }
            } else {
                ly_xxtz_title.setVisibility(View.INVISIBLE);
                // ly_main_new2.setVisibility(View.INVISIBLE);
                ly_emergency_notice.setVisibility(View.VISIBLE);
                ly_emergency_notice.setBackgroundResource(R.drawable.cx_ly_yyjz_notice);

                txt_main_new_title3.setText(notice_yyjz.xxzt);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                txt_main_new_time3.setText(format.format(Long.parseLong(notice_yyjz.fbsj)));
                txt_main_new_nr3.setText("");
                if (!TextUtils.isEmpty(notice_yyjz.xxnr)) {
                    Document document = Jsoup.parse(notice_yyjz.xxnr);
                    Elements p = document.select("p");
                    if (p != null && p.size() > 0) {
                        String new_rl_1 = "";
                        for (Element e : p) {
                            if (TextUtils.isEmpty(new_rl_1)) {
                                new_rl_1 = e.text();
                            } else {
                                new_rl_1 = new_rl_1 + "\n" + e.text();
                            }
                            if (!TextUtils.isEmpty(new_rl_1) && new_rl_1.length() > 100) {
                                break;
                            }
                        }
                        txt_main_new_nr3.setText(new_rl_1);
                    }
                }
            }
        } else {
            if (displayManager != null) {
                displayManager.updata(AppCodeView.APPCODE_UPDATA_JJTZ);
            } else {
                ly_xxtz_title.setVisibility(View.VISIBLE);
                ly_emergency_notice.setVisibility(View.INVISIBLE);
            }
        }
    }

    // 未打卡处理
    private void Absenteeism(String HHmmss) throws ParseException {
        if (application.clockRule_teather == null || application.clockRule_student == null) {
            return;
        }
        PersonnelAttendanceDatabase personnelAttendanceDatabase = new PersonnelAttendanceDatabase();
        List<PersonnelAttendanceModel> list = personnelAttendanceDatabase.query("date = ?",
                new String[]{MyApplication.getTime("yyyy-MM-dd")});
        if (list == null) {
            return;
        }
        List<PersonnelAttendanceModel> updataList = new ArrayList<PersonnelAttendanceModel>();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        long now = format.parse(HHmmss).getTime();
        for (PersonnelAttendanceModel model : list) {
            if (TextUtils.isEmpty(model.status)) {
                continue;
            }
            if (model.isToWorkClock.equals("0") || !model.status.split(",")[0].equals("11")) {
                continue;
            }
            ClockRuleModel ruleModel;
            if (model.js.equals("1")) {
                ruleModel = application.clockRule_teather;
            } else {
                ruleModel = application.clockRule_student;
            }
            long toWorkTime = format.parse(model.toWorkTime).getTime();
            long cgcd = toWorkTime + Integer.parseInt(ruleModel.cgcd) * 60 * 1000;
            if (now > cgcd) {
                model.status = "4,4";
                personnelAttendanceDatabase.update(model);
                updataList.add(model);
            }
        }
        if (updataList.size() > 0) {
            application.httpProcess.commitPersonnelAttendance(updataList);
            attendStudent(updataList);
        }
    }

    private void attendStudent(List<PersonnelAttendanceModel> attendanceModels) throws ParseException {
        AttendInfoDatabase attendInfoDatabase = new AttendInfoDatabase();
        CurriculumInfoDatabase curriculumInfoDatabase = new CurriculumInfoDatabase();
        SectionInfoDatabase sectionInfoDatabase = new SectionInfoDatabase();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        for (PersonnelAttendanceModel personnelAttendanceModel : attendanceModels) {
            boolean isUpdate = false;
            if (personnelAttendanceModel == null) {
                continue;
            }
            List<AttendInfoModel> attendInfoModels = attendInfoDatabase.query_by_skrq(personnelAttendanceModel.date);
            if (attendInfoModels == null) {
                continue;
            }
            if (TextUtils.isEmpty(personnelAttendanceModel.status)) {
                continue;
            }
            long toWorkTime = format.parse(personnelAttendanceModel.toWorkTime).getTime();
            long offWorkTime = format.parse(personnelAttendanceModel.offWorkTime).getTime();
            for (AttendInfoModel attendInfoModel : attendInfoModels) {
                if (!attendInfoModel.kqzt.equals("11")) {
                    continue;
                }
                CurriculumInfoModel curriculumInfoModel = curriculumInfoDatabase.query_by_id(attendInfoModel.skjhid);
                if (curriculumInfoModel == null) {
                    continue;
                }
                String[] jcs = curriculumInfoModel.jc.split("-");
                SectionInfoModel section1 = null, section2 = null;
                if (jcs.length > 1) {
                    section1 = sectionInfoDatabase.query_by_jcdm(jcs[0]);
                    section2 = sectionInfoDatabase.query_by_jcdm(jcs[jcs.length - 1]);
                } else {
                    section1 = sectionInfoDatabase.query_by_jcdm(jcs[0]);
                    section2 = section1;
                }
                if (section1 == null || section2 == null) {
                    continue;
                }
                if ((toWorkTime <= Long.parseLong(section1.jcskkssj)
                        && Long.parseLong(section2.jcskjssj) <= offWorkTime)
                        || (toWorkTime <= Long.parseLong(section2.jcskjssj)
                        && Long.parseLong(section2.jcskjssj) <= offWorkTime)
                        || (toWorkTime <= Long.parseLong(section1.jcskkssj)
                        && Long.parseLong(section1.jcskkssj) <= offWorkTime)) {
                    isUpdate = true;
                    attendInfoModel.kqzt = "4";
                    attendInfoDatabase.update(attendInfoModel.id, attendInfoModel);
                    Log.e(TAG, attendInfoModel.toString());
                    application.httpProcess.CommitAttend(attendInfoModel.xsxh, attendInfoModel);
                }
            }
            if (isUpdate) {
                Intent intent = new Intent(MyApplication.BROADCAST);
                intent.putExtra(MyApplication.BROADCAST_TAG, "absenteeism_ui");
                intent.putExtra("attend_id", attendInfoModels.get(0).id);
                intent.putExtra("attend_kqzt", attendInfoModels.get(0).kqzt);
                broadcastManager.sendBroadcast(intent);
            }
        }
    }

    private void playTask() {
        if (application.getRunningActivityName().equals("com.yy.doorplate.activity.CallActivity")) {
            return;
        }
        if (application.isDownloadPlay || application.isGuangbo) {
            return;
        }
        if (application.playTaskModels == null) {
            return;
        }
        if (application.playTaskModels.size() == 0) {
            return;
        }
        if (application.playTask_position != -1 && application.playTaskModels.size() > application.playTask_position
                && application.playTaskModels.get(application.playTask_position).playType.equals("MEDIA")
                && application.playTaskModels.get(application.playTask_position).playEndTime
                .equals(MyApplication.getTime("HH:mm:ss"))) {
            if (application.getRunningActivityName().equals("com.advertisement.activity.DisplayActivity")) {
                Intent intent = new Intent(MyApplication.BROADCAST);
                intent.putExtra(MyApplication.BROADCAST_TAG, "PlayActivity_finish");
                broadcastManager.sendBroadcast(intent);
            }
            application.playTask_position = -1;
        }
        for (int i = 0; i < application.playTaskModels.size(); i++) {
            try {
                PlayTaskModel model = application.playTaskModels.get(i);
                if (model.playType.equals("RADIO") && model.taskType.equals("TIMING")) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long startData = formatter.parse(model.playStartDate + " " + model.playStartTime).getTime();
                    long endData = formatter.parse(model.playEndDate + " " + model.playEndTime).getTime();
                    long nowData = System.currentTimeMillis();
                    if (startData > nowData || nowData >= endData) {
                        return;
                    }
                    formatter = new SimpleDateFormat("HH:mm:ss");
                    String HHmmss = formatter.format(System.currentTimeMillis());
                    Date time = formatter.parse(HHmmss);
                    Date start = formatter.parse(model.playStartTime);
                    Date end = formatter.parse(model.playEndTime);
                    if (start.getTime() <= time.getTime() && time.getTime() < end.getTime()) {
                        AppLog.d(TAG, "---guangbo---" + i);
                        playGuangbo(i);
                        break;
                    }
                } else if (model.playType.equals("MEDIA") && !application.isGuangbo && !application.isGuanggao
                        && application.playTask_position != i) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long startData = formatter.parse(model.playStartDate + " " + model.playStartTime).getTime();
                    long endData = formatter.parse(model.playEndDate + " " + model.playEndTime).getTime();
                    long nowData = System.currentTimeMillis();
                    if (startData > nowData || nowData >= endData) {
                        return;
                    }

                    formatter = new SimpleDateFormat("HH:mm:ss");
                    String HHmmss = formatter.format(System.currentTimeMillis());
                    Date time = formatter.parse(HHmmss);
                    Date start = formatter.parse(model.playStartTime);
                    Date end = formatter.parse(model.playEndTime);
                    if (start.getTime() <= time.getTime() && time.getTime() < end.getTime()) {
                        AppLog.d(TAG, "---guanggao---" + i);
                        application.playTask_position = i;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void playGuangbo(final int i) {
        final PlayTaskModel model = application.playTaskModels.get(i);
        if (model.srcType.equals("AUDIO")) {
            if (application.getRunningActivityName().equals("com.advertisement.activity.DisplayActivity")) {
                Intent intent = new Intent(MyApplication.BROADCAST);
                intent.putExtra(MyApplication.BROADCAST_TAG, "PlayActivity_finish");
                broadcastManager.sendBroadcast(intent);
            }
            application.isGuanggao = false;
            application.isGuangbo = true;
            application.playTask_position = i;
            String[] s = model.srcPath.split("/");
            final String path = MyApplication.PATH_ROOT + MyApplication.PATH_PLAYTASK + "/" + s[s.length - 1];
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    try {
                        application.mediaPlayer.stop();
                        application.mediaPlayer.reset();
                        application.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        application.mediaPlayer.setDataSource(path);
                        application.mediaPlayer.setDisplay(null);
                        application.mediaPlayer.setLooping(false);
                        application.mediaPlayer.setOnErrorListener(new OnErrorListener() {

                            @Override
                            public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                                application.isGuangbo = false;
                                if (application.playTask_position != -1
                                        && application.playTaskModels.size() > application.playTask_position) {
                                    PlayTaskDatabase database = new PlayTaskDatabase();
                                    database.delete(
                                            application.playTaskModels.get(application.playTask_position).taskId);
                                    application.playTaskModels.remove(application.playTask_position);
                                    application.playTask_position = -1;
                                }
                                return false;
                            }
                        });
                        application.mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                AppLog.d(TAG, "downLoad Play start");
                                mediaPlayer.start();
                                application.showGuangbo(model.taskDesc, false);
                            }
                        });
                        application.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                application.isGuangbo = false;
                                if (application.playTask_position != -1
                                        && application.playTaskModels.size() > application.playTask_position) {
                                    PlayTaskDatabase database = new PlayTaskDatabase();
                                    database.delete(
                                            application.playTaskModels.get(application.playTask_position).taskId);
                                    application.playTaskModels.remove(application.playTask_position);
                                    application.playTask_position = -1;
                                }
                                if (application.dialog != null && application.dialog.isShowing()) {
                                    application.dialog.dismiss();
                                }
                            }
                        });
                        application.mediaPlayer.prepareAsync();
                    } catch (Exception e) {
                        e.printStackTrace();
                        application.isGuangbo = false;
                        if (application.playTask_position != -1
                                && application.playTaskModels.size() > application.playTask_position) {
                            PlayTaskDatabase database = new PlayTaskDatabase();
                            database.delete(application.playTaskModels.get(application.playTask_position).taskId);
                            application.playTaskModels.remove(application.playTask_position);
                            application.playTask_position = -1;
                        }
                    }
                }
            }, 2000);
        }
    }

    private void timingOFFON() {
        if (TextUtils.isEmpty(application.time_off) || TextUtils.isEmpty(application.time_on)
                || TextUtils.isEmpty(application.time_off_actual)) {
            return;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long off = format.parse(application.time_off_actual).getTime();
            long on = format.parse(application.time_on).getTime();
            if (off <= System.currentTimeMillis()
                    && System.currentTimeMillis() < (on - 1000 * 60 * MyApplication.OFFON)) {
                application.dogProcess.disenbleLight();
                application.dogProcess.stopDog();
                application.dogProcess.close();
                // Date time_on = LocalToGTM(application.time_on);
                // application.mEYunSdk.setRebootClock(time_on.getDate(),
                // time_on.getHours(), time_on.getMinutes());
                // Intent intent = new Intent();
                // intent.setAction("system_to_reboot");
                // sendBroadcast(intent);
            } else if (off > System.currentTimeMillis() && !application.time_off_actual.equals(application.time_off)) {
                int i = (int) ((off - System.currentTimeMillis()) / 1000);
                if (i == 30) {
                    handler.sendEmptyMessage(12);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateImg() {
        File file = new File(MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    if (f.getName().startsWith("attend")) {
                        f.delete();
                    }
                }
            }
        }
        AttendInfoDatabase attendInfoDatabase = new AttendInfoDatabase();
        List<AttendInfoModel> attendInfoModels = attendInfoDatabase.query_all();
        if (attendInfoModels != null && attendInfoModels.size() > 0) {
            for (AttendInfoModel model : attendInfoModels) {
                if (!TextUtils.isEmpty(model.zp)) {
                    String[] s = model.zp.split("\\.");
                    application.downLoadImg(model.zp, MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE + "/attend_"
                            + model.id + "." + s[s.length - 1]);
                }
            }
        }
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
                            handler.sendEmptyMessage(4);
                        } else {
                            if (new_pics.size() > new_pics_i) {
                                new_pics.remove(new_pics_i);
                                new_pics_i = 0;
                                handler.sendEmptyMessage(4);
                            }
                        }
                    } else {
                        if (new_pics.size() > new_pics_i) {
                            new_pics.remove(new_pics_i);
                            new_pics_i = 0;
                            handler.sendEmptyMessage(4);
                        }
                    }
                } else {
                    if (application.httpDownLoad(urlStr, path)) {
                        handler.sendEmptyMessage(4);
                    } else {
                        if (new_pics.size() > new_pics_i) {
                            new_pics.remove(new_pics_i);
                            new_pics_i = 0;
                            handler.sendEmptyMessage(4);
                        }
                    }
                }
            }
        });
    }

    private boolean equModelTask() {
        if (application.getRunningActivityName().equals("com.yy.doorplate.activity.ExamActivity")) {
            return false;
        }
        EquModelTaskDatabase database = new EquModelTaskDatabase();
        List<EquModelTaskModel> taskModels = database.query_all();
        if (taskModels == null) {
            return false;
        }
        for (EquModelTaskModel model : taskModels) {
            if (!model.equModel.equals("01")) {
                continue;
            }
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startTime = formatter.parse(model.startTime);
                Date stopTime = formatter.parse(model.stopTime);
                long time = System.currentTimeMillis();
                if (time >= startTime.getTime() && time < stopTime.getTime()) {
                    Intent broad = new Intent(MyApplication.BROADCAST);
                    broad.putExtra(MyApplication.BROADCAST_TAG, "PlayActivity_finish");
                    broadcastManager.sendBroadcast(broad);

                    Intent intent = new Intent(this, ExamActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("EquModelTaskModel", model);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
