package com.camlawsocapp.culs.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.camlawsocapp.culs.R;
import com.camlawsocapp.culs.helpers.User;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class SignUpActivity extends AppCompatActivity {

    EditText txtfirstname, txtlastname, txtyearid, txtdegree, txtgradyearid;
    TextView privacyPolicy;
    CardView signupbtn;
    Spinner txtcollegeid, txtdegreeid;
    CircleImageView profilePic;
    CheckBox checkBox;

    String personName, personGivenName, personFamilyName, personEmail, personId;

    private static final int PICK_IMAGE_REQUEST =  1; //this is for the picture file intent in openFileChooser

    private Uri imageUri; //this is a uri (similar to url) that points to the image and uploads it to the firebase storage
    //for uploading profilepic to firebase
    private StorageReference storageRef;
    private DatabaseReference firebaseDatabaseReference;
    private StorageTask uploadTask;

    // Firebase instance variable
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    //private DatabaseReference mFirebaseDatabaseReference;

    //private FirebaseFirestore db;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    StorageReference photoReference= storageReference.child("users/DefaultProfilePic/ic_profile_icon_24dp.xml");
    private String defaultProfilePic = "users/DefaultProfilePic/ic_profile_icon_24dp.xml";

    //this will get the current instance of the document in cloud firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    //this retrieves the entire document with this specific uid - needed to update the profile information
    private DocumentReference docRef = db.collection("users").document(userid);

    private String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final Spinner userCollege = findViewById(R.id.college);
        final Spinner userDegree = findViewById(R.id.degree);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.college_names, R.layout.list_item);
        ArrayAdapter<CharSequence> adapter_degree = ArrayAdapter.createFromResource(this, R.array.degree_names, R.layout.list_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        adapter_degree.setDropDownViewResource(android.R.layout.simple_list_item_1);
        // Apply the adapter to the spinner
        userCollege.setAdapter(adapter);
        userDegree.setAdapter(adapter_degree);

        checkBox = findViewById(R.id.checkbox);

        privacyPolicy = findViewById(R.id.privacy_policy_hyperlink);
        privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

        storageRef = FirebaseStorage.getInstance().getReference("users/" + userid);

        txtfirstname = (EditText) findViewById(R.id.first);
        txtlastname = (EditText) findViewById(R.id.last);
        txtyearid = (EditText) findViewById(R.id.year);
        txtgradyearid = (EditText) findViewById(R.id.grad_year);
        //txtdegree = (EditText) findViewById(R.id.degree);
        signupbtn = (CardView) findViewById(R.id.signupbtn);
        profilePic = findViewById(R.id.original_profile_image);

        Uri personPhoto;

        //this gets the current users details
        /*GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            personPhoto = acct.getPhotoUrl();

            if(personPhoto!= null){
                GlideApp.with(this).load(personPhoto).into(profilePic);
                uploadFile(personPhoto);
            }
        }*/

        //privacyPolicy = findViewById(R.id.privacy_policy_hyperlink);
        //privacyPolicy.setText("https://www.culs.org.uk/privacy-policy");

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when the image is pressed, an intent will be sent to open the images file for user to pick a new picture which will show on the page
                openFileChooser();
                uploadFile();
            }
        });

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
                //String college_id = txtcollegeid.getText().toString().trim();
                String year_id = txtyearid.getText().toString().trim();
                String grad_year_id = txtgradyearid.getText().toString().trim();
                //String degree_id = txtdegree.getText().toString().trim();

                //get the value from the spinner
                String user_college = userCollege.getSelectedItem().toString();
                String user_degree = userDegree.getSelectedItem().toString();

                if(first_name.isEmpty() || last_name.isEmpty() || year_id.isEmpty() || !checkBox.isChecked() || grad_year_id.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please fill out all the fields", Toast.LENGTH_SHORT).show();
                }else if (!checkBox.isChecked()){
                    Toast.makeText(getApplicationContext(), "Please check the tick box", Toast.LENGTH_SHORT).show();
                }
                else{
                    User users = new User(first_name, last_name, user_college, year_id, grad_year_id, null, userid, null, null, "user",  user_degree, null);
                    docRef.set(users)
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

                    uploadFile();
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    finish();
                }

            }
        });

    }

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
            Picasso.get().load(imageUri).into(profilePic);
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
            StorageReference fileReference = storageRef.child("profilePic");//+ getFileExtension(imageUri)

            //this puts the file into storage
            uploadTask = fileReference.putFile(imageUri)
                    //then we need to try add the url to the database
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            //Toast.makeText(ProfileEditActivity.this, "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }
}