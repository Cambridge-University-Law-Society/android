package com.example.culs.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.culs.R;
import com.example.culs.helpers.Card;
import com.example.culs.helpers.GlideApp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ExpandedEventFragment extends Fragment implements View.OnClickListener {
    private TextView exEventName, exEventDnT, exEventLoc, exEventDesc, exEventTagNote, exEventSponsorName;
    private ImageView exEventPic, exEventTagIcon, exEventSponsorImage, exEventInterestedIcon;
    private LinearLayout exEventSponsorHolder, exEventInterestedHolder;
    private View rootView;
    private Card expandedCard = new Card();
    private String cardId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SimpleDateFormat spf = new SimpleDateFormat("EEE, dd MMM 'at' HH:mm");
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DocumentReference userDocRef = db.collection("users").document(userID);

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        rootView = inflater.inflate(R.layout.fragment_expanded_events, container, false);
        exEventName = rootView.findViewById(R.id.ex_event_name_text_view);
        exEventDnT = rootView.findViewById(R.id.ex_event_date_and_time_text_view);
        exEventLoc = rootView.findViewById(R.id.ex_event_location_text_view);
        exEventDesc = rootView.findViewById(R.id.ex_event_description_text_view);
        exEventTagNote = rootView.findViewById(R.id.ex_tag_note);
        exEventPic = rootView.findViewById(R.id.ex_event_pic_image_view);
        exEventTagIcon = rootView.findViewById(R.id.ex_tag_icon);
        exEventSponsorImage = rootView.findViewById(R.id.ex_sponsor_logo_image_view);
        exEventInterestedIcon = rootView.findViewById(R.id.ex_event_interested_star);
        exEventSponsorHolder = rootView.findViewById(R.id.ex_event_sponsor_holder);
        exEventSponsorName = rootView.findViewById(R.id.ex_sponsor_name_text_view);
        exEventInterestedHolder = rootView.findViewById(R.id.ex_event_interested_button);
        exEventInterestedHolder.setOnClickListener(this);
        exEventSponsorHolder.setOnClickListener(this);

        cardId = bundle.getString("Current Card ID");
        getCard();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    public void onStop() {
        super.onStop();
    }

    public void getCard() {
        DocumentReference docRef = db.collection("Events").document(cardId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println("Listen failed: " + error);
                    return;
                }

                if (value != null && value.exists()) {
                    expandedCard = value.toObject(Card.class);
                    expandedCard.setID(cardId);
                    if (HomeFragment.currentUser.getMyevents() == null) {
                        expandedCard.setInterested(false);
                    } else if (HomeFragment.currentUser.getMyevents().contains(cardId)) {
                        expandedCard.setInterested(true);
                    } else {
                        expandedCard.setInterested(false);
                    }
                    Toast.makeText(getContext(), expandedCard.getName(), Toast.LENGTH_SHORT).show();
                    exEventName.setText(expandedCard.getName());
                    exEventDnT.setText(spf.format(expandedCard.getDate().toDate()));
                    exEventLoc.setText(expandedCard.getLocation());
                    exEventDesc.setText(expandedCard.getDescription());
                    exEventTagNote.setText(expandedCard.getTags().get(0));
                    if (expandedCard.getInterested()) {
                        exEventInterestedIcon.setImageResource(R.drawable.ic_interested_button_on_24dp);
                    } else {
                        exEventInterestedIcon.setImageResource(R.drawable.ic_interested_button_off_24dp);
                    }
                    exEventSponsorName.setText(expandedCard.getSponsor());

                    FirebaseStorage eventStorage = FirebaseStorage.getInstance();
                    StorageReference eventStorageRef = eventStorage.getReference();
                    StorageReference eventPathReference = eventStorageRef.child("Events/" + expandedCard.getID() + "/coverPhoto");
                    eventPathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String eventImageUri = uri.toString();
                            GlideApp.with(getContext()).load(eventImageUri).placeholder(R.drawable.rounded_tags).fitCenter().into(exEventPic);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            GlideApp.with(getContext()).load(R.drawable.rounded_tags).placeholder(R.drawable.rounded_tags).fitCenter().into(exEventPic);
                        }
                    });

                    FirebaseStorage sponsorStorage = FirebaseStorage.getInstance();
                    StorageReference sponsorStorageRef = sponsorStorage.getReference();
                    StorageReference sponsorPathReference = sponsorStorageRef.child("Sponsors/" + expandedCard.getSponsor() + "/logo.png");
                    sponsorPathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String sponsorImageUri = uri.toString();
                            GlideApp.with(getContext()).load(sponsorImageUri).placeholder(R.drawable.rounded_tags).fitCenter().into(exEventSponsorImage);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            GlideApp.with(getContext()).load(R.drawable.mc_durks).placeholder(R.drawable.rounded_tags).fitCenter().into(exEventSponsorImage);
                        }
                    });


                } else {
                    System.out.print("Current data: null");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ex_event_sponsor_holder:
                Toast.makeText(getContext(), "Clicked on:" + expandedCard.getSponsor(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.ex_event_interested_button:
                if (HomeFragment.currentUser.getMyevents() == null) {
                    userDocRef.update("myevents", FieldValue.arrayUnion(cardId));
                    expandedCard.setInterested(true);
                    exEventInterestedIcon.setImageResource(R.drawable.ic_interested_button_on_24dp);
                } else if (HomeFragment.currentUser.getMyevents().contains(cardId)) {
                    userDocRef.update("myevents", FieldValue.arrayRemove(cardId));
                    expandedCard.setInterested(false);
                    exEventInterestedIcon.setImageResource(R.drawable.ic_interested_button_off_24dp);
                } else {
                    userDocRef.update("myevents", FieldValue.arrayUnion(cardId));
                    expandedCard.setInterested(true);
                    exEventInterestedIcon.setImageResource(R.drawable.ic_interested_button_on_24dp);
                }
                break;
            default:
                break;
        }
    }
}
