package com.yy.doorplate.model;

public class WeatherInfo {

	// ����
	public String currentCity;// ��ǰ����
	public String date;// ����
	public String dayPictureUrl;// ����ͼƬ·��
	public String nightPictureUrl;// ҹ��ͼƬ·��
	public String weather;// ����
	public String wind;// ��
	public String temperature;// �¶�

	@Override
	public String toString() {
		return "WeatherInfo [currentCity=" + currentCity + ", date=" + date
				+ ", dayPictureUrl=" + dayPictureUrl + ", nightPictureUrl="
				+ nightPictureUrl + ", weather=" + weather + ", wind=" + wind
				+ ", temperature=" + temperature + "]";
	}

}
