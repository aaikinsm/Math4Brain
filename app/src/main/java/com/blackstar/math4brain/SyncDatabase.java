package com.blackstar.math4brain;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.AccessToken;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Albert Jr on 2017-04-21.
 */

public class SyncDatabase extends AsyncTask<String, String, String> {

    String[] gFile;
    private int[] aScores = new int[3];
    Context context;

    private final String IPADRS = "amensah.com/kokotoa/sqlphp", FILENAME = "m4bfile1";
    private String VERSION, locale;

    private String url_update_user = "http://" + IPADRS + "/update_users.php";
    private String syncURL = "http://" + IPADRS + "/sync.php";

    public SyncDatabase(String[] fileData, Context ctx) {
        gFile = fileData;
        context = ctx;
        locale = Locale.getDefault().getLanguage();
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            VERSION = ("v" + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... args) {

        aScores[0] = Integer.parseInt(gFile[9]);
        aScores[1] = Integer.parseInt(gFile[10]);
        aScores[2] = Integer.parseInt(gFile[11]);
        int level = Integer.parseInt(gFile[7]);
        int average = 0;
        if (aScores[0] != 0 && aScores[1] != 0) {
            average = aScores[0] / aScores[1];
        }
        if(gFile[23] == null || gFile[23].equals("null"))
            gFile[23] = "NoImageURL";


        JSONParser jsonParser = new JSONParser();

        //score calculation
        long myGameScore = (level * 10000) + (average * 100) + (aScores[0]);

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", gFile[12]));
        params.add(new BasicNameValuePair("name", gFile[13]));
        params.add(new BasicNameValuePair("level", gFile[7]));
        params.add(new BasicNameValuePair("average", average + ""));
        params.add(new BasicNameValuePair("tpoints", aScores[0] + ""));
        params.add(new BasicNameValuePair("gscore", myGameScore + ""));
        params.add(new BasicNameValuePair("version", VERSION));
        params.add(new BasicNameValuePair("locale", locale));
        if (gFile[23].equals("NoImageURL"))
            params.add(new BasicNameValuePair("picture", "none"));
        else
            params.add(new BasicNameValuePair("picture", gFile[23]));


        jsonParser.makeHttpRequest(url_update_user, "POST", params);


        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            String facebookUserId = accessToken.getUserId();
            String deviceID = android.os.Build.DEVICE;
            String userdata = "";
            for (String dat : gFile) {
                userdata += dat + " ";
            }
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
                    if (success == 1 && json.has("data")) {
                        String data = json.getString("data");
                        if (data.length() > 30) {
                            try {
                                OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput(FILENAME, 0));
                                out.write(data);
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
