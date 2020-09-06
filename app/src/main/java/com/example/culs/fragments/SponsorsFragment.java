package com.example.culs.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.culs.R;
import com.example.culs.helpers.Card;
import com.example.culs.helpers.CustomAdapter;
import com.example.culs.helpers.Post;
import com.example.culs.helpers.PostType;
import com.example.culs.helpers.Sponsor;
import com.example.culs.helpers.User;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SponsorsFragment extends Fragment {
    private RecyclerView sponsorsView;
    private View rootView;
    private CustomAdapter customAdapter;
    private FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
    private List<PostType> types = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sponsors, container, false);
        setupCustomAdapter(rootView);
        setupToolbarOptionsMenu(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        types.clear();
        getListItems();
        customAdapter.notifyDataSetChanged();
    }

    public void onStop() {
        super.onStop();
    }

    private void setupCustomAdapter(View rootView) {
        sponsorsView = (RecyclerView) rootView.findViewById(R.id.sponsor_list);
        sponsorsView.setHasFixedSize(true);
        sponsorsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        customAdapter = new CustomAdapter(types);
        sponsorsView.setAdapter(customAdapter);

        customAdapter.setOnSponsorItemClickListener(new CustomAdapter.OnSponsorItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Sponsor clickedPost = (Sponsor) types.get(position);
                onSponsorClick(v, clickedPost);
        }
        });
    }

    private void getListItems() {

        mFirebaseFirestore.collection("Sponsors").orderBy("name", Query.Direction.ASCENDING)
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
                                    Sponsor sponsorAdded = dc.getDocument().toObject(Sponsor.class);
                                    sponsorAdded.setSponsorID(dc.getDocument().getId());
                                    types.add(dc.getNewIndex(), sponsorAdded);
                                    break;
                                case MODIFIED:
                                    types.remove(dc.getOldIndex());
                                    Sponsor sponsorChanged = dc.getDocument().toObject(Sponsor.class);
                                    sponsorChanged.setSponsorID(dc.getDocument().getId());
                                    types.add(dc.getNewIndex(), sponsorChanged);
                                    break;
                                case REMOVED:
                                    types.remove(dc.getOldIndex());
                                    break;
                                default:
                                    break;
                            }
                        }
                        customAdapter.notifyDataSetChanged();
                    }
                });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.app_bar_two, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setupToolbarOptionsMenu(View rootView) {
        setHasOptionsMenu(true);
        Toolbar myToolbar = rootView.findViewById(R.id.my_sponsors_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }

    public void onSponsorClick(View v, Sponsor currentSponsor) {

        Fragment nextFragment = new ExpandedSponsorsFragment();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nextFragment.setSharedElementEnterTransition(new DetailsTransition());
            nextFragment.setEnterTransition(new android.transition.Fade());
            nextFragment.setExitTransition(new android.transition.Fade());
            nextFragment.setSharedElementReturnTransition(new DetailsTransition());
        }

        Bundle bundle = new Bundle();
        bundle.putString("Current Sponsor ID", currentSponsor.getSponsorID());
        nextFragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, nextFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}


