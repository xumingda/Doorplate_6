package com.yy.doorplate.model;

public class ArtifactVerType {

	// ϵͳ���
	public String artifactVerId; // ����汾ID
	public String sysCode; // �������
	public String oldArtifactCode; // ��һ�汾�汾��
	public String artifactVerCode; // ���°汾��
	public String artifactVerInfo; // ����
	public String artifaceDownloadPath; // ·��
	public String md5Check; // MD5У��
	public String artifactType; // ���� 01������ 02��������
	public String fileName; // �ļ���

	@Override
	public String toString() {
		return "ArtifactVerType [artifactVerId=" + artifactVerId + ", sysCode="
				+ sysCode + ", oldArtifactCode=" + oldArtifactCode
				+ ", artifactVerCode=" + artifactVerCode + ", artifactVerInfo="
				+ artifactVerInfo + ", artifaceDownloadPath="
				+ artifaceDownloadPath + ", md5Check=" + md5Check
				+ ", artifactType=" + artifactType + ", fileName=" + fileName
				+ "]";
	}

}
