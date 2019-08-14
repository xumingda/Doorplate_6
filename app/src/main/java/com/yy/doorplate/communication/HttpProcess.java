package com.yy.doorplate.communication;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.net.io.ToNetASCIIOutputStream;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.android.xhapimanager.XHApiManager;
import com.common.AppLog;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.activity.InitActivity;
import com.yy.doorplate.database.AnswerDatabase;
import com.yy.doorplate.database.AttendInfoDatabase;
import com.yy.doorplate.database.BlackboardInfoDatabase;
import com.yy.doorplate.database.BooksInfoDatabase;
import com.yy.doorplate.database.CarouselDatabase;
import com.yy.doorplate.database.ClassEquInfoDatabase;
import com.yy.doorplate.database.ClassFeedbackDatabase;
import com.yy.doorplate.database.ClassInfoDatabase;
import com.yy.doorplate.database.ClassMottoInfoDatabase;
import com.yy.doorplate.database.ClassRoomInfoDatabase;
import com.yy.doorplate.database.ClockInfoDatabase;
import com.yy.doorplate.database.ClockRuleDatabase;
import com.yy.doorplate.database.CoursewareDatabase;
import com.yy.doorplate.database.CurriculumInfoDatabase;
import com.yy.doorplate.database.DiyMenuDatabase;
import com.yy.doorplate.database.EquInfoDatabase;
import com.yy.doorplate.database.EquListDatabase;
import com.yy.doorplate.database.EquModelTaskDatabase;
import com.yy.doorplate.database.ExaminationDatabase;
import com.yy.doorplate.database.LmInfoDatabase;
import com.yy.doorplate.database.MenuAndEqDatabase;
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.database.OnDutyInfoDatabase;
import com.yy.doorplate.database.OptionDatabase;
import com.yy.doorplate.database.OrgDatabase;
import com.yy.doorplate.database.PageLayoutDatabase;
import com.yy.doorplate.database.PersonInfoDatabase;
import com.yy.doorplate.database.PersonnelAttendanceDatabase;
import com.yy.doorplate.database.PicDreOrActivityDatabase;
import com.yy.doorplate.database.PlayTaskDatabase;
import com.yy.doorplate.database.PrizeInfoDatabase;
import com.yy.doorplate.database.QuestionDatabase;
import com.yy.doorplate.database.QuestionNaireDatabase;
import com.yy.doorplate.database.SchoolInfoDatabase;
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
import com.yy.doorplate.database.VoteInfoDatabase;
import com.yy.doorplate.database.VoteOptionDatabase;
import com.yy.doorplate.database.VoteThemeDatabase;
import com.yy.doorplate.database.XyxxDatabase;
import com.yy.doorplate.model.AnswerModel;
import com.yy.doorplate.model.AttendInfoModel;
import com.yy.doorplate.model.BlackboardInfoModel;
import com.yy.doorplate.model.BooksInfoModel;
import com.yy.doorplate.model.CarouselModel;
import com.yy.doorplate.model.ClassEquInfoModel;
import com.yy.doorplate.model.ClassFeedbackModel;
import com.yy.doorplate.model.ClassInfoModel;
import com.yy.doorplate.model.ClassMottoInfoModel;
import com.yy.doorplate.model.ClassRoomInfoModel;
import com.yy.doorplate.model.ClockInfoModel;
import com.yy.doorplate.model.ClockRuleModel;
import com.yy.doorplate.model.ControlModel;
import com.yy.doorplate.model.CoursewareModel;
import com.yy.doorplate.model.CurriculumInfoModel;
import com.yy.doorplate.model.DiyMenuModel;
import com.yy.doorplate.model.EquInfoModel;
import com.yy.doorplate.model.EquModelTaskModel;
import com.yy.doorplate.model.EquWorkInfoModel;
import com.yy.doorplate.model.ExaminationModel;
import com.yy.doorplate.model.FileDownloadProcessModel;
import com.yy.doorplate.model.LeaveModel;
import com.yy.doorplate.model.LeaveMsgModel;
import com.yy.doorplate.model.LmInfoModel;
import com.yy.doorplate.model.MenuAndEqModel;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.model.OnDutyInfoModel;
import com.yy.doorplate.model.OptionModel;
import com.yy.doorplate.model.OrgModel;
import com.yy.doorplate.model.PageLayoutModel;
import com.yy.doorplate.model.PersonInfoModel;
import com.yy.doorplate.model.PersonnelAttendanceModel;
import com.yy.doorplate.model.PicDreOrActivityModel;
import com.yy.doorplate.model.PlayTaskModel;
import com.yy.doorplate.model.PrizeInfoModel;
import com.yy.doorplate.model.QuestionModel;
import com.yy.doorplate.model.QuestionNaireModel;
import com.yy.doorplate.model.SchoolInfoModel;
import com.yy.doorplate.model.ScreensaverModel;
import com.yy.doorplate.model.SectionInfoModel;
import com.yy.doorplate.model.StarModel;
import com.yy.doorplate.model.StudentInfoModel;
import com.yy.doorplate.model.TeacherInfoModel;
import com.yy.doorplate.model.TelMenuInfoModel;
import com.yy.doorplate.model.TimerTypeModel;
import com.yy.doorplate.model.UserInfoModel;
import com.yy.doorplate.model.VideoInfoModel;
import com.yy.doorplate.model.VoteInfoModel;
import com.yy.doorplate.model.VoteOptionModel;
import com.yy.doorplate.model.VoteThemeModel;
import com.yy.doorplate.model.XyxxTypeModel;
import com.yy.doorplate.tool.EthernetUtils;

public class HttpProcess {

	public static final String SUCESS = "0000";
	public static final String ERROR = "9999";
	public static final String NONE = "9001";
	public static final String SUCESS_MSG = "操作成功";
	public static final String NONE_MSG = "暂无数据";

	public static final String EQUIPMENT_REGISTER = "equipmentRegister";
	public static final String QRY_EQU = "qryEquSetting";
	public static final String QRY_CLASS_DEVICE = "qryClassDeviceList";
	public static final String QRY_CLASS_MOTTO = "qryClassMottoInfo";
	public static final String GET_VERSION = "getVersionInfo";
	public static final String QRY_NOTICE = "qryNoticeList";
	public static final String QRY_CURRICULUM = "qryCurriculumList";
	public static final String COMMIT_ATTEND = "commitAttendInfo";
	public static final String QRY_ATTEND = "qryAttendInfo";
	public static final String QRY_DATATIME = "qryDatetime";
	public static final String QRY_STUDENT = "qryStudentInfoList";
	public static final String OPERATE_AUTHORIZATION = "operateAuthorization";
	public static final String QRY_EQU_LIST = "qryEquList";
	public static final String QRY_PLAY_TASK = "qryPlayTaskList";
	public static final String QRY_TEACHER = "qryTeacherInfo";
	public static final String QRY_PERSON = "qryPersonInfo";
	public static final String COMMIT_CONTROL = "commitControlInfoList";
	public static final String SET_IP = "set_ip";
	public static final String QRY_PRIZE = "qryPrizeInfoList";
	public static final String QRY_BLACKBOARD = "qryBlackboardInfoList";
	public static final String QRY_ONDUTY = "qryOnDutyInfoList";
	public static final String QRY_SCHOOL = "qrySchoolInfoList";
	public static final String QRY_QUEST = "qryQuestionnaireInfoList";
	public static final String COMMIT_QUEST = "commitQuestionnaireInfoList";
	public static final String QRY_WEATHER = "qryWeatherInfo";
	public static final String COMMIT_EQUWORKINFO = "equWorkInfoCommit";
	public static final String QRY_EQUMODEL = "qryEquModelTask";
	public static final String QRY_EXAM = "qryExaminationInfo";
	public static final String COMMIT_BACK = "cmdFeedbackCommit";
	public static final String QRY_MEDIA = "qryMediaInfo";
	public static final String QRY_PAGE = "qryPageLayoutInfo";
	public static final String COMMIT_FILEPROCESS = "commitFileDownloadProcessInfo";
	public static final String QRY_VIDEO = "getSmallVideoInfo";
	public static final String QRY_UPDATE_VIDEO = "updateSmallVideoInfo";
	public static final String QRY_UPDATE_USER = "updateUserInfo";
	public static final String QRY_CLASSINFO = "qryClassInfo";
	public static final String COMMIT_CLASSFEEDBACK = "commitClassFeedback";
	public static final String COMMIT_CLOCKINFO = "commitClockInfo";
	public static final String COMMIT_CLOCKINFO2 = "commitClockInfo2";
	public static final String QRY_TEACHERLIST = "qryTeacherList";
	public static final String QRY_CLOCKRULE = "qryClockRuleList";

	public static final String QRY_PERSONATTEND = "qryPersonnelAttendanceList";
	public static final String QRY_PERSONATTEND2 = "qryPersonnelAttendanceList2";
	public static final String QRY_PERSONATTEND3 = "qryPersonnelAttendanceList3";

	public static final String COMMIT_PERSONATTEND = "commitPersonnelAttendanceList";
	public static final String QRY_NEARATTEND = "qryNearAttendInfo";
	public static final String QRY_VOTE = "qryVoteThemeList";
	public static final String COMMIT_VOTE = "commitVoteInfoList";
	public static final String QRY_BOOK = "qryBooksList";
	public static final String QRY_PHOTO = "qryActivityPhoto";
	public static final String QRY_DIYMENU = "qryDiyMenu";
	public static final String QRY_STAR = "qryStarList";
	public static final String QRY_QUSET_CLASS = "qryClassQuestionnaire";
	public static final String COMMIT_QUEST_CLASS = "commitClassQuestionnaire";
	public static final String QRY_LM = "qryLmInfoList";
	public static final String QRY_LOGO = "qryLogoUrl";
	public static final String QRY_SCREENSAVER = "qryScreenResource";
	public static final String QRY_LEAVE_USER = "qryLeaveMsgToUser";
	public static final String COMMIT_LEAVE_MSG = "commitLeaveMsg";
	public static final String QRY_LEAVE_MSG = "qryLeaveMsg";
	public static final String QRY_LEAVE = "qryLeaveInfo";
	public static final String COMMIT_LEAVE = "commitLeaveInfo";
	public static final String QRY_CAROUSE = "qryCarouselDataInfo";
	public static final String QRY_ORG = "qryOrgInfoList";
	public static final String QRY_CITY_WEATHER = "qryCityWeatherInfo";

	private static final String TAG = "HttpProcess";

	private MyApplication application;

	private HttpUtils httpUtils;

	private LocalBroadcastManager broadcastManager;

	public HttpProcess(Context mContext) {
		application = (MyApplication) mContext;
		httpUtils = new HttpUtils(application);
		broadcastManager = LocalBroadcastManager.getInstance(application);
	}

	public void getWeather(final String jssysdm) {
		application.executoService.execute(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_WEATHER);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_WEATHER, httpUtils.getJsonWeather(jssysdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject weatherInfo = data.getJSONObject("weatherInfo");
							application.currentCity = weatherInfo.getString("currentCity");
							application.weather = weatherInfo.getString("weather");
							application.temperature = weatherInfo.getString("temperature");
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra("currentCity", weatherInfo.getString("currentCity"));
							intent.putExtra("weather", weatherInfo.getString("weather"));
							intent.putExtra("temperature", weatherInfo.getString("temperature"));
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	private String intToIp(int i) {

		return (i & 0xFF) + "." +
				((i >> 8) & 0xFF) + "." +
				((i >> 16) & 0xFF) + "." +
				(i >> 24 & 0xFF);
	}

	// 注册
	public void Register(final String equName, final String jssysdm, final String bjdm, final String orgId) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, EQUIPMENT_REGISTER);
				if (application.checkNetworkHw()) {
					AppLog.d(TAG, "application.checkNetworkHw result true");
					String ip = "", gateway = "", mask = "", dns1 = "", dns2 = "";

					if (MyApplication.RUNNING_TARGET == MyApplication.RUNNING_TARGET_PAD) {
						//lph20190329
						ip = MyApplication.apimanager.XHEthernetGetIP();
						gateway = MyApplication.apimanager.XHEthernetGetway();
						mask = MyApplication.apimanager.XHEthernetGetMask();
						dns1 = MyApplication.apimanager.XHEthernetGetDSN1();
						dns2 = MyApplication.apimanager.XHEthernetGetDSN2();
					} else if (MyApplication.RUNNING_TARGET == MyApplication.RUNNING_TARGET_PHONE) {

						WifiManager wifi = (WifiManager) MyApplication.getmContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
						WifiInfo info = wifi.getConnectionInfo();
						DhcpInfo dhcpInfo = wifi.getDhcpInfo();
						ip = intToIp(dhcpInfo.ipAddress);
						gateway = intToIp(dhcpInfo.gateway);
						mask = intToIp(dhcpInfo.netmask);
						dns1 = intToIp(dhcpInfo.dns1);
						dns2 = intToIp(dhcpInfo.dns2);
					} else {
						EthernetUtils ethernetUtils = new EthernetUtils(application);
						if (ethernetUtils.isUsingStaticIp()) {
							ip = ethernetUtils.getEthInfoFromStaticIP();
							gateway = ethernetUtils.getEthInfoFromStaticGATEWAY();
							mask = ethernetUtils.getEthInfoFromStaticNetmask();
							dns1 = ethernetUtils.getEthInfoFromStaticDNS1();
							dns2 = ethernetUtils.getEthInfoFromStaticDNS2();
						} else {
							ip = EthernetUtils.getEthInfoFromDhcpIP();
							gateway = ethernetUtils.getEthInfoFromDhcpGATEWAY();
							mask = ethernetUtils.getEthInfoFromDhcpNetmask();
							dns1 = ethernetUtils.getEthInfoFromDhcpDNS1();
							dns2 = ethernetUtils.getEthInfoFromDhcpDNS2();
						}
					}


					AppLog.d(TAG, "ip=" + ip);
					AppLog.d(TAG, "gateway=" + gateway);
					AppLog.d(TAG, "mask=" + mask);
					AppLog.d(TAG, "dns1=" + dns1);
					AppLog.d(TAG, "dns2=" + dns2);

					String result = httpUtils.post(EQUIPMENT_REGISTER,
							httpUtils.getJsonRegister(equName, jssysdm, bjdm, orgId, ip, gateway, mask, dns1, dns2));


					AppLog.d(TAG, "register result=" + result);
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject equInfo = data.getJSONObject("equInfo");
							EquInfoModel model = new EquInfoModel();
							model.equCode = equInfo.getString("equCode");
							model.equName = equInfo.getString("equName");
							model.jssysdm = equInfo.getString("jssysdm");
							model.jssysmc = equInfo.getString("jssysmc");
							model.ip = equInfo.getString("ip");
							model.gateway = equInfo.getString("gateway");
							model.subMask = equInfo.getString("subMask");
							model.dns = equInfo.getString("dns");
							model.dns2 = equInfo.getString("dns2");
							model.mac = equInfo.getString("mac");
							model.status = equInfo.getString("status");
							model.equMode = equInfo.getString("equMode");
							model.bjdm = equInfo.getString("bjdm");
							model.accSysIp = equInfo.getString("accSysIp");
							model.accSysPort = equInfo.getString("accSysPort");
							model.equType = equInfo.getString("equType");
							model.networkType = equInfo.getString("networkType");
							try {
								model.readCardRule = equInfo.getString("readCardRule");
								model.equVolume = equInfo.getString("equVolume");
								model.orgId = equInfo.getString("orgId");
								application.volume = Integer.parseInt(model.equVolume) / 100;
							} catch (Exception e) {
								e.printStackTrace();
							}
							EquInfoDatabase database = new EquInfoDatabase();
							database.drop();
							database.insert(model);
							application.equInfoModel = model;
							application.backupsEquInfo(model);
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra("networkType", model.networkType);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					AppLog.d(TAG, "application.checkNetworkHw result false");
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询设备信息
	public void QryEqu(final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_EQU);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_EQU, httpUtils.getJsonQryEqu());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject equInfo = data.getJSONObject("equInfo");
							EquInfoModel model = new EquInfoModel();
							model.equCode = equInfo.getString("equCode");
							model.equName = equInfo.getString("equName");
							model.jssysdm = equInfo.getString("jssysdm");
							model.jssysmc = equInfo.getString("jssysmc");
							model.ip = equInfo.getString("ip");
							model.gateway = equInfo.getString("gateway");
							model.subMask = equInfo.getString("subMask");
							model.dns = equInfo.getString("dns");
							model.dns2 = equInfo.getString("dns2");
							model.mac = equInfo.getString("mac");
							model.status = equInfo.getString("status");
							model.equMode = equInfo.getString("equMode");
							model.bjdm = equInfo.getString("bjdm");
							model.accSysIp = equInfo.getString("accSysIp");
							model.accSysPort = equInfo.getString("accSysPort");
							model.equType = equInfo.getString("equType");
							model.networkType = equInfo.getString("networkType");
							try {
								model.readCardRule = equInfo.getString("readCardRule");
								model.equVolume = equInfo.getString("equVolume");
								model.orgId = equInfo.getString("orgId");
								application.volume = Integer.parseInt(model.equVolume) / 100;
							} catch (Exception e) {
								e.printStackTrace();
							}
							EquInfoDatabase database = new EquInfoDatabase();
							database.drop();
							database.insert(model);
							application.equInfoModel = model;
							application.backupsEquInfo(model);
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra("networkType", model.networkType);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询教室信息
	public void QryClassRoom(final String jssysdm, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_CLASS_DEVICE);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_CLASS_DEVICE, httpUtils.getJsonQryClass(jssysdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject classInfo = data.getJSONObject("classRoomInfo");
							ClassRoomInfoModel classInfoModel = new ClassRoomInfoModel();
							classInfoModel.id = classInfo.getString("id");
							classInfoModel.lx = classInfo.getString("lx");
							classInfoModel.jssysdm = classInfo.getString("jssysdm");
							classInfoModel.jssysmc = classInfo.getString("jssysmc");
							classInfoModel.xsrl = classInfo.getString("xsrl");
							classInfoModel.bjrl = classInfo.getString("bjrl");
							classInfoModel.gxjgdm = classInfo.getString("gxjgdm");
							classInfoModel.gxjgmc = classInfo.getString("gxjgmc");
							classInfoModel.remark = classInfo.getString("remark");
							classInfoModel.cdglyxm = classInfo.getString("cdglyxm");
							classInfoModel.cdglygh = classInfo.getString("cdglygh");
							classInfoModel.cdglydh = classInfo.getString("cdglydh");
							classInfoModel.cdglyzp = classInfo.getString("cdglyzp");
							classInfoModel.cdglykh = classInfo.getString("cdglykh");
							ClassRoomInfoDatabase classInfoDatabase = new ClassRoomInfoDatabase();
							classInfoDatabase.delete();
							classInfoDatabase.insert(classInfoModel);
							application.classRoomInfoModel = classInfoModel;

							ClassEquInfoDatabase classEquInfoDatabase = new ClassEquInfoDatabase();
							classEquInfoDatabase.delete_all();
							try {
								String deviceList = data.getString("deviceList");
								List<ClassEquInfoModel> list = JSONArray.parseArray(deviceList,
										ClassEquInfoModel.class);
								if (list != null && list.size() > 0) {
									for (ClassEquInfoModel model : list) {
										classEquInfoDatabase.insert(model);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}


	// 查询班训信息20190403lph
	public void qryClassMottoInfo(final String bjdm, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_CLASS_MOTTO);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_CLASS_MOTTO, httpUtils.getJsonQryClassMotto(bjdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject classMottoInfo = data.getJSONObject("classMottoInfo");
							ClassMottoInfoModel classMotoInfoModel = new ClassMottoInfoModel();
							classMotoInfoModel.bjdm = classMottoInfo.getString("bjdm");
							classMotoInfoModel.bjmc = classMottoInfo.getString("bjmc");
							classMotoInfoModel.fdy = classMottoInfo.getString("fdy");
							classMotoInfoModel.fdyxm = classMottoInfo.getString("fdyxm");
							classMotoInfoModel.classMotto = classMottoInfo.getString("classMotto");
							classMotoInfoModel.Tagger = classMottoInfo.getString("Tagger");
							classMotoInfoModel.aphorism = classMottoInfo.getString("aphorism");
							classMotoInfoModel.lifePhoto = classMottoInfo.getString("lifePhoto");

							ClassMottoInfoDatabase classMottoInfoDatabase = new ClassMottoInfoDatabase();
							classMottoInfoDatabase.delete();
							classMottoInfoDatabase.insert(classMotoInfoModel);
							application.classMottoInfoModel = classMotoInfoModel;

//							ClassEquInfoDatabase classEquInfoDatabase = new ClassEquInfoDatabase();
//							classEquInfoDatabase.delete_all();
//							try {
//								String deviceList = data.getString("deviceList");
//								List<ClassEquInfoModel> list = JSONArray.parseArray(deviceList,
//										ClassEquInfoModel.class);
//								if (list != null && list.size() > 0) {
//									for (ClassEquInfoModel model : list) {
//										classEquInfoDatabase.insert(model);
//									}
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}


	// 查询版本信息
	public void GetVersion(final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, GET_VERSION);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(GET_VERSION, httpUtils.getJsonVersion());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							intent.putExtra("verNo", data.getString("verNo"));
							intent.putExtra("verDesc", data.getString("verDesc"));
							intent.putExtra("downloadUrl", data.getString("downloadUrl"));
							intent.putExtra("md5Check", data.getString("md5Check"));
							intent.putExtra("subject", data.getString("subject"));
							intent.putExtra("artifactList", data.getString("artifactList"));
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询通知
	public void QryNotice(final String jssysdm, final String page, final String pageCount, final String lmdm,
						  final String xxztlb, final String bjdm, final boolean isDel, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_NOTICE);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_NOTICE,
							httpUtils.getJsonQryNotice(jssysdm, page, pageCount, lmdm, xxztlb, bjdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							NoticeInfoDatabase database = new NoticeInfoDatabase();
							if (isDel) {
								database.drop();
							}
							try {
								String noticeList = data.getString("noticeList");
								List<NoticeInfoModel> list = JSONArray.parseArray(noticeList, NoticeInfoModel.class);
								if (list != null && list.size() > 0) {
									for (NoticeInfoModel model : list) {
										if (model != null) {
											database.insert(model);
										}
									}
								}

								JSONObject pageInfo = data.getJSONObject("pageInfo");
								String page = pageInfo.getString("page");
								String pageCount = pageInfo.getString("pageCount");
								String total = pageInfo.getString("total");
								System.out.println("page:" + page + " pageCount:" + pageCount + " total:" + total);
								application.setNotice_total(Integer.parseInt(total));
							} catch (Exception e) {
								e.printStackTrace();
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询多类通知
	public void QryNoticePJ(final String jssysdm, final String page, final String pageCount, final String bjdm,
							final boolean isDel, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_NOTICE);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_NOTICE,
							httpUtils.getJsonQryNotice(jssysdm, page, pageCount, "bpxxtz", "", bjdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							NoticeInfoDatabase database = new NoticeInfoDatabase();
							if (isDel) {
								database.drop();
							}
							try {
								String noticeList = data.getString("noticeList");
								List<NoticeInfoModel> list = JSONArray.parseArray(noticeList, NoticeInfoModel.class);
								if (list != null && list.size() > 0) {
									for (NoticeInfoModel model : list) {
										if (model != null) {
											database.insert(model);
										}
									}
								}

								JSONObject pageInfo = data.getJSONObject("pageInfo");
								String page = pageInfo.getString("page");
								String pageCount = pageInfo.getString("pageCount");
								String total = pageInfo.getString("total");
								System.out.println("page:" + page + " pageCount:" + pageCount + " total:" + total);
								application.setNotice_total_bpxxtz(Integer.parseInt(total));
							} catch (Exception e) {
								e.printStackTrace();
								return;
							}
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
							return;
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
						return;
					}

					result = httpUtils.post(QRY_NOTICE,
							httpUtils.getJsonQryNotice(jssysdm, page, pageCount, "bpbjtz", "", bjdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							NoticeInfoDatabase database = new NoticeInfoDatabase();
							try {
								String noticeList = data.getString("noticeList");
								List<NoticeInfoModel> list = JSONArray.parseArray(noticeList, NoticeInfoModel.class);
								if (list != null && list.size() > 0) {
									for (NoticeInfoModel model : list) {
										if (model != null) {
											database.insert(model);
										}
									}
								}

								JSONObject pageInfo = data.getJSONObject("pageInfo");
								String page = pageInfo.getString("page");
								String pageCount = pageInfo.getString("pageCount");
								String total = pageInfo.getString("total");
								System.out.println("page:" + page + " pageCount:" + pageCount + " total:" + total);
								application.setNotice_total_bpbjtz(Integer.parseInt(total));
							} catch (Exception e) {
								e.printStackTrace();
								return;
							}
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
							return;
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
						return;
					}

					result = httpUtils.post(QRY_NOTICE,
							httpUtils.getJsonQryNotice(jssysdm, page, pageCount, "bpbjxw", "", bjdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							NoticeInfoDatabase database = new NoticeInfoDatabase();
							try {
								String noticeList = data.getString("noticeList");
								List<NoticeInfoModel> list = JSONArray.parseArray(noticeList, NoticeInfoModel.class);
								if (list != null && list.size() > 0) {
									for (NoticeInfoModel model : list) {
										if (model != null) {
											database.insert(model);
										}
									}
								}

								JSONObject pageInfo = data.getJSONObject("pageInfo");
								String page = pageInfo.getString("page");
								String pageCount = pageInfo.getString("pageCount");
								String total = pageInfo.getString("total");
								System.out.println("page:" + page + " pageCount:" + pageCount + " total:" + total);
								application.setNotice_total_bpbjxw(Integer.parseInt(total));
							} catch (Exception e) {
								e.printStackTrace();
								return;
							}
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
							return;
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
						return;
					}

					result = httpUtils.post(QRY_NOTICE,
							httpUtils.getJsonQryNotice(jssysdm, page, pageCount, "bpjjtz", "", bjdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							NoticeInfoDatabase database = new NoticeInfoDatabase();
							try {
								String noticeList = data.getString("noticeList");
								List<NoticeInfoModel> list = JSONArray.parseArray(noticeList, NoticeInfoModel.class);
								if (list != null && list.size() > 0) {
									for (NoticeInfoModel model : list) {
										if (model != null) {
											database.insert(model);
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								return;
							}
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
							return;
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
						return;
					}

					result = httpUtils.post(QRY_NOTICE,
							httpUtils.getJsonQryNotice(jssysdm, page, pageCount, "bpyyjz", "", bjdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							NoticeInfoDatabase database = new NoticeInfoDatabase();
							try {
								String noticeList = data.getString("noticeList");
								List<NoticeInfoModel> list = JSONArray.parseArray(noticeList, NoticeInfoModel.class);
								if (list != null && list.size() > 0) {
									for (NoticeInfoModel model : list) {
										if (model != null) {
											database.insert(model);
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								return;
							}
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
							return;
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
						return;
					}

					result = httpUtils.post(QRY_LM, httpUtils.getJsonQryLm("0", "100"));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							LmInfoDatabase database = new LmInfoDatabase();
							database.drop();
							String lmInfoList = data.getString("lmInfoList");
							List<LmInfoModel> list = JSONArray.parseArray(lmInfoList, LmInfoModel.class);
							application.notice_total_bpxxxw = 0;
							if (list != null && list.size() > 0) {
								for (LmInfoModel model : list) {
									database.insert(model);
								}
							}
							if (QryNoticeXXXW(jssysdm, page, pageCount, "bpxxxw", "", bjdm, sn)) {
								intent.putExtra(MyApplication.CMD_RESULT, true);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	private boolean QryNoticeXXXW(String jssysdm, String page, String pageCount, String lmdm, String xxztlb,
								  String bjdm, String sn) {
		Intent intent = new Intent(MyApplication.BROADCAST);
		intent.putExtra(MyApplication.BROADCAST_TAG, QRY_NOTICE);
		String result = httpUtils.post(QRY_NOTICE,
				httpUtils.getJsonQryNotice(jssysdm, page, pageCount, lmdm, xxztlb, bjdm));
		if (!TextUtils.isEmpty(result)) {
			try {
				String code = new JSONObject(result).getString("code");
				String message = new JSONObject(result).getString("message");
				if (!code.equals("0000")) {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, message);
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
					return false;
				}
				JSONObject data = new JSONObject(result).getJSONObject("data");
				JSONObject error = data.getJSONObject("error");
				String error_id = error.getString("id");
				String error_message = error.getString("message");
				if (!error_id.equals("0000") && !error_id.equals("9001")) {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, error_message);
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
					return false;
				}
				NoticeInfoDatabase database = new NoticeInfoDatabase();
				try {
					String noticeList = data.getString("noticeList");
					List<NoticeInfoModel> list = JSONArray.parseArray(noticeList, NoticeInfoModel.class);
					if (list != null && list.size() > 0) {
						for (NoticeInfoModel model : list) {
							if (model != null) {
								database.insert(model);
							}
						}
					}

					JSONObject pageInfo = data.getJSONObject("pageInfo");
					String total = pageInfo.getString("total");
					application.notice_total_bpxxxw += Integer.parseInt(total);
					application.setNotice_total_bpxxxw(application.notice_total_bpxxxw);
					application.setNotice_total_bpxxxw(xxztlb, Integer.parseInt(total));
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				intent.putExtra(MyApplication.CMD_RESULT, false);
				intent.putExtra(MyApplication.CMD_MSG, "解析错误");
				intent.putExtra(MyApplication.CMD_SN, sn);
				broadcastManager.sendBroadcast(intent);
			}
		} else {
			intent.putExtra(MyApplication.CMD_RESULT, false);
			intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
			intent.putExtra(MyApplication.CMD_SN, sn);
			broadcastManager.sendBroadcast(intent);
		}
		return false;
	}

	// 查询课程
	public void QryCurriculum(final String jssysdm, final String startDate, final String endDate, final String rybh,
							  final boolean isDel, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_CURRICULUM);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_CURRICULUM,
							httpUtils.getJsonQryCurriculum(jssysdm, startDate, endDate, rybh));

					AppLog.d("课程查询结果" + result.toString());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							CurriculumInfoDatabase database = new CurriculumInfoDatabase();
							if (isDel) {
								database.delete_all();
							}
							try {
								String curriculumInfoList = data.getString("curriculumInfoList");
								List<CurriculumInfoModel> list = JSONArray.parseArray(curriculumInfoList,
										CurriculumInfoModel.class);
								if (list != null && list.size() > 0) {
									for (CurriculumInfoModel model : list) {
										database.insert(model);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询一周课程
	public void QryCurriculum_week(final String jssysdm, final String startDate, final String endDate,
								   final String rybh) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_CURRICULUM + "week");
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_CURRICULUM,
							httpUtils.getJsonQryCurriculum(jssysdm, startDate, endDate, rybh));
					AppLog.d("一周课程查询结果" + result.toString());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							CurriculumInfoDatabase database = new CurriculumInfoDatabase();
							try {
								String curriculumInfoList = data.getString("curriculumInfoList");
								if (TextUtils.isEmpty(rybh)) {
									List<CurriculumInfoModel> list = JSONArray.parseArray(curriculumInfoList,
											CurriculumInfoModel.class);

									SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									Calendar calendar = Calendar.getInstance();
									Date date = format.parse(startDate);
									format = new SimpleDateFormat("yyyy-MM-dd");
									for (int i = 0; i <= 6; i++) {
										calendar.setTime(date);
										calendar.add(Calendar.DAY_OF_MONTH, i);
										database.delete(format.format(calendar.getTime()));
									}

									if (list != null && list.size() > 0) {
										for (CurriculumInfoModel model : list) {
											database.insert(model);
										}
									}
								} else {
									intent.putExtra(MyApplication.CMD_RESULT, true);
									intent.putExtra(MyApplication.CMD_MSG, error_message);
									intent.putExtra("curriculumInfoList", curriculumInfoList);
									broadcastManager.sendBroadcast(intent);
									return;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询一天课程
	public void QryCurriculum_day(final String jssysdm, final String startDate, final String endDate, final String rybh,
								  final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_CURRICULUM + "day");
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_CURRICULUM,
							httpUtils.getJsonQryCurriculum(jssysdm, startDate, endDate, rybh));
					AppLog.d("一天课程查询结果" + result.toString());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							CurriculumInfoDatabase database = new CurriculumInfoDatabase();
							try {
								String curriculumInfoList = data.getString("curriculumInfoList");
								List<CurriculumInfoModel> list = JSONArray.parseArray(curriculumInfoList,
										CurriculumInfoModel.class);

								database.delete(startDate.substring(0, 10));

								if (list != null && list.size() > 0) {
									for (CurriculumInfoModel model : list) {
										database.insert(model);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 考勤上报
	public void CommitAttend(final String xsxh, final AttendInfoModel model) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, COMMIT_ATTEND);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(COMMIT_ATTEND, httpUtils.getJsonCommitAttend(xsxh, model));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询考勤信息
	public void QryAttend(final String jssysdm, final String skjhid, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_ATTEND);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_ATTEND, httpUtils.getJsonQryAttend(jssysdm, skjhid));
					AppLog.d("考勤信息查询结果" + result.toString());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							AttendInfoDatabase database = new AttendInfoDatabase();
							database.deleteByskjhid(skjhid);
							try {
								String attendInfoList = data.getString("attendInfoList");
								List<AttendInfoModel> list = JSONArray.parseArray(attendInfoList,
										AttendInfoModel.class);
								if (list != null && list.size() > 0) {
									for (AttendInfoModel model : list) {
										database.insert(model);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询日期时间
	public void QryDataTime(final String sn, final String orgId) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_DATATIME);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_DATATIME, httpUtils.getJsonQryDatetime(orgId));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							String sectionInfoList = data.getString("sectionInfoList");
							setTime(data.getString("datetime"));
							application.setDqxn(data.getString("dqxn"));
							application.setDqxq(data.getString("dqxq"));
							application.setKxsj(data.getString("kxsj"));
							application.setJssj(data.getString("jssj"));
							application.setZs(data.getString("zs"));
							application.setScreensaver_time(Integer.parseInt(data.getString("screenSaverTime")));
							// AppLog.d(TAG, "datetime:" +
							// data.getString("datetime")
							// + " dqxn:" + data.getString("dqxn")
							// + " dqxq:" + data.getString("dqxq")
							// + " kxsj:" + data.getString("kxsj")
							// + " jssj:" + data.getString("jssj"));

							SectionInfoDatabase database = new SectionInfoDatabase();
							database.delete();
							List<SectionInfoModel> list = JSONArray.parseArray(sectionInfoList, SectionInfoModel.class);
							if (list != null && list.size() > 0) {
								for (SectionInfoModel model : list) {
									database.insert(model);
								}
							}
							String startDeviceTime = data.getString("startDeviceTime");
							String stopDeviceTime = data.getString("stopDeviceTime");
							application.list_off = JSONArray.parseArray(stopDeviceTime, TimerTypeModel.class);
							application.list_on = JSONArray.parseArray(startDeviceTime, TimerTypeModel.class);
							TimeOFFDatabase offDatabase = new TimeOFFDatabase();
							TimeONDatabase onDatabase = new TimeONDatabase();
							offDatabase.delete_all();
							onDatabase.delete_all();
							String offtime=null;
							String ontime=null;
							if (application.list_off != null && application.list_off.size() > 0) {

								for (TimerTypeModel model : application.list_off) {
									offDatabase.insert(model);
									//DATE
									if(model.timerType.equals("REPEAT")){
										if (!TextUtils.isEmpty(model.time)) {
											String nowdata = MyApplication.getTime("yyyy-MM-dd");
											offtime=nowdata+"-"+model.time.replace(":","-").substring(0,5);
										}
									}else{
										offtime=model.date+"-"+model.time.replace(":","-").substring(0,5);
									}
									Log.e("startDeviceTime","startDeviceTimeoff:"+offtime);
								}
							}
							if (application.list_on != null && application.list_on.size() > 0) {

								for (TimerTypeModel model : application.list_on) {
									onDatabase.insert(model);
									if(model.timerType.equals("REPEAT")){
										if (!TextUtils.isEmpty(model.time)) {
											//加一天，第二天开机
											SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
											Date curDate = new Date(System.currentTimeMillis()+86399*1000L);
											String nowdata = formatter.format(curDate);
											ontime=nowdata+"-"+model.time.replace(":","-").substring(0,5);
										}
									}else{
										ontime=model.date+"-"+model.time.replace(":","-").substring(0,5);
									}
									Log.e("startDeviceTime","startDeviceTime:"+ontime);
								}
							}
							if(offtime!=null&ontime!=null){
								Log.e("start","设置start");
								//xmd模拟器打不开这个得注销
//								if(application.apimanager==null){
//									application.apimanager = new XHApiManager();
//									application.apimanager.XHSetPowerOffOnTime(offtime,ontime,true);
//								}else {
//									application.apimanager.XHSetPowerOffOnTime(offtime,ontime,true);
//								}

							}
							application.list_off = offDatabase.query_all();
							application.list_on = onDatabase.query_all();
							application.getOFFONTime(System.currentTimeMillis(), MyApplication.getTime("yyyy-MM-dd"));
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (Exception e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	private void setTime(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = sdf.parse(time);
			long l = d.getTime();

			Intent intent = new Intent();
			intent.setAction("set_system_time");
			intent.putExtra("time", l);
			application.sendBroadcast(intent);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	// 查询学生信息
	public void QryStudent(final String bjdm, final String rybh, final String sn, final boolean isDelStudent) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_STUDENT);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_STUDENT, httpUtils.getJsonStudentInfo(bjdm, rybh));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							try {
								JSONObject classInfo = data.getJSONObject("classInfo");
								ClassInfoModel infoModel = new ClassInfoModel();
								infoModel.id = classInfo.getString("id");
								infoModel.ssxy = classInfo.getString("ssxy");
								infoModel.ssxq = classInfo.getString("ssxq");
								infoModel.ssxqmc = classInfo.getString("ssxqmc");
								infoModel.ssxb = classInfo.getString("ssxb");
								infoModel.ssxbmc = classInfo.getString("ssxbmc");
								infoModel.sszy = classInfo.getString("sszy");
								infoModel.sszymc = classInfo.getString("sszymc");
								infoModel.bjdm = classInfo.getString("bjdm");
								infoModel.bjmc = classInfo.getString("bjmc");
								infoModel.nj = classInfo.getString("nj");
								infoModel.xz = classInfo.getString("xz");
								infoModel.xzshow = classInfo.getString("xzshow");
								infoModel.bjzxrs = classInfo.getString("bjzxrs");
								infoModel.bjsjzxrs = classInfo.getString("bjsjzxrs");
								infoModel.fdy = classInfo.getString("fdy");
								infoModel.fdyxm = classInfo.getString("fdyxm");
								infoModel.fdydh = classInfo.getString("fdydh");
								infoModel.fdyzp = classInfo.getString("fdyzp");
								ClassInfoDatabase classInfoDatabase = new ClassInfoDatabase();
								classInfoDatabase.delete();
								classInfoDatabase.insert(infoModel);
								application.classInfoModel = infoModel;
							} catch (Exception e1) {
								e1.printStackTrace();
							}

							try {
								StudentInfoDatabase database = new StudentInfoDatabase();
								if (isDelStudent) {
									database.drop();
								}
								String studentList = data.getString("studentList");
								List<StudentInfoModel> list = JSONArray.parseArray(studentList, StudentInfoModel.class);
								if (list != null && list.size() > 0) {
									for (StudentInfoModel model : list) {
										database.insert(model);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							intent.putExtra("studentList", data.getString("studentList"));
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 操作授权
	public void Permission(final String jssysdm, final String operateType, final String authorizationType,
						   final String userName, final String password, final String jobCardNo) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, OPERATE_AUTHORIZATION);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(OPERATE_AUTHORIZATION, httpUtils.getJsonPermission(jssysdm,
							operateType, authorizationType, userName, password, jobCardNo));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra("userName", data.getString("userName"));
							intent.putExtra("rybm", data.getString("rybh"));
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询设备列表
	public void QryEquList(final String jssysdm, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_EQU_LIST);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_EQU_LIST, httpUtils.getJsonEQList(jssysdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}

							EquListDatabase equListDatabase = new EquListDatabase();
							equListDatabase.delete_all();
							TelMenuDatabase telMenuDatabase = new TelMenuDatabase();
							telMenuDatabase.delete_all();
							MenuAndEqDatabase menuAndEqDatabase = new MenuAndEqDatabase();
							menuAndEqDatabase.delete_all();
							try {
								String telMenuInfoList = data.getString("telMenuInfoList");
								List<TelMenuInfoModel> list = JSONArray.parseArray(telMenuInfoList,
										TelMenuInfoModel.class);
								if (list != null && list.size() > 0) {
									for (TelMenuInfoModel model : list) {
										telMenuDatabase.insert(model);
										List<EquInfoModel> equInfoModels = JSONArray.parseArray(model.equInfoList,
												EquInfoModel.class);
										if (equInfoModels != null && equInfoModels.size() > 0) {
											for (EquInfoModel equ : equInfoModels) {
												equListDatabase.insert(equ);
												MenuAndEqModel menuAndEq = new MenuAndEqModel();
												menuAndEq.menuId = model.menuId;
												menuAndEq.equCode = equ.equCode;
												menuAndEqDatabase.insert(menuAndEq);
											}
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询播放任务
	public void QryPlayTask(final String jssysdm, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_PLAY_TASK);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_PLAY_TASK, httpUtils.getJsonPlayTask(jssysdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}

							try {
								String playTaskInfoList = data.getString("playTaskInfoList");
								application.playTaskModels = JSONArray.parseArray(playTaskInfoList,
										PlayTaskModel.class);
								PlayTaskDatabase database = new PlayTaskDatabase();
								database.delete_all();
								if (application.playTaskModels != null && application.playTaskModels.size() > 0) {
									for (PlayTaskModel model : application.playTaskModels) {
										database.insert(model);
									}
									application.playTaskModels = database.query_all();
									intent.putExtra(MyApplication.CMD_RESULT, true);
									intent.putExtra(MyApplication.CMD_MSG, error_message);
									intent.putExtra(MyApplication.CMD_SN, sn);
									broadcastManager.sendBroadcast(intent);
								} else {
									intent.putExtra(MyApplication.CMD_RESULT, false);
									intent.putExtra(MyApplication.CMD_MSG, "暂无数据");
									intent.putExtra(MyApplication.CMD_SN, sn);
									broadcastManager.sendBroadcast(intent);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询教师信息
	public void QryTeacher(final String jssysdm, final String skjsbm, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_TEACHER);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_TEACHER, httpUtils.getJsonTeacher(jssysdm, skjsbm));
					AppLog.d("教师信息查询结果" + result.toString());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							TeacherInfoDatabase database = new TeacherInfoDatabase();
							database.drop();
							try {
								String teacherInfoList = data.getString("teacherInfoList");
								List<TeacherInfoModel> list = JSONArray.parseArray(teacherInfoList,
										TeacherInfoModel.class);
								if (list != null && list.size() > 0) {
									for (TeacherInfoModel teacherInfoModel : list) {
										database.insert(teacherInfoModel);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询人员信息
	public void QryPerson(final String jssysdm, final String cardNo) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_PERSON);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_PERSON, httpUtils.getJsonPerson(jssysdm, cardNo));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}

							JSONObject personInfo = data.getJSONObject("personInfo");
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra("xm", personInfo.getString("xm"));
							intent.putExtra("js", personInfo.getString("js"));
							intent.putExtra("rybh", personInfo.getString("rybh"));
							intent.putExtra("xsbj", personInfo.getString("xsbj"));
							intent.putExtra("jsbm", personInfo.getString("jsbm"));
							intent.putExtra("cardid", personInfo.getString("cardid"));
							intent.putExtra("chaoxingUser", personInfo.getString("chaoxingUser"));
							intent.putExtra("fid", personInfo.getString("fid"));
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 控制信息同步
	public void CommitControl(final String jssysdm, final List<ControlModel> list, final String sn) {
		if (list == null) {
			return;
		}
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				org.json.JSONArray jsonArray = new org.json.JSONArray();
				try {
					for (int i = 0; i < list.size(); i++) {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("id", list.get(i).id);
						jsonObject.put("type", list.get(i).type);
						jsonObject.put("name", list.get(i).name);
						jsonObject.put("status", list.get(i).status);
						jsonArray.put(jsonObject);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, COMMIT_CONTROL);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(COMMIT_CONTROL, httpUtils.getJsonCommitControl(jssysdm, jsonArray));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询荣誉墙信息
	public void QryPrize(final String jssysdm, final String xsxh, final String sn, final boolean isDel) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_PRIZE);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_PRIZE, httpUtils.getJsonPrize(jssysdm, xsxh));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							PrizeInfoDatabase database = new PrizeInfoDatabase();
							if (isDel) {
								database.drop();
							}
							try {
								String prizeInfoList = data.getString("prizeInfoList");
								List<PrizeInfoModel> list = JSONArray.parseArray(prizeInfoList, PrizeInfoModel.class);
								if (list != null && list.size() > 0) {
									for (PrizeInfoModel model : list) {
										database.insert(model);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询小黑板信息
	public void QryBlackboard(final String jssysdm, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_BLACKBOARD);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_BLACKBOARD, httpUtils.getJsonBlackboard(jssysdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							BlackboardInfoDatabase database = new BlackboardInfoDatabase();
							database.delete();
							if (!error_id.equals("9001")) {
								JSONObject blackboardInfo = data.getJSONObject("blackboardInfo");
								BlackboardInfoModel model = new BlackboardInfoModel();
								model.tagName = blackboardInfo.getString("tagName");
								model.content = blackboardInfo.getString("content");
								database.insert(model);
							}

							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询值日信息
	public void QryOnDuty(final String jssysdm, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_ONDUTY);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_ONDUTY, httpUtils.getJsonBlackboard(jssysdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							OnDutyInfoDatabase database = new OnDutyInfoDatabase();
							database.delete_all();
							String onDutyInfoList = data.getString("onDutyInfoList");
							List<OnDutyInfoModel> list = JSONArray.parseArray(onDutyInfoList, OnDutyInfoModel.class);
							if (list != null && list.size() > 0) {
								for (OnDutyInfoModel model : list) {
									database.insert(model);
								}
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询学校信息
	public void QrySchool(final String jssysdm, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_SCHOOL);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_SCHOOL, httpUtils.getJsonSchool(jssysdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							SchoolInfoDatabase database = new SchoolInfoDatabase();
							database.delete_all();
							String schoolInfoList = data.getString("schoolInfoList");
							List<SchoolInfoModel> list = JSONArray.parseArray(schoolInfoList, SchoolInfoModel.class);
							if (list != null && list.size() > 0) {
								for (SchoolInfoModel model : list) {
									database.insert(model);
								}
							}

							XyxxDatabase xyxxDatabase = new XyxxDatabase();
							xyxxDatabase.delete();
							String xyxxInfoList = data.getString("xyxxInfoList");
							List<XyxxTypeModel> list1 = JSONArray.parseArray(xyxxInfoList, XyxxTypeModel.class);
							if (list1 != null && list1.size() > 0) {
								for (XyxxTypeModel model : list1) {
									xyxxDatabase.insert(model);
									//xmd设置学校名称
									application.schoolName=model.xymc;
								}
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询问卷调查信息
	public void QryQuest(final String jssysdm, final String questionnaireCode, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_QUEST);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_QUEST, httpUtils.getJsonQuset(jssysdm, questionnaireCode));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							try {
								doQuest(data.getString("questionnaireInfoList"));
							} catch (Exception e) {
								e.printStackTrace();
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	private void doQuest(String questionnaireInfoList) {
		QuestionNaireDatabase questionNaireDatabase = new QuestionNaireDatabase();
		QuestionDatabase questionDatabase = new QuestionDatabase();
		OptionDatabase optionDatabase = new OptionDatabase();
		AnswerDatabase answerDatabase = new AnswerDatabase();
		questionNaireDatabase.drop();
		questionDatabase.delete_all();
		optionDatabase.delete_all();
		answerDatabase.delete_all();
		List<QuestionNaireModel> questionNaireModels = JSONArray.parseArray(questionnaireInfoList,
				QuestionNaireModel.class);
		if (questionNaireModels != null && questionNaireModels.size() > 0) {
			for (QuestionNaireModel questionNaireModel : questionNaireModels) {
				questionNaireDatabase.insert(questionNaireModel);
				List<QuestionModel> questionModels = questionNaireModel.questionList;
				if (questionModels != null && questionModels.size() > 0) {
					for (QuestionModel questionModel : questionModels) {
						questionDatabase.insert(questionModel);
						List<OptionModel> optionModels = questionModel.optionList;
						if (optionModels != null && optionModels.size() > 0) {
							for (OptionModel optionModel : optionModels) {
								optionDatabase.insert(optionModel);
							}
						}
					}
				}
			}
		}
	}

	// 问卷调查信息提交
	public void CommitQuest(final String jssysdm, final QuestionNaireModel model) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, COMMIT_QUEST);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(COMMIT_QUEST, httpUtils.getJsonCommitQuset(jssysdm, model));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 设备运行信息提交
	public void CommitEquWorkInfo(final String jssysdm, final String path, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, COMMIT_EQUWORKINFO);
				if (application.checkNetworkHw()) {
					EquWorkInfoModel model = new EquWorkInfoModel();
					model.startTime = application.getOpenTime();
					model.nextStopTime = application.time_off;
					model.nextStartTime = application.time_on;
					model.cpu = application.getProcessCpuRate() + "";
					model.physMem = application.getTotalMemory() + "";
					model.usedMem = (application.getTotalMemory() - application.getAvailMemory()) + "";
					model.diskSpace = application.getTotalDisk() + "";
					model.usedSpace = (application.getTotalDisk() - application.getAvailDisk()) + "";
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					options.inPurgeable = true;
					options.inInputShareable = true;
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					try {
						InputStream is = new FileInputStream(path);
						Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
						if (bitmap != null) {
							WindowManager wm = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
							if (wm.getDefaultDisplay().getRotation() == Surface.ROTATION_180) {
								Matrix matrix = new Matrix();
								matrix.postRotate(180);
								bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
										matrix, true);
							}
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							int quality = 90;
							bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
							while (baos.toByteArray().length / 1024 > 500) {
								quality -= 10;
								baos.reset();
								bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
							}
							byte[] appicon = baos.toByteArray();
							model.currentScreen = Base64.encodeToString(appicon, Base64.DEFAULT);
						}
						is.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					String result = httpUtils.post(COMMIT_EQUWORKINFO, httpUtils.getJsonCommitEqu(jssysdm, model));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 设备模式任务下发
	public void getEquModel(final String jssysdm, final String sn) {
		application.executoService.execute(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_EQUMODEL);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_EQUMODEL, httpUtils.getJsonEquModel(jssysdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							String equModelTaskList = data.getString("equModelTaskList");
							EquModelTaskDatabase database = new EquModelTaskDatabase();
							database.delete();
							List<EquModelTaskModel> list = JSONArray.parseArray(equModelTaskList,
									EquModelTaskModel.class);
							if (list != null && list.size() > 0) {
								for (EquModelTaskModel model : list) {
									database.insert(model);
								}
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 考试安排查询
	public void getExam(final String jssysdm, final String sn) {
		application.executoService.execute(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_EXAM);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_EXAM, httpUtils.getJsonExam(jssysdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							String examinationInfoList = data.getString("examinationInfoList");
							ExaminationDatabase database = new ExaminationDatabase();
							database.delete();
							List<ExaminationModel> list = JSONArray.parseArray(examinationInfoList,
									ExaminationModel.class);
							if (list != null && list.size() > 0) {
								for (ExaminationModel model : list) {
									database.insert(model);
								}
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 命令执行反馈提交
	public void commitBack(final String cmdSn, final String id, final String message) {
		if (TextUtils.isEmpty(cmdSn)) {
			return;
		}
		Log.e(TAG, "-----------------commitBack----------------" + cmdSn);
		application.executoService.execute(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, COMMIT_BACK);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post_accSys(COMMIT_BACK, httpUtils.getJsonBack(cmdSn, id, message));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 获取节目单信息
	public void getMedia() {
		application.executoService.execute(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_MEDIA);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_MEDIA, httpUtils.getJsonMedia());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra("mediaInfo", data.getString("mediaInfo"));
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 获取页面布局信息
	public void qryPageLayout(final String sn) {
		application.executoService.execute(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_PAGE);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_PAGE, httpUtils.getJsonMedia());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_SN, sn);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_SN, sn);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							PageLayoutDatabase database = new PageLayoutDatabase();
							database.delete_all();
							String pageLayoutInfoList = data.getString("pageLayoutInfoList");
							List<PageLayoutModel> list = JSONArray.parseArray(pageLayoutInfoList,
									PageLayoutModel.class);
							if (list != null && list.size() > 0) {
								database.insert(list.get(0));
							}
							application.setRootResPath(data.getString("rootResPath"));
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_SN, sn);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_SN, sn);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_SN, sn);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_SN, sn);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 提交文件下载进度反馈信息
	public void commitFileProcess(final List<FileDownloadProcessModel> list) {
		application.executoService.execute(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, COMMIT_FILEPROCESS);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(COMMIT_FILEPROCESS, httpUtils.getJsonFileProcess(list));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 获取小视频信息
	public void qryVideo(final String sn, final String jssysdm, final String videoType, final String bjdm,
						 final String relaObjValue) {
		application.executoService.execute(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_VIDEO);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_VIDEO,
							httpUtils.getJsonVideo(jssysdm, videoType, bjdm, relaObjValue));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_SN, sn);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_SN, sn);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							VideoDatabase database = new VideoDatabase();
							database.delete_all();
							String smallVideoList = data.getString("smallVideoList");
							List<VideoInfoModel> list = JSONArray.parseArray(smallVideoList, VideoInfoModel.class);
							if (list != null && list.size() > 0) {
								for (VideoInfoModel model : list) {
									database.insert(model);
								}
							}

							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_SN, sn);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_SN, sn);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_SN, sn);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_SN, sn);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 更新小视频信息
	public void commitVideo(final String sn, final VideoInfoModel model) {
		application.executoService.execute(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_UPDATE_VIDEO);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_UPDATE_VIDEO, httpUtils.getJsonVideo(model));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_SN, sn);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_SN, sn);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_SN, sn);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_SN, sn);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_SN, sn);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_SN, sn);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 更新授权用户信息
	public void qryUser(final String sn) {
		application.executoService.execute(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_UPDATE_USER);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_UPDATE_USER, httpUtils.getJsonUser());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_SN, sn);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_SN, sn);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							UserInfoDatabase database = new UserInfoDatabase();
							database.delete_all();
							String userInfoList = data.getString("userInfoList");
							List<UserInfoModel> list = JSONArray.parseArray(userInfoList, UserInfoModel.class);
							if (list != null && list.size() > 0) {
								for (UserInfoModel model : list) {
									database.insert(model);
								}
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_SN, sn);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_SN, sn);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_SN, sn);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_SN, sn);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询课程信息
	public void QryClassInfo(final String skjhid, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_CLASSINFO);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_CLASSINFO, httpUtils.getJsonClassInfo(skjhid));
					AppLog.d("课程信息查询结果" + result.toString());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							CoursewareDatabase coursewareDatabase = new CoursewareDatabase();
							coursewareDatabase.delete_by_skjhid(skjhid);
							ClassFeedbackDatabase classFeedbackDatabase = new ClassFeedbackDatabase();
							classFeedbackDatabase.delete_by_skjhid(skjhid);

							String coursewareList = data.getString("coursewareList");
							List<CoursewareModel> list = JSONArray.parseArray(coursewareList, CoursewareModel.class);
							if (list != null && list.size() > 0) {
								for (CoursewareModel model : list) {
									coursewareDatabase.insert(model);
								}
							}
							String classFeedbackList = data.getString("classFeedbackList");
							List<ClassFeedbackModel> list2 = JSONArray.parseArray(classFeedbackList,
									ClassFeedbackModel.class);
							if (list2 != null && list2.size() > 0) {
								for (ClassFeedbackModel model : list2) {
									classFeedbackDatabase.insert(model);
								}
							}

							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							intent.putExtra("sknl", data.getString("sknl"));
							intent.putExtra("zynr", data.getString("zynr"));
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 提交课后反馈信息
	public void commitClassFeedback(final String sn, final ClassFeedbackModel model) {
		application.executoService.execute(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, COMMIT_CLASSFEEDBACK);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(COMMIT_CLASSFEEDBACK, httpUtils.getJsonClassFeedback(model));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_SN, sn);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_SN, sn);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_SN, sn);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_SN, sn);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_SN, sn);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_SN, sn);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 提交打卡信息
	public void commitClockInfo(final List<ClockInfoModel> list) {
		application.executoService.execute(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, COMMIT_CLOCKINFO);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(COMMIT_CLOCKINFO, httpUtils.getJsonClockInfo(list));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							if (list != null && list.size() > 0) {
								ClockInfoDatabase database = new ClockInfoDatabase();
								for (ClockInfoModel model : list) {
									model.isupload = "1";
									database.update(model);
								}
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 提交打卡信息2
	public void commitClockInfo2(final String card) {
		application.executoService.execute(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, COMMIT_CLOCKINFO2);
				Log.e("COMMIT_CLOCKINFO2","COMMIT_CLOCKINFO2:jinjinijin");
				if (application.checkNetworkHw()) {

					String result = httpUtils.post(COMMIT_CLOCKINFO2, httpUtils.getJsonClockInfo2(card));
					Log.e("COMMIT_CLOCKINFO2","COMMIT_CLOCKINFO2:"+result);
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra("code", "10001");
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra("code", "9999");
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}

							if (!error_id.equals("0000") && !error_id.equals("9999")) {
								intent.putExtra("code", "9001");
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							String personAttendInfo = data.getString("personAttendInfo");
							if (!TextUtils.isEmpty(personAttendInfo)) {
								PersonnelAttendanceModel model = JSON.parseObject(personAttendInfo,
										PersonnelAttendanceModel.class);
								if (model != null) {
									PersonnelAttendanceDatabase database = new PersonnelAttendanceDatabase();
									database.delete(model.id);
									database.insert(model);
								}
							}
							String xskqInfoList = data.getString("xskqInfoList");
							if (!TextUtils.isEmpty(xskqInfoList)) {
								List<AttendInfoModel> list = JSONArray.parseArray(xskqInfoList, AttendInfoModel.class);
								if (list != null && list.size() > 0) {
									AttendInfoDatabase database = new AttendInfoDatabase();
									for (AttendInfoModel model : list) {
										database.delete(model.id);
										database.insert(model);
									}
								}
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra("personAttendInfo", personAttendInfo);
							intent.putExtra("xskqInfoList", xskqInfoList);
							intent.putExtra("clockInfo", data.getString("clockInfo"));
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询考勤教师信息
	public void QryTeacherList(final boolean isDel, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_TEACHERLIST);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_TEACHERLIST, httpUtils.getJsonTeacherList());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							PersonInfoDatabase database = new PersonInfoDatabase();
							if (isDel) {
								database.delete_all();
							}
							String personList = data.getString("personList");
							List<PersonInfoModel> list = JSONArray.parseArray(personList, PersonInfoModel.class);
							if (list != null && list.size() > 0) {
								for (PersonInfoModel model : list) {
									if (model != null) {
										database.insert(model);
									}
								}
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询考勤规则信息
	public void QryClockRule(final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_CLOCKRULE);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_CLOCKRULE, httpUtils.getJsonClockRule());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							ClockRuleDatabase database = new ClockRuleDatabase();
							database.delete_all();

							try {
								JSONObject TeacherClockRule = data.getJSONObject("teacherClockRule");
								ClockRuleModel model = new ClockRuleModel();
								model.sbkyxtq = TeacherClockRule.getString("sbkyxtq");
								model.xbkyxtq = TeacherClockRule.getString("xbkyxtq");
								model.sbkyxyh = TeacherClockRule.getString("sbkyxyh");
								model.xbkyxyh = TeacherClockRule.getString("xbkyxyh");
								model.jbddk = TeacherClockRule.getString("jbddk");
								model.jbdxxdk = TeacherClockRule.getString("jbdxxdk");
								model.xbbdk = TeacherClockRule.getString("xbbdk");
								model.yxcd = TeacherClockRule.getString("yxcd");
								model.yzcd = TeacherClockRule.getString("yzcd");
								model.cgcd = TeacherClockRule.getString("cgcd");
								model.dkgz = TeacherClockRule.getString("dkgz");
								model.js = "1";
								application.clockRule_teather = model;
								database.insert(model);

								JSONObject studentClockRule = data.getJSONObject("studentClockRule");
								model = new ClockRuleModel();
								model.sbkyxtq = studentClockRule.getString("sbkyxtq");
								model.xbkyxtq = studentClockRule.getString("xbkyxtq");
								model.sbkyxyh = studentClockRule.getString("sbkyxyh");
								model.xbkyxyh = studentClockRule.getString("xbkyxyh");
								model.jbddk = studentClockRule.getString("jbddk");
								model.jbdxxdk = studentClockRule.getString("jbdxxdk");
								model.xbbdk = studentClockRule.getString("xbbdk");
								model.yxcd = studentClockRule.getString("yxcd");
								model.yzcd = studentClockRule.getString("yzcd");
								model.cgcd = studentClockRule.getString("cgcd");
								model.dkgz = studentClockRule.getString("dkgz");
								model.js = "2";
								application.clockRule_student = model;
								database.insert(model);
							} catch (Exception e) {
								e.printStackTrace();
							}

							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}
	// 查询选课人员考勤信息
	public void QryPersonnelAttendanceList(String skjhid)  {

		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_PERSONATTEND);
				if (application.checkNetworkHw()) {
					String result =httpUtils.post(QRY_PERSONATTEND3, httpUtils.getQryPersonnelAttendanceList3(skjhid));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							PersonnelAttendanceDatabase database = new PersonnelAttendanceDatabase();
							try {
								String personnelAttendanceList = data.getString("personnelAttendanceList");
								Log.e("我的", "查询人员考勤信息：" + personnelAttendanceList);

								intent.putExtra(MyApplication.CMD_RESULT, true);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra("personnelAttendanceList", personnelAttendanceList);
								broadcastManager.sendBroadcast(intent);
								return;
							} catch (Exception e) {
								e.printStackTrace();
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}
	// 查询人员考勤信息
	public void QryPersonAttend(final String sn, final String date, final String rybh) {

		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_PERSONATTEND);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_PERSONATTEND, httpUtils.getJsonPersonAttend(date, rybh));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							PersonnelAttendanceDatabase database = new PersonnelAttendanceDatabase();
							try {
								String personnelAttendanceList = data.getString("personnelAttendanceList");
								Log.e("我的", "查询人员考勤信息：" + personnelAttendanceList);
								if (TextUtils.isEmpty(rybh)) {
									database.drop();
									List<PersonnelAttendanceModel> list=new ArrayList<>();
									list.clear();
									list = JSONArray.parseArray(personnelAttendanceList,
											PersonnelAttendanceModel.class);
									if (list != null && list.size() > 0) {
										for (PersonnelAttendanceModel model : list) {
											if (model != null) {
												database.insert(model);
											}
										}
									}
								} else {
									intent.putExtra(MyApplication.CMD_RESULT, true);
									intent.putExtra(MyApplication.CMD_MSG, error_message);
									intent.putExtra(MyApplication.CMD_SN, sn);
									intent.putExtra("personnelAttendanceList", personnelAttendanceList);
									broadcastManager.sendBroadcast(intent);
									return;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询人员考勤信息
	public void QryPersonAttend2(final String sn, final String date, final String rybh) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_PERSONATTEND2);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_PERSONATTEND2, httpUtils.getJsonPersonAttend(date, rybh));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							String personnelAttendanceList = data.getString("personnelAttendanceList");
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							intent.putExtra("personnelAttendanceList", personnelAttendanceList);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 提交人员考勤信息
	public void commitPersonnelAttendance(final List<PersonnelAttendanceModel> list) {
		application.executoService.execute(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, COMMIT_PERSONATTEND);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(COMMIT_PERSONATTEND,
							httpUtils.getJsonCommitPersonnelAttendance(list));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询最近考勤信息
	public void QryNearAttend(final String cardId, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_NEARATTEND);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_NEARATTEND, httpUtils.getJsonNearAttend(cardId));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							intent.putExtra("currentTime", data.getString("currentTime"));
							intent.putExtra("nextTime", data.getString("nextTime"));
							intent.putExtra("currentPosition", data.getString("currentPosition"));
							intent.putExtra("nextPosition", data.getString("nextPosition"));
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询投票主题信息
	public void QryVote(final String page, final String pageCount, final String voteThemeId, final String activityKind,
						final String isDel, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_VOTE + "_" + activityKind);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_VOTE,
							httpUtils.getJsonVote(page, pageCount, voteThemeId, activityKind));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject pageInfo = data.getJSONObject("pageInfo");
							String page = pageInfo.getString("page");
							String pageCount = pageInfo.getString("pageCount");
							String total = pageInfo.getString("total");
							System.out.println("page:" + page + " pageCount:" + pageCount + " total:" + total);
							if (activityKind.equals("vote")) {
								application.setVote_total(Integer.parseInt(total));
							} else if (activityKind.equals("activity")) {
								application.setActivity_total(Integer.parseInt(total));
							}
							doVote(data.getString("voteThemeList"), activityKind, isDel);
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	private void doVote(String voteThemeList, String activityKind, String isDel) {
		VoteThemeDatabase voteThemeDatabase = new VoteThemeDatabase();
		VoteOptionDatabase voteOptionDatabase = new VoteOptionDatabase();
		VoteInfoDatabase voteInfoDatabase = new VoteInfoDatabase();
		if ("vote".equals(isDel) || "activity".equals(isDel)) {
			List<VoteThemeModel> list = voteThemeDatabase.query("activityKind = ?", new String[]{isDel});
			if (list != null && list.size() > 0) {
				for (VoteThemeModel model : list) {
					voteOptionDatabase.delete_by_voteThemeId(model.voteThemeId);
					voteInfoDatabase.delete_by_voteThemeId(model.voteThemeId);
				}
				voteThemeDatabase.delete_by_activityKind(isDel);
			}
		} else if ("all".equals(isDel)) {
			voteThemeDatabase.delete_all();
			voteOptionDatabase.delete_all();
			voteInfoDatabase.delete_all();
		}
		List<VoteThemeModel> voteThemeModels = JSONArray.parseArray(voteThemeList, VoteThemeModel.class);
		if (voteThemeModels != null && voteThemeModels.size() > 0) {
			for (VoteThemeModel voteThemeModel : voteThemeModels) {
				voteThemeDatabase.insert(voteThemeModel);
				List<VoteOptionModel> voteOptionModels = voteThemeModel.voteOptionList;
				if (voteOptionModels != null && voteOptionModels.size() > 0) {
					for (VoteOptionModel voteOptionModel : voteOptionModels) {
						voteOptionDatabase.insert(voteOptionModel, voteThemeModel.voteThemeId);
						List<VoteInfoModel> voteInfoModels = voteOptionModel.voteInfoList;
						if (voteInfoModels != null && voteInfoModels.size() > 0) {
							for (VoteInfoModel voteInfoModel : voteInfoModels) {
								voteInfoDatabase.insert(voteInfoModel);
							}
						}
					}
				}
			}
		}
	}

	// 提交投票信息
	public void commitVote(final List<VoteInfoModel> list) {
		application.executoService.execute(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, COMMIT_VOTE);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(COMMIT_VOTE, httpUtils.getJsonCommitVote(list));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询图书信息
	public void QryBook(final String jssysdm, final String page, final String pageCount, final String dataSources,
						final boolean isDel, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_BOOK);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_BOOK,
							httpUtils.getJsonBook(jssysdm, page, pageCount, dataSources));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject pageInfo = data.getJSONObject("pageInfo");
							String page = pageInfo.getString("page");
							String pageCount = pageInfo.getString("pageCount");
							String total = pageInfo.getString("total");
							System.out.println("page:" + page + " pageCount:" + pageCount + " total:" + total);

							BooksInfoDatabase database = new BooksInfoDatabase();
							if (isDel) {
								database.delete_all();
							}
							String booksList = data.getString("booksList");
							List<BooksInfoModel> booksInfoModels = JSONArray.parseArray(booksList,
									BooksInfoModel.class);
							if (booksInfoModels != null && booksInfoModels.size() > 0) {
								for (BooksInfoModel model : booksInfoModels) {
									database.insert(model);
								}
							}

							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 相册查询接口
	public void QryPhoto(final String jssysdm, final String bjdm, final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_PHOTO);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_PHOTO, httpUtils.getQryPhoto(jssysdm, bjdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							PicDreOrActivityDatabase database = new PicDreOrActivityDatabase();
							database.delete_all();
							String activityList = data.getString("activityList");
							Log.e("activityList","activityList:"+activityList);
							doPhoto(activityList);
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	private void doPhoto(String activityList) {
		PicDreOrActivityDatabase database = new PicDreOrActivityDatabase();
		List<PicDreOrActivityModel> list = JSONArray.parseArray(activityList, PicDreOrActivityModel.class);
		if (list != null && list.size() > 0) {
			for (PicDreOrActivityModel model : list) {
				if (model != null) {
					database.insert(model);
					if (!TextUtils.isEmpty(model.next)) {
						doPhoto(model.next);
					}
				}
			}
		}
	}

	// 自定义菜单查询
	public void QryDiyMenu(final String jssysdm) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_DIYMENU);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_DIYMENU, httpUtils.getJsonDiyMenu(jssysdm));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							DiyMenuDatabase database = new DiyMenuDatabase();
							database.delete_all();
							String list = data.getString("list");
							List<DiyMenuModel> diyMenuModels = JSONArray.parseArray(list, DiyMenuModel.class);
							if (diyMenuModels != null && diyMenuModels.size() > 0) {
								for (DiyMenuModel model : diyMenuModels) {
									if (model != null) {
										database.insert(model);
									}
								}
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 之星列表查询
	public void QryStar(final String jssysdm, final String bjdm, final String page, final String pageCount,
						final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_STAR);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_STAR, httpUtils.getQryStar(jssysdm, bjdm, page, pageCount));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}

							StarDatabase database = new StarDatabase();
							database.drop();

							String list = data.getString("list");
							List<StarModel> starModels = JSONArray.parseArray(list, StarModel.class);
							if (starModels != null && starModels.size() > 0) {
								for (StarModel model : starModels) {
									database.insert(model);
								}
							}

							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 获取课后查询问卷
	public void QryQuestClass(final String jssysdm, final String skjhid) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_QUSET_CLASS);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_QUSET_CLASS, httpUtils.getJsonQryQusetClass(jssysdm, skjhid));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							try {
								doQuestClass(data.getString("classQuestionnaireList"));
							} catch (Exception e) {
								e.printStackTrace();
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra("classQuestionnaireList", data.getString("classQuestionnaireList"));
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 获取课后查询问卷
	public void commitQuestClass(final String jssysdm, final List<AnswerModel> list) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, COMMIT_QUEST_CLASS);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(COMMIT_QUEST_CLASS,
							httpUtils.getJsonCommitQusetClass(jssysdm, list));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	private void doQuestClass(String questionnaireInfoList) {
		QuestionNaireDatabase questionNaireDatabase = new QuestionNaireDatabase();
		QuestionDatabase questionDatabase = new QuestionDatabase();
		OptionDatabase optionDatabase = new OptionDatabase();
		// AnswerDatabase answerDatabase = new AnswerDatabase();
		// questionNaireDatabase.drop();
		// questionDatabase.delete_all();
		// optionDatabase.delete_all();
		// answerDatabase.delete_all();
		List<QuestionNaireModel> questionNaireModels = JSONArray.parseArray(questionnaireInfoList,
				QuestionNaireModel.class);
		if (questionNaireModels != null && questionNaireModels.size() > 0) {
			for (QuestionNaireModel questionNaireModel : questionNaireModels) {
				questionNaireDatabase.insert(questionNaireModel);
				List<QuestionModel> questionModels = questionNaireModel.questionList;
				if (questionModels != null && questionModels.size() > 0) {
					for (QuestionModel questionModel : questionModels) {
						questionDatabase.insert(questionModel);
						List<OptionModel> optionModels = questionModel.optionList;
						if (optionModels != null && optionModels.size() > 0) {
							for (OptionModel optionModel : optionModels) {
								optionDatabase.insert(optionModel);
							}
						}
					}
				}
			}
		}
	}

	// logo查询
	public void QryLogo(final String sn) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_LOGO);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_LOGO, httpUtils.getJsonQryLogo());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								intent.putExtra(MyApplication.CMD_SN, sn);
								broadcastManager.sendBroadcast(intent);
								return;
							}

							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra(MyApplication.CMD_SN, sn);
							intent.putExtra("logoUrl", data.getString("logoUrl"));
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							intent.putExtra(MyApplication.CMD_SN, sn);
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					intent.putExtra(MyApplication.CMD_SN, sn);
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 屏保资源获取
	public void QryScreensaver() {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_SCREENSAVER);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_SCREENSAVER, httpUtils.getJsonQryScreensaver());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							ScreensaverDatabase database = new ScreensaverDatabase();
							database.drop();
							List<ScreensaverModel> list = JSONArray.parseArray(data.getString("data"),
									ScreensaverModel.class);
							if (list != null && list.size() > 0) {
								for (ScreensaverModel model : list) {
									database.insert(model);
								}
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 留言对象查询
	public void QryLeaveUser(final String cardNo, final String userType, final String page, final String pageCount) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_LEAVE_USER);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_LEAVE_USER,
							httpUtils.getJsonQryLeaveUser(cardNo, userType, page, pageCount));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra("personInfoList", data.getString("personInfoList"));
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 家校留言提交
	public void commitLeaveMsg(final LeaveMsgModel leaveMsgModel) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, COMMIT_LEAVE_MSG);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(COMMIT_LEAVE_MSG, httpUtils.getJsonCommitLeaveMsg(leaveMsgModel));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 家校留言查询接口
	public void qryLeaveMsg(final String formRyType, final String formRybh, final String toRyType, final String toRybh,
							final String startDate, final String endDate, final String page, final String pageCount) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_LEAVE_MSG);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_LEAVE_MSG, httpUtils.getJsonQryLeaveMsg(formRyType, formRybh,
							toRyType, toRybh, startDate, endDate, page, pageCount));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra("leaveMsmList", data.getString("leaveMsmList"));
							JSONObject pageInfo = data.getJSONObject("pageInfo");
							String page = pageInfo.getString("page");
							String pageCount = pageInfo.getString("pageCount");
							String total = pageInfo.getString("total");
							intent.putExtra("page", page);
							intent.putExtra("pageCount", pageCount);
							intent.putExtra("total", total);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 请假信息查询
	public void qryLeave(final String leaveRybh, final String page, final String pageCount) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_LEAVE);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_LEAVE, httpUtils.getJsonQryLeave(leaveRybh, page, pageCount));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							intent.putExtra("leaveInfoList", data.getString("leaveInfoList"));
							JSONObject pageInfo = data.getJSONObject("pageInfo");
							String page = pageInfo.getString("page");
							String pageCount = pageInfo.getString("pageCount");
							String total = pageInfo.getString("total");
							intent.putExtra("page", page);
							intent.putExtra("pageCount", pageCount);
							intent.putExtra("total", total);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 操作请假信息
	public void commitLeave(final LeaveModel leaveModel, final String operateType) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, COMMIT_LEAVE);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(COMMIT_LEAVE, httpUtils.getJsonCommitLeave(leaveModel, operateType));
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 轮播数据获取
	public void qryCarouse() {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_CAROUSE);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_CAROUSE, httpUtils.getJsonQryCarouse());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							CarouselDatabase database = new CarouselDatabase();
							database.drop();
							List<CarouselModel> list = JSONArray.parseArray(data.getString("carouselInfo"),
									CarouselModel.class);
							if (list != null && list.size() > 0) {
								for (CarouselModel model : list) {
									database.insert(model);
								}
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询机构信息
	public void qryOrg() {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_ORG);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_ORG, httpUtils.getJsonQryOrg());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							OrgDatabase database = new OrgDatabase();
							database.drop();
							List<OrgModel> list = JSONArray.parseArray(data.getString("orgInfoList"), OrgModel.class);
							if (list != null && list.size() > 0) {
								for (OrgModel model : list) {
									database.insert(model);
								}
							}
							intent.putExtra(MyApplication.CMD_RESULT, true);
							intent.putExtra(MyApplication.CMD_MSG, error_message);
							broadcastManager.sendBroadcast(intent);
						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	// 查询城市天气
	public void qryCityWeather() {
		application.executoService.execute(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, QRY_CITY_WEATHER);
				if (application.checkNetworkHw()) {
					String result = httpUtils.post(QRY_CITY_WEATHER, httpUtils.getJsonCityWeather());
					if (!TextUtils.isEmpty(result)) {
						try {
							String code = new JSONObject(result).getString("code");
							String message = new JSONObject(result).getString("message");
							if (!code.equals("0000")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							JSONObject data = new JSONObject(result).getJSONObject("data");
							JSONObject error = data.getJSONObject("error");
							String error_id = error.getString("id");
							String error_message = error.getString("message");
							if (!error_id.equals("0000") && !error_id.equals("9001")) {
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
								return;
							}
							try {
								JSONObject weatherInfo = data.getJSONObject("weatherInfo");
								intent.putExtra("weatherInfo", weatherInfo.toString());
								intent.putExtra(MyApplication.CMD_RESULT, true);
								intent.putExtra(MyApplication.CMD_MSG, error_message);
								broadcastManager.sendBroadcast(intent);
							} catch (Exception e) {
								e.printStackTrace();
								intent.putExtra(MyApplication.CMD_RESULT, false);
								intent.putExtra(MyApplication.CMD_MSG, "获取天气信息失败");
								broadcastManager.sendBroadcast(intent);
							}

						} catch (JSONException e) {
							e.printStackTrace();
							intent.putExtra(MyApplication.CMD_RESULT, false);
							intent.putExtra(MyApplication.CMD_MSG, "解析错误");
							broadcastManager.sendBroadcast(intent);
						}
					} else {
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "服务器连接失败");
						broadcastManager.sendBroadcast(intent);
					}
				} else {
					intent.putExtra(MyApplication.CMD_RESULT, false);
					intent.putExtra(MyApplication.CMD_MSG, "网络异常");
					broadcastManager.sendBroadcast(intent);
				}
			}
		});
	}

	private Timer timer_ip = null;
	private int timer_count = 5;

	public void setIP(final String ip, final String gateway, final String mask, final String dns1, final String dns2,
					  final String sn) {
		AppLog.d(TAG, "ip:" + ip + " gateway:" + gateway + " mask:" + mask + " dns1" + dns1 + " dns2:" + dns2);
		final EthernetUtils ethernetUtils = new EthernetUtils(application);
		ethernetUtils.setEtherentDisable();
		timer_count = 5;
		timer_ip = new Timer();
		timer_ip.schedule(new TimerTask() {
			@Override
			public void run() {
				if (!isEthernetConnected(application)) {
					Log.i(TAG, "---setStaticIp--- ip:" + ip + " mask:" + mask + " gateway:" + gateway + " dns1:" + dns1
							+ " dns2:" + dns2);
					if (Build.VERSION.SDK_INT >= 21) {
						Intent intent = new Intent();
						intent.setAction("ethernet_staticip_enable");
						intent.putExtra("ethernet_setip", ip);
						intent.putExtra("ethernet_setgateway", gateway);
						intent.putExtra("ethernet_setmask", mask);
						intent.putExtra("ethernet_setdns1", dns1);
						if (!TextUtils.isEmpty(dns2)) {
							intent.putExtra("ethernet_setdns2", dns2);
						}
						application.sendBroadcast(intent);
					} else {
						ethernetUtils.setStaticIpEnable();
						ethernetUtils.setEthInfoFromStaticIP(ip);
						ethernetUtils.setEthInfoFromStaticGATEWAY(gateway);
						ethernetUtils.setEthInfoFromStaticNetmask(mask);
						ethernetUtils.setEthInfoFromStaticDNS1(dns1);
						if (!TextUtils.isEmpty(dns2)) {
							ethernetUtils.SetEthInfoFromStaticDNS2(dns2);
						}
					}
					ethernetUtils.setEtherentEnable();
					timer_ip.cancel();
					timer_count = 5;
					timer_ip = new Timer();
					timer_ip.schedule(new TimerTask() {
						@Override
						public void run() {
							if (checkNetworkHw()) {
								AppLog.d(TAG, "设置静态IP成功");
								if (timer_ip != null) {
									timer_ip.cancel();
									timer_ip = null;
								}
								timer_ip = new Timer();
								timer_ip.schedule(new TimerTask() {
									@Override
									public void run() {
										Intent intent = new Intent(MyApplication.BROADCAST);
										intent.putExtra(MyApplication.BROADCAST_TAG, SET_IP);
										intent.putExtra(MyApplication.CMD_RESULT, true);
										intent.putExtra(MyApplication.CMD_MSG, "设置静态IP成功");
										intent.putExtra(MyApplication.CMD_SN, sn);
										broadcastManager.sendBroadcast(intent);
									}
								}, 5000);
							} else {
								if (timer_count <= 0) {
									AppLog.d(TAG, "设置静态IP失败");
									if (timer_ip != null) {
										timer_ip.cancel();
										timer_ip = null;
									}
									Intent intent = new Intent(MyApplication.BROADCAST);
									intent.putExtra(MyApplication.BROADCAST_TAG, SET_IP);
									intent.putExtra(MyApplication.CMD_RESULT, false);
									intent.putExtra(MyApplication.CMD_MSG, "设置静态IP失败");
									intent.putExtra(MyApplication.CMD_SN, sn);
									broadcastManager.sendBroadcast(intent);
								}
								timer_count--;
							}
						}
					}, 1000, 1000);
				} else {
					if (timer_count <= 0) {
						AppLog.d(TAG, "设置静态IP失败");
						if (timer_ip != null) {
							timer_ip.cancel();
							timer_ip = null;
						}
						Intent intent = new Intent(MyApplication.BROADCAST);
						intent.putExtra(MyApplication.BROADCAST_TAG, SET_IP);
						intent.putExtra(MyApplication.CMD_RESULT, false);
						intent.putExtra(MyApplication.CMD_MSG, "设置静态IP失败");
						intent.putExtra(MyApplication.CMD_SN, sn);
						broadcastManager.sendBroadcast(intent);
					}
					timer_count--;
				}
			}
		}, 1000, 1000);
	}

	private boolean isEthernetConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		if (networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	private boolean checkNetworkHw() {
		boolean isOkay = false;
		ConnectivityManager connectMgr = (ConnectivityManager) application
				.getSystemService(Context.CONNECTIVITY_SERVICE);
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
}
