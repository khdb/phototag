package com.khoahuy.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static long timestampOfDay = 86400L;
	public static long timestampOfWeek = 86400L * 7;

	public static String getDate(Long timeStamp) {

		try {
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
	
	public static int getDateOfMonth(Date date) throws Exception{
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal.get(Calendar.DAY_OF_MONTH);
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
			cal.set(Calendar.MILLISECOND, 0);
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
			cal.set(year, month + 1, 1, 0, 0, 0);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			return cal.getTime().getTime() / 1000;
		} catch (Exception ex) {
			throw new Exception("Get time ago error");
		}
	}
	
	public static String convertTimestampToHourText(long timestamp)
	{
		int hour = (int)timestamp/3600;
		timestamp = timestamp - hour*3600;
		int minute = (int)timestamp/60;
		return  hour + "h" + minute + "m";
	}

}
