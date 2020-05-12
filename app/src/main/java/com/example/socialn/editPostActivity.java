package com.example.socialn;

import androidx.appcompat.app.AppCompatActivity;

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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class editPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String post_content = intent.getStringExtra(FeedActivity.POST_CONTENT);
        final String post_id = intent.getStringExtra(FeedActivity.POST_ID);

        // Capture the layout's TextView and set the string as its text
        TextView editPostText = (TextView) findViewById(R.id.editPostText);
        editPostText.setText(post_content);

        Button edutPostBtn = (Button) findViewById(R.id.edutPostBtn);
        edutPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OkHttpClient client = new OkHttpClient();

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                String access_token = pref.getString("access_token", null);

                String url = "http://mtaa.marekmansell.sk/v1/post/" + post_id;

                JSONObject post_json = new JSONObject();
                JSONObject post_json_data = new JSONObject();

                EditText editPostText = (EditText) findViewById(R.id.editPostText);

                try {
                    post_json_data.put("content", editPostText.getText().toString());
                    post_json.put("post", post_json_data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaType JSON_type = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON_type, String.valueOf(post_json));

                final Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + access_token)
                        .addHeader("Content-Type", "application/json")
                        .patch(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        Log.d("ABCDEF", String.valueOf(response.code()));
                        if (response.code() == 200) {
                            Intent startIntent = new Intent(getApplicationContext(), FeedActivity.class);
                            startActivity(startIntent);

                        } else {


                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

//                                    TextView newPostErr = (TextView) findViewById(R.id.newPostErr);
//                                    newPostErr.setVisibility(View.VISIBLE);

                                }
                            });

                        }

                    }
                });














            }
        });


    }
}
