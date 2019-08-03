package com.android.ffbf.activity;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.ffbf._interface.FirebaseResponse;
import com.android.ffbf.authentication.Auth;
import com.android.ffbf.firebase.FireBaseDb;
import com.android.ffbf.model.User;

/*abstract class
 * all activities extend this class inorder to get similar methods or instances*/
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


    /*abstract method to getView of activity*/
    protected abstract int getView();

    /*abstract method to initialize views in activity*/
    protected abstract void initViews();


    @Override
    protected void onStart() {
        super.onStart();
    }


    /*method to show progress dialog whenever loading time is required*/
    public void setUpProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }


}

