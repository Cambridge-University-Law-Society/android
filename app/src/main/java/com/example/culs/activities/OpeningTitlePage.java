package com.example.culs.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.culs.R;

import java.util.Timer;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class OpeningTitlePage extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        Thread background = new Thread(){
            public void run() {
                try {
                    // Thread will sleep for 5 seconds
                    sleep((long) (0.5*1000));

                    // After 5 seconds redirect to another intent
                    Intent i=new Intent(getBaseContext(),MainActivity.class);
                    startActivity(i);

                    //Remove activity
                    finish();
                } catch (Exception e) {
                }
            }
        };
        background.start();

    }

}
