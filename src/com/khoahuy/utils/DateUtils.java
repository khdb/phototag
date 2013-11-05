package com.khoahuy.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static final long TIMESTAMP_OF_DAY = 86400L;
	public static final long TIMESTAMP_OF_WEEK = 86400L * 7;
	public static final int SECOND = 1;
	public static final int MINUTE = 60 * SECOND;
	public static final int HOUR = 60 * MINUTE;
	public static final int DAY = 24 * HOUR;
	public static final int MONTH = 30 * DAY;

	public static String getDate(Long timeStamp) {

		try {
			timeStamp = timeStamp * 1000;
			DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy - HH:mm:ss");
			Date netDate = (new Date(timeStamp));
			return sdf.format(netDate);
		} catch (Exception ex) {
			return "Parse time Error";
		}
	}

	public static long getCurrentTimestamp() {
		try {
			Date date = new Date();
			return date.getTime() / 1000;
		} catch (Exception ex) {
			return 0L;
		}

	}

	public static int getDateOfMonth(Date date) throws Exception {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal.get(Calendar.DAY_OF_MONTH);
		} catch (Exception ex) {
			throw new Exception("Get time ago error");
		}
	}

	public static String[] getDateName7DayAgo(Date date) throws Exception {
		try {
			String[] result = new String[7];
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			result[0] = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
			for (int i = 1; i < result.length; i++) {
				cal.add(Calendar.DATE, -1);
				result[i] = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
			}
			return result;
		} catch (Exception ex) {
			throw new Exception("Get time ago error");
		}
	}

	public static long getTimestampBeginOfDay() throws Exception {
		try {
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.HOUR_OF_DAY, 0);

			return cal.getTime().getTime() / 1000;
		} catch (Exception ex) {
			throw new Exception("Get time ago error");
		}
	}

	public static long getTimestampBeginOfDate(Date date) throws Exception {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.HOUR_OF_DAY, 0);

			return cal.getTime().getTime() / 1000;
		} catch (Exception ex) {
			throw new Exception("Get time ago error");
		}

	}

	public static long getTimestampEndOfDate(Date date) throws Exception {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			return cal.getTime().getTime() / 1000;
		} catch (Exception ex) {
			throw new Exception("Get time ago error");
		}

	}

	public static long getTimestampFirstDateOfMonth(int month, int year)
			throws Exception {
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(year, month, 1, 0, 0, 0);
			return cal.getTime().getTime() / 1000;
		} catch (Exception ex) {
			throw new Exception("Get time ago error");
		}
	}

	public static long getTimestampEndDateOfMonth(int month, int year)
			throws Exception {
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(year, month + 1, 1, 23, 59, 59);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			return cal.getTime().getTime() / 1000;
		} catch (Exception ex) {
			throw new Exception("Get time ago error");
		}
	}

	public static long getTimestamp(int year, int month, int dateOfMonth,
			int hourOfDate, int minute, int second) throws Exception {
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(year, month - 1, dateOfMonth, hourOfDate, minute, second);
			return cal.getTime().getTime() / 1000;
		} catch (Exception ex) {
			throw new Exception("Get time ago error");
		}
	}

	public static String convertTimestampToHourText(long timestamp) {
		int hour = (int) timestamp / 3600;
		timestamp = timestamp - hour * 3600;
		int minute = (int) timestamp / 60;
		return hour + "h" + minute + "m";
	}

	public static String getRelativeTime(long timestamp) {
		long delta = getCurrentTimestamp() - timestamp;
		int day = (int) delta / (3600 * 24);
		int hour = ((int) delta - (day * 3600 * 24)) / 3600;
		int minute = ((int) delta - (day * 3600 * 24) - hour * 3600) / 60;
		int second = ((int) delta - (day * 3600 * 24) - hour * 3600 - minute * 60);

		if (delta < 1) {
			return "tức thì";
		}
		if (delta < 1 * MINUTE) {
			return second == 1 ? "một giây trước" : second + " giây trước";
		}
		if (delta < 2 * MINUTE) {
			return "một phút trước";
		}
		if (delta < 55 * MINUTE) {
			return minute + " phút trước";
		}
		if (delta < 60 * MINUTE) {
			return "gần một giờ trước";
		}
		if (delta < 75 * MINUTE) {
			return "hơn một giờ trước";
		}
		if (delta < 24 * HOUR) {
			return hour + " giờ trước";
		}
		if (delta < 48 * HOUR) {
			return "hôm qua";
		}
		if (delta < 30 * DAY) {
			return day + " ngày trước";
		}
		if (delta < 12 * MONTH) {
			int months = (int) Math.floor((double) day / 30);
			return months <= 1 ? "một tháng trước" : months + " tháng trước";
		} else {
			int years = (int) Math.floor((double) day / 365);
			return years <= 1 ? "một năm trước" : years + " năm trước";
		}
	}
}
