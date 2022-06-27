package com.cst2335.pate;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button saveButton;
    public final static String TAG ="MainActivity";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EMAIL = "text";


    private String Email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "In onCreate, creating the objects");
        setContentView( R.layout.activity_main );

        editText = (EditText) findViewById(R.id.edittext);

        saveButton = (Button) findViewById(R.id.button);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText(editText.getText().toString());
                Intent goToProfile = new Intent(MainActivity.this, ProfileActivity.class);
                goToProfile.putExtra("EMAIL",editText.getText().toString());
                startActivity(goToProfile);
                EditText userText = findViewById(R.id.edittext);
                String userTyped = userText.getText().toString();
                Intent nextPage = new Intent(MainActivity.this,   ProfileActivity.class );

                nextPage.putExtra("EMAIL", userTyped);
                startActivity(    nextPage  );
            }
        });


        loadData();
        updateViews();
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(EMAIL, editText.getText().toString());

        editor.apply();

        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Email = sharedPreferences.getString(EMAIL, "");


    }

    public void updateViews() {
        editText.setText(Email);

    }

    @Override //screen is visible but not responding
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "In onStart, visible but not responding");
    }

    @Override //screen is visible but not responding
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "In onResume");
    }


    @Override //not visible
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "In onStop");
    }

    @Override  //garbage collected
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "In onDestroy");
    }

    protected void onPause() {
        super.onPause();
        editText = (EditText) findViewById(R.id.edittext);
        saveData();


    }

}