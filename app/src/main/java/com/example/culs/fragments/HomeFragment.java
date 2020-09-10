package com.example.culs.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.culs.R;
import com.example.culs.helpers.AppBarStateChangeListener;
import com.example.culs.activities.MainActivity;
import com.example.culs.helpers.Card;
import com.example.culs.helpers.CustomAdapter;
import com.example.culs.helpers.Post;
import com.example.culs.helpers.PostType;
import com.example.culs.helpers.User;
import com.firebase.ui.auth.data.model.State;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.ToLongBiFunction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeFragment extends Fragment {
    private RecyclerView eventsView;
    private View rootView;
    private CustomAdapter customAdapter;
    private FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
    private List<PostType> types = new ArrayList<>();
    private String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private DocumentReference userDocRef = mFirebaseFirestore.collection("users").document(userID);
    public static User currentUser;

    private AppBarLayout appBarLayout1,appBarLayout2;
    private Toolbar toolbar, loadedToolbar;
    private int shortAnimationDuration;
    private RelativeLayout relativeLayout;
    private CardView loadedCardView, searchBar;
    private SearchView searchView;
    private ImageView notificationsImageBtn;
    private Query events = mFirebaseFirestore.collection("Events").orderBy("date", Query.Direction.ASCENDING);
    private Query posts = mFirebaseFirestore.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING);
    ListenerRegistration eventsReg;
    ListenerRegistration postsReg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        setupCustomAdapter(rootView);
        //setupToolbarOptionsMenu(rootView);
        setupCollapsingToolbar(rootView);


        notificationsImageBtn = (ImageView) rootView.findViewById(R.id.notifications_btn);
        notificationsImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment nextFragment = new NotificationsFragment();

                Bundle bundle = new Bundle();
                nextFragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, nextFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setSearchView(rootView);
        types.clear();
        getListItems();
        customAdapter.notifyDataSetChanged();
        }

    public void onStop() {
        super.onStop();
        eventsReg.remove();
        //postsReg.remove();
    }


    private void setSearchView(View rootView){
        searchView = rootView.findViewById(R.id.search_bar);
        searchView.setQueryHint("Search");
        EditText editText = (EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setTextColor(Color.WHITE);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        customAdapter.getFilter().filter(newText);
                        if (newText== null || newText.length() == 0){
                            types.clear();
                            getListItems();
                            customAdapter.notifyDataSetChanged();
                        }
                        return false;
                    }
                });
            }
        });
    }


    private void setupCollapsingToolbar(View rootView){
        appBarLayout1 = (AppBarLayout) rootView.findViewById(R.id.appBarLayout);
        relativeLayout = rootView.findViewById(R.id.relative_layout);
        loadedCardView = rootView.findViewById(R.id.final_card_view);
        searchBar = rootView.findViewById(R.id.searchbar);
        loadedToolbar = rootView.findViewById(R.id.final_toolbar);

        appBarLayout1.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float percentage = ((float)Math.abs(verticalOffset)/appBarLayout.getTotalScrollRange());
                Log.d("PERC", String.valueOf(percentage));
                loadedToolbar.setAlpha(percentage);
                searchBar.setAlpha(1-percentage);
            }
        });
    }

    private void setupCustomAdapter(View rootView) {
        eventsView = (RecyclerView) rootView.findViewById(R.id.list);
        eventsView.setHasFixedSize(true);
        eventsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        customAdapter = new CustomAdapter(types);
        eventsView.setAdapter(customAdapter);
        customAdapter.setOnEventItemClickListener(new CustomAdapter.OnEventItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Card clickedCard = (Card) types.get(position);
                onCardClick(v, clickedCard);
            }

            @Override
            public void onInterestedClick(View v, int position) {
                Card intCard = (Card) types.get(position);
                if(HomeFragment.currentUser.getMyevents() == null){
                    userDocRef.update("myevents", FieldValue.arrayUnion(intCard.getID()));
                    ((Card) types.get(position)).setInterested(true);
                    customAdapter.notifyItemChanged(position);

                    FirebaseMessaging.getInstance().subscribeToTopic(intCard.getID())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getContext(), "Added to My Events.", Toast.LENGTH_SHORT).show();
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else if(currentUser.getMyevents().contains(intCard.getID())){
                    userDocRef.update("myevents", FieldValue.arrayRemove(intCard.getID()));
                    ((Card) types.get(position)).setInterested(false);
                    customAdapter.notifyItemChanged(position);

                    FirebaseMessaging.getInstance().unsubscribeFromTopic(intCard.getID())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getContext(), "Removed from My Events.", Toast.LENGTH_SHORT).show();
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    userDocRef.update("myevents", FieldValue.arrayUnion(intCard.getID()));
                    ((Card) types.get(position)).setInterested(true);
                    customAdapter.notifyItemChanged(position);

                    FirebaseMessaging.getInstance().subscribeToTopic(intCard.getID())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getContext(), "Added to My Events.", Toast.LENGTH_SHORT).show();
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void getListItems() {

        userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println("Listen failed: " + error);
                    return;
                }

                if (value != null && value.exists()) {
                    currentUser = value.toObject(User.class);
                } else {
                    System.out.print("Current data: null");
                }
            }
        });

        eventsReg = events.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            System.err.println("Listen failed:" + error);
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    final Card cardAdded = dc.getDocument().toObject(Card.class);
                                    cardAdded.setID(dc.getDocument().getId());
                                    if(currentUser.getMyevents() == null){
                                        cardAdded.setInterested(false);
                                    } else if(currentUser.getMyevents().contains(dc.getDocument().getId())){
                                        cardAdded.setInterested(true);
                                    } else {
                                        cardAdded.setInterested(false);
                                    }

                                    mFirebaseFirestore.collection("Sponsors")
                                            .whereEqualTo("name", cardAdded.getSponsor())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            cardAdded.setEventSponsorID(document.getId());
                                                        }
                                                    } else {
                                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    if (dc.getDocument().getBoolean("active")) {
                                        if (!dc.getDocument().getBoolean("archived")) {
                                            types.add(dc.getNewIndex(), cardAdded);
                                        }
                                    }
                                    break;
                                case MODIFIED:
                                    final Card cardChanged = dc.getDocument().toObject(Card.class);
                                    cardChanged.setID(dc.getDocument().getId());

                                    for(int i = 0; i < types.size(); i++){
                                        if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                            Card testCard = (Card) types.get(i);
                                            if(testCard.getID().equals(cardChanged.getID())){
                                                types.remove(i);
                                                customAdapter.notifyItemRemoved(i);
                                            }
                                        }
                                    }

                                    if(currentUser.getMyevents() == null){
                                        cardChanged.setInterested(false);
                                    } else if(currentUser.getMyevents().contains(dc.getDocument().getId())){
                                        cardChanged.setInterested(true);
                                    } else {
                                        cardChanged.setInterested(false);
                                    }

                                    mFirebaseFirestore.collection("Sponsors")
                                            .whereEqualTo("name", cardChanged.getSponsor())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            cardChanged.setEventSponsorID(document.getId());
                                                        }
                                                    } else {
                                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    if (dc.getDocument().getBoolean("active")) {
                                        if (!dc.getDocument().getBoolean("archived")) {
                                            types.add(cardChanged);
                                        }
                                    }
                                    break;
                                case REMOVED:
                                    final Card cardRemoved = dc.getDocument().toObject(Card.class);
                                    cardRemoved.setID(dc.getDocument().getId());

                                    for(int i = 0; i < types.size(); i++){
                                        if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                            Card testCard = (Card) types.get(i);
                                            if(testCard.getID().equals(cardRemoved.getID())){
                                                types.remove(i);
                                                customAdapter.notifyItemRemoved(i);
                                            }
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        Collections.sort(types);
                        customAdapter.notifyDataSetChanged();
                    }
                });

        /*postsReg = posts.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            System.err.println("Listen failed:" + error);
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                        final Post postAdded = dc.getDocument().toObject(Post.class);
                                        postAdded.setPostID(dc.getDocument().getId());

                                        DocumentReference docRefAdded = mFirebaseFirestore.collection("users").document(postAdded.getSenderID());
                                        docRefAdded.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                User postSender = documentSnapshot.toObject(User.class);
                                                postAdded.setSenderName(postSender.getFirstname() + " " + postSender.getLastname());
                                            }
                                        });
                                    if (!dc.getDocument().getBoolean("archived")) {
                                        types.add(postAdded);
                                    }
                                    break;
                                case MODIFIED:

                                        final Post postChanged = dc.getDocument().toObject(Post.class);
                                        postChanged.setPostID(dc.getDocument().getId());

                                        for (int i = 0; i < types.size(); i++) {
                                            if (types.get(i).getPostType() == PostType.TYPE_POST) {
                                                Post testPost = (Post) types.get(i);
                                                if (testPost.getPostID().equals(postChanged.getPostID())) {
                                                    types.remove(i);
                                                    customAdapter.notifyItemRemoved(i);
                                                }
                                            }
                                        }

                                        DocumentReference docRefChanged = mFirebaseFirestore.collection("users").document(postChanged.getSenderID());
                                        docRefChanged.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                User postSender = documentSnapshot.toObject(User.class);
                                                postChanged.setSenderName(postSender.getFirstname() + " " + postSender.getLastname());
                                            }
                                        });
                                    if (!dc.getDocument().getBoolean("archived")) {
                                        types.add(postChanged);
                                    }
                                    break;
                                case REMOVED:
                                    final Post postRemoved = dc.getDocument().toObject(Post.class);
                                    postRemoved.setPostID(dc.getDocument().getId());

                                    for (int i = 0; i < types.size(); i++) {
                                        if (types.get(i).getPostType() == PostType.TYPE_POST) {
                                            Post testPost = (Post) types.get(i);
                                            if (testPost.getPostID().equals(postRemoved.getPostID())) {
                                                types.remove(i);
                                                customAdapter.notifyItemRemoved(i);
                                            }
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }

                        }
                        Collections.sort(types);
                        customAdapter.notifyDataSetChanged();
                    }
                });*/
    }


    private void setupToolbarOptionsMenu(View rootView) {
        setHasOptionsMenu(true);
        Toolbar myToolbar = rootView.findViewById(R.id.final_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_culs_top_logo_invert);// set drawable icon
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void onCardClick(View v, Card currentCard) {

        Fragment nextFragment = new ExpandedEventFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Current Card ID", currentCard.getID());
        nextFragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, nextFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
