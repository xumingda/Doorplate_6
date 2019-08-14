package com.yy.doorplate.model;

public class PlayTaskModel {

	// 播放任务信息
	public String taskId; // 播放任务ID
	public String taskName; // 播放任务名称
	public String playStartDate; // 播放开始日期
	public String playEndDate; // 播放结束日期
	public String playStartTime; // 播放开始时间
	public String playEndTime; // 播放结束时间
	public String srcPath; // 资源地址
	public String srcType; // 资源类型，图片：IMAGE、视频:VIDEO、音频:AUDIO，模版:TEMPLATE
	public String fileType; // 文件类型
	public String taskType; // 任务类型，插播:REALTIME，定时播放：TIMING
	public String playType; // 播放类型:广播：RADIO，广告：MEDIA,区域广告：REGION_MEDIA,屏保:
							// SCREEN_SAVER
	public String taskDesc; // 播放任务描述
	public String position; // 播放类型为区域广告是必填，首页：01；校园文化：02；班级风采：03

	@Override
	public String toString() {
		return "PlayTaskModel [taskId=" + taskId + ", taskName=" + taskName
				+ ", playStartDate=" + playStartDate + ", playEndDate="
				+ playEndDate + ", playStartTime=" + playStartTime
				+ ", playEndTime=" + playEndTime + ", srcPath=" + srcPath
				+ ", srcType=" + srcType + ", fileType=" + fileType
				+ ", taskType=" + taskType + ", playType=" + playType
				+ ", taskDesc=" + taskDesc + ", position=" + position + "]";
	}

}
