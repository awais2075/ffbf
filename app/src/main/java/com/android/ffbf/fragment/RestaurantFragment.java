package com.android.ffbf.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.ffbf.R;
import com.android.ffbf._interface.FirebaseResponse;
import com.android.ffbf._interface.ItemClickListener;
import com.android.ffbf.activity.RestaurantDetailActivity;
import com.android.ffbf.adapter.RecyclerViewAdapter;
import com.android.ffbf.decoration.MyDividerItemDecoration;
import com.android.ffbf.enums.UserType;
import com.android.ffbf.firebase.FireBaseDb;
import com.android.ffbf.model.Restaurant;
import com.android.ffbf.model.User;
import com.android.ffbf.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantFragment extends BaseFragment implements FirebaseResponse<Restaurant>, ItemClickListener<Restaurant> {

    private FireBaseDb fireBaseDb;
    private User user;
    private Restaurant restaurant;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private TextView textView_noData;
    private FloatingActionButton fab;

    public RestaurantFragment(FireBaseDb fireBaseDb, User user) {
        this.fireBaseDb = fireBaseDb;
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference("restaurant");


        initViews(view);


        return view;
    }

    private void initViews(View view) {

        fab = view.findViewById(R.id.fab);
        textView_noData = view.findViewById(R.id.textView_noData);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL, 16));


        fab.setOnClickListener(this);

        if (user.getUserType() != UserType.Admin) {
            fab.hide();
        }


        fireBaseDb.view(databaseReference.orderByChild("restaurantName"), Restaurant.class, this);
    }

    @Override
    public void onSuccess(List<Restaurant> list) {
        if (!list.isEmpty()) {
            textView_noData.setVisibility(View.GONE);
        } else {
            textView_noData.setVisibility(View.VISIBLE);
        }

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, R.layout.item_restaurant, list);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onFailure(String message) {
        Util.showToast(getContext(), message);
    }

    @Override
    public void onItemClicked(Restaurant restaurant) {
        Util.showToast(getContext(), restaurant.getRestaurantName());
        startActivity(new Intent(getContext(), RestaurantDetailActivity.class).putExtra("restaurant", restaurant));

    }

    @Override
    public void onItemLongClicked(View view, Restaurant rest) {
        restaurant = rest;
        PopupMenu popup = new PopupMenu(getContext(), view);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.option_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.item_add:
                        Util.showToast(getContext(), restaurant.getRestaurantName() + " : " + menuItem.getTitle());
                        break;
                    case R.id.item_edit:
                        Util.showToast(getContext(), restaurant.getRestaurantName() + " : " + menuItem.getTitle());
                        break;
                    case R.id.item_delete:
                        Util.showToast(getContext(), restaurant.getRestaurantName() + " : " + menuItem.getTitle());
                        showAlertDialog("Are you sure to Delete this Restaurant...");
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                showCustomDialog(R.layout.dialog_add_restaurant);
                break;
        }
    }

    private void showCustomDialog(int layoutId) {
        AlertDialog.Builder customDialog = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(layoutId, null);
        final EditText editText_restaurantName = view.findViewById(R.id.editText_restaurantName);
        final EditText editText_restaurantInfo = view.findViewById(R.id.editText_restaurantInfo);

        customDialog.setView(view)
                .setCancelable(true)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String restaurantId = databaseReference.push().getKey();
                        Restaurant restaurant = new Restaurant(restaurantId, editText_restaurantName.getText().toString(), editText_restaurantInfo.getText().toString());
                        fireBaseDb.insert(databaseReference, restaurantId, restaurant);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();


        customDialog.show();
    }


    private void showAlertDialog(String title) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setCancelable(false)
                .setTitle(title)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fireBaseDb.delete(databaseReference, restaurant.getRestaurantId(), restaurant);
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


}
