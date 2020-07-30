package com.example.culs.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class SponsorAdapter extends RecyclerView.Adapter<SponsorHolder> {

    private final List<Sponsor> sponsors;
    private Context context;
    private int itemResource;
    private SponsorHolder.OnSponsorListener mOnSponsorListener;

    public SponsorAdapter(Context context, int itemResource, List<Sponsor> sponsors, SponsorHolder.OnSponsorListener onSponsorListener) {
        // 1. Initialize our adapter
        this.sponsors = sponsors;
        this.context = context;
        this.itemResource = itemResource;
        this.mOnSponsorListener = onSponsorListener;
    }

    @Override
    public SponsorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResource, parent, false);
        return new SponsorHolder(this.context, view, mOnSponsorListener, (ArrayList<Sponsor>) sponsors);
    }

    @Override
    public void onBindViewHolder(SponsorHolder holder, int position) {
        // 5. Use position to access the correct Bakery object
        Sponsor sponsor = this.sponsors.get(position);
        // 6. Bind the bakery object to the holder
        holder.bindSponsor(sponsor);
    }

    @Override
    public int getItemCount() {
        return this.sponsors.size();
    }
}

