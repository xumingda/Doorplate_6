package com.yy.doorplate.model;

public class SectionInfoModel {

	// 节次信息
	public String id; // id
	public String sjlb; // 时间类别
	public String jcdm; // 节次代码
	public String jcmc; // 节次名称
	public String jcskkssj; // 节次授课开始时间
	public String jcskjssj; // 节次授课结束时间

	@Override
	public String toString() {
		return "SectionInfoModel [id=" + id + ", sjlb=" + sjlb + ", jcdm="
				+ jcdm + ", jcmc=" + jcmc + ", jcskkssj=" + jcskkssj
				+ ", jcskjssj=" + jcskjssj + "]";
	}
}
