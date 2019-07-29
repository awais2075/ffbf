package com.android.ffbf.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.ffbf.R;
import com.android.ffbf._interface.FirebaseResponse;
import com.android.ffbf.authentication.Auth;
import com.android.ffbf.firebase.FireBaseDb;
import com.android.ffbf.model.User;

public abstract class BaseActivity extends AppCompatActivity implements FirebaseResponse {

    protected Auth auth;
    protected FireBaseDb fireBaseDb;
    protected ProgressDialog progressDialog;

    protected static User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getView());

        /*Initializing Email Auth*/
        auth = new Auth();
        auth.init();

        /*Initializing FireBase*/
        fireBaseDb = new FireBaseDb(this);

        /*Setting up Progress Dialog*/
        setUpProgressDialog();

        /*Initializing Activity Views*/
        initViews();
    }

    protected abstract int getView();

    protected abstract void initViews();


    @Override
    protected void onStart() {
        super.onStart();
    }

    public void setUpProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }


}

