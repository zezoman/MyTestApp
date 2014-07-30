package com.example.tsvetan.mytestapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tsvetan.mytestapp.adapters.FeedsAdapter;
import com.example.tsvetan.mytestapp.db.FeedsDB;
import com.example.tsvetan.mytestapp.db.SimpleCursorLoader;

public class FeedActivity extends FragmentActivity implements DialogInterface.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> , AdapterView.OnItemLongClickListener{
    private static final String TAG = FeedActivity.class.getName();
    AlertDialog addDialog = null;
    AlertDialog deleteDialog = null;
    EditText editText = null;
    ListView lvFeeds = null;
    private FeedsAdapter feedsAdapter;
    private static final int FEED_LOADER_IND = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        lvFeeds = (ListView)findViewById(R.id.lv_feeds);

        //replace this with cursor loader
//        Cursor c = ((MyApplication)getApplication()).getFeedsDB().getFeedsCursor();
//        lvFeeds.setAdapter(new FeedsAdapter(this, c));
//        startManagingCursor(c);
//        lvFeeds.setAdapter(new FeedsAdapter(this, c));
        feedsAdapter = new FeedsAdapter(this,null);
        lvFeeds.setAdapter(feedsAdapter);
        //register our loader
        getSupportLoaderManager().initLoader(FEED_LOADER_IND, null, this);

        lvFeeds.setOnItemLongClickListener(this);

        editText = new EditText(this);
        addDialog = createAlertDialog(R.string.feed_dialog_add_title , editText, this);
    }

    private AlertDialog createAlertDialog(int messageResId, View content, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder _addDialog = new AlertDialog.Builder(this);
        _addDialog.setTitle(getString(messageResId));
        _addDialog.setPositiveButton(getString(R.string.feed_dialog_btn_ok), listener);
        _addDialog.setNegativeButton(getString(R.string.feed_dialog_btn_cancel), listener);
        if(content != null){
            _addDialog.setView(content);
        }
        return _addDialog.create();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.btn_add_feeds){
            addDialog.show();
        }

        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == AlertDialog.BUTTON_POSITIVE){
            if(dialog.equals(addDialog)){
                String newFeed = editText.getText().toString().trim();
                if(newFeed.length()==0){
                    Toast.makeText(this, getString(R.string.feed_dialog_non_empty_msg), Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    ((MyApplication)getApplication()).getFeedsDB().addFeed(newFeed);
                    getSupportLoaderManager().restartLoader(FEED_LOADER_IND, null, this);
                    Toast.makeText(this, getString(R.string.feed_dialog_add_success_msg), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(TAG, "Problem adding the new feed URL.",e);
                    Toast.makeText(this, getString(R.string.feed_dialog_failed_msg), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new FeedsCursorLoader(this, ((MyApplication)getApplication()).getFeedsDB());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        feedsAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        feedsAdapter.swapCursor(null);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
        final AlertDialog deleteDialog = createAlertDialog(R.string.feed_dialog_delete_title, null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == AlertDialog.BUTTON_POSITIVE) {
                    try {
                        ((MyApplication) getApplication()).getFeedsDB().deleteFeed(id);
                        getSupportLoaderManager().restartLoader(FEED_LOADER_IND, null, FeedActivity.this);
                        Toast.makeText(FeedActivity.this, getString(R.string.feed_dialog_delete_success_msg), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Problem deleting the feed URL.", e);
                        Toast.makeText(FeedActivity.this, getString(R.string.feed_dialog_failed_msg), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        deleteDialog.show();
        return true;
    }

    private static final class FeedsCursorLoader extends SimpleCursorLoader{
        private FeedsDB feedsDB;

        public FeedsCursorLoader(Context context, FeedsDB feedsDB) {
            super(context);
            this.feedsDB = feedsDB;
        }

        @Override
        public Cursor loadInBackground() {
            return feedsDB.getFeedsCursor();
        }
    }
}
