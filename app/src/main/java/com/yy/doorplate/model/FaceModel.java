package com.yy.doorplate.model;

public class FaceModel {

	public int id;
	public String faceId; // ����ID
	public String rybh; // ��Ա���
	public String xm; // ����
	public String js; // ��ɫ��1:��ʦ,2:ѧ��
	public String cardid; // ��Ա����

	@Override
	public String toString() {
		return "FaceModel [id=" + id + ", faceId=" + faceId + ", rybh=" + rybh
				+ ", xm=" + xm + ", js=" + js + ", cardid=" + cardid + "]";
	}

}
