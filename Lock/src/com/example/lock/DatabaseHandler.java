package com.example.lock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 3;

	// Database Name
	private static final String DATABASE_NAME = "PasswordManager";

	// Contacts table name
	private static final String TABLE_NAME = "AppPassword";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String DATA = "password";
	private static final String MAIL = "email";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table AppPassword "
				+ "(id integer primary key, password text, email text)");

	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		// Create tables again
		onCreate(db);
	}

	// Adding new contact
	public void save(String passdata, String emaildata) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DATA, passdata);
		values.put(MAIL, emaildata);
		// Inserting Row
		db.insert(TABLE_NAME, null, values);
		db.close(); // Closing database connection
	}

	public String getPassword(int no) {
		String value;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME,
				new String[] { KEY_ID, DATA, MAIL }, KEY_ID + "=?",
				new String[] { String.valueOf(no) }, null, null, null, null);
		try {
			if (cursor != null)
				cursor.moveToFirst();
			value = cursor.getString(1);

		} finally {
			cursor.close();
			db.close();
		}
		return value;
	}

	
	public String getEmail(int no) {
		String value;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME,
				new String[] { KEY_ID, DATA, MAIL }, KEY_ID + "=?",
				new String[] { String.valueOf(no) }, null, null, null, null);
		try {
			if (cursor != null)
				cursor.moveToFirst();
			value = cursor.getString(2);

		} finally {
			cursor.close();
			db.close();
		}
		return value;
	}
	
	public void reset() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
	}
}
