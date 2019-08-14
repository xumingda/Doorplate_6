package com.yy.doorplate.model;

public class OnDutyInfoModel {

	// 值日信息
	public String jssysdm; // 教室实验室代码
	public String bjdm; // 班级代码
	public String date; // 日期
	public String xm; // 学生姓名
	public String xssh; // 学生学号

	@Override
	public String toString() {
		return "OnDutyInfoModel [jssysdm=" + jssysdm + ", bjdm=" + bjdm
				+ ", date=" + date + ", xm=" + xm + ", xssh=" + xssh + "]";
	}
}
