package com.camlawsocapp.culs.helpers;

import android.animation.LayoutTransition;
import android.net.Uri;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;

import com.camlawsocapp.culs.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import ru.embersoft.expandabletextview.ExpandableTextView;

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<PostType> mTypes;

    //when we filter through the list we'll remove items from it but when we remove characters from the input edit text we need to put items back into the list
    // therefore we create a copy of the list which always contains all the items
    private List<PostType> mTypesFull;

    private SimpleDateFormat spf = new SimpleDateFormat("EEE, dd MMM 'at' HH:mm");
    private OnEventItemClickListener mEventListener;
    private OnSponsorItemClickListener mSponsorListener;

    public CustomAdapter(List<PostType> types) {
        mTypes = types;
        mTypesFull = new ArrayList<>(types); //a new array list that contains all the items in types but also is independent
    }

    public interface OnEventItemClickListener {
        void onItemClick(View v, int position);
        void onInterestedClick(View v, int position);
    }

    public interface OnSponsorItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnEventItemClickListener(OnEventItemClickListener listener) {
        mEventListener = listener;
    }

    public void setOnSponsorItemClickListener(OnSponsorItemClickListener listener) {
        mSponsorListener = listener;
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        public ImageView eventPic, eventTagIcon[] = new ImageView[3], eventSponsorLogo, eventInterested;
        TextView eventDateTime, eventDescription, eventLocation, eventName, eventTagNote[] = new TextView[3], eventSponsor;
        LinearLayout eventTagHolder[] = new LinearLayout[3];

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = (TextView) itemView.findViewById(R.id.event_name_text_view);
            eventDateTime = (TextView) itemView.findViewById(R.id.event_date_and_time_text_view);
            eventLocation = (TextView) itemView.findViewById(R.id.event_location_text_view);
            eventDescription = (TextView) itemView.findViewById(R.id.event_description_text_view);
            eventPic = (ImageView) itemView.findViewById(R.id.event_pic_image_view);
            eventTagIcon[0] = (ImageView) itemView.findViewById(R.id.tag_icon_one);
            eventTagNote[0] = (TextView) itemView.findViewById(R.id.tag_note_one);
            eventTagIcon[1] = (ImageView) itemView.findViewById(R.id.tag_icon_two);
            eventTagNote[1] = (TextView) itemView.findViewById(R.id.tag_note_two);
            eventTagIcon[2] = (ImageView) itemView.findViewById(R.id.tag_icon_three);
            eventTagNote[2] = (TextView) itemView.findViewById(R.id.tag_note_three);
            eventTagHolder[0] = (LinearLayout) itemView.findViewById(R.id.event_tag_holder_one);
            eventTagHolder[1] = (LinearLayout) itemView.findViewById(R.id.event_tag_holder_two);
            eventTagHolder[2] = (LinearLayout) itemView.findViewById(R.id.event_tag_holder_three);
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

            for (int i = 0; i < 3; i++){
                eventTagHolder[i].setVisibility(View.INVISIBLE);
            }

            for (int i = 0; i < card.getTags().size(); i++) {
                eventTagHolder[i].setVisibility(View.VISIBLE);
                eventTagNote[i].setText(card.getTags().get(i));
                switch (card.getTags().get(i)){
                    case "Careers":
                        eventTagIcon[i].setImageResource(R.drawable.ic_careers_icon_24dp);
                        break;
                    case "Networking":
                        eventTagIcon[i].setImageResource(R.drawable.ic_networking_icon_24dp);
                        break;
                    case "Social":
                        eventTagIcon[i].setImageResource(R.drawable.ic_socials_icon_24dp);
                        break;
                    default:
                        eventTagIcon[i].setImageResource(R.drawable.ic_default_tag_24dp);
                        break;
                }
                eventTagHolder[i].animate().translationX(1f).setDuration(1000).setListener(null);
            }

            eventSponsor.setText(card.getSponsor());
            eventSponsorLogo.setImageResource(R.drawable.fbd_logo);
            if(card.getInterested()){
                eventInterested.setImageResource(R.drawable.ic_interested_button_on_24dp);
            } else {
                eventInterested.setImageResource(R.drawable.ic_interested_button_off_24dp);
            }

                BaseRequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);

                FirebaseStorage eventStorage = FirebaseStorage.getInstance();
                StorageReference eventStorageRef = eventStorage.getReference();
                StorageReference eventPathReference = eventStorageRef.child("Events/" + card.getID() + "/coverPhoto");
                Glide.with(itemView.getContext()).load(eventPathReference).apply(requestOptions).placeholder(R.drawable.image_placeholder).fitCenter().into(eventPic);

                FirebaseStorage sponsorStorage = FirebaseStorage.getInstance();
                StorageReference sponsorStorageRef = sponsorStorage.getReference();
                StorageReference sponsorPathReference = sponsorStorageRef.child("Sponsors/" + card.getSponsor() + "/logo.png");
                Glide.with(itemView.getContext()).load(sponsorPathReference).apply(requestOptions).placeholder(R.drawable.image_placeholder).fitCenter().into(eventSponsorLogo);


        }
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        TextView postTitle, postSender, postDateTime;
        ExpandableTextView postContent1;
        TextView postContent;
        ImageView postSenderPic;
        CardView postLayout;
        RelativeLayout postRelative;
        private final boolean INITIAL_IS_COLLAPSED = true;
        private boolean isCollapsed = INITIAL_IS_COLLAPSED;
        private final static int MAX_LINES_COLLAPSED = 3;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = (TextView) itemView.findViewById(R.id.post_title_text_view);
            postDateTime = (TextView) itemView.findViewById(R.id.post_timestamp_text_view);
            postSender = (TextView) itemView.findViewById(R.id.post_sender_text_view);
            //postContent1 = (ExpandableTextView) itemView.findViewById(R.id.post_content_expandable_text_view);
            postContent = (TextView) itemView.findViewById(R.id.post_content_text_view);
            postSenderPic = (ImageView) itemView.findViewById(R.id.post_profile_pic);
            postLayout = itemView.findViewById(R.id.post_card);
            postRelative = itemView.findViewById(R.id.relative_content);

        }

        void bindView(final int position) {

            final Post post = (Post) mTypes.get(position);
            postTitle.setText(post.getTitle());
            postDateTime.setText(spf.format(post.getTimestamp().toDate()));
            postSender.setText(post.getSenderName());

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            final BaseRequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);

            // Create a reference with an initial file path and name
            StorageReference pathReference = storageRef.child("users/"+ post.getSenderID() +"/profilePic");
            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String userProfileImageUri = uri.toString();
                    //GlideApp.with(getContext()).load(userProfileImageUri).placeholder(R.drawable.ic_profile_icon_24dp).fitCenter().into(image);
                    Glide.with(itemView.getContext()).load(userProfileImageUri).placeholder(R.drawable.noprofilepicture).apply(requestOptions).fitCenter().into(postSenderPic);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //GlideApp.with(getContext()).load(R.drawable.ic_profile_icon_24dp).placeholder(R.drawable.ic_profile_icon_24dp).fitCenter().into(image);
                    Glide.with(itemView.getContext()).load(R.drawable.noprofilepicture).placeholder(R.drawable.noprofilepicture).apply(requestOptions).fitCenter().into(postSenderPic);
                }
            });
            postContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isCollapsed){
                        postContent.setMaxLines(Integer.MAX_VALUE);
                    } else{
                        postContent.setMaxLines(MAX_LINES_COLLAPSED);
                    }
                    isCollapsed = !isCollapsed;
                    applyTransition();
                }
            });
            postContent.setText(post.getContent());
            //postContent1.setMovementMethod(LinkMovementMethod.getInstance());
            /*postContent1.setOnStateChangeListener(new ExpandableTextView.OnStateChangeListener() {
                @Override
                public void onStateChange(boolean isShrink) {
                    post.setShrink(isShrink);
                    mTypes.set(position, post);
                }
            });
            postContent1.resetState(post.isShrink());
            postContent1.setText(post.getContent());*/
        }

        private void applyTransition(){
            LayoutTransition transition = new LayoutTransition();
            transition.setDuration(300);
            transition.enableTransitionType(LayoutTransition.CHANGING);
            postLayout.setLayoutTransition(transition);
        }
    }

    public class SponsorViewHolder extends RecyclerView.ViewHolder {

        ImageView sponsorPic;
        TextView sponsorName, sponsorDescription, sponsorTypes;

        public SponsorViewHolder(@NonNull View itemView) {
            super(itemView);
            sponsorName = (TextView) itemView.findViewById(R.id.sponsor_name_text_view);
            sponsorDescription = (TextView) itemView.findViewById(R.id.sponsor_description_text_view);
            sponsorPic = (ImageView) itemView.findViewById(R.id.sponsor_logo_image_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSponsorListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mSponsorListener.onItemClick(v, position);
                        }
                    }
                }
            });
        }

        void bindView(int position) {

            final Sponsor sponsor = (Sponsor) mTypes.get(position);

            sponsorName.setText(sponsor.getName());
            sponsorDescription.setText(sponsor.getBio());
            sponsorPic.setImageDrawable(null);
            final BaseRequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);

            FirebaseStorage sponsorStorage = FirebaseStorage.getInstance();
            StorageReference    sponsorStorageRef = sponsorStorage.getReference();
            StorageReference sponsorPathReference = sponsorStorageRef.child("Sponsors/" + sponsor.getName() + "/logo.png");
            Glide.with(itemView.getContext()).load(sponsorPathReference).placeholder(R.drawable.image_placeholder).fitCenter().into(sponsorPic);
        }
    }


    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        ImageView NotificationSenderPic;
        TextView notificationTitle, notificationDateTime, notificationType, notificationSenderName;
        ExpandableTextView notificationContent;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationSenderName = (TextView) itemView.findViewById(R.id.notification_sender_text_view);
            notificationTitle = (TextView) itemView.findViewById(R.id.notification_title_text_view);
            notificationType = (TextView) itemView.findViewById(R.id.notification_type_text_view);
            notificationDateTime = (TextView) itemView.findViewById(R.id.notification_date_and_time_text_view);
            notificationContent = (ExpandableTextView) itemView.findViewById(R.id.notification_content_text_view);
        }

        void bindView(final int position) {

            final Notification notification = (Notification) mTypes.get(position);
            notificationSenderName.setText(notification.getNotificationSenderName());
            notificationType.setText(notification.getType());
            notificationTitle.setText(notification.getTitle());
            notificationDateTime.setText(spf.format(notification.getTimestamp().toDate()));
            notificationContent.setText(notification.getContent());
            notificationContent.setOnStateChangeListener(new ExpandableTextView.OnStateChangeListener() {
                @Override
                public void onStateChange(boolean isShrink) {
                    notification.setShrink(isShrink);
                    mTypes.set(position, notification);
                }
            });
            notificationContent.setText(notification.getContent());
            notificationContent.resetState(notification.isShrink());

        }
    }

    @Override
    public int getItemViewType(int position) {
        return mTypes.get(position).getPostType();
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
            case PostType.TYPE_SPONSOR:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.sponsor_item, parent, false);
                return new SponsorViewHolder(itemView);
            case PostType.TYPE_NOTIFICATION:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.notifications_item, parent, false);
                return new NotificationViewHolder(itemView);
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
            case PostType.TYPE_SPONSOR:
                ((SponsorViewHolder) holder).bindView(position);
                break;
            case PostType.TYPE_NOTIFICATION:
                ((NotificationViewHolder) holder).bindView(position);

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




    @Override
    public Filter getFilter() {
        return mTypesFilter;
    }

    private Filter mTypesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            //performFiltering will work in the background so will not freeze the app
            //the charSequence will be the input from the search - so use this as logic
            List<PostType> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0){
                filteredList.addAll(mTypes); //contains all the items
            }else{
                String filterInput = charSequence.toString().toLowerCase().trim();
                for (int position=0 ; position < mTypes.size(); position++ ){
                    PostType type = mTypes.get(position);
                        if (type.getPostType() == PostType.TYPE_EVENT){
                            Card card = (Card) mTypes.get(position);
                            if (card.getName().toLowerCase().contains(filterInput)||card.getLocation().toLowerCase().contains(filterInput)||card.getSponsor().toLowerCase().contains(filterInput)){
                                filteredList.add(type);
                            }
                        }
                        if (type.getPostType() == PostType.TYPE_POST){
                            Post post = (Post) mTypes.get(position);
                            if(post.getTitle().toLowerCase().contains(filterInput)){
                                filteredList.add(type);
                            }
                        }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mTypes.clear();//we only want to have the filtered results in this list
            mTypes.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

}