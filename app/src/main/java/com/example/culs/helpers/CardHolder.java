package com.example.culs.helpers;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.culs.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class CardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public Card card;
    public Context context;
    public final ImageView eventPic;
    public final TextView eventDateTime;
    public final TextView eventName;
    public final TextView eventLocation;
    public final TextView eventDescription;
    public final ImageView friendProfs[]= new ImageView[3];
    public final ImageView eventTagIcon;
    public final TextView eventTagNote;
    public final LinearLayout eventTagHolder;
    public int highestTag = 0;                              
    public String IMAGE_URL;
    public ArrayList<Card> cardList = null;
    OnCardListener onCardListener;

    public CardHolder(Context context, View itemView, OnCardListener onCardListener, ArrayList<Card> cards) {
        super(itemView);
        cardList = cards;
        this.context = context;
        this.eventName = (TextView) itemView.findViewById(R.id.event_name_text_view);
        this.eventDateTime = (TextView) itemView.findViewById(R.id.event_date_and_time_text_view);
        this.eventLocation = (TextView) itemView.findViewById(R.id.event_location_text_view);
        this.eventDescription = (TextView) itemView.findViewById(R.id.event_description_text_view);
        this.eventPic = (ImageView) itemView.findViewById(R.id.event_pic_image_view);
        this.friendProfs[0] = (ImageView) itemView.findViewById(R.id.event_friend_1_image_view);
        this.friendProfs[1] = (ImageView) itemView.findViewById(R.id.event_friend_2_image_view);
        this.friendProfs[2] = (ImageView) itemView.findViewById(R.id.event_friend_3_image_view);
        this.eventTagIcon = (ImageView) itemView.findViewById(R.id.tag_icon);
        this.eventTagNote = (TextView) itemView.findViewById(R.id.tag_note);
        this.eventTagHolder = (LinearLayout) itemView.findViewById(R.id.event_tag_holder);
        itemView.setOnClickListener(this);
        this.onCardListener = onCardListener;

    }

    public void bindCard(Card card) {
        this.card = card;
        this.eventName.setText(card.getName());
//        this.eventDateTime.setText(card.getEventDateAndTime());
        this.eventLocation.setText(card.getLocation());
        this.eventDescription.setText(card.getDescription());
        

//        try {
//            fetchImage(this, card);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.eventPic.setTransitionName(card.getName() + "_image");
            this.eventDescription.setTransitionName(card.getName() + "_description");
            this.eventName.setTransitionName(card.getName() + "_name");
            this.eventDateTime.setTransitionName(card.getName() + "_dnt");
            this.eventLocation.setTransitionName(card.getName() + "_location");
            this.eventTagHolder.setTransitionName(card.getName() + "_tagholder");
            this.eventTagIcon.setTransitionName(card.getName() + "_tagicon");
            this.eventTagNote.setTransitionName(card.getName() + "_tagnote");
        }
    }

    @Override
    public void onClick(View v) {
        // 5. Handle the onClick event for the ViewHolder
            onCardListener.onCardClick(v, getAdapterPosition(), cardList);
    }

    public interface OnCardListener{
        void onCardClick(View v, int position, ArrayList<Card> cardList);
    }

//    private void fetchImage(final CardHolder holder, final Card currentCard) throws IOException {
//        //using glide method
//
//        //Reference to an image file in cloud storage
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//
//        // Create a reference with an initial file path and name
//        StorageReference pathReference = storageRef.child("Events/" + currentCard.getEventImage());
//        String downloadUrl = pathReference.toString();
//
//        //try to download to a local file
//        final File file = File.createTempFile("eventPic", "jpg");
//        pathReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                GlideApp.with(holder.itemView).load(file).into(holder.eventPic);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//
//        //GlideApp.with(this.getContext()).load(pathReference).into(image);
//
//    }

}
