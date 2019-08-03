package com.android.ffbf.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.ffbf._interface.ItemClickListener;
import com.android.ffbf.holder.RecyclerViewHolder;

import java.util.List;

/**
 * RecyclerView Adapter for List population
 */
public class RecyclerViewAdapter<Model> extends RecyclerView.Adapter<RecyclerViewHolder> {

    private ItemClickListener itemClickListener;
    private int itemLayout;
    private List<Model> list;

    public RecyclerViewAdapter(ItemClickListener itemClickListener, int itemLayout, List<Model> list) {
        this.itemClickListener = itemClickListener;
        this.itemLayout = itemLayout;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new RecyclerViewHolder(view, itemClickListener, list);
    }

    /**
     * Binding holder with specific item from list
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int position) {
        recyclerViewHolder.bind(list.get(position));
    }

    /**
     * Returns list size or Item count of list
     */
    @Override
    public int getItemCount() {
        return list.size();
    }
}
