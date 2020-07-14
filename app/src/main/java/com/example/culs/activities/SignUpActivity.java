package com.example.culs.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.culs.R;
import com.example.culs.helpers.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignUpActivity extends AppCompatActivity {

    EditText txtfirstname, txtlastname, txtcollegeid, txtyearid;
    TextView signupbtn;

    // Firebase instance variable
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseFirestore db;

    private String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        txtfirstname = (EditText) findViewById(R.id.first);
        txtlastname = (EditText) findViewById(R.id.last);
        txtcollegeid = (EditText) findViewById(R.id.college);
        txtyearid = (EditText) findViewById(R.id.year);
        signupbtn = (TextView) findViewById(R.id.signupbtn);

        //gets current instance of the database
        mFirebaseAuth = FirebaseAuth.getInstance();

        //setting a textView as a button
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Logic

                //convert all textViews to string
                String first_name = txtfirstname.getText().toString().trim();
                String last_name = txtlastname.getText().toString().trim();
                String college_id = txtcollegeid.getText().toString().trim();
                String year_id = txtyearid.getText().toString().trim();

                User users = new User(first_name, last_name, college_id, year_id);
                db.getInstance().collection("users").document(userid).set(users)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot written with ID: ");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                finish();

            }
        });

}
}