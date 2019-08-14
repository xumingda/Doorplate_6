package com.yy.doorplate.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class General {

	private static final String TAG = "General";
	public static int DevId;

	public static String getFileType(String path) {
		int index = path.lastIndexOf(".");
		if (index >= 0) {
			return path.substring(index + 1);
		}
		return null;
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

	public static byte[] getBytes(long s) {
		byte[] buf = new byte[8];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = (byte) (s & 0x00000000000000ff);
			s >>= 8;
		}
		return buf;
	}

	private static String HexToStr(byte data) {
		String hexStr = "";

		if (Integer.toHexString(data & 0xff).length() == 1) {
			hexStr += "0";
		}
		hexStr += Integer.toHexString(data & 0xff).toUpperCase();

		return hexStr;

	}

	public static byte[] bcd_to_ascii(byte[] bcd, int len) {
		byte[] asc = new byte[len * 2 + 1];
		int tmp = 0;
		for (int i = 0; i < len; i++) {
			System.arraycopy(HexToStr(bcd[i]).getBytes(), 0, asc, tmp, 2);
			tmp += 2;
		}
		return asc;
	}

	public static byte[] bcd_to_ascii_tmp(byte[] bcd, int len) {
		byte uch_buf_bcd;
		byte[] ustr_ascii = new byte[len * 2 + 1];
		int i;
		int offset = 0;
		for (i = 0; i < len; i++) {
			uch_buf_bcd = bcd[i];
			ustr_ascii[offset++] = (byte) (((uch_buf_bcd >> 4) > 0x09) ? ((uch_buf_bcd >> 4) - 0x0A + 'A')
					: ((uch_buf_bcd >> 4) + '0'));
			ustr_ascii[offset++] = (byte) (((uch_buf_bcd & 0x0f) > 0x09) ? ((uch_buf_bcd & 0x0f) - 0x0A + 'A')
					: ((uch_buf_bcd & 0x0f) + '0'));
		}
		return ustr_ascii;
	}

	@SuppressLint("SimpleDateFormat")
	public static String gettime(String pattern) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern,
				Locale.getDefault());
		String date = sDateFormat.format(new Date());

		return date;
	}

	public static byte[] gettimems() {
		byte[] date = new byte[9];
		Calendar CD = Calendar.getInstance();
		int YY = CD.get(Calendar.YEAR);
		int MM = CD.get(Calendar.MONTH) + 1;
		int DD = CD.get(Calendar.DATE);
		int HH = CD.get(Calendar.HOUR);
		int NN = CD.get(Calendar.MINUTE);
		int SS = CD.get(Calendar.SECOND);
		int MI = CD.get(Calendar.MILLISECOND);
		date[0] = (byte) (YY & 0xFF);
		date[1] = (byte) (MM & 0xFF);
		date[2] = (byte) (DD & 0xFF);
		date[3] = (byte) (HH & 0xFF);
		date[4] = (byte) (NN & 0xFF);
		date[5] = (byte) (SS & 0xFF);
		date[6] = (byte) (MI & 0xFF);
		date[7] = (byte) ((MM >> 8) & 0xFF);
		// SystemManager.LOGI(TAG, "--------------gettimems---------------");
		// debugHex(TAG, date, 8);
		// SystemManager.LOGI(TAG, "--------------------------------------");
		return date;
	}

	private static long getTime(String user_time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d;
		long l = 0;
		try {
			d = sdf.parse(user_time);
			l = d.getTime();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return l;
	}

	public static void setTime(String time, Context context) {
		String regex = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";

		if (time.matches(regex)) {
			Intent intent = new Intent();
			intent.setAction("set_system_time");
			long t = getTime(time);
			intent.putExtra("time", t);
			context.sendBroadcast(intent);
		}
	}

	public static String GetInetAddress(String host) {
		String IPAddress = "";
		InetAddress ReturnStr1 = null;
		try {
			ReturnStr1 = java.net.InetAddress.getByName(host);
			IPAddress = ReturnStr1.getHostAddress();
			System.out.println(IPAddress);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return IPAddress;
		}
		return IPAddress;
	}

	public static String getrandom(boolean numberFlag, int length) {

		String retStr = "";
		String strTable = numberFlag ? "1234567890"
				: "1234567890abcdefghijkmnpqrstuvwxyz";
		int len = strTable.length();
		boolean bDone = true;
		do {
			retStr = "";
			int count = 0;
			for (int i = 0; i < length; i++) {
				double dblR = Math.random() * len;
				int intR = (int) Math.floor(dblR);
				char c = strTable.charAt(intR);
				if (('0' <= c) && (c <= '9')) {
					count++;
				}
				retStr += strTable.charAt(intR);
			}
			if (count >= 2) {
				bDone = false;
			}
		} while (bDone);
		return retStr;
	}

	public static void debugHex(String tag, byte[] data, int datalen) {
		String hexStr = "";
		for (int i = 0; i < datalen; i++) {

			if ((i % 16) == 0) {
				Log.d(tag, hexStr);
				hexStr = "";
			}

			hexStr += Integer.toHexString(data[i] & 0xff) + " ";
		}

		Log.d(tag, hexStr);
	}

	public static String getFileMember(String strFilePath, String Id) {
		Log.i("TestFile", "path:" + strFilePath + "  tmId:" + Id);
		if (strFilePath == null) {
			return null;
		}
		if (Id == null) {
			return null;
		}
		String path = strFilePath;

		File file = new File(path);
		// 閸掋倖鏌囬弰顖氭儊娑撶儤鏋冩禒璺恒仚
		if (file.isDirectory()) {
			Log.d("TestFile", "The File doesn't not exist.");
		} else {
			try {
				InputStream instream = new FileInputStream(file);
				if (instream != null) {
					InputStreamReader inputreader = new InputStreamReader(
							instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;

					while ((line = buffreader.readLine()) != null) {
						// content += line + "\n";
						Log.d("TestFile", "line:" + line);
						if (line.indexOf(Id) >= 0) {

							String[] member = line.split("=");

							if (member.length >= 2) {
								Log.d("TestFile", "find tmid byte.length:"
										+ member.length);
								Log.d("TestFile", "bytes[0]:" + member[0]);
								Log.d("TestFile", "bytes[1]:" + member[1]);
								return member[1];
							}
						}
					}
					instream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static byte[] hexStr2Str(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

		}
		return d;
	}

	public static boolean checkByte(byte[] src, byte[] dst, int len) {
		int i = 0;
		for (i = 0; i < len; i++) {
			if (src[i] != dst[i]) {
				return false;
			}
		}
		return true;
	}

	public static int getPicture(String filename, byte[] readbuf)
			throws IOException {

		RandomAccessFile conf = new RandomAccessFile(filename, "rw");
		int readlen = conf.read(readbuf);
		Log.i(TAG, "readlen = " + readlen);
		conf.close();
		return readlen;
	}

	public static void wirte(String filename, String data) throws IOException {

		RandomAccessFile conf = new RandomAccessFile(filename, "rw");
		conf.write(data.getBytes());
		conf.close();
	}

	public static String countMask(String ip) {
		String mask = new String();
		String[] tmp;
		if (ip == null || ip.length() == 0) {
			return null;
		}
		Log.i(TAG, "ip = " + ip);
		tmp = ip.split("\\.");
		if (tmp != null) {
			mask = tmp[0] + "." + tmp[1] + "." + tmp[2] + "." + "1";

			return mask;
		} else {
			return null;
		}
	}

	public static long getLong(byte[] buf) {
		if (buf == null) {
			throw new IllegalArgumentException("byte array is null!");
		}

		if (buf.length > 8) {
			throw new IllegalArgumentException("byte array size > 8 !");
		}

		long r = 0;

		for (int i = 0; i < buf.length; i++) {
			r <<= 8;
			r |= (buf[i] & 0x00000000000000ff);
		}
		return r;
	}

	public static long getLongContrary(byte[] buf) {
		if (buf == null) {
			throw new IllegalArgumentException("byte array is null!");
		}

		if (buf.length > 8) {
			throw new IllegalArgumentException("byte array size > 8 !");
		}

		long r = 0;

		for (int i = buf.length - 1; i >= 0; i--) {
			r <<= 8;
			r |= (buf[i] & 0x00000000000000ff);
		}
		// for (int i = 0; i < buf.length; i++) {
		return r;
	}

	public static String getMacAddress() {
		String result = "";
		String Mac = "";
		result = callCmd("busybox ifconfig", "HWaddr");

		if (result == null) {
			return null;
		}
		if (result.length() > 0 && result.contains("HWaddr")) {
			Mac = result.substring(result.indexOf("HWaddr") + 6,
					result.length() - 1);
			if (Mac.length() > 1) {
				result = Mac.toLowerCase();
			}
		}
		return result.trim();
	}

	private static String callCmd(String cmd, String filter) {
		String result = "";
		String line = "";
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			InputStreamReader is = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(is);
			while ((line = br.readLine()) != null
					&& line.contains(filter) == false) {

			}
			result = line;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/***
	 * 转成格林威治时间
	 * 
	 * @param LocalDate
	 */
	private Date LocalToGTM(String LocalDate) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date result_date;
		long result_time = 0;
		try {
			result_date = format.parse(LocalDate);
			result_time = result_date.getTime();
			String s = format.format(result_time - 8 * 60 * 60 * 1000);
			return format.parse(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
