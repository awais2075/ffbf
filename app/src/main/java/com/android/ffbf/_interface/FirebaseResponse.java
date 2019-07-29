package com.android.ffbf._interface;

import java.util.List;

public interface FirebaseResponse<Model> {

    void onSuccess(List<Model> list);

    void onFailure(String message);
}
