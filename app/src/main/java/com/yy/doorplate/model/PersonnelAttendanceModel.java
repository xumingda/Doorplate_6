package com.yy.doorplate.model;import java.io.Serializable;public class PersonnelAttendanceModel implements Serializable {	// 人员考勤信息	public String id;	public String date; // 日期	public String toWorkTime; // 上班时间	public String offWorkTime; // 下班时间	public String rybh; // 人员编号	public String xm; // 姓名	public String js; // 角色，1:教师,2:学生	public String isToWorkClock; // 上班是否打卡 0 不打卡 1打卡	public String isOffWorkClock; // 下班是否打卡 0 不打卡 1打卡	public String status; // 考勤状态 0出勤 1事假 2病假 3迟到 4旷课 5早退 6公假 7实习 8集训 9其它 10缺席							// 11未签到 12签退	public String cardid; // 人员卡号	public String toWorkClockTime; // 上班打卡时间	public String offWorkClockTime; // 下班打卡时间	@Override	public String toString() {		return "PersonnelAttendanceModel [id=" + id + ", date=" + date				+ ", toWorkTime=" + toWorkTime + ", offWorkTime=" + offWorkTime				+ ", rybh=" + rybh + ", xm=" + xm + ", js=" + js				+ ", isToWorkClock=" + isToWorkClock + ", isOffWorkClock="				+ isOffWorkClock + ", status=" + status + ", cardid=" + cardid				+ ", toWorkClockTime=" + toWorkClockTime				+ ", offWorkClockTime=" + offWorkClockTime + "]";	}}