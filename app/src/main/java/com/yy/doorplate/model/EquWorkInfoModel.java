package com.yy.doorplate.model;

public class EquWorkInfoModel {

	// �豸������Ϣ
	public String startTime; // ����ʱ��
	public String nextStopTime; // �´ιػ�ʱ��
	public String nextStartTime; // �´ο���ʱ��
	public String cpu; // Cpuռ����
	public String physMem; // �����ڴ�
	public String usedMem; // ʹ���ڴ�
	public String diskSpace; // ���̿ռ�
	public String usedSpace; // ��ʹ�ÿռ�
	public String currentScreen; // ��ǰ�豸����

	@Override
	public String toString() {
		return "EquWorkInfoModel [startTime=" + startTime + ", nextStopTime="
				+ nextStopTime + ", nextStartTime=" + nextStartTime + ", cpu="
				+ cpu + ", physMem=" + physMem + ", usedMem=" + usedMem
				+ ", diskSpace=" + diskSpace + ", usedSpace=" + usedSpace
				+ ", currentScreen=" + currentScreen + "]";
	}
}
