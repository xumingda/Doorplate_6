package com.yy.doorplate.model;

import java.util.List;

public class QuestionNaireModel {

	// �����ʾ���Ϣ
	public String title; // ����
	public String questionnaireCode; // �����ʾ����
	public String questionnaireDesc; // �ʾ��������
	public String questionnaireBelongKind; // ������� 1ѧУ,2�༶,3�κ��ʾ�
	public String questionnaireIconAddress; // ����ͼ���ַ
	public String createTime; // ����ʱ��
	public List<QuestionModel> questionList; // �����б�

	@Override
	public String toString() {
		return "QuestionNaireModel [title=" + title + ", questionnaireCode="
				+ questionnaireCode + ", questionnaireDesc="
				+ questionnaireDesc + ", questionnaireBelongKind="
				+ questionnaireBelongKind + ", questionnaireIconAddress="
				+ questionnaireIconAddress + ", createTime=" + createTime
				+ ", questionList=" + questionList + "]";
	}
}
