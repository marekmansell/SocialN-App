package com.example.socialn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.YELLOW;

public class FeedActivity extends AppCompatActivity {

    public static final String POST_ID = "com.example.socialn.POST_ID";
    public static final String POST_CONTENT = "com.example.socialn.POST_CONTENT";


    private void setOnClick(final Button btn, final String str){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                final String access_token = pref.getString("access_token", null);

                OkHttpClient client = new OkHttpClient();
                String url = "http://mtaa.marekmansell.sk/v1/post/" + str;
                MediaType JSON_type = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON_type, "");

                final Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + access_token)
                        .addHeader("Content-Type", "application/json")
                        .delete(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        Log.d("ABCDEF", String.valueOf(response.code()));
                        if (response.code() == 204) {
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);

                        } else {

                        }

                    }
                });

            }
        });
    }

    private void setOnClickUpdate(final Button btn, final String post_id, final String post_content){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), editPostActivity.class);
                intent.putExtra(POST_ID, post_id);
                intent.putExtra(POST_CONTENT, post_content);
                startActivity(intent);

            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String access_token = pref.getString("access_token", null);

        final OkHttpClient client = new OkHttpClient();


        final String url = "http://mtaa.marekmansell.sk/v1/post/";

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + access_token)
                .addHeader("Content-Type", "application/json")
                .build();


        Button feedNewPostBtn = (Button) findViewById(R.id.feedNewPostBtn);
        feedNewPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), NewPostActivity.class);
                startActivity(startIntent);
            }
        });


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();
                Log.d("ABCDEF", String.valueOf(response.code()));

                if (response.code() == 200) {

                    try {
                        JSONObject responseJSON = new JSONObject(myResponse);
                        final JSONArray postsJSON = responseJSON.getJSONArray("posts");
                        for (int i = 0; i < postsJSON.length(); i++) {
                            Log.d("ABCDEF", postsJSON.getJSONObject(i).toString());
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                LinearLayout ll = (LinearLayout)findViewById(R.id.feedLayout);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                for (int i = postsJSON.length() - 1; i >= 0; i--) {
                                    TextView postHeaderTextView = new TextView(getApplicationContext());
                                    try {
                                        postHeaderTextView.setText(postsJSON.getJSONObject(i).getString("users.formatted_name").toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    postHeaderTextView.setTextColor(BLACK);
                                    postHeaderTextView.setTextSize(30);
                                    ll.addView(postHeaderTextView, lp);

                                    TextView postContentTextView = new TextView(getApplicationContext());
                                    try {
                                        postContentTextView.setText(postsJSON.getJSONObject(i).getString("posts.content").toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    postContentTextView.setTextColor(BLACK);
                                    postContentTextView.setTextSize(20);
                                    ll.addView(postContentTextView, lp);

                                    ImageView postImage = new ImageView(getApplicationContext());
                                    try {

                                        byte[] decodedString = Base64.decode(postsJSON.getJSONObject(i).getString("posts.photo").toString(), Base64.DEFAULT);
                                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                                        postImage.setImageBitmap(decodedByte);
                                        if (decodedByte != null) {
                                            postImage.setImageBitmap(Bitmap.createScaledBitmap(decodedByte, decodedByte.getWidth()*5, decodedByte.getHeight()*5, false));
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.d("ABCDEF", "Err");
                                    }
                                    ll.addView(postImage, lp);

                                    Button delBtn = new Button(getApplicationContext());
                                    delBtn.setText("DELETE");
                                    try {
                                        setOnClick(delBtn, String.valueOf(postsJSON.getJSONObject(i).getString("posts.id").toString()));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ll.addView(delBtn, lp);


                                    Button editBtn = new Button(getApplicationContext());
                                    editBtn.setText("UPDATE");
                                    try {
                                        setOnClickUpdate(editBtn, String.valueOf(postsJSON.getJSONObject(i).getString("posts.id").toString()), String.valueOf(postsJSON.getJSONObject(i).getString("posts.content").toString()));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ll.addView(editBtn, lp);
                                }


                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {

                }

            }
        });










//        for (int i = 0; i < 5; i++) {
//            TextView postHeaderTextView = new TextView(this);
//            postHeaderTextView.setText("Andrejko Uže" + String.valueOf(i));
//            postHeaderTextView.setTextColor(BLACK);
//            postHeaderTextView.setTextSize(30);
//            ll.addView(postHeaderTextView, lp);
//
//            TextView postContentTextView = new TextView(this);
//            postContentTextView.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam");
//            postContentTextView.setTextColor(BLACK);
//            postContentTextView.setTextSize(20);
//            ll.addView(postContentTextView, lp);
//        }

    }
}
