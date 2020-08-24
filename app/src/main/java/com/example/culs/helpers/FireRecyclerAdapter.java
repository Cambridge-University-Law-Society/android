package com.example.culs.helpers;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.culs.R;
import com.example.culs.fragments.HomeFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FireRecyclerAdapter extends FirestoreRecyclerAdapter<Card, FireRecyclerAdapter.CardViewHolder> {

    public FireRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Card> options) {
        super(options);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        CardViewHolder cardViewHolder = new CardViewHolder(view);
        return cardViewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull final CardViewHolder holder, int position, @NonNull Card model) {

        SimpleDateFormat spf=new SimpleDateFormat("EEE, dd MMM 'at' HH:mm");

        DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
        String eventID = snapshot.getId();
        holder.eventName.setText(model.getName());
        holder.eventLocation.setText(model.getLocation());
        Date eventDate = model.getDate().toDate();
        String eventDateText = spf.format(eventDate);
        holder.eventDateTime.setText(eventDateText);
        holder.eventDescription.setText(model.getDescription());
        holder.eventPic.setImageDrawable(null);
        holder.eventTagNote.setText(model.getTags().get(0));
        holder.eventSponsor.setText(model.getSponsor());
        holder.eventSponsorLogo.setImageResource(R.drawable.fbd_logo);

        FirebaseStorage eventStorage = FirebaseStorage.getInstance();
        StorageReference eventStorageRef = eventStorage.getReference();
        StorageReference eventPathReference = eventStorageRef.child("Events/" + eventID + "/coverPhoto");
        eventPathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String eventImageUri = uri.toString();
                GlideApp.with(holder.itemView.getContext()).load(eventImageUri).placeholder(R.drawable.rounded_tags).fitCenter().into(holder.eventPic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                GlideApp.with(holder.itemView.getContext()).load(R.drawable.rounded_tags).placeholder(R.drawable.rounded_tags).fitCenter().into(holder.eventPic);
            }
        });

        FirebaseStorage sponsorStorage = FirebaseStorage.getInstance();
        StorageReference sponsorStorageRef = sponsorStorage.getReference();
        StorageReference sponsorPathReference = sponsorStorageRef.child("Sponsors/" + model.getSponsor() + "/logo.png");
        sponsorPathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String sponsorImageUri = uri.toString();
                GlideApp.with(holder.itemView.getContext()).load(sponsorImageUri).placeholder(R.drawable.rounded_tags).fitCenter().into(holder.eventSponsorLogo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                GlideApp.with(holder.itemView.getContext()).load(R.drawable.mc_durks).placeholder(R.drawable.rounded_tags).fitCenter().into(holder.eventSponsorLogo);
            }
        });

    }

    class CardViewHolder extends RecyclerView.ViewHolder{

        ImageView eventPic, eventTagIcon, eventSponsorLogo;
        TextView eventDateTime, eventDescription, eventLocation, eventName, eventTagNote, eventSponsor;
        LinearLayout eventTagHolder;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = (TextView) itemView.findViewById(R.id.event_name_text_view);
            eventDateTime = (TextView) itemView.findViewById(R.id.event_date_and_time_text_view);
            eventLocation = (TextView) itemView.findViewById(R.id.event_location_text_view);
            eventDescription = (TextView) itemView.findViewById(R.id.event_description_text_view);
            eventPic = (ImageView) itemView.findViewById(R.id.event_pic_image_view);
            eventTagIcon = (ImageView) itemView.findViewById(R.id.tag_icon);
            eventTagNote = (TextView) itemView.findViewById(R.id.tag_note);
            eventTagHolder = (LinearLayout) itemView.findViewById(R.id.event_tag_holder);
            eventSponsor = (TextView) itemView.findViewById(R.id.event_sponsor_text_view);
            eventSponsorLogo = (ImageView) itemView.findViewById(R.id.sponsor_logo);
        }
    }
}
