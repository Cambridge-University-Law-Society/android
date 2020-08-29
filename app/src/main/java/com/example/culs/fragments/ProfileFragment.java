package com.example.culs.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.culs.R;
import com.example.culs.activities.LoginActivity;
import com.example.culs.activities.ProfileEditActivity;
import com.example.culs.helpers.GlideApp;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private TextView username, userCrsid, userBio, userCollege, userYear, userGradYear, userInterests, admin;
    private TextView signout_btn;
    private CircleImageView profileImage;
    private TextView edit_btn;
    private GoogleSignInClient mSignInClient;
    //private ListView myInterestsList;

    //Firebase Instance variables - add them when you need them and explain their function

    //you can get an instance of the authentication by doing .getInstance()
    private FirebaseAuth mFirebaseAuth;
    //this will get the current instance of the document in cloud firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    //this retrieves the entire document with this specific uid
    private DocumentReference docRef = db.collection("users").document(userid);

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;
    List<String> myInterests;


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
        edit_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        return v;

    }


    public void onCreate(@Nullable Bundle savedInstanceState, LayoutInflater inflater, ViewGroup container) throws IOException {
        super.onCreate(savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        loadData(v);
    }

    public void signOut(View v) {
        //SIGN OUT METHOD
        signout_btn = v.findViewById(R.id.sign_out);
        signout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mFirebaseAuth.getInstance().signOut();
                FirebaseAuth.getInstance().signOut();
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
        final ListView myInterestsList = (ListView) v.findViewById(R.id.myInterestsList);

        /*//set up the adapter
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, myInterests);
        myInterestsList.setAdapter(adapter);*/

        //method for loading textView data

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(error == null){
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

                        /*if (documentSnapshot.get("interests") != null){

                            myInterests = documentSnapshot.get("interests");
                                    //toObject(MyInterestsList.class).interests;
                            for (int i=0; i < myInterests.size(); i++){
                                adapter.add(myInterests.get(i));
                                adapter.notifyDataSetChanged();
                            }
                        }*/

                    }

                }
            }

        });

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        myInterests = (List<String>) document.get("interests");
                        final ArrayList<String> list = new ArrayList<String>();
                        if (myInterests == null){
                            list.add("You don't have any interests");
                        }else{
                            for (int i = 0; i < myInterests.size(); ++i) {
                            list.add(myInterests.get(i));
                            }
                        }
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list);
                        myInterestsList.setAdapter(adapter);
                        setListViewHeight(myInterestsList);
                        Log.d(TAG, list.toString());
                    }
                } else{
                    Toast.makeText(getContext(), "didn't work", Toast.LENGTH_SHORT).show();
                }
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
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String userProfileImageUri = uri.toString();
                GlideApp.with(getContext()).load(userProfileImageUri).placeholder(R.drawable.ic_profile_icon_24dp).fitCenter().into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                GlideApp.with(getContext()).load(R.drawable.ic_profile_icon_24dp).placeholder(R.drawable.ic_profile_icon_24dp).fitCenter().into(image);
            }
        });

    }

    public static void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
            }

            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}