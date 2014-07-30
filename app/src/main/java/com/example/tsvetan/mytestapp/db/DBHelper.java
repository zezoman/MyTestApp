package com.example.tsvetan.mytestapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tsvetan on 24.7.2014 г..
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "yarss";
    public static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

//    private DBHelper() {
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table feeds (" +
                "_id integer primary key autoincrement," +
                "feed_url text not null" +
                ");");
        db.execSQL("create table items (" +
                "_id integer primary key autoincrement," +
                "title text not null," +
                "description text not null," +
                "link text not null," +
                "guid text not null," +
                "date_time text not null" +
                ");");
    }

    public SQLiteDatabase open(){
        return getWritableDatabase();
    }

    public void close() {
        super.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
