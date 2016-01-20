package com.example.shabnam.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Shabnam on 1/15/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    String TAG = "INSERT_FAILED";

    // Database Info
    private static final String DATABASE_NAME = "TodoDB_10";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_ITEM_LIST = "itemList";

    // Post Table Columns
    private static final String KEY_ITEM_LIST_ID = "id";
    private static final String KEY_ITEM_LIST_ITEM = "item";
    private static final String KEY_ITEM_LIST_PRIORITY = "priority";
    private static final String KEY_ITEM_LIST_DAY = "day";
    private static final String KEY_ITEM_LIST_MONTH = "month";
    private static final String KEY_ITEM_LIST_YEAR = "year";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEM_LIST +
        "(" +
                KEY_ITEM_LIST_ID + " INTEGER PRIMARY KEY," +
                KEY_ITEM_LIST_ITEM + " STRING," +
                KEY_ITEM_LIST_PRIORITY + " STRING," +
                KEY_ITEM_LIST_DAY + " STRING," +
                KEY_ITEM_LIST_MONTH + " STRING," +
                KEY_ITEM_LIST_YEAR + " STRING" +
        ")";

        db.execSQL(CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_LIST);
            onCreate(db);
        }
    }

    public void addItem(int anId, String anItem, String aPriority, String aDay, String aMonth, String aYear ) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ITEM_LIST_ITEM, anItem);
            values.put(KEY_ITEM_LIST_PRIORITY, aPriority);
            values.put(KEY_ITEM_LIST_ID, anId);
            values.put(KEY_ITEM_LIST_YEAR, aYear);
            values.put(KEY_ITEM_LIST_MONTH, aMonth);
            values.put(KEY_ITEM_LIST_DAY, aDay);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_ITEM_LIST, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }
    // Delete all posts and users in the database
    public void updateItemFields(int anId, String anItem, String aPriority, String aDay, String aMonth, String aYear) {

        SQLiteDatabase db = getReadableDatabase();
        String strSQL="Update " + TABLE_ITEM_LIST + " SET "+ KEY_ITEM_LIST_PRIORITY + " = \"" + aPriority + "\", "+
        KEY_ITEM_LIST_MONTH + " = \"" + aMonth + "\" , " +
        KEY_ITEM_LIST_DAY + " = \"" + aDay + "\" , " +
        KEY_ITEM_LIST_YEAR + " = \"" + aYear + "\" , " +
        KEY_ITEM_LIST_ITEM + " = \"" + anItem + "\"  "
        +"WHERE " + KEY_ITEM_LIST_ID + " = " + anId;
        db.execSQL(strSQL);
    }

    public void deleteItem(int anId ) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            db.delete(TABLE_ITEM_LIST, KEY_ITEM_LIST_ID + "=" + anId, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all items");
        } finally {
            db.endTransaction();
        }
    }
    public void updateItem(int aNewId ) {

        SQLiteDatabase db = getWritableDatabase();
        String strSQL = "UPDATE " + TABLE_ITEM_LIST + " SET "+ KEY_ITEM_LIST_ID + "  =  "+ aNewId;

        db.execSQL(strSQL);

    }

    public Cursor getAllItems() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_ITEM_LIST, null);

        return result;
    }

    public Cursor getItem(int anId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_ITEM_LIST + " WHERE " + KEY_ITEM_LIST_ID + " = " + anId, null);

        return result;
    }


}
