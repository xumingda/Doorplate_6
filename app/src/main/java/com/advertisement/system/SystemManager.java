package com.advertisement.system;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.advertisement.network.NetworkManager;
import com.advertisement.resource.ResFileParse;
import com.advertisement.resource.ResFileParse.FileParamer;
import com.advertisement.resource.ResFileParse.ResPlayPlan;
import com.advertisement.resource.ResManager;
import com.advertisement.resource.ResPlaybill;
import com.advertisement.resource.ResPlaybill.PlayList;
import com.advertisement.resource.ResTheme;
import com.advertisement.resource.ResTheme.Form.Appcode;
import com.advertisement.resource.ResThemeSwitch.ThemePolicy;
import com.advertisement.resource.ResUpdate;
import com.advertisement.resource.ResUpdate.UpdateListener;
import com.advertisement.system.ServiceMsg.ServiceMsgListener;
import com.common.AppLog;
import com.common.Unit;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.database.ThemeSwitchDatabase;
import com.yy.doorplate.model.ThemeSwitch;

public class SystemManager implements UpdateListener {

	public static SystemManager Instacne = new SystemManager();

	private static final boolean isLogInfoOn = true;
	private static final boolean isLogErrOn = true;
	private static final boolean isLogWarnOn = true;
	private static final boolean isLogDbgOn = true;

	private Context mContext = null;

	private SystemConfig mSystemConfig = null;
	private NetworkManager mNetworkManager = null;
	private ResManager mResManager = null;

	private SystemHandler mSystemHandler = null;
	private ServiceMsg mServiceMsg = null;
	private ResUpdate mResUpdate = null;

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;

	private static final String TAG = "AdvSystem";

	public static void LOGI(String tag, String msg) {
		if (isLogInfoOn) {
//			Log.i(tag, msg);
			AppLog.i(msg);
		}
	}

	public static void LOGE(String tag, String msg) {
		if (isLogErrOn) {
//			Log.e(tag, msg);
			AppLog.e(msg);
		}
	}

	public static void LOGW(String tag, String msg) {
		if (isLogWarnOn) {
//			Log.w(tag, msg);
			AppLog.w(msg);
		}
	}

	public static void LOGD(String tag, String msg) {
		if (isLogDbgOn) {
			Log.d(tag, msg);
//			AppLog.d(tag, msg);
		}
	}

	public static void LogHex(String tag, byte[] data, int datalen) {
		String hexStr = "";
		for (int i = 0; i < datalen; i++) {

			if ((i % 16) == 0) {
				Log.d(tag, hexStr);
				hexStr = "";
			}
			if (Integer.toHexString(data[i] & 0xff).length() == 1) {
				hexStr += "0";
			}
			hexStr += Integer.toHexString(data[i] & 0xff).toUpperCase() + " ";
		}
		Log.d(tag, hexStr);
	}

	public static String outHexStr(byte data) {
		String hexStr = "";
		if (Integer.toHexString(data & 0xff).length() == 1) {
			hexStr += "0";
		}
		hexStr += Integer.toHexString(data & 0xff).toUpperCase();
		return hexStr;
	}

	public static SystemManager getSystemManager() {
		return Instacne;
	}

	public void setContext(Context context) {
		this.mContext = context;
		broadcastManager = LocalBroadcastManager.getInstance(mContext);
	}

	public Context getContext() {
		return mContext;
	}

	public MyApplication getApplication() {
		return application;
	}

	public void setApplication(MyApplication application) {
		this.application = application;
	}

	public SystemConfig getConfig() {
		return mSystemConfig;
	}

	public void initConfig() {
		mSystemConfig = new SystemConfig(mContext);
		mSystemHandler = new SystemHandler();

		if (mNetworkManager == null) {
			mNetworkManager = new NetworkManager(this);
		}

		if (mResManager == null) {
			mResManager = new ResManager(this);
		}

		if (mResUpdate == null) {
			mResUpdate = new ResUpdate(mContext, this);
		}

	}

	public void start() {

		initConfig();

		if (mResManager != null) {
			mResManager.start();
		}

		if (mNetworkManager != null) {
			mNetworkManager.start();
		}
	}

	// 主题下载
	public void downloadPolicy(String url) {
		listThemeSwitch.clear();
		listThemeSwitch_i = 0;
		layer = 0;

		isErrorThreadRunning = false;
		if (reportDownErrorThread != null) {
			reportDownErrorThread.interrupt();
			reportDownErrorThread = null;
		}

		mResManager.setSchedule("");

		Message msg = new Message();
		msg.what = ConstData.ResMsgType.MSG_ADD_DOWNLOAD;
		msg.obj = url;
		msg.arg1 = ConstData.FileTrans.FILE_TYPE_THEME_POLICY;
		sendMsgToManager(mResManager, msg);
	}

	public void sendVmcBroadcast() {
		String tmId = null;
		if (mSystemConfig.installState) {

			tmId = mSystemConfig.tmId;
		}
		Intent intent = new Intent();
		intent.setAction(ConstData.VMC_ACTION);
		intent.putExtra("tmId", tmId);
		mContext.sendBroadcast(intent);
	}

	public void sendAdvBroadcast() {
		Message msg = new Message();
		if (mSystemConfig.isNormalExist) {
			msg.what = ConstData.SystemMsgType.MSG_UPDATE_THEME_POLICY;
			msg.arg1 = ConstData.FileTrans.FILE_TYPE_THEME_POLICY;
			mSystemHandler.sendMessage(msg);
		} else if (mSystemConfig.isDefaultExist) {
			msg.what = ConstData.SystemMsgType.MSG_UPDATE_THEME_POLICY;
			msg.arg1 = ConstData.FileTrans.FILE_TYPE_PAD_POLICY;
			mSystemHandler.sendMessage(msg);
		}
	}

	public void stop() {

		if (mResManager != null) {
			mResManager.stop();
		}
	}

	public BaseManager getResManager() {
		return mResManager;
	}

	/**
	 * @see 发送消息到system核心调度
	 */
	public void sendMsgToSystem(Message msg) {
		if (mSystemHandler != null) {
			mSystemHandler.sendMessage(msg);
		}
	}

	/**
	 * @see 发送消息到各子manager中
	 */
	public void sendMsgToManager(BaseManager manager, Message msg) {
		if (manager.getHandler() != null) {
			manager.getHandler().sendMessage(msg);
		}
	}

	public class SystemHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			Intent mIntent = null;

			switch (msg.what) {
			// --更新当前的主题播放策略
			case ConstData.SystemMsgType.MSG_UPDATE_THEME_POLICY:

				mIntent = new Intent();
				mIntent.setAction(ConstData.ACTIVITY_UPDATE);
				mIntent.putExtra("ThemePolicyType", msg.arg1);

				switch (msg.arg1) {
				case ConstData.FileTrans.FILE_TYPE_INSERT_POLICY:
					ResPlayPlan plan = ResFileParse
							.getPlayPlanByPolicy(mSystemConfig.readParam(
									"insertThemePolicy",
									mSystemConfig.insertThemePolicy));
					// --过滤第三方信息发布
					for (ThemePolicy policy : plan.mThemeSwitch.mExtThemePolicyList) {
						LOGI(TAG, "===ext policy:" + policy.toString());
						if (policy.name.indexOf(ConstData.EXT_INFO_FILTER) > 0) {
							LOGD(TAG, "find the filter!");
							// --移动第三方信息发布到指定文件夹下，减少检索时间
							Unit.moveFileToDir(mSystemConfig.readParam(
									"insertThemePolicy",
									mSystemConfig.insertThemePolicy),
									SystemConfig.getStoreRootPath()
											+ "/upload/ext/201611");
							return;
						}
					}
					mIntent.putExtra("ThemePolicyPath", mSystemConfig
							.readParam("insertThemePolicy",
									mSystemConfig.insertThemePolicy));
					mIntent.putExtra(MyApplication.CMD_RESULT, true);
					broadcastManager.sendBroadcast(mIntent);
					break;
				case ConstData.FileTrans.FILE_TYPE_PAD_POLICY:
					// --当存在正常播放策略时，垫片将不播放，只存储
					if (mSystemConfig.isNormalExist == false) {

						mIntent.putExtra("ThemePolicyPath", mSystemConfig
								.readParam("defaultThemePolicy",
										mSystemConfig.defaultThemePolicy));
						mIntent.putExtra(MyApplication.CMD_RESULT, true);
						broadcastManager.sendBroadcast(mIntent);
					}
					break;
				case ConstData.FileTrans.FILE_TYPE_THEME_POLICY:
					// mIntent.putExtra("ThemePolicyPath", mSystemConfig
					// .readParam("normalThemePolicy",
					// mSystemConfig.normalThemePolicy));
					// mIntent.putExtra(MyApplication.CMD_RESULT, true);
					// broadcastManager.sendBroadcast(mIntent);
					doNextThemeSwitch(msg.arg1, mSystemConfig.readParam(
							"normalThemePolicy",
							mSystemConfig.normalThemePolicy));
					break;
				}

				break;
			case ConstData.SystemMsgType.MSG_UPDATE_ERROR: {
				reportDownError(msg.arg1, msg.arg2);
				break;
			}
			case ConstData.SystemMsgType.MSG_REPORT_INSTALL_STATUS:
				uiGetInstallStatus();// 向界面上报安装状态
				break;
			// --平台下发程序文件
			case ConstData.SystemMsgType.MSG_UPDATE_APP:
				SystemManager.LOGD(TAG, "======================");
				SystemManager.LOGD(TAG, "install app:" + msg.obj);
				SystemManager.LOGD(TAG, "======================");
				installApp((String) msg.obj);
				break;
			case ConstData.SystemMsgType.MSG_REBOOT:// --系统重启
				SystemManager.LOGD(TAG, "======================");
				SystemManager.LOGD(TAG, "|       REBOOT       |");
				SystemManager.LOGD(TAG, "======================");
				reboot();
				break;
			case ConstData.SystemMsgType.MSG_SHUTDOWN:// --系统关屏幕
				SystemManager.LOGD(TAG, "======================");
				SystemManager.LOGD(TAG, "|       SHUTDOWN     |");
				SystemManager.LOGD(TAG, "======================");
				break;
			case ConstData.SystemMsgType.MSG_POWERON:// --系统开屏幕
				SystemManager.LOGD(TAG, "======================");
				SystemManager.LOGD(TAG, "|       POWERON      |");
				SystemManager.LOGD(TAG, "======================");
				break;
			}
		}
	}

	private Thread reportDownErrorThread = null;
	private boolean isErrorThreadRunning = false;
	private String storeStr;
	private SystemConfig config;

	// 下载失败（一条themeswitch只通报一次错误）
	private void reportDownError(final int ThemePolicyType, final int type) {
		config = SystemManager.getSystemManager().getConfig();
		storeStr = config.readParam("FtpDownloadTotal", "");
		if (reportDownErrorThread == null) {
			isErrorThreadRunning = true;
			reportDownErrorThread = new Thread(new Runnable() {
				public void run() {
					while (isErrorThreadRunning) {
						storeStr = config.readParam("FtpDownloadTotal", "");
						if (storeStr.length() == 0) {
							break;
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (isErrorThreadRunning) {
							Intent mIntent = new Intent();
							mIntent.setAction(ConstData.ACTIVITY_UPDATE);
							mIntent.putExtra("ThemePolicyType", ThemePolicyType);
							mIntent.putExtra(MyApplication.CMD_RESULT, false);
							mIntent.putExtra(MyApplication.CMD_MSG, type);
							broadcastManager.sendBroadcast(mIntent);
						}
					}
				}
			});
			reportDownErrorThread.start();
		}
	}

	private ThemeSwitchDatabase themeSwitchDatabase = new ThemeSwitchDatabase();
	private List<ThemeSwitch> listThemeSwitch = new ArrayList<ThemeSwitch>();
	private int layer = 0, listThemeSwitch_i = 0;

	private void doNextThemeSwitch(int ThemePolicyType, String ThemePolicyPath) {
		if (layer == 0) {
			layer++;
			findThemeSwitch(mSystemConfig.readParam("normalThemePolicy",
					mSystemConfig.normalThemePolicy), listThemeSwitch);
			if (listThemeSwitch.size() > 0) {
				Message msg = new Message();
				msg.what = ConstData.ResMsgType.MSG_ADD_DOWNLOAD;
				msg.obj = listThemeSwitch.get(listThemeSwitch_i).themeSwitchUrl;
				msg.arg1 = ConstData.FileTrans.FILE_TYPE_THEME_POLICY;
				sendMsgToManager(mResManager, msg);
			} else {
				Intent mIntent = new Intent();
				mIntent.setAction(ConstData.ACTIVITY_UPDATE);
				mIntent.putExtra("ThemePolicyType", ThemePolicyType);
				mIntent.putExtra("ThemePolicyPath", ThemePolicyPath);
				mIntent.putExtra(MyApplication.CMD_RESULT, true);
				broadcastManager.sendBroadcast(mIntent);
			}
		} else {
			listThemeSwitch_i++;
			if (listThemeSwitch.size() > listThemeSwitch_i) {
				Message msg = new Message();
				msg.what = ConstData.ResMsgType.MSG_ADD_DOWNLOAD;
				msg.obj = listThemeSwitch.get(listThemeSwitch_i).themeSwitchUrl;
				msg.arg1 = ConstData.FileTrans.FILE_TYPE_THEME_POLICY;
				sendMsgToManager(mResManager, msg);
			} else {
				layer++;
				List<ThemeSwitch> list = new ArrayList<ThemeSwitch>();
				for (ThemeSwitch themeSwitch : listThemeSwitch) {
					findThemeSwitch(getFtpPath(themeSwitch.themeSwitchUrl),
							list);
				}
				listThemeSwitch_i = 0;
				listThemeSwitch = list;
				if (listThemeSwitch.size() > 0) {
					Message msg = new Message();
					msg.what = ConstData.ResMsgType.MSG_ADD_DOWNLOAD;
					msg.obj = listThemeSwitch.get(listThemeSwitch_i).themeSwitchUrl;
					msg.arg1 = ConstData.FileTrans.FILE_TYPE_THEME_POLICY;
					sendMsgToManager(mResManager, msg);
				} else {
					Intent mIntent = new Intent();
					mIntent.setAction(ConstData.ACTIVITY_UPDATE);
					mIntent.putExtra("ThemePolicyType", ThemePolicyType);
					mIntent.putExtra("ThemePolicyPath", ThemePolicyPath);
					mIntent.putExtra(MyApplication.CMD_RESULT, true);
					broadcastManager.sendBroadcast(mIntent);
				}
			}
		}
		Log.e(TAG, "layer : " + layer + "\nlistThemeSwitch.size() : "
				+ listThemeSwitch.size() + "\nlistThemeSwitch_i : "
				+ listThemeSwitch_i);
	}

	private void findThemeSwitch(String path, List<ThemeSwitch> list) {
		ResPlayPlan mResPlayPlan = ResFileParse.getPlayPlanByPolicy(path);
		if (mResPlayPlan == null || !mResPlayPlan.isFilesExist) {
			return;
		}
		ThemePolicy mThemePolicy = mResPlayPlan.mThemeSwitch.mThemePolicyList
				.get(0);
		FileParamer filePar = new FileParamer(mThemePolicy.url);
		ResTheme mTheme = ResFileParse.parseThemeXmlFile(filePar.fileLocalPath);
		if (mTheme == null) {
			return;
		}
		for (Appcode appcode : mTheme.mForm.mAppcodeList) {
			filePar = new FileParamer(appcode.playbill);
			ResPlaybill mResPlaybill = ResFileParse
					.parsePlaybillXmlFile(filePar.fileLocalPath);
			if (mResPlaybill.mPlayList.size() > 0) {
				PlayList playList = mResPlaybill.mPlayList.get(0);
				Log.i(TAG, "findThemeSwitch : " + playList.mAppcodeAd.url);
				if (playList.mAppcodeAd.url.indexOf("themeswitch") > 0) {
					ThemeSwitch themeSwitch = new ThemeSwitch();
					themeSwitch.layer = layer;
					themeSwitch.themeSwitchUrl = playList.mAppcodeAd.url;
					themeSwitchDatabase.insert(themeSwitch);
					list.add(themeSwitch);
				}
			}
		}
	}

	private String getFtpPath(String ftpUrl) {
		String tmp = ftpUrl.substring(6);
		int index = tmp.indexOf("/");
		return "/mnt/sdcard/Adv" + tmp.substring(index);
	}

	public void setServiceMsg(ServiceMsg msger) {
		mServiceMsg = msger;
	}

	public ServiceMsgListener mSrvMsgListener = new ServiceMsgListener() {

		@Override
		public void OnHandleMessage(Message msg) {
			switch (msg.what) {
			case ConstData.ActivityMsgType.MSG_U2S_INSTALL:// --请求手动安装

				break;
			case ConstData.ActivityMsgType.MSG_U2S_INSTALL_ST:// --应答安装状态
				uiGetInstallStatus();
				break;
			case ConstData.ActivityMsgType.MSG_U2S_GET_SERVER:
				uiGetServerInfo();
				break;
			case ConstData.ActivityMsgType.MSG_U2S_SET_SERVER:
				ConstData.ServerInfo server = (ConstData.ServerInfo) msg.obj;
				uiSetServerInfo(server);
				break;
			case ConstData.ActivityMsgType.MSG_U2S_GET_CONFIG:
				uiGetConfig();
				break;
			case ConstData.ActivityMsgType.MSG_U2S_UDISK_UPDATE:
				uiUdiskUpdate((String) msg.obj);
				break;
			case ConstData.ActivityMsgType.MSG_U2S_REBOOT:
				mSystemHandler
						.sendEmptyMessage(ConstData.SystemMsgType.MSG_REBOOT);
				break;
			}
		}
	};

	public ServiceMsgListener getSrvMsgListener() {
		return mSrvMsgListener;
	}

	public void uiUdiskUpdate(String rootPath) {

		mResUpdate.startUdisk(rootPath);

	}

	public void uiGetConfig() {
		Message msg = new Message();
		msg.what = ConstData.ActivityMsgType.MSG_S2U_GET_CONFIG;
		msg.obj = mSystemConfig;
		mServiceMsg.sendMessage(msg);
	}

	public void uiGetInstallStatus() {
		ConstData.DevInstallStatus status = new ConstData.DevInstallStatus();
		status.isInstalled = mSystemConfig.installState;
		if (status.isInstalled) {
			status.installKey = mSystemConfig.installKey;
			status.installNo = mSystemConfig.installJobNum;
			// --安装成功将发送广播到VMC
			sendVmcBroadcast();
		}
		if (mServiceMsg != null) {
			Message msg1 = new Message();
			msg1.what = ConstData.ActivityMsgType.MSG_S2U_INSTALL_ST;
			msg1.obj = status;
			mServiceMsg.sendMessage(msg1);
		}
	}

	public void uiGetServerInfo() {
		ConstData.ServerInfo info = new ConstData.ServerInfo();
		info.ip1 = mSystemConfig.readParam("0x1020", mSystemConfig.serverIp);
		info.port1 = mSystemConfig.readParam("0x1000",
				mSystemConfig.serverPortNum);
		info.ip2 = mSystemConfig.readParam("0xF001", mSystemConfig.serverIp2);
		info.port2 = mSystemConfig.readParam("0xF002",
				mSystemConfig.serverPortNum2);
		Message msg1 = new Message();
		msg1.what = ConstData.ActivityMsgType.MSG_S2U_GET_SERVER;
		msg1.obj = info;
		mServiceMsg.sendMessage(msg1);
	}

	public void uiSetServerInfo(ConstData.ServerInfo server) {

	}

	public void uiReportUpdateInfo(ConstData.UpdateInfo info) {
		Message msg1 = new Message();
		msg1.what = ConstData.ActivityMsgType.MSG_S2U_UDISK_REPORT;
		msg1.obj = info;
		mServiceMsg.sendMessage(msg1);
	}

	public void installApp(String path) {
		if (!new File(path).exists()) {

			return;
		}
		PackageManager pm = mContext.getPackageManager();
		PackageInfo pi = null;
		// --获取需要升级的apk信息
		pi = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
		// --若为本应用，先判断版本
		if (pi.packageName.equals(mContext.getPackageName())) {
			try {
				SystemManager.LOGI(
						TAG,
						"curVersionCode:"
								+ pm.getPackageInfo(mContext.getPackageName(),
										0).versionCode);
				SystemManager.LOGI(TAG, "newVersionCode:" + pi.versionCode);
				if (pi.versionCode <= pm.getPackageInfo(
						mContext.getPackageName(), 0).versionCode) {
					return;
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				return;
			}

		}

		Intent intent = new Intent("slient_install_dmb");
		intent.putExtra("dmb", path);
		mContext.sendBroadcast(intent);
	}

	public void reboot() {
		Intent intent = new Intent();
		intent.setAction("system_to_reboot");
		mContext.sendBroadcast(intent);
	}

	@Override
	public void onStart(boolean success) {

	}

	@Override
	public void onUpdate(String src, String dst, boolean success) {

	}

	@Override
	public void onStop() {

	}

	public static long getSdcardUsableSpace() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return (blockSize * availableBlocks);
	}
}
