package com.example.culs.fragments;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.culs.R;
import com.example.culs.helpers.Card;
import com.example.culs.helpers.CustomAdapter;
import com.example.culs.helpers.GlideApp;
import com.example.culs.helpers.Notification;
import com.example.culs.helpers.PostType;
import com.example.culs.helpers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ExpandedEventFragment extends Fragment implements View.OnClickListener {
    private TextView exEventName, exEventDnT, exEventLoc, exEventDesc, exEventTagNote[] = new TextView[3], exEventSponsorName, exEventInterestedText;
    private ImageView exEventPic, exEventTagIcon[] = new ImageView[3], exEventSponsorImage, exEventInterestedIcon, backbutton;
    private LinearLayout exEventSponsorHolder, exEventInterestedHolder, exEventTagHolder[] = new LinearLayout[3];
    private View rootView;
    private Card expandedCard = new Card();
    private String cardId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SimpleDateFormat spf = new SimpleDateFormat("EEE, dd MMM 'at' HH:mm");
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DocumentReference userDocRef = db.collection("users").document(userID);
    private RecyclerView specificNotificationsView;
    private CustomAdapter customAdapter;
    private List<PostType> types = new ArrayList<>();
    public String sponsorID;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        rootView = inflater.inflate(R.layout.fragment_expanded_events, container, false);
        exEventName = rootView.findViewById(R.id.ex_event_name_text_view);
        exEventDnT = rootView.findViewById(R.id.ex_event_date_and_time_text_view);
        exEventLoc = rootView.findViewById(R.id.ex_event_location_text_view);
        exEventDesc = rootView.findViewById(R.id.ex_event_description_text_view);
        exEventPic = rootView.findViewById(R.id.ex_event_pic_image_view);
        exEventTagIcon[0] = (ImageView) rootView.findViewById(R.id.ex_tag_icon_one);
        exEventTagNote[0] = (TextView) rootView.findViewById(R.id.ex_tag_note_one);
        exEventTagIcon[1] = (ImageView) rootView.findViewById(R.id.ex_tag_icon_two);
        exEventTagNote[1] = (TextView) rootView.findViewById(R.id.ex_tag_note_two);
        exEventTagIcon[2] = (ImageView) rootView.findViewById(R.id.ex_tag_icon_three);
        exEventTagNote[2] = (TextView) rootView.findViewById(R.id.ex_tag_note_three);
        exEventTagHolder[0] = (LinearLayout) rootView.findViewById(R.id.ex_event_tag_holder_one);
        exEventTagHolder[1] = (LinearLayout) rootView.findViewById(R.id.ex_event_tag_holder_two);
        exEventTagHolder[2] = (LinearLayout) rootView.findViewById(R.id.ex_event_tag_holder_three);
        exEventSponsorImage = rootView.findViewById(R.id.ex_sponsor_logo_image_view);
        exEventInterestedIcon = rootView.findViewById(R.id.ex_event_interested_star);
        exEventInterestedText = rootView.findViewById(R.id.addtomyevents_text_view);
        exEventSponsorHolder = rootView.findViewById(R.id.ex_event_sponsor_holder);
        exEventSponsorName = rootView.findViewById(R.id.ex_sponsor_name_text_view);
        exEventInterestedHolder = rootView.findViewById(R.id.ex_event_interested_button);
        exEventInterestedHolder.setOnClickListener(this);
        exEventSponsorHolder.setOnClickListener(this);
        setupCustomAdapter(rootView);
        cardId = bundle.getString("Current Card ID");
        getCard();

        backbutton = rootView.findViewById(R.id.back_button);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                fragmentManager.popBackStack(null, 0);
            }
        });

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

    public void getCard() {
        DocumentReference docRef = db.collection("Events").document(cardId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println("Listen failed: " + error);
                    return;
                }

                if (value != null && value.exists()) {
                    expandedCard = value.toObject(Card.class);
                    expandedCard.setID(cardId);
                    db.collection("Sponsors")
                            .whereEqualTo("name", expandedCard.getSponsor())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            expandedCard.setEventSponsorID(document.getId());
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    if (HomeFragment.currentUser.getMyevents() == null) {
                        expandedCard.setInterested(false);
                    } else if (HomeFragment.currentUser.getMyevents().contains(cardId)) {
                        expandedCard.setInterested(true);
                    } else {
                        expandedCard.setInterested(false);
                    }
                    exEventName.setText(expandedCard.getName());
                    exEventDnT.setText(spf.format(expandedCard.getDate().toDate()));
                    exEventLoc.setText(expandedCard.getLocation());
                    exEventDesc.setText(expandedCard.getDescription());

                    for (int i = 0; i < 3; i++){
                        exEventTagHolder[i].setVisibility(View.INVISIBLE);
                    }

                    for (int i = 0; i < expandedCard.getTags().size(); i++) {
                        exEventTagHolder[i].setVisibility(View.VISIBLE);
                        exEventTagNote[i].setText(expandedCard.getTags().get(i));
                        switch (expandedCard.getTags().get(i)){
                            case "Careers":
                                exEventTagIcon[i].setImageResource(R.drawable.ic_careers_icon_24dp);
                                break;
                            case "Networking":
                                exEventTagIcon[i].setImageResource(R.drawable.ic_networking_icon_24dp);
                                break;
                            case "Social":
                                exEventTagIcon[i].setImageResource(R.drawable.ic_socials_icon_24dp);
                                break;
                        }
                        exEventTagHolder[i].animate().translationX(1f).setDuration(1000).setListener(null);
                    }

                    if (expandedCard.getInterested()) {
                        exEventInterestedIcon.setImageResource(R.drawable.ic_interested_button_on_24dp);
                        exEventInterestedText.setText("Remove from My Events");

                    } else {
                        exEventInterestedIcon.setImageResource(R.drawable.ic_interested_button_off_24dp);
                        exEventInterestedText.setText("Add to My Events");

                    }
                    exEventSponsorName.setText(expandedCard.getSponsor());

                    FirebaseStorage eventStorage = FirebaseStorage.getInstance();
                    StorageReference eventStorageRef = eventStorage.getReference();
                    StorageReference eventPathReference = eventStorageRef.child("Events/" + expandedCard.getID() + "/coverPhoto");
                    Glide.with(getContext()).load(eventPathReference).placeholder(R.drawable.rounded_tags).fitCenter().into(exEventPic);

                    FirebaseStorage sponsorStorage = FirebaseStorage.getInstance();
                    StorageReference sponsorStorageRef = sponsorStorage.getReference();
                    StorageReference sponsorPathReference = sponsorStorageRef.child("Sponsors/" + expandedCard.getSponsor() + "/logo.png");
                    Glide.with(getContext()).load(sponsorPathReference).placeholder(R.drawable.rounded_tags).fitCenter().into(exEventSponsorImage);

                } else {
                    System.out.print("Current data: null");
                }
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ex_event_sponsor_holder:

                if(expandedCard.getEventSponsorID() != null) {
                    Fragment nextFragment = new ExpandedSponsorsFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("Current Sponsor ID", expandedCard.getEventSponsorID());
                    nextFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, nextFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(getContext(), "No Sponsor Page", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ex_event_interested_button:
                if (HomeFragment.currentUser.getMyevents() == null) {
                    userDocRef.update("myevents", FieldValue.arrayUnion(cardId));
                    expandedCard.setInterested(true);
                    exEventInterestedIcon.setImageResource(R.drawable.ic_interested_button_on_24dp);
                    exEventInterestedText.setText("Remove from My Events");

                } else if (HomeFragment.currentUser.getMyevents().contains(cardId)) {
                    userDocRef.update("myevents", FieldValue.arrayRemove(cardId));
                    expandedCard.setInterested(false);
                    exEventInterestedIcon.setImageResource(R.drawable.ic_interested_button_off_24dp);
                    exEventInterestedText.setText("Add to My Events");

                } else {
                    userDocRef.update("myevents", FieldValue.arrayUnion(cardId));
                    expandedCard.setInterested(true);
                    exEventInterestedIcon.setImageResource(R.drawable.ic_interested_button_on_24dp);
                    exEventInterestedText.setText("Remove from My Events");

                }
                break;
            default:
                break;
        }
    }

    private void setupCustomAdapter(View rootView) {
        specificNotificationsView = (RecyclerView) rootView.findViewById(R.id.specific_notification_list);
        specificNotificationsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        customAdapter = new CustomAdapter(types);
        specificNotificationsView.setAdapter(customAdapter);
    }

    private void getListItems() {

        db.collection("notifications").whereArrayContains("receiverID", cardId).orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            System.err.println("Listen failed:" + error);
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Notification notificationAdded = dc.getDocument().toObject(Notification.class);
                                    notificationAdded.setNotificationID(dc.getDocument().getId());
                                    types.add(notificationAdded);
                                    break;
                                case MODIFIED:
                                    types.remove(dc.getOldIndex());
                                    Notification notificationChanged = dc.getDocument().toObject(Notification.class);
                                    notificationChanged.setNotificationID(dc.getDocument().getId());
                                    types.add(notificationChanged);
                                    break;
                                case REMOVED:
                                    types.remove(dc.getOldIndex());
                                    break;
                                default:
                                    break;
                            }

                        }
                        Collections.sort(types);
                        customAdapter.notifyDataSetChanged();
                    }
                });
    }
}
