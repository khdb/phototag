package com.khoahuy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHandler extends SQLiteOpenHelper {

	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "khoahuy.db";
	public static final String TABLE_ITEMS = "items";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_IMAGE = "image";
	public static final String COLUMN_CHECK_IN = "checkin";
	public static final String COLUMN_CHECK_OUT = "checkout";

	static final String CREATE_DB_TABLE = String
			.format(" CREATE TABLE IF NOT EXISTS "
					+ "%s (%s CHARACTER(25) PRIMARY KEY, %s TEXT NOT NULL, %s TIMESTAMP NOT NULL, %s TIMESTAMP NOT NULL );",
					TABLE_ITEMS, COLUMN_ID, COLUMN_IMAGE, COLUMN_CHECK_IN,
					COLUMN_CHECK_OUT);

	public MyDBHandler(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_DB_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
