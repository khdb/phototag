package com.khoahuy.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.khoahuy.utils.DateUtils;

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

	public void addWaitingItem(NFCItem item) {

		ContentValues values = new ContentValues();
		values.put(COLUMN_NFCID, item.getNfcid());
		values.put(COLUMN_IMAGE, item.getImage());
		if (item.getCheckIn() != null)
			values.put(COLUMN_CHECK_IN, item.getCheckIn());
		else
			values.put(COLUMN_CHECK_IN, ((new Date()).getTime()));

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
			values.put(COLUMN_CHECK_OUT, ((new Date()).getTime()));

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

	// Statistic region

	public Map<String, Integer> getWaitingItemOfDate(Date date) {
		/*
		 * SELECT strftime('%H', checkin, 'unixepoch'), count(*) FROM
		 * waiting_items WHERE(checkin >= "1382979600000" AND checkin <=
		 * "1382979686400") GROUP BY (strftime('%H', checkin,'unixepoch'));
		 */
		try {
			Long from = DateUtils.getTimestampBeginOfDate(date);
			Long to = from + DateUtils.timestampOfDay;
			String[] projection = {
					"strftime('%H', " + COLUMN_CHECK_IN + ", 'unixepoch')",
					"count(*)" };
			String selection = COLUMN_CHECK_IN + " >= \"" + from + "\" AND "
					+ COLUMN_CHECK_IN + " <= \"" + to + "\""
					+ ") GROUP BY (strftime('%H', " + COLUMN_CHECK_IN
					+ ",'unixepoch')";
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
			return result;
		} catch (Exception ex) {
			Log.e("Huy", ex.toString());
			return null;
		}
	}

	public Map<String, Integer> getWaitingItemOfWeek(Date dateEndOfWeek) {
		/*
		 * SELECT strftime('%d', checkin, 'unixepoch'), count(*) FROM
		 * waiting_items WHERE(checkin >= "1382979600000" AND checkin <=
		 * "1382979686400") GROUP BY (strftime('%d', checkin,'unixepoch'));
		 */
		try {
			Long to = DateUtils.getTimestampEndOfDate(dateEndOfWeek);
			Long from = to - DateUtils.timestampOfWeek;
			String[] projection = {
					"strftime('%d', " + COLUMN_CHECK_IN + ", 'unixepoch')",
					"count(*)" };
			String selection = COLUMN_CHECK_IN + " >= \"" + from + "\" AND "
					+ COLUMN_CHECK_IN + " <= \"" + to + "\""
					+ ") GROUP BY (strftime('%d', " + COLUMN_CHECK_IN
					+ ",'unixepoch')";
			String sort = COLUMN_CHECK_IN + " ASC";
			Cursor cursor = myCR.query(MyContentProvider.WAITING_CONTENT_URI,
					projection, selection, null, sort);

			Map<String, Integer> result = new LinkedHashMap<String, Integer>();
			if (cursor.moveToFirst())
				do {
					String date = String.valueOf(cursor.getInt(0));
					int count = cursor.getInt(1);
					result.put(date, count);
				} while (cursor.moveToNext());
			cursor.close();
			return result;
		} catch (Exception ex) {
			Log.e("Huy", ex.toString());
			return null;
		}
	}

	public Map<String, Integer> getWaitingItemOfMonth(int month, int year) {
		/*
		 * SELECT strftime('%d', checkin, 'unixepoch'), count(*) FROM
		 * waiting_items WHERE(checkin >= "1382979600000" AND checkin <=
		 * "1382979686400") GROUP BY (strftime('%d', checkin,'unixepoch'));
		 */
		try {
			Long from = DateUtils.getTimestampFirstDateOfMonth(month, year);
			Long to = DateUtils.getTimestampEndDateOfMonth(month, year);
			String[] projection = {
					"strftime('%d', " + COLUMN_CHECK_IN + ", 'unixepoch')",
					"count(*)" };
			String selection = COLUMN_CHECK_IN + " >= \"" + from + "\" AND "
					+ COLUMN_CHECK_IN + " <= \"" + to + "\""
					+ ") GROUP BY (strftime('%d', " + COLUMN_CHECK_IN
					+ ",'unixepoch')";
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

	public Map<String, Integer> getWaitingItemOfYear(int year) {
		/*
		 * SELECT strftime('%m', checkin, 'unixepoch'), count(*) FROM
		 * waiting_items WHERE(checkin >= "1382979600000" AND checkin <=
		 * "1382979686400") GROUP BY (strftime('%m', checkin,'unixepoch'));
		 */
		try {
			Long from = DateUtils.getTimestampFirstDateOfMonth(0, year);
			Long to = DateUtils.getTimestampEndDateOfMonth(12, year);
			String[] projection = {
					"strftime('%m', " + COLUMN_CHECK_IN + ", 'unixepoch')",
					"count(*)" };
			String selection = COLUMN_CHECK_IN + " >= \"" + from + "\" AND "
					+ COLUMN_CHECK_IN + " <= \"" + to + "\""
					+ ") GROUP BY (strftime('%m', " + COLUMN_CHECK_IN
					+ ",'unixepoch')";
			String sort = COLUMN_CHECK_IN + " ASC";
			Cursor cursor = myCR.query(MyContentProvider.WAITING_CONTENT_URI,
					projection, selection, null, sort);
			
			Map<String, Integer> result = new LinkedHashMap<String, Integer>();
			for (int i = 1; i <= 12; i++)
			{
				result.put(String.valueOf(i), 0);
			}
			if (cursor.moveToFirst())
				do {
					String date = String.valueOf(cursor.getInt(0));
					int count = cursor.getInt(1);
					result.put(date, count);
				} while (cursor.moveToNext());
			cursor.close();
			
			return result;
		} catch (Exception ex) {
			Log.e("Huy", ex.toString());
			return null;
		}
	}
}
