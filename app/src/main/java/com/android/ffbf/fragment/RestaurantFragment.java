package com.android.ffbf.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantFragment extends BaseFragment implements FirebaseResponse<Restaurant>, ItemClickListener<Restaurant> {

    private FireBaseDb fireBaseDb;
    private User user;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private TextView textView_noData;
    private FloatingActionButton fab;

    private List<Restaurant> restaurantList = new ArrayList<>();

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

    /*Initializing Views defined in xml associated with this Fragment*/
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

    /*Declared in Interface FirebaseOperations to get List of Items whenever view function is called*/
    @Override
    public void onSuccess(List<Restaurant> list) {
        restaurantList = list;
        if (!list.isEmpty()) {
            textView_noData.setVisibility(View.GONE);
        } else {
            textView_noData.setVisibility(View.VISIBLE);
        }

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, R.layout.item_restaurant, list);
        recyclerView.setAdapter(adapter);

    }

    /*Declared in Interfaces FirebaseOperations
     * to get Failure messages*/@Override
    public void onFailure(String message) {
        Util.showToast(getContext(), message);
    }

    /**
     * Return Object whenever user clicks an item from List(RecyclerView)
     */
    @Override
    public void onItemClicked(Restaurant restaurant) {
        startActivity(new Intent(getContext(), RestaurantDetailActivity.class).putExtra("restaurant", restaurant));
    }

    /**
     * Returns object with view whenever user make a long click on item from List(RecyclerView)
     *
     * Show Popup Menu on LongClicked
     * to Delete specific item
     */
    @Override
    public void onItemLongClicked(View view, final Restaurant restaurant) {
        if (user.getUserType() == UserType.Admin) {
            PopupMenu popup = new PopupMenu(getContext(), view);
            popup.getMenuInflater().inflate(R.menu.option_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.item_delete:
                            showAlertDialog("Are you sure to delete this Restaurant", restaurant);
                            break;
                    }
                    return true;
                }
            });
            popup.show();

        }
    }

    private void showAlertDialog(String title, final Restaurant restaurant) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setCancelable(false)
                .setTitle(title)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fireBaseDb.delete(databaseReference, restaurant.getRestaurantId(), restaurant);

                        //delete all reviews associate with this
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
            case R.id.fab:
                showCustomDialog(R.layout.dialog_add_restaurant);
                break;
        }
    }


    private void showCustomDialog(final int layoutId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(layoutId, null);
        final EditText editText_restaurantName = view.findViewById(R.id.editText_restaurantName);
        final EditText editText_restaurantInfo = view.findViewById(R.id.editText_restaurantInfo);
        final TextView textView_add = view.findViewById(R.id.textView_add);
        final TextView textView_cancel = view.findViewById(R.id.textView_cancel);
        builder.setView(view)
                .setCancelable(true);

        final AlertDialog customDialog = builder.create();
        customDialog.show();


        textView_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String restaurantId = databaseReference.push().getKey();
                Restaurant restaurant = new Restaurant(restaurantId, editText_restaurantName.getText().toString(), editText_restaurantInfo.getText().toString());

                if (TextUtils.isEmpty(editText_restaurantName.getText().toString()) || TextUtils.isEmpty(editText_restaurantInfo.getText().toString())) {
                    Util.showToast(getContext(), "Input fields shouldn't be empty....");
                } else {
                    if (!isRestaurantAlreadyExists(restaurant.getRestaurantName().trim())) {
                        fireBaseDb.insert(databaseReference, restaurantId, restaurant);
                        customDialog.dismiss();
                    } else {
                        Util.showToast(getContext(), "Restaurant Already Exists");
                    }
                }
            }
        });
        textView_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
    }


    private boolean isRestaurantAlreadyExists(String restaurantName) {
        for (int i = 0; i < restaurantList.size(); i++) {
            if (restaurantList.get(i).getRestaurantName().equalsIgnoreCase(restaurantName)) {
                return true;
            }
        }
        return false;
    }
}
