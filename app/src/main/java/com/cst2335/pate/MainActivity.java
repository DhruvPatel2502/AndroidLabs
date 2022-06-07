package com.cst2335.pate;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_constraint);

        Button iBtn = findViewById(R.id.button);
        iBtn.setOnClickListener( btn -> Toast.makeText(this, this.getString(R.string.Toast), LENGTH_LONG).show());


        SwitchCompat swt = findViewById(R.id.Switch);

        swt.setOnCheckedChangeListener((cb, b) -> {
            String snackMessageT = this.getResources().getString(R.string.snackmessageT);
            String snackMessageF = this.getResources().getString(R.string.snackmessageF);

            if(b){
                Snackbar.make(swt,snackMessageT, LENGTH_SHORT).setAction(this.getString(R.string.undo), click ->cb.setChecked(!b)).show();
            }else{
                Snackbar.make(swt,snackMessageF, LENGTH_SHORT).setAction(this.getString(R.string.undo), click ->cb.setChecked(!b)).show();
            }
        });



}
}