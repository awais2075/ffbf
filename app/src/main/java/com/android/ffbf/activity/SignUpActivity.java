package com.android.ffbf.activity;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.android.ffbf.R;
import com.android.ffbf._interface.AuthResponse;
import com.android.ffbf.enums.UserType;
import com.android.ffbf.model.User;
import com.android.ffbf.util.Util;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SignUpActivity extends BaseActivity implements View.OnClickListener, AuthResponse {


    private EditText
            editText_userName,
            editText_firstName,
            editText_surName,
            editText_email,
            editText_password;

    private Boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
    }

    @Override
    protected void initViews() {
        editText_userName = findViewById(R.id.editText_userName);
        editText_firstName = findViewById(R.id.editText_firstName);
        editText_surName = findViewById(R.id.editText_surName);
        editText_email = findViewById(R.id.editText_email);
        editText_password = findViewById(R.id.editText_password);

        editText_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText_password.getText().toString().length() < 6) {
                    editText_password.setError("Password should be greater than 6 characters");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        findViewById(R.id.button_signUp).setOnClickListener(this);
        findViewById(R.id.button_signIn).setOnClickListener(this);
    }

    @Override
    protected int getView() {
        return R.layout.activity_sign_up;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_signUp:
                if (isValidInput()) {
                    progressDialog.show();
                    UserType userType = UserType.User;
                    if (isAdmin) {
                        userType = UserType.Critic;
                    }
                    auth.signUp(this, this, new User(
                            "", editText_userName.getText().toString(), editText_email.getText().toString(),
                            editText_firstName.getText().toString(), editText_surName.getText().toString(), editText_password.getText().toString(), userType));

                }
                break;
            case R.id.button_signIn:
                finish();
                break;

        }
    }

    private boolean isValidInput() {
        boolean isValid = true;
        if (TextUtils.isEmpty(editText_userName.getText().toString())) {
            editText_userName.setError("Shouldn't be Empty");
            isValid = false;
        }
        if (TextUtils.isEmpty(editText_firstName.getText().toString())) {
            editText_firstName.setError("Shouldn't be Empty");
            isValid = false;
        }
        if (TextUtils.isEmpty(editText_surName.getText().toString())) {
            editText_surName.setError("Shouldn't be Empty");
            isValid = false;
        }
        if (TextUtils.isEmpty(editText_email.getText().toString())) {
            editText_email.setError("Shouldn't be Empty");
            isValid = false;
        }
        if (TextUtils.isEmpty(editText_password.getText().toString())) {
            editText_password.setError("Shouldn't be Empty");
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void onSuccess(User user) {
        fireBaseDb.insert(FirebaseDatabase.getInstance().getReference("user"), user.getUserId(), user);
        fireBaseDb.view(FirebaseDatabase.getInstance().getReference("user").orderByChild(user.getUserId()), User.class);
    }

    @Override
    public void onSuccess(List list) {
        progressDialog.hide();
        user = (User) list.get(0);
        auth.signOut();
        Util.showToast(this, "Registration Successful...");
        finish();
    }

    @Override
    public void onFailure(String message) {
        editText_userName.setText("");
        editText_firstName.setText("");
        editText_surName.setText("");
        editText_email.setText("");
        editText_password.setText("");

        progressDialog.hide();
        Util.showToast(this, message);
    }
}
