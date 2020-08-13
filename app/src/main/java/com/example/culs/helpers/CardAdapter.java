package com.example.culs.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.culs.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class CardAdapter extends RecyclerView.Adapter<CardHolder> {

    private List<Card> mCards;
    private Context mContext;
    private int mItemResource;
    private CardHolder.OnCardListener mOnCardListener;

    public CardAdapter(Context context, int itemResource, List<Card> cards, CardHolder.OnCardListener onCardListener) {
        // 1. Initialize our adapter
        mCards = cards;
        mContext = context;
        mItemResource = itemResource;
        mOnCardListener = onCardListener;
    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(mItemResource, parent, false);
        return new CardHolder(mContext, view, mOnCardListener, (ArrayList<Card>) mCards);
    }

    @Override
    public void onBindViewHolder(final CardHolder holder, int position) {
        Card card = mCards.get(position);
        GlideApp.with(holder.itemView.getContext()).load(card.getImageURL()).placeholder(R.drawable.rounded_tags).fitCenter().into(holder.eventPic);
        holder.bindCard(card);
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    public void clear() {
        mCards.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Card> cards) {
        mCards.addAll(cards);
        notifyDataSetChanged();
    }

}


