package com.android.ffbf.authentication;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.ffbf._interface.AuthResponse;
import com.android.ffbf.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Auth {
    private FirebaseAuth firebaseAuth;


    public void init() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signIn(Activity context, final AuthResponse authResponse, String userEmail, String userPassword) {
        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(task.getResult().getUser().getUid());
                            authResponse.onSuccess(user);
                        } else {
                            authResponse.onFailure(task.getException().getMessage());
                        }

                    }
                });
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

    public User getCurrentUser() {
        if (firebaseAuth.getCurrentUser() != null) {
            return new User(firebaseAuth.getCurrentUser().getUid());
        }
        return null;
    }

    public void signUp(Activity context, final AuthResponse authResponse, final User user) {
        firebaseAuth.createUserWithEmailAndPassword(user.getUserEmail(), user.getUserPassword())
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user.setUserId(task.getResult().getUser().getUid());
                            authResponse.onSuccess(user);
                        } else {
                            authResponse.onFailure(task.getException().getMessage());
                        }
                    }
                });
    }
}
