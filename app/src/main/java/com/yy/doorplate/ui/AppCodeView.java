package com.yy.doorplate.ui;

import com.yy.doorplate.MyApplication;

import android.content.Context;
import android.widget.RelativeLayout;

public abstract class AppCodeView extends RelativeLayout {

	public final static int APPCODE_UPDATA_EQU = 0; // 设备数据更新
	public final static int APPCODE_UPDATA_NOTIC = 1; // 通知数据更新
	public final static int APPCODE_UPDATA_CURRICULUM = 2; // 课程数据更新
	public final static int APPCODE_UPDATA_REGION_MEDIA = 3; // 区域广告更新
	public final static int APPCODE_UPDATA_WEATHER = 4; // 天气更新
	public final static int APPCODE_UPDATA_DIALOG_DIS = 5; // 隐藏认证加载条
	public final static int APPCODE_UPDATA_HONOR = 6; // 荣誉墙数据更新
	public final static int APPCODE_UPDATA_SCHOOL = 7; // 学校信息数据更新
	public final static int APPCODE_UPDATA_QUEST = 8; // 问卷调查数据更新
	public final static int APPCODE_UPDATA_ENGLISH = 9; // 英语角数据更新
	public final static int APPCODE_UPDATA_ZHIRI = 10; // 值日数据更新
	public final static int APPCODE_UPDATA_ATTEND = 11; // 考勤数据更新
	public final static int APPCODE_UPDATA_BOOK = 12; // 推荐图书数据更新
	public final static int APPCODE_UPDATA_JJTZ = 13; // 紧急通知数据更新

	public MyApplication application = null;
	public Context context = null;
	public String nextPath = null;

	public AppCodeView(Context context, MyApplication application,
			String nextPath) {
		super(context);
		this.application = application;
		this.context = context;
		if (nextPath.indexOf("themeswitch") > 0) {
			this.nextPath = application.getFtpPath(nextPath);
		}
	}

	public abstract void initView();

	public abstract void update(int type);

	public abstract void pause();

	public abstract void resume();

	public abstract void stop();

	public abstract void destroy();

}
