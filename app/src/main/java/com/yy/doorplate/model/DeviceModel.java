package com.yy.doorplate.model;

public class DeviceModel {

	// 智能家居，设备控制
	public int id;
	public String name;
	public int img;
	public int state;

	@Override
	public String toString() {
		return "DeviceModel [id=" + id + ", name=" + name + ", img=" + img
				+ ", state=" + state + "]";
	}
}
