package com.khoahuy.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.khoahuy.database.provider.MyContentProvider;
import com.khoahuy.phototag.model.NFCItem;

public class NFCItemProvider {
	
	private ContentResolver myCR;
	
	public final String COLUMN_ID = "_id";
	public final String COLUMN_IMAGE = "image";
	public final String COLUMN_CHECK_IN = "checkin";
	public final String COLUMN_CHECK_OUT = "checkout";

	public NFCItemProvider(ContentResolver cr)
	{
		myCR = cr;
	}
	
	public void addProduct(NFCItem item) {

		ContentValues values = new ContentValues();
		values.put(COLUMN_ID, item.getId());
		values.put(COLUMN_IMAGE, item.getImage());
		values.put(COLUMN_CHECK_IN, item.getCheckIn().getTime());
		values.put(COLUMN_CHECK_OUT, item.getCheckOut().getTime());
		myCR.insert(MyContentProvider.CONTENT_URI, values);
	}

	public NFCItem findProduct(String id) {
		String[] projection = { COLUMN_ID, COLUMN_IMAGE, COLUMN_CHECK_IN,
				COLUMN_CHECK_OUT };

		String selection = COLUMN_ID + " = \"" + id + "\"";

		Cursor cursor = myCR.query(MyContentProvider.CONTENT_URI, projection,
				selection, null, null);

		NFCItem item = new NFCItem();

		if (cursor.moveToFirst()) {
			cursor.moveToFirst();
			item.setId(cursor.getString(0));
			item.setImage(cursor.getString(1));
			//item.setCheckIn(cursor.getString(2));
			//item.setCheckOut(cursor.getString(2));

			cursor.close();
		} else {
			item = null;
		}
		return item;
	}

	public boolean deleteItem(String id) {

		boolean result = false;

		String selection = COLUMN_ID + " = \"" + id + "\"";

		int rowsDeleted = myCR.delete(MyContentProvider.CONTENT_URI, selection,
				null);

		if (rowsDeleted > 0)
			result = true;

		return result;
	}

}
