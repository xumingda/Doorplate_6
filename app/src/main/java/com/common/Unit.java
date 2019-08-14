package com.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

public class Unit {

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static boolean serviceIsRunning(Context context, String className) {
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager
				.getRunningServices(200);

		for (int i = 0; i < mServiceList.size(); i++) {

			if (className.equals(mServiceList.get(i).service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static void copyBigDataToSD(Context context, String strOutFileName,
			String strInFileName) throws IOException {
		InputStream myInput;
		OutputStream myOutput = new FileOutputStream(strOutFileName);
		myInput = context.getAssets().open(strInFileName);
		byte[] buffer = new byte[2048];
		int length = myInput.read(buffer);
		while (length > 0) {
			myOutput.write(buffer, 0, length);
			length = myInput.read(buffer);
		}

		myOutput.flush();
		myInput.close();
		myOutput.close();
	}

	public static byte asciiToByte(byte ascii) {
		byte ret;
		if (ascii >= 'A') {
			ret = (byte) ((ascii - 'A') + 0X0A);
			ret &= 0x0f;
			return ret;
		} else {
			ret = (byte) (ascii - (byte) 0x30);
			ret = (byte) (ret & 0x0f);
			return ret;
		}
	}

	public static byte byteToAscii(byte data) {
		byte ret = 0x0f;
		if (data >= 0X0A) {
			ret = (byte) ((data - 0X0A) + 'A');
		} else {
			ret = (byte) (data + '0');
		}

		return ret;
	}

	public static int AsciiToHexByte(byte[] dst, byte[] src) {
		if (src.length == 0) {
			return 0;
		}

		if ((src.length % 2) != 0) {
			return 0;
		}

		for (int i = 0; i < src.length;) {
			dst[i / 2] = (byte) (asciiToByte(src[i]) << 4);
			dst[i / 2] += asciiToByte(src[i + 1]);
			i += 2;
		}

		return src.length / 2;
	}

	public static int AsciiToHexByte(byte[] dst, int dstOffset, byte[] src,
			int srcOffset, int len) {
		if (len == 0) {
			return 0;
		}

		if ((len % 2) != 0) {
			return 0;
		}

		for (int i = 0; i < len;) {
			dst[(i / 2) + dstOffset] = (byte) (asciiToByte(src[i + srcOffset]) << 4);
			dst[(i / 2) + dstOffset] += asciiToByte(src[i + 1 + srcOffset]);
			i += 2;
		}

		return len / 2;
	}

	public static int HexToAsciiByte(byte[] dst, byte[] src) {

		for (int i = 0; i < src.length; i++) {
			dst[i * 2] = byteToAscii((byte) ((src[i] >> 4) & 0x0f));
			dst[(i * 2) + 1] = byteToAscii((byte) (src[i] & 0x0f));
		}

		return src.length * 2;
	}

	public static int IndexOfByte(byte[] src, int offer, int len, byte data) {
		for (int i = offer; i < len; i++) {
			if (data == src[i]) {
				return i;
			}
		}

		return len;
	}

	public static void LogHex(String tag, byte[] data, int datalen) {
		String hexStr = "";
		for (int i = 0; i < datalen; i++) {

			if ((i % 16) == 0) {
				Log.d(tag, hexStr);
				hexStr = "";
			}
			if (Integer.toHexString(data[i] & 0xff).length() == 1) {
				hexStr += "0";
			}
			hexStr += Integer.toHexString(data[i] & 0xff).toUpperCase() + " ";
		}
		Log.d(tag, hexStr);
	}

	public static String byteToHexStr(byte[] data, int offset, int datalen) {
		String hexStr = "";
		for (int i = 0; i < datalen; i++) {
			if (Integer.toHexString(data[i + offset] & 0xff).length() == 1) {
				hexStr += "0";
			}
			hexStr += Integer.toHexString(data[i + offset] & 0xff)
					.toUpperCase();
		}

		return hexStr;
	}

	public static void saveBmp(String path, Bitmap bmp) {
		if (bmp == null) {
			return;
		}

		// 位图大小
		int nBmpWidth = bmp.getWidth();
		int nBmpHeight = bmp.getHeight();

		// 图像数据大小
		int bufferSize = nBmpHeight * (nBmpWidth * 3 + nBmpWidth % 4);
		File file = new File(path);

		try {

			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream fileos = new FileOutputStream(path);
			// bmp文件头
			int bfType = 0x4d42;
			long bfSize = 14 + 40 + bufferSize;
			int bfReserved1 = 0;
			int bfReserved2 = 0;
			long bfOffBits = 14 + 40;
			// 保存bmp文件头
			writeWord(fileos, bfType);
			writeDword(fileos, bfSize);
			writeWord(fileos, bfReserved1);
			writeWord(fileos, bfReserved2);
			writeDword(fileos, bfOffBits);
			// bmp信息头
			long biSize = 40L;
			long biWidth = nBmpWidth;
			long biHeight = nBmpHeight;
			int biPlanes = 1;
			int biBitCount = 24;
			long biCompression = 0L;
			long biSizeImage = 0L;
			long biXpelsPerMeter = 0L;
			long biYPelsPerMeter = 0L;
			long biClrUsed = 0L;
			long biClrImportant = 0L;
			// 保存bmp信息头
			writeDword(fileos, biSize);
			writeLong(fileos, biWidth);
			writeLong(fileos, biHeight);
			writeWord(fileos, biPlanes);
			writeWord(fileos, biBitCount);
			writeDword(fileos, biCompression);
			writeDword(fileos, biSizeImage);
			writeLong(fileos, biXpelsPerMeter);
			writeLong(fileos, biYPelsPerMeter);
			writeDword(fileos, biClrUsed);
			writeDword(fileos, biClrImportant);
			// 像素扫描
			byte bmpData[] = new byte[bufferSize];
			/*
			 * int wWidth = (nBmpWidth * 3 + nBmpWidth % 4); for (int nCol = 0,
			 * nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol)
			 * for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++,
			 * wByteIdex += 3) { int clr = bmp.getPixel(wRow, nCol);
			 * bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);
			 * bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte)
			 * Color.green(clr); bmpData[nRealCol * wWidth + wByteIdex + 2] =
			 * (byte) Color.red(clr); }
			 */
			for (int i = 0; i < nBmpHeight; i++) {
				for (int j = 0; j < nBmpWidth; j++) {
					int clr = bmp.getPixel(j, i);
					bmpData[(i * nBmpWidth + j) * 3] = (byte) Color.blue(clr);
					bmpData[((i * nBmpWidth + j) * 3) + 1] = (byte) Color
							.green(clr);
					bmpData[((i * nBmpWidth + j) * 3) + 2] = (byte) Color
							.red(clr);
				}
			}
			fileos.write(bmpData);
			fileos.flush();
			fileos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void moveFileToDir(String oldPath, String newDir) {
		File file = new File(oldPath);

		if (!new File(newDir).exists()) {
			new File(newDir).mkdirs();
		}

		String newPath = newDir + "/" + file.getName();
		copyFile(oldPath, newPath);
		if (new File(newPath).exists()) {
			file.delete();
		}
	}

	/**
	 * 获取缩放后的本地图片
	 * 
	 * @param filePath
	 *            文件路径
	 * @param width
	 *            宽
	 * @param height
	 *            高
	 * @return
	 */
	public static Bitmap readBitmapFromFileDescriptor(String filePath,
			int width, int height) {
		try {
			FileInputStream fis = new FileInputStream(filePath);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFileDescriptor(fis.getFD(), null, options);
			float srcWidth = options.outWidth;
			float srcHeight = options.outHeight;
			int inSampleSize = 1;

			if (srcHeight > height || srcWidth > width) {
				if (srcWidth > srcHeight) {
					inSampleSize = Math.round(srcHeight / height);
				} else {
					inSampleSize = Math.round(srcWidth / width);
				}
			}

			options.inJustDecodeBounds = false;
			options.inSampleSize = inSampleSize;

			return BitmapFactory.decodeFileDescriptor(fis.getFD(), null,
					options);
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static boolean copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);

			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				inStream.close();

				return true;
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}
		return false;
	}

	public static void writeWord(FileOutputStream fout, int value)
			throws IOException {
		byte[] b = new byte[2];
		b[0] = (byte) (value & 0xff);
		b[1] = (byte) (value >> 8 & 0xff);
		fout.write(b);
	}

	public static void writeLong(FileOutputStream fout, long value)
			throws IOException {
		byte[] b = new byte[4];
		b[0] = (byte) (value & 0xff);
		b[1] = (byte) (value >> 8 & 0xff);
		b[2] = (byte) (value >> 16 & 0xff);
		b[3] = (byte) (value >> 24 & 0xff);
		fout.write(b);
	}

	public static void writeDword(FileOutputStream fout, long value)
			throws IOException {
		byte[] b = new byte[4];
		b[0] = (byte) (value & 0xff);
		b[1] = (byte) (value >> 8 & 0xff);
		b[2] = (byte) (value >> 16 & 0xff);
		b[3] = (byte) (value >> 24 & 0xff);
		fout.write(b);
	}
}
