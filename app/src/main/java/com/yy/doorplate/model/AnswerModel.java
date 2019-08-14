package com.yy.doorplate.model;

public class AnswerModel {

	// 调察问卷答案
	public String questionCode; // 问题编码
	public String answer; // 答案
	public String replyPerson; // 回复人员
	public String questionnaireCode; // 调查问卷编码
	public String skjhid; // 授课计划ID

	@Override
	public String toString() {
		return "AnswerModel [questionCode=" + questionCode + ", answer="
				+ answer + ", replyPerson=" + replyPerson
				+ ", questionnaireCode=" + questionnaireCode + ", skjhid="
				+ skjhid + "]";
	}

}
