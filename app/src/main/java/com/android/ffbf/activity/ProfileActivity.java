package com.android.ffbf.activity;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.ffbf.R;
import com.android.ffbf._interface.ItemClickListener;
import com.android.ffbf.adapter.RecyclerViewAdapter;
import com.android.ffbf.decoration.MyDividerItemDecoration;
import com.android.ffbf.model.User;
import com.android.ffbf.util.Util;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProfileActivity extends BaseActivity implements ItemClickListener<User>, DialogInterface.OnClickListener, View.OnClickListener {

    private RecyclerView recyclerView;

    private TextView
            textView_userName,
            textView_userEmail,
            textView_firstName,
            textView_surName;
    private EditText
            editText_firstName,
            editText_surName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fireBaseDb.view(FirebaseDatabase.getInstance().getReference("user"), User.class);

    }

    @Override
    protected int getView() {
        return R.layout.activity_profile;
    }

    @Override
    protected void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
    }

    @Override
    public void onSuccess(List list) {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, R.layout.item_profile, list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onFailure(String message) {

    }

    @Override
    public void onItemClicked(User temUser) {
        if (user.getUserId().equals(temUser.getUserId())) {
            showCustomDialog(R.layout.dialog_update_profile, temUser);
        } else {
            showCustomDialog(R.layout.dialog_view_profile, temUser);
        }
    }

    @Override
    public void onItemLongClicked(View view, User user) {

    }

    protected void showCustomDialog(int layoutId, User selectedUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(layoutId, null);
        builder.setView(view).setCancelable(true);

        final AlertDialog customDialog = builder.create();
        customDialog.show();

        textView_userName = view.findViewById(R.id.textView_userName);
        textView_userEmail = view.findViewById(R.id.textView_email);

        textView_userName.setText(selectedUser.getUserName());
        textView_userEmail.setText(selectedUser.getUserEmail());

        if (layoutId == R.layout.dialog_update_profile) {
            editText_firstName = view.findViewById(R.id.editText_firstName);
            editText_surName = view.findViewById(R.id.editText_surName);

            editText_firstName.setText(selectedUser.getUserFirstName());
            editText_surName.setText(selectedUser.getUserSurName());

            Button button_update = view.findViewById(R.id.button_update);
            button_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(editText_firstName.getText().toString()) || TextUtils.isEmpty(editText_surName.getText().toString())) {
                        Util.showToast(v.getContext(), "Input fields shouldn't be Empty...");
                    } else {
                        user.setUserFirstName(editText_firstName.getText().toString());
                        user.setUserSurName(editText_surName.getText().toString());
                        fireBaseDb.update(FirebaseDatabase.getInstance().getReference("user"), user.getUserId(), user);

                        customDialog.dismiss();
                    }
                }
            });
        } else {
            textView_firstName = view.findViewById(R.id.textView_firstName);
            textView_surName = view.findViewById(R.id.textView_surName);

            textView_firstName.setText(selectedUser.getUserFirstName());
            textView_surName.setText(selectedUser.getUserSurName());
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }
}
