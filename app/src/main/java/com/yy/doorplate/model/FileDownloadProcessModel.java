package com.yy.doorplate.model;

public class FileDownloadProcessModel {

	// 文件下载进度反馈信息
	public String equCode; // 设备编码
	public String schedule; // 排期
	public String fileName; // 文件名
	public String status; // 状态

	@Override
	public String toString() {
		return "FileDownloadProcessModel [equCode=" + equCode + ", schedule="
				+ schedule + ", fileName=" + fileName + ", status=" + status
				+ "]";
	}

}
