package com.yy.doorplate.model;

public class PlayTaskModel {

	// ����������Ϣ
	public String taskId; // ��������ID
	public String taskName; // ������������
	public String playStartDate; // ���ſ�ʼ����
	public String playEndDate; // ���Ž�������
	public String playStartTime; // ���ſ�ʼʱ��
	public String playEndTime; // ���Ž���ʱ��
	public String srcPath; // ��Դ��ַ
	public String srcType; // ��Դ���ͣ�ͼƬ��IMAGE����Ƶ:VIDEO����Ƶ:AUDIO��ģ��:TEMPLATE
	public String fileType; // �ļ�����
	public String taskType; // �������ͣ��岥:REALTIME����ʱ���ţ�TIMING
	public String playType; // ��������:�㲥��RADIO����棺MEDIA,�����棺REGION_MEDIA,����:
							// SCREEN_SAVER
	public String taskDesc; // ������������
	public String position; // ��������Ϊ�������Ǳ����ҳ��01��У԰�Ļ���02���༶��ɣ�03

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
