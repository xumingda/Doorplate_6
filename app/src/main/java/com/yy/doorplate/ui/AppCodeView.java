package com.yy.doorplate.ui;

import com.yy.doorplate.MyApplication;

import android.content.Context;
import android.widget.RelativeLayout;

public abstract class AppCodeView extends RelativeLayout {

	public final static int APPCODE_UPDATA_EQU = 0; // �豸���ݸ���
	public final static int APPCODE_UPDATA_NOTIC = 1; // ֪ͨ���ݸ���
	public final static int APPCODE_UPDATA_CURRICULUM = 2; // �γ����ݸ���
	public final static int APPCODE_UPDATA_REGION_MEDIA = 3; // ���������
	public final static int APPCODE_UPDATA_WEATHER = 4; // ��������
	public final static int APPCODE_UPDATA_DIALOG_DIS = 5; // ������֤������
	public final static int APPCODE_UPDATA_HONOR = 6; // ����ǽ���ݸ���
	public final static int APPCODE_UPDATA_SCHOOL = 7; // ѧУ��Ϣ���ݸ���
	public final static int APPCODE_UPDATA_QUEST = 8; // �ʾ�������ݸ���
	public final static int APPCODE_UPDATA_ENGLISH = 9; // Ӣ������ݸ���
	public final static int APPCODE_UPDATA_ZHIRI = 10; // ֵ�����ݸ���
	public final static int APPCODE_UPDATA_ATTEND = 11; // �������ݸ���
	public final static int APPCODE_UPDATA_BOOK = 12; // �Ƽ�ͼ�����ݸ���
	public final static int APPCODE_UPDATA_JJTZ = 13; // ����֪ͨ���ݸ���

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
