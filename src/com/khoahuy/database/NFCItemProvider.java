package com.khoahuy.database;

import java.util.Date;

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
	
	public int countWaitingItemOfToday()
	{
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
	
	public int countUsedItemOfToday()
	{
		try {
			Long timestamp = DateUtils.getTimestampBeginOfDay();
			String[] projection = { COLUMN_NFCID };
			String selection = COLUMN_CHECK_OUT+ " >= \"" + timestamp + "\"";
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

}
