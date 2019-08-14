package com.yy.doorplate.model;

import java.io.Serializable;

public class EquModelTaskModel implements Serializable {

	// 设备模式任务信息
	private static final long serialVersionUID = 1L;

	public String startTime; // 开始时间
	public String stopTime; // 结束时间
	public String equModel; // 设备模式 00:正常 01:考试 02:紧急
	public String screenModelCode; // 展示模版编码

	@Override
	public String toString() {
		return "EquModelTaskModel [startTime=" + startTime + ", stopTime="
				+ stopTime + ", equModel=" + equModel + ", screenModelCode="
				+ screenModelCode + "]";
	}

}
