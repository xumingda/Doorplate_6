package com.eyunsdk.core;

public class EYunDefine {
	// --指纹模块类型
	public static final int FP_TYPE_ZAZ = 1;
	public static final int FP_TYPE_GTM = 2;

	public class RfidType {
		public static final int NoRfidCard = -1;
		public static final int MifareCard = 1;
		public static final int TypeACard = 2;
		public static final int TypeBCard = 3;
		public static final int CHNIDCard = 4;
		public static final int IDCard = 5;
	}

	public class LockStatus {
		public static final int LOCKED = 0;
		public static final int UNLOCKED = 1;
		public static final int UNKNOWN = 2;
	}
}
