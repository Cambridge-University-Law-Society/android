package com.example.culs.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.culs.fragments.ProfileFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.culs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditActivity extends AppCompatActivity {

    private EditText firstName, lastName, userCrsid, userBio, userCollege, userYear, userDegree, userInterests;
    private CircleImageView profileImage;
    private TextView save_btn;
    private TextView cancel_btn;

    private static final int PICK_IMAGE_REQUEST =  1; //this is for the picture file intent in openFileChooser

    private Uri imageUri; //this is a uri (similar to url) that points to the image and uploads it to the firebase storage
    //for uploading profilepic to firebase
    private StorageReference storageRef;
    private DatabaseReference firebaseDatabaseReference;
    private StorageTask uploadTask;

    //this will get the current instance of the document in cloud firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    //this retrieves the entire document with this specific uid - needed to update the profile information
    private DocumentReference docRef = db.collection("users").document(userid);


    private String TAG = "ProfileEditFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        loadCurrentData();

        storageRef = FirebaseStorage.getInstance().getReference("users/" + userid);
        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("users/" + userid);

        profileImage = findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when the image is pressed, an intent will be sent to open the images file for user to pick a new picture which will show on the page
                openFileChooser();
            }
        });

        save_btn = findViewById(R.id.button_done);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this will upload the new profile picture to firebase storage
                if (uploadTask != null){
                    Toast.makeText(ProfileEditActivity.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
                }else {
                    uploadFile();
                    updateData();
                }

                Intent intent = new Intent(ProfileEditActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        cancel_btn = findViewById(R.id.cancel_button);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileEditActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

    }

    private void loadCurrentData(){
        //load the current data from firebase into the edit text views

        final EditText firstName = (EditText) findViewById(R.id.first_name);
        final EditText lastName = (EditText) findViewById(R.id.last_name);
        final EditText userCrsid = (EditText) findViewById(R.id.crsid);
        final EditText userBio = (EditText) findViewById(R.id.bio);
        final EditText userCollege = (EditText) findViewById(R.id.edit_college);
        final EditText userYear = (EditText) findViewById(R.id.edit_useryear);
        final EditText userDegree = (EditText) findViewById(R.id.edit_degree);

        //method for loading textView data

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){

                            if (documentSnapshot.get("firstname") != null && documentSnapshot.get("lastname") != null) {
                                String first_name = documentSnapshot.getString("firstname");
                                String last_name = documentSnapshot.getString("lastname");

                                firstName.setText(first_name);
                                lastName.setText(last_name);
                            }else{
                                firstName.setText("First Name Here");
                                lastName.setText("Last Name here");
                            }

                            if (documentSnapshot.get("crsid") != null) {
                                String crsid = documentSnapshot.getString("crsid");

                                userCrsid.setText(crsid);
                            }else{
                                userCrsid.setText("CRSID");
                            }

                            if (documentSnapshot.get("bio") != null) {
                                String bio = documentSnapshot.getString("bio");

                                userBio.setText(bio);
                            }else{
                                userBio.setText("Add a description about yourself, including interests and ambitions.");
                            }

                            if (documentSnapshot.get("college") != null) {
                                String college = documentSnapshot.getString("college");

                                userCollege.setText(college);
                            }else{
                                userCollege.setText("Select a College");
                            }

                            if (documentSnapshot.get("year") != null) {
                                String year = documentSnapshot.getString("year");

                                userYear.setText(year);
                            }else{
                                userBio.setText("Your Year");
                            }

                            if (documentSnapshot.get("degree") != null) {
                                String degree = documentSnapshot.getString("degree");

                                userDegree.setText(degree);
                            }else{
                                userDegree.setText("Your Degree");
                            }

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileEditActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });


        //set the profile picture from firebase
        profileImage = findViewById(R.id.profile_image);
        fetchImage(profileImage);


    }

    private void fetchImage(ImageView image){
        //using glide method

        //Reference to an image file in cloud storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a reference with an initial file path and name
        StorageReference pathReference = storageRef.child("users/"+userid+"/profilePic.jpg");

        Glide.with(this).load(pathReference).into(image);

    }

    private void updateData() {
        //TODO: MAKE THIS CODE BETTER
        //this will update the document with the information in the editText boxes

        //TODO: Add the updated editview stuff here
        //initalise the view first
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        userCrsid = (EditText) findViewById(R.id.crsid);
        userBio = (EditText) findViewById(R.id.bio);
        userCollege = (EditText) findViewById(R.id.edit_college);
        userYear = (EditText) findViewById(R.id.edit_useryear);
        userDegree = (EditText) findViewById(R.id.edit_degree);

        //convert all textViews to string
        String first_name = firstName.getText().toString().trim();
        String last_name = lastName.getText().toString().trim();
        String user_crsid = userCrsid.getText().toString().trim();
        String user_bio = userBio.getText().toString().trim();
        String user_college = userCollege.getText().toString().trim();
        String user_year = userYear.getText().toString().trim();
        String user_degree = userDegree.getText().toString().trim();

        docRef.update("firstname", first_name);
        docRef.update("lastname", last_name);
        docRef.update("crsid", user_crsid);
        docRef.update("bio", user_bio);
        docRef.update("college", user_college);
        docRef.update("year", user_year);
        docRef.update("degree", user_degree);

    }

    //TODO: Add method to upload and change the profile picture

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*"); //ensures we only see images in our file chooser
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null & data.getData() != null){
            imageUri = data.getData();

            //IF WE WANTED TO LOAD THE PICTURE FROM THE FILE INTO THE IMAGE VIEW WE WOULD DO THIS
            //native way of putting in an image into an imageview
            //mProfilePic.setImageURI(imageUri);

            //using picasso
            Picasso.get().load(imageUri).into(profileImage);
        }
    }

    private String getFileExtension(Uri uri){
        //this function gets the file extension of the image (e.g. it will get .jpg)
        ContentResolver cR = this.getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //this will upload the picture file selected to firebase storage
    private void uploadFile(){
        if (imageUri != null){
            //set the picture name to profilePic
            StorageReference fileReference = storageRef.child("profilePic."+ getFileExtension(imageUri));

            //this puts the file into storage
            uploadTask = fileReference.putFile(imageUri)
                    //then we need to try add the url to the database
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ProfileEditActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                            if (taskSnapshot.getMetadata()!= null){
                                if (taskSnapshot.getMetadata().getReference()!=null){
                                    String result = taskSnapshot.getStorage().getPath();
                                    //updates the database with this --> THIS MIGHT NOT BE NEEDED
                                    docRef.update("profilePicRef", result);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(ProfileEditActivity.this, "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }
}