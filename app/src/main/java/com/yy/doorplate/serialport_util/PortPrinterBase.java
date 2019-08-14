package com.yy.doorplate.serialport_util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by WNI10 on 2018/3/31.
 */

public class PortPrinterBase {
    private OutputStream out;
    protected int lineCount = 40;
    private String printType="0";
    public PortPrinterBase(OutputStream out, String printType){
        this.out = out;
        this.printType = printType;
        initPrinter();
        this.lineCount = 20;
    }
    public static final String LEFT = "LEFT";
    public static final String CENTER = "CENTER";
    public static final String RIGHT = "RIGHT";
//    public static final String
    public static final byte HT = 0x9;
    public static final byte LF = 0x0A;
    public static final byte CR = 0x0D;
    public static final byte ESC = 0x1B;
    public static final byte DLE = 0x10;
    public static final byte GS = 0x1D;
    public static final byte FS = 0x1C;
    public static final byte STX = 0x02;
    public static final byte US = 0x1F;
    public static final byte CAN = 0x18;
    public static final byte CLR = 0x0C;
    public static final byte EOT = 0x04;

    /* ��ʼ����ӡ�� */
    public static final byte[] ESC_INIT = new byte[] {ESC, '@'};
    /* ���ñ�׼ģʽ */
    public static final byte[] ESC_STANDARD = new byte[] {ESC, 'S'};
    /* ���ú��ִ�ӡģʽ */
    public static final byte[] ESC_CN_FONT = new byte[] {FS, '&'};
    /* ѡ���ַ��� */
    public static final byte[] ESC_SELECT_CHARACTER = new byte[] {ESC, 'R', 9};
    /* �����û��Զ��庺������ �h7118 */
    public static final byte[] ESC_FS_2 = new byte[] {FS, 0x32, 0x71, 0x18};
    /* ȡ���û��Զ������� */
    public static final byte[] ESC_CANCEL_DEFINE_FONT = new byte[]{ESC, '%', 0};
    /* ��Ǯ��ָ�� */
    public static final byte[] ESC_OPEN_DRAWER = new byte[]{ESC, 'p', 0x00, 0x10, (byte) 0xff};
    /* ��ָֽ��GS V m
    * m  0,48 Executes a full cut(cuts the paper completely)
    *    1,49 Excutes a partilal cut(one point left uncut)
    */
    public static final byte[] POS_CUT_MODE_FULL = new byte[]{GS, 'V', 0x00};
    public static final byte[] POS_CUT_MODE_PARTIAL = new byte[]{GS, 'V', 0x01};
    /* �����ַ� ���������A (6 ��12)�������ַ� ��ȫ������A ��12��12�� */
    public static final byte[] ESC_FONT_A = new byte[]{ESC, '!', 0};
    /* �����ַ� ���������B (8��16)�������ַ� ��ȫ������B ��16��16�� */
    public static final byte[] ESC_FONT_B = new byte[]{ESC, '!', 1};
    /* 12*24   0/48*/
    public static final byte[] ESC_FONTA= new byte[]{ESC, 'M', 48};
    /* 9*17    1/49*/
    public static final byte[] ESC_FONTB= new byte[]{ESC, 'M', 1};
    /* Ĭ����ɫ����ָ�� */
    public static final byte[] ESC_FONT_COLOR_DEFAULT = new byte[] {ESC, 'r', 0x00};
    /* ��ɫ����ָ�� */
    public static final byte[] ESC_FONT_COLOR_RED = new byte[] {ESC, 'r', 0x01 };
    /* ��׼��С */
    public static final byte[] FS_FONT_ALIGN = new byte[]{FS, 0x21, 1, ESC, 0x21, 1};
    /* ����Ŵ�һ�� */
    public static final byte[] FS_FONT_ALIGN_DOUBLE = new byte[]{FS, 0x21, 4, ESC, 0x21, 4};
    /* ����Ŵ�һ�� */
    public static final byte[] FS_FONT_VERTICAL_DOUBLE = new byte[]{FS, 0x21, 8, ESC, 0x21, 8, GS, '!', 0x01};
    /* �������򶼷Ŵ�һ�� */
    public static final byte[] FS_FONT_DOUBLE = new byte[]{FS, 0x21, 12, ESC, 0x21, 48};
    /* �����ӡ���� */
    public static final byte[] ESC_ALIGN_LEFT = new byte[]{0x1b,'a', 0x00};
    /* ���д�ӡ���� */
    public static final byte[] ESC_ALIGN_CENTER = new byte[]{0x1b,'a', 0x01};
    /* ���Ҵ�ӡ���� */
    public static final byte[] ESC_ALIGN_RIGHT = new byte[]{0x1b,'a', 0x02};
    /* ����Ӵ� */
    public static final byte[] ESC_SETTING_BOLD = new byte[]{ESC, 0x45, 1};
    /* ȡ������Ӵ� */
    public static final byte[] ESC_CANCEL_BOLD = new byte[]{ESC, 0x45, 0};
    //DLE EOT n ʵʱ״̬����
    //������ؽ��Ϊ22
    /**
     * ��DLE EOT n ʵʱ״̬����
     [��ʽ] ASCII�� DLE EOT n
     ʮ�������� 10 04 n
     ʮ������ 16 4 n
     [��Χ] 1 �� n �� 4
     [����] �������в�����ʵʱ���ʹ�ӡ��״̬������ n ����ָ����Ҫ���͵Ĵ�ӡ��״̬��
     n = 1�����ʹ�ӡ��״̬
     n = 2�������ѻ�״̬
     n = 3�����ʹ���״̬
     n = 4������ֽ������״̬
     [ע��] ��ӡ���յ�������������������״̬
     ���������Ҫ����2��������ֽڵ����������С�
     ��ʹ��ӡ����ESC =(ѡ������)��������Ϊ��ֹ����������Ȼ��Ч��
     ��ӡ�����͵�ǰ״̬��ÿһ״̬��1���ֽ����ݱ�ʾ��
     ��ӡ������״̬ʱ����ȷ�������Ƿ��յ���
     ��ӡ���յ�����������ִ�С�
     ������ֻ�Դ��ڴ�ӡ����Ч����ӡ�����κ�״̬���յ����������ִ�С�
     */
    public static final byte[] PRINT_STATE_DLE_EOT = new byte[] {DLE, EOT,0x01};

    public void initPrinter(){
        try {
            //modify by gongqiyi 20090917
            //ESC_INIT ������ջ�����������
            //out.write(ESC_INIT);
            //�Զ�������
            //out.write(ESC_FS_2);
            out.write(ESC_STANDARD);
            out.write(ESC_CANCEL_DEFINE_FONT);
            out.write(ESC_FONTA);
            out.write(ESC_SELECT_CHARACTER);
            //���뺺��ģʽ��ӡ
            //out.write(ESC_CN_FONT);


            //out.write(ESC_FONT_B);
            //out.write(ESC_FONTA);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * ��ֽ����ֽλ�ò���ֽ
     */
    /*public void executeLineFeedAndPaperCut(){
        try {
            out.write(PrinterParameterConf.printerParameterConf.getProperty
                    (PrinterParameterConf.PRINTCUTLINE_NAME).getBytes());
            out.write(POS_CUT_MODE_PARTIAL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    /**
     * ����ͷ��ӡ
     * @param str
     */
    public void billHeaderPrinter(String str){
        try {
            out.write(ESC_ALIGN_CENTER);
            out.write(FS_FONT_DOUBLE);
            out.write((str+"\n").getBytes());
            out.write(LF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * �е��Ŵ�ӡ
     * @param str
     */
    public void callNumPrinter(String str){
        try {
            out.write(ESC_ALIGN_LEFT);
            out.write(FS_FONT_DOUBLE);
            out.write((str+"\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * ˫����С����
     * @param str
     */
    public void doubleSizePrinter(String str, String align){
        try {
            if(CENTER.equals(align)){
                out.write(ESC_ALIGN_LEFT);
            }else if(RIGHT.equals(align)){
                out.write(ESC_ALIGN_RIGHT);
            }else{
                out.write(ESC_ALIGN_LEFT);
            }
            out.write(FS_FONT_DOUBLE);
            out.write((str+"\n").getBytes());
            //out.write(LF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * ��׼�����ӡһ��
     * @param str ���ӡ���ַ���
     * @param align ��ӡ��λ�� LEFT/CENTER/RIGHT ����ΪĬ�Ͼ����ӡ
     */
    public void standardPrinterLine(String str, String align){
        try{
            if(CENTER.equals(align)){
                out.write(ESC_ALIGN_CENTER);
                out.write(FS_FONT_ALIGN);
                out.write(ESC_CN_FONT);
                out.write(ESC_CANCEL_BOLD);
                if("1".equals(printType)){
                    out.write(ESC_FONTA);
                }else{
                    out.write(ESC_FONT_B);
                }
                out.write(str.getBytes());
            }else if(RIGHT.equals(align)){
                out.write(ESC_ALIGN_RIGHT);
                out.write(FS_FONT_ALIGN);
                out.write(ESC_CN_FONT);
                out.write(ESC_CANCEL_BOLD);
                if("1".equals(printType)){
                    out.write(ESC_FONTA);
                }else{
                    out.write(ESC_FONT_B);
                }
                out.write(str.getBytes());
            }else{
                out.write(ESC_ALIGN_LEFT);
                out.write(FS_FONT_ALIGN);
                out.write(ESC_CN_FONT);
                out.write(ESC_CANCEL_BOLD);
                if("1".equals(printType)){
                    out.write(ESC_FONTA);
                }else{
                    out.write(ESC_FONT_B);
                }
                out.write(str.getBytes());
            }
            out.write("\n".getBytes());
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ��׼���������ӡһ��
     * @param str ���ӡ���ַ���
     * @param align ��ӡ��λ�� LEFT/CENTER/RIGHT ����ΪĬ�Ͼ����ӡ
     */
    public void standardBoldPrinterLine(String str, String align){
        try{
            if(CENTER.equals(align)){
                out.write(ESC_ALIGN_CENTER);
                out.write(FS_FONT_ALIGN);
                out.write(ESC_CN_FONT);
                out.write(ESC_SETTING_BOLD);
                if("1".equals(printType)){
                    out.write(ESC_FONTA);
                }else{
                    out.write(ESC_FONT_B);
                }
                out.write(str.getBytes());
            }else if(RIGHT.equals(align)){
                out.write(ESC_ALIGN_RIGHT);
                out.write(FS_FONT_ALIGN);
                out.write(ESC_CN_FONT);
                out.write(ESC_SETTING_BOLD);
                if("1".equals(printType)){
                    out.write(ESC_FONTA);
                }else{
                    out.write(ESC_FONT_B);
                }
                out.write(str.getBytes());
            }else{
                out.write(ESC_ALIGN_LEFT);
                out.write(FS_FONT_ALIGN);
                out.write(ESC_CN_FONT);
                out.write(ESC_SETTING_BOLD);
                if("1".equals(printType)){
                    out.write(ESC_FONTA);
                }else{
                    out.write(ESC_FONT_B);
                }
                out.write(str.getBytes());
            }
            out.write("\n".getBytes());
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ˫�������尴�д�ӡ
     * @param str
     * @param align
     */
    public void largeSizePrinterLine(String str, String align){
        try{
            if(CENTER.equals(align)){
                out.write(ESC_ALIGN_CENTER);
                out.write(FS_FONT_ALIGN_DOUBLE);
                out.write(str.getBytes());
            }else if(RIGHT.equals(align)){
                out.write(ESC_ALIGN_RIGHT);
                out.write(FS_FONT_ALIGN_DOUBLE);
                out.write(str.getBytes());
            }else{
                out.write(ESC_ALIGN_LEFT);
                out.write(FS_FONT_ALIGN_DOUBLE);
                out.write(str.getBytes());
            }
            out.write("\n".getBytes());
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ˫�������尴�д�ӡ
     * @param str
     * @param align
     */
    public void largeHSizePrinterLine(String str, String align){
        try{
            if(CENTER.equals(align)){
                out.write(ESC_ALIGN_CENTER);
                out.write(FS_FONT_VERTICAL_DOUBLE);
                out.write(str.getBytes());
            }else if(RIGHT.equals(align)){
                out.write(ESC_ALIGN_RIGHT);
                out.write(FS_FONT_VERTICAL_DOUBLE);
                out.write(str.getBytes());
            }else{
                out.write(ESC_ALIGN_LEFT);
                out.write(FS_FONT_VERTICAL_DOUBLE);
                out.write(str.getBytes());
            }
            out.write("\n".getBytes());
        }catch(IOException e) {
            e.printStackTrace();
        }
    }    /**
     * ��������ɫ���д�ӡ
     * @param str
     * @param align
     */
    public void largeSizeRedPrinterLine(String str, String align){
        try{
            if(CENTER.equals(align)){
                out.write(ESC_ALIGN_CENTER);
                out.write(FS_FONT_ALIGN_DOUBLE);
                out.write(ESC_FONT_COLOR_RED);
                out.write(str.getBytes());
            }else if(RIGHT.equals(align)){
                out.write(ESC_ALIGN_RIGHT);
                out.write(FS_FONT_ALIGN_DOUBLE);
                out.write(ESC_FONT_COLOR_RED);
                out.write(str.getBytes());
            }else{
                out.write(ESC_ALIGN_LEFT);
                out.write(FS_FONT_ALIGN_DOUBLE);
                out.write(ESC_FONT_COLOR_RED);
                out.write(str.getBytes());
            }
            out.write("\n".getBytes());
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void openDrawer(){
        try {
            out.write(ESC_OPEN_DRAWER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String makePrintString(int lineChars, String txt1, String txt2){
        if(txt1 == null){
            txt1 = "";
        }
        if(txt2 == null){
            txt2 = "";
        }
        int spaces = 0;
        String tab = "";
        try{
            spaces = lineChars - (txt1.getBytes().length + txt2.getBytes().length);
            for (int j = 0 ; j < spaces ; j++){
                tab += " ";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return txt1 + tab + txt2;
    }
    public String makePrintString(int lineChars, String txt1, String txt2, String txt3){
        if(txt1 == null){
            txt1 = "";
        }
        if(txt2 == null){
            txt2 = "";
        }
        if(txt3 == null){
            txt3 = "";
        }
        int spaces = 0;
        int lineChars1 = lineChars*2/3;
        String tab = "";
        String returnStr = txt1;
        try{
            spaces = lineChars1 - (returnStr.getBytes().length + txt2.getBytes().length);
            for (int j = 0 ; j < spaces ; j++){
                tab += " ";
            }
            returnStr = txt1 + tab + txt2;
            spaces = lineChars - (returnStr.getBytes().length + txt3.getBytes().length);
            tab = "";
            for (int j = 0 ; j < spaces ; j++){
                tab += " ";
            }
            returnStr = returnStr + tab + txt3;
        }catch(Exception e){
            e.printStackTrace();
        }
        return returnStr;
    }
}
