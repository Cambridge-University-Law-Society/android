package com.example.culs.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContentResolver;

import com.bumptech.glide.Glide;
import com.example.culs.R;
import com.example.culs.activities.MainActivity;
import com.example.culs.helpers.User;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {

    TextView username, userCrsid, userBio, userCollege, userYear, userGradYear, userInterests;

    //Stuff needed to search for a photo and upload a photo - we will set the ProfilePic as a button that you press to search for a new photo

    private static final int PICK_IMAGE_REQUEST =  1; //can use any button
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private ImageView  mProfilePic;

    private Uri mImageUri; //this is a uri that points to the image and uploads it to the firebase storage

    // Firebase instance variable
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;

    private DatabaseReference mFirebaseDatabaseReference;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    private User user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    //this retrieves the entire document with this specific uid
    private DocumentReference docRef = db.collection("users").document(userid);

    private String TAG = "ProfileFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*//TODO  Add image to the profile pic from firebase

        if (user.getProfilePicRef() != null){
            String imageUrl = user.getProfilePicRef();
            if (imageUrl.startsWith("gs://")){
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(imageUrl);
                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            String downloadUrl = task.getResult().toString();
                            Glide.with(mProfilePic.getContext()).load(downloadUrl).into(mProfilePic);
                        }else {
                            Log.w(TAG, "Getting download url was not successful.",
                                    task.getException());
                        }
                    }
                });
            }else{
                Toast.makeText(getActivity(), "incorrect method", Toast.LENGTH_SHORT).show();
            }
        }else{
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_profile_icon_24dp);
            mProfilePic.setImageDrawable(myDrawable);
        }*/
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

        mProfilePic.bringToFront();
        ((View)mProfilePic.getParent()).requestLayout();

        mStorageRef = FirebaseStorage.getInstance().getReference("users/" + userid);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("users/" + userid);

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
                mProfilePic.setRotation(-90);
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploadTask != null){
                    Toast.makeText(getActivity(), "Upload in Progress", Toast.LENGTH_SHORT).show();
                }else {
                    uploadFile();
                }
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
        final ImageView mProfilePic = (ImageView) v.findViewById(R.id.profile_image);

        //TODO: Add the interests TextView to the loadData


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ userid);


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



        /*final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users/"+userid);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                   @Override
                                                                   public void onSuccess(Uri uri) {
                                                                       Log.d("URI", uri.toString());
                                                                       Picasso.get().load(uri.toString()).into(profilePic);
                                                                   }
                                                               }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "No image available", Toast.LENGTH_SHORT).show();
            }
        });*/



        /*final StorageReference storageRef = FirebaseStorage.getInstance().getReference("users/P7dcbf8ICoSYfbOjWzUGCeNcoxd2/profilePic.jpg");  //try using the getFileExtension function

        final long ONE_MEGABYTE = 50000 * 50000;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                mProfilePic.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "error in loading firebase image", Toast.LENGTH_SHORT).show();
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

    private String getFileExtension(Uri uri){
        //this function gets the file extension of the image (e.g. it will get .jpg)
        ContentResolver cR = getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        if (mImageUri != null){
             //set the picture name to profilePic
            StorageReference fileReference = mStorageRef.child("profilePic." + getFileExtension(mImageUri));

            //this puts the file the storage
            mUploadTask = fileReference.putFile(mImageUri)
                    //then we are trying to add the url to the database
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getActivity(), "Upload Successful", Toast.LENGTH_LONG).show();
                            if (taskSnapshot.getMetadata()!= null){
                                if (taskSnapshot.getMetadata().getReference()!= null){
                                    String result = taskSnapshot.getStorage().getPath();
                                    docRef.update("profilePicRef", result);
                                    /*result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            docRef.update("profilePicRef", imageUrl);
                                        }
                                    });*/
                                }
                            }
                            //instead of all that this might work instead
                            //docRef.update("profilePicRef", taskSnapshot.getStorage().getDownloadUrl());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });

        }else{
            //if the user didn't pick an image
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}




