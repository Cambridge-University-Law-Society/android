package com.example.culs.helpers;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.culs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.MyViewHolder> {

    private ArrayList<InterestsModel> interestsModel;
    private OnNoteListener mOnNoteListener; //this sets the onNoteListener to the viewholder
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    //this retrieves the entire document with this specific uid
    private DocumentReference docRef = db.collection("users").document(userid);
    List<String> myInterests;


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

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        myInterests = (List<String>) document.get("interests");
                        //final ArrayList<String> list = new ArrayList<String>();
                        if (myInterests == null){

                        }else{
                            for (int i = 0; i < myInterests.size(); ++i) {
                                if (holder.interests_tag.getText().toString().equals(myInterests.get(i))){
                                    holder.interests_card.setCardBackgroundColor(Color.parseColor("#2F0B3A"));
                                    holder.interests_tag.setTextColor(Color.WHITE);
                                }else{
                                    //holder.interests_card.setCardBackgroundColor(Color.WHITE);
                                }
                            }
                        }

                    }
                }
            }
        });


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
            onNoteListener.onNoteClick(getAdapterPosition(), interests_card, interests_tag); //getAdapterPosition returns the position of the holder clicked
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position, CardView cardView, TextView textView); //will send the position of the clicked item
    }


}
