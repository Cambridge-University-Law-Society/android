package com.example.culs.activities;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.culs.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Timer;
import java.util.logging.LogRecord;

public class OpeningTitlePage extends Activity {

    // Firebase instance variable
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mFirebaseDatabaseReference;
    private GoogleSignInClient mSignInClient;
    private GoogleSignInClient mGoogleSignInClient;

    private static final String TAG = "OPENING";


    @Override
    protected void onStart() {
        super.onStart();
        //firebaseAuth.addAuthStateListener(authStateListener);
        //firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //firebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        /*mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                Log.d(TAG, "onAuthStateChanged:" + firebaseUser);
                if (firebaseUser == null) {
                    Intent intent = new Intent(OpeningTitlePage.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };*/

        // User is signed in
        // Start home activity
        Thread background = new Thread(){
                public void run() {
                    try {
                        // Thread will sleep for 5 seconds
                        sleep((long) (1*1000));

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
