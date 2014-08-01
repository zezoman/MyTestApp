package com.example.tsvetan.mytestapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;


public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private static String TAG = MainActivity.class.getName();
    ListView lvMain;
    ItemsAdapter itemsAdapter;
    private static final int MAIN_LOADER_IND = 0;
    File fileInternal;
    File fileExternal;

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

        initFiles();

        Log.d(TAG, "onCreate() finished");
    }

    private void initFiles(){
        String storageState = Environment.getExternalStorageState();
        fileInternal = new File(getFilesDir(),"test_internal.txt");
        fileExternal = new File(getExternalFilesDir(null),"test_external.txt");
        BufferedWriter bufferedWriterInternal = null;
        BufferedWriter bufferedWriterExternal = null;

        //Internal storage is always available
        try {
            bufferedWriterInternal = new BufferedWriter(new FileWriter(fileInternal,true));
            bufferedWriterInternal.write("Hello internal storage, the date is "+new Date());
            bufferedWriterInternal.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bufferedWriterInternal != null){
                try {
                    bufferedWriterInternal.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //External storage MAY NOT be available and we want to use it for WRITE operation
        if (storageState.equals(Environment.MEDIA_MOUNTED)){
            try {
                bufferedWriterExternal = new BufferedWriter(new FileWriter(fileExternal,true));
                bufferedWriterExternal.write("Hello external storage, the date is "+new Date());
                bufferedWriterExternal.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(bufferedWriterExternal != null){
                    try {
                        bufferedWriterExternal.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //External storage MAY NOT be available and we want to use it for READ operation
        if (storageState.equals(Environment.MEDIA_MOUNTED) || storageState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
            // Get a top-level public external storage directory for placing files of a particular type. This is where the user will typically place and manage
            // their own files, so you should be careful about what you put here to ensure you don't erase their files or get in the way of their own organization.
            File fileExternalPublic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            Log.d(TAG, "getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES): "+fileExternalPublic.getAbsolutePath());


            // For storing custom public files which do not comply with the Environment types in getExternalStoragePublicDirectory.
            // It return the primary external storage directory. Applications should not directly use this top-level directory, in order to avoid polluting the user's root namespace
            // It is not deleted on uninstall and can be
            File fileExternal = Environment.getExternalStorageDirectory();
            Log.d(TAG, "getExternalStorageDirectory(): "+fileExternal.getAbsolutePath());

            // These files are internal to the applications, and not typically visible to the user as media.
            // This is like the getFilesDir() for internal storage in that these files will be deleted when the application is uninstalled.
            File fileExternalPrivate = getExternalFilesDir(null); //null means the root of the files directory
            Log.d(TAG, "getExternalFilesDir(null): "+fileExternalPrivate);

            //!!! Always use File.mkdirs() to ensure that the path exists.
        }else{
            Log.d(TAG, "External storage is not available!");
        }

        // uses internal storage
        FileOutputStream fos = null;
        try {
            byte[] bytes = "Hello Android".getBytes("utf-8");
            fos = openFileOutput("bin",Context.MODE_PRIVATE);
            fos.write(bytes);
            fos.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // uses internal storage
        FileInputStream fis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            fis = openFileInput("bin"); // uses internal storage

            byte[] data = new byte[128];
            int bytesRead=0;
            while((bytesRead = fis.read(data)) != -1){
                baos.write(data,0,bytesRead);
            }
            String dataStr = baos.toString("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(baos != null){
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


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
        } else if (id == R.id.btn_httpclient) {
            //explicit activity (FeedActivity will handle it and no one else)
            startActivity(new Intent(this, HttpClientActivity.class));
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
