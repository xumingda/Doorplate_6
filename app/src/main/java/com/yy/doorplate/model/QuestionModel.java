package com.yy.doorplate.model;

import java.util.List;

public class QuestionModel {

	// 调察问卷问题信息
	public String questionCode; // 问题编码
	public String question; // 问题
	public String questionnaireCode; // 调查问卷编码
	public String answerType; // 答案类型，单选：RADIO；多选：MULTISELECT;文本:TEXT
	public List<OptionModel> optionList; // 选项，答案类型为单选或多选时必传
	public AnswerModel answerInfo; // 答案信息

	@Override
	public String toString() {
		return "QuestionModel [questionCode=" + questionCode + ", question="
				+ question + ", questionnaireCode=" + questionnaireCode
				+ ", answerType=" + answerType + "]";
	}
}
