package com.khoahuy.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static String getDate(Long timeStamp) {

		try {
			DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy - HH:mm:ss");
			Date netDate = (new Date(timeStamp));
			return sdf.format(netDate);
		} catch (Exception ex) {
			return "Parse time Error";
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

			return cal.getTime().getTime();
		} catch (Exception ex) {
			throw new Exception("Get time ago error");
		}
	}

	public static long getTimestampOfDate(Date date) throws Exception {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.HOUR_OF_DAY, 0);

			return cal.getTime().getTime() /1000;
		} catch (Exception ex) {
			throw new Exception("Get time ago error");
		}

	}

}
