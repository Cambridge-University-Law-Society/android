package com.example.culs.helpers;

import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.culs.helpers.GlideApp;
import com.example.culs.R;
import com.example.culs.fragments.HomeFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.embersoft.expandabletextview.ExpandableTextView;

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PostType> mTypes;
    private SimpleDateFormat spf = new SimpleDateFormat("EEE, dd MMM 'at' HH:mm");
    private OnEventItemClickListener mEventListener;
    private OnPostItemClickListener mPostListener;

    public CustomAdapter(List<PostType> types) {
        mTypes = types;
    }

    public interface OnEventItemClickListener {
        void onItemClick(View v, int position);
        void onInterestedClick(View v, int position);
    }

    public interface OnPostItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnEventItemClickListener(OnEventItemClickListener listener) {
        mEventListener = listener;
    }

    public void setOnPostItemClickListener(OnPostItemClickListener listener) {
        mPostListener = listener;
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        ImageView eventPic, eventTagIcon, eventSponsorLogo, eventInterested;
        TextView eventDateTime, eventDescription, eventLocation, eventName, eventTagNote, eventSponsor;
        LinearLayout eventTagHolder;

        public EventViewHolder(@NonNull View itemView) {
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
            eventInterested = (ImageView) itemView.findViewById(R.id.event_interested_button);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mEventListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mEventListener.onItemClick(v, position);
                        }
                    }
                }
            });
            eventInterested.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mEventListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mEventListener.onInterestedClick(v, position);
                        }
                    }
                }
            });
        }

        void bindView(int position) {

            final Card card = (Card) mTypes.get(position);

            eventName.setText(card.getName());
            eventDateTime.setText(spf.format(card.getDate().toDate()));
            eventLocation.setText(card.getLocation());
            eventDescription.setText(card.getDescription());
            eventPic.setImageDrawable(null);
            eventTagNote.setText(card.getTags().get(0));
            eventSponsor.setText(card.getSponsor());
            eventSponsorLogo.setImageResource(R.drawable.fbd_logo);
            if(card.getInterested()){
                eventInterested.setImageResource(R.drawable.ic_interested_button_on_24dp);
            } else {
                eventInterested.setImageResource(R.drawable.ic_interested_button_off_24dp);
            }

            FirebaseStorage eventStorage = FirebaseStorage.getInstance();
            StorageReference eventStorageRef = eventStorage.getReference();
            StorageReference eventPathReference = eventStorageRef.child("Events/" + card.getID() + "/coverPhoto");
            eventPathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String eventImageUri = uri.toString();
                    GlideApp.with(itemView.getContext()).load(eventImageUri).placeholder(R.drawable.rounded_tags).fitCenter().into(eventPic);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    GlideApp.with(itemView.getContext()).load(R.drawable.rounded_tags).placeholder(R.drawable.rounded_tags).fitCenter().into(eventPic);
                }
            });

            FirebaseStorage sponsorStorage = FirebaseStorage.getInstance();
            StorageReference sponsorStorageRef = sponsorStorage.getReference();
            StorageReference sponsorPathReference = sponsorStorageRef.child("Sponsors/" + card.getSponsor() + "/logo.png");
            sponsorPathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String sponsorImageUri = uri.toString();
                    GlideApp.with(itemView.getContext()).load(sponsorImageUri).placeholder(R.drawable.rounded_tags).fitCenter().into(eventSponsorLogo);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    GlideApp.with(itemView.getContext()).load(R.drawable.mc_durks).placeholder(R.drawable.rounded_tags).fitCenter().into(eventSponsorLogo);
                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.eventPic.setTransitionName(card.getName() + "_image");
            }

        }
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        TextView postTitle, postSender, postContent;
        ExpandableTextView postDateTime;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postContent = (TextView) itemView.findViewById(R.id.post_title_text_view);
            postTitle = (TextView) itemView.findViewById(R.id.post_date_and_time_text_view);
            postSender = (TextView) itemView.findViewById(R.id.post_sender_text_view);
            postDateTime = (ExpandableTextView) itemView.findViewById(R.id.post_content_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPostListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mPostListener.onItemClick(v, position);
                        }
                    }
                }
            });
        }

        void bindView(final int position) {

            final Post post = (Post) mTypes.get(position);
            postContent.setText(post.getTitle());
            postTitle.setText(spf.format(post.getTimestamp().toDate()));
            postSender.setText(post.getSenderID());
            postDateTime.setText(post.getContent());

            postDateTime.setText(post.getContent());
            postDateTime.setOnStateChangeListener(new ExpandableTextView.OnStateChangeListener() {
                @Override
                public void onStateChange(boolean isShrink) {
                    post.setShrink(isShrink);
                    mTypes.set(position, post);
                }
            });
            postDateTime.setText(post.getContent());
            postDateTime.resetState(post.isShrink());

        }
    }

    @Override
    public int getItemViewType(int position) {
        return mTypes.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case PostType.TYPE_EVENT:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_item, parent, false);
                return new EventViewHolder(itemView);
            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.post_item, parent, false);
                return new PostViewHolder(itemView);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case PostType.TYPE_EVENT:
                ((EventViewHolder) holder).bindView(position);
                break;
            case PostType.TYPE_POST:
                ((PostViewHolder) holder).bindView(position);
                break;

        }
    }

    @Override
    public int getItemCount() {
        if (mTypes == null) {
            return 0;
        } else {
            return mTypes.size();
        }
    }

    public void clear() {
        int size = mTypes.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                mTypes.remove(i);
            }

            notifyItemRangeRemoved(0, size);
        }
    }

}