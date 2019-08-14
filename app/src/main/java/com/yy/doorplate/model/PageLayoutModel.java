package com.yy.doorplate.model;

public class PageLayoutModel {

	// 页面布局
	public String pageCode; // 页面编码：首页：INDEX，校园文化：SCHOOL，班级风采:CLASS
	public String resPath; // 资源地址
	public String pageName; // 页面名称

	@Override
	public String toString() {
		return "PageLayoutModel [pageCode=" + pageCode + ", resPath=" + resPath
				+ ", pageName=" + pageName + "]";
	}

}
