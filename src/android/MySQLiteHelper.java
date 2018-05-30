package com.hmkcode.android.sqlite;
 
import java.util.LinkedList;
import java.util.List;
  
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class MySQLiteHelper extends SQLiteOpenHelper {
 
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ea.db";
 
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create phoneNumbers table
        String phoneNumbersQuery = "CREATE TABLE IF NOT EXISTS phoneNumbers ( " +
                "rowid INTEGER PRIMARY KEY, " + 
                "phoneNumber TEXT)";
 
        // create phoneNumbers table
        db.execSQL(phoneNumbersQuery);

        // SQL statement to create keywords table
        String keywordsQuery = "CREATE TABLE IF NOT EXISTS keywords ( " +
                "rowid INTEGER PRIMARY KEY, " + 
                "keywordId TEXT, " +
                "keyword TEXT)";
 
        // create phoneNumbers table
        db.execSQL(keywordsQuery);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS phoneNumbers");
        db.execSQL("DROP TABLE IF EXISTS keywords");
 
        // create fresh tables
        this.onCreate(db);
    }

    // Get All records from table
    public List<String> getAllRecords(String table, int ind) {
        List<String> records = new LinkedList<String>();
 
        // 1. build the query
        String query = "SELECT  * FROM " + table;
 
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
 
        // 3. go over each row, build book and add it to list
        String tmp = "";
        if (cursor.moveToFirst()) {
            do {
                tmp = cursor.getString(ind);
 
                // Add record to records
                records.add(tmp);
            } while (cursor.moveToNext());
        }
 
        Log.d("SMSPluginDatabase", records.toString());
 
        // return books
        return records;
    }

    public void savePhoneNumber(String phoneNumber)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("phoneNumber", phoneNumber);

        db.insert("phoneNumbers", null, values);
    }

    public void removePhoneNumber(String phoneNumber)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        
        String selection = "phoneNumber LIKE ?";
        String[] selectionArgs = { phoneNumber };

        int deletedRows = db.delete("phoneNumbers", selection, selectionArgs);
    }

    public void saveKeyword(String keywordId, String keyword)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("keywordId", keywordId);
        values.put("keyword", keyword);

        db.insert("keywords", null, values);
    }

    public void removeKeyword(String keywordId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        
        String selection = "keywordId LIKE ?";
        String[] selectionArgs = { keywordId };
        
        int deletedRows = db.delete("keywords", selection, selectionArgs);
    }
}