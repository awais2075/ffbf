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

    /**
     * SignIn user on the basis of
     * Email
     * Password
     */
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

    /**
     * SignOut user
     */
    public void signOut() {
        firebaseAuth.signOut();
    }

    /**
     * Whenever application launches it always checks previous loggedIn user
     * if it finds, it will logIn from that user (i.e. MainActivity will be launched)
     * otherwise it will show SignIn Activity
     */
    public User getCurrentUser() {
        if (firebaseAuth.getCurrentUser() != null) {
            return new User(firebaseAuth.getCurrentUser().getUid());
        }
        return null;
    }

    /**
     * Register a user on the basis of its information defined in User class
     */
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
