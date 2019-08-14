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
	 *名称:public byte[] DecodePicture(byte[] data,int width,int height,int compress)
	 *功能:用于拍照数据的压缩转成JPEG
	 *参数:data->传入的原始数据
	 *    width->需要压缩的图片宽度
	 *    height->需要压缩的图片高度
	 *    compress->压缩的百分比，代表剩余(compress)%,压缩(100-compress)%
	 *================================== 
	 */
	public byte[] DecodePicture(byte[] data,int width,int height,int compress) throws IOException{
		
		/*转换成bitmap数据流*/
		bitmap=BitmapFactory.decodeByteArray(data, 0,data.length);
		
		Log.i("CameraPicture","DecodePicture: bitmap="+bitmap);
		
		if(bitmap==null)
		{
			return null;
		}
		
		/* 计算压缩的比例 */
		float scaleWidth=((float)width)/bitmap.getWidth();
		float scaleHeight=((float)height)/bitmap.getHeight();
		
		Log.i("CameraPicture","scaleWidth="+scaleWidth+"scaleHeight="+scaleHeight+"bitmap WIDTH="+bitmap.getWidth()+
				"bitmap HEIGHT="+bitmap.getHeight());
		
		/* 图片处理类，Matrix 是一个3X3的矩阵 */
		Matrix matrix = new Matrix();  
        matrix.postScale(scaleWidth, scaleHeight);  
		
        /* 从原始bitmap中截取出一张图片 */
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),  
        		bitmap.getHeight(), matrix, true); 
        
        /* 申请一个字节输出流 */
        out = new ByteArrayOutputStream(data.length);
        
        /* 压缩成JPEG格式的图片，并输出到一个字节流中 ,compress表示剩余(compress)%,压缩(100-compress)%*/
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
