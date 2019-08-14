package com.yy.doorplate.model;

public class ClockInfoModel {

	// 打卡记录
	public String id; // 打卡记录ID
	public String xm; // 姓名
	public String js; // 角色，1:教师,2:学生
	public String rybh; // 人员编号
	public String dkrq; // 打卡日期
	public String dksj; // 打卡时间
	public String dklx; // 打卡类型，1：刷卡
	public String isupload; // 该条记录是否上传平台，0：未上传，1：已上传

	@Override
	public String toString() {
		return "ClockInfoModel [id=" + id + ", xm=" + xm + ", js=" + js
				+ ", rybh=" + rybh + ", dkrq=" + dkrq + ", dksj=" + dksj
				+ ", dklx=" + dklx + ", isupload=" + isupload + "]";
	}

}
