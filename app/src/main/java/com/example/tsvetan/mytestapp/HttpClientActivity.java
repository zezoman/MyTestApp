package com.example.tsvetan.mytestapp;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;

import com.example.tsvetan.mytestapp.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HttpClientActivity extends Activity {
    private static final String TAG = "HttpClientActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_client);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.http_client, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {
        Button doGetBtn;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_http_client, container, false);
            doGetBtn = (Button)rootView.findViewById(R.id.btn_doget_httpactivity);
            doGetBtn.setOnClickListener(this);
            return rootView;
        }

        @Override
        public void onClick(View v) {
            TestGet task = new TestGet();
            task.execute("http://google.com");
        }

        private class TestGet extends AsyncTask<String, Void, Void>{

            @Override
            protected Void doInBackground(String... params) {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                BufferedReader br = null;
                try {
                    HttpResponse resp = client.execute(get);
                    br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
                    StringBuilder sb = new StringBuilder();
                    String chunk = null;
                    while( (chunk = br.readLine()) != null ){
                        sb.append(chunk);
                    }
                    Log.d(TAG, sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (br!=null){
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        }
    }
}
