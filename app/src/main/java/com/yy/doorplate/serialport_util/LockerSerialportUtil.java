package com.yy.doorplate.serialport_util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;


import com.yy.doorplate.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

/**
 * @author fanming
 * ���ڹ�����
 */
public class LockerSerialportUtil {
	
	private static LockerSerialportUtil INSTANCE;
	
	private static final String TAG = "SerialPort";
    protected OutputStream mOutputStreamBox;
    protected InputStream mInputStreamBox;
    protected ReadThreadBox mReadThreadBox;
    private SerialBroadcastReceiverBox m_SerialRecBox;
    private LockerPortInterface sportInterface;
    protected SerialPort boxPort;
    boolean firstRegisterBox = true;
    private static Context mContext;
    private String path;
    private  int baudrate;
    private SerialBroadcastReceiverBox m_Receiver2;
    
    private LockerSerialportUtil(String path, int baudrate, LockerPortInterface lockerPortInterface){
    	this.path = path;
        this.baudrate = baudrate;
    	setSerialPort(lockerPortInterface);
    }
    
    public static void init(Context context, String path, int baudrate, LockerPortInterface lockerPortInterface){
    	mContext = context;
    	INSTANCE = new LockerSerialportUtil(path, baudrate, lockerPortInterface);
    }
    
    public static LockerSerialportUtil getInstance(){
		return INSTANCE;
    }
    
    public SerialPort getboxPort() throws SecurityException, IOException, InvalidParameterException {
        return boxPort;
    }

    public class SerialBroadcastReceiverBox extends BroadcastReceiver {
        Context ct = null;
        public SerialBroadcastReceiverBox(Context c) {
            Log.i(TAG, "enter  SerialBroadcastReceiverBox ");
            ct = c;
            m_Receiver2 = this;
        }

        //ע��  �����㲥
        public void registerAction() {
            Log.i(TAG, "enter  registerAction ");
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            if(ct == null){
            	Log.e(TAG, "ct nulll");
            }
            if(m_Receiver2 == null){
                Log.e(TAG, "m_Receiver2 nulll");
            }
            ct.registerReceiver(m_Receiver2, filter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "enter  onReceive");
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) { // ��Ļ������򿪴���
                Log.i(TAG, "recevied  ACTION_SCREEN_ON ");
                if (boxPort == null) {
                    try {
                        boxPort = new SerialPort(new File(path), baudrate, 0);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                        DisplayError(context, R.string.error_security);
                    } catch (IOException e) {
                        e.printStackTrace();
                        DisplayError(context,R.string.error_unknown);
                    } catch (InvalidParameterException e) {
                        e.printStackTrace();
                        DisplayError(context,R.string.error_configuration);
                    }
                }
            }

            if (action.equals(Intent.ACTION_SCREEN_OFF)) {  // ������رմ���
                Log.i(TAG, "recevied  ACTION_SCREEN_OFF ");
                if (boxPort != null) {
                    closeSerialPort();
                    mReadThreadBox.interrupt();
                }
            }
            context.unregisterReceiver(this);
        }
    }

    /**
     * ���ڹر�
     */
    public void closeSerialPort() {
        if (boxPort != null) {
            boxPort.close();
            boxPort = null;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            mContext.unregisterReceiver(m_Receiver2);
        }
    }
    /**
     * ���ڳ�ʼ���������ʾ
     * @param context
     * @param resourceId
     */
    private void DisplayError(Context context, int resourceId) {
    	Toast.makeText(context,path+"\n"+context.getString(resourceId), Toast.LENGTH_SHORT).show();
    }

    boolean boxFlag = true;

    /**
     * ���������ݵ����߳�
     * @author fanming
     *
     */
    private class ReadThreadBox extends Thread {
        @Override
        public void run() {
            super.run();
            while (boxFlag) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStreamBox == null) return;
                    /* read��һֱ�ȴ����ݣ����Ҫ�ж��Ƿ������ɣ�ֻ�����ý�����ʶ��������������Ĵ��� */
                    //Log.i("SerialPort", mReadThreadBox.getName() + "---locker port------��ȡ��");
                    size = mInputStreamBox.read(buffer);
//                    sendMessage();
                    if (size > 0) {
                        sportInterface.onLockerDataReceived(buffer, size,path);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                //Log.i("SerialPort","read end");
            }
            Log.i("SerialPort", "-----locker port--- �ر�");
        }
    }


    /**
     * ��ʼ������
     * @param lockerPortInterface
     */
    private void setSerialPort(LockerPortInterface lockerPortInterface){
        this.sportInterface = lockerPortInterface;
        try {
            /* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }
			/* Open the serial port */
            boxPort = new SerialPort(new File(path), baudrate, 0);

            mOutputStreamBox = boxPort.getOutputStream();
            mInputStreamBox = boxPort.getInputStream();
			/* Create a serial rec buf  thread */
            mReadThreadBox = new ReadThreadBox();
//            SerialPortState = true;
            mReadThreadBox.start();
            if (firstRegisterBox) {
            	if(mContext == null){
            		Log.e(TAG, "mContext nulll");
            	}
//                m_SerialRecBox = new SerialBroadcastReceiverBox(mContext);
//                m_SerialRecBox.registerAction();
                firstRegisterBox = false;
                Log.i(TAG, "----locker port--- ע�����");
            }
            lockerPortInterface.onLockerOutputStream(mOutputStreamBox);
        } catch (SecurityException e) {
            e.printStackTrace();
            DisplayError(mContext,R.string.error_security);
        } catch (IOException e) {
            e.printStackTrace();
            DisplayError(mContext,R.string.error_unknown);
        } catch (InvalidParameterException e) {
            e.printStackTrace();
            DisplayError(mContext,R.string.error_configuration);
        }
    }


}

