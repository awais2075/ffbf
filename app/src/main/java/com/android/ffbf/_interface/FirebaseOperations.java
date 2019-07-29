package com.android.ffbf._interface;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.List;

public interface FirebaseOperations<Model> {

    boolean insert(DatabaseReference databaseReference, String key, Model value);

    boolean delete(DatabaseReference databaseReference, String key, Model value);

    boolean update(DatabaseReference databaseReference, String key, Model value);

    List<Model> view(Query query, Class refClass);

}
