package com.blackstar.math4brain;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SendMessage extends AppCompatActivity {

    String TAG = "SendMessage", name, email, message;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            email = extras.getString("email");
        }

        setContentView(R.layout.message);
        Button submit = findViewById(R.id.msgSend);
        final EditText text = findViewById(R.id.msgBox);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = safeString(text.getText().toString());
                sendMessage();
                Toast.makeText(getApplicationContext(),"Message Sent", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

    public void sendMessage(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://amensah.com/kokotoa/contact-form-handler.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("message", message);
                return params;
            }
        };
        queue.add(postRequest);

    }

    public String safeString(String str){
        str = str.replace("\0","0").replace("\'","&qt").replace(
                "\"","&qt2").replace("\b","").replace(
                        "\\","&slash");

        return str;
    }
}
