package com.yy.doorplate.model;

public class ClassFeedbackModel {

	// 授课反馈信息
	public String id; // 反馈ID
	public String skjhid; // 授课计划ID
	public String feedback; // 反馈内容
	public String level; // 评分星级
	public String rybh; // 反馈人员编号

	@Override
	public String toString() {
		return "ClassFeedbackModel [id=" + id + ", skjhid=" + skjhid
				+ ", feedback=" + feedback + ", level=" + level + ", rybh="
				+ rybh + "]";
	}
}
