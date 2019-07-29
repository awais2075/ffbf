package com.android.ffbf.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.ffbf.R;
import com.android.ffbf._interface.ItemClickListener;
import com.android.ffbf.adapter.RecyclerViewAdapter;
import com.android.ffbf.decoration.MyDividerItemDecoration;
import com.android.ffbf.enums.UserType;
import com.android.ffbf.model.Review;
import com.android.ffbf.model.StreetStall;
import com.android.ffbf.model.User;
import com.android.ffbf.util.Util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StreetStallDetailActivity extends BaseActivity implements ItemClickListener<Review>, View.OnClickListener {

    private StreetStall streetStall;

    private int dialogId; //1 for custom, 2 for alert
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<Review> reviewList = new ArrayList<>();
    private TextView
            textView_streetStallName,
            textView_streetStallLocation,
            textView_streetStallFoodType,
            textView_streetStallInfo,
            textView_addReview;
    private EditText editText_review;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("review");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getView() {
        return R.layout.activity_street_stall_detail;
    }

    @Override
    protected void initViews() {
        streetStall = (StreetStall) getIntent().getSerializableExtra("streetStall");

        textView_streetStallName = findViewById(R.id.textView_streetStallName);
        textView_streetStallLocation = findViewById(R.id.textView_streetStallLocation);
        textView_streetStallFoodType = findViewById(R.id.textView_streetStallFoodType);
        textView_streetStallInfo = findViewById(R.id.textView_streetStallInfo);

        textView_addReview = findViewById(R.id.textView_addReview);

        if (user.getUserType() == UserType.User) {
            textView_addReview.setVisibility(View.VISIBLE);
        }


        textView_streetStallName.setText(streetStall.getStreetStallName());
        textView_streetStallLocation.setText(streetStall.getStreetStallLocation());
        if (!streetStall.isVegetarian()) {
            textView_streetStallFoodType.setText(getString(R.string.non_vegetarian));
        }
        textView_streetStallInfo.setText(streetStall.getStreetStallInfo());

        textView_addReview.setOnClickListener(this);
        populateData();
    }

    private void populateData() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        fireBaseDb.view(databaseReference.orderByChild("restaurantId").equalTo(streetStall.getStreetStallId()), Review.class);

    }

    @Override
    public void onSuccess(List list) {
        reviewList = list;
        for (int i = 0; i < reviewList.size(); i++) {
            if (reviewList.get(i).getReviewerName().equals(user.getUserName())) {
                Collections.swap(reviewList, i, 0);
            }
        }
        adapter = new RecyclerViewAdapter(this, R.layout.item_review, list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onFailure(String message) {
        Util.showToast(this, message);

    }

    @Override
    public void onItemClicked(Review review) {
    }

    @Override
    public void onItemLongClicked(View view, final Review review) {
        if (review.getReviewerName().equals(user.getUserName()) || user.getUserType() == UserType.Admin) {
            PopupMenu popup = new PopupMenu(this, view);
            popup.getMenuInflater().inflate(R.menu.option_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.item_add:
                            Util.showToast(StreetStallDetailActivity.this, streetStall.getStreetStallName() + " : " + menuItem.getTitle());
                            break;
                        case R.id.item_edit:
                            Util.showToast(StreetStallDetailActivity.this, streetStall.getStreetStallName() + " : " + menuItem.getTitle());
                            break;
                        case R.id.item_delete:
                            showAlertDialog("Are you sure to delete this review", review);
                            Util.showToast(StreetStallDetailActivity.this, streetStall.getStreetStallName() + " : " + menuItem.getTitle());
                            break;
                    }
                    return true;
                }
            });
            popup.show();

        }

    }

    private void showAlertDialog(String title, final Review review) {
        dialogId = 2;
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false)
                .setTitle(title)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fireBaseDb.delete(databaseReference, review.getReviewId(), review);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textView_addReview:
                if (!isAlreadyReviewed()) {
                    showCustomDialog(R.layout.dialog_add_review);
                } else {
                    Util.showToast(this, "You've already reviewed...");
                }
                break;
        }
    }

    private boolean isAlreadyReviewed() {
        for (int i = 0; i < reviewList.size(); i++) {
            if (reviewList.get(i).getReviewerName().equals(user.getUserName())) {
                return true;
            }
        }
        return false;
    }

    private void showCustomDialog(int layoutId) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(layoutId, null);
        alertDialog.setView(view)
                .setCancelable(true)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String reviewId = databaseReference.push().getKey();
                        Review review = new Review(reviewId, user.getUserName(), editText_review.getText().toString(), 0.0f, streetStall.getStreetStallId());
                        fireBaseDb.insert(databaseReference, reviewId, review);

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();


        editText_review = view.findViewById(R.id.editText_review);


        alertDialog.show();
    }

}
