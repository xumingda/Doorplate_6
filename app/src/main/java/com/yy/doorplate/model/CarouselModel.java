package com.yy.doorplate.model;

public class CarouselModel {

	public final static String TYPE_SCHOOL = "ѧУ�Ļ�";
	public final static String TYPE_CLASS = "�༶�Ļ�";
	public final static String TYPE_SCREEN = "����";
	public final static String TYPE_MAIN = "��ҳ�ֲ�";

	public final static String SOURCE_CLASS_PHOTO = "�༶���";
	public final static String SOURCE_SCHOOL_PHOTO = "ѧУ���";
	public final static String SOURCE_CLASS_NEWS = "�༶����";
	public final static String SOURCE_SCHOOL_NEWS = "ѧУ����";
	public final static String SOURCE_CXXXT = "ѧϰͨ";
	public final static String SOURCE_SX = "��Ѷ";

	public String id;
	public String dataSource; // ������Դ �༶��ᣬѧУ��ᣬ�༶���ţ�ѧУ���ţ�ѧϰͨ����Ѷ
	public String modelType; // ģ������ У԰�Ļ����༶�Ļ�����������ҳ�ֲ�
	public String publicDate; // ����ʱ��
	public String outDate; // ����ʱ��
	public String photos; // ѧУ���

	@Override
	public String toString() {
		return "CarouselModel [id=" + id + ", dataSource=" + dataSource
				+ ", modelType=" + modelType + ", publicDate=" + publicDate
				+ ", outDate=" + outDate + ", photos=" + photos + "]";
	}
}
