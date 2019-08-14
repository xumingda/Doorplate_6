package com.yy.doorplate.model;

import java.util.List;

public class QuestionNaireModel {

	// 调察问卷信息
	public String title; // 标题
	public String questionnaireCode; // 调查问卷编码
	public String questionnaireDesc; // 问卷调查描述
	public String questionnaireBelongKind; // 主题归属 1学校,2班级,3课后问卷
	public String questionnaireIconAddress; // 主题图标地址
	public String createTime; // 创建时间
	public List<QuestionModel> questionList; // 问题列表

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
