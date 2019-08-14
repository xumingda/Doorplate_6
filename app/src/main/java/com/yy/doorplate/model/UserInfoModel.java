package com.yy.doorplate.model;

public class UserInfoModel {

	// 用户信息
	public String userName; // 用户名
	public String password; // 密码，加密后数据
	public String realName; // 真实姓名
	public String cardNo; // 卡编码

	@Override
	public String toString() {
		return "UserInfoModel [userName=" + userName + ", password=" + password
				+ ", realName=" + realName + ", cardNo=" + cardNo + "]";
	}

}
