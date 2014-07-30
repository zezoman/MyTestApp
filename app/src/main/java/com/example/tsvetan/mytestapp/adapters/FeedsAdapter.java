package com.example.tsvetan.mytestapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.tsvetan.mytestapp.R;

/**
 * Created by Tsvetan on 24.7.2014 г..
 */
public class FeedsAdapter extends CursorAdapter {

    public FeedsAdapter(Context context, Cursor c) {
        //we use false for the auto re-query param because we don't want the cursor to automatically
        // check for changes. We use Loader for that purpose
        super(context, c, false);
    }


    @Override
    // newView is called for each and every ListView row. The view can be reused
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_feed_lv_row, parent, false);
        ViewHolder vHold = new ViewHolder();
        vHold.tView = (TextView) view.findViewById(R.id.tv_lv_row_feeds);
        view.setTag(vHold);
        return view;
    }


    @Override
    // Here we fill in the actual data into the constructed empty view (newView())
    // The 'cursor' is already in position to read the data
    // for the current row and fill it in its corresponding 'view' element from the list
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vHold = (ViewHolder) view.getTag();
        String text = cursor.getString(cursor.getColumnIndex("feed_url"));
        vHold.tView.setText(text);
    }

    public class ViewHolder{
        TextView tView;
    }
}
