package com.example.tsvetan.mytestapp.parser;

import android.database.Cursor;

import com.example.tsvetan.mytestapp.MyApplication;

import java.util.ArrayList;

public class FeedUpdater {

	public void updateAll(MyApplication app) {
		Cursor c = app.getFeedsDB().getFeedsCursor();
		RssParser parser = new RssParser();
		ArrayList<Item> result = new ArrayList<Item>();
		if (c.moveToFirst()) {
			do {
				result.addAll(parser.parse(c.getString(c
						.getColumnIndex("feed_url"))));
			} while (c.moveToNext());
		}
		if (c != null && !c.isClosed()) {
            c.close();
        }
		if(result.size()>0){
        	app.getItemsDB().addItems(result);        	
        }
	}

}
