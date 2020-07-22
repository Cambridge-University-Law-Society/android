package com.example.culs.fragments;

import android.app.Activity;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.culs.R;
import com.example.culs.activities.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {

    TextView username, userCrsid, userBio, userCollege, userYear, userGradYear, userInterests;

    //Stuff needed to search for a photo and upload a photo - we will set the ProfilePic as a button that you press to search for a new photo

    private static final int PICK_IMAGE_REQUEST =  1; //can use any button
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private ImageView mProfilePic;

    private Uri mImageUri; //this is a uri that points to the image and uploads it to the firebase storage

    // Firebase instance variable
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    //this retrieves the entire document with this specific uid
    private DocumentReference docRef = db.collection("users").document(userid);

    private String TAG = "ProfileFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_profile, container, false);

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        //TODO : Make the Profile Image use a switch case between a button and imageview

        mButtonChooseImage = v.findViewById(R.id.button_choose);
        mButtonUpload = v.findViewById(R.id.button_upload);
        mProfilePic = v.findViewById(R.id.profile_image);

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        loadData(v);

        return  v;

    }

    public void loadData (View v) {

        final TextView username = (TextView) v.findViewById(R.id.user_name);
        final TextView userCrsid = (TextView) v.findViewById(R.id.crsid);
        final TextView userBio = (TextView) v.findViewById(R.id.bio);
        final TextView userCollege = (TextView) v.findViewById(R.id.user_college);
        final TextView userYear = (TextView) v.findViewById(R.id.user_year);
        final TextView userGradYear = (TextView) v.findViewById(R.id.user_grad);
        //ImageView profilePic = (ImageView) v.findViewById(R.id.profile_image);

        //TODO: Add the interests TextView to the loadData


        //put the method for loading data here instead of in onCreateView
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String first_name = documentSnapshot.getString("firstname");
                            String last_name = documentSnapshot.getString("lastname");
                            String crsid = documentSnapshot.getString("crsid");
                            String bio  = documentSnapshot.getString("bio");
                            String college = documentSnapshot.getString("college");
                            String year = documentSnapshot.getString("year");
                            //ImageView profile_pic = documentSnapshot.getString("profilePicRef");

                            username.setText(first_name + " " + last_name);
                            userCrsid.setText(crsid);
                            userBio.setText(bio);
                            userCollege.setText(college);
                            userYear.setText(year);
                            userGradYear.setText("2022");

                        } else{
                            Toast.makeText(getActivity(), "Document does not  exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });


        /*mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("firstname").getValue().toString();
                String crsid = dataSnapshot.child("crsid").getValue().toString();
                String bio = dataSnapshot.child("bio").getValue().toString();
                String college = dataSnapshot.child("firstname").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*"); //ensures we only see images in our file chooser
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();


            //native way of putting in an image into an imageview
            //mProfilePic.setImageURI(mImageUri);

            //using picasso
            Picasso.get().load(mImageUri).into(mProfilePic);
        }
    }
}




