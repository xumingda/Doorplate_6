package com.yy.doorplate.model;

public class TimerTypeModel {

	// 定时开关机
	public String timerType; // 类型：DATE:日期，REPEAT：重复
	public String time; // 时间
	public String repeatWeek; // 重复，
	// Mo:星期一
	// Tu:星期二
	// We:星期三
	// Th:星期四
	// Fr:星期五
	// Sa:星期六
	// Su:星期日
	// 多个用‘,’隔开
	public String date; // 日期

	@Override
	public String toString() {
		return "TimerTypeModel [timerType=" + timerType + ", time=" + time
				+ ", repeatWeek=" + repeatWeek + ", date=" + date + "]";
	}
}
