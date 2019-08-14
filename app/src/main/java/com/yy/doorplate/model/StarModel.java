package com.yy.doorplate.model;

public class StarModel {

	// 班级之星
	public String id; // 编号
	public String starId; // 编码
	public String starName; // 名称
	public String starType; // 类型1学校，2班级
	public String starIsCode; // 之星所属
	public String getStarPersonCode; // 之星获取人员编号
	public String getStarPersonName; // 之星获取人员名称
	public String awardsPerson; // 颁奖人姓名
	public String awardsUnit; // 颁奖单位
	public String awardsData; // 颁奖日期
	public String iconUrl; // 图片地址
	public String Info; // 获奖详情

	@Override
	public String toString() {
		return "StarModel [id=" + id + ", starId=" + starId + ", starName="
				+ starName + ", starType=" + starType + ", starIsCode="
				+ starIsCode + ", getStarPersonCode=" + getStarPersonCode
				+ ", getStarPersonName=" + getStarPersonName
				+ ", awardsPerson=" + awardsPerson + ", awardsUnit="
				+ awardsUnit + ", awardsData=" + awardsData + ", iconUrl="
				+ iconUrl + ", Info=" + Info + "]";
	}
}
