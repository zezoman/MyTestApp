package com.example.tsvetan.mytestapp;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.example.tsvetan.mytestapp.db.DBHelper;
import com.example.tsvetan.mytestapp.db.FeedsDB;
import com.example.tsvetan.mytestapp.db.ItemsDB;

/**
 * Created by Tsvetan on 24.7.2014 г..
 */
public class MyApplication extends Application{
    private SQLiteDatabase db;
    private FeedsDB feedsDB;
    private ItemsDB itemsDB=null;

    public SQLiteDatabase getDB(){
        if(db==null){
            db = (new DBHelper(getApplicationContext())).open();
        }
        return db;
    }

    public FeedsDB getFeedsDB(){
        if(feedsDB == null){
            feedsDB = new FeedsDB(getDB());
        }
        return feedsDB;
    }

    public ItemsDB getItemsDB(){
        if(itemsDB==null){
            itemsDB=new ItemsDB(getDB());
        }
        return itemsDB;
    }
}
