package com.yy.doorplate.model;

public class PicDreOrActivityModel {

	// 图片、目录、活动类
	public String objectId; // 对象ID
	public String parentId; // 父节点
	public String objectType; // 对象类型 1 照片,2 目录,3活动
	public String objectName; // 对象名称
	public String objectComment; // 对象简介
	public String objectIcon; // 对象图标
	public String objectEntityAddress; // 对象实体地址
	public String objectPoint; // 点赞数
	public String objectHasCount; // 拥有对象
	public String kind; // 对象归属 1学校,2班级
	public String createData; // 创建时间
	public String next;

	@Override
	public String toString() {
		return "PicDreOrActivityModel [objectId=" + objectId + ", parentId="
				+ parentId + ", objectType=" + objectType + ", objectName="
				+ objectName + ", objectComment=" + objectComment
				+ ", objectIcon=" + objectIcon + ", objectEntityAddress="
				+ objectEntityAddress + ", objectPoint=" + objectPoint
				+ ", objectHasCount=" + objectHasCount + ", kind=" + kind
				+ ", createData=" + createData + "]";
	}
}
