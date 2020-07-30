package com.example.culs.fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.culs.R;
import com.example.culs.helpers.Card;
import com.example.culs.helpers.CardAdapter;
import com.example.culs.helpers.CardHolder;
import com.example.culs.helpers.SponsorAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;


import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.SimpleDateFormat;

import java.util.Date;

public class HomeFragment extends Fragment implements CardHolder.OnCardListener {
    private RecyclerView eventsView;
    private SwipeRefreshLayout swipeContainer;
    String[] friends = {"friend 1", "friend 2", "friend 3"};
    int[] friendPics3 = {R.drawable.mc_durks, R.drawable.mc_durks, R.drawable.mc_durks};
    int[] friendPics2 = {R.drawable.mc_durks, R.drawable.mc_durks};
    int[] friendPics1 = {R.drawable.mc_durks};
    int[] friendPics0 = {};
    Boolean[] boolCase1 = {false, true, false, false, false};
    Boolean[] boolCase2 = {true, false, false, false, false};
    Boolean[] boolCase3 = {false, true, true, false, false};
    Boolean[] boolCase4 = {false, true, true, true, false};
    Boolean[] boolCase5 = { true, true, true, true, true};
    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yy HH:mm");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference noteRef = db.collection("Events").document("gPel12YOlfp6t7mG2g1I");
    private final ArrayList<Card> cards = new ArrayList<Card>();
    private ListenerRegistration noteListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        setHasOptionsMenu(true);//Make sure you have this line of code.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setEnterTransition(new android.transition.Slide());
            setExitTransition(new android.transition.Slide());
        }

        final CardAdapter adapter = new CardAdapter(getActivity() , R.layout.card_item, cards, this);
        // Create a list of words
        loadNote(adapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNote(adapter);
            }
        });
//        cards.add(new Card("Mojitos with Baker McKenzie", "Sunday 30th October: 22:00 - 3:00", hehelong, "Vinyl, Sidney St", "This is an event that is hosted by Baker McKenzie every year and is always lots of fun. There will be an open bar and representatives to talk to. ", R.drawable.allbarone, friends, friendPics1, boolCase5));
//        cards.add(new Card("Buffet and Bellinis", "Thursday 3rd November: 22:00 - 3:00", 12543, "Lola Lo's, Corn Exchange St", "This will be another great event at NOVI, hosted by Macfarlanes. The night will start with some light cocktails, including Bellinis at the bar and some substantial canapés. There will be representatives from Macfarlanes to talk to throughout the night who will be able to provide an interesting insight into life as a lawyer.", R.drawable.lawevent2, friends, friendPics2, boolCase3));
//        cards.add(new Card("Engineering and Law Lolas Night", "Saturday 18th June: 18:00 - 6:00", 12543, "Queens' College Cambridge, Silver St", "This is going to be a very fun event in Lola’s with the Engineering Society. There will be some a discount at the door for members!", R.drawable.lolas, friends, friendPics2, boolCase4));
//        cards.add(new Card("Burritos and Mojitos", "Saturday 18th June: 18:00 - 6:00", 12543, "Queens' College Cambridge, Silver St", "Fun night planned at the classic bar Ta Bouche. There will be free mojitos as well as plenty of other drinks at the bar.", R.drawable.lawevent1, friends, friendPics2, boolCase1));

        adapter.notifyDataSetChanged();
        eventsView = (RecyclerView) rootView.findViewById(R.id.list);
        eventsView.setAdapter(adapter);
        eventsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Toolbar myToolbar = rootView.findViewById(R.id.my_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_search_icon_24dp);// set drawable icon
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return rootView;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        noteListener = noteRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
//                if(error != null){
//                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if(documentSnapshot.exists()) {
//                    Boolean eventActive = documentSnapshot.getBoolean("Active");
//                    if (eventActive) {
//                        String eventName = documentSnapshot.getString("name");
//                        Timestamp timestamp = documentSnapshot.getTimestamp("date");
//                        Long utcTimestamp = timestamp.getSeconds();
//                        String eventDate = sdf.format(utcTimestamp);
//                        String eventLocation = documentSnapshot.getString("location");
//                        String eventDescription = documentSnapshot.getString("description");
//                        cards.add(new Card(eventName, eventDate, utcTimestamp, eventLocation, eventDescription, R.drawable.allbarone, friends, friendPics1, boolCase5));
//                        Toast.makeText(getActivity(), eventName, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
//    }

//    @Override
//    public void onStop(){
//        super.onStop();
//        noteListener.remove();
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.app_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCardClick(View v, int position, ArrayList<Card> cardList) {
        Card currentCard = cardList.get(position);

        Fragment nextFragment = new ExpandedEventFragment();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nextFragment.setSharedElementEnterTransition(new DetailsTransition());
            nextFragment.setEnterTransition(new android.transition.Fade());
            nextFragment.setExitTransition(new android.transition.Fade());
            nextFragment.setSharedElementReturnTransition(new DetailsTransition());
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable("Current Card", currentCard);
        nextFragment.setArguments(bundle);
        // Step 2: Create an Intent to start the next Activity

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addSharedElement(v.findViewById(R.id.event_pic_image_view), "expandedImage");
        fragmentTransaction.addSharedElement(v.findViewById(R.id.event_description_text_view), "expandedDescrip");
        fragmentTransaction.addSharedElement(v.findViewById(R.id.event_name_text_view), "expandedName");
        fragmentTransaction.addSharedElement(v.findViewById(R.id.event_date_and_time_text_view), "expandedDNT");
        fragmentTransaction.addSharedElement(v.findViewById(R.id.event_location_text_view), "expandedLoc");
        fragmentTransaction.addSharedElement(v.findViewById(R.id.event_tag_holder), "expandedTagHold");
        fragmentTransaction.addSharedElement(v.findViewById(R.id.tag_icon), "expandedTagIcon");
        fragmentTransaction.addSharedElement(v.findViewById(R.id.tag_note), "expandedTagNote");
        fragmentTransaction.replace(R.id.fragment_container, nextFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void loadNote(final CardAdapter adapter){

        swipeContainer.setRefreshing(true);
        noteRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            Boolean eventActive = documentSnapshot.getBoolean("Active");
                            if(eventActive) {
                                String eventName = documentSnapshot.getString("name");
                                String eventImage = documentSnapshot.getString("ImageRef");
                                Timestamp timestamp = documentSnapshot.getTimestamp("date");
                                Long utcTimestamp = timestamp.getSeconds();
                                String eventDate = sdf.format(utcTimestamp);
                                String eventLocation = documentSnapshot.getString("location");
                                String eventDescription = documentSnapshot.getString("description");
                                cards.add(new Card(eventName, eventDate, utcTimestamp, eventLocation, eventDescription, eventImage, friends, friendPics1, boolCase5));
                                Toast.makeText(getActivity(), eventImage, Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                                return;
                            }
                        } else {
                            Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    }
                });
        swipeContainer.setRefreshing(false);
        return;
    }

}
