package com.yy.doorplate.model;

public class TeacherInfoModel {

	// 教师信息
	public String id;
	public String xm; // 姓名
	public String xb; // 性别
	public String date; // 出身年月
	public String zgxlmc; // 学历
	public String zgxwmc; // 学位
	public String byxx; // 毕业院校
	public String byzy; // 专业
	public String jxqk; // 教学情况
	public String kyqk; // 科研情况
	public String zp; // 照片
	public String jgzc; // 教工职称
	public String jgjl; // 教工教龄
	public String rybh; // 人员编号

	@Override
	public String toString() {
		return "TeacherInfoModel [id=" + id + ", xm=" + xm + ", xb=" + xb
				+ ", date=" + date + ", zgxlmc=" + zgxlmc + ", zgxwmc="
				+ zgxwmc + ", byxx=" + byxx + ", byzy=" + byzy + ", jxqk="
				+ jxqk + ", kyqk=" + kyqk + ", zp=" + zp + ", jgzc=" + jgzc
				+ ", jgjl=" + jgjl + ", rybh=" + rybh + "]";
	}
}
