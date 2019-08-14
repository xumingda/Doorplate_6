package com.yy.doorplate.tool;

import java.util.Calendar;

public class DateUtils {
	/**
	 * ��ȡ��ǰ���
	 * 
	 * @return
	 */
	public static int getYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	/**
	 * ��ȡ��ǰ�·�
	 * 
	 * @return
	 */
	public static int getMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	/**
	 * ��ȡ��ǰ�����Ǹ��µĵڼ���
	 * 
	 * @return
	 */
	public static int getCurrentDayOfMonth() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * ��ȡ��ǰ�����Ǹ��ܵĵڼ���
	 * 
	 * @return
	 */
	public static int getCurrentDayOfWeek() {
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * ��ȡ��ǰ�Ǽ���
	 */
	public static int getHour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);// ��ʮ��Сʱ��
	}

	/**
	 * ��ȡ��ǰ�Ǽ���
	 * 
	 * @return
	 */
	public static int getMinute() {
		return Calendar.getInstance().get(Calendar.MINUTE);
	}

	/**
	 * ��ȡ��ǰ��
	 * 
	 * @return
	 */
	public static int getSecond() {
		return Calendar.getInstance().get(Calendar.SECOND);
	}

	/**
	 * ���ݴ������ݺ��·ݣ���ȡ��ǰ�·ݵ������ֲ�
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int[][] getDayOfMonthFormat(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, 1);// ����ʱ��Ϊÿ�µĵ�һ��
		//����������ʽ����,6��7��
	    int days[][] = new int[6][7];
		// ���ø��µĵ�һ�����ܼ�
		int daysOfFirstWeek = calendar.get(Calendar.DAY_OF_WEEK);
		// ���ñ����ж�����
		int daysOfMonth = getDaysOfMonth(year, month);
		// �����ϸ����ж�����
		int daysOfLastMonth = getLastDaysOfMonth(year, month);
		int dayNum = 1;
		int nextDayNum = 1;
		// �����ڸ�ʽ�������
		for (int i = 0; i < days.length; i++) {
			for (int j = 0; j < days[i].length; j++) {
				if (i == 0 && j < daysOfFirstWeek - 1) {
					days[i][j] = daysOfLastMonth - daysOfFirstWeek + 2 + j;
				} else if (dayNum <= daysOfMonth) {
					days[i][j] = dayNum++;
				} else {
					days[i][j] = nextDayNum++;
				}
			}
		}
		return days;
	}

	/**
	 * ���ݴ������ݺ��·ݣ��ж���һ���ж�����
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getLastDaysOfMonth(int year, int month) {
		int lastDaysOfMonth = 0;
		if (month == 1) {
			lastDaysOfMonth = getDaysOfMonth(year - 1, 12);
		} else {
			lastDaysOfMonth = getDaysOfMonth(year, month - 1);
		}
		return lastDaysOfMonth;
	}

	/**
	 * ���ݴ������ݺ��·ݣ��жϵ�ǰ���ж�����
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getDaysOfMonth(int year, int month) {
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			return 31;
		case 2:
			if (isLeap(year)) {
				return 29;
			} else {
				return 28;
			}
		case 4:
		case 6:
		case 9:
		case 11:
			return 30;
		}
		return -1;
	}

	/**
	 * �ж��Ƿ�Ϊ����
	 * 
	 * @param year
	 * @return
	 */
	public static boolean isLeap(int year) {
		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
			return true;
		}
		return false;
	}
}
