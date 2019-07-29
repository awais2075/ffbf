package com.android.ffbf.activity;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.ffbf.R;
import com.android.ffbf._interface.ItemClickListener;
import com.android.ffbf.adapter.RecyclerViewAdapter;
import com.android.ffbf.decoration.MyDividerItemDecoration;
import com.android.ffbf.enums.UserType;
import com.android.ffbf.model.Restaurant;
import com.android.ffbf.model.Review;
import com.android.ffbf.util.Util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class RestaurantDetailActivity extends BaseActivity implements
        View.OnClickListener, DatePickerDialog.OnDateSetListener, ItemClickListener<Review>, DialogInterface.OnClickListener {

    private Restaurant restaurant;
    private List<Review> reviewList = new ArrayList<>();
    private TextView textView_restaurantName;
    private TextView textView_restaurantInfo;
    private TextView textView_addReview;
    private TextView textView_makeReservation;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private EditText editText_review;
    private boolean isToAdd = false;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("review");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getView() {
        return R.layout.layout_test;
    }

    @Override
    protected void initViews() {

        restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");

        textView_restaurantName = findViewById(R.id.textView_restaurantName);
        textView_restaurantInfo = findViewById(R.id.textView_restaurantInfo);

        textView_addReview = findViewById(R.id.textView_addReview);
        textView_makeReservation = findViewById(R.id.textView_makeReservation);

        textView_restaurantName.setText(restaurant.getRestaurantName());
        textView_restaurantInfo.setText(restaurant.getRestaurantInfo());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));

        populateData();

        if (user.getUserType() == UserType.Critic) {
            textView_addReview.setVisibility(View.VISIBLE);
        }

        if (user.getUserType() == UserType.User) {
            textView_makeReservation.setVisibility(View.VISIBLE);
        }


        textView_addReview.setOnClickListener(this);
        textView_makeReservation.setOnClickListener(this);
    }

    private void populateData() {

        fireBaseDb.view(databaseReference.orderByChild("restaurantId").equalTo(restaurant.getRestaurantId()), Review.class);
        /*reviewList.add(new Review("001", "Muhammad Awais Rashid", getString(R.string.review), 2.5f));
        reviewList.add(new Review("001", "Muhammad Anas Rashid", getString(R.string.review), 4.5f));
        reviewList.add(new Review("001", "Muhammad Ubaid Rashid", getString(R.string.review), 4.0f));
        reviewList.add(new Review("001", "Muhammad Ubaid Rashid", getString(R.string.review), 4.0f));
        reviewList.add(new Review("001", "Muhammad Ubaid Rashid", getString(R.string.review), 3.5f));
        reviewList.add(new Review("001", "Muhammad Ubaid Rashid", getString(R.string.review), 4.0f));
        reviewList.add(new Review("001", "Muhammad Ubaid Rashid", getString(R.string.review), 4.0f));
        reviewList.add(new Review("001", "Muhammad Ubaid Rashid", getString(R.string.review), 4.0f));
        reviewList.add(new Review("001", "Muhammad Ubaid Rashid", getString(R.string.review), 4.0f));*/
    }

    @Override
    public void onSuccess(List list) {
        reviewList = list;
        for (int i = 0; i < reviewList.size(); i++) {
            if (reviewList.get(i).getReviewerName().equals(user.getUserName())) {
                Collections.swap(reviewList, i, 0);
            }
        }
        adapter = new RecyclerViewAdapter(this, R.layout.item_review, reviewList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onFailure(String message) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textView_addReview:
                if (!isAlreadyReviewed()) {
                    showAlertDialog();
                } else {
                    Util.showToast(this, "You've already reviewed...");
                }
                break;
            case R.id.textView_makeReservation:
                setUpDatePicker();
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

    private void setUpDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Util.showToast(this, "Selected Date is " + dayOfMonth + " : " + month + " : " + year);

        String date = year + "-" + (++month) + "-" + dayOfMonth + "%" + year;
        String webUrl = "https://www.opentable.com/s/?covers=2&dateTime=" + date + "%3A00&metroId=72&regionIds=5316&pinnedRids%5B0%5D=87967&enableSimpleCuisines=true&includeTicketedAvailability=true&pageType=0";

        startActivity(new Intent(this, WebActivity.class).putExtra("webUrl", webUrl));
/*        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
        startActivity(browserIntent);*/

    }

    @Override
    public void onItemClicked(Review review) {
        review.setReviewRating(5.0f);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemLongClicked(View view, Review review) {

    }

    private void showAlertDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_review, null);
        alertDialog.setView(view)
                .setCancelable(true)
                .setPositiveButton("Add", this)
                .setNegativeButton("Cancel", this)
                .create();


        editText_review = view.findViewById(R.id.editText_review);


        alertDialog.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case -1: {
                Util.showToast(this, "Positive");
                String reviewId = databaseReference.push().getKey();
                Review review = new Review(reviewId, user.getUserName(), editText_review.getText().toString(), 2.5f, restaurant.getRestaurantId());
                fireBaseDb.insert(databaseReference, reviewId, review);
            }
            break;
            case -2: {
                Util.showToast(this, "Negative");
            }
            break;
            case -3: {
                Util.showToast(this, "Neutral");
            }
            break;
        }
        dialog.dismiss();

    }


}