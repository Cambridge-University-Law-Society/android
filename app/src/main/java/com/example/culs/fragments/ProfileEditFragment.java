package com.example.culs.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.culs.R;
import com.example.culs.activities.MainActivity;
import com.example.culs.activities.ProfileEditActivity;
import com.example.culs.helpers.GlideApp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditFragment extends Fragment {

    private EditText firstName, lastName, userCrsid, userBio, userYear, userInterests;
    private Spinner userCollege, userDegree;
    private CircleImageView profileImage;
    private TextView save_btn;
    private TextView cancel_btn;
    private TextView editProfileImage;

    private static final int PICK_IMAGE_REQUEST =  1; //this is for the picture file intent in openFileChooser

    private Uri imageUri; //this is a uri (similar to url) that points to the image and uploads it to the firebase storage
    //for uploading profilepic to firebase
    private StorageReference storageRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference firebaseDatabaseReference;
    private StorageTask uploadTask;

    //this will get the current instance of the document in cloud firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    //this retrieves the entire document with this specific uid - needed to update the profile information
    private DocumentReference docRef = db.collection("users").document(userid);


    private String TAG = "ProfileEditFragment";
    private Fragment ProfileEditActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        final View v = inflater.inflate(R.layout.activity_profile_edit, container, false);
        v.setBackgroundColor(Color.WHITE);

        storageRef = FirebaseStorage.getInstance().getReference("users/" + userid);
        if(mDatabase==null){
            //mDatabase = FirebaseDatabase.getInstance();
            //mDatabase.setPersistenceEnabled(true);
            firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("users/" + userid);
        }


        profileImage = v.findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when the image is pressed, an intent will be sent to open the images file for user to pick a new picture which will show on the page
                openFileChooser();
            }
        });

        editProfileImage = v.findViewById(R.id.edit_profile_button);
        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when the image is pressed, an intent will be sent to open the images file for user to pick a new picture which will show on the page
                openFileChooser();
            }
        });

        //SPINNER STUFF
        final Spinner userCollege = v.findViewById(R.id.edit_college_spinner);
        final Spinner userDegree = v.findViewById(R.id.edit_degree_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.college_names, R.layout.list_item);
        ArrayAdapter<CharSequence> adapter_degree = ArrayAdapter.createFromResource(getContext(), R.array.degree_names, R.layout.list_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.list_item_2);
        adapter_degree.setDropDownViewResource(R.layout.list_item_2);
        // Apply the adapter to the spinner
        userCollege.setAdapter(adapter);
        userDegree.setAdapter(adapter_degree);

        save_btn = v.findViewById(R.id.button_done);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this will upload the new profile picture to firebase storage
                if (uploadTask != null){
                    Toast.makeText(getActivity(), "Upload in Progress", Toast.LENGTH_SHORT).show();
                }else {
                    uploadFile();
                    updateData(userCollege, userDegree, v);
                }

                sendToProfileFragment();

                //Intent intent = new Intent(getActivity(), MainActivity.class);
                //startActivity(intent);
                //finish();

                //goToFragment();
                //getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, new ProfileFragment()).commit();
                //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        cancel_btn = v.findViewById(R.id.cancel_button);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToProfileFragment();
                //goToFragment();
                //getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, new ProfileFragment()).commit();
                //Intent intent = new Intent(ProfileEditActivity.this, MainActivity.class);
                //startActivity(intent);
                //finish();
                //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        try {
            loadCurrentData(adapter, userCollege, v);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return v;
    }


    private void sendToProfileFragment(){
        Fragment nextFragment = new ProfileFragment();
        Fragment currentFragment = ProfileEditActivity;

        //Bundle bundle = new Bundle();
        //nextFragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //Fragment fragment = (Fragment) fragmentManager.findFragmentById(R.id.frame_layout);
        //fragmentTransaction.replace(R.id.fragment_container2,new HomeFragment());
        fragmentTransaction.replace(R.id.fragment_container2, nextFragment);
        //fragmentTransaction.detach(currentFragment);
        //fragmentTransaction.attach(nextFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private void loadCurrentData(final ArrayAdapter<CharSequence> adapter, final Spinner mSpinner, View v) throws IOException {
        //load the current data from firebase into the edit text views

        final EditText firstName = (EditText) v.findViewById(R.id.first_name);
        final EditText lastName = (EditText) v.findViewById(R.id.last_name);
        final EditText userCrsid = (EditText) v.findViewById(R.id.crsid);
        final EditText userBio = (EditText) v.findViewById(R.id.bio);
        //final TextView userCollege = (TextView) findViewById(R.id.selected_college);
        final EditText userYear = (EditText) v.findViewById(R.id.edit_useryear);
        //final EditText userDegree = (EditText) findViewById(R.id.edit_degree);

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
                            }

                            if (documentSnapshot.get("bio") != null) {
                                String bio = documentSnapshot.getString("bio");

                                userBio.setText(bio);
                            }else{
                            }

                            if (documentSnapshot.get("college") != null) {
                                String college = documentSnapshot.getString("college");

                                if (college != null){
                                    int spinnerPosition = adapter.getPosition(college);
                                    mSpinner.setSelection(spinnerPosition);
                                }

                                //userCollege.setText(college);
                            }else{
                            }

                            if (documentSnapshot.get("year") != null) {
                                String year = documentSnapshot.getString("year");

                                userYear.setText(year);
                            }else{
                            }


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


        //set the profile picture from firebase
        profileImage = v.findViewById(R.id.profile_image);
        fetchImage(profileImage);


    }

    private void fetchImage(final CircleImageView image) throws IOException {
        //using glide method

        //Reference to an image file in cloud storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final BaseRequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);

        // Create a reference with an initial file path and name
        StorageReference pathReference = storageRef.child("users/"+userid+"/profilePic");

        //try to download to a local file
        /*final File file = File.createTempFile("profilePic", "jpg");
        pathReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                GlideApp.with(getActivity()).load(file).into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error in loading", Toast.LENGTH_SHORT).show();
            }
        });*/
        Glide.with(getActivity()).load(pathReference).placeholder(R.drawable.noprofilepicture).apply(requestOptions).fitCenter().into(image);
        /*pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String userProfileImageUri = uri.toString();
                //GlideApp.with(getContext()).load(userProfileImageUri).placeholder(R.drawable.ic_profile_icon_24dp).fitCenter().into(image);
                Glide.with(getContext()).load(userProfileImageUri).placeholder(R.drawable.noprofilepicture).apply(requestOptions).fitCenter().into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //GlideApp.with(getContext()).load(R.drawable.ic_profile_icon_24dp).placeholder(R.drawable.ic_profile_icon_24dp).fitCenter().into(image);
                Glide.with(getContext()).load(R.drawable.noprofilepicture).placeholder(R.drawable.noprofilepicture).apply(requestOptions).fitCenter().into(image);
            }
        });*/

    }

    private void updateData(Spinner mSpinner, Spinner mSpinner2, View v) {
        //TODO: MAKE THIS CODE BETTER
        //this will update the document with the information in the editText boxes

        //DONE: Add the updated editview stuff here
        //initalise the view first
        firstName = (EditText) v.findViewById(R.id.first_name);
        lastName = (EditText) v.findViewById(R.id.last_name);
        userCrsid = (EditText) v.findViewById(R.id.crsid);
        userBio = (EditText) v.findViewById(R.id.bio);
        //userCollege = (EditText) findViewById(R.id.edit_college);
        userYear = (EditText) v.findViewById(R.id.edit_useryear);

        //convert all textViews to string
        String first_name = firstName.getText().toString().trim();
        String last_name = lastName.getText().toString().trim();
        String user_crsid = userCrsid.getText().toString().trim();
        String user_bio = userBio.getText().toString().trim();
        //String user_college = userCollege.getText().toString().trim();
        String user_year = userYear.getText().toString().trim();

        //get the value from the spinner
        String user_college = mSpinner.getSelectedItem().toString();
        String user_degree = mSpinner2.getSelectedItem().toString();

        docRef.update("firstname", first_name);
        docRef.update("lastname", last_name);
        docRef.update("crsid", user_crsid);
        docRef.update("bio", user_bio);
        docRef.update("college", user_college);
        docRef.update("year", user_year);
        docRef.update("degree", user_degree);
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*"); //ensures we only see images in our file chooser
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
        ContentResolver cR = getActivity().getApplicationContext().getContentResolver();
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
                            //Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            //Toast.makeText(ProfileEditActivity.this, "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }
}
