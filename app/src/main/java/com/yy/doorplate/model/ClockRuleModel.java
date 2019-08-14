package com.yy.doorplate.model;

public class ClockRuleModel {

	// 考勤规则
	public String sbkyxtq; // 上班卡允许提前分钟数
	public String xbkyxtq; // 下班卡允许提前分钟数
	public String sbkyxyh; // 上班卡允许延后分钟数
	public String xbkyxyh; // 下班卡允许延后分钟数
	public String jbddk; // 加班单打卡
	public String jbdxxdk; // 加班单休息打卡
	public String xbbdk; // 下班不打卡 0 不打卡 1打卡
	public String yxcd; // 允许迟到分钟数
	public String yzcd; // 严重迟到分钟数
	public String cgcd; // 旷工迟到分钟数
	public String dkgz; // 打卡规则，0：根据课表打卡1：根据配置时间段打卡
	public String js; // 角色，1:教师,2:学生

	@Override
	public String toString() {
		return "ClockRuleModel [sbkyxtq=" + sbkyxtq + ", xbkyxtq=" + xbkyxtq
				+ ", sbkyxyh=" + sbkyxyh + ", xbkyxyh=" + xbkyxyh + ", jbddk="
				+ jbddk + ", jbdxxdk=" + jbdxxdk + ", xbbdk=" + xbbdk
				+ ", yxcd=" + yxcd + ", yzcd=" + yzcd + ", cgcd=" + cgcd
				+ ", dkgz=" + dkgz + ", js=" + js + "]";
	}
}
