package com.android.ffbf.holder;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.ffbf.R;
import com.android.ffbf._interface.ItemClickListener;
import com.android.ffbf.model.Restaurant;
import com.android.ffbf.model.Review;
import com.android.ffbf.model.StreetStall;
import com.android.ffbf.model.User;
import com.android.ffbf.util.Util;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

/**
 * RecyclerView Holder for RecyclerView Adapter required to bind data of every item in list to its view
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private ItemClickListener itemClickListener;
    private List anyList;
    private View itemView;

    public <Model> RecyclerViewHolder(View view, ItemClickListener itemClickListener, List<Model> anyList) {
        super(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        this.itemClickListener = itemClickListener;
        this.anyList = anyList;
        this.itemView = view;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onItemClicked(anyList.get(getAdapterPosition()));
    }

    public <Model> void bind(Model model) {
        if (model instanceof Restaurant) {
            ((TextView) itemView.findViewById(R.id.textView_restaurantName)).setText(((Restaurant) model).getRestaurantName());
            ((TextView) itemView.findViewById(R.id.textView_restaurantInfo)).setText(((Restaurant) model).getRestaurantInfo());
        } else if (model instanceof Review) {
            ((TextView) itemView.findViewById(R.id.textView_reviewImage)).setText(((Review) model).getReviewerName().charAt(0) + "");
            ((TextView) itemView.findViewById(R.id.textView_reviewerName)).setText(((Review) model).getReviewerName());
            ((TextView) itemView.findViewById(R.id.textView_reviewText)).setText(((Review) model).getReviewText());
            ((RatingBar) itemView.findViewById(R.id.ratingBar)).setRating(((Review) model).getReviewRating());
        } else if (model instanceof User) {
            ((TextView) itemView.findViewById(R.id.textView_fullName)).setText(((User) model).getUserFirstName() + " " + ((User) model).getUserSurName());
            ((TextView) itemView.findViewById(R.id.textView_userType)).setText(((User) model).getUserType().toString());
        } else if (model instanceof StreetStall) {
            Glide.with(itemView.getContext()).load(((StreetStall) model).getStreetStallImageUrl()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Util.showToast(itemView.getContext(), e.getMessage());
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            })
                    .into((ImageView) itemView.findViewById(R.id.imageView_streetStallImage));
            ((TextView) itemView.findViewById(R.id.textView_streetStallName)).setText(((StreetStall) model).getStreetStallName());
            ((TextView) itemView.findViewById(R.id.textView_streetStallLocation)).setText(((StreetStall) model).getStreetStallLocation());

            if (((StreetStall) model).isVegetarian()) {
                ((TextView) itemView.findViewById(R.id.textView_streetStallFoodType)).setText("Vegetarian");
            } else {
                ((TextView) itemView.findViewById(R.id.textView_streetStallFoodType)).setText("Non-Vegetarian");
            }
        }
    }

    /**
     * Returns object with view whenever user make a long click on item from List(RecyclerView)
     */
    @Override
    public boolean onLongClick(View view) {
        itemClickListener.onItemLongClicked(view, anyList.get(getAdapterPosition()));
        return true;
    }
}
