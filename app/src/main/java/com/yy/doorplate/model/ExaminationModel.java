package com.yy.doorplate.model;

public class ExaminationModel {

	// 考试信息
	public String ksmc; // 考试名称
	public String kcmc; // 考场名称
	public String ksrq; // 考试日期
	public String ksjs; // 考试教室
	public String kskssj; // 考试开始时间
	public String ksjssj; // 考试结束时间
	public String kskm; // 考试科目
	public String jcls; // 监考老师

	@Override
	public String toString() {
		return "ExaminationModel [ksmc=" + ksmc + ", kcmc=" + kcmc + ", ksrq="
				+ ksrq + ", ksjs=" + ksjs + ", kskssj=" + kskssj + ", ksjssj="
				+ ksjssj + ", kskm=" + kskm + ", jcls=" + jcls + "]";
	}

}
