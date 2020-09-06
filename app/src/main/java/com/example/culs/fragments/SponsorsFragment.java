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
import com.example.culs.helpers.Sponsor;
import com.example.culs.helpers.SponsorAdapter;
import com.example.culs.helpers.SponsorHolder;
import com.google.firebase.firestore.DocumentReference;
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

public class SponsorsFragment extends Fragment implements SponsorHolder.OnSponsorListener{
    private RecyclerView sponsorsView;
    private String[] linkstring = {"banana", "apple"};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sponsors, container, false);
        setHasOptionsMenu(true);//Make sure you have this line of code.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setEnterTransition(new android.transition.Slide());
            setExitTransition(new android.transition.Slide());
        }

        final ArrayList<Sponsor> sponsors = new ArrayList<Sponsor>();

        SponsorAdapter adapter = new SponsorAdapter(getActivity() , R.layout.sponsor_item, sponsors, this);
        sponsorsView = (RecyclerView) rootView.findViewById(R.id.sponsor_list);
        sponsorsView.setAdapter(adapter);
        sponsorsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /*Toolbar myToolbar = rootView.findViewById(R.id.my_sponsors_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_search_icon_24dp);// set drawable icon
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.app_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSponsorClick(View v, int position, ArrayList<Sponsor> sponsorList) {
        Sponsor currentSponsor = sponsorList.get(position);

        Toast.makeText(getActivity(), currentSponsor.getSponsorName(), Toast.LENGTH_LONG).show();
    }

}


