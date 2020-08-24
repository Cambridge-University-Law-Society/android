package com.example.culs.fragments;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.culs.R;
import com.example.culs.helpers.Card;
import com.example.culs.helpers.EventDecorator;
import com.example.culs.helpers.GlideApp;
import com.example.culs.helpers.OneDayDecorators;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

//import java.time.LocalDate;

public class CalendarFragment extends Fragment implements OnDateSelectedListener {
    private RecyclerView eventsView;
    private SwipeRefreshLayout swipeContainer;

    //you can get an instance of the authentication by doing .getInstance()
    private FirebaseAuth mFirebaseAuth;
    //this will get the current instance of the document in cloud firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //this retrieves the entire document with this specific uid
    private CollectionReference eventsReference = db.collection("Events");

    private List<String> eventsid = new ArrayList<>(); //this kind of declaration of a list allows the list to be typecast into any other type of list
    private ArrayList<DocumentReference> eventsDocRef = new ArrayList<DocumentReference>();
    private ArrayList<DocumentReference> eventsDocRef1 = new ArrayList<DocumentReference>();
    SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm");

    private ArrayList<String> dateTime = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    //card stuff
    private ArrayList<Card> cards = new ArrayList<Card>();
    private FirestoreRecyclerAdapter<Card, CardViewHolder> fire_adapter;

    //private List<EventDay> events = new ArrayList<>();

    private final OneDayDecorators oneDayDecorators = new OneDayDecorators();

    private MaterialCalendarView calendarView;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);
        setHasOptionsMenu(true);

        calendarView = (MaterialCalendarView) v.findViewById(R.id.calendarView);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        eventsView = (RecyclerView) v.findViewById(R.id.cardlist);

        eventsView.setHasFixedSize(true);
        eventsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        calendarView.addDecorators(
                oneDayDecorators
        );

        addEventDots(calendarView);

        //call data load to load cards into the RecyclerView
        getDate(calendarView);

        /*final LocalDate instance = LocalDate.now();
        //calendarView.setOnDateChangedListener(this);
        //handling the cards recyclerview
        final CardAdapter adapter = new CardAdapter(getActivity() , R.layout.card_item, cards, this);
        //call data load to load into the adapter
        //UNCOMMENT THIS TO GET THE CARDS CODE
        // getDate(calendarView, adapter);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDate(calendarView, adapter);
            }
        });
        adapter.notifyDataSetChanged();
        //eventsView.setAdapter(adapter);
        //eventsView.setLayoutManager(new LinearLayoutManager(getActivity()));*/


        new eventsListGenerator().executeOnExecutor(Executors.newSingleThreadExecutor());

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.app_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //DONE:READ SELECTED DATE AND DISPLAY ALL THE CARDS FOR THOSE EVENTS ON THAT DATE



    private void getDate(MaterialCalendarView c){
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

                    loadData(timestamp, timestamp1);
                    //Log.d(TAG1, timestamp.toString());
                    //Log.d(TAG1, timestamp1.toString());
                    //Toast.makeText(getActivity(), timestamp1.toString(), Toast.LENGTH_SHORT).show();
                } catch (ParseException ex){
                    Log.v("Exception", ex.getLocalizedMessage());
                }
            }
        });
    }

    private void loadData(Timestamp timestamp, Timestamp timestamp2){

        Query query = eventsReference.whereEqualTo("active", true).whereGreaterThan("date", timestamp).whereLessThan("date", timestamp2).orderBy("date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Card> options = new FirestoreRecyclerOptions.Builder<Card>().setQuery(query, Card.class).build();
        fire_adapter = new FirestoreRecyclerAdapter<Card, CardViewHolder>(options) {
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

            @NonNull
            @Override
            public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
                CardViewHolder cardViewHolder = new CardViewHolder(view);
                return cardViewHolder;
            }
        };
        eventsView.setAdapter(fire_adapter);
        fire_adapter.startListening();
    }

    /*@Override
    public void onStart(){
        super.onStart();
        getDate(calendarView);
        fire_adapter.startListening();

    }*/

//    @Override
//    public void onStop(){
//        super.onStop();
//        fire_adapter.stopListening();
//    }

    private void getEventDates(){
        Query events = eventsReference.whereEqualTo("active", true);

    }



        /*
        //DONE: extract from firebase a list of all the active events' id
        eventsReference.whereEqualTo("active", true).whereGreaterThan("date", timestamp).whereLessThan("date", timestamp2).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            //QueryDocumentSnapshot contains data read from a document in your Firebase Database as part of a query
                            //Since query results contain only existing documents, the exists property will always be true
                            for (QueryDocumentSnapshot document : task.getResult()){ //this is the syntax for a "for-each" loop (loops through each element in array)
                                eventsDocRef.add(eventsReference.document(document.getId())); //adds documentReference to eventsList
                                dateLocation.add(String.valueOf(eventsReference.document(document.getString("location"))));
                                eventsid.add(document.getId()); //adds documentid to eventsUid
                            }
                            Log.d(TAG, eventsDocRef.toString());
                            Log.d(TAG, eventsid.toString());
                            Log.d(TAG, dateLocation.toString());
                            //Toast.makeText(getActivity(), eventsDocRef.toString(), Toast.LENGTH_SHORT).show();
                        }else{
                            Log.d(TAG, "Error getting the documents", task.getException());
                        }
                    }
                });

        swipeContainer.setRefreshing(true);
        for(int i=0; i < eventsDocRef.size(); i++){
            eventsDocRef.get(i).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                Boolean eventActive = documentSnapshot.getBoolean("active");
                                if (eventActive) {
                                    String eventName = documentSnapshot.getString("name");
                                    String eventImage = documentSnapshot.getString("imageref");
                                    Timestamp timestamp = documentSnapshot.getTimestamp("date");
                                    Long utcTimestamp = timestamp.getSeconds();
                                    String eventDate = sdf.format(utcTimestamp);
                                    Date newDate = timestamp.toDate();
                                    //Log.d(TAG1, newDate.toString());
                                    String eventLocation = documentSnapshot.getString("location");
                                    String eventDescription = documentSnapshot.getString("description");
                                    //cards.add(new Card(eventName, eventDate, utcTimestamp, eventLocation, eventDescription, eventImage, friends, friendPics1, boolCase5));
                                    adapter.notifyDataSetChanged();
                                    return;
                                }
                            } else {
                                Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }*/

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

//    public void onCardClick(View v, int position, ArrayList<Card> cardList) {
//        Card currentCard = cardList.get(position);
//
//        Fragment nextFragment = new ExpandedEventFragment();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            nextFragment.setSharedElementEnterTransition(new DetailsTransition());
//            nextFragment.setEnterTransition(new android.transition.Fade());
//            nextFragment.setExitTransition(new android.transition.Fade());
//            nextFragment.setSharedElementReturnTransition(new DetailsTransition());
//        }
//
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("Current Card", currentCard);
//        nextFragment.setArguments(bundle);
//        // Step 2: Create an Intent to start the next Activity
//
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.addSharedElement(v.findViewById(R.id.event_pic_image_view), "expandedImage");
//        fragmentTransaction.addSharedElement(v.findViewById(R.id.event_description_text_view), "expandedDescrip");
//        fragmentTransaction.addSharedElement(v.findViewById(R.id.event_name_text_view), "expandedName");
//        fragmentTransaction.addSharedElement(v.findViewById(R.id.event_date_and_time_text_view), "expandedDNT");
//        fragmentTransaction.addSharedElement(v.findViewById(R.id.event_location_text_view), "expandedLoc");
//        fragmentTransaction.addSharedElement(v.findViewById(R.id.event_tag_holder), "expandedTagHold");
//        fragmentTransaction.addSharedElement(v.findViewById(R.id.tag_icon), "expandedTagIcon");
//        fragmentTransaction.addSharedElement(v.findViewById(R.id.tag_note), "expandedTagNote");
//        fragmentTransaction.replace(R.id.fragment_container, nextFragment);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
//    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        oneDayDecorators.setDate(date.getDate());
        widget.invalidateDecorators();

    }

    public void addEventDots(final MaterialCalendarView c) {
        //get the dates of all the events
        final Calendar calendar = Calendar.getInstance();
        final List<CalendarDay> list = new ArrayList<CalendarDay>();

        eventsReference.whereEqualTo("active", true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //QueryDocumentSnapshot contains data read from a document in your Firebase Database as part of a query
                            //Since query results contain only existing documents, the exists property will always be true
                            for (QueryDocumentSnapshot document : task.getResult()) { //this is the syntax for a "for-each" loop (loops through each element in array)
                                eventsDocRef1.add(eventsReference.document(document.getId())); //adds documentReference to eventsList
                                dateTime.add(String.valueOf(eventsReference.document(document.getString("location"))));
                                Log.d(TAG3, dateTime.toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting the documents", task.getException());
                        }
                    }
                    //convert to Calendar
                    //use addDecorators to add the dots
                });
    }

        //Log.d(TAG1, list.toString());
        //List<CalendarDay> eventDays = list;
        //c.addDecorators(new EventDecorator(Color.RED, eventDays));







    private class eventsListGenerator extends AsyncTask<Void, Void, List<CalendarDay>>{

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LocalDate temp = LocalDate.now().minusMonths(2);
            final ArrayList<CalendarDay> dates = new ArrayList<>();
            for(int i=0; i<30; i++){
                final CalendarDay day = CalendarDay.from(temp);
                dates.add(day);
                temp = temp.plusDays(5);
            }
            return dates;
        }

        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays){
            super.onPostExecute(calendarDays);

            //if (isFin)
            //calendarView.addDecorator(new EventDecorator(Color.RED, calendarDays));
        }
    }

    //TODO:SET A MARKER ON ALL THE DATES THAT HAVE AN EVENT
    //TODO:use dummy dates at the moment
    //TODO:add the real dates

    //TODO:add the My Events to the user

    //TODO:create the split calendar with All Events and My Events

    //TODO:read the My Events section and add circles to those dates to the My Events calendar and add the events cards

    //TODO:might have to create my own calendar






    /*

    //set a date to a calendar
        Calendar calendar = Calendar.getInstance();
        String date1 = "20 08 2020";
        long timestamp = 1596931200;

        calendar.setTimeInMillis(timestamp*1000);

        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy", Locale.ENGLISH);
        //calendar.setTime(format.parse(date1));
        //Date date = format.parse(date1);
        //calendar.setTimeInMillis(utcTimestamp * 1000);
        //events.add(new EventDay(calendar, R.drawable.ic_interested_button_off_24dp));
        //calendarView.setEvents(events);


        //TODO:extract from firebase the information about each event and put in list and log it -- TEST IT ANOTHER WAY
        for (int i = 0; i < eventsDocRef.size(); i++) {
            eventsDocRef.get(i).get() //first .get() will get the documentReference, and then the second get() will get the field info from that document
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Boolean eventActive = documentSnapshot.getBoolean("Active");
                                if (eventActive) {
                                    String eventName = documentSnapshot.getString("name");
                                    String eventImage = documentSnapshot.getString("ImageRef");

                                    //DONE: How do you get the timestamp converted into a standard date and time
                                    Timestamp timestamp = documentSnapshot.getTimestamp("date");
                                    Long utcTimestamp = timestamp.getSeconds(); //method 2
                                    Date date = new Date(utcTimestamp); //method 1
                                    String eventDate = sdf.format(utcTimestamp);
                                    String eventLocation = documentSnapshot.getString("location");
                                    String eventDescription = documentSnapshot.getString("description");
                                    //dateList.add(eventDate);
                                }
                            }
                            else {
                                Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        //TODO:extract the name and timestamp of each event from firebase - maybe store it in a dictionary
        //and set it so that when you click on a day, it tells you in the list view the name and time of the event

        for (String i : eventsid){
            Log.d(TAG, i);
            DocumentReference docRef = db.collection("Events").document(i);
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Event event = dataSnapshot.getValue(Event.class);
                            String location = event.location;
                            dateLocation.add(location);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                }
                });
        };
        Log.d(TAG1, dateLocation.toString());
*/








    /* ATTEMPT 2
        //create a list of timestamps that are read from the firebase database
        //loop through all the documents and read the field "date" in each one - and store that timestamp in this list
        eventsReference.whereEqualTo("Active", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                documentList.add(db.collection("Events").document(document.getId()));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        for (int i = 0; i < documentList.size(); i++) {
            documentList.get(i).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Boolean eventActive = documentSnapshot.getBoolean("Active");
                                if (eventActive) {
                                    Timestamp timestamp = documentSnapshot.getTimestamp("date");
                                    //add the timestamp from this event into this list
                                    timestamps.add(timestamp);
                                    return;
                                }
                            } else {
                                Toast.makeText(getActivity(), "Document does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        //somehow pass that timestamp into the calendar and set those dates to be highlighted
        //ValueEventListener will receive the data about date changes to an event location
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Calendar calendar = Calendar.getInstance();
                //need to try convert the timestamp into a year,month,date
                for (int i=0; i < timestamps.size(); i++){
                    String time = getDate(i);
                    String[] items1 = time.split("-");
                    int year= Integer.parseInt(items1[0]);
                    int month=Integer.parseInt(items1[1]);
                    int day=Integer.parseInt(items1[2]);

                    calendar.set(year,month,day);
                    events.add(new EventDay(calendar, R.drawable.ic_interested_button_off_24dp));
                }
                calendarView.setEvents(events);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };*/

        /* FIRST QUICK LITTLE CALENDARVIEW DEMO
        // CalendarView and TextView
        calendarView = (CalendarView) v.findViewById(R.id.calendarView);
        date_view = (TextView) v.findViewById(R.id.date_view);

        // Define the variable of CalendarView type and TextView type
        private TextView date_view;

        private CalendarView calendarView;
        private List<EventDay> eventDays = new ArrayList<>();

        // Add Listener in calendar
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            // In this Listener have one method and in this method we will get the value of DAYS, MONTH, YEARS
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Store the value of date with format in String type Variable
                // Add 1 in month because month index is start with 0
                String Date = dayOfMonth + "/" + (month + 1) + "/" + year;

                // set this date in TextView for Display
                date_view.setText(Date);
            }
        });*/

}
