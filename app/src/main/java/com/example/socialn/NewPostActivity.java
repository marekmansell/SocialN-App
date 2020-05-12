package com.example.socialn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewPostActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String base_64_image = "";
    public static final int PICK_IMAGE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imageView.setImageBitmap(imageBitmap);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            base_64_image = encoded;

            Log.d("ABCDEF", encoded);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        final String access_token = pref.getString("access_token", null);


        Button newPostUploadImgBtn = (Button) findViewById(R.id.newPostUploadImgBtn);
        newPostUploadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

//                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                getIntent.setType("image/*");
//
//                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                pickIntent.setType("image/*");
//
//                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
//
//                startActivityForResult(chooserIntent, PICK_IMAGE);

            }
        });


        Button newPostSubmitBtn = (Button) findViewById(R.id.newPostSubmitBtn);
        newPostSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OkHttpClient client = new OkHttpClient();

                String url = "http://mtaa.marekmansell.sk/v1/post/";

                JSONObject post_json = new JSONObject();
                JSONObject post_json_data = new JSONObject();

                EditText postText = (EditText) findViewById(R.id.editText);

                try {
                    post_json_data.put("content", postText.getText().toString());
                    post_json_data.put("photo", base_64_image);
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
                        .post(body)
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

                                    TextView newPostErr = (TextView) findViewById(R.id.newPostErr);
                                    newPostErr.setVisibility(View.VISIBLE);

                                }
                            });

                        }

                    }
                });

            }
        });

    }
}
