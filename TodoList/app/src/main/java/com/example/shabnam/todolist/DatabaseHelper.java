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
    private static final String DATABASE_NAME = "todoLists";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_ITEM_LIST = "itemList";

    // Post Table Columns
    private static final String KEY_ITEM_LIST_ID = "id";
    private static final String KEY_ITEM_LIST_ITEM = "item";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEM_LIST +
        "(" +
                KEY_ITEM_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_ITEM_LIST_ITEM + " STRING" +
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

    public void addItem(String item) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_ITEM_LIST_ITEM, item);

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
    public void deleteAllItems() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_ITEM_LIST, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all items");
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllItems() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_ITEM_LIST, null);

        return result;
    }


}
