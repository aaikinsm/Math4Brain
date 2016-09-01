package com.blackstar.math4brain;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus on 2/5/2016.
 */
public class SyncDataActivity extends Activity {

    String[] gFile = new String[20];
    String syncURL = "http://amensah.com/kokotoa/sqlphp/sync.php";
    String userdata = "", FILENAME = "m4bfile1";
    boolean backup = true;
    short status, animFrame;
    TextView output;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync);
        output= (TextView) findViewById(R.id.syncInfo);
        final EditText email = (EditText) findViewById(R.id.emailText);
        final Button backupButton = (Button) findViewById(R.id.backupButton);
        final Button restoreButton = (Button) findViewById(R.id.restoreButton);
        final ImageView syncImage = (ImageView) findViewById(R.id.syncImage);
        final Handler mHandler = new Handler();

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey("userData")) {
            gFile = extras.getStringArray("userData");
        }

        final Runnable waitForResponse= new Runnable() {
            @Override
            public void run() {
                if(status==0){
                    if(animFrame==0) syncImage.setImageResource(R.drawable.sync);
                    else if(animFrame==1) syncImage.setImageResource(R.drawable.sync2);
                    else if(animFrame==2) syncImage.setImageResource(R.drawable.sync3);
                    else if(animFrame==3) syncImage.setImageResource(R.drawable.sync4);
                    else if(animFrame==4) syncImage.setImageResource(R.drawable.sync5);
                    else if(animFrame==5) syncImage.setImageResource(R.drawable.sync6);
                    else animFrame = -1;
                    animFrame++;
                    mHandler.postDelayed(this,50);
                }
                else if(status==1){
                    syncImage.setImageResource(R.drawable.syncok);
                    output.setText(R.string.complete);
                    status = 3;
                    mHandler.postDelayed(this, 2000);
                }
                else if(status==2){
                    syncImage.setImageResource(R.drawable.syncfailed);
                    output.setText("");
                    status=3;
                    mHandler.postDelayed(this, 2000);
                }
                else{
                    finish();
                }
            }
        };

        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().length()<5){
                    output.setText(R.string.enter_valid_email);
                }
                else{
                    gFile[22]=email.getText().toString();
                    for(int i = 0; i<gFile.length; i++){
                        userdata += gFile[i]+" ";
                    }
                    output.setText(R.string.loading);
                    backup = true;
                    status=0;
                    mHandler.post(waitForResponse);
                    new SyncDatabase().execute();
                }
            }
        });

        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().length()<5){
                    output.setText(R.string.enter_valid_email);
                }
                else{
                    gFile[22]=email.getText().toString();
                    backup = false;
                    output.setText(R.string.loading);
                    status = 0;
                    mHandler.post(waitForResponse);
                    new SyncDatabase().execute();
                }
            }
        });

    }

    class SyncDatabase extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            JSONParser jsonParser = new JSONParser();
            try {
                List<NameValuePair> params2 = new ArrayList<>();
                params2.add(new BasicNameValuePair("id", gFile[12]));
                params2.add(new BasicNameValuePair("email", gFile[22]));
                params2.add(new BasicNameValuePair("data", userdata));
                params2.add(new BasicNameValuePair("backup", ""+backup));
                JSONObject json = jsonParser.makeHttpRequest(syncURL, "POST", params2);
                try {
                    int success = json.getInt("success");
                    if (success == 1 && json.has("data")) {
                        String data = json.getString("data");
                        System.out.println("tstdata:"+data);
                        if(data.length()>30) {
                            try {
                                OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME, 0));
                                out.write(data);
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            status = 1;
                        }
                        else
                            status = 2;
                    }
                    else if (success == 2){
                        status =1;
                    }
                    else {
                        status=2;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }catch (NullPointerException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}


