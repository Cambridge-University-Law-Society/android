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

public class SponsorHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Sponsor sponsor;
    private Context context;
    private final ImageView sponsorPic;
    private final TextView sponsorName;
    private final TextView sponsorDescription;
    private final TextView sponsorTypes;
    private ArrayList<Sponsor> sponsorList = null;
    OnSponsorListener onSponsorListener;

    public SponsorHolder(Context context, View itemView, OnSponsorListener onSponsorListener, ArrayList<Sponsor> sponsors) {
        super(itemView);
        sponsorList = sponsors;
        this.context = context;
        this.sponsorName = (TextView) itemView.findViewById(R.id.sponsor_name_text_view);
        this.sponsorDescription = (TextView) itemView.findViewById(R.id.sponsor_description_text_view);
        this.sponsorTypes = (TextView) itemView.findViewById(R.id.sponsor_law_types);
        this.sponsorPic = (ImageView) itemView.findViewById(R.id.sponsor_logo_image_view);
        itemView.setOnClickListener(this);
        this.onSponsorListener = onSponsorListener;

    }

    public void bindSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
        this.sponsorName.setText(sponsor.getSponsorName());
        this.sponsorDescription.setText(sponsor.getSponsorDescription());
        this.sponsorTypes.setText(sponsor.getSponsorTypes());
        this.sponsorPic.setImageResource(sponsor.getSponsorLogo());
        //this.eventDescription.setText(sponsor.getSponsorLinks());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.sponsorPic.setTransitionName(sponsor.getSponsorName() + "_logo");
            this.sponsorName.setTransitionName(sponsor.getSponsorName() + "_name");
            this.sponsorDescription.setTransitionName(sponsor.getSponsorName() + "_description");
            this.sponsorTypes.setTransitionName(sponsor.getSponsorName() + "_types");
        }
        }

    @Override
    public void onClick(View v) {
        // 5. Handle the onClick event for the ViewHolder
            onSponsorListener.onSponsorClick(v, getAdapterPosition(), sponsorList);
    }

    public interface OnSponsorListener{
        void onSponsorClick(View v, int position, ArrayList<Sponsor> sponsorList);
    }

}
