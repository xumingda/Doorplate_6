package com.yy.doorplate.model;

public class FaceModel {

	public int id;
	public String faceId; // 人脸ID
	public String rybh; // 人员编号
	public String xm; // 姓名
	public String js; // 角色，1:教师,2:学生
	public String cardid; // 人员卡号

	@Override
	public String toString() {
		return "FaceModel [id=" + id + ", faceId=" + faceId + ", rybh=" + rybh
				+ ", xm=" + xm + ", js=" + js + ", cardid=" + cardid + "]";
	}

}
