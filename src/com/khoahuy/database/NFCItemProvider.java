package com.khoahuy.database;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.khoahuy.utils.DateUtils;
import com.khoahuy.utils.StatisticUtils;

import com.khoahuy.database.provider.MyContentProvider;
import com.khoahuy.phototag.model.NFCItem;

public class NFCItemProvider {

	private ContentResolver myCR;

	public static final String COLUMN_ID = "id";
	public static final String COLUMN_IMAGE = "image";
	public static final String COLUMN_CHECK_IN = "checkin";
	public static final String COLUMN_CHECK_OUT = "checkout";

	public static final String COLUMN_NFCID = "nfcid";

	public NFCItemProvider(ContentResolver cr) {
		myCR = cr;
	}

	private String getStringOfNumber(String number) {
		return String.valueOf(Integer.parseInt(number));
	}

	public void addWaitingItem(NFCItem item) {

		ContentValues values = new ContentValues();
		values.put(COLUMN_NFCID, item.getNfcid());
		values.put(COLUMN_IMAGE, item.getImage());
		if (item.getCheckIn() != null)
			values.put(COLUMN_CHECK_IN, item.getCheckIn());
		else
			values.put(COLUMN_CHECK_IN, DateUtils.getCurrentTimestamp());

		myCR.insert(MyContentProvider.WAITING_CONTENT_URI, values);
	}

	public NFCItem findWaitingItem(String nfcid) {
		String[] projection = { COLUMN_NFCID, COLUMN_IMAGE, COLUMN_CHECK_IN };

		String selection = COLUMN_NFCID + " = \"" + nfcid + "\"";

		Cursor cursor = myCR.query(MyContentProvider.WAITING_CONTENT_URI,
				projection, selection, null, null);

		NFCItem item = new NFCItem();

		if (cursor.moveToFirst()) {
			cursor.moveToFirst();
			item.setNfcid(cursor.getString(0));
			item.setImage(cursor.getString(1));
			item.setCheckIn(cursor.getLong(2));
			cursor.close();
		} else {
			item = null;
		}
		return item;
	}

	public NFCItem getNewestWaitingItem() {
		String[] projection = { COLUMN_NFCID, COLUMN_IMAGE, COLUMN_CHECK_IN };

		String sort = COLUMN_CHECK_IN + " DESC";

		Cursor cursor = myCR.query(MyContentProvider.WAITING_CONTENT_URI,
				projection, null, null, sort);

		NFCItem item = new NFCItem();

		if (cursor.moveToFirst()) {
			cursor.moveToFirst();
			item.setNfcid(cursor.getString(0));
			item.setImage(cursor.getString(1));
			item.setCheckIn(cursor.getLong(2));
			cursor.close();
		} else {
			item = null;
		}
		return item;
	}

	public NFCItem getNewestUsedItem() {
		String[] projection = { COLUMN_NFCID, COLUMN_CHECK_IN, COLUMN_CHECK_OUT };

		String sort = COLUMN_CHECK_OUT + " DESC";

		Cursor cursor = myCR.query(MyContentProvider.USED_CONTENT_URI,
				projection, null, null, sort);

		NFCItem item = new NFCItem();

		if (cursor.moveToFirst()) {
			cursor.moveToFirst();
			item.setNfcid(cursor.getString(0));
			item.setImage(cursor.getString(1));
			item.setCheckIn(cursor.getLong(2));
			cursor.close();
		} else {
			item = null;
		}
		return item;
	}

	public NFCItem getOldestWaitingItemOfToday() {
		try {
			Long timestamp = DateUtils.getTimestampBeginOfDay();
			String[] projection = { COLUMN_NFCID, COLUMN_IMAGE, COLUMN_CHECK_IN };
			String selection = COLUMN_CHECK_IN + " >= \"" + timestamp + "\"";
			String sort = COLUMN_CHECK_IN + " ASC";

			Cursor cursor = myCR.query(MyContentProvider.WAITING_CONTENT_URI,
					projection, selection, null, sort);

			NFCItem item = new NFCItem();

			if (cursor.moveToFirst()) {
				cursor.moveToFirst();
				item.setNfcid(cursor.getString(0));
				item.setImage(cursor.getString(1));
				item.setCheckIn(cursor.getLong(2));
				cursor.close();
			} else {
				item = null;
			}
			return item;
		} catch (Exception ex) {
			Log.e("Huy", ex.toString());
			return null;
		}

	}

	public int countWaitingItemOfToday() {
		try {
			Long timestamp = DateUtils.getTimestampBeginOfDay();
			String[] projection = { COLUMN_NFCID };
			String selection = COLUMN_CHECK_IN + " >= \"" + timestamp + "\"";

			Cursor cursor = myCR.query(MyContentProvider.WAITING_CONTENT_URI,
					projection, selection, null, null);
			int result = cursor.getCount();
			cursor.close();
			return result;
		} catch (Exception ex) {
			Log.e("Huy", ex.toString());
			return 0;
		}

	}

	public int countUsedItemOfToday() {
		try {
			Long timestamp = DateUtils.getTimestampBeginOfDay();
			String[] projection = { COLUMN_NFCID };
			String selection = COLUMN_CHECK_OUT + " >= \"" + timestamp + "\"";
			Cursor cursor = myCR.query(MyContentProvider.USED_CONTENT_URI,
					projection, selection, null, null);
			int result = cursor.getCount();
			cursor.close();
			return result;
		} catch (Exception ex) {
			Log.e("Huy", ex.toString());
			return 0;
		}

	}

	public int countWaitingItem() {
		String[] projection = { COLUMN_NFCID };
		Cursor cursor = myCR.query(MyContentProvider.WAITING_CONTENT_URI,
				projection, null, null, null);

		int result = cursor.getCount();
		cursor.close();
		return result;
	}

	public void addUsedItem(NFCItem item) {

		ContentValues values = new ContentValues();
		values.put(COLUMN_NFCID, item.getNfcid());
		values.put(COLUMN_CHECK_IN, item.getCheckIn());
		if (item.getCheckOut() != null)
			values.put(COLUMN_CHECK_OUT, item.getCheckOut());
		else
			values.put(COLUMN_CHECK_OUT, DateUtils.getCurrentTimestamp());

		myCR.insert(MyContentProvider.USED_CONTENT_URI, values);
	}

	public boolean deleteWaitingItem(String nfcid) {

		boolean result = false;

		String selection = COLUMN_NFCID + " = \"" + nfcid + "\"";

		int rowsDeleted = myCR.delete(MyContentProvider.WAITING_CONTENT_URI,
				selection, null);

		if (rowsDeleted > 0)
			result = true;

		return result;
	}

	// Bar graphic Statistic region

	public Map<String, Integer> getWaitingItemOfDayStatistic(Date date) {
		/*
		 * SELECT strftime('%H', checkin, 'unixepoch', 'localtime'), count(*) FROM
		 * waiting_items WHERE(checkin >= "1382979600000" AND checkin <=
		 * "1382979686400") GROUP BY (strftime('%H', checkin,'unixepoch', 'localtime'));
		 */
		try {
			Long from = DateUtils.getTimestampBeginOfDate(date);
			Long to = from + DateUtils.TIMESTAMP_OF_DAY;
			String[] projection = {
					"strftime('%H', " + COLUMN_CHECK_IN + ", 'unixepoch', 'localtime')",
					"count(*)" };
			String selection = COLUMN_CHECK_IN + " >= \"" + from + "\" AND "
					+ COLUMN_CHECK_IN + " <= \"" + to + "\""
					+ ") GROUP BY (strftime('%H', " + COLUMN_CHECK_IN
					+ ",'unixepoch', 'localtime')";
			String sort = COLUMN_CHECK_IN + " ASC";
			Cursor cursor = myCR.query(MyContentProvider.WAITING_CONTENT_URI,
					projection, selection, null, sort);

			Map<String, Integer> result = new LinkedHashMap<String, Integer>();
			if (cursor.moveToFirst())
				do {
					String hour = String.valueOf(cursor.getInt(0));
					int count = cursor.getInt(1);
					result.put(hour, count);
				} while (cursor.moveToNext());
			cursor.close();
			result = StatisticUtils.normalizationDateData(result);
			return result;
		} catch (Exception ex) {
			Log.e("Huy", ex.toString());
			return null;
		}
	}

	public Map<String, Integer> getWaitingItemOfWeekStatistic(Date dateEndOfWeek) {
		/*
		 * SELECT strftime('%d', checkin, 'unixepoch', 'localtime'), count(*) FROM
		 * waiting_items WHERE(checkin >= "1382979600000" AND checkin <=
		 * "1382979686400") GROUP BY (strftime('%d', checkin,'unixepoch', 'localtime'));
		 */
		try {
			Long to = DateUtils.getTimestampEndOfDate(dateEndOfWeek);
			Long from = to - DateUtils.TIMESTAMP_OF_WEEK + 1;
			Log.i("Statistic", "Get Waiting Item Of Week " + dateEndOfWeek
					+ ": From: " + from + " - To: " + to);
			String[] projection = {
					"strftime('%d', " + COLUMN_CHECK_IN + ", 'unixepoch', 'localtime')",
					"count(*)" };
			String selection = COLUMN_CHECK_IN + " >= \"" + from + "\" AND "
					+ COLUMN_CHECK_IN + " <= \"" + to + "\""
					+ ") GROUP BY (strftime('%d', " + COLUMN_CHECK_IN
					+ ",'unixepoch', 'localtime')";
			String sort = COLUMN_CHECK_IN + " ASC";
			Cursor cursor = myCR.query(MyContentProvider.WAITING_CONTENT_URI,
					projection, selection, null, sort);

			Map<String, Integer> result = new LinkedHashMap<String, Integer>();
			if (cursor.moveToFirst())
				do {
					String date = getStringOfNumber(cursor.getString(0));
					int count = cursor.getInt(1);
					result.put(date, count);
				} while (cursor.moveToNext());
			cursor.close();
			result = StatisticUtils
					.normalizationWeekData(result, dateEndOfWeek);
			return result;
		} catch (Exception ex) {
			Log.e("Huy", ex.toString());
			return null;
		}
	}

	public Map<String, Integer> getWaitingItemOfMonthStatistic(int monthIndex,
			int year) {
		/*
		 * SELECT strftime('%d', checkin, 'unixepoch', 'localtime'), count(*) FROM
		 * waiting_items WHERE(checkin >= "1382979600000" AND checkin <=
		 * "1382979686400") GROUP BY (strftime('%d', checkin,'unixepoch', 'localtime'));
		 */
		try {
			Long from = DateUtils.getTimestampFirstDateOfMonth(monthIndex, year);
			Long to = DateUtils.getTimestampEndDateOfMonth(monthIndex, year);
			Log.i("Statistic", "Get Waiting Item Of MonthIndex " + monthIndex +
					 ": From: " + from + " - To: " + to);
			String[] projection = {
					"strftime('%d', " + COLUMN_CHECK_IN + ", 'unixepoch', 'localtime')",
					"count(*)" };
			String selection = COLUMN_CHECK_IN + " >= \"" + from + "\" AND "
					+ COLUMN_CHECK_IN + " <= \"" + to + "\""
					+ ") GROUP BY (strftime('%d', " + COLUMN_CHECK_IN
					+ ",'unixepoch', 'localtime')";
			String sort = COLUMN_CHECK_IN + " ASC";
			Cursor cursor = myCR.query(MyContentProvider.WAITING_CONTENT_URI,
					projection, selection, null, sort);

			Map<String, Integer> result = new LinkedHashMap<String, Integer>();
			int[] weekCount = new int[5];
			if (cursor.moveToFirst())
				do {
					int date = cursor.getInt(0);
					if (date <= 7)
						weekCount[0] += cursor.getInt(1);
					else if (date <= 14)
						weekCount[1] += cursor.getInt(1);
					else if (date <= 21)
						weekCount[2] += cursor.getInt(1);
					else if (date <= 28)
						weekCount[3] += cursor.getInt(1);
					else
						weekCount[4] += cursor.getInt(1);
				} while (cursor.moveToNext());
			result.put("1~7", weekCount[0]);
			result.put("8~14", weekCount[1]);
			result.put("15~21", weekCount[2]);
			result.put("22~28", weekCount[3]);
			result.put("28~end", weekCount[4]);
			cursor.close();
			return result;
		} catch (Exception ex) {
			Log.e("Huy", ex.toString());
			return null;
		}
	}

	public Map<String, Integer> getWaitingItemOfYearStatistic(int year) {
		/*
		 * SELECT strftime('%m', checkin, 'unixepoch', 'localtime'), count(*) FROM
		 * waiting_items WHERE(checkin >= "1382979600000" AND checkin <=
		 * "1382979686400") GROUP BY (strftime('%m', checkin,'unixepoch', 'localtime'));
		 */
		try {
			Long from = DateUtils.getTimestamp(year, 1, 1, 0, 0, 0);
			Long to = DateUtils.getTimestamp(year, 12, 31, 23, 59, 59);
			// Log.i("Statistic", "Get Waiting Item Of Year " + year +
			// ": From: " + from + " - To: " + to);

			String[] projection = {
					"strftime('%m', " + COLUMN_CHECK_IN + ", 'unixepoch', 'localtime')",
					"count(*)" };
			String selection = COLUMN_CHECK_IN + " >= \"" + from + "\" AND "
					+ COLUMN_CHECK_IN + " <= \"" + to + "\""
					+ ") GROUP BY (strftime('%m', " + COLUMN_CHECK_IN
					+ ",'unixepoch', 'localtime')";
			String sort = COLUMN_CHECK_IN + " ASC";
			Cursor cursor = myCR.query(MyContentProvider.WAITING_CONTENT_URI,
					projection, selection, null, sort);

			Map<String, Integer> result = new LinkedHashMap<String, Integer>();
			for (int i = 1; i <= 12; i++) {
				result.put(String.valueOf(i), 0);
			}
			if (cursor.moveToFirst())
				do {
					String month = getStringOfNumber(cursor.getString(0));
					int count = cursor.getInt(1);
					result.put(month, count);
				} while (cursor.moveToNext());
			cursor.close();

			return result;
		} catch (Exception ex) {
			Log.e("Huy", ex.toString());
			return null;
		}
	}

	// Pie graphic Statistic region

	public Map<String, Integer> getUsedItemStatistic(long from, long to,
			int[] thresholdArray) {
		/*
		 * SELECT strftime('%d', checkout, 'unixepoch', 'localtime'), count(*) FROM
		 * used_items WHERE(checkout >= "1380405237" AND checkout <=
		 * "1482979686400") GROUP BY (strftime('%d', checkout,'unixepoch', 'localtime'));
		 */
		try {
			String[] projection = { "(checkout - checkin) as time" };
			String selection = COLUMN_CHECK_OUT + " >= \"" + from + "\" AND "
					+ COLUMN_CHECK_OUT + " <= \"" + to + "\"";
			Cursor cursor = myCR.query(MyContentProvider.USED_CONTENT_URI,
					projection, selection, null, null);

			thresholdArray = StatisticUtils.convertToTimestamp(thresholdArray);

			Map<String, Integer> result = new LinkedHashMap<String, Integer>();
			int[] qualityArray = new int[thresholdArray.length + 1];
			if (cursor.moveToFirst())
				do {
					int timeQuality = cursor.getInt(0);
					for (int i = 0; i < thresholdArray.length; i++) {
						if (timeQuality < thresholdArray[i]) {
							qualityArray[i]++;
							break;
						}
						if (i == thresholdArray.length - 1)
							qualityArray[i + 1]++;
					}
				} while (cursor.moveToNext());
			cursor.close();
			result = StatisticUtils.convert2ArrayIntoMap(thresholdArray,
					qualityArray);
			return result;
		} catch (Exception ex) {
			Log.e("Huy", ex.toString());
			return null;
		}
	}
}
