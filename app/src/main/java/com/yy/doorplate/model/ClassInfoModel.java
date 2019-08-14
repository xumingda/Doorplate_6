package com.yy.doorplate.model;

public class ClassInfoModel {

	// 班级信息
	public String id; // 班级id
	public String ssxy; // 所属学院
	public String ssxq; // 所属校区
	public String ssxqmc; // 所属校区名称
	public String ssxb; // 所属系部
	public String ssxbmc; // 所属系部名称
	public String sszy; // 所属专业
	public String sszymc; // 所属专业名称
	public String bjdm; // 班级代码
	public String bjmc; // 班级名称
	public String nj; // 年级
	public String xz; // 学制
	public String xzshow; // 学制显示内容
	public String bjzxrs; // 班级在校人数
	public String bjsjzxrs; // 班级实际在校人数
	public String fdy; // 辅导员
	public String fdyxm; // 辅导员姓名
	public String fdydh; // 辅导员电话
	public String fdyzp; // 辅导员照片

	@Override
	public String toString() {
		return "ClassInfoModel [id=" + id + ", ssxy=" + ssxy + ", ssxq=" + ssxq
				+ ", ssxqmc=" + ssxqmc + ", ssxb=" + ssxb + ", ssxbmc="
				+ ssxbmc + ", sszy=" + sszy + ", sszymc=" + sszymc + ", bjdm="
				+ bjdm + ", bjmc=" + bjmc + ", nj=" + nj + ", xz=" + xz
				+ ", xzshow=" + xzshow + ", bjzxrs=" + bjzxrs + ", bjsjzxrs="
				+ bjsjzxrs + ", fdy=" + fdy + ", fdyxm=" + fdyxm + ", fdydh="
				+ fdydh + ", fdyzp=" + fdyzp + "]";
	}
}
