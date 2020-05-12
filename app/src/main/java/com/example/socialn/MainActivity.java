package com.example.socialn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String POST_ID = "com.example.socialn.POST_ID";
    public static final String POST_CONTENT = "com.example.socialn.POST_CONTENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button  loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText loginUserText = (EditText) findViewById(R.id.loginUserText);
                EditText loginPassword = (EditText) findViewById(R.id.loginPassword);


                OkHttpClient client = new OkHttpClient();


                String url = "http://mtaa.marekmansell.sk/v1/hello";

                final Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization",
                                   Credentials.basic(loginUserText.getText().toString(),
                                                     loginPassword.getText().toString()))
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() == 200) {
                            final String myResponse = response.body().string();

                            try {
                                JSONObject responseJSON = new JSONObject(myResponse);
                                String access_token = responseJSON.getString("access_token");

                                Log.d("ABCDEF", "New token: " + access_token);

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("access_token", access_token);
                                editor.commit();

                                Intent startIntent = new Intent(getApplicationContext(), FeedActivity.class);
                                startActivity(startIntent);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else {

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    TextView loginWrongTextView = (TextView) findViewById(R.id.loginWrongTextView);
                                    loginWrongTextView.setVisibility(View.VISIBLE);

                                }
                            });


                            Log.d("ABCDEF", "Unsuccessful login");
                        }

                    }
                });

            }
        });
    }
}
