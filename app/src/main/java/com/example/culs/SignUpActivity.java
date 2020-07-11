package com.example.culs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.MalformedInputException;


public class SignUpActivity extends AppCompatActivity {

    EditText txtfirstname, txtlastname, txtcollegeid, txtyearid;
    TextView signupbtn;

    // Firebase instance variable
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseFirestore db;

    SignUpActivity(FirebaseFirestore db){
        this.db = db;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txtfirstname = (EditText) findViewById(R.id.firstname);
        txtlastname = (EditText) findViewById(R.id.lastname);
        txtcollegeid = (EditText) findViewById(R.id.collegeid);
        txtyearid = (EditText) findViewById(R.id.yearid);
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
                db.collection("users").add(users);


                // Add a new document with a generated id.
                //db.collection("users").add({
                        //firstname: first_name,
                        //lastname: last_name,
                        //college: college_id,
                        //year: year_id
            //})

                //Data data = new Data(...);
                //db.collection("users").add(data)

            }
        });

}
}