package com.yy.doorplate.model;

import java.util.List;

public class QuestionModel {

	// �����ʾ�������Ϣ
	public String questionCode; // �������
	public String question; // ����
	public String questionnaireCode; // �����ʾ����
	public String answerType; // �����ͣ���ѡ��RADIO����ѡ��MULTISELECT;�ı�:TEXT
	public List<OptionModel> optionList; // ѡ�������Ϊ��ѡ���ѡʱ�ش�
	public AnswerModel answerInfo; // ����Ϣ

	@Override
	public String toString() {
		return "QuestionModel [questionCode=" + questionCode + ", question="
				+ question + ", questionnaireCode=" + questionnaireCode
				+ ", answerType=" + answerType + "]";
	}
}
