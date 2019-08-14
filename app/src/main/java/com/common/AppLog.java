/*
 * Copyright (C) 2015 The Telink Bluetooth Light Project
 *
 */
package com.common;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.yy.doorplate.MyApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AppLog {

    public final static String TAG = "APP";

    public static boolean ENABLE = true;
    public static boolean LOG2FILE_ENABLE = false;
    public static String FILE_PREFIX = "APPLogger";
    private static BufferedWriter mWriter;


    public static boolean isLoggable(int level) {
        if (ENABLE)
            return Log.isLoggable(TAG, level);
        return false;
    }

    public static String getStackTraceString(Throwable th) {
        if (ENABLE)
            return Log.getStackTraceString(th);
        return th.getMessage();
    }

    public static int println(int level, String msg) {
        if (ENABLE)
            return Log.println(level, TAG, msg);
        return 0;
    }

    public static int v(String msg) {
        write2File(msg);
        if (ENABLE)
            return Log.v(TAG, msg);
        return 0;
    }

    public static int v(String msg, Throwable th) {
        write2File(msg);
        write2File(getStackTraceString(th));
        if (ENABLE)
            return Log.v(TAG, msg, th);
        return 0;
    }

    public static int d(String msg) {
        write2File((formatTime(System.currentTimeMillis()) +msg));
        if (ENABLE)
            return Log.d(TAG, msg);
        return 0;
    }

    public static int d(String tag,String msg) {
        write2File((formatTime(System.currentTimeMillis()) +msg));
        if (ENABLE)
            return Log.d(tag, msg);
        return 0;
    }

    public static final String FORMAT_TIME      = "HH:mm:ss";
    /**
     * 时间格式化
     */
    public static String formatTime(Long time) {
        return formatDate(new SimpleDateFormat(FORMAT_TIME, Locale.CHINA), time);
    }
    /**
     * 时间格式化
     */
    public static String formatDate(SimpleDateFormat format, Long time) {
        if (null == time || time <= 0) { return ""; }
        return format.format(new Date(String.valueOf(time).length() == 13 ? time : time * 1000));
    }
    public static int d(String msg, Throwable th) {
        write2File(msg);
        write2File(getStackTraceString(th));
        if (ENABLE)
            return Log.d(TAG, msg, th);
        return 0;
    }

    public static int i(String msg) {
        write2File(msg);
        if (ENABLE)
            return Log.i(TAG, msg);
        return 0;
    }



    public static int i(String tag,String msg) {
        write2File(msg);
        if (ENABLE)
            return Log.i(tag, msg);
        return 0;
    }

    public static int i(String msg, Throwable th) {
        write2File(msg);
        write2File(getStackTraceString(th));
        if (ENABLE)
            return Log.i(TAG, msg, th);
        return 0;
    }






    public static int w(String msg) {
        write2File(msg);
        if (ENABLE)
            return Log.w(TAG, msg);
        return 0;
    }

    public static int w(String msg, Throwable th) {
        write2File(msg);
        write2File(getStackTraceString(th));
        if (ENABLE)
            return Log.w(TAG, msg, th);
        return 0;
    }

    public static int w(Throwable th) {
        write2File(getStackTraceString(th));
        if (ENABLE)
            return Log.w(TAG, th);
        return 0;
    }

    public static int e(String msg) {
        write2File(msg);
        if (ENABLE)
            return Log.w(TAG, msg);
        return 0;
    }

    public static int e(String msg, Throwable th) {
        write2File(msg);
        write2File(getStackTraceString(th));
        if (ENABLE)
            return Log.e(TAG, msg, th);
        return 0;
    }

    private static void write2File(String log) {
        if (LOG2FILE_ENABLE) {
            try {
                if(mWriter == null){
                    String fileName = "app-";
                    fileName += formatTime(System.currentTimeMillis());
                    fileName += ".log";
                    LOG2FILE_ENABLE = true;
                    onCreate(fileName,log);
                }else {
                    mWriter.write(log);
                    mWriter.newLine();
                    mWriter.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void onCreate(String fileName, String log) {
//        File root = Environment.getExternalStorageDirectory();
//        File dir = new File(root.getPath() + "/doorplateApp/Log");

        File dir = new File(MyApplication.PATH_ROOT + "/AppLog");
        try {
            if (!dir.exists())
                dir.mkdir();
            File file = new File(dir, fileName);
            if (!file.exists())
                file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            mWriter = new BufferedWriter(osw, 1024);
            mWriter.write(TAG + " begin : ");
            mWriter.write(log);
            mWriter.newLine();
            mWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void onCreate(String fileName) {

//        File root = Environment.getExternalStorageDirectory();
//        File dir1 = new File(root.getPath() + "/doorplateApp/Log");
//        try {
//            if (!dir1.exists()) {
//                dir1.mkdirs();
//            }
//            File file = new File(dir1, fileName);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            FileOutputStream fos = new FileOutputStream(file);
//            OutputStreamWriter osw = new OutputStreamWriter(fos);
//            mWriter = new BufferedWriter(osw, 1024);
//            mWriter.write(TAG + " begin : ");
//
//            mWriter.newLine();
//            mWriter.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        File dir = new File(MyApplication.PATH_ROOT + "/AppLog");

        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            mWriter = new BufferedWriter(osw, 1024);
            mWriter.write(TAG + " begin : ");

            mWriter.newLine();
            mWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }









//        File root = Environment.getExternalStorageDirectory();
//        File dir = new File(root.getPath() + "/doorplateApp");
//
//        try {
//            if (!dir.exists())
//                dir.mkdir();
//            File dir1 = new File(root.getPath() + "/app/log");
//            if (!dir1.exists())
//                dir1.mkdir();
//            File file = new File(dir1, fileName);
//            if (!file.exists())
//                file.createNewFile();
//            FileOutputStream fos = new FileOutputStream(file);
//            OutputStreamWriter osw = new OutputStreamWriter(fos);
//            mWriter = new BufferedWriter(osw, 1024);
//            mWriter.write(TAG + " begin : ");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static void onDestroy() {
        try {
            if (mWriter != null) {
                mWriter.write(TAG + " end : ");
                mWriter.close();
            }
        } catch (Exception e) {
        }
    }
}
