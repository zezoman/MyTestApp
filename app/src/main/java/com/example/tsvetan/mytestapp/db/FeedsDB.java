package com.example.tsvetan.mytestapp.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Tsvetan on 24.7.2014 г..
 */
public class FeedsDB {
    private static final String TAG = FeedsDB.class.getName();
    public static final String TABLE_NAME = "feeds";
    private SQLiteDatabase db;
    public FeedsDB(SQLiteDatabase db){
        this.db = db;
    }

    public void addFeed(String url) throws Exception {
        if(url == null || url.trim().length() == 0){
            throw new Exception("The provided feed URL cannot be empty!");
        }

        isFeedExists(url);

        ContentValues vals = new ContentValues();
        vals.put("feed_url", url);
        long rowId = db.insert(TABLE_NAME, null, vals);
        if(rowId==-1){
            Log.e(TAG, "Error inserting new row in "+TABLE_NAME);
            throw new Exception("Error inserting new row in "+TABLE_NAME);
        }else{
            Log.d(TAG, "New url inserted "+TABLE_NAME);
        }

    }

    public void isFeedExists(String feed) throws Exception {
        Cursor cur = null;
        try{
            cur = db.query(false, TABLE_NAME, new String[]{"feed_url"}, TABLE_NAME + ".feed_url = ?", new String[]{feed}, null, null, null, null);
            if(cur.getCount()>0){
                Log.w(TAG, "Feed url already exists in DB!");
                throw new Exception("Feed urls already exists and will not be added again!");
            }
            Log.d(TAG, "Feed url is unique in DB");
        }finally {
            if(cur!=null){
                cur.close();
            }
        }
    }

    public Cursor getFeedsCursor(){
        //select all columns and all rows
        return db.query(false, TABLE_NAME, null, null, null, null, null, null, null);
    }

    public void deleteFeed(long Id) {
        db.delete(TABLE_NAME, TABLE_NAME+"._id = ?", new String[]{String.valueOf(Id)});
    }
}
