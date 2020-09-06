package com.example.culs.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.culs.R;

import java.util.Timer;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class OpeningTitlePage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

}
