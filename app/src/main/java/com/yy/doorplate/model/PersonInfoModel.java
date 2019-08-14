package com.yy.doorplate.model;

public class PersonInfoModel {

	// 人员信息
	public String xm; // 姓名
	public String js; // 角色，1:教师,2:学生
	public String rybh; // 人员编号
	public String xsbj; // 学生班级
	public String jsbm; // 教师部门
	public String cardid; // 卡号
	public String chaoxingUser; // 超星学习通账号
	public String fid; // 超星机构id

	@Override
	public String toString() {
		return "PersonInfoModel [xm=" + xm + ", js=" + js + ", rybh=" + rybh
				+ ", xsbj=" + xsbj + ", jsbm=" + jsbm + ", cardid=" + cardid
				+ ", chaoxingUser=" + chaoxingUser + ", fid=" + fid + "]";
	}
}
