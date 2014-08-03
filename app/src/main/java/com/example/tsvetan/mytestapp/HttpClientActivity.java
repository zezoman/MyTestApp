package com.example.tsvetan.mytestapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
        Button doPostBtn;
        TextView tvResponse;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_http_client, container, false);
            doGetBtn = (Button)rootView.findViewById(R.id.btn_doget_httpactivity);
            doGetBtn.setOnClickListener(this);
            doPostBtn = (Button)rootView.findViewById(R.id.btn_dopost_httpactivity);
            doPostBtn.setOnClickListener(this);
            tvResponse = (TextView) rootView.findViewById(R.id.tv_httpclient);
            return rootView;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == doGetBtn.getId()){
                TestGet task = new TestGet();
                task.execute("http://google.com");
            } else if(v.getId() == doPostBtn.getId()){
                TestPost task = new TestPost();
                task.execute("http://google.com");
            }

        }

        private class TestGet extends AsyncTask<String, Void, Void>{
            private StringBuilder sb;

            @Override
            protected Void doInBackground(String... params) {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                BufferedReader br = null;
                try {
                    HttpResponse resp = client.execute(get);
                    br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
                    sb = new StringBuilder();
                    String chunk = null;
                    while( (chunk = br.readLine()) != null ){
                        sb.append(chunk);
                    }

                    Log.d(TAG, "Received GET response successfully.");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error receiving GET response.");
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

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                tvResponse.setText(sb.toString());
            }
        }

        private class TestPost extends AsyncTask<String, Void, Void>{
            private StringBuilder sb;

            @Override
            protected Void doInBackground(String... params) {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(params[0]);
                post.addHeader("Content-Type", "application/x-www-form-urlencoded");
                List<NameValuePair> postParams = new ArrayList();
                postParams.add(new BasicNameValuePair("q", "proba"));
                try {
                    post.setEntity(new UrlEncodedFormEntity(postParams));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
                BufferedReader br = null;
                try {
                    HttpResponse resp = client.execute(post);
                    br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
                    sb = new StringBuilder();
                    String chunk = null;
                    while( (chunk = br.readLine()) != null ){
                        sb.append(chunk);
                    }

                    Log.d(TAG, "Received POST response successfully.");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error receiving POST response.");
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

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                tvResponse.setText(sb.toString());
            }
        }
    }
}
