package com.camera;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class CameraPicture {

	public Bitmap bitmap=null;
	public byte[] array=null;
	public ByteArrayOutputStream out;
	/*
	 *==================================
	 *����:public byte[] DecodePicture(byte[] data,int width,int height,int compress)
	 *����:�����������ݵ�ѹ��ת��JPEG
	 *����:data->�����ԭʼ����
	 *    width->��Ҫѹ����ͼƬ���
	 *    height->��Ҫѹ����ͼƬ�߶�
	 *    compress->ѹ���İٷֱȣ�����ʣ��(compress)%,ѹ��(100-compress)%
	 *================================== 
	 */
	public byte[] DecodePicture(byte[] data,int width,int height,int compress) throws IOException{
		
		/*ת����bitmap������*/
		bitmap=BitmapFactory.decodeByteArray(data, 0,data.length);
		
		Log.i("CameraPicture","DecodePicture: bitmap="+bitmap);
		
		if(bitmap==null)
		{
			return null;
		}
		
		/* ����ѹ���ı��� */
		float scaleWidth=((float)width)/bitmap.getWidth();
		float scaleHeight=((float)height)/bitmap.getHeight();
		
		Log.i("CameraPicture","scaleWidth="+scaleWidth+"scaleHeight="+scaleHeight+"bitmap WIDTH="+bitmap.getWidth()+
				"bitmap HEIGHT="+bitmap.getHeight());
		
		/* ͼƬ�����࣬Matrix ��һ��3X3�ľ��� */
		Matrix matrix = new Matrix();  
        matrix.postScale(scaleWidth, scaleHeight);  
		
        /* ��ԭʼbitmap�н�ȡ��һ��ͼƬ */
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),  
        		bitmap.getHeight(), matrix, true); 
        
        /* ����һ���ֽ������ */
        out = new ByteArrayOutputStream(data.length);
        
        /* ѹ����JPEG��ʽ��ͼƬ���������һ���ֽ����� ,compress��ʾʣ��(compress)%,ѹ��(100-compress)%*/
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, compress, out);
        
        array=out.toByteArray();
        
        Log.i("CameraPicture","array.length="+array.length);
        
        try{
        	out.flush();
            
            out.close();
        }
        catch(IOException e){
        	
        	e.printStackTrace();
        	
        	Log.e("CameraPicture","out flush error!");
        }
        
        
		return array;
		
	}
}
