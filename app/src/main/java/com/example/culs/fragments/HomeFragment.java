package com.example.culs.fragments;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;

import com.example.culs.R;
import com.example.culs.helpers.Card;
import com.example.culs.helpers.CardAdapter;
import com.example.culs.helpers.CardHolder;
import com.example.culs.helpers.GlideApp;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

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
import de.hdodenhof.circleimageview.CircleImageView;

import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;

public class HomeFragment extends Fragment implements CardHolder.OnCardListener {
    private RecyclerView eventsView;
    private View rootView;
    public static final String TAG = "Shonak";
    private SwipeRefreshLayout swipeContainer;
    String[] friends = {"friend 1", "friend 2", "friend 3"};
    private ArrayList<DocumentReference> documentList = new ArrayList<DocumentReference>();
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
    private CollectionReference eventsRef = db.collection("Events");
    private ListenerRegistration noteListener;
    private ArrayList<Card> cards = new ArrayList<>();
    private FirestoreRecyclerAdapter<Card, CardViewHolder> fire_adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

        @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);

        eventsView = (RecyclerView) rootView.findViewById(R.id.list);
        eventsView.setHasFixedSize(true);
//        eventsView.setAdapter(adapter);
        eventsView.setLayoutManager(new LinearLayoutManager(getActivity()));


        Toolbar myToolbar = rootView.findViewById(R.id.my_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_search_icon_24dp);// set drawable icon
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        //fetch the info from firebase
        Query query = FirebaseFirestore.getInstance().collection("Events").orderBy("date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Card> options = new FirestoreRecyclerOptions.Builder<Card>().setQuery(query, Card.class).build();
        fire_adapter = new FirestoreRecyclerAdapter<Card, CardViewHolder>(options) {
                @NonNull
                @Override
                public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
                    CardViewHolder cardViewHolder = new CardViewHolder(view);
                    return cardViewHolder;
                }

                //this creates the card with the info
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
                    holder.eventSponsorLogo.setImageResource(R.drawable.ano_logo);

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
                            Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
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
            };

            eventsView.setAdapter(fire_adapter);
            fire_adapter.startListening();
        }

        public void onStop() {
            super.onStop();
            fire_adapter.stopListening();
        }

    public static class CardViewHolder extends RecyclerView.ViewHolder{

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

}
