package com.yy.doorplate.model;

import java.io.Serializable;

public class NoticeInfoModel implements Serializable {

	// ֪ͨ
	public String id; // ��Ϣ����ID
	public String lmdm; // ��Ŀ���룬ѧУ֪ͨ:bpxxtz,ѧУ����:bpxxxw,�༶֪ͨ:bpbjtz,�༶����:bpbjxw
	public String lmmc; // ��Ŀ����
	public String xxzt; // ����
	public String xxnr; // ��������
	public String fbr; // ������
	public String fbrxm; // ����������
	public String fbrbm; // �����˲���
	public String fbrbmmc; // �����˲�������
	public String fbsj; // ����ʱ��
	public String hhs; // �ظ���
	public String dzs; // ������
	public String zttp; // ����ͼƬ
	public String tplb; // ͼƬ�б�
	public String wjlb; // �ļ��б�
	public String remark; // ˵��
	public String create_time; // ����ʱ��
	public String update_time; // ����ʱ��
	public String deltag; // ɾ����־
	public String oprybh; // ������Ա
	public String xxztcode; // ��Ϣ����CODE
	public String xxztlb; // ѧУ������Ŀ���
	public String xxztzt; // ��Ϣ����״̬ 1���� 0����
	public String nrlb; // �������0:��ͨ��Ϣ,1:�����ʾ�;2:ͶƱ����;3:�����
	public String gldm; // ��������ID
	public String endTime; // ʧЧʱ��

	@Override
	public String toString() {
		return "NoticeInfoModel [id=" + id + ", lmdm=" + lmdm + ", lmmc="
				+ lmmc + ", xxzt=" + xxzt + ", xxnr=" + xxnr + ", fbr=" + fbr
				+ ", fbrxm=" + fbrxm + ", fbrbm=" + fbrbm + ", fbrbmmc="
				+ fbrbmmc + ", fbsj=" + fbsj + ", hhs=" + hhs + ", dzs=" + dzs
				+ ", zttp=" + zttp + ", tplb=" + tplb + ", wjlb=" + wjlb
				+ ", remark=" + remark + ", create_time=" + create_time
				+ ", update_time=" + update_time + ", deltag=" + deltag
				+ ", oprybh=" + oprybh + ", xxztcode=" + xxztcode + ", xxztlb="
				+ xxztlb + ", xxztzt=" + xxztzt + ", nrlb=" + nrlb + ", gldm="
				+ gldm + ", end_time=" + endTime + "]";
	}
}
