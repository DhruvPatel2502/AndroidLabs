package com.cst2335.pate;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ProfileActivity extends AppCompatActivity {
    public static final String TAG = "PROFILE_ACTIVITY";
    public static String Email;
    EditText Edittext;
    ImageView imgv;
    Button chatroom;

    ActivityResultLauncher<Intent> myPictureTakerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult()
            , new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {

                        try {
                            Intent data = result.getData();

                            Bitmap imgbitmap = (Bitmap) data.getExtras().get("data");
                            imgv.setImageBitmap(imgbitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.w(TAG, "Can't output PNG");
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED)
                        Log.i(TAG, "User refused to capture a picture.");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Edittext=findViewById(R.id.edittext);
        Intent fromMain = getIntent();
        Email = fromMain.getStringExtra("EMAIL");



        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("EMAIL", Email);

        Edittext.setText(Email);
        editor.apply();

        ImageButton ib = findViewById(R.id.imageButton);
        ib.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    myPictureTakerLauncher.launch(takePictureIntent);
                }
            }
        });
        imgv = findViewById(R.id.imageButton);



         chatroom = findViewById(R.id.chatRoom);
        chatroom.setOnClickListener(click -> {
            Intent gotoChatroom = new Intent(ProfileActivity.this,ChatRoomActivity.class);
            startActivity(gotoChatroom);
        });
        Button goToWeather = findViewById(R.id.goToWeather);
        goToWeather.setOnClickListener(click ->

        {
            Intent gotoWeather = new Intent(ProfileActivity.this, WeatherForecast.class);
            startActivity(gotoWeather);
        });

    }

    @Override //screen is visible but not responding
    protected void onStart() {
        super.onStart();

        Log.e(TAG, "In onStart, visible but not responding");
    }

    @Override //screen is visible but not responding
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "In onResume");
    }


    @Override //not visible
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "In onStop");
    }

    @Override  //garbage collected
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "In onDestroy");
    }

    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

}