package com.yy.doorplate.model;

public class AttendInfoModel {

	// 学生考勤
	public String id; // 学生考勤ID
	public String xn; // 学年
	public String xq; // 学期
	public String jxrwid; // 教学任务ID
	public String kcmc; // 课程名称
	public String zc; // 周次
	public String skrq; // 授课日期
	public String jcshow; // 节次
	public String skjhid; // 授课计划ID
	public String ks; // 课时
	public String ssxy; // 所属学院
	public String ssxb; // 所属系部
	public String ssxbmc; // 所属系部名称
	public String ssbj; // 班级代码
	public String ssbjmc; // 班级名称
	public String xsxh; // 学号
	public String zwh; // 座位号
	public String xsxm; // 姓名
	public String kqzt; // 考勤状态 0出勤 1事假 2病假 3迟到 4旷课 5早退 6公假 7实习 8集训 9其它 10缺席 11未签到
	public String kqtjry; // 考勤数据提交人
	public String kqtjryxm; // 考勤数据提交人姓名
	public String kqsj; // 考勤时间
	public String glqj; // 关联请假
	public String remark; // 说明
	public String zp; // 个人照片地址
	public String xb; // 性别
	public String xskh; // 学生卡卡号
	public String qdsx; // 签到顺序

	@Override
	public String toString() {
		return "AttendInfoModel [id=" + id + ", xn=" + xn + ", xq=" + xq
				+ ", jxrwid=" + jxrwid + ", kcmc=" + kcmc + ", zc=" + zc
				+ ", skrq=" + skrq + ", jcshow=" + jcshow + ", skjhid="
				+ skjhid + ", ks=" + ks + ", ssxy=" + ssxy + ", ssxb=" + ssxb
				+ ", ssxbmc=" + ssxbmc + ", ssbj=" + ssbj + ", ssbjmc="
				+ ssbjmc + ", xsxh=" + xsxh + ", zwh=" + zwh + ", xsxm=" + xsxm
				+ ", kqzt=" + kqzt + ", kqtjry=" + kqtjry + ", kqtjryxm="
				+ kqtjryxm + ", kqsj=" + kqsj + ", glqj=" + glqj + ", remark="
				+ remark + ", zp=" + zp + ", xb=" + xb + ", xskh=" + xskh
				+ ", qdsx=" + qdsx + "]";
	}

}
