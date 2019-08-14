package com.eyun.upgrade;

public class ApkPatchJni {
	static {
		System.loadLibrary("ApkPatchJni");
	}

	/*
	 * oldApk:原始旧版本APK的路径 newApk:生成新版本APK的路径 patch:远端生成的补丁文件路径
	 */
	public static native int ApkPatch(String oldApk, String newApk, String patch);
}
