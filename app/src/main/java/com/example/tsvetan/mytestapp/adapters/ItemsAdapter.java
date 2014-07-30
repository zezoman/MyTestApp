package com.example.tsvetan.mytestapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tsvetan.mytestapp.R;

/**
 * Created by Tsvetan on 25.7.2014 г..
 */
public class ItemsAdapter extends CursorAdapter {
    public ItemsAdapter(Context context, Cursor c) {
        super(context, c,false);
    }

    @Override
    public void bindView(View view, Context arg1, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.title.setText(cursor.getString(cursor.getColumnIndex("title")));
        viewHolder.desc.setText(cursor.getString(cursor.getColumnIndex("description")));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.lv_main_row, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.title = (TextView) view.findViewById(R.id.tv_title_lv_row_main);
        viewHolder.desc = (TextView) view.findViewById(R.id.tv_descr_lv_row_main);
        view.setTag(viewHolder);
        return view;
    }

    public class ViewHolder {
        TextView title;
        TextView desc;
    }
}
