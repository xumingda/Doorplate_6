package com.yy.doorplate.model;

public class ClassRoomInfoModel {

	// 教室信息
	public String id; // 教室实验室信息ID
	public String lx; // 教室实验室类型
	public String jssysdm; // 教师实验室代码
	public String jssysmc; // 教师实验室名称
	public String xsrl; // 学生容量
	public String bjrl; // 班级容量
	public String gxjgdm; // 管辖机构代码（多机构，分割）
	public String gxjgmc; // 管辖机构名称（多机构，分割）
	public String remark; // 说明
	public String cdglyxm; // 场地管理员姓名
	public String cdglygh; // 场地管理员工号
	public String cdglydh; // 场地管理员电话
	public String cdglyzp; // 场地管理员照片
	public String cdglykh; // 场地管理员卡号

	@Override
	public String toString() {
		return "ClassRoomInfoModel [id=" + id + ", lx=" + lx + ", jssysdm="
				+ jssysdm + ", jssysmc=" + jssysmc + ", xsrl=" + xsrl
				+ ", bjrl=" + bjrl + ", gxjgdm=" + gxjgdm + ", gxjgmc="
				+ gxjgmc + ", remark=" + remark + ", cdglyxm=" + cdglyxm
				+ ", cdglygh=" + cdglygh + ", cdglydh=" + cdglydh
				+ ", cdglyzp=" + cdglyzp + ", cdglykh=" + cdglykh + "]";
	}
}
