package com.yy.doorplate.model;

public class TimerTypeModel {

	// ��ʱ���ػ�
	public String timerType; // ���ͣ�DATE:���ڣ�REPEAT���ظ�
	public String time; // ʱ��
	public String repeatWeek; // �ظ���
	// Mo:����һ
	// Tu:���ڶ�
	// We:������
	// Th:������
	// Fr:������
	// Sa:������
	// Su:������
	// ����á�,������
	public String date; // ����

	@Override
	public String toString() {
		return "TimerTypeModel [timerType=" + timerType + ", time=" + time
				+ ", repeatWeek=" + repeatWeek + ", date=" + date + "]";
	}
}
