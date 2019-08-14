package com.yy.doorplate.model;

public class SchoolInfoModel {

	// 校园文化学校简介信息
	public String infoName; // 信息名称
	public String infoCode; // 信息编码
	public String content; // 内容

	@Override
	public String toString() {
		return "SchoolInfoModel [infoName=" + infoName + ", infoCode="
				+ infoCode + ", content=" + content + "]";
	}
}
