package com.yy.doorplate.model;

public class CoursewareModel {

	// 课件信息
	public String id; // 课件ID
	public String skjhid; // 授课计划ID
	public String fileName; // 课件名称
	public String filePath; // 课件路径

	@Override
	public String toString() {
		return "CoursewareModel [id=" + id + ", skjhid=" + skjhid
				+ ", fileName=" + fileName + ", filePath=" + filePath + "]";
	}
}
