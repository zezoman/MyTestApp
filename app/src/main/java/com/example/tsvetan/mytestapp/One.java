package com.example.tsvetan.mytestapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class One extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.one, menu);
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
        Button sendBtn;
        EditText editText;
        TextView textView;
        MyLocationListener myLocList;
        LocationManager lm;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_one, container, false);
            sendBtn = (Button)rootView.findViewById(R.id.btn_send_one);
            sendBtn.setOnClickListener(this);
            editText = (EditText) rootView.findViewById(R.id.edt_one);
            textView = (TextView) rootView.findViewById(R.id.tv_result_one);
            myLocList = new MyLocationListener();
            lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1000, myLocList);
        }

        @Override
        public void onClick(View v) {
            String text = editText.getText().toString().trim();
            if(text.length() > 0){
                Intent in = new Intent(getActivity(), Second.class);
                in.putExtra("message", text);
                //if we wanted to get a result from the invoked activity we must use
                // startActivityForResult() instead of startActivity()
                startActivityForResult(in, 1);
            }else{
                Toast.makeText(getActivity(), "Cannot send empty text.", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        //we should override onActivityResult() in order to receive the result
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == 1){//in case we started several activities
                if(RESULT_OK == resultCode){//the invoked activity finished successfully
                    String resultTxt = data.getStringExtra("message");
                    if(resultTxt!= null && resultTxt.length()>0){
                        textView.setText(resultTxt);
                    }else{
                        Toast.makeText(getActivity(), "No result was supplied.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        private class MyLocationListener implements LocationListener{

            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(getActivity(), "Location changed: Lat: "+ location.getLatitude()+" Long: "+location.getLongitude(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                String statusTxt = "";
                switch (status){
                    case LocationProvider.OUT_OF_SERVICE: statusTxt = "Out of service";break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE: statusTxt = "Temporary unavailable";break;
                    case LocationProvider.AVAILABLE: statusTxt = "Available";break;
                    default:statusTxt = "Unknown";break;
                }
                Toast.makeText(getActivity(), "Provider "+provider+" status changed to: "+statusTxt, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(getActivity(), "Provider "+provider+" enabled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getActivity(), "Provider "+provider+" disabled.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
