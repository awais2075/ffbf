package com.android.ffbf._interface;

import com.android.ffbf.model.User;

public interface AuthResponse {

    void onSuccess(User user);

    void onFailure(String message);
}
