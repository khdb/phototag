package com.khoahuy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHandler extends SQLiteOpenHelper {

	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "phototag.db";
	public static final String WAITING_ITEMS_TABLE = "waiting_items";
	public static final String USED_ITEMS_TABLE = "used_items";

	public static final String COLUMN_ID = "id";
	public static final String COLUMN_IMAGE = "image";
	public static final String COLUMN_CHECK_IN = "checkin";
	public static final String COLUMN_CHECK_OUT = "checkout";
	
	public static final String COLUMN_NFCID = "nfcid";

	static final String CREATE_DB_WAITING_TABLE = String
			.format(" CREATE TABLE IF NOT EXISTS "
					+ "%s (%s CHARACTER(25) PRIMARY KEY, %s TEXT NOT NULL, %s TIMESTAMP NOT NULL);",
					WAITING_ITEMS_TABLE, COLUMN_NFCID, COLUMN_IMAGE, COLUMN_CHECK_IN);
	
	static final String CREATE_DB_USED_TABLE = String
			.format(" CREATE TABLE IF NOT EXISTS "
					+ "%s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					"%s CHARACTER(25), %s TIMESTAMP NOT NULL, %s TIMESTAMP NOT NULL);",
					USED_ITEMS_TABLE, COLUMN_ID, COLUMN_NFCID, COLUMN_CHECK_IN, COLUMN_CHECK_OUT);

	public MyDBHandler(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_DB_WAITING_TABLE);
		db.execSQL(CREATE_DB_USED_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
