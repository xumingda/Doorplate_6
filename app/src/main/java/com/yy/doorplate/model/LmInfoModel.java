package com.yy.doorplate.model;

public class LmInfoModel {

	// 栏目信息
	public String id; // id
	public String lmdm; // 栏目代码
	public String lmmc; // 栏目名称
	public String xxztlb; // 子栏目代码
	public String xxztlbmc; // 子栏目名称
	public String sjlx; // 类型 1本地数据，2第三方数据
	public String lmlj; // 数据来源

	@Override
	public String toString() {
		return "LmInfoModel [id=" + id + ", lmdm=" + lmdm + ", lmmc=" + lmmc
				+ ", xxztlb=" + xxztlb + ", xxztlbmc=" + xxztlbmc + ", sjlx="
				+ sjlx + ", lmlj=" + lmlj + "]";
	}
}
