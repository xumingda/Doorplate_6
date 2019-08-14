package com.yy.doorplate.model;

public class VideoInfoModel {

	// 小视频
	public String id; // 资源ID
	public String relaObjType; // 关联对象类型，班级：bjdm,个人：rybh
	public String relaObjValue; // 根据关联对象类型赋值
	public String fileDesc; // 文件描述
	public String fileName; // 文件名
	public String resPath; // 资源地址
	public String status; // 状态
	public String likeNum; // 点赞数

	@Override
	public String toString() {
		return "VedioInfoModel [id=" + id + ", relaObjType=" + relaObjType
				+ ", relaObjValue=" + relaObjValue + ", fileDesc=" + fileDesc
				+ ", fileName=" + fileName + ", resPath=" + resPath
				+ ", status=" + status + ", likeNum=" + likeNum + "]";
	}

}
