package com.example.culs.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.culs.R;

import java.util.ArrayList;

public class MyInterestsAdapter extends RecyclerView.Adapter<MyInterestsAdapter.MyNewViewHolder>{

    private ArrayList<InterestsModel> interestsModel;

    public MyInterestsAdapter(ArrayList<InterestsModel> interestsModel) {
        this.interestsModel = interestsModel;
    }

    public MyNewViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interests_card_item, parent, false);

        //view.setOnClickListener()

        MyNewViewHolder myViewHolder = new MyNewViewHolder(view); //pass in the global onNoteListener
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyNewViewHolder holder, int position) {
        TextView interests_tag = holder.interests_tag;
        interests_tag.setText(interestsModel.get(position).getInterests());
    }


    @Override
    public int getItemCount() {
        return interestsModel.size();
    }

    public class MyNewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView interests_tag;
        CardView interests_card;
        public MyNewViewHolder(@NonNull View itemView) {
            super(itemView);
            this.interests_tag = (TextView) itemView.findViewById(R.id.interests_tag);
            this.interests_card = (CardView) itemView.findViewById(R.id.interests_card);

            //attach onClickListener to entire view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }
}
