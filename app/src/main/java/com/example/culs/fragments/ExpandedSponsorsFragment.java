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
import com.example.culs.helpers.Sponsor;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ExpandedSponsorsFragment extends Fragment {
    private TextView exSponsorName, exSponsorBio, exSponsorTypes, exSponsorWebsite, exSponsorLinks;
    private ImageView exSponsorCover, exSponsorLogo;
    private View rootView;
    private Sponsor expandedSponsor = new Sponsor();
    private String sponsorID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        rootView = inflater.inflate(R.layout.fragment_expanded_sponsors, container, false);
        exSponsorName = rootView.findViewById(R.id.ex_sponsor_name_text_view);
        exSponsorBio = rootView.findViewById(R.id.ex_sponsor_description_text_view);
        exSponsorCover = rootView.findViewById(R.id.ex_sponsor_pic_image_view);

        sponsorID = bundle.getString("Current Sponsor ID");
        getSponsor();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onStop() {
        super.onStop();
    }

    public void getSponsor() {
        DocumentReference docRef = db.collection("Sponsors").document(sponsorID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println("Listen failed: " + error);
                    return;
                }

                if (value != null && value.exists()) {
                    expandedSponsor = value.toObject(Sponsor.class);
                    expandedSponsor.setSponsorID(sponsorID);
                    Toast.makeText(getContext(), expandedSponsor.getName(), Toast.LENGTH_SHORT).show();
                    exSponsorName.setText(expandedSponsor.getName());
                    exSponsorBio.setText(expandedSponsor.getBio());

                    FirebaseStorage eventStorage = FirebaseStorage.getInstance();
                    StorageReference eventStorageRef = eventStorage.getReference();
                    StorageReference eventPathReference = eventStorageRef.child("Sponsors/" + expandedSponsor.getName() + "/coverPhoto.png");
                    eventPathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String eventImageUri = uri.toString();
                            GlideApp.with(getContext()).load(eventImageUri).placeholder(R.drawable.rounded_tags).fitCenter().into(exSponsorCover);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            GlideApp.with(getContext()).load(R.drawable.rounded_tags).placeholder(R.drawable.rounded_tags).fitCenter().into(exSponsorCover);
                        }
                    });

//                    FirebaseStorage sponsorStorage = FirebaseStorage.getInstance();
//                    StorageReference sponsorStorageRef = sponsorStorage.getReference();
//                    StorageReference sponsorPathReference = sponsorStorageRef.child("Sponsors/" + expandedSponsor.getName() + "/logo.png");
//                    sponsorPathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            String sponsorImageUri = uri.toString();
//                            GlideApp.with(getContext()).load(sponsorImageUri).placeholder(R.drawable.rounded_tags).fitCenter().into(exEventSponsorImage);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            GlideApp.with(getContext()).load(R.drawable.mc_durks).placeholder(R.drawable.rounded_tags).fitCenter().into(exEventSponsorImage);
//                        }
//                    });


                } else {
                    System.out.print("Current data: null");
                }
            }
        });
    }

}