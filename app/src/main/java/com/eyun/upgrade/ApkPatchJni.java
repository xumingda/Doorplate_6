package com.eyun.upgrade;

public class ApkPatchJni {
	static {
		System.loadLibrary("ApkPatchJni");
	}

	/*
	 * oldApk:ԭʼ�ɰ汾APK��·�� newApk:�����°汾APK��·�� patch:Զ�����ɵĲ����ļ�·��
	 */
	public static native int ApkPatch(String oldApk, String newApk, String patch);
}
