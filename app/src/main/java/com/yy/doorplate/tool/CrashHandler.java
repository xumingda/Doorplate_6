package com.yy.doorplate.tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.yy.doorplate.MyApplication;

public class CrashHandler implements UncaughtExceptionHandler {

	private static final String TAG = "CrashHandler";

	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static CrashHandler INSTANCE = new CrashHandler();
	// 程序的Context对象
	private MyApplication mContext;

	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	// 用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(MyApplication context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		ex.printStackTrace();
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (mContext.mEYunSdk != null) {
				mContext.mEYunSdk.Finish();
			}
			if (mContext.cameraManager != null) {
				mContext.cameraManager.close();
			}
			if (mContext.tcpProcess != null) {
				mContext.tcpProcess.stopTcp();
			}
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}

	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		handler.sendEmptyMessage(0);

		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 保存日志文件
		saveCrashInfo2File(ex);
		return true;
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			mContext.showToast("很抱歉,程序出现异常,即将重启");
		};
	};

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "Doorplate-" + time + "-" + timestamp + ".log";
//			File root = Environment.getExternalStorageDirectory();
//			File dir = new File(root.getPath() + "/Doorplate/Log/");
			File dir = new File(MyApplication.PATH_ROOT + "/CrashLog");
				if (!dir.exists()) {
					dir.mkdir();
				}
				File file = new File(dir, fileName);
				if (!file.exists()) {
					Toast.makeText(this.mContext, fileName, Toast.LENGTH_SHORT).show();
					file.createNewFile();
				}

				FileOutputStream fos = new FileOutputStream(file);
				fos.write(sb.toString().getBytes());
				fos.close();
			} catch (IOException e) {
				Log.e(TAG, "an error occured while writing file...", e);
			}
			return null;
		}
	}

//	private String saveCrashInfo2File(Throwable ex) {
//
//		StringBuffer sb = new StringBuffer();
//		for (Map.Entry<String, String> entry : infos.entrySet()) {
//			String key = entry.getKey();
//			String value = entry.getValue();
//			sb.append(key + "=" + value + "\n");
//		}
//
//		Writer writer = new StringWriter();
//		PrintWriter printWriter = new PrintWriter(writer);
//		ex.printStackTrace(printWriter);
//		Throwable cause = ex.getCause();
//		while (cause != null) {
//			cause.printStackTrace(printWriter);
//			cause = cause.getCause();
//		}
//		printWriter.close();
//		String result = writer.toString();
//		sb.append(result);
//		try {
//			long timestamp = System.currentTimeMillis();
//			String time = formatter.format(new Date());
//			String fileName = "Doorplate-" + time + "-" + timestamp + ".log";
//			if (Environment.getExternalStorageState().equals(
//					Environment.MEDIA_MOUNTED)) {
//				String path = "/mnt/internal_sd/Doorplate/Log/";
//				File dir = new File(path);
//				if (!dir.exists()) {
//					dir.mkdirs();
//				}
//				FileOutputStream fos = new FileOutputStream(path + fileName);
//				fos.write(sb.toString().getBytes());
//				fos.close();
//			}
//			return fileName;
//		} catch (Exception e) {
//			Log.e(TAG, "an error occured while writing file...", e);
//		}
//		return null;
//	}

//}
