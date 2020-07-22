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

    private Card card;
    private Context context;
    private final ImageView eventPic;
    private final TextView eventDateTime;
    private final TextView eventName;
    private final TextView eventLocation;
    private final TextView eventDescription;
    private final ImageView friendProfs[]= new ImageView[3];
    private final ImageView eventTagIcon;
    private final TextView eventTagNote;
    private final LinearLayout eventTagHolder;
    private int highestTag = 0;
    private ArrayList<Card> cardList = null;
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
        this.eventName.setText(card.getEventName());
        this.eventDateTime.setText(card.getEventDateAndTime());
        this.eventLocation.setText(card.getEventLocation());
        this.eventDescription.setText(card.getEventDescription());
        this.eventPic.setImageResource(card.getEventImage());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.eventPic.setTransitionName(card.getEventName() + "_image");
            this.eventDescription.setTransitionName(card.getEventName() + "_description");
            this.eventName.setTransitionName(card.getEventName() + "_name");
            this.eventDateTime.setTransitionName(card.getEventName() + "_dnt");
            this.eventLocation.setTransitionName(card.getEventName() + "_location");
            this.eventTagHolder.setTransitionName(card.getEventName() + "_tagholder");
            this.eventTagIcon.setTransitionName(card.getEventName() + "_tagicon");
            this.eventTagNote.setTransitionName(card.getEventName() + "_tagnote");
        }
        for(int i=0; i<(card.getEventFriendPics().length); i++){
            this.friendProfs[i].setImageResource(card.getEventFriendPics()[i]);
        }
        for(int i=0; i<(card.getEventTags().length); i++){
            if(card.getEventTags()[i] == true) {
                highestTag = (i+1);
            }
        }
        switch(highestTag){
            case 0:
                this.eventTagIcon.setImageResource(R.drawable.ic_cancel_tag_24dp);
                this.eventTagHolder.setBackgroundResource(R.drawable.rounded_tags);
                this.eventTagNote.setText("Canceled");
                break;
            case 1:
                this.eventTagIcon.setImageResource(R.drawable.ic_new_tag_24dp);
                this.eventTagHolder.setBackgroundResource(R.drawable.rounded_tags);
                this.eventTagNote.setText("New");
                break;
            case 2:
                this.eventTagIcon.setImageResource(R.drawable.ic_socials_icon_24dp);
                this.eventTagHolder.setBackgroundResource(R.drawable.rounded_tags);
                this.eventTagNote.setText("Social");
                break;
            case 3:
                this.eventTagIcon.setImageResource(R.drawable.ic_upcoming_tag_24dp);
                this.eventTagHolder.setBackgroundResource(R.drawable.rounded_tags);
                this.eventTagNote.setText("Upcoming");
                break;
            case 4:
                this.eventTagIcon.setImageResource(R.drawable.ic_careers_icon_24dp);
                this.eventTagHolder.setBackgroundResource(R.drawable.rounded_tags);
                this.eventTagNote.setText("Careers");
                break;
            case 5:
                this.eventTagIcon.setImageResource(R.drawable.ic_networking_icon_24dp);
                this.eventTagHolder.setBackgroundResource(R.drawable.rounded_tags);
                this.eventTagNote.setText("Networking");
                break;
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

}
