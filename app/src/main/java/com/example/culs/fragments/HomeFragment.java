package com.example.culs.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.culs.R;
import com.example.culs.helpers.Card;
import com.example.culs.helpers.CardHolder;
import com.example.culs.helpers.FireRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
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
import com.google.firebase.firestore.Query;

public class HomeFragment extends Fragment implements CardHolder.OnCardListener {
    private RecyclerView eventsView;
    private View rootView;
    private FireRecyclerAdapter fire_adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

        @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        setupFireAdapter(rootView);
        setupToolbarOptionsMenu(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
            fire_adapter.startListening();
        }

    public void onStop() {
        super.onStop();
        fire_adapter.stopListening();
    }

    private void setupFireAdapter(View rootView) {
        eventsView = (RecyclerView) rootView.findViewById(R.id.list);
        eventsView.setHasFixedSize(true);
        eventsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Query query = FirebaseFirestore.getInstance().collection("Events").orderBy("date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Card> options = new FirestoreRecyclerOptions.Builder<Card>().setQuery(query, Card.class).build();
        fire_adapter = new FireRecyclerAdapter(options);
        eventsView.setAdapter(fire_adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.app_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setupToolbarOptionsMenu(View rootView) {
        setHasOptionsMenu(true);
        Toolbar myToolbar = rootView.findViewById(R.id.my_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_search_icon_24dp);// set drawable icon
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
