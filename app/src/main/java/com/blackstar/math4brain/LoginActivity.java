package com.blackstar.math4brain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Asus on 2/5/2016.
 */
public class LoginActivity extends Activity {
    int FILESIZE =25;
    String[] gFile = new String[FILESIZE];
    String syncURL = "http://amensah.com/kokotoa/sqlphp/sync.php", FILENAME = "m4bfile1",
            FILEDP = "displayPicture.png";
    int status, animFrame;
    String facebookUserId, deviceID;
    TextView output;
    CallbackManager callbackManager;
    ProfilePictureView profilePictureView;
    boolean initial = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync);
        output = (TextView) findViewById(R.id.syncInfo);
        final ImageView syncImage = (ImageView) findViewById(R.id.syncImage);
        final TextView skipLink = (TextView) findViewById(R.id.textViewSkip);
        final Handler mHandler = new Handler();
        deviceID = android.os.Build.DEVICE;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("userData")) {
                gFile = extras.getStringArray("userData");
            }
            if (extras.containsKey("initial")) {
                skipLink.setVisibility(View.VISIBLE);
                initial = true;
            } else {
                skipLink.setVisibility(View.GONE);
            }
        }

        final Runnable waitForResponse = new Runnable() {
            @Override
            public void run() {
                if (status == -1) {
                    if (animFrame == 0) syncImage.setImageResource(R.drawable.sync);
                    else if (animFrame == 1) syncImage.setImageResource(R.drawable.sync2);
                    else if (animFrame == 2) syncImage.setImageResource(R.drawable.sync3);
                    else if (animFrame == 3) syncImage.setImageResource(R.drawable.sync4);
                    else if (animFrame == 4) syncImage.setImageResource(R.drawable.sync5);
                    else if (animFrame == 5) syncImage.setImageResource(R.drawable.sync6);
                    else animFrame = -1;
                    animFrame++;
                    mHandler.postDelayed(this, 50);
                } else if (status == 0 || status == 1) {
                    syncImage.setImageResource(R.drawable.syncok);
                    status = 3;
                    mHandler.postDelayed(this, 2000);
                } else if (status == 2) {
                    syncImage.setImageResource(R.drawable.syncfailed);
                    status = 3;
                    mHandler.postDelayed(this, 2000);
                } else {
                    if (initial)
                            Toast.makeText(getApplicationContext(), R.string.try_challenge, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        };


//////////////////////////////////////

        profilePictureView = (ProfilePictureView) findViewById(R.id.friendProfilePicture);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            profilePictureView.setProfileId(accessToken.getUserId());
            syncImage.setVisibility(View.GONE);
            output.setVisibility(View.GONE);
        }

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                facebookUserId = loginResult.getAccessToken().getUserId();

                profilePictureView.setProfileId(facebookUserId);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                try {
                                    gFile[22] = object.getString("email");
                                    if (gFile[13] == null || gFile[13].equals("User:_no_name"))
                                        gFile[13] = object.getString("name").replace(" ", "_");

                                    output.setText(R.string.loading);
                                    status = -1;
                                    mHandler.post(waitForResponse);
                                    new SyncDb().execute();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                output.setText(gFile[13].replace("_", " "));
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                status = 2;
                mHandler.post(waitForResponse);
            }

            @Override
            public void onError(FacebookException exception) {
                status = 2;
                mHandler.post(waitForResponse);
            }
        });


        skipLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), UserActivity.class);
                i.putExtra("setName", true);
                startActivity(i);
                finish();
            }
        });


//////////////////////////////////////////////////////////

    }


    class SyncDb extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {

            String userdata="";
            for (int i = 0; i < gFile.length; i++) {
                userdata += gFile[i] + " ";
            }

            JSONParser jsonParser = new JSONParser();
            try {
                List<NameValuePair> params2 = new ArrayList<>();
                params2.add(new BasicNameValuePair("scoreID", gFile[12]));
                params2.add(new BasicNameValuePair("email", gFile[22]));
                params2.add(new BasicNameValuePair("data", userdata));
                params2.add(new BasicNameValuePair("name", gFile[13]));
                params2.add(new BasicNameValuePair("fbUID", facebookUserId));
                params2.add(new BasicNameValuePair("deviceID", deviceID));
                JSONObject json = jsonParser.makeHttpRequest(syncURL, "POST", params2);
                try {
                    int success = json.getInt("success");
                    if (success == 1){
                        if(json.has("data")) {
                            String data = json.getString("data");
                            if (data.length() > 30) {
                                try {
                                    OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME, 0));
                                    out.write(data);
                                    out.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                status = 1;
                            } else
                                status = 2;
                        }else
                            status = 2;
                            Log.d("DatabaseResponse:", json.getString("message"));
                    } else {
                        status = success;
                        Log.d("DatabaseResponse:", json.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            //save FB photo
            URL imageURL;
            try {
                imageURL = new URL("https://graph.facebook.com/" + facebookUserId + "/picture?type=large");
                String filePath = getFilesDir().getPath() + "/"+FILEDP;
                File f = new File(filePath);
                FileOutputStream out = new FileOutputStream(f);
                Bitmap bmp = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("progress","error occured");
            }
            return null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}


