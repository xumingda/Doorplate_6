package com.yy.doorplate.model;

public class FileDownloadProcessModel {

	// �ļ����ؽ��ȷ�����Ϣ
	public String equCode; // �豸����
	public String schedule; // ����
	public String fileName; // �ļ���
	public String status; // ״̬

	@Override
	public String toString() {
		return "FileDownloadProcessModel [equCode=" + equCode + ", schedule="
				+ schedule + ", fileName=" + fileName + ", status=" + status
				+ "]";
	}

}
