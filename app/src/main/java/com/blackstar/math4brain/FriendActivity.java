package com.blackstar.math4brain;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by Albert on 2017-09-15.
 */

public class FriendActivity extends Activity {

    List<String[]> fList = new ArrayList<>();
    UserListAdapter listAdapter;
    String IPADRS="amensah.com/kokotoa/sqlphp";
    Handler mHandler = new Handler();
    Runnable timer;
    boolean complete = false;
    int rank = 0, friendAddStatus = 0;
    ArrayList<String> friendIds = new ArrayList<>();
    String searchText = "", myId = "12345678-1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);
        final ImageButton addBtn = (ImageButton) findViewById(R.id.imageButtonAdd) ;
        final TextView info = (TextView) findViewById(R.id.textViewInfo);
        final EditText uid = (EditText) findViewById(R.id.editTextUID);
        final ImageButton searchBtn = (ImageButton) findViewById(R.id.imageButtonSearch) ;
        final ProgressBar prog = (ProgressBar) findViewById(R.id.progressBar4) ;
        final ListView friendList = (ListView) findViewById(R.id.friendsList);
        final LinearLayout listContainer = (LinearLayout) findViewById(R.id.fListContainer);
        listAdapter = new UserListAdapter(this, R.layout.users_row, fList, 2);
        friendList.setAdapter(listAdapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("id")){
                myId = extras.getString("id");
            }
        }

        info.setVisibility(View.GONE);
        uid.setVisibility(View.GONE);
        searchBtn.setVisibility(View.GONE);
        listContainer.setVisibility(View.GONE);

        addBtn.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick (View v){
                info.setVisibility(View.VISIBLE);
                uid.setVisibility(View.VISIBLE);
                searchBtn.setVisibility(View.VISIBLE);
                addBtn.setVisibility(View.GONE);
            }
        });

        searchBtn.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick (View v){
                searchText = uid.getText().toString();
                searchBtn.setEnabled(false);
                info.setText(R.string.searching);
                prog.setVisibility(View.VISIBLE);
                complete = false;
                new AddFriend().execute();
                timer.run();
            }
        });

        friendList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(fList.get(position)[0].replace("_"," "),friendIds.get(position));
                return false;
            }
        });


        new GetUser().execute();
        searchBtn.setEnabled(false);

        timer = new Runnable(){
            @Override
            public void run(){
                if (complete) {
                    prog.setVisibility(View.GONE);
                    listAdapter.notifyDataSetChanged();
                    listContainer.setVisibility(View.VISIBLE);
                    searchBtn.setEnabled(true);

                    if (friendAddStatus == 1){
                        //refresh friend list
                        info.setText(R.string.friend_added);
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent i = new Intent(getApplicationContext(), FriendActivity.class);
                        i.putExtra("id",myId);
                        startActivity(i);
                        finish();
                    }else if (friendAddStatus == 2){
                        info.setText(R.string.invalid_uid);
                        uid.setText("");
                    }
                }
                else mHandler.postDelayed(timer, 100);
            }
        };

        timer.run();

    }

    public void showDeleteDialog(String name, final String fId){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox);
        TextView body = (TextView) dialog.findViewById(R.id.textViewMsg);
        body.setText(String.format(getResources().getString(R.string.delete_person),name));
        Button dialogButton = (Button) dialog.findViewById(R.id.button1);
        dialogButton.setVisibility(View.VISIBLE);
        dialogButton.setText(R.string.delete);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RemoveFriend().execute(fId);
                dialog.dismiss();
            }
        });
        Button dialogButton2 = (Button) dialog.findViewById(R.id.button2);
        dialogButton2.setVisibility(View.VISIBLE);
        dialogButton2.setText(R.string.close);
        dialogButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private class GetUser extends AsyncTask<String, String, String> {

        private  String url_get_user = "http://"+IPADRS+"/get_user.php";
        private  String url_get_rank = "http://"+IPADRS+"/get_rank.php";
        private  String url_get_friends = "http://"+IPADRS+"/get_friends.php";

        @Override
        protected String doInBackground(String... params) {

Log.d("progress","starting friends search");
            JSONParser jsonParser = new JSONParser();
            JSONArray friends;
            List<NameValuePair> param0 = new ArrayList<>();
            param0.add(new BasicNameValuePair("id", myId));
            JSONObject json0 = jsonParser.makeHttpRequest(url_get_friends,"POST", param0);
            try {
                int success = json0.getInt("success");
                if (success == 1) {
                    friends = json0.getJSONArray("users");
                    for (int i=0; i<friends.length(); i++) {
                        friendIds.add(friends.getString(i));
                    }
                } else {
                    Log.e("database error","no friends");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            List<NameValuePair> param = new ArrayList<>();
            JSONObject json, json2;
            for (String uid : friendIds) {
                Log.d("progress","getting rank for a friend------------");
                param.clear();
                param.add(new BasicNameValuePair("id", uid));

                //Getting user rank
                json2 = jsonParser.makeHttpRequest(url_get_rank, "POST", param);
                try {
                    int success = json2.getInt("success");
                    if (success == 1) {
                        rank = (int) Double.parseDouble(json2.getString("message"));
                    } else {
                        Log.e("database error", "missing rank data");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("progress","getting data for a friend");

                // Getting user data
                JSONArray user;
                json = jsonParser.makeHttpRequest(url_get_user, "POST", param);
                try {
                    int success = json.getInt("success");
                    if (success == 1) {
                        user = json.getJSONArray("users");
                        JSONObject c = user.getJSONObject(0);
                        String[] myInf = new String[6];
                        myInf[0] = (c.getString("name"));
                        myInf[1] = (c.getString("level"));
                        myInf[2] = (c.getString("average"));
                        myInf[3] = (c.getString("tpoints"));
                        myInf[4] = (rank + "");
                        myInf[5] = (c.getString("pic"));
                        fList.add(myInf);
                    } else {
                        Log.e("database error", "missing user data");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            complete = true;
            return null;
        }
    }


    private class AddFriend extends AsyncTask<String, String, String> {

        private  String url_add_friend = "http://"+IPADRS+"/add_friend.php";

        @Override
        protected String doInBackground(String... params) {

            if(searchText.isEmpty() || friendIds.contains(searchText)){
                Log.e("input error", "invalid input");
                friendAddStatus = 2;
                complete = true;
            }
            else{
                JSONParser jsonParser = new JSONParser();
                List<NameValuePair> param0 = new ArrayList<>();
                param0.add(new BasicNameValuePair("myId", myId));
                param0.add(new BasicNameValuePair("friendId", searchText));
                JSONObject json0 = jsonParser.makeHttpRequest(url_add_friend,"POST", param0);
                try {
                    int success = json0.getInt("success");
                    if (success == 1) {
                        friendAddStatus = 1 ;
                        complete = true;
                    } else if (success ==2) {
                        Log.e("database error",json0.getString("message"));
                        friendAddStatus = 2;
                        complete = true;
                    }else{
                        Log.e("database error",json0.getString("message"));
                        friendAddStatus = 3;
                        complete = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }


    private class RemoveFriend extends AsyncTask<String, String, String> {

        private  String url_remove_friend = "http://"+IPADRS+"/remove_friend.php";

        @Override
        protected String doInBackground(String... params) {

                String friendId = params[0];

                JSONParser jsonParser = new JSONParser();
                List<NameValuePair> param0 = new ArrayList<>();
                param0.add(new BasicNameValuePair("myId", myId));
                param0.add(new BasicNameValuePair("friendId", friendId));
                JSONObject json0 = jsonParser.makeHttpRequest(url_remove_friend,"POST", param0);
                try {
                    int success = json0.getInt("success");
                    if (success == 1) {
                        Intent i = new Intent(getApplicationContext(), FriendActivity.class);
                        i.putExtra("id",myId);
                        startActivity(i);
                        finish();
                    } else{
                        Log.e("database error",json0.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            return null;
        }
    }
}

