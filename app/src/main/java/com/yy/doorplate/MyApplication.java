package com.yy.doorplate;

import com.common.AppLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.net.io.Util;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;
import sun.misc.BASE64Encoder;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.ect.alarm.AlarmPowerItem;
import android.hardware.ect.alarm.AlarmPowerManager;
import android.hardware.ect.gpio.IgpioStateListener;
import android.hardware.ect.gpio.gpioctlManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.advertisement.activity.DisplayActivity;
import com.advertisement.system.ConstData;
import com.advertisement.system.SystemManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.camera.CameraManager;
import com.camera.CameraManager.CameraManagerListener;
import com.common.LogcatHelper;
import com.eyun.upgrade.ApkPatchJni;
import com.eyunsdk.core.EYunSDKCore;
import com.eyunsdk.device.DeviceManager.TX260ReadListener;
import com.github.mikephil.charting.utils.Utils;
import com.network.NetworkManager;
import com.network.NetworkManager.NetworkStateListener;
import com.network.NetworkState;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.bugly.crashreport.CrashReport;
import com.yy.doorplate.activity.CallActivity;
import com.yy.doorplate.activity.ControlActivity;
import com.yy.doorplate.activity.PlayActivity;
import com.yy.doorplate.activity.VideoCallOtherActivity;
import com.yy.doorplate.communication.DogProcess;
import com.yy.doorplate.communication.FTPManager;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.communication.SmartUtils;
import com.yy.doorplate.communication.TcpProcess;
import com.yy.doorplate.communication.TcpProcess.TcpCallback;
import com.yy.doorplate.database.AttendInfoDatabase;
import com.yy.doorplate.database.CarouselDatabase;
import com.yy.doorplate.database.ClassInfoDatabase;
import com.yy.doorplate.database.ClassMottoInfoDatabase;
import com.yy.doorplate.database.ClassRoomInfoDatabase;
import com.yy.doorplate.database.ClockInfoDatabase;
import com.yy.doorplate.database.ClockRuleDatabase;
import com.yy.doorplate.database.CurriculumInfoDatabase;
import com.yy.doorplate.database.DeviceDatabase;
import com.yy.doorplate.database.DiyMenuDatabase;
import com.yy.doorplate.database.EquInfoDatabase;
import com.yy.doorplate.database.EquListDatabase;
import com.yy.doorplate.database.MenuAndEqDatabase;
import com.yy.doorplate.database.PersonInfoDatabase;
import com.yy.doorplate.database.PersonnelAttendanceDatabase;
import com.yy.doorplate.database.PicDreOrActivityDatabase;
import com.yy.doorplate.database.PlayTaskDatabase;
import com.yy.doorplate.database.PrizeInfoDatabase;
import com.yy.doorplate.database.QuestionNaireDatabase;
import com.yy.doorplate.database.SceneDatabase;
import com.yy.doorplate.database.ScreensaverDatabase;
import com.yy.doorplate.database.SectionInfoDatabase;
import com.yy.doorplate.database.StarDatabase;
import com.yy.doorplate.database.StudentInfoDatabase;
import com.yy.doorplate.database.TeacherInfoDatabase;
import com.yy.doorplate.database.TelMenuDatabase;
import com.yy.doorplate.database.TimeOFFDatabase;
import com.yy.doorplate.database.TimeONDatabase;
import com.yy.doorplate.database.UserInfoDatabase;
import com.yy.doorplate.database.VideoDatabase;
import com.yy.doorplate.database.VoteThemeDatabase;
import com.yy.doorplate.model.ArtifactVerType;
import com.yy.doorplate.model.AttendInfoModel;
import com.yy.doorplate.model.CarouselModel;
import com.yy.doorplate.model.ClassInfoModel;
import com.yy.doorplate.model.ClassMottoInfoModel;
import com.yy.doorplate.model.ClassRoomInfoModel;
import com.yy.doorplate.model.ClockInfoModel;
import com.yy.doorplate.model.ClockRuleModel;
import com.yy.doorplate.model.ControlModel;
import com.yy.doorplate.model.CurriculumInfoModel;
import com.yy.doorplate.model.DeviceModel;
import com.yy.doorplate.model.DiyMenuModel;
import com.yy.doorplate.model.EquInfoModel;
import com.yy.doorplate.model.MenuAndEqModel;
import com.yy.doorplate.model.PersonInfoModel;
import com.yy.doorplate.model.PersonnelAttendanceModel;
import com.yy.doorplate.model.PicDreOrActivityModel;
import com.yy.doorplate.model.PlayTaskModel;
import com.yy.doorplate.model.PrizeInfoModel;
import com.yy.doorplate.model.QuestionNaireModel;
import com.yy.doorplate.model.SceneModel;
import com.yy.doorplate.model.ScreensaverModel;
import com.yy.doorplate.model.SectionInfoModel;
import com.yy.doorplate.model.StarModel;
import com.yy.doorplate.model.StudentInfoModel;
import com.yy.doorplate.model.TeacherInfoModel;
import com.yy.doorplate.model.TelMenuInfoModel;
import com.yy.doorplate.model.TimerTypeModel;
import com.yy.doorplate.model.VideoInfoModel;
import com.yy.doorplate.model.VoteThemeModel;
import com.yy.doorplate.protocol.SmartProtocol;
import com.yy.doorplate.protocol.TcpProtocol;
import com.yy.doorplate.serialport_util.LockerPortInterface;
import com.yy.doorplate.serialport_util.MyFunc;
import com.yy.doorplate.serialport_util.PortPrinterBase;
import com.yy.doorplate.tool.CrashHandler;
import com.yy.doorplate.tool.General;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;
import com.yy.doorplate.view.CustomTextView;
import com.yy.doorplate.view.FloatView;
import com.yy.face.FaceProcess;

import com.android.xhapimanager.XHApiManager;


public class MyApplication extends Application implements NetworkStateListener {

	// --------------------------公有静态宏定义-----------------------------

	// 广播
	public final static String BROADCAST = "COM.YY.DOORPLATE.BROADCAST";
	public final static String BROADCAST_TAG = "CMD";
	public final static String CMD_RESULT = "result";
	public final static String CMD_MSG = "msg";
	public final static String CMD_SN = "sn";

	public final static String NOTICE_PAGE_COUNT = "10";
	//public final static String DEFAULT_SERVER_IP = "http://wht.chaoxing.com/web-unified";
	public final static String DEFAULT_SERVER_IP = "http://106.13.93.137:8080/web-unified";

	public static final String DATABASE = "/data/data/com.yy.doorplate/doorplate.db";
	public static final String PATH_ROOT = "/mnt/sdcard/Doorplate";
	// 广播广告相关媒体文件
	public static final String PATH_PLAYTASK = "/PlayTask";
	// 远程升级应用
	public static final String PATH_APP = "/APP";
	// 考勤，学生，教师等图片
	public static final String PATH_PICTURE = "/Picture";
	// 考勤时的拍照图片
	public static final String PATH_ATTEND = "/Attend";
	// 通知相关图片,附件
	public static final String PATH_NOTICE = "/Notice";
	// 截屏图片
	public static final String PATH_SCREENCAP = "/Screen";
	// 小视频
	public static final String PATH_VIDEO = "/Video";
	// 推荐图书封面
	public static final String PATH_BOOK = "/Book";
	// 相册
	public static final String PATH_PHOTO = "/Photo";
	// Logo
	public static final String PATH_LOGO = "/Logo";
	// 屏保图片
	public static final String PATH_SCREENSAVER = "/Screensaver";
	// 视讯相关目录
	public static final String PATH_ADV = "/mnt/sdcard/Adv";

	// 屏保模式
	public final static int SCREENSAVER_MODE_NONE = 0;
	public final static int SCREENSAVER_MODE_BLACK = 1;
	public final static int SCREENSAVER_MODE_TIME = 2;
	public final static int SCREENSAVER_MODE_AD = 3;

	// 第三方阅读应用包名
	public static final String READ = "com.touchmachine.horizontal";
	// com.touchmachine.horizontal 横屏
	// com.touch.machine.changsha 竖屏

	// 超星学习通包名
	public static final String CHAOXING = "com.chaoxing.pad";

	// WPS包名
	public static final String WPS = "cn.wps.moffice_eng";

	// 定时开关机，临近开机时间5分钟时不做处理
	public static final int OFFON = 5;

	// 下载图片的超时时间
	public static final int DOWNLOAD_IMG_TIMEOUT = 3000;

	// 喂狗时间
	public static final int DOG_TIMEOUT = 30;


	public static final int RUNNING_TARGET_PHONE = 0;
	public static final int RUNNING_TARGET_PAD = 1;
	public static final int RUNNING_TARGET_PHONE_TEST = 2;

	public static final int RUNNING_TARGET = RUNNING_TARGET_PHONE;

	// --------------------------公有全局变量-----------------------------

	public Timer timer_init = null;

	public Toast toast = null;

	public HttpProcess httpProcess = null;

	public TcpProcess tcpProcess = null;

	public SmartUtils smartUtils = null;

	public EYunSDKCore mEYunSdk = null;

	public FaceProcess faceProcess = null;

	// public DeviceManager mDeviceManager = null;

	public NetworkManager mNetworkManager = null;

	public CameraManager cameraManager = null;

	public DogProcess dogProcess = new DogProcess();
	public Thread thread_dog = null;
	public boolean is_thread_dog = false;

	// 同步完成的标志
	public boolean isSynComplete = false;
	// 数据库是否出现异常
	public boolean isSqlError = false;

	// 设备信息
	public EquInfoModel equInfoModel = null;
	// 班级信息
	public ClassInfoModel classInfoModel = null;
	// 教室信息
	public ClassRoomInfoModel classRoomInfoModel = null;
	// 班训信息
	public ClassMottoInfoModel classMottoInfoModel = null;
	// 当天课表
	public List<CurriculumInfoModel> curriculumInfos_today = null;
	// 上节课程
	public CurriculumInfoModel curriculumInfoModel_last = null;
	// 当前课程
	public CurriculumInfoModel curriculumInfoModel_now = null;
	// 下节课程
	public CurriculumInfoModel curriculumInfoModel_next = null;

	public List<PlayTaskModel> playTaskModels = new ArrayList<PlayTaskModel>();

	public List<TimerTypeModel> list_off, list_on;
	public String time_off, time_on, time_off_actual;

	public ExecutorService executoService = null;

	// 时间相关信息
	public String dqxn, dqxq, kxsj, jssj, zs, today, server_ip;

	public int notice_total = 0, notice_total_bpxxtz = 0, notice_total_bpbjtz = 0, notice_total_bpxxxw = 0,
			notice_total_bpbjxw = 0, vote_total = 0, activity_total = 0;

	public AlertDialog dialog = null;

	// 认证操作的类型
	public String operateType;
	// 刷卡认证类型:
	// QRY_PERSON_CLASS 提交课后反馈查询人员
	// QRY_PERSON_FACE 注册人脸查询人员
	// QRY_PERSON_GRXX 查询个人信息
	// QRY_PERSON_VOTE 提交活动信息查询人员
	// QRY_PERSON_CHAOXING 查询人员学习通账号
	// QRY_PERSON_BORROW 借还书查询人员
	public String cardType;

	// 标记是否在下载更新中
	public boolean isDownloadAPP = false;
	// 标记是否在下载播放任务中
	public boolean isDownloadPlay = false;
	// 标记是否在更新通知中
	public boolean isUpdateNotice = false;
	// 标记是否在更新课程中
	public boolean isUpdateCurriculum = false;
	// 标记是否在更新设备中
	public boolean isUpdateEQ = false;
	// 标记是否在广播播放中
	public boolean isGuangbo = false;
	// 标记是否在广告播放中
	public boolean isGuanggao = false;
	// 标记是否在通知列表下拉加载更多中
	public boolean isUpdataNoticeList = false;
	// 标记是否在考勤界面因为没有该条考勤记录，临时查询考勤记录
	public boolean isQryAttend = false;
	// 标记是否平台主动下发考勤记录
	public boolean isQryAttendTCP = false;
	// 标记是否在拍照中
	public boolean isTakePicture = false;
	// 标记是否在查询个人信息
	public boolean isGrxx = false;

	public int playTask_position = -1;

	// 标记当前状态
	public int networdState = NetworkState.STATE_NETWORK_DISABLED;

	public EquInfoModel call_eq = null;

	public MediaPlayer mediaPlayer = null, mediaPlayer_region = null;

	public boolean regionIsPlaying = false;

	public String call_from_who;

	public AudioManager mAudioManager = null;
	public boolean isSilent = true;
	public int volume = 0;

	// 屏保相关
	public int screensaver_time = 60;
	public int screensaver_mode = SCREENSAVER_MODE_AD;

	public int pageLayoutTotal = 0, pageLayoutCount = 0;
	public String pageSn;

	public SystemManager systemManager;

	public static XHApiManager apimanager=null;

	// 天气信息
	public String currentCity = "", weather = "", temperature = "";

	// 根资源地址
	public String rootResPath = null;

	// 超时15分钟旷课处理
	public int absenteeism_timeout = 15;

	public WindowManager windowManager = null;
	public FloatView floatView = null;

	// 考勤规则
	public ClockRuleModel clockRule_teather, clockRule_student;

	// 是否启动自动亮度调节
	public boolean isAutoBrightness = true;
	// 监控板版本
	public int dogVersion;

	// Logo路径
	public String logoUrl;
	public String schoolName;


	public static boolean jjtzActiveAlive = false;
	public static boolean tktzActiveAlive = false;

	// --------------------------私有变量-----------------------------

	private static final String TAG = "MyApplication";

	public final static String APP_ID = "1301";
	public final static String APP_NAME = "com.yy.doorplate";
	public final static String APP_TOOL_ID = "1302";
	public final static String APP_TOOL_NAME = "com.toolservice";
	public final static String APP_READ_ID = "3304";
	public final static String APP_READ_NAME = "com.touchmachine.horizontal";

	private static Context mContext = null;

	private SharedPreferences mPreferences;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private AlertDialog dialog_hint = null, dialog_attend = null;
	private Timer timer_dialog = null;
	private int time_dialog = 5;
	private TextView txt_alert_hint;
	private TextView dialog_off_txt;
	private Button dialog_off_btn1, dialog_off_btn2;

	private ProgressDialog progressDialog = null;

	private int curriculumInfos_today_id = 0;

	private Handler handler_call = null;

	private String mCardNum = null;
	private Timer mTimerGetCard = null;

	private int downloadAPP_total = 0, downloadAPP_avail = 0;

	private List<VideoInfoModel> vedioInfoModels = null;
	private int vedioInfoModels_i = 0;
	private String vedioSN;

	private gpioctlManager mgpioctlManager;

	private String updateEQError = null;

	public Timer refreshTimer;
	public boolean refreshFlag = false;

	//串口
	public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
	private SerialPort mSerialPortLeft = null;
	private SerialPort mSerialPortRight = null;
	/**
	 * 串口名称
	 */
	private String PATH = "/dev/ttyS1";
	private String PATHRIGHT = "/dev/ttyS4";
	/**
	 * 波特率
	 */
	private int BAUDRATE = 19200;
	SerialControl ComA;
	SerialControl ComB;
	public static Context getmContext() {
		return mContext;
	}

	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();//获取跟目录
		}
		return sdDir.toString();
	}
	private class SerialControl extends SerialHelper {

		//		public SerialControl(String sPort, String sBaudRate){
//			super(sPort, sBaudRate);
//		}
		public SerialControl() {
		}

		@Override
		protected void onDataReceived(final ComBean ComRecData) {
			//数据解析转10进制补位
			String temp=MyFunc.ByteArrToHex(ComRecData.bRec).trim();
			if(temp.length()>13){
				String newcard=temp.substring(4,12);
				String date=newcard.substring(6,8)+newcard.substring(4,6)+newcard.substring(2,4)+newcard.substring(0,2);
				int ten=Integer.parseInt(date,16);
				String ten_date=String.valueOf(ten);

				if(ten_date.length()==9){
					ten_date="0"+ten_date;
				}else if(ten_date.length()==8){
					ten_date="00"+ten_date;
				}
				String card = ten_date;
				mCardNum = card;
				if (TextUtils.isEmpty(operateType) && TextUtils.isEmpty(cardType)) {
					Message msg = Message.obtain();
					msg.what = 0;
					msg.obj = mCardNum;
					handlerCommotAttend.sendMessage(msg);
				} else if (!TextUtils.isEmpty(operateType)) {
					if (classInfoModel != null && classRoomInfoModel != null
							&& !TextUtils.isEmpty(classRoomInfoModel.cdglykh)
							&& mCardNum.equals(classRoomInfoModel.cdglykh)) {
						Intent intent = new Intent(BROADCAST);
						intent.putExtra(BROADCAST_TAG, HttpProcess.OPERATE_AUTHORIZATION);
						intent.putExtra(CMD_RESULT, true);
						broadcastManager.sendBroadcast(intent);
						return;
					}
					UserInfoDatabase userInfoDatabase = new UserInfoDatabase();
					if (userInfoDatabase.query_by_cardNo(card) != null) {
						Intent intent = new Intent(BROADCAST);
						intent.putExtra(BROADCAST_TAG, HttpProcess.OPERATE_AUTHORIZATION);
						intent.putExtra(CMD_RESULT, true);
						broadcastManager.sendBroadcast(intent);
						return;
					}
					httpProcess.Permission(equInfoModel.jssysdm, operateType, "JOB_CARD", "", "", mCardNum);
					if (operateType.equals("VEDIO_CALL")) {
						call_from_who = mCardNum;
					}
					Intent intent = new Intent(MyApplication.BROADCAST);
					intent.putExtra(MyApplication.BROADCAST_TAG, "permission_ui");
					broadcastManager.sendBroadcast(intent);
				} else if (!TextUtils.isEmpty(cardType)) {
					Intent intent = new Intent(MyApplication.BROADCAST);
					intent.putExtra(MyApplication.BROADCAST_TAG, cardType);
					intent.putExtra("mCardNum", mCardNum);
					broadcastManager.sendBroadcast(intent);
				}
			}

		}
	}

	public static XHApiManager getApimanager() {
		return apimanager;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogcatHelper.getInstance(this).start();
		ComA = new SerialControl();
		ComB = new SerialControl();

		String fileName = "app-";
		fileName += AppLog.formatTime(System.currentTimeMillis());
		fileName += ".log";
		AppLog.LOG2FILE_ENABLE = true;
		AppLog.onCreate(fileName);

		AppLog.d(TAG, "-------MyApplication onCreate------");

		if (RUNNING_TARGET == RUNNING_TARGET_PAD) {
			apimanager = new XHApiManager();
		}

		mContext = getApplicationContext();
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this);

		CrashReport.initCrashReport(mContext, "24221dcf9d", false);
		CrashReport.setUserId(getSN());
		//String mac=getMacAddress();

		httpProcess = new HttpProcess(this);

		executoService = Executors.newFixedThreadPool(5);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BROADCAST);
		filter.addAction(ConstData.ACTIVITY_UPDATE);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		if (!serviceIsRunning(this, "com.hhd.dmbservice.main.myservice")) {
			Intent service = new Intent("com.hhd.dmbservice.main.myservice");
			startService(service);
		}


		if (RUNNING_TARGET == RUNNING_TARGET_PHONE) {
			systemManager = SystemManager.getSystemManager();
			systemManager.setContext(this);
			systemManager.setApplication(this);
			//lph20190328
			systemManager.start();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		dialog_hint = builder.create();
		dialog_hint.getWindow().setType(LayoutParams.TYPE_SYSTEM_ALERT);
		dialog_hint.setCanceledOnTouchOutside(false);
		dialog_hint.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (timer_dialog != null) {
					timer_dialog.cancel();
					timer_dialog = null;
				}
			}
		});
		dialog_attend = builder.create();
		dialog_attend.getWindow().setType(LayoutParams.TYPE_SYSTEM_ALERT);
		dialog_attend.setCanceledOnTouchOutside(false);
		dialog_attend.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (timer_dialog != null) {
					timer_dialog.cancel();
					timer_dialog = null;
				}
			}
		});

		initData();
	}

	// 开启开门狗
	public void openDog() {
		if (dogProcess.init() > 0) {
			dogProcess.startDog(DOG_TIMEOUT);
			is_thread_dog = true;
			thread_dog = new Thread(new Runnable() {

				@Override
				public void run() {
					while (is_thread_dog) {
						dogProcess.feedDog();
						try {
							Thread.sleep(DOG_TIMEOUT / 2 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			thread_dog.start();
			dogProcess.enbleLight(mContext);
		}
	}

	private void initData() {
		mPreferences = getSharedPreferences("com.yy.doorplate", Activity.MODE_PRIVATE);
		isSynComplete = mPreferences.getBoolean("isSynComplete", false);
		isAutoBrightness = mPreferences.getBoolean("isAutoBrightness", true);
		dqxn = mPreferences.getString("dqxn", null);
		dqxq = mPreferences.getString("dqxq", null);
		kxsj = mPreferences.getString("kxsj", null);
		jssj = mPreferences.getString("jssj", null);
		zs = mPreferences.getString("zs", null);
		today = mPreferences.getString("today", null);
		server_ip = mPreferences.getString("server_ip", DEFAULT_SERVER_IP);

		notice_total = mPreferences.getInt("notice_total", 0);
		notice_total_bpbjtz = mPreferences.getInt("notice_total_bpbjtz", 0);
		notice_total_bpbjxw = mPreferences.getInt("notice_total_bpbjxw", 0);
		notice_total_bpxxtz = mPreferences.getInt("notice_total_bpxxtz", 0);
		notice_total_bpxxxw = mPreferences.getInt("notice_total_bpxxxw", 0);
		vote_total = mPreferences.getInt("vote_total", 0);
		activity_total = mPreferences.getInt("activity_total", 0);
		screensaver_time = mPreferences.getInt("screensaver_time", 60);
		screensaver_mode = mPreferences.getInt("screensaver_mode", SCREENSAVER_MODE_AD);
		absenteeism_timeout = mPreferences.getInt("absenteeism_timeout", 15);
		rootResPath = mPreferences.getString("rootResPath", rootResPath);
		logoUrl = mPreferences.getString("logoUrl", null);

		EquInfoDatabase equInfoDatabase = new EquInfoDatabase();
		equInfoModel = equInfoDatabase.query();

		AudioManager mAm = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = mAm.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2;
		try {
			if(equInfoModel.equVolume!=null) {
				volume = Integer.parseInt(equInfoModel.equVolume) / 100;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (equInfoModel == null && isSynComplete) {
			recoveryEquInfo();
			isSqlError = true;
		}
		ClassRoomInfoDatabase classInfoDatabase = new ClassRoomInfoDatabase();
		classRoomInfoModel = classInfoDatabase.query();


		ClassMottoInfoDatabase classMottoDatabase = new ClassMottoInfoDatabase();
		classMottoInfoModel = classMottoDatabase.query();

		ClassInfoDatabase infoDatabase = new ClassInfoDatabase();
		classInfoModel = infoDatabase.query();

		CurriculumInfoDatabase curriculumInfoDatabase = new CurriculumInfoDatabase();
		curriculumInfos_today = curriculumInfoDatabase.query_by_skrq(MyApplication.getTime("yyyy-MM-dd"));
		curriculumInfoModel_now = findCurriculum(10);

		PlayTaskDatabase playTaskDatabase = new PlayTaskDatabase();
		playTaskModels = playTaskDatabase.query_all();

		TimeOFFDatabase offDatabase = new TimeOFFDatabase();
		list_off = offDatabase.query_all();
		TimeONDatabase onDatabase = new TimeONDatabase();
		list_on = onDatabase.query_all();
//		if(list_off.size()>0&&list_on.size()>0){
//			Log.e("开机时间","开机时间"+list_off.get(0).time+"     "+list_on.get(0).time);
////			apimanager.XHSetPowerOffOnTime(list_off.get(0).time);
//		}

		ClockRuleDatabase clockRuleDatabase = new ClockRuleDatabase();
		clockRule_teather = clockRuleDatabase.query_by_js("1");
		clockRule_student = clockRuleDatabase.query_by_js("2");

		try {
			getOFFONTime(System.currentTimeMillis(), getTime("yyyy-MM-dd"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		File file = new File(PATH_ROOT + PATH_APP);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(PATH_ROOT + PATH_ATTEND);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(PATH_ROOT + PATH_PICTURE);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(PATH_ROOT + PATH_PLAYTASK);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(PATH_ROOT + PATH_NOTICE);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(PATH_ROOT + PATH_SCREENCAP);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(PATH_ROOT + PATH_VIDEO);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(PATH_ROOT + PATH_BOOK);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(PATH_ROOT + PATH_PHOTO);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(PATH_ROOT + PATH_LOGO);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(PATH_ROOT + PATH_SCREENSAVER);
		if (!file.exists()) {
			file.mkdirs();
		}

		DeviceDatabase database = new DeviceDatabase();
		try {
			database.query_inspect();
		} catch (Exception e) {
			e.printStackTrace();
			List<DeviceModel> deviceModels = database.query_all_();
			database.drop();
			if (deviceModels != null) {
				for (DeviceModel model : deviceModels) {
					database.insert(model);
				}
			}
		}
		DeviceModel deviceModel = database.query_by_id(1);
		if (deviceModel == null) {
			deviceModel = new DeviceModel();
			deviceModel.name = "门禁";
			deviceModel.img = 9;
			database.insert(deviceModel);
		}

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		closeVoice();
	}

	public void initServer() {
//        readEvens();
		closeVoice();
		if (smartUtils == null) {
			smartUtils = new SmartUtils();
			smartUtils.init();
		}
		// if (faceProcess == null) {
		// faceProcess = new FaceProcess(this);
		// if (faceProcess.init() > 0) {
		// // 保存识别记录图片关闭、关闭活体检测、关闭实时识别
		// faceProcess.face_rec_set(0, 1, 0, 1, 1, 0, 0, 0, 0, 0);
		// // faceProcess.person_clear();
		// }
		// }
		if (tcpProcess == null && !TextUtils.isEmpty(equInfoModel.accSysIp)
				&& !TextUtils.isEmpty(equInfoModel.accSysPort)) {
			tcpProcess = new TcpProcess(tcpCallback);
			tcpProcess.init();
			tcpProcess.startTcp(equInfoModel.accSysIp, equInfoModel.accSysPort);
			//tcpProcess.startTcp("192.168.1.108", "10000");
		}
		if (mNetworkManager == null) {
			mNetworkManager = new NetworkManager(this, MyApplication.this, null, null);
			mNetworkManager.startServer(0);
		}


		// if (cameraManager == null) {
		// cameraManager = new CameraManager(this, cameraManagerListener);
		// }

		// if (!serviceIsRunning(this,
		// "com.yy.doorplate.communication.MonitorService") && TYPE == 0) {
		// Intent service = new Intent(
		// "com.yy.doorplate.communication.MonitorService");
		// startService(service);
		// }
		mediaPlayer = new MediaPlayer();
		mediaPlayer_region = new MediaPlayer();

		//lph20190406
//		mgpioctlManager = new gpioctlManager(this);
//		mgpioctlManager.SetMode(gpioctlManager.GPIO1, 0);
//		mgpioctlManager.registergpioListener(stub);

		// 初始化本地视频列表缩略图
		initImageLoader(this);
	}

	public void openUSBListener() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_CHECKING);
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addAction(Intent.ACTION_MEDIA_SHARED);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		filter.addDataScheme("file");
		registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction() == Intent.ACTION_MEDIA_MOUNTED) {
					String path = intent.getData().getPath();
					checkUSB(path);
				}
			}
		}, filter);
	}

	private void checkUSB(String path) {
//		if (!path.startsWith("/mnt/usb_storage")) {
//			return;
//		}
		if (Build.VERSION.SDK_INT >= 21 && path.equals("/mnt/usb_storage/USB_DISK2")) {
			path = "/mnt/usb_storage/USB_DISK2/udisk0";
		}
		File[] files = new File(path).listFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				if (file.getName().startsWith("Doorplate") && file.getName().endsWith(".apk")) {
					PackageManager pm = getPackageManager();
					PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
					if (info.applicationInfo.packageName.equals(APP_NAME)) {
						if (info.versionCode >= getVersionCode()) {
							showToast("U盘升级中，请勿操作");
							Intent intent = new Intent();
							intent.setAction("slient_install_dmb");
							intent.putExtra("dmb", file.getAbsolutePath());
							intent.putExtra("reboot_flag", false);
							intent.putExtra("packageName", APP_NAME);
							sendBroadcast(intent);
						}
					}
				}
			}
		}
	}

	private IgpioStateListener.Stub stub = new IgpioStateListener.Stub() {
		@Override
		public void gpioStateEvent(int gpionum, int status) {
			Log.d(TAG, "gpio" + gpionum + " = " + status);
			if (gpionum == 1 && status == 0) {
				oneButtonAlarm();
			}
		}
	};

	private void oneButtonAlarm() {
		if (getRunningActivityName().equals("com.yy.doorplate.activity.CallActivity")) {
			return;
		}
		call_from_who = null;
		TelMenuDatabase telMenuDatabase = new TelMenuDatabase();
		TelMenuInfoModel telMenuInfoModel = telMenuDatabase.query_by_menuType("ALARM");
		if (telMenuInfoModel != null) {
			MenuAndEqDatabase menuAndEqDatabase = new MenuAndEqDatabase();
			EquListDatabase equListDatabase = new EquListDatabase();
			List<MenuAndEqModel> menuAndEqModels = menuAndEqDatabase.query_by_tel(telMenuInfoModel.menuId);
			if (menuAndEqModels != null && menuAndEqModels.size() > 0) {
				if (menuAndEqModels.size() == 1) {
					EquInfoModel equInfoModel = equListDatabase.query_by_id(menuAndEqModels.get(0).equCode);
					if (equInfoModel != null && !TextUtils.isEmpty(equInfoModel.ip)) {
						call_eq = equInfoModel;
						Intent intent = new Intent(this, CallActivity.class);
						intent.putExtra("isCall", true);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					} else {
						showToast("设备IP地址异常，无法呼叫");
					}
				} else {
					Intent intent = new Intent(this, VideoCallOtherActivity.class);
					intent.putExtra("menuId", telMenuInfoModel.menuId);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
			} else {
				showToast("暂无" + telMenuInfoModel.menuName + "信息");
			}
		} else {
			showToast("暂无报警设备信息");
		}
	}

	private void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2).memoryCacheExtraOptions(480, 800)
				.denyCacheImageMultipleSizesInMemory().tasksProcessingOrder(QueueProcessingType.LIFO).threadPoolSize(3)
				// default
				.threadPriority(Thread.NORM_PRIORITY - 2)
				// default
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				// default
				.denyCacheImageMultipleSizesInMemory().memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(13)
				// default
				.diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100).writeDebugLogs().build();
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
	}

	public void showToast(String msg) {
		if (toast == null) {
			toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
			LinearLayout linearLayout = (LinearLayout) toast.getView();
			TextView textView = (TextView) linearLayout.getChildAt(0);
			textView.setTextSize(25);
		}
		toast.setText(msg);
		toast.show();
	}

	public void showDialog(String msg, int s) {


		if (dialog_attend.isShowing()) {
			dialog_attend.dismiss();
		}
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		if (!dialog_hint.isShowing()) {
			dialog_hint.show();
		}
		dialog_hint.getWindow().setLayout(800, 450);
		dialog_hint.getWindow().setContentView(R.layout.alertdialog_hint);
		txt_alert_hint = (TextView) dialog_hint.getWindow().findViewById(R.id.txt_alert_hint);
		txt_alert_hint.setText(msg);
		if (s == 0) {
			if (timer_dialog != null) {
				timer_dialog.cancel();
				timer_dialog = null;
			}
		} else {
			time_dialog = s;
			if (timer_dialog == null) {
				timer_dialog = new Timer();
				timer_dialog.schedule(new TimerTask() {
					@Override
					public void run() {
						if (time_dialog == 0) {
							dialog_hint.dismiss();
						}
						time_dialog--;
					}
				}, 0, 1000);
			}
		}
	}

	public void stopDialog() {
		if (timer_dialog != null) {
			timer_dialog.cancel();
			timer_dialog = null;
		}
		if (dialog_hint.isShowing()) {
			dialog_hint.dismiss();
		}
	}

	public void showDialogOff() {
		if (dialog_attend.isShowing()) {
			dialog_attend.dismiss();
		}
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		if (!dialog_hint.isShowing()) {
			dialog_hint.show();
		}
		dialog_hint.getWindow().setLayout(800, 450);
		dialog_hint.getWindow().setContentView(R.layout.dialog_off);
		dialog_off_txt = (TextView) dialog_hint.getWindow().findViewById(R.id.dialog_off_txt);
		dialog_off_btn1 = (Button) dialog_hint.getWindow().findViewById(R.id.dialog_off_btn1);
		dialog_off_btn2 = (Button) dialog_hint.getWindow().findViewById(R.id.dialog_off_btn2);
		dialog_off_btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog_hint.dismiss();
				if (timer_dialog != null) {
					timer_dialog.cancel();
					timer_dialog = null;
				}
			}
		});
		dialog_off_btn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					getOFFONTime(format.parse(time_on).getTime(), time_on.substring(0, 9));
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog_hint.dismiss();
				if (timer_dialog != null) {
					timer_dialog.cancel();
					timer_dialog = null;
				}
			}
		});
		if (timer_dialog != null) {
			timer_dialog.cancel();
			timer_dialog = null;
		}
		time_dialog = 30;
		timer_dialog = new Timer();
		timer_dialog.schedule(new TimerTask() {
			@Override
			public void run() {
				handler_updata.sendEmptyMessage(12);
				if (time_dialog == 0) {
					dialog_hint.dismiss();
				}
				time_dialog--;
			}
		}, 0, 1000);
	}

	public static String jcToTime(String jc) {
		if (!TextUtils.isEmpty(jc)) {
			SectionInfoDatabase database = new SectionInfoDatabase();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			String[] jcs = jc.split("-");
			if (jcs != null && jcs.length > 1) {
				SectionInfoModel model1 = database.query_by_jcdm(jcs[0]);
				SectionInfoModel model2 = database.query_by_jcdm(jcs[jcs.length - 1]);
				if (model1 != null && model2 != null) {
					Date date1 = new Date(Long.parseLong(model1.jcskkssj));
					Date date2 = new Date(Long.parseLong(model2.jcskjssj));
					return sdf.format(date1) + "~" + sdf.format(date2);
				}
			} else {
				SectionInfoModel model = database.query_by_jcdm(jc);
				if (model != null) {
					Date date1 = new Date(Long.parseLong(model.jcskkssj));
					Date date2 = new Date(Long.parseLong(model.jcskjssj));
					return sdf.format(date1) + "~" + sdf.format(date2);
				}
			}
		}
		return "";
	}

	public CurriculumInfoModel findCurriculum(int yxtq) {
		if (curriculumInfos_today == null) {
			return null;
		}
		try {
			SectionInfoDatabase sectionInfoDatabase = new SectionInfoDatabase();
			SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
			String HHmmss = formatter.format(System.currentTimeMillis());
			Date time = formatter.parse(HHmmss);
			for (CurriculumInfoModel model : curriculumInfos_today) {
				String[] jcs = model.jc.split("-");
				SectionInfoModel section1 = null, section2 = null;
				if (jcs.length > 1) {
					section1 = sectionInfoDatabase.query_by_jcdm(jcs[0]);
					section2 = sectionInfoDatabase.query_by_jcdm(jcs[jcs.length - 1]);
				} else {
					section1 = sectionInfoDatabase.query_by_jcdm(jcs[0]);
					section2 = section1;
				}
				if (section1 != null && section2 != null) {
					long kssj = Long.parseLong(section1.jcskkssj) - yxtq * 60 * 1000;
					long jssj = Long.parseLong(section2.jcskjssj);
					if (kssj <= time.getTime() && time.getTime() <= jssj) {
						return model;
					}
				}
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		//Log.e("我的Id","11111222233333");
		return null;
	}

	private TcpCallback tcpCallback = new TcpCallback() {

		@Override
		public void request(byte cmd, byte[] data, final String sn) {
			Log.d(TAG, "request cmd:" + (cmd & 0xFF));
			switch (cmd) {
				case TcpProtocol.PACKET_CMD_UPDATA_EQ: {
					if (getRunningActivityName().equals("com.advertisement.activity.DisplayActivity")) {
						Intent intent = new Intent(MyApplication.BROADCAST);
						intent.putExtra(MyApplication.BROADCAST_TAG, "PlayActivity_finish");
						broadcastManager.sendBroadcast(intent);
					}
					isUpdateEQ = true;
					httpProcess.QryDataTime(sn, equInfoModel.orgId);
					handler_updata.sendEmptyMessage(1);
					handler_touch.removeMessages(0);
					File file = new File(MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE);
					if (file.exists()) {
						File[] files = file.listFiles();
						if (files != null && files.length > 0) {
							for (File f : files) {
								f.delete();
							}
						}
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_NOTICE: {
					if (!isUpdateNotice && !isUpdateEQ) {
						isUpdateNotice = true;
						httpProcess.QryNoticePJ(equInfoModel.jssysdm, "0", MyApplication.NOTICE_PAGE_COUNT,
								equInfoModel.bjdm, true, sn);
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_CURRICULUM: {
					if (!isUpdateCurriculum && !isUpdateEQ) {
						isUpdateCurriculum = true;
						httpProcess.QryCurriculum_day(equInfoModel.jssysdm,
								MyApplication.getTime("yyyy-MM-dd") + " 00:00:00",
								MyApplication.getTime("yyyy-MM-dd") + " 23:59:59", "", sn);
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_UPDATA_TIME:
					if (!isUpdateEQ) {
						httpProcess.QryDataTime(sn, equInfoModel.orgId);
					}
					break;
				case TcpProtocol.PACKET_CMD_UPDATA_EQLIST:
					if (!isUpdateEQ) {
						httpProcess.QryEquList(equInfoModel.jssysdm, sn);
					}
					break;
				case TcpProtocol.PACKET_CMD_PLAY_TASK:
					if (!isDownloadPlay && !isUpdateEQ) {
						isDownloadPlay = true;
						httpProcess.QryPlayTask(equInfoModel.jssysdm, sn);
						playTask_sn = sn;
						handler_touch.removeMessages(0);
					}
					break;
				case TcpProtocol.PACKET_CMD_UPDATA_CONTROL: {
					DeviceDatabase deviceDatabase = new DeviceDatabase();
					SceneDatabase sceneDatabase = new SceneDatabase();
					List<ControlModel> controlModels = new ArrayList<ControlModel>();
					List<DeviceModel> deviceModels = deviceDatabase.query_all();
					if (deviceModels != null && deviceModels.size() > 0) {
						for (DeviceModel model : deviceModels) {
							ControlModel controlModel = new ControlModel();
							controlModel.id = model.id + "";
							controlModel.type = "00";
							controlModel.name = model.name;
							// controlModel.status = "00";
							// controlModels.add(controlModel);
							controlModel.status = "01";
							controlModels.add(controlModel);
						}
					}
					List<SceneModel> sceneModels = sceneDatabase.query_all();
					if (sceneModels != null && sceneModels.size() > 0) {
						for (SceneModel model : sceneModels) {
							ControlModel controlModel = new ControlModel();
							controlModel.id = model.id + "";
							controlModel.type = "01";
							controlModel.name = model.name;
							controlModel.status = "00";
							controlModels.add(controlModel);
						}
					}
					if (controlModels != null && controlModels.size() > 0) {
						httpProcess.CommitControl(equInfoModel.jssysdm, controlModels, sn);
					} else {
						httpProcess.commitBack(sn, HttpProcess.NONE, HttpProcess.NONE_MSG);
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_CONTROL:
					httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
					// SystemManager.LogHex(TAG, data, data.length);
					if (data != null && data.length > 2) {
						if (data[0] == (byte) 0x00) {
							if (data[1] == (byte) 0x01) {
								if (data[2] == (byte) 0x00) {
									smartUtils.openDoor(mContext, 1500);
								} else {
									smartUtils.closeDoor(mContext);
								}
								return;
							}
							if (data[2] == (byte) 0x00) {
								Log.d(TAG, "deviceCtrl id=" + data[1] + " ON");
								smartUtils.deviceCtrl(data[1], SmartProtocol.EQU_ON);
							} else if (data[2] == (byte) 0x01) {
								Log.d(TAG, "deviceCtrl id=" + data[1] + " OFF");
								smartUtils.deviceCtrl(data[1], SmartProtocol.EQU_OFF);
							}
						} else if (data[0] == (byte) 0x01) {
							Log.d(TAG, "equCtrl id=" + data[1]);
							smartUtils.equCtrl(data[1]);
						}
					}
					break;
				case TcpProtocol.PACKET_CMD_PRIZE: {
					if (!isUpdateEQ) {
						httpProcess.QryPrize(equInfoModel.jssysdm, "", sn, true);
						httpProcess.QryStar(equInfoModel.jssysdm, equInfoModel.bjdm, "0", "1000", sn);
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_BLACKBOARD: {
					if (!isUpdateEQ) {
						httpProcess.QryBlackboard(equInfoModel.jssysdm, sn);
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_ONDUTY: {
					if (!isUpdateEQ) {
						httpProcess.QryOnDuty(equInfoModel.jssysdm, sn);
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_SCHOOL: {
					if (!isUpdateEQ) {
						httpProcess.QrySchool(equInfoModel.jssysdm, sn);
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_QUEST: {
					if (!isUpdateEQ) {
						httpProcess.QryQuest(equInfoModel.jssysdm, "", sn);
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_EQUINFO: {
					executoService.execute(new Runnable() {
						@Override
						public void run() {
							File file = new File(MyApplication.PATH_ROOT + MyApplication.PATH_SCREENCAP);
							File[] files = file.listFiles();
							if (files != null && files.length > 0) {
								for (File f : files) {
									f.delete();
								}
							}
							String path = MyApplication.PATH_ROOT + MyApplication.PATH_SCREENCAP + "/"
									+ System.currentTimeMillis() + ".jpg";
							// 截屏
							takeScreenshot(path);
							Message msg = Message.obtain();
							msg.what = 0;
							Bundle bundle = new Bundle();
							bundle.putString("path", path);
							bundle.putString("sn", sn);
							msg.obj = bundle;
							handler_updata.sendMessageDelayed(msg, 8000);
						}
					});
					break;
				}
				case TcpProtocol.PACKET_CMD_EQUMODE: {
					if (!isUpdateEQ) {
						httpProcess.getEquModel(equInfoModel.jssysdm, sn);
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_EXAM: {
					if (!isUpdateEQ) {
						httpProcess.getExam(equInfoModel.jssysdm, sn);
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_PAGE: {
					if (pageLayoutTotal == 0) {
						httpProcess.qryPageLayout(sn);
						httpProcess.QryDiyMenu(equInfoModel.jssysdm);
						pageSn = sn;
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_VIDEO: {
					if (!isUpdateEQ && vedioInfoModels == null && classInfoModel != null) {
						httpProcess.qryVideo(null, equInfoModel.jssysdm, "bjdm", equInfoModel.bjdm, equInfoModel.bjdm);
						vedioSN = sn;
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_USER: {
					if (!isUpdateEQ) {
						httpProcess.qryUser(sn);
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_VOTE: {
					if (!isUpdateEQ) {
						httpProcess.QryVote("0", MyApplication.NOTICE_PAGE_COUNT, "", "vote", "vote", sn);
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_ACTIVITY: {
					if (!isUpdateEQ) {
						httpProcess.QryVote("0", MyApplication.NOTICE_PAGE_COUNT, "", "activity", "activity", sn);
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_PHOTO: {
					if (!isUpdateEQ) {
						if (classInfoModel != null) {
							httpProcess.QryPhoto(equInfoModel.jssysdm, equInfoModel.bjdm, sn);
						}
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_SYNEQU: {
					if (!isUpdateEQ) {
						httpProcess.Register(equInfoModel.equName, equInfoModel.jssysdm, equInfoModel.bjdm,
								equInfoModel.orgId);
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_UPDATEATTEND: {
					if (!isUpdateEQ) {
						handler_updata.removeMessages(100);
						handler_updata.sendEmptyMessageDelayed(100, 10 * 1000);
					}
					break;
				}
				case TcpProtocol.PACKET_CMD_OFF:
				case TcpProtocol.PACKET_CMD_RELOAD:
				case TcpProtocol.PACKET_CMD_REBOOT: {
					httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
					Intent intent = new Intent();
					intent.setAction("system_to_reboot");
					sendBroadcast(intent);
					break;
				}
				case TcpProtocol.PACKET_CMD_VERSION_CHECK: {
					isDownloadAPP = true;
					httpProcess.GetVersion(sn);
					break;
				}
			}
		}

		@Override
		public void connectResult(byte cmd, boolean ret) {
			Log.d(TAG, "connectResult cmd:" + (cmd & 0xFF) + " ret:" + ret);
		}
	};

	private CameraManagerListener cameraManagerListener = new CameraManagerListener() {

		@Override
		public void takePictureCompletion(String path) {
			Log.d(TAG, "takePictureCompletion path:" + path);
			isTakePicture = false;
		}
	};

	@Override
	public void NetworkStateChange(int state) {
		Log.i(TAG, "---NetworkStateChange---state:" + state);
		if (isUpdateEQ) {
			return;
		}
		networdState = state;
		handler_network.sendEmptyMessage(state);
		if (handler_call != null && state != NetworkState.STATE_CALL_ED) {
			handler_call.sendEmptyMessage(state);
		}
	}

	@Override
	public void OnCalled(String ip, int dstId, byte callType, int indoorId) {
		Log.i(TAG, "---OnCalled---ip:" + ip + " dstId:" + dstId + " callType:" + callType + " indoorId:" + indoorId);
		if (isUpdateEQ) {
			return;
		}
		if (getRunningActivityName().equals("com.advertisement.activity.DisplayActivity")) {
			Intent intent = new Intent(MyApplication.BROADCAST);
			intent.putExtra(MyApplication.BROADCAST_TAG, "PlayActivity_finish");
			broadcastManager.sendBroadcast(intent);
		} else if (mediaPlayer.isPlaying() && dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			mediaPlayer.stop();
			mediaPlayer.reset();
			closeVoice();
		}
		isGuangbo = false;
		isGuanggao = false;
		playTask_position = -1;

		EquListDatabase database = new EquListDatabase();
		call_eq = database.query_by_ip(ip);
		if (call_eq != null) {
			Intent intent = new Intent(this, CallActivity.class);
			intent.putExtra("isCall", false);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			showToast("查暂无该设备");
			mNetworkManager.stopCall();
		}
	}

	@Override
	public void OnUnlock() {
		Log.d(TAG, "---OnUnlock---");
	}

	@Override
	public void OnWords(String ip, int dstId, String path) {
		Log.d(TAG, "---OnWords---ip:" + ip + " dstId:" + dstId + " path:" + path);
	}

	@Override
	public void SendRecvResult(byte type, int state) {
		Log.d(TAG, "---SendRecvResult---type:" + type + " state:" + state);
	}

	@Override
	public void OnCard(String card) {
		Log.d(TAG, "---OnCard---" + card);
		if (handler_call != null && !TextUtils.isEmpty(card)) {
			Message msg = Message.obtain();
			msg.obj = card;
			msg.what = 99;
			handler_call.sendMessage(msg);
		}
	}


	private void OpenComPort(SerialHelper ComPort) {
		try {
			ComPort.open();
		} catch (SecurityException e) {
			ShowMessage("打开串口失败:没有串口读/写权限!");
		} catch (IOException e) {
			ShowMessage("打开串口失败:未知错误!");
		} catch (InvalidParameterException e) {
			ShowMessage("打开串口失败:参数错误!");
		}
	}
	private void ShowMessage(String sMsg) {
		Toast.makeText(this, sMsg, Toast.LENGTH_SHORT).show();
	}

	//读卡信息
	public void readEvens() {
		Log.e("readEvens", "readEvens");
		ComA.setPort(PATH);
		ComA.setBaudRate(BAUDRATE);
		OpenComPort(ComA);

		ComB.setPort(PATHRIGHT);
		ComB.setBaudRate(BAUDRATE);
		OpenComPort(ComB);

//		if (mEYunSdk == null) {
//			mEYunSdk = new EYunSDKCore(getApplicationContext());
//			mEYunSdk.Init();
//			mEYunSdk.startTX260Read(new TX260ReadListener() {
//
//				@Override
//				public void OnCrad(String card) {
//					mCardNum = card;
//					if (TextUtils.isEmpty(operateType) && TextUtils.isEmpty(cardType)) {
//						Message msg = Message.obtain();
//						msg.what = 0;
//						msg.obj = mCardNum;
//						handlerCommotAttend.sendMessage(msg);
//					} else if (!TextUtils.isEmpty(operateType)) {
//						if (classInfoModel != null && classRoomInfoModel != null
//								&& !TextUtils.isEmpty(classRoomInfoModel.cdglykh)
//								&& mCardNum.equals(classRoomInfoModel.cdglykh)) {
//							Intent intent = new Intent(BROADCAST);
//							intent.putExtra(BROADCAST_TAG, HttpProcess.OPERATE_AUTHORIZATION);
//							intent.putExtra(CMD_RESULT, true);
//							broadcastManager.sendBroadcast(intent);
//							return;
//						}
//						UserInfoDatabase userInfoDatabase = new UserInfoDatabase();
//						if (userInfoDatabase.query_by_cardNo(card) != null) {
//							Intent intent = new Intent(BROADCAST);
//							intent.putExtra(BROADCAST_TAG, HttpProcess.OPERATE_AUTHORIZATION);
//							intent.putExtra(CMD_RESULT, true);
//							broadcastManager.sendBroadcast(intent);
//							return;
//						}
//						httpProcess.Permission(equInfoModel.jssysdm, operateType, "JOB_CARD", "", "", mCardNum);
//						if (operateType.equals("VEDIO_CALL")) {
//							call_from_who = mCardNum;
//						}
//						Intent intent = new Intent(MyApplication.BROADCAST);
//						intent.putExtra(MyApplication.BROADCAST_TAG, "permission_ui");
//						broadcastManager.sendBroadcast(intent);
//					} else if (!TextUtils.isEmpty(cardType)) {
//						Intent intent = new Intent(MyApplication.BROADCAST);
//						intent.putExtra(MyApplication.BROADCAST_TAG, cardType);
//						intent.putExtra("mCardNum", mCardNum);
//						broadcastManager.sendBroadcast(intent);
//					}
//				}
//			});
//		}
		// if (mTimerGetCard == null && TYPE != 2) {
		// mTimerGetCard = new Timer();
		// mTimerGetCard.schedule(new TimerTask() {
		// @Override
		// public void run() {
		// byte[] uid = new byte[40];
		// int[] uidlen = new int[1];
		// uidlen[0] = 0;
		// if (!isUpdateEQ) {
		// mEYunSdk.GetRfidId(uid, 0, uidlen);
		// }
		// if (uidlen[0] != 0) {
		// if (!TextUtils.isEmpty(Unit.byteToHexStr(uid, 0,
		// uidlen[0]))) {
		// mCardNum = Unit.byteToHexStr(uid, 0, uidlen[0]);
		// Log.d(TAG, "card:" + mCardNum);
		// mTimerGetCard.cancel();
		// mTimerGetCard = null;
		// handler_touch.removeMessages(0);
		// handler_touch.sendEmptyMessageDelayed(0,
		// screensaver_time * 1000);
		// if (TextUtils.isEmpty(operateType)) {
		// Message msg = Message.obtain();
		// msg.what = 0;
		// msg.obj = mCardNum;
		// handlerCommotAttend.sendMessage(msg);
		// } else {
		// if (classRoomInfoModel != null
		// && !TextUtils
		// .isEmpty(classRoomInfoModel.cdglykh)
		// && mCardNum
		// .equals(classRoomInfoModel.cdglykh)) {
		// Intent intent = new Intent(BROADCAST);
		// intent.putExtra(BROADCAST_TAG,
		// HttpProcess.OPERATE_AUTHORIZATION);
		// intent.putExtra(CMD_RESULT, true);
		// broadcastManager.sendBroadcast(intent);
		// return;
		// }
		// httpProcess.Permission(equInfoModel.jssysdm,
		// operateType, "JOB_CARD", "", "",
		// mCardNum);
		// if (operateType.equals("VEDIO_CALL")) {
		// call_from_who = mCardNum;
		// }
		// Intent intent = new Intent(
		// MyApplication.BROADCAST);
		// intent.putExtra(MyApplication.BROADCAST_TAG,
		// "permission_ui");
		// broadcastManager.sendBroadcast(intent);
		// }
		// handlerCommotAttend
		// .sendEmptyMessageDelayed(1, 3000);
		// }
		// }
		// }
		// }, 10, 500);
		// }
	}

	// 通过包名获取版本号
	public static String getVersionName(Context context, String sysCode, String packagename) {
		if (sysCode.equals(APP_ID)) {
			packagename = APP_NAME;
		} else if (sysCode.equals(APP_TOOL_ID)) {
			packagename = APP_TOOL_NAME;
		} else if (sysCode.equals(APP_READ_ID)) {
			packagename = APP_READ_NAME;
		}
		PackageManager manager = context.getPackageManager();// 获取包管理器
		try {
			// 通过当前的包名获取包的信息
			PackageInfo info = manager.getPackageInfo(packagename, 0);// 获取包对象信息
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Handler handlerCommotAttend = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					commotAttend(msg.obj.toString());
					break;
				case 1:
					readEvens();
					break;
			}
		}

		;
	};

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.getWindow().setType(LayoutParams.TYPE_SYSTEM_ALERT);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setTitle(null);
			progressDialog.setMessage("加载中");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
		}
	}

	private void closeProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public void commotAttend(String card) {
		Log.d(TAG, "==================commotAttend:" + card);
//		showProgressDialog();
		httpProcess.commitClockInfo2(card);
		// ClockInfoModel clockInfoModel = commitClock(card);
		// if (clockInfoModel == null) {
		// showAttendError("未找到该人员信息");
		// httpProcess.QryNearAttend(card, null);
		// return;
		// }
		// try {
		// if (clockInfoModel.js.equals("1") && clockRule_teather != null) {
		// attend(clockInfoModel, card, clockRule_teather);
		// } else if (clockInfoModel.js.equals("2")
		// && clockRule_student != null) {
		// attend(clockInfoModel, card, clockRule_student);
		// } else {
		// showAttendError("考勤规则异常");
		// }
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
	}

	private ClockInfoModel commitClock(String card) {
		ClockInfoDatabase clockInfoDatabase = new ClockInfoDatabase();

		// AttendInfoDatabase attendInfoDatabase = new AttendInfoDatabase();
		// List<AttendInfoModel> attendInfoModels = attendInfoDatabase
		// .query_by_xskh(card);
		// if (attendInfoModels != null) {
		// ClockInfoModel clockInfoModel = new ClockInfoModel();
		// clockInfoModel.id = System.currentTimeMillis() + "";
		// clockInfoModel.xm = attendInfoModels.get(0).xsxm;
		// clockInfoModel.js = "2";
		// clockInfoModel.rybh = attendInfoModels.get(0).xsxh;
		// clockInfoModel.dkrq = getTime("yyyy-MM-dd");
		// clockInfoModel.dksj = getTime("HH:mm:ss");
		// clockInfoModel.dklx = "1";
		// clockInfoModel.isupload = "0";
		// clockInfoDatabase.insert(clockInfoModel);
		//
		// List<ClockInfoModel> clockInfoModels = clockInfoDatabase
		// .query_by_isupload("0");
		// if (clockInfoModels != null) {
		// httpProcess.commitClockInfo(clockInfoModels);
		// }
		// return clockInfoModel;
		// }
		PersonInfoDatabase personInfoDatabase = new PersonInfoDatabase();
		PersonInfoModel personInfoModel = personInfoDatabase.query_by_cardid(card);
		if (personInfoModel != null) {
			ClockInfoModel clockInfoModel = new ClockInfoModel();
			clockInfoModel.id = System.currentTimeMillis() + "";
			clockInfoModel.xm = personInfoModel.xm;
			clockInfoModel.js = "1";
			clockInfoModel.rybh = personInfoModel.rybh;
			clockInfoModel.dkrq = getTime("yyyy-MM-dd");
			clockInfoModel.dksj = getTime("HH:mm:ss");
			clockInfoModel.dklx = "1";
			clockInfoModel.isupload = "0";
			clockInfoDatabase.insert(clockInfoModel);

			List<ClockInfoModel> clockInfoModels = clockInfoDatabase.query_by_isupload("0");
			if (clockInfoModels != null) {
				httpProcess.commitClockInfo(clockInfoModels);
			}
			return clockInfoModel;
		}

		PersonnelAttendanceDatabase personnelAttendanceDatabase = new PersonnelAttendanceDatabase();
		List<PersonnelAttendanceModel> list = personnelAttendanceDatabase.query("date = ? and cardid = ?",
				new String[]{getTime("yyyy-MM-dd"), card});
		if (list != null) {
			ClockInfoModel clockInfoModel = new ClockInfoModel();
			clockInfoModel.id = System.currentTimeMillis() + "";
			clockInfoModel.xm = list.get(0).xm;
			clockInfoModel.js = list.get(0).js;
			clockInfoModel.rybh = list.get(0).rybh;
			clockInfoModel.dkrq = getTime("yyyy-MM-dd");
			clockInfoModel.dksj = getTime("HH:mm:ss");
			clockInfoModel.dklx = "1";
			clockInfoModel.isupload = "0";
			clockInfoDatabase.insert(clockInfoModel);
			List<ClockInfoModel> clockInfoModels = clockInfoDatabase.query_by_isupload("0");
			if (clockInfoModels != null) {
				httpProcess.commitClockInfo(clockInfoModels);
			}
			return clockInfoModel;
		}
		return null;
	}

	private void attend(ClockInfoModel clockInfoModel, String card, ClockRuleModel ruleModel) throws ParseException {
		PersonnelAttendanceDatabase personnelAttendanceDatabase = new PersonnelAttendanceDatabase();
		List<PersonnelAttendanceModel> list = personnelAttendanceDatabase.query("date = ? and rybh = ?",
				new String[]{clockInfoModel.dkrq, clockInfoModel.rybh});
		if (list == null) {
			if (clockInfoModel.js.equals("1")) {
				showAttendError("暂无该教师考勤信息");
			} else {
				showAttendError("暂无该学生考勤信息");
			}
			httpProcess.QryNearAttend(card, null);
			return;
		}
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		boolean isIntime = false, isUpdata = false;
		long now = format.parse(clockInfoModel.dksj).getTime();
		for (PersonnelAttendanceModel model : list) {
			long toWorkTime = format.parse(model.toWorkTime).getTime();
			long offWorkTime = format.parse(model.offWorkTime).getTime();
			long sbkyxtq = toWorkTime - Integer.parseInt(ruleModel.sbkyxtq) * 60 * 1000;
			long sbkyxyh = toWorkTime + Integer.parseInt(ruleModel.sbkyxyh) * 60 * 1000;
			long yxcd = toWorkTime + Integer.parseInt(ruleModel.yxcd) * 60 * 1000;
			long cgcd = toWorkTime + Integer.parseInt(ruleModel.cgcd) * 60 * 1000;
			long xbkyxtq = offWorkTime - Integer.parseInt(ruleModel.xbkyxtq) * 60 * 1000;
			long xbkyxyh = offWorkTime + Integer.parseInt(ruleModel.xbkyxyh) * 60 * 1000;
			if (sbkyxtq <= now && now <= sbkyxyh) {
				isIntime = true;
				if (!TextUtils.isEmpty(model.status) && model.status.split(",")[0].equals("11")) {
					isUpdata = true;
					String[] s = model.status.split(",");
					if (cgcd <= now) {
						model.status = "4," + s[1];
						model.toWorkClockTime = clockInfoModel.dksj;
						showAttend("4", clockInfoModel, "旷课");
						if (model.js.equals("2")) {
							attendStudent(model, card);
						}
					}
					if (yxcd <= now && now < cgcd) {
						model.status = "3," + s[1];
						model.toWorkClockTime = clockInfoModel.dksj;
						showAttend("3", clockInfoModel, "迟到");
						if (model.js.equals("2")) {
							attendStudent(model, card);
						}
					}
					if (now < yxcd) {
						model.status = "0," + s[1];
						model.toWorkClockTime = clockInfoModel.dksj;
						showAttend("0", clockInfoModel, "已签到");
						if (model.js.equals("2")) {
							attendStudent(model, card);
						}
					}
					personnelAttendanceDatabase.update(model);
					List<PersonnelAttendanceModel> models = new ArrayList<PersonnelAttendanceModel>();
					models.add(model);
					httpProcess.commitPersonnelAttendance(models);
					break;
				}
			}
			if (xbkyxtq <= now && now <= xbkyxyh) {
				isIntime = true;
				if (!TextUtils.isEmpty(model.status) && model.status.split(",")[1].equals("11")) {
					isUpdata = true;
					String[] s = model.status.split(",");
					if (now < offWorkTime) {
						model.status = s[0] + ",5";
						model.offWorkClockTime = clockInfoModel.dksj;
						showAttend("5", clockInfoModel, "早退");
						if (model.js.equals("2")) {
							attendStudent(model, card);
						}
					}
					if (offWorkTime <= now) {
						model.status = s[0] + ",0";
						model.offWorkClockTime = clockInfoModel.dksj;
						showAttend("0", clockInfoModel, "已签退");
						if (model.js.equals("2")) {
							attendStudent(model, card);
						}
					}
					personnelAttendanceDatabase.update(model);
					List<PersonnelAttendanceModel> models = new ArrayList<PersonnelAttendanceModel>();
					models.add(model);
					httpProcess.commitPersonnelAttendance(models);
					break;
				}
			}
		}
		if (!isIntime) {
			showAttendError("未在考勤时段内打卡");
			httpProcess.QryNearAttend(card, null);
			return;
		}
		if (!isUpdata) {
			for (int i = list.size() - 1; i >= 0; i--) {
				PersonnelAttendanceModel model = list.get(i);
				long toWorkTime = format.parse(model.toWorkTime).getTime();
				long offWorkTime = format.parse(model.offWorkTime).getTime();
				long sbkyxtq = toWorkTime - Integer.parseInt(ruleModel.sbkyxtq) * 60 * 1000;
				long sbkyxyh = toWorkTime + Integer.parseInt(ruleModel.sbkyxyh) * 60 * 1000;
				long xbkyxtq = offWorkTime - Integer.parseInt(ruleModel.xbkyxtq) * 60 * 1000;
				long xbkyxyh = offWorkTime + Integer.parseInt(ruleModel.xbkyxyh) * 60 * 1000;
				if (xbkyxtq <= now && now <= xbkyxyh) {
					if (!TextUtils.isEmpty(model.status) && !model.status.split(",")[1].equals("11")) {
						String[] s = model.status.split(",");
						if (s[1].equals("5")) {
							showAttend("5", clockInfoModel, "早退");
						} else if (s[1].equals("0")) {
							showAttend("0", clockInfoModel, "已签退");
						} else if (s[1].equals("4")) {
							showAttend("4", clockInfoModel, "旷课");
						}
						break;
					}
				}
				if (sbkyxtq <= now && now <= sbkyxyh) {
					if (!TextUtils.isEmpty(model.status) && !model.status.split(",")[0].equals("11")) {
						String[] s = model.status.split(",");
						if (s[0].equals("4")) {
							showAttend("4", clockInfoModel, "旷课");
						} else if (s[0].equals("3")) {
							showAttend("3", clockInfoModel, "迟到");
						} else if (s[0].equals("0")) {
							showAttend("0", clockInfoModel, "已签到");
						}
						break;
					}
				}
			}
		}
	}

	private void attendStudent(PersonnelAttendanceModel personnelAttendModel, String card) throws ParseException {
		if (personnelAttendModel == null) {
			return;
		}
		AttendInfoDatabase attendInfoDatabase = new AttendInfoDatabase();
		List<AttendInfoModel> attendInfoModels = attendInfoDatabase.query_by_skrqAndxskh(personnelAttendModel.date,
				card);
		if (attendInfoModels == null) {
			return;
		}
		if (TextUtils.isEmpty(personnelAttendModel.status)) {
			return;
		}
		boolean isUpdate = false;
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		long toWorkTime = format.parse(personnelAttendModel.toWorkTime).getTime();
		long offWorkTime = format.parse(personnelAttendModel.offWorkTime).getTime();
		CurriculumInfoDatabase curriculumInfoDatabase = new CurriculumInfoDatabase();
		SectionInfoDatabase sectionInfoDatabase = new SectionInfoDatabase();
		for (AttendInfoModel attendInfoModel : attendInfoModels) {
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
			if ((toWorkTime <= Long.parseLong(section1.jcskkssj) && Long.parseLong(section2.jcskjssj) <= offWorkTime)
					|| (toWorkTime <= Long.parseLong(section2.jcskjssj)
					&& Long.parseLong(section2.jcskjssj) <= offWorkTime)
					|| (toWorkTime <= Long.parseLong(section1.jcskkssj)
					&& Long.parseLong(section1.jcskkssj) <= offWorkTime)) {
				isUpdate = true;
				String[] s = personnelAttendModel.status.split(",");
				attendInfoModel.kqzt = s[0];
				if (!attendInfoModel.kqzt.equals("4") && s[1].equals("5")) {
					attendInfoModel.kqzt = s[1];
				}
				attendInfoDatabase.update(attendInfoModel.id, attendInfoModel);
				httpProcess.CommitAttend(attendInfoModel.xsxh, attendInfoModel);
			}
		}
		if (isUpdate) {
			Intent intent = new Intent(MyApplication.BROADCAST);
			intent.putExtra(MyApplication.BROADCAST_TAG, "commotAttend_ui");
			intent.putExtra("attend_id", attendInfoModels.get(0).id);
			intent.putExtra("attend_kqzt", attendInfoModels.get(0).kqzt);
			broadcastManager.sendBroadcast(intent);
		}
	}

	private ImageView img_attend_state, img_attend_photo;
	private TextView txt_attend_state1, txt_attend_state2, txt_attend_state3, txt_attend_state4, txt_attend_name,
			txt_attend_xh, txt_attend_banji, txt_attend_time;
	private Button btn_attend_close;

	private void showAttend(AttendInfoModel attendInfoModel, String msg) {
		if (dialog_hint.isShowing()) {
			dialog_hint.dismiss();
		}
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		if (!dialog_attend.isShowing()) {
			dialog_attend.show();
		}
		dialog_attend.getWindow().setLayout(1112, 660);
		dialog_attend.getWindow().setContentView(R.layout.dialog_attend);
		img_attend_state = (ImageView) dialog_attend.getWindow().findViewById(R.id.img_attend_state);
		img_attend_photo = (ImageView) dialog_attend.getWindow().findViewById(R.id.img_attend_photo);
		txt_attend_state1 = (TextView) dialog_attend.getWindow().findViewById(R.id.txt_attend_state1);
		txt_attend_state3 = (TextView) dialog_attend.getWindow().findViewById(R.id.txt_attend_state3);
		txt_attend_name = (TextView) dialog_attend.getWindow().findViewById(R.id.txt_attend_name);
		txt_attend_xh = (TextView) dialog_attend.getWindow().findViewById(R.id.txt_attend_xh);
		txt_attend_banji = (TextView) dialog_attend.getWindow().findViewById(R.id.txt_attend_banji);
		txt_attend_time = (TextView) dialog_attend.getWindow().findViewById(R.id.txt_attend_time);
		btn_attend_close = (Button) dialog_attend.getWindow().findViewById(R.id.btn_attend_close);
		btn_attend_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog_attend.dismiss();
			}
		});
		if (attendInfoModel.kqzt.equals("0") || attendInfoModel.kqzt.equals("6") || attendInfoModel.kqzt.equals("7")
				|| attendInfoModel.kqzt.equals("8")) {
			img_attend_state.setImageResource(R.drawable.img_attend_good);
		} else if (attendInfoModel.kqzt.equals("1") || attendInfoModel.kqzt.equals("2")
				|| attendInfoModel.kqzt.equals("9")) {
			img_attend_state.setImageResource(R.drawable.img_attend_notbad);
		} else if (attendInfoModel.kqzt.equals("3") || attendInfoModel.kqzt.equals("4")
				|| attendInfoModel.kqzt.equals("5") || attendInfoModel.kqzt.equals("10")) {
			img_attend_state.setImageResource(R.drawable.img_attend_bad);
		}
		txt_attend_state1.setText(attendInfoModel.xsxm + "同学，" + msg + "!");
		txt_attend_state3.setText(attendInfoModel.qdsx);
		txt_attend_name.setText(attendInfoModel.xsxm);
		txt_attend_xh.setText("(" + attendInfoModel.xsxh + ")");
		txt_attend_banji.setText("班级: " + attendInfoModel.ssbjmc);
		txt_attend_time.setText("时间: " + getTime("yyyy年MM月dd日 HH:mm:ss"));
		if (!TextUtils.isEmpty(attendInfoModel.zp)) {
			String[] s = attendInfoModel.zp.split("\\.");
			ImageLoader loader = new ImageLoader();
			img_attend_photo.setImageBitmap(loader.decodeSampledBitmapFromPath(
					PATH_ROOT + PATH_PICTURE + "/attend_" + attendInfoModel.id + "." + s[s.length - 1], 180, 220));
		}
		time_dialog = 5;
		if (timer_dialog == null) {
			timer_dialog = new Timer();
			timer_dialog.schedule(new TimerTask() {
				@Override
				public void run() {
					if (time_dialog == 0) {
						dialog_attend.dismiss();
					}
					time_dialog--;
				}
			}, 0, 1000);
		}
	}

	/**
	 * @param status 状态：1、打卡成功：打钩 2、非正常状态：感叹号
	 * @param title  文字标题
	 * @param msg    服务端消息
	 */
	private void showAttend(int status, String title, String msg) {
		if (dialog_hint.isShowing()) {
			dialog_hint.dismiss();
		}
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		if (!dialog_attend.isShowing()) {
			dialog_attend.show();
		}

		dialog_attend.getWindow().setLayout(1112, 660);
		dialog_attend.getWindow().setContentView(R.layout.dialog_guangbo);
		img_guangbo_state = (ImageView) dialog_attend.getWindow().findViewById(R.id.img_guangbo_state);
		txt_guangbo_state = (TextView) dialog_attend.getWindow().findViewById(R.id.txt_guangbo_state);
		txt_guangbo_msg = (TextView) dialog_attend.getWindow().findViewById(R.id.txt_guangbo_msg);
		btn_guangbo_close = (Button) dialog_attend.getWindow().findViewById(R.id.btn_guangbo_close);
		if (status == 1) {
			img_guangbo_state.setImageResource(R.drawable.img_attend_good);
			txt_guangbo_state.setTextColor(Color.BLACK);
		} else if (status == 2) {
			img_guangbo_state.setImageResource(R.drawable.img_attend_exclamation);
			txt_guangbo_state.setTextColor(Color.RED);
		}
		txt_guangbo_state.setText(title);
		txt_guangbo_msg.setText(msg);
		btn_guangbo_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog_attend.dismiss();
			}
		});
		time_dialog = 10;
		if (timer_dialog == null) {
			timer_dialog = new Timer();
			timer_dialog.schedule(new TimerTask() {
				@Override
				public void run() {
					if (time_dialog == 0) {
						dialog_attend.dismiss();
					}
					time_dialog--;
				}
			}, 0, 1000);
		}
	}

	private void showAttend(String kqzt, ClockInfoModel clockInfoModel, String msg) {
		if (dialog_hint.isShowing()) {
			dialog_hint.dismiss();
		}
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		if (!dialog_attend.isShowing()) {
			dialog_attend.show();
		}
		dialog_attend.getWindow().setLayout(1112, 660);
		dialog_attend.getWindow().setContentView(R.layout.dialog_attend);
		img_attend_state = (ImageView) dialog_attend.getWindow().findViewById(R.id.img_attend_state);
		img_attend_photo = (ImageView) dialog_attend.getWindow().findViewById(R.id.img_attend_photo);
		txt_attend_state1 = (TextView) dialog_attend.getWindow().findViewById(R.id.txt_attend_state1);
		txt_attend_name = (TextView) dialog_attend.getWindow().findViewById(R.id.txt_attend_name);
		txt_attend_xh = (TextView) dialog_attend.getWindow().findViewById(R.id.txt_attend_xh);
		txt_attend_banji = (TextView) dialog_attend.getWindow().findViewById(R.id.txt_attend_banji);
		txt_attend_time = (TextView) dialog_attend.getWindow().findViewById(R.id.txt_attend_time);
		btn_attend_close = (Button) dialog_attend.getWindow().findViewById(R.id.btn_attend_close);
		btn_attend_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog_attend.dismiss();
			}
		});
		if (kqzt.equals("0")) {
			img_attend_state.setImageResource(R.drawable.img_attend_good);
		} else if (kqzt.equals("3") || kqzt.equals("5")) {
			img_attend_state.setImageResource(R.drawable.img_attend_notbad);
		} else {
			img_attend_state.setImageResource(R.drawable.img_attend_bad);
		}
		if (clockInfoModel.js.equals("1")) {
			txt_attend_state1.setText(clockInfoModel.xm + "教师，" + msg + "!");
			txt_attend_name.setText(clockInfoModel.xm);
			txt_attend_xh.setText("(" + clockInfoModel.rybh + ")");
			txt_attend_banji.setVisibility(View.INVISIBLE);
			txt_attend_time.setText("时间: " + clockInfoModel.dkrq + " " + clockInfoModel.dksj);
		} else if (clockInfoModel.js.equals("2")) {
			txt_attend_state1.setText(clockInfoModel.xm + "学生，" + msg + "!");
			txt_attend_name.setText(clockInfoModel.xm);
			txt_attend_time.setText("时间: " + clockInfoModel.dkrq + " " + clockInfoModel.dksj);

			AttendInfoDatabase database = new AttendInfoDatabase();
			List<AttendInfoModel> list = database.query_by_xsxh(clockInfoModel.rybh);
			if (list != null) {
				txt_attend_xh.setText("(" + list.get(0).xsxh + ")");
				txt_attend_banji.setText("班级: " + list.get(0).ssbjmc);
				if (!TextUtils.isEmpty(list.get(0).zp)) {
					String[] s = list.get(0).zp.split("\\.");
					ImageLoader loader = new ImageLoader();
					img_attend_photo.setImageBitmap(loader.decodeSampledBitmapFromPath(
							PATH_ROOT + PATH_PICTURE + "/attend_" + list.get(0).id + "." + s[s.length - 1], 180, 220));
				}
			} else {
				txt_attend_xh.setText("(" + clockInfoModel.rybh + ")");
				txt_attend_banji.setVisibility(View.INVISIBLE);
			}
		}
		time_dialog = 10;
		if (timer_dialog == null) {
			timer_dialog = new Timer();
			timer_dialog.schedule(new TimerTask() {
				@Override
				public void run() {
					if (time_dialog == 0) {
						dialog_attend.dismiss();
					}
					time_dialog--;
				}
			}, 0, 1000);
		}
	}

	private ImageView img_guangbo_state;
	private TextView txt_guangbo_state, txt_guangbo_msg;
	private Button btn_guangbo_close;

	private void showAttendError(String msg) {
		if (dialog_hint.isShowing()) {
			dialog_hint.dismiss();
		}
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		if (!dialog_attend.isShowing()) {
			dialog_attend.show();
		}
		dialog_attend.getWindow().setLayout(1112, 660);
		dialog_attend.getWindow().setContentView(R.layout.dialog_guangbo);
		img_guangbo_state = (ImageView) dialog_attend.getWindow().findViewById(R.id.img_guangbo_state);
		txt_guangbo_state = (TextView) dialog_attend.getWindow().findViewById(R.id.txt_guangbo_state);
		txt_guangbo_msg = (TextView) dialog_attend.getWindow().findViewById(R.id.txt_guangbo_msg);
		btn_guangbo_close = (Button) dialog_attend.getWindow().findViewById(R.id.btn_guangbo_close);
		img_guangbo_state.setImageResource(R.drawable.img_attend_bad);
		txt_guangbo_state.setText("考勤异常");
		txt_guangbo_msg.setText(msg);
		btn_guangbo_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog_attend.dismiss();
			}
		});
		time_dialog = 10;
		if (timer_dialog == null) {
			timer_dialog = new Timer();
			timer_dialog.schedule(new TimerTask() {
				@Override
				public void run() {
					if (time_dialog == 0) {
						dialog_attend.dismiss();
					}
					time_dialog--;
				}
			}, 0, 1000);
		}
	}

	public void showGuangbo(String msg, boolean isClose) {
		openVoice();
		if (regionIsPlaying && mediaPlayer_region.isPlaying()) {
			mediaPlayer_region.pause();
		}

		if (dialog_attend.isShowing()) {
			dialog_attend.dismiss();
		}
		if (!dialog_hint.isShowing()) {
			dialog_hint.dismiss();
		}
		if (dialog != null && dialog.isShowing()) {
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			dialog = builder.create();
		}
		dialog.getWindow().setType(LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				closeVoice();
				if (regionIsPlaying) {
					mediaPlayer_region.start();
				}
			}
		});
		dialog.show();
		dialog.getWindow().setLayout(1112, 660);
		dialog.getWindow().setContentView(R.layout.dialog_guangbo);
		img_guangbo_state = (ImageView) dialog.getWindow().findViewById(R.id.img_guangbo_state);
		txt_guangbo_state = (TextView) dialog.getWindow().findViewById(R.id.txt_guangbo_state);
		txt_guangbo_msg = (TextView) dialog.getWindow().findViewById(R.id.txt_guangbo_msg);
		btn_guangbo_close = (Button) dialog.getWindow().findViewById(R.id.btn_guangbo_close);
		txt_guangbo_state.setText("请注意");
		txt_guangbo_msg.setText(msg);
		btn_guangbo_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
					mediaPlayer.reset();
					isGuangbo = false;
					isGuanggao = false;
					closeVoice();
				}
			}
		});
		if (isClose) {
			btn_guangbo_close.setVisibility(View.VISIBLE);
		} else {
			btn_guangbo_close.setVisibility(View.INVISIBLE);
		}
	}

	private boolean serviceIsRunning(Context context, String className) {
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(200);
		for (int i = 0; i < mServiceList.size(); i++) {
			if (className.equals(mServiceList.get(i).service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private boolean CheckVersion(String oldV, String newV) {
		String[] oldVStrings = oldV.split("\\.");
		String[] newVStrings = newV.split("\\.");
		if (oldVStrings.length == newVStrings.length) {
			if (Integer.parseInt(newVStrings[0]) > Integer.parseInt(oldVStrings[0])) {
				return true;
			} else if (oldVStrings.length > 1 && Integer.parseInt(newVStrings[1]) > Integer.parseInt(oldVStrings[1])) {
				return true;
			} else if (oldVStrings.length > 2 && Integer.parseInt(newVStrings[2]) > Integer.parseInt(oldVStrings[2])) {
				return true;
			}
		}
		return false;
	}

	private void doVersion(final List<ArtifactVerType> list, final String sn, final String subject) {
		executoService.execute(new Runnable() {
			@Override
			public void run() {
				List<ArtifactVerType> verTypes = new ArrayList<ArtifactVerType>();
				if (subject.equals("2000")) {
					for (ArtifactVerType model : list) {
						String ver = getVersionName(mContext, model.sysCode, model.artifactVerInfo);
						if (ver != null && !CheckVersion(ver, model.artifactVerCode)) {
						} else {
							verTypes.add(model);
						}
					}
				} else {
					verTypes = list;
				}
				boolean isFail = false;
				if (verTypes.size() > 0) {
					downloadAPP_total = verTypes.size();
					downloadAPP_avail = 0;
					String[] paths = new String[downloadAPP_total];
					String[] packageNames = new String[downloadAPP_total];
					for (ArtifactVerType model : verTypes) {
						paths[downloadAPP_avail] = downLoadAPP(model);
						packageNames[downloadAPP_avail] = model.artifactVerInfo;
						downloadAPP_avail++;
					}
					for (int i = 0; i < paths.length; i++) {
						if (TextUtils.isEmpty(paths[i])) {
							continue;
						}
						File file = new File(paths[i]);
						String md5 = getMd5ByFile(file);
						if (!TextUtils.isEmpty(md5) && !TextUtils.isEmpty(verTypes.get(i).md5Check)
								&& (md5.equals(verTypes.get(i).md5Check.toUpperCase())
								|| md5.endsWith(verTypes.get(i).md5Check.toUpperCase())
								|| md5.startsWith(verTypes.get(i).md5Check.toUpperCase()))) {
							Message msg = Message.obtain();
							msg.what = 10;
							msg.obj = verTypes.get(i).artifactVerInfo + "应用下载成功，正在安装";
							handler_updata.sendMessage(msg);
							if (verTypes.get(i).artifactType.equals("01")) {
								String newApk = PATH_ROOT + PATH_APP + "/" + verTypes.get(i).sysCode + "-"
										+ verTypes.get(i).artifactVerCode + ".apk";
								String oldApk = PATH_ROOT + PATH_APP + "/" + verTypes.get(i).sysCode + "-"
										+ verTypes.get(i).oldArtifactCode + ".apk";
								ApkPatchJni.ApkPatch(oldApk, newApk, paths[i]);
								Intent intent = new Intent();
								intent.setAction("slient_install_dmb");
								intent.putExtra("dmb", newApk);
								if (packageNames[i].equals(APP_TOOL_NAME)) {
									intent.putExtra("reboot_flag", true);
								} else {
									intent.putExtra("reboot_flag", false);
								}
								if (packageNames[i].equals(APP_NAME)) {
									intent.putExtra("packageName", packageNames[i]);
								}
								sendBroadcast(intent);
							} else if (verTypes.get(i).artifactType.equals("02")) {
								Intent intent = new Intent();
								intent.setAction("slient_install_dmb");
								intent.putExtra("dmb", paths[i]);
								if (packageNames[i].equals(APP_TOOL_NAME)) {
									intent.putExtra("reboot_flag", true);
								} else {
									intent.putExtra("reboot_flag", false);
								}
								if (packageNames[i].equals(APP_NAME)) {
									intent.putExtra("packageName", packageNames[i]);
								}
								sendBroadcast(intent);
							}
						} else {
							Log.e(TAG, "文件校验失败");
							file.delete();
							Message msg = Message.obtain();
							msg.what = 10;
							msg.obj = verTypes.get(i).artifactVerInfo + "更新应用失败，原因：文件校验失败";
							handler_updata.sendMessage(msg);
							isFail = true;
						}
					}
					handler_updata.sendEmptyMessage(3);
					isDownloadAPP = false;
				} else {
					downloadAPP_total = 0;
					downloadAPP_avail = 0;
					isDownloadAPP = false;
					Message msg = Message.obtain();
					msg.what = 10;
					msg.obj = "应用已是最新版本";
					handler_updata.sendMessage(msg);
				}
				Bundle bundle = new Bundle();
				bundle.putString("sn", sn);
				if (isFail) {
					bundle.putString("id", HttpProcess.ERROR);
					bundle.putString("msg", "文件校验失败");
				} else {
					bundle.putString("id", HttpProcess.SUCESS);
					bundle.putString("msg", HttpProcess.SUCESS_MSG);
				}
				Message msg = Message.obtain();
				msg.what = 11;
				msg.obj = bundle;
				handler_updata.sendMessage(msg);
			}
		});
	}

	private String downLoadAPP(final ArtifactVerType info) {
		if (TextUtils.isEmpty(info.artifaceDownloadPath)) {
			return null;
		}
		InputStream input = null;
		OutputStream output = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(info.artifaceDownloadPath);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(DOWNLOAD_IMG_TIMEOUT);
			connection.connect();
			int status = connection.getResponseCode();
			Log.d(TAG, "downLoad APP status --- " + status);
			if (status != HttpURLConnection.HTTP_OK) {
				return null;
			}
			int fileLength = connection.getContentLength();
			input = connection.getInputStream();
			String filename = null;
			// 补丁下载
			if (info.artifactType.equals("01")) {
				filename = PATH_ROOT + PATH_APP + "/" + info.fileName;
			}
			// 完整包下载
			else if (info.artifactType.equals("02")) {
				filename = PATH_ROOT + PATH_APP + "/" + info.sysCode + "-" + info.artifactVerCode + ".apk";
			}
			File file = new File(filename);
			if (file.exists()) {
				file.delete();
			}
			output = new FileOutputStream(filename);
			// 读取大文件
			byte[] buffer = new byte[4 * 1024];
			long total = 0;
			int count;
			while ((count = input.read(buffer)) != -1) {
				total += count;
				output.write(buffer, 0, count);

				Message msg = Message.obtain();
				msg.what = 2;
				msg.arg1 = (int) (total * 100 / fileLength);
				handler_updata.sendMessage(msg);
			}
			return filename;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.disconnect();
			try {
				if (output != null)
					output.close();
				if (input != null)
					input.close();
			} catch (IOException ignored) {
				ignored.printStackTrace();
			}
		}
		return null;
	}

	public void downLoadImg(final String urlStr, final String path) {
		if (TextUtils.isEmpty(urlStr)) {
			return;
		}
		final File file = new File(path);
		if (file.exists()) {
			return;
		}
		executoService.execute(new Runnable() {
			@Override
			public void run() {
				if (urlStr.startsWith("ftp")) {
					FTPManager ftpManager = new FTPManager();
					if (ftpManager.connect(urlStr, path)) {
						ftpManager.download();
						ftpManager.disConnect();
					}
				} else {
					httpDownLoad(urlStr, path);
				}
			}
		});
	}

	public boolean httpDownLoad(String urlStr, String path) {
		InputStream input = null;
		OutputStream output = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(DOWNLOAD_IMG_TIMEOUT);
			connection.connect();
			int status = connection.getResponseCode();
			Log.d(TAG, "start downLoad urlStr:" + urlStr + "\npath:" + path + "\ndownLoad status --- " + status);
			if (status != HttpURLConnection.HTTP_OK) {
				return false;
			}
			input = connection.getInputStream();
			output = new FileOutputStream(path);
			byte[] buffer = new byte[4 * 1024];
			int count;
			while ((count = input.read(buffer)) != -1) {
				output.write(buffer, 0, count);
			}
			if (connection != null)
				connection.disconnect();
			if (output != null)
				output.close();
			if (input != null)
				input.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void initImgStudent() {
		StudentInfoDatabase database = new StudentInfoDatabase();
		List<StudentInfoModel> studentInfoModels = database.query_all();
		if (studentInfoModels != null && studentInfoModels.size() > 0) {
			for (StudentInfoModel model : studentInfoModels) {
				if (!TextUtils.isEmpty(model.zp)) {
					String[] s = model.zp.split("\\.");
					downLoadImg(model.zp, PATH_ROOT + PATH_PICTURE + "/student_" + model.id + "." + s[s.length - 1]);
				}
			}
		}
	}

	public void initImgTeacher() {
		TeacherInfoDatabase database = new TeacherInfoDatabase();
		List<TeacherInfoModel> teacherInfoModels = database.query_all();
		if (teacherInfoModels != null && teacherInfoModels.size() > 0) {
			for (TeacherInfoModel model : teacherInfoModels) {
				if (!TextUtils.isEmpty(model.zp)) {
					String[] s = model.zp.split("\\.");
					downLoadImg(model.zp, PATH_ROOT + PATH_PICTURE + "/teacher_" + model.rybh + "." + s[s.length - 1]);
				}
			}
		}
	}

	public void initImgAttend() {
		AttendInfoDatabase attendInfoDatabase = new AttendInfoDatabase();
		List<AttendInfoModel> attendInfoModels = attendInfoDatabase.query_all();
		if (attendInfoModels != null && attendInfoModels.size() > 0) {
			for (AttendInfoModel model : attendInfoModels) {
				if (!TextUtils.isEmpty(model.zp)) {
					String[] s = model.zp.split("\\.");
					downLoadImg(model.zp, PATH_ROOT + PATH_PICTURE + "/attend_" + model.id + "." + s[s.length - 1]);
				}
			}
		}
	}

	public void initImgTelMenu() {
		TelMenuDatabase telMenuDatabase = new TelMenuDatabase();
		List<TelMenuInfoModel> telMenuInfoModels = telMenuDatabase.query_all();
		if (telMenuInfoModels != null && telMenuInfoModels.size() > 0) {
			for (TelMenuInfoModel model : telMenuInfoModels) {
				if (!TextUtils.isEmpty(model.menuIcon)) {
					String[] s = model.menuIcon.split("\\.");
					downLoadImg(model.menuIcon,
							PATH_ROOT + PATH_PICTURE + "/talkIcon_" + model.menuId + "." + s[s.length - 1]);
				}
			}
		}
	}

	public void initImgPrize() {
		PrizeInfoDatabase prizeInfoDatabase = new PrizeInfoDatabase();
		List<PrizeInfoModel> prizeInfoModels = prizeInfoDatabase.query_all();
		if (prizeInfoModels != null && prizeInfoModels.size() > 0) {
			for (PrizeInfoModel model : prizeInfoModels) {
				if (!TextUtils.isEmpty(model.iconUrl)) {
					String[] s = model.iconUrl.split("/");
					downLoadImg(model.iconUrl, PATH_ROOT + PATH_PICTURE + "/prizeIcon_" + s[s.length - 1]);
				}
			}
		}
	}

	public void initImgSatr() {
		StarDatabase starDatabase = new StarDatabase();
		List<StarModel> starModels = starDatabase.query(null, null);
		if (starModels != null && starModels.size() > 0) {
			for (StarModel model : starModels) {
				if (!TextUtils.isEmpty(model.iconUrl)) {
					String[] s = model.iconUrl.split("/");
					downLoadImg(model.iconUrl, PATH_ROOT + PATH_PICTURE + "/star_" + s[s.length - 1]);
				}
			}
		}
	}

	public void initImgPic() {
		PicDreOrActivityDatabase dreOrActivityDatabase = new PicDreOrActivityDatabase();
		List<PicDreOrActivityModel> dreOrActivityModels = dreOrActivityDatabase.query(null, null);
		if (dreOrActivityModels != null && dreOrActivityModels.size() > 0) {
			for (PicDreOrActivityModel model : dreOrActivityModels) {
				if (!TextUtils.isEmpty(model.objectIcon)) {
					String[] s = model.objectIcon.split("/");
					downLoadImg(model.objectIcon, PATH_ROOT + PATH_PHOTO + "/" + s[s.length - 1]);
				}
				if (!TextUtils.isEmpty(model.objectEntityAddress)) {
					String[] s = model.objectEntityAddress.split("/");
					downLoadImg(model.objectEntityAddress, PATH_ROOT + PATH_PHOTO + "/" + s[s.length - 1]);
				}
			}
		}
	}

	public void initImgQuest() {
		QuestionNaireDatabase questionNaireDatabase = new QuestionNaireDatabase();
		List<QuestionNaireModel> questionNaireModels = questionNaireDatabase.query_all();
		if (questionNaireModels != null && questionNaireModels.size() > 0) {
			for (QuestionNaireModel model : questionNaireModels) {
				if (!TextUtils.isEmpty(model.questionnaireIconAddress)) {
					String[] s = model.questionnaireIconAddress.split("/");
					downLoadImg(model.questionnaireIconAddress, PATH_ROOT + PATH_PICTURE + "/quest_" + s[s.length - 1]);
				}
			}
		}
	}

	public void initImgVote() {
		VoteThemeDatabase voteThemeDatabase = new VoteThemeDatabase();
		List<VoteThemeModel> voteThemeModels = voteThemeDatabase.query(null, null);
		if (voteThemeModels != null && voteThemeModels.size() > 0) {
			for (VoteThemeModel model : voteThemeModels) {
				if (!TextUtils.isEmpty(model.themeIcon)) {
					String[] s = model.themeIcon.split("/");
					downLoadImg(model.themeIcon, PATH_ROOT + PATH_PICTURE + "/vote_" + s[s.length - 1]);
				}
				if (!TextUtils.isEmpty(model.themeIconTitle)) {
					String[] s = model.themeIconTitle.split("/");
					downLoadImg(model.themeIconTitle, PATH_ROOT + PATH_PICTURE + "/vote_" + s[s.length - 1]);
				}
			}
		}
	}

	private void initImgDiyMenu() {
		DiyMenuDatabase database = new DiyMenuDatabase();
		List<DiyMenuModel> list = database.query_all();
		if (list != null && list.size() > 0) {
			for (DiyMenuModel model : list) {
				if (!TextUtils.isEmpty(model.iconAddress)) {
					String[] s = model.iconAddress.split("/");
					downLoadImg(model.iconAddress, PATH_ROOT + PATH_PICTURE + "/diyMenuIcon_" + s[s.length - 1]);
				}
			}
		}
	}

	private void downLoadPlay(final String path, final int i, final String sn) {
		Log.d(TAG, "start downLoad Play " + i);
		if (TextUtils.isEmpty(path)) {
			downLoadPlayError(i, sn);
			return;
		}
		String[] s = path.split("/");
		File file = new File(PATH_ROOT + PATH_PLAYTASK + "/" + s[s.length - 1]);
		if (file.exists()) {
			downLoadPlayFinish(i, sn);
			return;
		}
		executoService.execute(new Runnable() {
			@Override
			public void run() {
				String[] s = path.split("/");
				if (path.startsWith("http")) {
					if (httpDownLoad(path, PATH_ROOT + PATH_PLAYTASK + "/" + s[s.length - 1])) {
						downLoadPlayFinish(i, sn);
					} else {
						downLoadPlayError(i, sn);
					}
				} else if (path.startsWith("ftp")) {
					FTPManager ftpManager = new FTPManager();
					if (ftpManager.connect(path, PATH_ROOT + PATH_PLAYTASK + "/" + s[s.length - 1])) {
						if (ftpManager.download()) {
							downLoadPlayFinish(i, sn);
						} else {
							downLoadPlayError(i, sn);
						}
						ftpManager.disConnect();
					} else {
						downLoadPlayError(i, sn);
					}
				} else {
					downLoadPlayError(i, sn);
				}
			}
		});
	}

	private void downLoadPlayError(int i, String sn) {
		playTaskDownload_count--;
		PlayTaskDatabase database = new PlayTaskDatabase();
		database.delete(playTaskModels.get(i).taskId);
		if (playTaskDownload_count == 0) {
			downLoadPlayFinish(sn);
		}
	}

	private void downLoadPlayFinish(int i, String sn) {
		playTaskDownload_count--;
		if (playTaskDownload_count == 0) {
			downLoadPlayFinish(sn);
		}
	}

	private void downLoadVideo(final String urlStr, final String path) {
		if (TextUtils.isEmpty(urlStr)) {
			downLoadVideoError();
			return;
		}
		final File file = new File(path);
		if (file.exists()) {
			downLoadVideoFinish();
			return;
		}
		executoService.execute(new Runnable() {
			@Override
			public void run() {
				FTPManager ftpManager = new FTPManager();
				if (ftpManager.connect(urlStr, path)) {
					if (ftpManager.download()) {
						ftpManager.disConnect();
						downLoadVideoFinish();
					} else {
						downLoadVideoError();
					}
				} else {
					downLoadVideoError();
				}
			}
		});
	}

	private void downLoadVideoError() {
		VideoDatabase database = new VideoDatabase();
		database.delete_by_id(vedioInfoModels.get(vedioInfoModels_i).id);
		vedioInfoModels_i++;
		handler_updata.sendEmptyMessage(4);
	}

	private void downLoadVideoFinish() {
		vedioInfoModels_i++;
		handler_updata.sendEmptyMessage(4);
	}

	public String getRunningActivityName() {
		String runningActivity = "";
		try {
			ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
		} catch (SecurityException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return runningActivity;
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ConstData.ACTIVITY_UPDATE)) {
				if (isDownloadPlay) {
					Log.d(TAG, "------------ConstData.ACTIVITY_UPDATE-----------");
					boolean result = intent.getBooleanExtra(CMD_RESULT, false);
					if (!result) {
						PlayTaskDatabase database = new PlayTaskDatabase();
						database.delete(maps_playTask.get(maps_playTask_count).get("taskId"));
					}
					maps_playTask_count++;
					if (maps_playTask_count < maps_playTask.size()) {
						systemManager.downloadPolicy(maps_playTask.get(maps_playTask_count).get("srcPath"));
					}
					playTaskDownload_count--;
					if (playTaskDownload_count == 0) {
						downLoadPlayFinish(playTask_sn);
					}
				}
			}
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals(HttpProcess.GET_VERSION)) {
				boolean result = intent.getBooleanExtra(CMD_RESULT, false);
				String msg = intent.getStringExtra(CMD_MSG);
				String sn = intent.getStringExtra(CMD_SN);
				if (result) {
					List<ArtifactVerType> verList = null;
					try {
						verList = JSONArray.parseArray(intent.getStringExtra("artifactList"), ArtifactVerType.class);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					if (verList != null && verList.size() > 0) {
						doVersion(verList, sn, intent.getStringExtra("subject"));
					} else {
						isDownloadAPP = false;
						httpProcess.commitBack(sn, HttpProcess.NONE, HttpProcess.NONE_MSG);
					}
				} else {
					isDownloadAPP = false;
					showToast("更新应用失败，原因：" + msg);
					httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
				}
			} else if (tag.equals(HttpProcess.QRY_DATATIME) && isSynComplete
					&& !getRunningActivityName().equals("com.yy.doorplate.activity.EmptyActivity")) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (isUpdateEQ) {
					httpProcess.QryEqu(sn);
					if (!result) {
						updateEQError = "同步时间失败;";
						showToast("同步时间失败，原因：" + msg);
					}
				} else {
					if (result) {
						showToast("同步时间成功");
						handler_touch.removeMessages(0);
						handler_touch.sendEmptyMessageDelayed(0, screensaver_time * 1000);
						httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
					} else {
						showToast("同步时间失败，原因：" + msg);
						httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
					}
				}
			} else if (tag.equals(HttpProcess.COMMIT_CONTROL)) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (result) {
					httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
				} else {
					httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
				}
			} else if (tag.equals(HttpProcess.COMMIT_EQUWORKINFO)) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (result) {
					httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
				} else {
					httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
				}
			} else if (tag.equals(HttpProcess.QRY_EQU) && isSynComplete && isUpdateEQ) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				String networkType = intent.getStringExtra("networkType");
				if ("STATIC".equals(networkType)) {
					showDialog("设置静态IP...", 60);
					httpProcess.setIP(equInfoModel.ip, equInfoModel.gateway, equInfoModel.subMask, equInfoModel.dns,
							equInfoModel.dns2, sn);
				} else {
					showDialog("查询教室信息...", 60);
					httpProcess.QryClassRoom(equInfoModel.jssysdm, sn);
				}
				handler_touch.removeMessages(0);
				if (result) {
					Log.d(TAG, tag + " " + msg);
				} else {
					Log.e(TAG, tag + " " + msg);
					updateEQError = updateEQError + "查询设备信息失败;";
					showToast("查询设备信息失败，原因：" + msg);
				}
			} else if (tag.equals(HttpProcess.EQUIPMENT_REGISTER) && isSynComplete && isUpdateEQ) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String networkType = intent.getStringExtra("networkType");
				if (result) {
					Log.d(TAG, tag + " " + msg);
					if ("STATIC".equals(networkType)) {
						showDialog("设置静态IP...", 60);
						httpProcess.setIP(equInfoModel.ip, equInfoModel.gateway, equInfoModel.subMask, equInfoModel.dns,
								equInfoModel.dns2, null);
					} else {
						showDialog("查询教室信息...", 60);
						httpProcess.QryClassRoom(equInfoModel.jssysdm, null);
					}
					handler_touch.removeMessages(0);
				} else {
					Log.e(TAG, tag + " " + msg);
					isUpdateEQ = false;
					handler_touch.sendEmptyMessageDelayed(0, screensaver_time * 1000);
					showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.SET_IP) && isSynComplete && isUpdateEQ) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				showDialog("查询教室信息...", 60);
				httpProcess.QryClassRoom(equInfoModel.jssysdm, sn);
				if (result) {
					Log.d(TAG, tag + " " + msg);
				} else {
					Log.e(TAG, tag + " " + msg);
					updateEQError = updateEQError + "设置静态IP失败;";
					showToast("设置静态IP失败，原因：" + msg);
				}
			} else if (tag.equals(HttpProcess.QRY_CLASS_DEVICE) && isSynComplete && isUpdateEQ) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (!TextUtils.isEmpty(equInfoModel.bjdm)) {
					showDialog("查询班级信息...", 60);
					httpProcess.QryStudent(equInfoModel.bjdm, "", sn, true);
				} else {
					showDialog("查询教师信息...", 60);
					httpProcess.QryTeacher(equInfoModel.jssysdm, "", sn);
				}
				httpProcess.QryLogo(null);
				httpProcess.QryScreensaver();
				httpProcess.getWeather(equInfoModel.jssysdm);
				httpProcess.qryUser(null);
				httpProcess.QryBook(equInfoModel.jssysdm, "0", "25", "", true, null);
				httpProcess.qryCarouse();
				if (result) {
					Log.d(TAG, tag + " " + msg);
				} else {
					Log.e(TAG, tag + " " + msg);
					updateEQError = updateEQError + "查询教室信息失败;";
					showToast("查询教室信息失败，原因：" + msg);
				}
			} else if (tag.equals(HttpProcess.QRY_STUDENT) && isSynComplete && isUpdateEQ) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				initImgStudent();
				showDialog("查询教师信息...", 60);
				httpProcess.QryTeacher(equInfoModel.jssysdm, "", sn);
				if (result) {
					Log.d(TAG, tag + " " + msg);
				} else {
					Log.e(TAG, tag + " " + msg);
					updateEQError = updateEQError + "查询班级信息失败;";
					showToast("查询班级信息失败，原因：" + msg);
				}
			} else if (tag.equals(HttpProcess.QRY_TEACHER) && isSynComplete && isUpdateEQ) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				initImgTeacher();
				showDialog("查询课程信息...", 60);
				httpProcess.QryCurriculum(equInfoModel.jssysdm, kxsj, jssj, "", true, sn);
				if (result) {
					Log.d(TAG, tag + " " + msg);
				} else {
					Log.e(TAG, tag + " " + msg);
					updateEQError = updateEQError + "查询教师信息失败;";
					showToast("查询教师信息失败，原因：" + msg);
				}
			} else if (tag.equals(HttpProcess.QRY_CURRICULUM) && isSynComplete && isUpdateEQ) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				CurriculumInfoDatabase database = new CurriculumInfoDatabase();
				curriculumInfos_today = database.query_by_skrq(MyApplication.getTime("yyyy-MM-dd"));
				if (curriculumInfos_today != null && curriculumInfos_today.size() > 0) {
					showDialog("查询考勤信息...", 180);
					curriculumInfos_today_id = 0;
					httpProcess.QryAttend(equInfoModel.jssysdm,
							curriculumInfos_today.get(curriculumInfos_today_id++).id, sn);
				} else {
					showDialog("查询通知信息...", 60);
					httpProcess.QryNoticePJ(equInfoModel.jssysdm, "0", MyApplication.NOTICE_PAGE_COUNT,
							equInfoModel.bjdm, true, sn);
				}
				if (result) {
					Log.d(TAG, tag + " " + msg);
				} else {
					Log.e(TAG, tag + " " + msg);
					updateEQError = updateEQError + "查询课程信息失败;";
					showToast("查询课程信息失败，原因：" + msg);
				}
			} else if (tag.equals(HttpProcess.QRY_ATTEND) && isSynComplete && isUpdateEQ && !isQryAttend
					&& !isQryAttendTCP) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (curriculumInfos_today_id < curriculumInfos_today.size()) {
					httpProcess.QryAttend(equInfoModel.jssysdm,
							curriculumInfos_today.get(curriculumInfos_today_id++).id, sn);
				} else {
					initImgAttend();
					showDialog("查询通知信息...", 60);
					httpProcess.QryNoticePJ(equInfoModel.jssysdm, "0", MyApplication.NOTICE_PAGE_COUNT,
							equInfoModel.bjdm, true, sn);
				}
				if (result) {
					Log.d(TAG, tag + " " + msg + " " + curriculumInfos_today_id);
				} else {
					Log.e(TAG, tag + " " + msg);
					updateEQError = updateEQError + "查询考勤信息失败;";
					showToast("查询考勤信息失败，原因：" + msg);
				}
			} else if (tag.equals(HttpProcess.QRY_NOTICE) && isSynComplete && isUpdateEQ) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				showDialog("查询其他设备列表...", 60);
				httpProcess.QryEquList(equInfoModel.jssysdm, sn);
				if (result) {
					Log.d(TAG, tag + " " + msg);
				} else {
					Log.e(TAG, tag + " " + msg);
					updateEQError = updateEQError + "查询通知信息失败;";
					showToast("查询通知信息失败，原因：" + msg);
				}
			} else if (tag.equals(HttpProcess.QRY_EQU_LIST) && isSynComplete) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				initImgTelMenu();
				if (isUpdateEQ) {
					showDialog("查询荣誉墙信息...", 60);
					httpProcess.QryPrize(equInfoModel.jssysdm, "", sn, true);
					if (result) {
						Log.d(TAG, tag + " " + msg);
					} else {
						Log.e(TAG, tag + " " + msg);
						updateEQError = updateEQError + "查询其他设备列表失败;";
						showToast("查询其他设备列表失败，原因：" + msg);
					}
				} else {
					if (result) {
						Log.d(TAG, tag + " " + msg);
						showToast("更新其它设备列表成功");
						httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
					} else {
						Log.e(TAG, tag + " " + msg);
						showToast("更新其它设备列表失败，原因:" + msg);
						httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_PLAY_TASK)) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					doPlayTask(sn);
				} else {
					Log.e(TAG, tag + " " + msg);
					playTaskModels = null;
					isDownloadPlay = false;
					showToast("下载播放文件失败，原因：" + msg);
					httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
					handler_touch.sendEmptyMessageDelayed(0, screensaver_time * 1000);
				}
			} else if (tag.equals(HttpProcess.QRY_PRIZE) && isSynComplete && !isGrxx) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				initImgPrize();
				if (isUpdateEQ) {
					showDialog("查询班级之星...", 60);
					httpProcess.QryStar(equInfoModel.jssysdm, equInfoModel.bjdm, "0", "1000", sn);
					if (result) {
						Log.d(TAG, tag + " " + msg);
					} else {
						Log.e(TAG, tag + " " + msg);
						updateEQError = updateEQError + "查询荣誉墙信息失败;";
						showToast("查询荣誉墙信息失败，原因：" + msg);
					}
				} else {
					if (result) {
						Log.d(TAG, tag + " " + msg);
						showToast("更新荣誉墙成功");
						httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
					} else {
						Log.e(TAG, tag + " " + msg);
						showToast("更新荣誉墙失败，原因:" + msg);
						httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_STAR) && isSynComplete) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				initImgSatr();
				if (isUpdateEQ) {
					showDialog("查询小黑板信息...", 60);
					httpProcess.QryBlackboard(equInfoModel.jssysdm, sn);
					if (result) {
						Log.d(TAG, tag + " " + msg);
					} else {
						Log.e(TAG, tag + " " + msg);
						updateEQError = updateEQError + "查询班级之星失败;";
						showToast("查询班级之星失败，原因：" + msg);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_BLACKBOARD) && isSynComplete) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (isUpdateEQ) {
					showDialog("查询值日信息...", 60);
					httpProcess.QryOnDuty(equInfoModel.jssysdm, sn);
					if (result) {
						Log.d(TAG, tag + " " + msg);
					} else {
						Log.e(TAG, tag + " " + msg);
						updateEQError = updateEQError + "查询小黑板失败;";
						showToast("查询小黑板失败，原因：" + msg);
					}
				} else {
					if (result) {
						showToast("更新小黑板成功");
						httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
					} else {
						showToast("更新小黑板失败，原因:" + msg);
						httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_ONDUTY) && isSynComplete) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (isUpdateEQ) {
					showDialog("查询学校信息...", 60);
					httpProcess.QrySchool(equInfoModel.jssysdm, sn);
					if (result) {
						Log.d(TAG, tag + " " + msg);
					} else {
						Log.e(TAG, tag + " " + msg);
						updateEQError = updateEQError + "查询值日信息失败;";
						showToast("查询值日信息失败，原因：" + msg);
					}
				} else {
					if (result) {
						showToast("更新值日信息成功");
						httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
					} else {
						showToast("更新值日信息失败，原因:" + msg);
						httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_SCHOOL) && isSynComplete) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (isUpdateEQ) {
					showDialog("查询问卷调查信息...", 60);
					httpProcess.QryQuest(equInfoModel.jssysdm, "", sn);
					if (result) {
						Log.d(TAG, tag + " " + msg);
					} else {
						Log.e(TAG, tag + " " + msg);
						updateEQError = updateEQError + "查询学校信息失败;";
						showToast("查询学校信息失败，原因：" + msg);
					}
				} else {
					if (result) {
						showToast("更新校园文化成功");
						httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
					} else {
						showToast("更新校园文化失败，原因:" + msg);
						httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_QUEST) && isSynComplete) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				initImgQuest();
				if (isUpdateEQ) {
					showDialog("查询投票评比信息...", 60);
					httpProcess.QryVote("0", MyApplication.NOTICE_PAGE_COUNT, "", "vote", "vote", sn);
					if (result) {
						Log.d(TAG, tag + " " + msg);
					} else {
						Log.e(TAG, tag + " " + msg);
						updateEQError = updateEQError + "查询问卷调查信息失败;";
						showToast("查询问卷调查信息失败，原因：" + msg);
					}
				} else {
					if (result) {
						showToast("更新问卷调查成功");
						httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
					} else {
						showToast("更新问卷调查失败，原因:" + msg);
						httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_VOTE + "_vote") && isSynComplete) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (isUpdateEQ) {
					showDialog("查询活动报名信息...", 60);
					httpProcess.QryVote("0", MyApplication.NOTICE_PAGE_COUNT, "", "activity", "activity", sn);
					if (result) {
						Log.d(TAG, tag + " " + msg);
					} else {
						Log.e(TAG, tag + " " + msg);
						updateEQError = updateEQError + "查询投票评比信息失败;";
						showToast("查询投票评比信息失败，原因：" + msg);
					}
				} else {
					if (result) {
						Log.d(TAG, tag + " " + msg);
						showToast("更新投票信息成功");
						httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
					} else {
						Log.e(TAG, tag + " " + msg);
						showToast("更新投票信息失败，原因:" + msg);
						httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_VOTE + "_activity") && isSynComplete) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				initImgVote();
				if (isUpdateEQ) {
					showDialog("查询考勤教师信息...", 60);
					httpProcess.QryTeacherList(true, sn);
					if (result) {
						Log.d(TAG, tag + " " + msg);
					} else {
						Log.e(TAG, tag + " " + msg);
						updateEQError = updateEQError + "查询活动报名信息失败;";
						showToast("查询活动报名信息失败，原因：" + msg);
					}
				} else {
					if (result) {
						Log.d(TAG, tag + " " + msg);
						showToast("更新活动信息成功");
						httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
					} else {
						Log.e(TAG, tag + " " + msg);
						showToast("更新活动信息失败，原因:" + msg);
						httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_TEACHERLIST) && isSynComplete) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (isUpdateEQ) {
					showDialog("查询考勤规则信息...", 60);
					httpProcess.QryClockRule(sn);
					if (result) {
						Log.d(TAG, tag + " " + msg);
					} else {
						Log.e(TAG, tag + " " + msg);
						updateEQError = updateEQError + "查询考勤教师信息失败;";
						showToast("查询考勤教师信息失败，原因：" + msg);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_CLOCKRULE) && isSynComplete) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (isUpdateEQ) {
					showDialog("查询人员考勤信息...", 60);
					httpProcess.QryPersonAttend(sn, MyApplication.getTime("yyyy-MM-dd"), "");
					if (result) {
						Log.d(TAG, tag + " " + msg);
					} else {
						Log.e(TAG, tag + " " + msg);
						updateEQError = updateEQError + "查询考勤规则信息失败;";
						showToast("查询考勤规则信息失败，原因：" + msg);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_PERSONATTEND) && isSynComplete
					&& !getRunningActivityName().equals("com.yy.doorplate.activity.EmptyActivity") && !isGrxx) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (isUpdateEQ) {
					setToday(getTime("yyyyMMdd"));
					showDialog("查询相册信息...", 0);
					httpProcess.QryPhoto(equInfoModel.jssysdm, equInfoModel.bjdm, sn);
					if (result) {
						Log.d(TAG, tag + " " + msg);
					} else {
						Log.e(TAG, tag + " " + msg);
						updateEQError = updateEQError + "查询考人员考勤信息失败;";
						showToast("查询考人员考勤信息失败，原因：" + msg);
					}
				} else {
					if (result) {
						setToday(getTime("yyyyMMdd"));
					}
				}
			} else if (tag.equals(HttpProcess.QRY_PHOTO) && isSynComplete) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				initImgPic();
				if (isUpdateEQ) {
					// showDialog("获取小视频信息...", 300);
					// httpProcess.qryVideo(sn, equInfoModel.jssysdm, "bjdm",
					// equInfoModel.bjdm, equInfoModel.bjdm);
					if (result) {
						Log.d(TAG, tag + " " + msg);
					} else {
						Log.e(TAG, tag + " " + msg);
						updateEQError = updateEQError + "查询相册信息失败;";
						showToast("查询相册信息失败，原因：" + msg);
					}
					Finish(sn);
				} else {
					if (result) {
						Log.d(TAG, tag + " " + msg);
						showToast("更新相册成功");
						httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
					} else {
						Log.e(TAG, tag + " " + msg);
						showToast("更新相册失败，原因:" + msg);
						httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_VIDEO) && isSynComplete && !isGrxx) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				vedioSN = sn;
				if (isUpdateEQ) {
					if (result) {
						Log.d(TAG, tag + " " + msg);
					} else {
						Log.e(TAG, tag + " " + msg);
						updateEQError = updateEQError + "查询小视频信息失败;";
						showToast("查询小视频信息失败，原因：" + msg);
					}
					VideoDatabase database = new VideoDatabase();
					vedioInfoModels = database.query("relaObjType = ? and relaObjValue = ?",
							new String[]{"bjdm", equInfoModel.bjdm});
					vedioInfoModels_i = 0;
					if (vedioInfoModels != null && vedioInfoModels.size() > 0) {
						handler_updata.sendEmptyMessage(4);
					} else {
						Finish(sn);
					}
				} else {
					if (result) {
						Log.d(TAG, tag + " " + msg);
						VideoDatabase database = new VideoDatabase();
						vedioInfoModels = database.query("relaObjType = ? and relaObjValue = ?",
								new String[]{"bjdm", equInfoModel.bjdm});
						vedioInfoModels_i = 0;
						if (vedioInfoModels != null && vedioInfoModels.size() > 0) {
							handler_updata.sendEmptyMessage(4);
						} else {
							httpProcess.commitBack(sn, HttpProcess.NONE, HttpProcess.NONE_MSG);
						}
					} else {
						httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_UPDATE_USER) && isSynComplete && !isUpdateEQ) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					showToast("更新授权用户成功");
					httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
				} else {
					Log.e(TAG, tag + " " + msg);
					showToast("更新授权用户失败，原因:" + msg);
					httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
				}
			} else if (tag.equals(HttpProcess.QRY_NEARATTEND)) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String currentTime = intent.getStringExtra("currentTime");
				String currentPosition = intent.getStringExtra("currentPosition");
				String nextTime = intent.getStringExtra("nextTime");
				String nextPosition = intent.getStringExtra("nextPosition");
				if (result) {
					if (dialog_attend != null && dialog_attend.isShowing() && txt_guangbo_msg != null) {
						String s = txt_guangbo_msg.getText().toString();
						if (TextUtils.isEmpty(currentTime)) {
							s = s + "\n下一次考勤时段:\n" + nextTime + "\n下一次考勤地点:\n" + nextPosition;
						} else {
							s = s + "\n当前考勤时段:" + currentTime + "\n当前考勤地点:" + currentPosition + "\n下一次考勤时段:" + nextTime
									+ "\n下一次考勤地点:" + nextPosition;
						}
						txt_guangbo_msg.setTextSize(35);
						txt_guangbo_msg.setText(s);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_DIYMENU)) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				if (result) {
					initImgDiyMenu();
				}
			} else if (tag.equals(HttpProcess.QRY_LOGO)) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				String logoUrl = intent.getStringExtra("logoUrl");
				if (result) {
					downLoadImgLogo(logoUrl, sn);
				} else {
					showToast("Logo加载失败，原因:" + msg);
					httpProcess.commitBack(sn, HttpProcess.ERROR, msg);
				}
			} else if (tag.equals(HttpProcess.QRY_SCREENSAVER)) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				if (result) {
					ScreensaverDatabase database = new ScreensaverDatabase();
					List<ScreensaverModel> list = database.query_all();
					if (list != null && list.size() > 0) {
						for (ScreensaverModel model : list) {
							if (!TextUtils.isEmpty(model.url)) {
								String[] s = model.url.split("/");
								downLoadImg(model.url, PATH_ROOT + PATH_SCREENSAVER + "/" + s[s.length - 1]);
							}
						}
					}
				}
			} else if (tag.equals(HttpProcess.COMMIT_CLOCKINFO2)) {
				boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				closeProgressDialog();
				if (result) {
					String personAttendInfo = intent.getStringExtra("personAttendInfo");
					String clockInfo = intent.getStringExtra("clockInfo");
//					if (!TextUtils.isEmpty(personAttendInfo) && !TextUtils.isEmpty(clockInfo)) {
//						PersonnelAttendanceModel personnelAttendanceModel = JSON.parseObject(personAttendInfo,
//								PersonnelAttendanceModel.class);
//						ClockInfoModel clockInfoModel = JSON.parseObject(clockInfo, ClockInfoModel.class);
//						if (personnelAttendanceModel == null) {
//							return;
//						}
//						if (TextUtils.isEmpty(personnelAttendanceModel.status)) {
//							return;
//						}
//						String[] strings = personnelAttendanceModel.status.split(",");
//						if (!"4".equals(strings[0]) && strings.length > 1 && "5".equals(strings[1])) {
//							showAttend("5", clockInfoModel, "早退");
//						} else if (!"4".equals(strings[0]) && strings.length > 1 && "12".equals(strings[1])) {
//							showAttend("0", clockInfoModel, "已签退");
//						} else if ("0".equals(strings[0])) {
//							showAttend("0", clockInfoModel, "已签到");
//						} else if ("3".equals(strings[0])) {
//							showAttend("3", clockInfoModel, "迟到");
//						} else if ("4".equals(strings[0])) {
//							showAttend("4", clockInfoModel, "旷课");
//						}
					showAttend(1, "打卡成功", msg);
//					}
				} else {
					String code = intent.getStringExtra("code");
					if(code!=null&&code!=""){
						if (code.equals("9999")) {
							showAttend(2, "打卡成功", msg);
						} else if (code.equals("9001")) {
							showAttend(2, msg, "");
						} else {
							showAttendError(msg);
						}
					}

				}

			}
		}
	}

	private void Finish(String sn) {
		curriculumInfoModel_now = findCurriculum(10);
		tcpProcess.reStart(equInfoModel.accSysIp, equInfoModel.accSysPort);
		handler_touch.sendEmptyMessageDelayed(0, screensaver_time * 1000);
		Intent i = new Intent(MyApplication.BROADCAST);
		i.putExtra(MyApplication.BROADCAST_TAG, "updataEQ");
		broadcastManager.sendBroadcast(i);
		if (TextUtils.isEmpty(updateEQError)) {
			httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
		} else {
			httpProcess.commitBack(sn, HttpProcess.ERROR, updateEQError);
			updateEQError = null;
		}
		isUpdateEQ = false;
		stopDialog();
	}

	private void downLoadImgLogo(final String urlStr, final String sn) {
		if (TextUtils.isEmpty(urlStr)) {
			setLogoUrl(null);
			Intent intent = new Intent(MyApplication.BROADCAST);
			intent.putExtra(MyApplication.BROADCAST_TAG, "logo_update");
			broadcastManager.sendBroadcast(intent);
			httpProcess.commitBack(sn, HttpProcess.NONE, HttpProcess.NONE_MSG);
			return;
		}
		if ("null".equals(urlStr)) {
			setLogoUrl(null);
			Intent intent = new Intent(MyApplication.BROADCAST);
			intent.putExtra(MyApplication.BROADCAST_TAG, "logo_update");
			broadcastManager.sendBroadcast(intent);
			httpProcess.commitBack(sn, HttpProcess.NONE, HttpProcess.NONE_MSG);
			return;
		}
		String[] s = urlStr.split("/");
		final String path = PATH_ROOT + PATH_LOGO + "/" + s[s.length - 1];
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		executoService.execute(new Runnable() {
			@Override
			public void run() {
				if (urlStr.startsWith("ftp")) {
					FTPManager ftpManager = new FTPManager();
					if (ftpManager.connect(urlStr, path)) {
						if (ftpManager.download()) {
							setLogoUrl(path);
							Intent intent = new Intent(MyApplication.BROADCAST);
							intent.putExtra(MyApplication.BROADCAST_TAG, "logo_update");
							broadcastManager.sendBroadcast(intent);
							httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
						} else {
							// showToast("Logo加载失败");
							httpProcess.commitBack(sn, HttpProcess.ERROR, "下载失败");
						}
						ftpManager.disConnect();
					}
				} else {
					if (httpDownLoad(urlStr, path)) {
						setLogoUrl(path);
						Intent intent = new Intent(MyApplication.BROADCAST);
						intent.putExtra(MyApplication.BROADCAST_TAG, "logo_update");
						broadcastManager.sendBroadcast(intent);
						httpProcess.commitBack(sn, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
					} else {
						// showToast("Logo加载失败");
						httpProcess.commitBack(sn, HttpProcess.ERROR, "下载失败");
					}
				}
			}
		});
	}

	private int playTaskDownload_count = 0, maps_playTask_count = 0;
	private String playTask_sn;
	private List<Map<String, String>> maps_playTask = new ArrayList<Map<String, String>>();

	private void doPlayTask(String sn) {
		if (getRunningActivityName().equals("com.advertisement.activity.DisplayActivity")) {
			Intent intent = new Intent(MyApplication.BROADCAST);
			intent.putExtra(MyApplication.BROADCAST_TAG, "PlayActivity_finish");
			broadcastManager.sendBroadcast(intent);
		} else if (mediaPlayer != null && mediaPlayer.isPlaying() && dialog != null && dialog.isShowing()
				&& isGuangbo) {
			dialog.dismiss();
			if (!getRunningActivityName().equals("com.yy.doorplate.activity.CallActivity")) {
				mediaPlayer.stop();
				mediaPlayer.reset();
				closeVoice();
			}
		}
		isGuangbo = false;
		isGuanggao = false;
		playTask_position = -1;

		// File file = new File(PATH_ROOT + PATH_PLAYTASK);
		// File[] files = file.listFiles();
		// if (files != null && files.length > 0) {
		// for (File f : files) {
		// f.delete();
		// }
		// }
		maps_playTask_count = 0;
		maps_playTask.clear();
		if (playTaskModels != null && playTaskModels.size() > 0) {
			playTaskDownload_count = playTaskModels.size();
			for (int i = 0; i < playTaskModels.size(); i++) {
				if (!TextUtils.isEmpty(playTaskModels.get(i).srcPath)) {
					if (playTaskModels.get(i).srcType.equals("TEMPLATE")) {
						Map<String, String> map = new HashMap<String, String>();
						map.put("taskId", playTaskModels.get(i).taskId);
						map.put("srcPath", playTaskModels.get(i).srcPath);
						maps_playTask.add(map);
					} else {
						downLoadPlay(playTaskModels.get(i).srcPath, i, sn);
					}
				}
			}
			if (maps_playTask.size() > 0) {
				systemManager.downloadPolicy(maps_playTask.get(maps_playTask_count).get("srcPath"));
			}
		} else {
			playTaskModels = null;
			isDownloadPlay = false;
			httpProcess.commitBack(sn, HttpProcess.NONE, HttpProcess.NONE_MSG);
			handler_touch.sendEmptyMessageDelayed(0, screensaver_time * 1000);
		}
	}

	// 播放任务下载完成
	private void downLoadPlayFinish(String sn) {
		Log.d(TAG, "-------downLoadPlayFinish-------");
		isDownloadPlay = false;
		maps_playTask_count = 0;
		maps_playTask.clear();
		Bundle bundle = new Bundle();
		bundle.putString("sn", sn);
		bundle.putString("id", HttpProcess.SUCESS);
		bundle.putString("msg", HttpProcess.SUCESS_MSG);
		Message msg = Message.obtain();
		msg.what = 11;
		msg.obj = bundle;
		handler_updata.sendMessage(msg);
		PlayTaskDatabase playTaskDatabase = new PlayTaskDatabase();
		playTaskModels = playTaskDatabase.query_all();
		handler_touch.removeMessages(0);
		handler_touch.sendEmptyMessageDelayed(0, screensaver_time * 1000);
		if (getRunningActivityName().equals("com.yy.doorplate.activity.CallActivity")) {
			return;
		}
		if (playTaskModels != null && playTaskModels.size() > 0) {
			for (int i = 0; i < playTaskModels.size(); i++) {
				final PlayTaskModel model = playTaskModels.get(i);
				if (model.playType.equals("RADIO") && model.taskType.equals("REALTIME")
						&& model.srcType.equals("AUDIO")) {
					String[] s = model.srcPath.split("/");
					String path = PATH_ROOT + PATH_PLAYTASK + "/" + s[s.length - 1];
					playTask_position = i;
					isGuangbo = true;
					try {
						mediaPlayer.stop();
						mediaPlayer.reset();
						mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
						mediaPlayer.setDataSource(path);
						mediaPlayer.setDisplay(null);
						mediaPlayer.setLooping(false);
						mediaPlayer.setOnErrorListener(new OnErrorListener() {

							@Override
							public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
								isGuangbo = false;
								if (playTask_position != -1 && playTaskModels.size() > playTask_position) {
									PlayTaskDatabase database = new PlayTaskDatabase();
									database.delete(playTaskModels.get(playTask_position).taskId);
									playTaskModels.remove(playTask_position);
									playTask_position = -1;
								}
								if (dialog != null && dialog.isShowing()) {
									dialog.dismiss();
								}
								return false;
							}
						});
						mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

							@Override
							public void onPrepared(MediaPlayer mediaPlayer) {
								Log.d(TAG, "downLoad Play start");
								mediaPlayer.start();
								showGuangbo(model.taskDesc, false);
							}
						});
						mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp) {
								isGuangbo = false;
								if (playTask_position != -1 && playTaskModels.size() > playTask_position) {
									PlayTaskDatabase database = new PlayTaskDatabase();
									database.delete(playTaskModels.get(playTask_position).taskId);
									playTaskModels.remove(playTask_position);
									playTask_position = -1;
								}
								if (dialog != null && dialog.isShowing()) {
									dialog.dismiss();
								}
							}
						});
						mediaPlayer.prepareAsync();
					} catch (Exception e) {
						e.printStackTrace();
						isGuangbo = false;
						if (playTask_position != -1 && playTaskModels.size() > playTask_position) {
							PlayTaskDatabase database = new PlayTaskDatabase();
							database.delete(playTaskModels.get(playTask_position).taskId);
							playTaskModels.remove(playTask_position);
							playTask_position = -1;
						}
						if (dialog != null && dialog.isShowing()) {
							dialog.dismiss();
						}
					}
					break;
				}
			}
		}
		Intent intent = new Intent(MyApplication.BROADCAST);
		intent.putExtra(MyApplication.BROADCAST_TAG, "playTask");
		broadcastManager.sendBroadcast(intent);
	}

	private Handler handler_updata = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0: {
					Bundle bundle = (Bundle) msg.obj;
					httpProcess.CommitEquWorkInfo(equInfoModel.jssysdm, bundle.getString("path"), bundle.getString("sn"));
					break;
				}
				case 1: {
					showDialog("正在更新设备信息...", 60);
					break;
				}
				case 2:
					// 文件下载进度
					if (progressDialog == null) {
						progressDialog = new ProgressDialog(MyApplication.this);
						progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						progressDialog.getWindow().setType(LayoutParams.TYPE_SYSTEM_ALERT);
						progressDialog.setCancelable(true);
						progressDialog.setCanceledOnTouchOutside(false);
						progressDialog.setTitle("下载中...");
						progressDialog.setMax(100 * downloadAPP_total);
						progressDialog.setOnDismissListener(new OnDismissListener() {

							@Override
							public void onDismiss(DialogInterface arg0) {
								progressDialog = null;
							}
						});
						progressDialog.setProgress(msg.arg1 + 100 * downloadAPP_avail);
						progressDialog.show();
					} else {
						progressDialog.setProgress(msg.arg1 + 100 * downloadAPP_avail);
					}
					break;
				case 3:
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
					break;
				case 4: {
					if (vedioInfoModels.size() > vedioInfoModels_i) {
						String[] s = vedioInfoModels.get(vedioInfoModels_i).resPath.split("/");
						String path = MyApplication.PATH_ROOT + MyApplication.PATH_VIDEO + "/" + s[s.length - 1];
						downLoadVideo(vedioInfoModels.get(vedioInfoModels_i).resPath, path);
					} else {
						vedioInfoModels = null;
						vedioInfoModels_i = 0;
						if (isUpdateEQ) {
							Finish(vedioSN);
						} else {
							httpProcess.commitBack(vedioSN, HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
						}
					}
					break;
				}
				case 10:
					showToast(msg.obj.toString());
					break;
				case 11:
					Bundle bundle = (Bundle) msg.obj;
					httpProcess.commitBack(bundle.getString("sn"), bundle.getString("id"), bundle.getString("msg"));
					break;
				case 12:
					if (dialog_hint.isShowing() && dialog_off_txt != null) {
						dialog_off_txt.setText(time_dialog + "秒后关机");
					}
					break;
				case 100: {
					if (curriculumInfoModel_now != null) {
						isQryAttendTCP = true;
						httpProcess.QryAttend(equInfoModel.jssysdm, curriculumInfoModel_now.id, null);
					} else if (curriculumInfoModel_next != null) {
						isQryAttendTCP = true;
						httpProcess.QryAttend(equInfoModel.jssysdm, curriculumInfoModel_next.id, null);
					}
					break;
				}
			}
		}
	};

	private Handler handler_network = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case NetworkState.STATE_NETWORK_DISABLED: {
					Log.i(TAG, "网络不可用");
					break;
				}
				case NetworkState.STATE_NETWORK_ENABLED: {
					Log.i(TAG, "网络可用");
					if (!TextUtils.isEmpty(today) && !today.equals(getTime("yyyyMMdd"))) {
					}
					break;
				}
				case NetworkState.STATE_CALL_ED: {
					Log.i(TAG, "被叫");
					break;
				}
				case NetworkState.STATE_CALL_IN: {
					Log.i(TAG, "通话中");
					break;
				}
				case NetworkState.STATE_CALL_END: {
					Log.i(TAG, "通话结束");
					break;
				}
				case NetworkState.STATE_CALL_WAIT: {
					Log.i(TAG, "主动呼叫，等待对方应答");
					break;
				}
				case NetworkState.STATE_CALL_BUSY: {
					Log.i(TAG, "对方返回应答，线路繁忙");
					break;
				}
				case NetworkState.STATE_CALL_REFUSE: {
					Log.i(TAG, "对方返回应答，拒绝接听");
					break;
				}
				case NetworkState.STATE_CALL_CANNOT: {
					Log.i(TAG, "对方返回应答，无法接听");
					break;
				}
				case NetworkState.STATE_CALL_ERROR: {
					Log.i(TAG, "呼叫异常");
					break;
				}
				case NetworkState.STATE_TIMEOUT: {
					Log.i(TAG, "通讯超时");
					break;
				}
				case NetworkState.STATE_CALL_IN_MONITOR: {
					Log.i(TAG, "监控通话中");
					break;
				}
			}
		}
	};

	public Handler handler_touch = new Handler() {
		public void handleMessage(Message msg) {
			// Log.d(TAG, "-------handler_touch------");
			if (msg.what == 0) {
				CarouselDatabase carouselDatabase = new CarouselDatabase();
				List<CarouselModel> carouselModels = carouselDatabase.query("modelType = ?",
						new String[]{CarouselModel.TYPE_SCREEN});
				if (screensaver_mode == SCREENSAVER_MODE_AD && isSynComplete && carouselModels != null
						&& carouselModels.size() > 0 && curriculumInfoModel_now == null) {
					Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, "permission_finish");
				broadcastManager.sendBroadcast(intent);
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				if (dialog_attend != null && dialog_attend.isShowing()) {
					dialog_attend.dismiss();
				}
				if (dialog_hint != null && dialog_hint.isShowing()) {
					dialog_hint.dismiss();
				}
			}
		}

		;
	};

	public String getFtpPath(String ftpUrl) {
		String tmp = ftpUrl.substring(6);
		int index = tmp.indexOf("/");
		return PATH_ADV + tmp.substring(index);
	}

	public TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			handler_touch.removeMessages(0);
		}

		@Override
		public void afterTextChanged(Editable s) {
			handler_touch.sendEmptyMessageDelayed(0, screensaver_time * 1000);
		}
	};

	private void playGuanggao(final PlayTaskModel model) {
		Log.d(TAG, model.toString());
		if (TextUtils.isEmpty(model.srcPath)) {
			return;
		}
		String[] s = model.srcPath.split("/");
		final String path = PATH_ROOT + PATH_PLAYTASK + "/" + s[s.length - 1];
		File file = new File(path);
		if (file.exists()) {
			isGuanggao = true;
			if (model.srcType.equals("IMAGE")) {
				Intent intent = new Intent(this, PlayActivity.class);
				intent.putExtra("path", path);
				intent.putExtra("type", "IMAGE");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} else if (model.srcType.equals("VIDEO")) {
				Intent intent = new Intent(this, PlayActivity.class);
				intent.putExtra("path", path);
				intent.putExtra("type", "VIDEO");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		}
	}

	public void getOFFONTime(long timeLong, String dateString) throws Exception {
		time_off = null;
		time_on = null;
		time_off_actual = null;
		if (list_off != null && list_off.size() > 0 && list_on != null && list_on.size() > 0) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			for (TimerTypeModel off : list_off) {
				if (off.timerType.equals("DATE") && !TextUtils.isEmpty(off.date) && !TextUtils.isEmpty(off.time)) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("off", off.date + " " + off.time);
					for (TimerTypeModel on : list_on) {
						if (on.timerType.equals("DATE") && !TextUtils.isEmpty(on.date) && !TextUtils.isEmpty(on.time)
								&& formatter.parse(on.date + " " + on.time).getTime() > formatter
								.parse(off.date + " " + off.time).getTime()) {
							map.put("on", on.date + " " + on.time);
							break;
						}
					}
					if (!TextUtils.isEmpty(map.get("on"))) {
						list.add(map);
					}
				}
			}
			if (list.size() > 0) {
				for (Map<String, String> map : list) {
					if (map.get("off").startsWith(dateString)
							&& timeLong < formatter.parse(map.get("on")).getTime() - 1000 * 60 * OFFON) {
						time_off = map.get("off");
						time_on = map.get("on");
						break;
					}
					if (formatter.parse(map.get("off")).getTime() <= timeLong
							&& timeLong < formatter.parse(map.get("on")).getTime() - 1000 * 60 * OFFON) {
						time_off = map.get("off");
						time_on = map.get("on");
						break;
					}
				}
			}
			if (TextUtils.isEmpty(time_off) || TextUtils.isEmpty(time_on)) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date(timeLong));
				int repeat = 0;
				switch (calendar.get(Calendar.DAY_OF_WEEK)) {
					case 1:
						repeat = 7;
						break;
					case 2:
						repeat = 1;
						break;
					case 3:
						repeat = 2;
						break;
					case 4:
						repeat = 3;
						break;
					case 5:
						repeat = 4;
						break;
					case 6:
						repeat = 5;
						break;
					case 7:
						repeat = 6;
						break;
				}
				for (TimerTypeModel model : list_on) {
					if (model.timerType.equals("REPEAT") && !TextUtils.isEmpty(model.time)
							&& !TextUtils.isEmpty(model.repeatWeek) && Integer.parseInt(model.repeatWeek) >= repeat) {
						String data = dateString;
						calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(model.repeatWeek) - repeat);
						data = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
						if (timeLong < formatter.parse(data + " " + model.time).getTime() - 1000 * 60 * OFFON) {
							time_on = data + " " + model.time;
							repeat = Integer.parseInt(model.repeatWeek);
							break;
						}
					}
				}
				if (TextUtils.isEmpty(time_on)) {
					for (TimerTypeModel model : list_on) {
						if (model.timerType.equals("REPEAT") && !TextUtils.isEmpty(model.time)
								&& !TextUtils.isEmpty(model.repeatWeek)) {
							String data = dateString;
							calendar.add(Calendar.DAY_OF_MONTH, 7 - (repeat - Integer.parseInt(model.repeatWeek)));
							data = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
							if (timeLong < formatter.parse(data + " " + model.time).getTime() - 1000 * 60 * OFFON) {
								time_on = data + " " + model.time;
								repeat = Integer.parseInt(model.repeatWeek);
								break;
							}
						}
					}
				}
				if (!TextUtils.isEmpty(time_on)) {
					for (int i = list_off.size() - 1; i >= 0; i--) {
						TimerTypeModel model = list_off.get(i);
						if (model.timerType.equals("REPEAT") && !TextUtils.isEmpty(model.time)
								&& !TextUtils.isEmpty(model.repeatWeek)
								&& Integer.parseInt(model.repeatWeek) <= repeat) {
							calendar.setTime(formatter.parse(time_on));
							calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(model.repeatWeek) - repeat);
							String data = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
							if (formatter.parse(time_on).getTime() > formatter.parse(data + " " + model.time)
									.getTime()) {
								time_off = data + " " + model.time;
								break;
							}
						}
					}
					if (TextUtils.isEmpty(time_off)) {
						for (int i = list_off.size() - 1; i >= 0; i--) {
							TimerTypeModel model = list_off.get(i);
							if (model.timerType.equals("REPEAT") && !TextUtils.isEmpty(model.time)
									&& !TextUtils.isEmpty(model.repeatWeek)) {
								calendar.setTime(formatter.parse(time_on));
								calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(model.repeatWeek) - repeat - 7);
								String data = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
								if (formatter.parse(time_on).getTime() > formatter.parse(data + " " + model.time)
										.getTime()) {
									time_off = data + " " + model.time;
									break;
								}
							}
						}
					}
				}
			}
		}
		time_off_actual = time_off;
		setTimingOFFON(timeLong);
		Log.d(TAG, "======== time_off:" + time_off + " time_on:" + time_on + " time_off_actual:" + time_off_actual);
	}

	private void setTimingOFFON(long timeLong) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			AlarmPowerManager mAlarmPowerManager = new AlarmPowerManager();
			List<AlarmPowerItem> mAlarmPowerItem_list = mAlarmPowerManager.getAlarmPowerList();
			if (mAlarmPowerItem_list != null) {
				mAlarmPowerManager.removeAllalarmPower();
			}
			if (TextUtils.isEmpty(time_off) || TextUtils.isEmpty(time_on)) {
				return;
			}
			String year_on = Integer.parseInt(time_on.substring(0, 4)) + "";
			String month_on = Integer.parseInt(time_on.substring(5, 7)) + "";
			String day_on = Integer.parseInt(time_on.substring(8, 10)) + "";
			String hour_on = Integer.parseInt(time_on.substring(11, 13)) + "";
			String minute_on = Integer.parseInt(time_on.substring(14, 16)) + "";
			String second_on = Integer.parseInt(time_on.substring(17, 19)) + "";
			String year_off = null, month_off = null, day_off = null, hour_off = null, minute_off = null,
					second_off = null;
			if (timeLong < formatter.parse(time_off).getTime()) {
				year_off = Integer.parseInt(time_off.substring(0, 4)) + "";
				month_off = Integer.parseInt(time_off.substring(5, 7)) + "";
				day_off = Integer.parseInt(time_off.substring(8, 10)) + "";
				hour_off = Integer.parseInt(time_off.substring(11, 13)) + "";
				minute_off = Integer.parseInt(time_off.substring(14, 16)) + "";
				second_off = Integer.parseInt(time_off.substring(17, 19)) + "";
			} else if (formatter.parse(time_off).getTime() <= timeLong) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date(timeLong));
				calendar.add(Calendar.MINUTE, 3);
				year_off = calendar.get(Calendar.YEAR) + "";
				month_off = (calendar.get(Calendar.MONTH) + 1) + "";
				day_off = calendar.get(Calendar.DAY_OF_MONTH) + "";
				hour_off = calendar.get(Calendar.HOUR_OF_DAY) + "";
				minute_off = calendar.get(Calendar.MINUTE) + "";
				second_off = calendar.get(Calendar.SECOND) + "";
				time_off_actual = formatter.format(calendar.getTime());
			}
			if (time_off.substring(0, 10).equals(time_on.substring(0, 10))) {
				mAlarmPowerManager.CreatOneAlarmPower(year_on, month_on, day_on, hour_on, minute_on, second_on,
						hour_off, minute_off, second_off, "1", "1");
			} else {
				mAlarmPowerManager.CreatOneAlarmPower(year_off, month_off, day_off, "", "", "", hour_off, minute_off,
						second_off, "0", "1");
				mAlarmPowerManager.CreatOneAlarmPower(year_on, month_on, day_on, hour_on, minute_on, second_on, "", "",
						"", "1", "0");
			}

			mAlarmPowerItem_list = mAlarmPowerManager.getAlarmPowerList();
			if (mAlarmPowerItem_list != null) {
				for (int i = 0; i < mAlarmPowerItem_list.size(); i++) {
					mAlarmPowerManager.updateAlarmPowerActive(i, "true");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showStudentDialog(StudentInfoModel model) {
		if (model == null) {
			return;
		}
		if (dialog_attend.isShowing()) {
			dialog_attend.dismiss();
		}
		if (!dialog_hint.isShowing()) {
			dialog_hint.dismiss();
		}
		if (dialog != null && dialog.isShowing()) {
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			dialog = builder.create();
		}
		final ImageLoader imageLoader = new ImageLoader(this);
		dialog.getWindow().setType(LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				imageLoader.clearCache();
				imageLoader.cancelTask();
			}
		});
		dialog.show();
		dialog.getWindow().setLayout(1860, 851);
		dialog.getWindow().setContentView(R.layout.cx_dialog_student);

		final ImageView img_student_touxiang = (ImageView) dialog.findViewById(R.id.img_student_touxiang);
		TextView txt_student_1 = (TextView) dialog.findViewById(R.id.txt_student_1);
		TextView txt_student_2 = (TextView) dialog.findViewById(R.id.txt_student_2);
		TextView txt_student_3 = (TextView) dialog.findViewById(R.id.txt_student_3);
		CustomTextView txt_student_4 = (CustomTextView) dialog.findViewById(R.id.txt_student_4);
		TextView txt_student_5 = (TextView) dialog.findViewById(R.id.txt_student_5);
		TextView txt_student_6 = (TextView) dialog.findViewById(R.id.txt_student_6);
		TextView txt_student_7 = (TextView) dialog.findViewById(R.id.txt_student_7);
		TextView txt_student_8 = (TextView) dialog.findViewById(R.id.txt_student_8);
		TextView txt_student_9 = (TextView) dialog.findViewById(R.id.txt_student_9);
		TextView txt_student_10 = (TextView) dialog.findViewById(R.id.txt_student_10);
		TextView txt_student_11 = (TextView) dialog.findViewById(R.id.txt_student_11);
		TextView txt_student_12 = (TextView) dialog.findViewById(R.id.txt_student_12);
		if (!TextUtils.isEmpty(model.zp)) {
			String[] s = model.zp.split("\\.");
			String path = PATH_ROOT + PATH_PICTURE + "/student_" + model.id + "." + s[s.length - 1];
			imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					if (bitmap != null) {
						img_student_touxiang.setImageBitmap(bitmap);
					}
				}
			}, 321, 310);
		}
		String xm = "", xb = "", xsxh = "", ssbjmc = "", xszw = "", bzr = "", csny = "", yddh = "", lxrdh = "",
				jtdz = "", tc = "";
		if (!TextUtils.isEmpty(model.xm)) {
			xm = model.xm;
		}
		if ("1".equals(model.xb)) {
			xb = "男";
		} else if ("2".equals(model.xb)) {
			xb = "女";
		}
		if (!TextUtils.isEmpty(model.xsxh)) {
			xsxh = model.xsxh;
		}
		if (!TextUtils.isEmpty(model.ssbjmc)) {
			ssbjmc = model.ssbjmc;
		}
		if (!TextUtils.isEmpty(model.xszw)) {
			xszw = model.xszw;
		}
		if (!TextUtils.isEmpty(model.bzr)) {
			bzr = model.bzr;
		}
		if (!TextUtils.isEmpty(model.csny)) {
			csny = model.csny;
		}
		if (!TextUtils.isEmpty(model.yddh)) {
			if (model.yddh.length() >= 4) {
				String temp = model.yddh.substring(0, model.yddh.length() - 4);
				for (int i = 0; i < temp.length(); i++) {
					temp = temp.replace(String.valueOf(temp.charAt(i)), "*");
				}
				yddh = temp + model.yddh.substring(model.yddh.length() - 4, model.yddh.length());
			}
		}
		if (!TextUtils.isEmpty(model.lxrdh)) {
			if (model.lxrdh.length() >= 4) {
				String temp = model.lxrdh.substring(0, model.lxrdh.length() - 4);
				for (int i = 0; i < temp.length(); i++) {
					temp = temp.replace(String.valueOf(temp.charAt(i)), "*");
				}
				lxrdh = temp + model.lxrdh.substring(model.lxrdh.length() - 4, model.lxrdh.length());
			} else {
				lxrdh = model.lxrdh;
			}
		}
		if (!TextUtils.isEmpty(model.jtdz)) {
			jtdz = model.jtdz;
		}
		if (!TextUtils.isEmpty(model.tc)) {
			tc = model.tc;
		}
		txt_student_1.setText("姓名 : " + xm);
		txt_student_2.setText("性别 : " + xb);
		txt_student_3.setText("学号 : " + xsxh);
		txt_student_4.setText("就读班级 : " + ssbjmc);
		txt_student_5.setText("职务 : " + xszw);
		txt_student_6.setText("班主任 : " + bzr);
		txt_student_7.setText("出生日期 : " + csny);
		txt_student_8.setText("联系电话 : " + yddh);
		txt_student_9.setText("紧急联系电话 : " + lxrdh);
//		txt_student_10.setText("家庭住址 : " + jtdz);
//		txt_student_11.setText("特长 : " + tc);
		txt_student_10.setText("特长 : " + tc);
		String prize = "";
		PrizeInfoDatabase prizeInfoDatabase = new PrizeInfoDatabase();
		List<PrizeInfoModel> list = prizeInfoDatabase.query_by_xsxh(model.xsxh);
		if (list != null) {
			for (int i = 0; i < 5; i++) {
				if (list.size() > i) {
					prize = prize + list.get(i).prizeName + "\n";
				}
			}
		}
		txt_student_12.setText("所获奖项 : \n" + prize);
	}

	public void showTeacherDialog(TeacherInfoModel model) {
		if (model == null) {
			return;
		}
		if (dialog_attend.isShowing()) {
			dialog_attend.dismiss();
		}
		if (!dialog_hint.isShowing()) {
			dialog_hint.dismiss();
		}
		if (dialog != null && dialog.isShowing()) {
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			dialog = builder.create();
		}
		final ImageLoader imageLoader = new ImageLoader(this);
		dialog.getWindow().setType(LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				imageLoader.clearCache();
				imageLoader.cancelTask();
			}
		});
		dialog.show();
		dialog.getWindow().setLayout(1440, 720);
		dialog.getWindow().setContentView(R.layout.cx_dialog_teacher);

		final ImageView img_teacher_touxiang = (ImageView) dialog.findViewById(R.id.img_teacher_touxiang);
		TextView txt_teacher_1 = (TextView) dialog.findViewById(R.id.txt_teacher_1);
		TextView txt_teacher_2 = (TextView) dialog.findViewById(R.id.txt_teacher_2);
		WebView webView = (WebView) dialog.findViewById(R.id.webView);
		if (!TextUtils.isEmpty(model.zp)) {
			String[] s = model.zp.split("\\.");
			String path = PATH_ROOT + PATH_PICTURE + "/teacher_" + model.rybh + "." + s[s.length - 1];
			imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					if (bitmap != null) {
						img_teacher_touxiang.setImageBitmap(bitmap);
					}
				}
			}, 180, 180);
		}
		String xm = "", byzy = "", zc = "", byxx = "", zgxlmc = "", jl = "", jxqk = "";
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
		txt_teacher_1.setText("教师姓名 : " + xm + "\n所学专业 : " + byzy + "\n职称 : " + zc);
		txt_teacher_2.setText("毕业院校 : " + byxx + "\n学历学位 : " + zgxlmc + "\n教师教龄 : " + jl);
		if (!TextUtils.isEmpty(model.jxqk)) {
			WebSettings webSettings = webView.getSettings();
			webSettings.setDefaultFontSize(30);
			webView.loadDataWithBaseURL(null, model.jxqk, "text/html", "utf-8", null);
		}
	}

	public void openApp(Context context, String packageName) {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		PackageManager mPackageManager = getPackageManager();
		List<ResolveInfo> mAllApps = mPackageManager.queryIntentActivities(mainIntent, 0);
		for (ResolveInfo info : mAllApps) {
			if (info.activityInfo.packageName.equals(packageName)) {
				ComponentName componet = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
				Intent intent = new Intent();
				intent.setComponent(componet);
				context.startActivity(intent);
				break;
			}
		}
	}

	public void openVoice() {
		isSilent = false;
		// application.mAudioManager.setStreamMute(
		// AudioManager.STREAM_MUSIC, false);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
		if (mEYunSdk != null)
			mEYunSdk.setAudioMode(2);
		if (dogProcess != null)
			dogProcess.setAudioMode(2);
	}

	public void closeVoice() {
		isSilent = true;
		// application.mAudioManager
		// .setStreamMute(AudioManager.STREAM_MUSIC, true);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		if (mEYunSdk != null)
			mEYunSdk.setAudioMode(0);
		if (dogProcess != null)
			dogProcess.setAudioMode(0);
	}

	// 打开欧瑞博智能家居
	public void openOrvibo(String userType, String userNumber) {
		ComponentName componet = new ComponentName("com.zy.orvibo.smarthome",
				"com.zy.orvibo.smarthome.register.RegisterActivity");
		Intent intent = new Intent();
		intent.setComponent(componet);
		intent.putExtra("equCode", equInfoModel.equCode);
		intent.putExtra("equName", equInfoModel.equName);
		intent.putExtra("jssysdm", equInfoModel.jssysdm);
		intent.putExtra("bjdm", equInfoModel.bjdm);
		intent.putExtra("orgId", equInfoModel.orgId);
		intent.putExtra("userType", userType);
		intent.putExtra("userNumber", userNumber);
		intent.putExtra("httpip", getServer_ip().replace("/web-unified", ""));
		Log.e(TAG, "equCode:" + equInfoModel.equCode + "\nequName:" + equInfoModel.equName + "\njssysdm:"
				+ equInfoModel.jssysdm + "\nbjdm:" + equInfoModel.bjdm + "\norgId:" + equInfoModel.orgId + "\nuserType:"
				+ userType + "\nuserNumber:" + userNumber + "\nhttpip" + getServer_ip().replace("/web-unified", ""));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			Intent i = new Intent(this, ControlActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
		}
	}

	/**
	 * 启动学习通
	 *
	 * @param fid
	 * @param admin
	 */
	public void launchStudy(String fid, String admin) {
		try {
			String url = "cxstudy://cxstudy/login?fid=" + fid + "&fidType=1&token=" + encrypt(admin, "wgd&%$32");
			Log.d(TAG, url);
			launchStudyAndSignIn(this, url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断是否已经安装学习通
	 *
	 * @param context
	 * @param packageName 学习通包名
	 * @return
	 */
	public boolean isAppInstalled(Context context, String packageName) {
		try {
			return context.getPackageManager().getPackageInfo(packageName, 0) != null;
		} catch (NameNotFoundException e) {
		}
		return false;
	}

	/**
	 * 启动学习通（仅用作唤醒学习通）
	 *
	 * @param context
	 * @param packageName 学习通包名
	 */
	private void launchStudy(Context context, String packageName) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		if (intent != null) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

	/**
	 * @param context 启动学习通并登录
	 * @param url     登录url（如：
	 *                cxstudy://cxstudy/login?fid=xxx&fidType=xxx&token=xxx）
	 */
	private void launchStudyAndSignIn(Context context, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * Description 根据键值进行加密
	 *
	 * @param data
	 * @param key  加密键byte数组
	 * @return
	 */
	private String encrypt(String data, String key) throws Exception {
		byte[] bt = encrypt(data.getBytes(), key.getBytes());
		String strs = new BASE64Encoder().encode(bt);
		return strs;
	}

	/**
	 * Description 根据键值进行加密
	 *
	 * @param data
	 * @param key  加密键byte数组
	 * @return

	 */
	private byte[] encrypt(byte[] data, byte[] key) throws Exception {
		// 生成一个可信任的随机数源
		SecureRandom sr = new SecureRandom();

		// 从原始密钥数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);

		// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance("DES");

		// 用密钥初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

		return cipher.doFinal(data);
	}

	public String getMd5ByFile(File file) {
		if (!file.exists()) {
			return null;
		}
		try {
			MessageDigest digest;
			FileInputStream in;
			byte buffer[] = new byte[1024];
			int len;
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
			BigInteger bigInt = new BigInteger(1, digest.digest());
			return bigInt.toString(16).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getMd5ByString(String string) {
		if (TextUtils.isEmpty(string)) {
			return null;
		}
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bytes = digest.digest(string.getBytes());
			String result = "";
			for (byte b : bytes) {
				String temp = Integer.toHexString(b & 0xff);
				if (temp.length() == 1) {
					temp = "0" + temp;
				}
				result += temp;
			}
			return result.toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void UsbOtgCtrl(int mode) {
		try {
			FileOutputStream outputStream = new FileOutputStream("/sys/bus/platform/drivers/usb20_otg/force_usb_mode");
			byte[] buffer = new byte[2];

			if (mode == 1) {
				buffer[0] = '1'; // DEVICE->HOST
			} else {
				buffer[0] = '2'; // HOST->DEVICE
			}
			outputStream.write(buffer, 0, 1);
			outputStream.flush();
			outputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void debugHex(String tag, byte[] data, int datalen) {
		String hexStr = "";
		for (int i = 0; i < datalen; i++) {
			// if ((i % 16) == 0) {
			// Log.d(tag, hexStr);
			// hexStr = "";
			// }
			hexStr += Integer.toHexString(data[i] & 0xff) + " ";
		}
		Log.d(tag, hexStr);
	}

	public static String getEquId() {
//        String id = getSN();
//
//        if (RUNNING_TARGET == RUNNING_TARGET_PHONE_TEST) {
//            String serial = "360293545224970987";
//            return serial;
//        } else if (RUNNING_TARGET == RUNNING_TARGET_PHONE) {
//            if (id.startsWith("EB") && id.length() == 16) {
//                return String.valueOf(General.getLongContrary(General.hexStr2Str(id)));
//            } else {
//                return String.valueOf(General.getLong(General.hexStr2Str(id)));
//            }
//        } else {
//            if (id.startsWith("EB") && id.length() == 16) {
//                return String.valueOf(General.getLongContrary(General.hexStr2Str(id)));
//            } else {
//                return String.valueOf(General.getLong(General.hexStr2Str(id)));
//            }
//        }
        return  "97530052956247";
	}

	public static String getSN() {
		String serial = null;
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class);
			serial = (String) get.invoke(c, "ro.serialno");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!TextUtils.isEmpty(serial) && serial.startsWith("EB") && serial.length() == 16) {
		} else {
			serial = getMacAddress().replace(":", "");
		}
		return serial;
	}

	public static byte[] getTcpEquId() {
		String id = getSN();
		if (id.startsWith("EB") && id.length() == 16) {
			return General.hexStr2Str(id);
		} else {
			byte[] test = General.hexStr2Str(id);
			byte[] temp = new byte[test.length];
			int j = test.length;
			for (int i = 0; i < test.length; i++) {
				temp[i] = test[j - 1];
				j--;
			}
			return temp;
		}
	}

	public static String getSMEquId() {
		String s = getMacAddress().replace(":", "");
		return s.substring(s.length() - 4, s.length());
	}

	public static String getMacAddress() {
		if (RUNNING_TARGET == RUNNING_TARGET_PAD) {
			return apimanager.XHEthernetGetMac();
		} else if (RUNNING_TARGET == RUNNING_TARGET_PHONE) {
			WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			if (TextUtils.isEmpty(info.getMacAddress())) {
				return getNetMacAddress();
			} else {
				if (info.getMacAddress().toLowerCase().equals("02:00:00:00:00:00")) {
					return getMacAddr();
				}
				return info.getMacAddress();
			}

		} else {
			return "01:09:6E:0D:02:EE";
		}

	}

	public static String getMacAddr() {
		try {
			List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface nif : all) {
				if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

				byte[] macBytes = nif.getHardwareAddress();
				if (macBytes == null) {
					return "";
				}

				StringBuilder res1 = new StringBuilder();
				for (byte b : macBytes) {
					res1.append(String.format("%02X:", b));
				}

				if (res1.length() > 0) {
					res1.deleteCharAt(res1.length() - 1);
				}
				return res1.toString();
			}
		} catch (Exception ex) {
		}
		return "02:00:00:00:00:00";
	}

	private static String getNetMacAddress() {
		try {
			return loadFileAsString("/sys/class/net/eth0/address").toUpperCase().substring(0, 17);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String loadFileAsString(String filePath) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
		}
		reader.close();
		return fileData.toString();
	}

	public static String getTime(String format) {
		String str = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			Date curDate = new Date(System.currentTimeMillis());
			str = formatter.format(curDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public boolean checkNetworkHw() {
		boolean isOkay = false;
		ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ethNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!ethNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
			// --网卡未开启（以太网或者wifi关闭了）
			isOkay = false;
		} else {
			// --wifi或者以太网以开启
			if (!ethNetInfo.isAvailable() && !wifiNetInfo.isAvailable()) { // --无法获取IP或者无法正常使用
				isOkay = false;
			} else {// --网卡正常使用
				isOkay = true;
			}
		}
		return isOkay;
	}

	public String getVersionName() {
		try {
			PackageManager packageManager = getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getVersionCode() {
		try {
			PackageManager packageManager = getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			return packInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * adb命令执行 && 重要：该方法必须在线程中执行
	 */
	public boolean implementSuper(String command) {
		Log.d(TAG, command);
		LocalSocket client;
		LocalSocketAddress address;
		client = new LocalSocket();
		address = new LocalSocketAddress("shell_server", LocalSocketAddress.Namespace.RESERVED);
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream());

			client.connect(address);

			out.println(command);
			out.flush();
			out.close();
			client.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void takeScreenshot(String path) {
		Intent intent = new Intent();
		intent.setAction("screencap");
		intent.putExtra("screencap_path", path);
		sendBroadcast(intent);
	}

	public long getTotalMemory() {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.totalMem / 1024 / 1024;
	}

	public long getAvailMemory() {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.availMem / 1024 / 1024;
	}

	public long getTotalDisk() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			long availCount = sf.getAvailableBlocks();
			return blockSize * blockCount / 1024 / 1024;
		}
		return 0;
	}

	public long getAvailDisk() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			long availCount = sf.getAvailableBlocks();
			return blockSize * availCount / 1024 / 1024;
		}
		return 0;
	}

	public int getProcessCpuRate() {
		int rate = 0;
		try {
			String Result;
			Process p;
			p = Runtime.getRuntime().exec("top -n 1");

			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((Result = br.readLine()) != null) {
				if (Result.trim().length() < 1) {
					continue;
				} else {
					String[] CPUusr = Result.split("%");
					String[] CPUusage = CPUusr[0].split("User");
					String[] SYSusage = CPUusr[1].split("System");
					rate = Integer.parseInt(CPUusage[1].trim()) + Integer.parseInt(SYSusage[1].trim());
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return rate;
	}

	public String getOpenTime() {
		String time = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis() - SystemClock.elapsedRealtime());
		time = formatter.format(curDate);
		return time;
	}

	public void createFloating() {

		if (windowManager != null) {
			return;
		}

		// 获取WindowManager
		windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		// 设置LayoutParams(全局变量）相关参数
		LayoutParams windowManagerParams = new LayoutParams();

		windowManagerParams.type = LayoutParams.TYPE_PHONE; // 设置window type
		windowManagerParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
		// 设置Window flag
		windowManagerParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
		/*
		 * 注意，flag的值可以为： 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
		 * LayoutParams.FLAG_NOT_TOUCH_MODAL 不影响后面的事件 LayoutParams.FLAG_NOT_FOCUSABLE
		 * 不可聚焦 LayoutParams.FLAG_NOT_TOUCHABLE 不可触摸
		 */
		// 调整悬浮窗口至左上角，便于调整坐标
		windowManagerParams.gravity = Gravity.LEFT | Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值
		windowManagerParams.x = 0;
		windowManagerParams.y = 0;
		// 设置悬浮窗口长宽数据
		windowManagerParams.width = (int) getResources().getDimension(R.dimen.dimen_50_dip);
		windowManagerParams.height = (int) getResources().getDimension(R.dimen.dimen_50_dip);
		floatView = new FloatView(this, windowManager, windowManagerParams);
		floatView.setBackgroundResource(R.drawable.btn_float_back);
		floatView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if ("1.4".equals(getVersionName(mContext, APP_TOOL_ID, APP_TOOL_NAME))) {
					Intent intent = new Intent();
					intent.setAction("ToolService_key_event");
					intent.putExtra("ToolService_key_code", 4);
					sendBroadcast(intent);
				} else {
					Intent intent = new Intent();
					intent.setAction("stop_app");
					intent.putExtra("stop_app_packagename", MyApplication.WPS);
					sendBroadcast(intent);

					intent = new Intent();
					intent.setAction("stop_app");
					intent.putExtra("stop_app_packagename", MyApplication.CHAOXING);
					sendBroadcast(intent);
				}
			}
		});
		// 显示myFloatView图像
		windowManager.addView(floatView, windowManagerParams);
	}

	public void destroyFloating() {
		if (windowManager != null && floatView != null) {
			windowManager.removeView(floatView);
			windowManager = null;
			floatView = null;
		}
	}

	// 备份设备信息，防止数据库损坏
	public void backupsEquInfo(EquInfoModel equInfoModel) {
		mPreferences.edit().putString("equInfoModel.equCode", equInfoModel.equCode).apply();
		mPreferences.edit().putString("equInfoModel.equName", equInfoModel.equName).apply();
		mPreferences.edit().putString("equInfoModel.jssysdm", equInfoModel.jssysdm).apply();
		mPreferences.edit().putString("equInfoModel.jssysmc", equInfoModel.jssysmc).apply();
		mPreferences.edit().putString("equInfoModel.ip", equInfoModel.ip).apply();
		mPreferences.edit().putString("equInfoModel.gateway", equInfoModel.gateway).apply();
		mPreferences.edit().putString("equInfoModel.subMask", equInfoModel.subMask).apply();
		mPreferences.edit().putString("equInfoModel.dns", equInfoModel.dns).apply();
		mPreferences.edit().putString("equInfoModel.dns2", equInfoModel.dns2).apply();
		mPreferences.edit().putString("equInfoModel.mac", equInfoModel.mac).apply();
		mPreferences.edit().putString("equInfoModel.bjdm", equInfoModel.bjdm).apply();
		mPreferences.edit().putString("equInfoModel.accSysIp", equInfoModel.accSysIp).apply();
		mPreferences.edit().putString("equInfoModel.accSysPort", equInfoModel.accSysPort).apply();
		mPreferences.edit().putString("equInfoModel.networkType", equInfoModel.networkType).apply();
		mPreferences.edit().putString("equInfoModel.readCardRule", equInfoModel.readCardRule).apply();
		mPreferences.edit().putString("equInfoModel.orgId", equInfoModel.orgId).apply();
	}

	private void recoveryEquInfo() {
		equInfoModel = new EquInfoModel();
		equInfoModel.equCode = mPreferences.getString("equInfoModel.equCode", getEquId());
		equInfoModel.equName = mPreferences.getString("equInfoModel.equName", null);
		equInfoModel.jssysdm = mPreferences.getString("equInfoModel.jssysdm", null);
		equInfoModel.jssysmc = mPreferences.getString("equInfoModel.jssysmc", null);
		equInfoModel.ip = mPreferences.getString("equInfoModel.ip", null);
		equInfoModel.gateway = mPreferences.getString("equInfoModel.gateway", null);
		equInfoModel.subMask = mPreferences.getString("equInfoModel.subMask", null);
		equInfoModel.dns = mPreferences.getString("equInfoModel.dns", null);
		equInfoModel.dns2 = mPreferences.getString("equInfoModel.dns2", null);
		equInfoModel.mac = mPreferences.getString("equInfoModel.mac", null);
		equInfoModel.bjdm = mPreferences.getString("equInfoModel.bjdm", null);
		equInfoModel.accSysIp = mPreferences.getString("equInfoModel.accSysIp", null);
		equInfoModel.accSysPort = mPreferences.getString("equInfoModel.accSysPort", null);
		equInfoModel.networkType = mPreferences.getString("equInfoModel.networkType", null);
		equInfoModel.readCardRule = mPreferences.getString("equInfoModel.readCardRule", null);
		equInfoModel.orgId = mPreferences.getString("equInfoModel.orgId", null);
		EquInfoDatabase equInfoDatabase = new EquInfoDatabase();
		equInfoDatabase.drop();
		equInfoDatabase.insert(equInfoModel);
	}

	public void setHandler_call(Handler handler_call) {
		this.handler_call = handler_call;
	}

	public void setSynComplete(boolean isSynComplete) {
		this.isSynComplete = isSynComplete;
		mPreferences.edit().putBoolean("isSynComplete", isSynComplete).commit();
	}

	public void setDqxn(String dqxn) {
		this.dqxn = dqxn;
		mPreferences.edit().putString("dqxn", dqxn).commit();
	}

	public void setDqxq(String dqxq) {
		this.dqxq = dqxq;
		mPreferences.edit().putString("dqxq", dqxq).commit();
	}

	public void setKxsj(String kxsj) {
		this.kxsj = kxsj;
		mPreferences.edit().putString("kxsj", kxsj).commit();
	}

	public void setJssj(String jssj) {
		this.jssj = jssj;
		mPreferences.edit().putString("jssj", jssj).commit();
	}

	public void setZs(String zs) {
		this.zs = zs;
		mPreferences.edit().putString("zs", zs).commit();
	}

	public void setToday(String today) {
		this.today = today;
		mPreferences.edit().putString("today", today).commit();
	}

	public void setNotice_total(int notice_total) {
		this.notice_total = notice_total;
		mPreferences.edit().putInt("notice_total", notice_total).commit();
	}

	public void setNotice_total_bpxxtz(int notice_total_bpxxtz) {
		this.notice_total_bpxxtz = notice_total_bpxxtz;
		mPreferences.edit().putInt("notice_total_bpxxtz", notice_total_bpxxtz).commit();
	}

	public void setNotice_total_bpbjtz(int notice_total_bpbjtz) {
		this.notice_total_bpbjtz = notice_total_bpbjtz;
		mPreferences.edit().putInt("notice_total_bpbjtz", notice_total_bpbjtz).commit();
	}

	public void setNotice_total_bpbjxw(int notice_total_bpbjxw) {
		this.notice_total_bpbjxw = notice_total_bpbjxw;
		mPreferences.edit().putInt("notice_total_bpbjxw", notice_total_bpbjxw).commit();
	}

	public void setNotice_total_bpxxxw(int notice_total_bpxxxw) {
		this.notice_total_bpxxxw = notice_total_bpxxxw;
		mPreferences.edit().putInt("notice_total_bpxxxw", notice_total_bpxxxw).commit();
	}

	public void setNotice_total_bpxxxw(String xxztlb, int notice_total_bpxxxw) {
		mPreferences.edit().putInt("notice_total_bpxxxw_" + xxztlb, notice_total_bpxxxw).commit();
	}

	public int getNotice_total_bpxxxw(String xxztlb) {
		return mPreferences.getInt("notice_total_bpxxxw_" + xxztlb, 0);
	}

	public void setVote_total(int vote_total) {
		this.vote_total = vote_total;
		mPreferences.edit().putInt("vote_total", vote_total).commit();
	}

	public void setActivity_total(int activity_total) {
		this.activity_total = activity_total;
		mPreferences.edit().putInt("vote_total", vote_total).commit();
	}

	public String getServer_ip() {
		server_ip = mPreferences.getString("server_ip", DEFAULT_SERVER_IP);
		return server_ip;
	}

	public void setServer_ip(String server_ip) {
		this.server_ip = server_ip;
		mPreferences.edit().putString("server_ip", server_ip).commit();
	}

	public void setScreensaver_time(int screensaver_time) {
		this.screensaver_time = screensaver_time;
		mPreferences.edit().putInt("screensaver_time", screensaver_time).commit();
	}

	public void setScreensaver_mode(int screensaver_mode) {
		this.screensaver_mode = screensaver_mode;
		mPreferences.edit().putInt("screensaver_mode", screensaver_mode).commit();
	}

	public void setAbsenteeism_timeout(int absenteeism_timeout) {
		this.absenteeism_timeout = absenteeism_timeout;
		mPreferences.edit().putInt("absenteeism_timeout", absenteeism_timeout).commit();
	}

	public void setRootResPath(String rootResPath) {
		this.rootResPath = rootResPath;
		mPreferences.edit().putString("rootResPath", rootResPath).commit();
	}

	public void setAutoBrightness(boolean isAutoBrightness) {
		this.isAutoBrightness = isAutoBrightness;
		mPreferences.edit().putBoolean("isAutoBrightness", isAutoBrightness).commit();
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
		mPreferences.edit().putString("logoUrl", logoUrl).commit();
	}

}

