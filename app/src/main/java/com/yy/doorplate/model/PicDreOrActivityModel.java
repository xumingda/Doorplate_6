package com.yy.doorplate.model;

public class PicDreOrActivityModel {

	// ͼƬ��Ŀ¼�����
	public String objectId; // ����ID
	public String parentId; // ���ڵ�
	public String objectType; // �������� 1 ��Ƭ,2 Ŀ¼,3�
	public String objectName; // ��������
	public String objectComment; // ������
	public String objectIcon; // ����ͼ��
	public String objectEntityAddress; // ����ʵ���ַ
	public String objectPoint; // ������
	public String objectHasCount; // ӵ�ж���
	public String kind; // ������� 1ѧУ,2�༶
	public String createData; // ����ʱ��
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
