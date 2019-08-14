package com.yy.doorplate.model;

public class PrizeInfoModel {

	// 荣誉墙信息
	public String prizeId; // 奖状ID
	public String prizeCode; // 奖状编码
	public String prizeName; // 奖状名称
	public String iconUrl; // 图片路径
	public String prizeDate; // 获奖日期
	public String prizeType; // 奖状类型，学校：SCHOOL，班级:CLASS
	public String relaCode; // 关联编码，类型为学校时关联学校编码，类型为班级时为班级编码
	public String prizePerson; // 获奖人员
	public String prizeUnit; // 获奖单位
	public String ranking; // 排名
	public String awardsUnit; // 颁奖单位
	public String xsxh; // 学生学号

	@Override
	public String toString() {
		return "PrizeInfoModel [prizeId=" + prizeId + ", prizeCode="
				+ prizeCode + ", prizeName=" + prizeName + ", iconUrl="
				+ iconUrl + ", prizeDate=" + prizeDate + ", prizeType="
				+ prizeType + ", relaCode=" + relaCode + ", prizePerson="
				+ prizePerson + ", prizeUnit=" + prizeUnit + ", ranking="
				+ ranking + ", awardsUnit=" + awardsUnit + ", xsxh=" + xsxh
				+ "]";
	}

}
