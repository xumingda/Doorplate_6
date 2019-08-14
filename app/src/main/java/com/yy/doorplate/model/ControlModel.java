package com.yy.doorplate.model;

public class ControlModel {

	// 控制信息
	public String id; // 设备或场景ID
	public String type; // 类型：00:设备，01:场景
	public String name; // 名称
	public String status; // 状态,00：开,01:关

	@Override
	public String toString() {
		return "ControlModel [id=" + id + ", type=" + type + ", name=" + name
				+ ", status=" + status + "]";
	}
}
