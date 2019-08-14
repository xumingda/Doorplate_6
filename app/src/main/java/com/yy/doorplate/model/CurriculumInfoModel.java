package com.yy.doorplate.model;

public class CurriculumInfoModel {

	// 课程信息
	public String id; // 授课计划ID
	public String jxrwid; // 教学任务ID
	public String zc; // 周次
	public String skrq; // 授课日期
	public String xqs; // 星期
	public String jc; // 节次
	public String jcshow; // 节次显示
	public String kcmc; // 课程名称
	public String sknl; // 授课内容
	public String skfs; // 授课方式
	public String skfsmc; // 授课方式名称
	public String zyts; // 作业题数
	public String zypgfs; // 作业批改形式
	public String zypgfsmc; // 作业批改形式名称
	public String fzrszs; // 人数/组数[实践]
	public String syyqsbsl; // 每组使用仪器设备及数量[实践]
	public String skcdlx; // 使用场地类型
	public String skcddm; // 授课场地代码
	public String skcdmc; // 授课场地名称
	public String skjs; // 授课教师
	public String skjsxm; // 授课教师姓名
	public String skjszp; // 授课教师照片
	public String skbj; // 授课班级
	public String skbjmc; // 班级名称
	public String syrs; // 使用人数
	public String remark; // 说明

	@Override
	public String toString() {
		return "CurriculumInfoModel [id=" + id + ", jxrwid=" + jxrwid + ", zc="
				+ zc + ", skrq=" + skrq + ", xqs=" + xqs + ", jc=" + jc
				+ ", jcshow=" + jcshow + ", kcmc=" + kcmc + ", sknl=" + sknl
				+ ", skfs=" + skfs + ", skfsmc=" + skfsmc + ", zyts=" + zyts
				+ ", zypgfs=" + zypgfs + ", zypgfsmc=" + zypgfsmc + ", fzrszs="
				+ fzrszs + ", syyqsbsl=" + syyqsbsl + ", skcdlx=" + skcdlx
				+ ", skcddm=" + skcddm + ", skcdmc=" + skcdmc + ", skjs="
				+ skjs + ", skjsxm=" + skjsxm + ", skjszp=" + skjszp
				+ ", skbj=" + skbj + ", skbjmc=" + skbjmc + ", syrs=" + syrs
				+ ", remark=" + remark + "]";
	}
}
