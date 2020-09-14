package com.example.culs.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.culs.helpers.Card;
import com.example.culs.helpers.CustomAdapter;
import com.example.culs.helpers.EventDecorator;
import com.example.culs.helpers.GetEventDate;
import com.example.culs.helpers.OneDayDecorators;
import com.example.culs.helpers.PostType;

import com.example.culs.R;

import com.example.culs.helpers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import java.time.LocalDate;

public class CalendarFragment extends Fragment implements OnDateSelectedListener {
    private RecyclerView eventsView;
    private MaterialCalendarView allEventsCalendar;
    private MaterialCalendarView myEventsCalendar;
    private Button allEventsbtn;
    private Button myEventsbtn;
    private CardView allEventsButton;
    private CardView myEventsButton;
    private TextView allEventsText, myEventsText;
    private ImageView allEventsBar, myEventsBar;
    private ListenerRegistration CalMyEventsReg, CalEventsReg, CalMyUserReg, CalUserReg;
    private Query CalMyEvents, CalEvents;

    private DocumentReference CalMyUser, CalUser;

    //instantiate the short animation between the all events calendar view and myevents calendar view
    private int shortAnimationDuration;

    //you can get an instance of the authentication by doing .getInstance()
    private FirebaseAuth mFirebaseAuth;
    //this will get the current instance of the document in cloud firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //this retrieves the entire document with this specific uid
    private CollectionReference eventsReference = db.collection("Events");
    private ArrayList<String> uninterested = new ArrayList<>();
    private ArrayList<String> interested = new ArrayList<>();
    //this retrieves the entire document with this specific uid
    final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DocumentReference userDocRef = db.collection("users").document(userid);
    public static User currentUser;
    private List<String> eventsid; //this kind of declaration of a list allows the list to be typecast into any other type of list

    private ArrayList<DocumentReference> eventsDocRef = new ArrayList<DocumentReference>();
    private ArrayList<DocumentReference> eventsDocRef1 = new ArrayList<DocumentReference>();
    SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm");

    private ArrayList<Date> dateTime = new ArrayList<Date>();
    private ArrayAdapter<String> adapter;

    //card stuff
    private ArrayList<Card> cards = new ArrayList<Card>();
    private CustomAdapter customAdapter;
    private List<PostType> types = new ArrayList<>();

    private ArrayList<Integer> events = new ArrayList<>();

    private final OneDayDecorators oneDayDecorators = new OneDayDecorators();

    private GetEventDate dates;

    private String TAG = "CalendarFragment";
    private String TAG1 = "Rajan";
    private String TAG3 = "RAJAN1";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);
        setHasOptionsMenu(true);
        setupCustomAdapter(v);

        allEventsCalendar = (MaterialCalendarView) v.findViewById(R.id.allEventsCalendarView);
        myEventsCalendar = (MaterialCalendarView) v.findViewById(R.id.myEventsCalendarView);

        allEventsBar = v.findViewById(R.id.allEventBar);
        myEventsBar = v.findViewById(R.id.myEventsBar);

        //keep myEventsCalendar invisible on start
        myEventsCalendar.setVisibility(View.GONE);

        allEventsButton = v.findViewById(R.id.alleventsbutton);
        allEventsText = v.findViewById(R.id.allEvents);
        allEventsButton.setCardBackgroundColor(Color.TRANSPARENT);
        allEventsButton.setRadius(6);
        allEventsText.setTextColor(Color.WHITE);
        allEventsBar.setVisibility(View.VISIBLE);

        myEventsButton = v.findViewById(R.id.myeventsbutton);
        myEventsText = v.findViewById(R.id.myEvents);
        myEventsButton.setCardBackgroundColor(Color.TRANSPARENT);
        myEventsText.setTextColor(Color.WHITE);
        myEventsBar.setVisibility(View.INVISIBLE);

        /*allEventsbtn = (Button) v.findViewById(R.id.alleventsbtn);
        allEventsbtn.setBackgroundColor(Color.LTGRAY);
        allEventsbtn.setTextColor(Color.MAGENTA);
        myEventsbtn = (Button) v.findViewById(R.id.myeventsbtn);
        myEventsbtn.setBackgroundColor(Color.TRANSPARENT);
        myEventsbtn.setTextColor(Color.WHITE);*/

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        allEventsCalendar.addDecorators(
                oneDayDecorators
        );

        myEventsCalendar.addDecorators(
                oneDayDecorators
        );

        //call data load to load cards into the RecyclerView
        getDate(allEventsCalendar, v);
        getMyEventsDate(myEventsCalendar, v);

        addAllEventDots(allEventsCalendar);
        addMyEventsDots(myEventsCalendar);

        allEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CalMyEventsReg != null) {
                    CalMyEventsReg.remove();
                }

                if(CalMyUserReg != null) {
                    CalMyUserReg.remove();
                }

                for(String i : interested){
                    DocumentReference eventDocRef = db.collection("Events").document(i);
                    eventDocRef.update("attendees", FieldValue.arrayUnion(userid));
                }

                for(String i : uninterested){
                    DocumentReference eventDocRef = db.collection("Events").document(i);
                    eventDocRef.update("attendees", FieldValue.arrayRemove(userid));
                }


                if (allEventsCalendar.getVisibility() == View.GONE) {
                    crossFade(myEventsCalendar, allEventsCalendar);
                    crossFade(myEventsBar,allEventsBar);
                    allEventsButton.setCardBackgroundColor(Color.TRANSPARENT);
                    allEventsText.setTextColor(Color.WHITE);
                    allEventsButton.setRadius(8);
                    //allEventsBar.setVisibility(View.VISIBLE);

                    myEventsButton.setCardBackgroundColor(Color.TRANSPARENT);
                    myEventsText.setTextColor(Color.WHITE);
                    myEventsButton.setRadius(8);
                    //myEventsBar.setVisibility(View.INVISIBLE);
                    if (customAdapter != null) {
                        types.clear();
                        customAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        myEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CalEventsReg != null) {
                    CalEventsReg.remove();
                }
                if(CalUserReg != null) {
                    CalUserReg.remove();
                }

                for(String i : interested){
                    DocumentReference eventDocRef = db.collection("Events").document(i);
                    eventDocRef.update("attendees", FieldValue.arrayUnion(userid));
                }

                for(String i : uninterested){
                    DocumentReference eventDocRef = db.collection("Events").document(i);
                    eventDocRef.update("attendees", FieldValue.arrayRemove(userid));
                }

                if (myEventsCalendar.getVisibility() == View.GONE) {
                    crossFade(allEventsCalendar, myEventsCalendar);
                    myEventsButton.setCardBackgroundColor(Color.TRANSPARENT);
                    myEventsText.setTextColor(Color.WHITE);
                    myEventsButton.setRadius(8);
                    //myEventsBar.setVisibility(View.VISIBLE);
                    crossFade(allEventsBar, myEventsBar);

                    allEventsButton.setCardBackgroundColor(Color.TRANSPARENT);
                    allEventsText.setTextColor(Color.WHITE);
                    allEventsButton.setRadius(8);
                    //allEventsBar.setVisibility(View.INVISIBLE);
                    if (customAdapter != null) {
                        types.clear();
                        customAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        //new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());

        return v;
    }

    public void onStop() {
        super.onStop();
        if(CalMyEventsReg != null) {
            CalMyEventsReg.remove();
        }
        if(CalEventsReg != null) {
            CalEventsReg.remove();
        }
        if(CalUserReg != null) {
            CalUserReg.remove();
        }
        if(CalMyUserReg != null) {
            CalMyUserReg.remove();
        }

        for(String i : interested){
            DocumentReference eventDocRef = db.collection("Events").document(i);
            eventDocRef.update("attendees", FieldValue.arrayUnion(userid));
        }

        for(String i : uninterested){
            DocumentReference eventDocRef = db.collection("Events").document(i);
            eventDocRef.update("attendees", FieldValue.arrayRemove(userid));
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setupCustomAdapter(View rootView) {
        //eventsView = (RecyclerView) rootView.findViewById(R.id.cardlist);
        //eventsView.setHasFixedSize(true);
        //eventsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //eventsView.setAdapter(customAdapter);
        customAdapter = new CustomAdapter(types);

        customAdapter.setOnEventItemClickListener(new CustomAdapter.OnEventItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Card clickedCard = (Card) types.get(position);
                onCardClick(v, clickedCard);
            }

            @Override
            public void onInterestedClick(View v, int position) {
                Card intCard = (Card) types.get(position);
                if(currentUser.getMyevents() == null){
                    intCard.setInterested(true);

                    if(!interested.contains(intCard.getID())) {
                        interested.add(intCard.getID());
                    }
                    while(uninterested.contains(intCard.getID())) {
                        uninterested.remove(intCard.getID());
                    }

                    userDocRef.update("myevents", FieldValue.arrayUnion(intCard.getID()));
                    customAdapter.notifyItemChanged(position);
                    FirebaseMessaging.getInstance().subscribeToTopic(intCard.getID())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else if(currentUser.getMyevents().contains(intCard.getID())){
                    intCard.setInterested(false);
                    customAdapter.notifyItemChanged(position);

                    if(!uninterested.contains(intCard.getID())) {
                        uninterested.add(intCard.getID());
                    }
                    while(interested.contains(intCard.getID())) {
                        interested.remove(intCard.getID());
                    }

                    userDocRef.update("myevents", FieldValue.arrayRemove(intCard.getID()));
                    customAdapter.notifyItemChanged(position);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(intCard.getID())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    intCard.setInterested(true);
                    customAdapter.notifyItemChanged(position);

                    if(!interested.contains(intCard.getID())) {
                        interested.add(intCard.getID());
                    }
                    while(uninterested.contains(intCard.getID())) {
                        uninterested.remove(intCard.getID());
                    }

                    userDocRef.update("myevents", FieldValue.arrayUnion(intCard.getID()));
                    customAdapter.notifyItemChanged(position);
                    FirebaseMessaging.getInstance().subscribeToTopic(intCard.getID())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void crossFade(final View fadeOutView, View fadeInView) {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        fadeInView.setAlpha(0f);
        fadeInView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        fadeInView.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        fadeOutView.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeOutView.setVisibility(View.GONE);
                    }
                });
    }

    private void getDate(MaterialCalendarView c, final View view) {
        //this will return the date of the selected day in a Date format

        c.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int day = date.getDay();
                int month = date.getMonth();
                int year = date.getYear();

                int day2 = date.getDay() + 1;

                //currentDate = date.getDate();

                String currentDate = day + "-" + month + "-" + year;
                String nextDate = day2 + "-" + month + "-" + year;
                //Toast.makeText(getActivity(), nextDate, Toast.LENGTH_SHORT).show();
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-M-yyyy");
                try {
                    Date d = sdf1.parse(currentDate);
                    Date d2 = sdf1.parse(nextDate);
                    Timestamp timestamp = new Timestamp(d);
                    Timestamp timestamp1 = new Timestamp(d2);

                    loadData(timestamp, timestamp1, view);
                    types.clear();
                    customAdapter.notifyDataSetChanged();
                    //Log.d(TAG1, timestamp.toString());
                    //Log.d(TAG1, timestamp1.toString());
                    //Toast.makeText(getActivity(), timestamp1.toString(), Toast.LENGTH_SHORT).show();
                } catch (ParseException ex) {
                    Log.v("Exception", ex.getLocalizedMessage());
                }
            }
        });
    }

    private void getMyEventsDate(MaterialCalendarView c, final View view) {
        //this will return the date of the selected day in a Date format

        c.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int day = date.getDay();
                int month = date.getMonth();
                int year = date.getYear();

                int day2 = date.getDay() + 1;

                //currentDate = date.getDate();

                String currentDate = day + "-" + month + "-" + year;
                String nextDate = day2 + "-" + month + "-" + year;
                //Toast.makeText(getActivity(), nextDate, Toast.LENGTH_SHORT).show();
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-M-yyyy");
                try {
                    Date d = sdf1.parse(currentDate);
                    Date d2 = sdf1.parse(nextDate);
                    Timestamp timestamp = new Timestamp(d);
                    Timestamp timestamp1 = new Timestamp(d2);

                    loadMyEventsData(timestamp, timestamp1, view);
                    types.clear();
                    customAdapter.notifyDataSetChanged();
                    //Log.d(TAG1, d.toString());
                    //Log.d(TAG1, d2.toString());
                    //Toast.makeText(getActivity(), timestamp1.toString(), Toast.LENGTH_SHORT).show();
                } catch (ParseException ex) {
                    Log.v("Exception", ex.getLocalizedMessage());
                }
            }
        });
    }


    private void loadData(Timestamp timestamp, Timestamp timestamp2, View rootView) {

        eventsView = (RecyclerView) rootView.findViewById(R.id.cardlist);
        eventsView.setHasFixedSize(true);
        eventsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        CalUserReg = userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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

        CalEventsReg = eventsReference.whereEqualTo("active", true).whereGreaterThan("date", timestamp)
                .whereLessThan("date", timestamp2).orderBy("date", Query.Direction.ASCENDING)
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
                                    final Card cardAdded = dc.getDocument().toObject(Card.class);
                                    cardAdded.setID(dc.getDocument().getId());
                                    if(currentUser.getMyevents() == null){
                                        cardAdded.setInterested(false);
                                    } else if(currentUser.getMyevents().contains(dc.getDocument().getId())){
                                        cardAdded.setInterested(true);
                                    } else {
                                        cardAdded.setInterested(false);
                                    }

                                    db.collection("Sponsors")
                                            .whereEqualTo("name", cardAdded.getSponsor())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            cardAdded.setEventSponsorID(document.getId());
                                                        }
                                                    } else {
                                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        if (!dc.getDocument().getBoolean("archived")) {
                                            types.add(cardAdded);
                                            Collections.sort(types);
                                            int index = 0;

                                            for(int i = 0; i < types.size(); i++){
                                                if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                                    Card testCard = (Card) types.get(i);
                                                    if(testCard.getID().equals(cardAdded.getID())){
                                                        index = i;
                                                    }
                                                }
                                            }

                                        } else {
                                            for(int i = 0; i < types.size(); i++){
                                                if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                                    Card testCard = (Card) types.get(i);
                                                    if(testCard.getID().equals(cardAdded.getID())){
                                                        types.remove(i);
                                                    }
                                                }
                                            }
                                        }
                                    break;
                                case MODIFIED:
                                    final Card cardChanged = dc.getDocument().toObject(Card.class);
                                    cardChanged.setID(dc.getDocument().getId());
                                    int index = 0;

                                    for(int i = 0; i < types.size(); i++){
                                        if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                            Card testCard = (Card) types.get(i);
                                            if(testCard.getID().equals(cardChanged.getID())){
                                                types.remove(i);
                                                index = i;
                                            }
                                        }
                                    }

                                    if(currentUser.getMyevents() == null){
                                        cardChanged.setInterested(false);
                                    } else if(currentUser.getMyevents().contains(dc.getDocument().getId())){
                                        cardChanged.setInterested(true);
                                    } else {
                                        cardChanged.setInterested(false);
                                    }

                                    db.collection("Sponsors")
                                            .whereEqualTo("name", cardChanged.getSponsor())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            cardChanged.setEventSponsorID(document.getId());
                                                        }
                                                    } else {
                                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        if (!dc.getDocument().getBoolean("archived")) {
                                            types.add(index, cardChanged);
                                            Collections.sort(types);
                                        } else {
                                            for(int i = 0; i < types.size(); i++){
                                                if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                                    Card testCard = (Card) types.get(i);
                                                    if(testCard.getID().equals(cardChanged.getID())){
                                                        types.remove(i);
                                                    }
                                                }
                                            }
                                        }
                                    break;
                                case REMOVED:
                                    final Card cardRemoved = dc.getDocument().toObject(Card.class);
                                    cardRemoved.setID(dc.getDocument().getId());

                                    for(int i = 0; i < types.size(); i++){
                                        if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                            Card testCard = (Card) types.get(i);
                                            if(testCard.getID().equals(cardRemoved.getID())){
                                                types.remove(i);

                                            }
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        eventsView.setAdapter(customAdapter);
                        customAdapter.notifyDataSetChanged();
                    }
                });

        /*eventsView = (RecyclerView) rootView.findViewById(R.id.cardlist);
        eventsView.setHasFixedSize(true);
        eventsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Query query = eventsReference.whereEqualTo("active", true).whereGreaterThan("date", timestamp)
                .whereLessThan("date", timestamp2).orderBy("date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Card> options = new FirestoreRecyclerOptions.Builder<Card>().setQuery(query, Card.class).build();
        fire_adapter = new FireRecyclerAdapter(options);
        eventsView.setAdapter(fire_adapter);

        fire_adapter.startListening();*/

        /*fire_adapter = new FirestoreRecyclerAdapter<Card, CardViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CardViewHolder holder, int position, @NonNull Card model) {
                SimpleDateFormat spf=new SimpleDateFormat("EEE, dd MMM 'at' HH:mm");

                DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
                String eventID = snapshot.getId();
                holder.eventName.setText(model.getName());
                holder.eventLocation.setText(model.getLocation());
                //TRY TO ADD THIS TO A LIST!!
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

            @NonNull
            @Override
            public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
                CardViewHolder cardViewHolder = new CardViewHolder(view);
                return cardViewHolder;
            }
        };
        eventsView.setAdapter(fire_adapter);
        fire_adapter.startListening();*/

    }

    private void loadMyEventsData(Timestamp timestamp, Timestamp timestamp2, View rootView) {
        eventsView = (RecyclerView) rootView.findViewById(R.id.cardlist);
        eventsView.setHasFixedSize(true);
        eventsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        CalMyUserReg = userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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

        CalMyEvents = eventsReference.whereEqualTo("active", true).whereArrayContains("attendees", userid)
                .whereGreaterThan("date", timestamp).whereLessThan("date", timestamp2).orderBy("date", Query.Direction.ASCENDING);
        CalMyEventsReg = CalMyEvents
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
                                    final Card cardAdded = dc.getDocument().toObject(Card.class);
                                    cardAdded.setID(dc.getDocument().getId());
                                    if(currentUser.getMyevents() == null){
                                        cardAdded.setInterested(false);
                                    } else if(currentUser.getMyevents().contains(dc.getDocument().getId())){
                                        cardAdded.setInterested(true);
                                    } else {
                                        cardAdded.setInterested(false);
                                    }

                                    db.collection("Sponsors")
                                            .whereEqualTo("name", cardAdded.getSponsor())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            cardAdded.setEventSponsorID(document.getId());
                                                        }
                                                    } else {
                                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    if (dc.getDocument().getBoolean("active")) {
                                        if (!dc.getDocument().getBoolean("archived")) {
                                            types.add(cardAdded);
                                            Collections.sort(types);
                                            int index = 0;

                                            for(int i = 0; i < types.size(); i++){
                                                if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                                    Card testCard = (Card) types.get(i);
                                                    if(testCard.getID().equals(cardAdded.getID())){
                                                        index = i;
                                                    }
                                                }
                                            }

                                        } else {
                                            for(int i = 0; i < types.size(); i++){
                                                if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                                    Card testCard = (Card) types.get(i);
                                                    if(testCard.getID().equals(cardAdded.getID())){
                                                        types.remove(i);
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        for(int i = 0; i < types.size(); i++){
                                            if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                                Card testCard = (Card) types.get(i);
                                                if(testCard.getID().equals(cardAdded.getID())){
                                                    types.remove(i);
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case MODIFIED:
                                    final Card cardChanged = dc.getDocument().toObject(Card.class);
                                    cardChanged.setID(dc.getDocument().getId());
                                    int index = 0;

                                    for(int i = 0; i < types.size(); i++){
                                        if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                            Card testCard = (Card) types.get(i);
                                            if(testCard.getID().equals(cardChanged.getID())){
                                                types.remove(i);
                                                index = i;
                                            }
                                        }
                                    }

                                    if(currentUser.getMyevents() == null){
                                        cardChanged.setInterested(false);
                                    } else if(currentUser.getMyevents().contains(dc.getDocument().getId())){
                                        cardChanged.setInterested(true);
                                    } else {
                                        cardChanged.setInterested(false);
                                    }

                                    db.collection("Sponsors")
                                            .whereEqualTo("name", cardChanged.getSponsor())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            cardChanged.setEventSponsorID(document.getId());
                                                        }
                                                    } else {
                                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    if (dc.getDocument().getBoolean("active")) {
                                        if (!dc.getDocument().getBoolean("archived")) {
                                            types.add(index, cardChanged);

                                        } else {
                                            for(int i = 0; i < types.size(); i++){
                                                if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                                    Card testCard = (Card) types.get(i);
                                                    if(testCard.getID().equals(cardChanged.getID())){
                                                        types.remove(i);
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        for(int i = 0; i < types.size(); i++){
                                            if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                                Card testCard = (Card) types.get(i);
                                                if(testCard.getID().equals(cardChanged.getID())){
                                                    types.remove(i);
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case REMOVED:
                                    final Card cardRemoved = dc.getDocument().toObject(Card.class);
                                    cardRemoved.setID(dc.getDocument().getId());

                                    for(int i = 0; i < types.size(); i++){
                                        if (types.get(i).getPostType() == PostType.TYPE_EVENT ){
                                            Card testCard = (Card) types.get(i);
                                            if(testCard.getID().equals(cardRemoved.getID())){
                                                types.remove(i);
                                            }
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        eventsView.setAdapter(customAdapter);
                        customAdapter.notifyDataSetChanged();
                    }
                });

        /*Query query = eventsReference.whereEqualTo("active", true).whereArrayContains("attendees", userid)
                .whereGreaterThan("date", timestamp).whereLessThan("date", timestamp2).orderBy("date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Card> options = new FirestoreRecyclerOptions.Builder<Card>().setQuery(query, Card.class).build();
        fire_adapter = new FireRecyclerAdapter(options);
        eventsView.setAdapter(fire_adapter);

        fire_adapter.startListening();*/

    }

    public void onCardClick(View v, Card currentCard) {

        Fragment nextFragment = new ExpandedEventFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Current Card ID", currentCard.getID());
        nextFragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, nextFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        oneDayDecorators.setDate(date.getDate());
        widget.invalidateDecorators();
    }

    public void addAllEventDots(final MaterialCalendarView calendarView){
        //method use getData and get the timestamp straight from that Map and then use get() on the map to get the date value
        eventsReference.whereEqualTo("active", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.d(TAG, document.getId() + " => " + document.getData());
                        String documentID = document.getId();
                        Map<String, Object> documentData = document.getData();
                        Date dates = document.getTimestamp("date").toDate();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dates);
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int color = Color.WHITE;
                        EventDecorator decorator = new EventDecorator(color, year, month, day);
                        calendarView.addDecorator(decorator);
                        Log.d(TAG, String.valueOf(year));
                        Log.d(TAG, String.valueOf(month));
                        Log.d(TAG, String.valueOf(day));
                        //Object date = documentData.get("date");
                        //Integer times = date.hashCode();
                        //og.d(TAG, date.toString());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void addMyEventsDots(final MaterialCalendarView calendarView){
        //method use getData and get the timestamp straight from that Map and then use get() on the map to get the date value
        eventsReference.whereEqualTo("active", true).whereArrayContains("attendees", userid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.d(TAG, document.getId() + " => " + document.getData());
                        String documentID = document.getId();
                        Map<String, Object> documentData = document.getData();
                        Date dates = document.getTimestamp("date").toDate();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dates);
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int color = Color.WHITE;
                        EventDecorator decorator = new EventDecorator(color, year, month, day);
                        calendarView.addDecorator(decorator);
                        Log.d(TAG, String.valueOf(year));
                        Log.d(TAG, String.valueOf(month));
                        Log.d(TAG, String.valueOf(day));
                        //Object date = documentData.get("date");
                        //Integer times = date.hashCode();
                        //og.d(TAG, date.toString());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

//    public void addEventDots(final MaterialCalendarView c) {
//        //get the dates of all the events
//        final Calendar calendar = Calendar.getInstance();
//        final List<CalendarDay> list = new ArrayList<CalendarDay>();
//
//        /*eventsReference.whereEqualTo("active", true).get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()){
//                            QuerySnapshot documentSnapshot = task.getResult();
//
//                        }
//                    }
//                })*/
//
//
//        eventsReference.whereEqualTo("active", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            //QueryDocumentSnapshot contains data read from a document in your Firebase Database as part of a query
//                            //Since query results contain only existing documents, the exists property will always be true
//                            for (QueryDocumentSnapshot document : task.getResult()) { //this is the syntax for a "for-each" loop (loops through each element in array)
//                                eventsDocRef1.add(eventsReference.document(document.getId())); //adds documentReference to eventsList
//                                //dateTime.add(String.valueOf(eventsReference.document(document.getString("location"))));
//                                //Log.d(TAG3, dateTime.toString());
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting the documents", task.getException());
//                        }
//                    }
//                    //convert to Calendar
//                    //use addDecorators to add the dots
//                });
//        Log.d(TAG3, eventsDocRef1.toString());
//        final ArrayList<CalendarDay> dates = new ArrayList<>();
//        for (int i = 0; i < eventsDocRef.size(); i++) {
//            eventsDocRef1.get(i).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                @Override
//                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                    if (value.exists()) {
//                        Timestamp timestamp = value.getTimestamp("date");
//                        Date timeDate = timestamp.toDate();
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTime(timeDate);
//                        int year = calendar.get(Calendar.YEAR);
//                        int month = calendar.get(Calendar.MONTH);
//                        int day = calendar.get(Calendar.DAY_OF_MONTH);
//                        int color = R.color.colorAccent;
//                        final CalendarDay day1 = CalendarDay.from(year, month, day);
//                        dates.add(day1);
//                    }
//                }
//            });
//        }
//        Log.d(TAG3, dates.toString());
//    }

}