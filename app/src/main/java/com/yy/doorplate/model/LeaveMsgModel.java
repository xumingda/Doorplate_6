package com.yy.doorplate.model;

public class LeaveMsgModel {

	public String kind; // 留言类型 stu:学生,patriarch：家长, tearch:老师
	public String bjdm; // 留言人所在班级
	public String formRyType; // 留言人员类型 stu:学生,patriarch：家长, tearch:老师
	public String formRybh; // 留言人员编号
	public String formRyXm; // 留言人员姓名
	public String toRyType; // 留言对象人员类型 stu:学生,patriarch：家长, tearch:老师
	public String toRybh; // 留言目标人员编号
	public String toRyXm; // 留言目标人员姓名
	public String msg; // 留言内容
	public String msgDate; // 留言日期

	@Override
	public String toString() {
		return "LeaveMsgModel [kind=" + kind + ", bjdm=" + bjdm
				+ ", formRyType=" + formRyType + ", formRybh=" + formRybh
				+ ", formRyXm=" + formRyXm + ", toRyType=" + toRyType
				+ ", toRybh=" + toRybh + ", toRyXm=" + toRyXm + ", msg=" + msg
				+ ", msgDate=" + msgDate + "]";
	}
}
