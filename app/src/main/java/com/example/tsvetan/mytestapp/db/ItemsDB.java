package com.example.tsvetan.mytestapp.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tsvetan.mytestapp.parser.Item;

import java.util.ArrayList;

public class ItemsDB {
	private SQLiteDatabase db;
	private static final String TABLE_NAME="items";
	
	public ItemsDB(SQLiteDatabase db){
		this.db=db;
	}
	
	public void addItems(ArrayList<Item> items) {
		if (items != null && items.size() > 0) {
			for (Item i : items) {
				ContentValues vals=new ContentValues();
				vals.put("title", i.getTitle());
				vals.put("description", i.getDescription());
				vals.put("link", i.getLink());
				vals.put("guid", i.getGuid());
				vals.put("date_time", i.getDate());
				db.insert(TABLE_NAME, null, vals);
			}
		}
	}
	
	public Cursor getItemsCursor(){
		return db.query(TABLE_NAME,new String[]{"_id,title,description,link"},null,null,null,null,null);
		
	}
	
}
