package com.yy.doorplate.model;

public class VideoInfoModel {

	// С��Ƶ
	public String id; // ��ԴID
	public String relaObjType; // �����������ͣ��༶��bjdm,���ˣ�rybh
	public String relaObjValue; // ���ݹ����������͸�ֵ
	public String fileDesc; // �ļ�����
	public String fileName; // �ļ���
	public String resPath; // ��Դ��ַ
	public String status; // ״̬
	public String likeNum; // ������

	@Override
	public String toString() {
		return "VedioInfoModel [id=" + id + ", relaObjType=" + relaObjType
				+ ", relaObjValue=" + relaObjValue + ", fileDesc=" + fileDesc
				+ ", fileName=" + fileName + ", resPath=" + resPath
				+ ", status=" + status + ", likeNum=" + likeNum + "]";
	}

}
