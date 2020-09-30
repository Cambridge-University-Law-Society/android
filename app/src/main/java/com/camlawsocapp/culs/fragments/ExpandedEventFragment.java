package com.camlawsocapp.culs.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.camlawsocapp.culs.R;
import com.camlawsocapp.culs.helpers.Card;
import com.camlawsocapp.culs.helpers.CustomAdapter;
import com.camlawsocapp.culs.helpers.Notification;
import com.camlawsocapp.culs.helpers.PostType;
import com.camlawsocapp.culs.helpers.User;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
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
    private ArrayList<String> uninterested = new ArrayList<>();
    private ArrayList<String> interested = new ArrayList<>();
    private ListenerRegistration eventsReg;
    private DocumentReference events;
    private ListenerRegistration notifsReg;
    private ListenerRegistration userReg;
    private Query notifs;
    public User currentUser;

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
        getCard();
        customAdapter.notifyDataSetChanged();
    }

    public void onStop() {

        super.onStop();

        eventsReg.remove();
        notifsReg.remove();
        userReg.remove();

        for(String i : interested){
            DocumentReference eventDocRef = db.collection("Events").document(i);
            eventDocRef.update("attendees", FieldValue.arrayUnion(userID));
        }

        for(String i : uninterested){
            DocumentReference eventDocRef = db.collection("Events").document(i);
            eventDocRef.update("attendees", FieldValue.arrayRemove(userID));
        }
    }

    public void getCard() {

        events = db.collection("Events").document(cardId);
        eventsReg = events.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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

                    if (currentUser.getMyevents() == null) {
                        expandedCard.setInterested(false);
                    } else if (currentUser.getMyevents().contains(cardId)) {
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
                            default:
                                exEventTagIcon[i].setImageResource(R.drawable.ic_default_tag_24dp);
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
                    Glide.with(getContext()).load(eventPathReference).placeholder(R.drawable.image_placeholder).fitCenter().into(exEventPic);

                    if(expandedCard.getSponsor().equals("Law Society")){
                        exEventSponsorImage.setImageResource(R.drawable.culs_black_logo);
                    } else {
                        FirebaseStorage sponsorStorage = FirebaseStorage.getInstance();
                        StorageReference sponsorStorageRef = sponsorStorage.getReference();
                        StorageReference sponsorPathReference = sponsorStorageRef.child("Sponsors/" + expandedCard.getSponsor() + "/logo.png");
                        Glide.with(getContext()).load(sponsorPathReference).placeholder(R.drawable.image_placeholder).fitCenter().into(exEventSponsorImage);
                    }
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
                if (currentUser.getMyevents() == null) {
                    userDocRef.update("myevents", FieldValue.arrayUnion(cardId));
                    expandedCard.setInterested(true);
                    exEventInterestedIcon.setImageResource(R.drawable.ic_interested_button_on_24dp);
                    exEventInterestedText.setText("Remove from My Events");
                    if(!interested.contains(expandedCard.getID())) {
                        interested.add(expandedCard.getID());
                    }
                    while(uninterested.contains(expandedCard.getID())) {
                        uninterested.remove(expandedCard.getID());
                    }

                    userDocRef.update("myevents", FieldValue.arrayUnion(expandedCard.getID()));
                    FirebaseMessaging.getInstance().subscribeToTopic(expandedCard.getID())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else if (currentUser.getMyevents().contains(cardId)) {
                    userDocRef.update("myevents", FieldValue.arrayRemove(cardId));
                    expandedCard.setInterested(false);
                    exEventInterestedIcon.setImageResource(R.drawable.ic_interested_button_off_24dp);
                    exEventInterestedText.setText("Add to My Events");

                    if(!uninterested.contains(expandedCard.getID())) {
                        uninterested.add(expandedCard.getID());
                    }
                    while(interested.contains(expandedCard.getID())) {
                        interested.remove(expandedCard.getID());
                    }

                    userDocRef.update("myevents", FieldValue.arrayRemove(expandedCard.getID()));

                    FirebaseMessaging.getInstance().unsubscribeFromTopic(expandedCard.getID())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else {
                    userDocRef.update("myevents", FieldValue.arrayUnion(cardId));
                    expandedCard.setInterested(true);
                    exEventInterestedIcon.setImageResource(R.drawable.ic_interested_button_on_24dp);
                    exEventInterestedText.setText("Remove from My Events");

                    if(!interested.contains(expandedCard.getID())) {
                        interested.add(expandedCard.getID());
                    }
                    while(uninterested.contains(expandedCard.getID())) {
                        uninterested.remove(expandedCard.getID());
                    }

                    userDocRef.update("myevents", FieldValue.arrayUnion(expandedCard.getID()));

                    FirebaseMessaging.getInstance().subscribeToTopic(expandedCard.getID())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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
        notifs = db.collection("notifications").whereArrayContains("receiverID", cardId).orderBy("timestamp", Query.Direction.DESCENDING);
        userReg = userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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

        notifsReg = notifs
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            System.err.println("Listen failed:" + error);
                            return;
                        }

                        for (final DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    final Notification notificationAdded = dc.getDocument().toObject(Notification.class);
                                    notificationAdded.setNotificationID(dc.getDocument().getId());
                                    DocumentReference docRefAdded = db.collection("users").document(notificationAdded.getSenderID());
                                    docRefAdded.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                           @Override
                                           public void onSuccess(DocumentSnapshot documentSnapshot) {
                                               if (documentSnapshot.exists()) {
                                                   User notificationSender = documentSnapshot.toObject(User.class);
                                                   notificationAdded.setNotificationSenderName(notificationSender.getFirstname() + " " + notificationSender.getLastname());
                                                   customAdapter.notifyItemChanged(dc.getNewIndex());
                                               } else {
                                                   DocumentReference docRefDelete = db.collection("notifications").document(dc.getDocument().getId());
                                                   docRefDelete.delete();
                                               }
                                           }
                                           });
                                    types.add(notificationAdded);
                                    break;
                                case MODIFIED:
                                    types.remove(dc.getOldIndex());
                                    final Notification notificationChanged = dc.getDocument().toObject(Notification.class);
                                    notificationChanged.setNotificationID(dc.getDocument().getId());
                                    DocumentReference docRefChanged = db.collection("users").document(notificationChanged.getSenderID());
                                    docRefChanged.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()){
                                                User notificationSender = documentSnapshot.toObject(User.class);
                                                notificationChanged.setNotificationSenderName(notificationSender.getFirstname() + " " + notificationSender.getLastname());
                                                customAdapter.notifyItemChanged(dc.getNewIndex());
                                            }else {
                                                //notificationChanged.setNotificationSenderName("CULS Admin");
                                                DocumentReference docRefDelete = db.collection("notifications").document(dc.getDocument().getId());
                                                docRefDelete.delete();
                                            }
                                        }
                                    });
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
