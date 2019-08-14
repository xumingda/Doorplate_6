package com.yy.doorplate.model;

public class OptionModel {

	// 调察问卷问题选项
	public String questionCode; // 问题编码
	public String optionCode; // 选项编码
	public String optionName; // 选项名称

	@Override
	public String toString() {
		return "OptionModel [questionCode=" + questionCode + ", optionCode="
				+ optionCode + ", optionName=" + optionName + "]";
	}
}
