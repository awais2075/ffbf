package com.android.ffbf.activity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.ffbf.R;
import com.android.ffbf._interface.AuthResponse;
import com.android.ffbf.model.User;
import com.android.ffbf.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SignInActivity extends BaseActivity implements View.OnClickListener, AuthResponse {

    private EditText editText_email;
    private EditText editText_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog.show();
        onSuccess(auth.getCurrentUser());
    }

    @Override
    protected int getView() {
        return R.layout.activity_sign_in;
    }

    @Override
    protected void initViews() {
        editText_email = findViewById(R.id.editText_email);
        editText_password = findViewById(R.id.editText_password);

        findViewById(R.id.button_signIn).setOnClickListener(this);
        findViewById(R.id.button_signUp).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_signIn:
                progressDialog.show();
                auth.signIn(this, this, editText_email.getText().toString(), editText_password.getText().toString());
                break;
            case R.id.button_signUp:
                editText_email.setText("");
                editText_password.setText("");
                startActivity(new Intent(this, SignUpActivity.class).putExtra("isAdmin", false));
                break;

        }
    }


    @Override
    public void onSuccess(User user) {
        if (user != null) {
            fireBaseDb.view(FirebaseDatabase.getInstance().getReference("user").orderByChild("userId").equalTo(user.getUserId()), User.class);
        } else {
            progressDialog.hide();
        }
    }

    @Override
    public void onSuccess(List list) {
        editText_email.setText("");
        editText_password.setText("");

        progressDialog.hide();
        user = (User) list.get(0);
        startActivity(new Intent(this, MainActivity.class).putExtra("user", user));
        finish();
    }

    @Override
    public void onFailure(String message) {
        editText_email.setText("");
        editText_password.setText("");

        progressDialog.hide();
        Util.showToast(this, message);

    }
}
