package com.example.culs.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


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
    public void onBindViewHolder(CardHolder holder, int position) {
        // 5. Use position to access the correct Bakery object
        Card card = this.cards.get(position);
        // 6. Bind the bakery object to the holder
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
}


