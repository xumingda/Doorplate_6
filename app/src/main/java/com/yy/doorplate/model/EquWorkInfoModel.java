package com.yy.doorplate.model;

public class EquWorkInfoModel {

	// 设备运行信息
	public String startTime; // 开机时间
	public String nextStopTime; // 下次关机时间
	public String nextStartTime; // 下次开机时间
	public String cpu; // Cpu占用率
	public String physMem; // 物理内存
	public String usedMem; // 使用内存
	public String diskSpace; // 磁盘空间
	public String usedSpace; // 已使用空间
	public String currentScreen; // 当前设备截屏

	@Override
	public String toString() {
		return "EquWorkInfoModel [startTime=" + startTime + ", nextStopTime="
				+ nextStopTime + ", nextStartTime=" + nextStartTime + ", cpu="
				+ cpu + ", physMem=" + physMem + ", usedMem=" + usedMem
				+ ", diskSpace=" + diskSpace + ", usedSpace=" + usedSpace
				+ ", currentScreen=" + currentScreen + "]";
	}
}
