package com.example.tsvetan.mytestapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tsvetan.mytestapp.adapters.ItemsAdapter;
import com.example.tsvetan.mytestapp.db.ItemsDB;
import com.example.tsvetan.mytestapp.db.SimpleCursorLoader;
import com.example.tsvetan.mytestapp.parser.FeedUpdater;


public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private static String TAG = MainActivity.class.getName();
    ListView lvMain;
    ItemsAdapter itemsAdapter;
    private static final int MAIN_LOADER_IND = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemsAdapter = new ItemsAdapter(this, null);
        lvMain = (ListView) findViewById(R.id.lv_main);
        lvMain.setAdapter(itemsAdapter);
        lvMain.setEmptyView(findViewById(R.id.tv_latest_news_head));
        lvMain.setOnItemClickListener(this);

        getSupportLoaderManager().initLoader(MAIN_LOADER_IND, null, this);

        Log.d(TAG, "onCreate()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu()");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.btn_manage_feeds) {
            //explicit activity (FeedActivity will handle it and no one else)
            startActivity(new Intent(this, FeedActivity.class));
        } else if(item.getItemId()==R.id.btn_refresh_feeds){
            UpdateFeeds upd = new UpdateFeeds();
            upd.execute();
        } else if (id == R.id.btn_one) {
            //explicit activity (FeedActivity will handle it and no one else)
            startActivity(new Intent(this, One.class));
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new ItemsCursorLoader(getApplicationContext(),((MyApplication)getApplication()).getItemsDB());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        itemsAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        itemsAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor c=(Cursor)itemsAdapter.getItem(position);
        //implicit intent, we don't care which app will handle it
        Intent openItemInBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(c.getString(c.getColumnIndex("link"))));
        //this is how we can pass some params to the target activity/service/broadcaster
//        openItemInBrowser.putExtra("key1", "String");
//        openItemInBrowser.putExtra("key2", (byte)1);
        startActivity(openItemInBrowser);
    }

    public static final class ItemsCursorLoader extends SimpleCursorLoader {
        private ItemsDB items;
        public ItemsCursorLoader(Context context, ItemsDB items) {
            super(context);
            this.items=items;
        }

        @Override
        public Cursor loadInBackground() {
            return items.getItemsCursor();
        }

    }

    private class UpdateFeeds extends AsyncTask<Void, Integer, Void>{
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, getString(R.string.prd_title_main), getString(R.string.prd_message_main));
        }

        @Override
        //this specific method is not running on the UI thread so here we do the time consuming work
        protected Void doInBackground(Void... params) {
            FeedUpdater updater = new FeedUpdater();
            updater.updateAll((MyApplication) getApplication());
//            for(int i=0;i<x;i++){ onProgressUpdate(i); }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            getSupportLoaderManager().restartLoader(MAIN_LOADER_IND, null, MainActivity.this);
            progressDialog.dismiss();
        }
    }
}
