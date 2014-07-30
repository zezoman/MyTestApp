package com.example.tsvetan.mytestapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.tsvetan.mytestapp.service.MyService;

import java.util.Date;

public class Second extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    Button backBtn;
    EditText editText;
    ToggleButton toggleButton;
    Button startServBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        backBtn = (Button) findViewById(R.id.btn_back_second);
        backBtn.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.edt_second);

        toggleButton = (ToggleButton)findViewById(R.id.toggleButtonService);
        toggleButton.setOnCheckedChangeListener(this);

        startServBtn = (Button) findViewById(R.id.btn_start_service);
        startServBtn.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.second, menu);
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_back_second){
            String text = editText.getText().toString().trim();
            if(text.length() > 0){
                Intent back = new Intent(this, One.class);
                back.putExtra("message", text);
                setResult(RESULT_OK, back);
                //Call this when your activity is done and should be closed. The ActivityResult is
                // propagated back to whoever launched you via onActivityResult().
                finish();
            }else{
                Toast.makeText(this, "Cannot send back empty text.", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.btn_start_service){
            Toast.makeText(this, "Starting the service.", Toast.LENGTH_SHORT).show();
            Intent in = new Intent(this, MyService.class);
            in.putExtra("date", new Date().toString());
            startService(in);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent invoker = getIntent();
        String text = invoker.getStringExtra("message");
        editText.setText(text);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            Toast.makeText(this, "Starting the service.", Toast.LENGTH_SHORT).show();
            Intent in = new Intent(this, MyService.class);
            in.putExtra("date", new Date().toString());
            startService(in);
        }else{
            Toast.makeText(this, "Stopping the service.", Toast.LENGTH_SHORT).show();
            stopService((new Intent(this, MyService.class)));
        }
    }
}
