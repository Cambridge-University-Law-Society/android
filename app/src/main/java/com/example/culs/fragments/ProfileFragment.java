package com.example.culs.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.culs.R;
import com.example.culs.activities.LoginActivity;
import com.example.culs.activities.ProfileEditActivity;
import com.example.culs.helpers.InterestsAdapter;
import com.example.culs.helpers.InterestsModel;
import com.example.culs.helpers.MyInterestsAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements InterestsAdapter.OnNoteListener {
    private TextView username, userCrsid, userBio, userCollege, userYear, userGradYear, userInterests, admin;
    private TextView signout_btn, noInterests;
    private CircleImageView profileImage;
    private TextView edit_btn;
    private GoogleSignInClient mSignInClient;
    private ImageView add_interests_btn, done_interests_btn;

    //Firebase Instance variables - add them when you need them and explain their function

    //you can get an instance of the authentication by doing .getInstance()
    private FirebaseAuth mFirebaseAuth;
    //this will get the current instance of the document in cloud firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    //this retrieves the entire document with this specific uid
    private DocumentReference docRef = db.collection("users").document(userid);

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;
    List<String> myInterests;
    ArrayList<String> mInterests;


    //GridLayout stuff
    private GridLayoutManager gridLayoutManager, all_gridLayoutManager;
    private RecyclerView.Adapter recyclerAdapter, all_recyclerAdapter;
    private RecyclerView recyclerView, all_recyclerView;
    private ArrayList<InterestsModel> interestsModel;
    private ArrayList<InterestsModel> myInterestsList;


    private String TAG = "ProfileFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_profile, container, false);

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        noInterests = (TextView) v.findViewById(R.id.no_interests);

        recyclerView = (RecyclerView) v.findViewById(R.id.interests_recycler);
        recyclerView.setHasFixedSize(true);

        all_recyclerView = (RecyclerView) v.findViewById(R.id.all_interests_recycler);

        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        all_gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        all_recyclerView.setLayoutManager(all_gridLayoutManager);
        all_recyclerView.setItemAnimator(new DefaultItemAnimator());

        interestsModel = new ArrayList<InterestsModel>();
        myInterestsList = new ArrayList<InterestsModel>(); //creates a list of all the interests

        /*for (int i = 0; i < DataSet.all_interests.size(); i++){
            data.add(new InterestsModel(
               DataSet.all_interests.get(i)
            ));
        }
        recyclerAdapter = new InterestsAdapter(data);
        recyclerView.setAdapter(recyclerAdapter);*/

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        myInterests = (List<String>) document.get("interests");
                        //final ArrayList<String> list = new ArrayList<String>();
                        if (myInterests == null||myInterests.isEmpty()){
                            //interestsModel.add(new InterestsModel("You don't have any interests"));
                            noInterests.setVisibility(View.VISIBLE);
                        }else{
                            for (int i = 0; i < myInterests.size(); ++i) {
                                noInterests.setVisibility(View.GONE);
                                interestsModel.add(new InterestsModel(myInterests.get(i)));
                                myInterestsList.add(new InterestsModel(myInterests.get(i)));
                            }
                        }

                        //GridAdapter gridAdapter = new GridAdapter(getContext(), list);
                        //gridView.setAdapter(gridAdapter);

                        /*//this is for the list view
                        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);
                        myInterestsList.setAdapter(adapter);
                        setListViewHeight(myInterestsList);*/

                        initMyRecyclerView();

                        Log.d(TAG, interestsModel.toString());
                    }
                } else{
                    Toast.makeText(getContext(), "didn't work", Toast.LENGTH_SHORT).show();
                }
            }
        });



        //inflate new recyclerview on add buttons press
        add_interests_btn = (ImageView) v.findViewById(R.id.add_interests_button);
        done_interests_btn = (ImageView) v.findViewById(R.id.done_interests_button);

        add_interests_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestsModel.clear();
                for (int i = 0; i < DataSet.all_interests.size(); i++){
                    interestsModel.add(new InterestsModel(
                            DataSet.all_interests.get(i)
                    ));
                }


                initAllRecyclerView();
                noInterests.setVisibility(View.GONE);
                done_interests_btn.setVisibility(View.VISIBLE);
                add_interests_btn.setVisibility(View.INVISIBLE);

            }
        });

        done_interests_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                myInterests = (List<String>) document.get("interests");
                                interestsModel.clear();
                                //final ArrayList<String> list = new ArrayList<String>();
                                if (myInterests == null||myInterests.isEmpty()){
                                    //interestsModel.add(new InterestsModel("You don't have any interests"));
                                    noInterests.setVisibility(View.VISIBLE);
                                }else{
                                    for (int i = 0; i < myInterests.size(); ++i) {
                                        noInterests.setVisibility(View.GONE);
                                        interestsModel.add(new InterestsModel(myInterests.get(i)));
                                    }
                                }

                                //GridAdapter gridAdapter = new GridAdapter(getContext(), list);
                                //gridView.setAdapter(gridAdapter);

                                /*//this is for the list view
                                final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);
                                myInterestsList.setAdapter(adapter);
                                setListViewHeight(myInterestsList);*/

                                initMyRecyclerView();
                                done_interests_btn.setVisibility(View.INVISIBLE);
                                add_interests_btn.setVisibility(View.VISIBLE);
                                Log.d(TAG, interestsModel.toString());
                            }
                        } else{
                            Toast.makeText(getContext(), "didn't work", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


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
                /*Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);*/
                Fragment nextFragment = new ProfileEditFragment();

                Bundle bundle = new Bundle();
                nextFragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, nextFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return v;

    }

    public void initAllRecyclerView(){
        all_recyclerAdapter = new InterestsAdapter(interestsModel, this);
        all_recyclerView.setAdapter(all_recyclerAdapter);
        all_recyclerAdapter.notifyDataSetChanged();
        all_recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    public void initMyRecyclerView(){
        recyclerAdapter = new MyInterestsAdapter(interestsModel);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
        all_recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onNoteClick(int position, CardView cardView, TextView textView) {
        //here do the things you want to do on a click event
        //e.g create an intent to go somewhere
        String one_interest = interestsModel.get(position).getInterests(); //this gets a reference to the object that is pressed
        Log.d(TAG, "onNoteClick: " + String.valueOf(one_interest));
        if (cardView.getCardBackgroundColor() == ColorStateList.valueOf(getResources().getColor(R.color.colorAccent))){
            cardView.setCardBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
            docRef.update("interests", FieldValue.arrayRemove(one_interest));
        }
        else{
            cardView.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
            textView.setTextColor(Color.WHITE);
            docRef.update("interests", FieldValue.arrayUnion(one_interest));
        }
        //cardView.setCardBackgroundColor(Color.BLACK);
    }

    public static class DataSet {

        static ArrayList<String> all_interests = new ArrayList<>(Arrays.asList(
                "Administrative Law",
                "Aspects of Obligations",
                "Civil Law",
                "Civil Law II",
                "Constitutional Law",
                "Contract Law",
                "Criminal Law",
                "Commercial Law",
                "Company Law",
                "Comparative Law",
                "Competition Law",
                "Conficts of Laws",
                "CPE",
                "CSPS",
                "Equity",
                "European Union Law",
                "Family Law",
                "Human Rights Law",
                "Intellectual Property",
                "International Law",
                "Jurisprudence",
                "Labour Law",
                "Land Law",
                "Legal History",
                "Tort Law"
        ));

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
                FirebaseMessaging.getInstance().unsubscribeFromTopic(userid)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Removed from My Events.", Toast.LENGTH_SHORT).show();
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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
        //final ListView myInterestsList = (ListView) v.findViewById(R.id.myInterestsList);

        //final GridView gridView = (GridView) v.findViewById(R.id.interestsGridView);

        //set up the adapter
        //adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, myInterests);
        //myInterestsList.setAdapter(adapter);

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

                        if (documentSnapshot.get("crsid") == null||documentSnapshot.get("crsid").toString().equals("")) {
                            userCrsid.setText("crsid");

                        } else{
                            String crsid = documentSnapshot.getString("crsid");

                            userCrsid.setText(crsid);
                        }

                        if (documentSnapshot.get("bio") == null||documentSnapshot.get("bio").toString().equals("")) {
                            userBio.setText("Add a description about yourself, including interests and ambitions.");

                        }else{
                            String bio = documentSnapshot.getString("bio");

                            userBio.setText(bio);
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

                        /*if (documentSnapshot.get("status").equals("admin")){
                            admin.setVisibility(View.VISIBLE);
                        }*/

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
        final BaseRequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);

        // Create a reference with an initial file path and name
        StorageReference pathReference = storageRef.child("users/"+userid+"/profilePic");
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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