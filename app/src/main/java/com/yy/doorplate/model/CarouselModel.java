package com.yy.doorplate.model;

public class CarouselModel {

	public final static String TYPE_SCHOOL = "学校文化";
	public final static String TYPE_CLASS = "班级文化";
	public final static String TYPE_SCREEN = "屏保";
	public final static String TYPE_MAIN = "首页轮播";

	public final static String SOURCE_CLASS_PHOTO = "班级相册";
	public final static String SOURCE_SCHOOL_PHOTO = "学校相册";
	public final static String SOURCE_CLASS_NEWS = "班级新闻";
	public final static String SOURCE_SCHOOL_NEWS = "学校新闻";
	public final static String SOURCE_CXXXT = "学习通";
	public final static String SOURCE_SX = "视讯";

	public String id;
	public String dataSource; // 数据来源 班级相册，学校相册，班级新闻，学校新闻，学习通，视讯
	public String modelType; // 模块类型 校园文化，班级文化，屏保，首页轮播
	public String publicDate; // 发布时间
	public String outDate; // 过期时间
	public String photos; // 学校相册

	@Override
	public String toString() {
		return "CarouselModel [id=" + id + ", dataSource=" + dataSource
				+ ", modelType=" + modelType + ", publicDate=" + publicDate
				+ ", outDate=" + outDate + ", photos=" + photos + "]";
	}
}
