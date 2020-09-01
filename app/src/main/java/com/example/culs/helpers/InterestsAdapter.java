package com.example.culs.helpers;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.culs.R;

import java.util.ArrayList;

public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.MyViewHolder> {

    private ArrayList<InterestsModel> interestsModel;
    private OnNoteListener mOnNoteListener; //this sets the onNoteListener to the viewholder


    public InterestsAdapter(ArrayList<InterestsModel> data, OnNoteListener onNoteListener){
        this.interestsModel = data;
        this.mOnNoteListener = onNoteListener;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interests_card_item, parent, false);

        //view.setOnClickListener()

        MyViewHolder myViewHolder = new MyViewHolder(view, mOnNoteListener); //pass in the global onNoteListener
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        TextView interests_tag = holder.interests_tag;
        interests_tag.setText(interestsModel.get(position).getInterests());

        /*holder.interests_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.interests_card.getCardElevation() == 1){
                    holder.interests_card.setCardElevation(2);
                    holder.interests_card.setCardBackgroundColor(Color.MAGENTA);
                }
                if (holder.interests_card.getCardElevation() == 2){
                    holder.interests_card.setCardElevation(1);
                    holder.interests_card.setCardBackgroundColor(Color.TRANSPARENT);
                }

            }
        });*/
    }

    @Override
    public int getItemCount() {
        return interestsModel.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView interests_tag;
        CardView interests_card;
        OnNoteListener onNoteListener;

        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            this.interests_tag = (TextView) itemView.findViewById(R.id.interests_tag);
            this.interests_card = (CardView) itemView.findViewById(R.id.interests_card);
            this.onNoteListener = onNoteListener;

            //attach onClickListener to entire view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getAdapterPosition(), interests_card); //getAdapterPosition returns the position of the holder clicked
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position, CardView cardView); //will send the position of the clicked item
    }


}
