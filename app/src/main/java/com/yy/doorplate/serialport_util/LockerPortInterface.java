package com.yy.doorplate.serialport_util;

import java.io.OutputStream;

/**
 * @author fanming
 */
public interface LockerPortInterface {

    /**
     *
     * @param buffer  ���ص��ֽ�����
     * @param size    ���ص��ֽڳ���
     * @param path    ������������ж��������Ҫʶ�����ĸ����ڷ��ص����ݣ����򲻴����Ը����Լ��ı���ϰ�ߣ�
     */
    void onLockerDataReceived(final byte[] buffer, final int size, final String path);

    /**
     * �����������ͨ����������򴮿ڷ���ָ��
     * @param outputStream
     */
    void onLockerOutputStream(OutputStream outputStream);

}
