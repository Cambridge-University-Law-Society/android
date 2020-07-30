package com.example.culs.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.culs.activities.LoginActivity;
import com.example.culs.activities.MainActivity;
import com.example.culs.activities.ProfileEditActivity;
import com.example.culs.helpers.GlideApp;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private TextView username, userCrsid, userBio, userCollege, userYear, userGradYear, userInterests, admin;
    private TextView signout_btn;
    private CircleImageView profileImage;
    private TextView edit_btn;

    //Firebase Instance variables - add them when you need them and explain their function

    //you can get an instance of the authentication by doing .getInstance()
    private FirebaseAuth mFirebaseAuth;
    //this will get the current instance of the document in cloud firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    //this retrieves the entire document with this specific uid
    private DocumentReference docRef = db.collection("users").document(userid);

    private String TAG = "ProfileFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_profile, container, false);

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        //call signout function
        signOut(v);

        //call loadData function
        try {
            loadData(v);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO set an intent to send pressing the edit button to the ProfileEditActivity with a fade transition
        edit_btn = v.findViewById(R.id.edit_button);
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        return v;

    }


    public void signOut(View v) {
        //SIGN OUT METHOD
        signout_btn = v.findViewById(R.id.sign_out);
        signout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    public void loadData(View v) throws IOException {
        //this function will load in the current data of the user in the Firebase Database

        //current textView data
        final TextView username = (TextView) v.findViewById(R.id.user_name);
        final TextView userCrsid = (TextView) v.findViewById(R.id.crsid);
        final TextView userBio = (TextView) v.findViewById(R.id.bio);
        final TextView userCollege = (TextView) v.findViewById(R.id.user_college);
        final TextView userYear = (TextView) v.findViewById(R.id.user_year);
        final TextView userDegree = (TextView) v.findViewById(R.id.user_degree);
        final TextView admin = (TextView) v.findViewById(R.id.admin_mode);

        //method for loading textView data

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){

                            if (documentSnapshot.get("firstname") != null && documentSnapshot.get("lastname") != null) {
                                String first_name = documentSnapshot.getString("firstname");
                                String last_name = documentSnapshot.getString("lastname");

                                username.setText(first_name + " " + last_name);
                            }else{
                                username.setText("Username Here");
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

                            if (documentSnapshot.get("status").equals("admin")){
                                admin.setVisibility(View.VISIBLE);
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
        final CircleImageView profilePic = (CircleImageView) v.findViewById(R.id.profile_image);
        //profileImage = v.findViewById(R.id.profile_image);
        fetchImage(profilePic);

        //TODO: Load in the list of interests

    }

    private void fetchImage(final CircleImageView image) throws IOException {
        //using glide method

        //Reference to an image file in cloud storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a reference with an initial file path and name
        StorageReference pathReference = storageRef.child("users/"+userid+"/profilePic.jpg");
        String downloadUrl = pathReference.toString();

        //try to download to a local file
        final File file = File.createTempFile("profilePic", "jpg");
        pathReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                GlideApp.with(ProfileFragment.this).load(file).into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error in loading", Toast.LENGTH_SHORT).show();
            }
        });

        //GlideApp.with(this.getContext()).load(pathReference).into(image);

    }

}