package com.android.ffbf.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.ffbf.R;
import com.android.ffbf._interface.AuthResponse;
import com.android.ffbf.model.User;
import com.android.ffbf.util.Util;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


/**
 * Activity for User to SignIn
 * inorder to use Application
 */
public class SignInActivity extends BaseActivity implements View.OnClickListener, AuthResponse {

    private EditText editText_email;
    private EditText editText_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog.show();
        onSuccess(auth.getCurrentUser());
    }

    /**
     * Getting View associated with this Activity i.e. xml layout
     */
    @Override
    protected int getView() {
        return R.layout.activity_sign_in;
    }

    /**
     * Initializing Views defined in xml associated with this Activity
     */
    @Override
    protected void initViews() {
        editText_email = findViewById(R.id.editText_email);
        editText_password = findViewById(R.id.editText_password);

        findViewById(R.id.button_signIn).setOnClickListener(this);
        findViewById(R.id.button_signUp).setOnClickListener(this);

    }

    /**
     * Implementing click listeners of Views that are consuming onClick Event
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_signIn:
                if (isValidInput()) {
                    progressDialog.show();
                    auth.signIn(this, this, editText_email.getText().toString(), editText_password.getText().toString());
                }
                break;
            case R.id.button_signUp:
                editText_email.setText("");
                editText_password.setText("");
                startActivity(new Intent(this, SignUpActivity.class).putExtra("isAdmin", false));
                break;

        }
    }

    /**
     * Validating Input Values
     */
    private boolean isValidInput() {
        boolean isValid = true;
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


    /**
     * Declared in Interface AuthResponse to get Specific User on LogIn or Registration
     */
    @Override
    public void onSuccess(User user) {
        if (user != null) {
            fireBaseDb.view(FirebaseDatabase.getInstance().getReference("user").orderByChild("userId").equalTo(user.getUserId()), User.class);
        } else {
            progressDialog.hide();
        }
    }

    /**
     * Declared in Interface FirebaseOperations to get List of Items whenever view function is called
     */
    @Override
    public void onSuccess(List list) {
        editText_email.setText("");
        editText_password.setText("");

        progressDialog.hide();
        user = (User) list.get(0);
        startActivity(new Intent(this, MainActivity.class).putExtra("user", user));
        finish();
    }

    /**
     * Declared in Interfaces AuthResponse & FirebaseOperations
     * to get Failure messages
     */
    @Override
    public void onFailure(String message) {
        editText_email.setText("");
        editText_password.setText("");

        progressDialog.hide();
        Util.showToast(this, message);

    }
}
