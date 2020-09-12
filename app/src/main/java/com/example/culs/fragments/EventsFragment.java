package com.example.culs.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.culs.R;
import com.example.culs.helpers.Card;
import com.example.culs.helpers.CustomAdapter;
import com.example.culs.helpers.Post;
import com.example.culs.helpers.PostType;
import com.example.culs.helpers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventsFragment extends Fragment {
    private RecyclerView eventsView;
    private View rootView;
    private CustomAdapter customAdapter;
    private FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
    private List<PostType> types = new ArrayList<>();
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DocumentReference userDocRef = mFirebaseFirestore.collection("users").document(userID);
    private Query events = mFirebaseFirestore.collection("Events").whereArrayContains("attendees", HomeFragment.currentUser.getUid()).orderBy("date", Query.Direction.ASCENDING);
    ListenerRegistration eventsReg;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_events, container, false);
        setupCustomAdapter(rootView);
        //setupToolbarOptionsMenu(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        types.clear();
        getListItems();
        customAdapter.notifyDataSetChanged();
    }

    public void onStop() {
        super.onStop();
    }

    private void setupCustomAdapter(View rootView) {
        eventsView = (RecyclerView) rootView.findViewById(R.id.myevent_list);
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
                    customAdapter.notifyDataSetChanged();
                } else if(HomeFragment.currentUser.getMyevents().contains(intCard.getID())){
                    userDocRef.update("myevents", FieldValue.arrayRemove(intCard.getID()));
                    HomeFragment.currentUser.getMyevents().remove(intCard.getID());
                    ((Card) types.get(position)).setInterested(false);
                    types.remove(position);
                    customAdapter.notifyItemRemoved(position);
                } else {
                    userDocRef.update("myevents", FieldValue.arrayUnion(intCard.getID()));
                    ((Card) types.get(position)).setInterested(true);
                    customAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void getListItems() {

//        final List<DocumentReference> interestedDocRefs = new ArrayList<>();
//        interestedDocRefs.clear();
//
//        if(HomeFragment.currentUser.getMyevents() != null) {
//            for (int i = 0; i < HomeFragment.currentUser.getMyevents().size(); i++) {
//                DocumentReference currentInterest;
//                currentInterest = mFirebaseFirestore.collection("Events").document(HomeFragment.currentUser.getMyevents().get(i));
//                interestedDocRefs.add(currentInterest);
//            }
//        }
//
//        for (int i = 0; i < interestedDocRefs.size(); i++) {
//            final int finalI = i;
//            interestedDocRefs.get(i)
//                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                            if (error != null) {
//                                System.err.println("Listen failed: " + error);
//                                return;
//                            }
//
//                            if (value != null && value.exists()) {
//                                Card interestingCard = new Card();
//                                interestingCard = value.toObject(Card.class);
//                                interestingCard.setID(HomeFragment.currentUser.getMyevents().get(finalI));
//                                interestingCard.setInterested(true);
//                                types.add(interestingCard);
//                            } else {
//                                System.out.print("Current data: null");
//                            }
//                            Collections.sort(types);
//                            customAdapter.notifyDataSetChanged();
//                        }
//                    });
//        }

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
                            if(HomeFragment.currentUser.getMyevents() == null){
                                cardAdded.setInterested(false);
                            } else if(HomeFragment.currentUser.getMyevents().contains(dc.getDocument().getId())){
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
                                    types.add(cardAdded);
                                    Collections.sort(types);
                                    int index = 0;

                                    for(int i = 0; i < types.size(); i++){
                                        if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                            Card testCard = (Card) types.get(i);
                                            if(testCard.getID().equals(cardAdded.getID())){
                                                index = i;
                                            }
                                        }
                                    }
                                    customAdapter.notifyItemInserted(index);
                                } else {
                                    for(int i = 0; i < types.size(); i++){
                                        if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                            Card testCard = (Card) types.get(i);
                                            if(testCard.getID().equals(cardAdded.getID())){
                                                types.remove(i);
                                                customAdapter.notifyItemRemoved(i);
                                                Collections.sort(types);
                                            }
                                        }
                                    }
                                }
                            } else {
                                for(int i = 0; i < types.size(); i++){
                                    if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                        Card testCard = (Card) types.get(i);
                                        if(testCard.getID().equals(cardAdded.getID())){
                                            types.remove(i);
                                            customAdapter.notifyItemRemoved(i);
                                            Collections.sort(types);
                                        }
                                    }
                                }
                            }
                            break;
                        case MODIFIED:
                            final Card cardChanged = dc.getDocument().toObject(Card.class);
                            cardChanged.setID(dc.getDocument().getId());
                            int index = 0;

                            for(int i = 0; i < types.size(); i++){
                                if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                    Card testCard = (Card) types.get(i);
                                    if(testCard.getID().equals(cardChanged.getID())){
                                        types.remove(i);
                                        index = i;
                                    }
                                }
                            }

                            if(HomeFragment.currentUser.getMyevents() == null){
                                cardChanged.setInterested(false);
                            } else if(HomeFragment.currentUser.getMyevents().contains(dc.getDocument().getId())){
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
                                    types.add(index, cardChanged);
                                    customAdapter.notifyItemChanged(index);
                                    Collections.sort(types);
                                } else {
                                    for(int i = 0; i < types.size(); i++){
                                        if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                            Card testCard = (Card) types.get(i);
                                            if(testCard.getID().equals(cardChanged.getID())){
                                                types.remove(i);
                                                customAdapter.notifyItemRemoved(i);
                                                Collections.sort(types);
                                            }
                                        }
                                    }
                                }
                            } else {
                                for(int i = 0; i < types.size(); i++){
                                    if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                        Card testCard = (Card) types.get(i);
                                        if(testCard.getID().equals(cardChanged.getID())){
                                            types.remove(i);
                                            customAdapter.notifyItemRemoved(i);
                                            Collections.sort(types);
                                        }
                                    }
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
                                        Collections.sort(types);
                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.app_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setupToolbarOptionsMenu(View rootView) {
        setHasOptionsMenu(true);
        /*Toolbar myToolbar = rootView.findViewById(R.id.my_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_search_icon_24dp);// set drawable icon
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
    }

    public void onCardClick(View v, Card currentCard) {

        Fragment nextFragment = new ExpandedEventFragment();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nextFragment.setSharedElementEnterTransition(new DetailsTransition());
            nextFragment.setEnterTransition(new android.transition.Fade());
            nextFragment.setExitTransition(new android.transition.Fade());
            nextFragment.setSharedElementReturnTransition(new DetailsTransition());
        }

        Bundle bundle = new Bundle();
        bundle.putString("Current Card ID", currentCard.getID());
        nextFragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addSharedElement(v.findViewById(R.id.event_pic_image_view), "expandedImage");
        fragmentTransaction.replace(R.id.fragment_container, nextFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}

//    private void setupFireAdapter(View rootView) {
//        eventsView = (RecyclerView) rootView.findViewById(R.id.list);
//        eventsView.setHasFixedSize(true);
//        eventsView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        Query query = FirebaseFirestore.getInstance().collection("Events").orderBy("date", Query.Direction.ASCENDING);
//        FirestoreRecyclerOptions<Card> options = new FirestoreRecyclerOptions.Builder<Card>().setQuery(query, Card.class).build();
//        fire_adapter = new FireRecyclerAdapter(options);
//        eventsView.setAdapter(fire_adapter);
//    }