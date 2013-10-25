package com.khoahuy.database.provider;

import com.khoahuy.database.MyDBHandler;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MyContentProvider extends ContentProvider {

	private MyDBHandler myDB;

	private static final String AUTHORITY = "com.khoahuy.database.provider.MyContentProvider";
	private static final String WAITING_ITEMS_TABLE = "waiting_items";
	private static final String USED_ITEMS_TABLE = "used_items";
	public static final Uri WAITING_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + WAITING_ITEMS_TABLE);
	public static final Uri USED_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + USED_ITEMS_TABLE);

	public static final int WAITING_ITEMS = 1;
	public static final int WAITING_ITEMS_ID = 2;
	public static final int USED_ITEMS = 3;
	public static final int USED_ITEMS_ID = 4;

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(AUTHORITY, WAITING_ITEMS_TABLE, WAITING_ITEMS);
		sURIMatcher.addURI(AUTHORITY, WAITING_ITEMS_TABLE + "/#",
				WAITING_ITEMS_ID);
		sURIMatcher.addURI(AUTHORITY, USED_ITEMS_TABLE, USED_ITEMS);
		sURIMatcher.addURI(AUTHORITY, USED_ITEMS_TABLE + "/#", USED_ITEMS_ID);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = myDB.getWritableDatabase();
		int rowsDeleted = 0;

		switch (uriType) {
		case WAITING_ITEMS:
			rowsDeleted = sqlDB.delete(MyDBHandler.WAITING_ITEMS_TABLE,
					selection, selectionArgs);
			break;

		case WAITING_ITEMS_ID:
			String nfcid = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(MyDBHandler.WAITING_ITEMS_TABLE,
						MyDBHandler.COLUMN_ID + "=" + nfcid, null);
			} else {
				rowsDeleted = sqlDB.delete(MyDBHandler.WAITING_ITEMS_TABLE,
						MyDBHandler.COLUMN_ID + "=" + nfcid + " and " + selection,
						selectionArgs);
			}
			break;
		case USED_ITEMS:
			rowsDeleted = sqlDB.delete(MyDBHandler.USED_ITEMS_TABLE, selection,
					selectionArgs);
			break;

		case USED_ITEMS_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(MyDBHandler.USED_ITEMS_TABLE,
						MyDBHandler.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(MyDBHandler.WAITING_ITEMS_TABLE,
						MyDBHandler.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
		/*
		 * switch (uriMatcher.match(uri)){ case ITEMS: return
		 * "vnd.android.cursor.dir/vnd.example.students";
		 * 
		 * case ITEMS_ID: return "vnd.android.cursor.item/vnd.example.students";
		 * default: throw new IllegalArgumentException("Unsupported URI: " +
		 * uri); }
		 */
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		int uriType = sURIMatcher.match(uri);
		Uri result;
		SQLiteDatabase sqlDB = myDB.getWritableDatabase();

		long id = 0;
		switch (uriType) {
		case WAITING_ITEMS:
			id = sqlDB.insert(MyDBHandler.WAITING_ITEMS_TABLE, null, values);
			result = Uri.parse(WAITING_ITEMS_TABLE + "/" + id); 
			break;
		case USED_ITEMS:
			id = sqlDB.insert(MyDBHandler.USED_ITEMS_TABLE, null, values);
			result = Uri.parse(USED_ITEMS_TABLE + "/" + id);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		myDB = new MyDBHandler(getContext(), null, null, 1);
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		

		int uriType = sURIMatcher.match(uri);

		switch (uriType) {
		case WAITING_ITEMS_ID:
			queryBuilder.setTables(MyDBHandler.WAITING_ITEMS_TABLE);
			queryBuilder.appendWhere(MyDBHandler.COLUMN_NFCID + "="
					+ uri.getLastPathSegment());
			break;
		case WAITING_ITEMS:
			queryBuilder.setTables(MyDBHandler.WAITING_ITEMS_TABLE);
			break;
		case USED_ITEMS_ID:
			queryBuilder.setTables(MyDBHandler.USED_ITEMS_TABLE);
			queryBuilder.appendWhere(MyDBHandler.COLUMN_ID + "="
					+ uri.getLastPathSegment());
			break;
		case USED_ITEMS:
			queryBuilder.setTables(MyDBHandler.USED_ITEMS_TABLE);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI");
		}

		Cursor cursor = queryBuilder.query(myDB.getReadableDatabase(),
				projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = myDB.getWritableDatabase();
		int rowsUpdated = 0;

		switch (uriType) {
		case WAITING_ITEMS:
			rowsUpdated = sqlDB.update(MyDBHandler.WAITING_ITEMS_TABLE, values,
					selection, selectionArgs);
			break;
		case WAITING_ITEMS_ID:
			String nfcid = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(MyDBHandler.WAITING_ITEMS_TABLE,
						values, MyDBHandler.COLUMN_NFCID + "=" + nfcid, null);
			} else {
				rowsUpdated = sqlDB.update(MyDBHandler.WAITING_ITEMS_TABLE,
						values, MyDBHandler.COLUMN_NFCID + "=" + nfcid + " and "
								+ selection, selectionArgs);
			}
			break;
		case USED_ITEMS:
			rowsUpdated = sqlDB.update(MyDBHandler.USED_ITEMS_TABLE, values,
					selection, selectionArgs);
			break;
		case USED_ITEMS_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(MyDBHandler.USED_ITEMS_TABLE,
						values, MyDBHandler.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = sqlDB.update(MyDBHandler.USED_ITEMS_TABLE,
						values, MyDBHandler.COLUMN_ID + "=" + id + " and "
								+ selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

}
