package com.example.culs.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.culs.fragments.ProfileFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class CardAdapter extends RecyclerView.Adapter<CardHolder> {

    private final List<Card> cards;
    private Context context;
    private int itemResource;
    private CardHolder.OnCardListener mOnCardListener;

    public CardAdapter(Context context, int itemResource, List<Card> cards, CardHolder.OnCardListener onCardListener) {
        // 1. Initialize our adapter
        this.cards = cards;
        this.context = context;
        this.itemResource = itemResource;
        this.mOnCardListener = onCardListener;
    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResource, parent, false);
        return new CardHolder(this.context, view, mOnCardListener, (ArrayList<Card>) cards);
    }

    @Override
    public void onBindViewHolder(final CardHolder holder, int position) {

        Card card = this.cards.get(position);

        try {
            fetchImage(holder, card);
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.bindCard(card);
    }

    @Override
    public int getItemCount() {
        return this.cards.size();
    }

    public void clear() {
        cards.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Card> cards) {
        cards.addAll(cards);
        notifyDataSetChanged();
    }

    private void fetchImage(final CardHolder holder, final Card currentCard) throws IOException {
        //using glide method

        //Reference to an image file in cloud storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a reference with an initial file path and name
        StorageReference pathReference = storageRef.child("Events/" + currentCard.getEventImage());
        String downloadUrl = pathReference.toString();

        //try to download to a local file
        final File file = File.createTempFile("eventPic", "jpg");
        pathReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                GlideApp.with(holder.itemView).load(file).into(holder.eventPic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        //GlideApp.with(this.getContext()).load(pathReference).into(image);

    }
}


