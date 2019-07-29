package com.android.ffbf.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.ffbf.R;
import com.android.ffbf._interface.FirebaseResponse;
import com.android.ffbf._interface.ItemClickListener;
import com.android.ffbf.activity.StreetStallDetailActivity;
import com.android.ffbf.adapter.RecyclerViewAdapter;
import com.android.ffbf.enums.UserType;
import com.android.ffbf.firebase.FireBaseDb;
import com.android.ffbf.model.StreetStall;
import com.android.ffbf.model.User;
import com.android.ffbf.util.Constants;
import com.android.ffbf.util.Permission;
import com.android.ffbf.util.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class StreetFragment extends BaseFragment implements ItemClickListener<StreetStall>, FirebaseResponse {

    private ImageView imageView_stallImage;


    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<StreetStall> streetStallList = new ArrayList<>();

    private Permission permission;
    private TextView textView_noData;
    private FloatingActionButton fab;
    private Uri imageUri;
    private FireBaseDb fireBaseDb;
    private User user;
    private DatabaseReference databaseReference;

    public StreetFragment(FireBaseDb fireBaseDb, User user) {
        this.fireBaseDb = fireBaseDb;
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_street, container, false);

        permission = new Permission(getContext(), Constants.PERMISSION_CODE);
        databaseReference = FirebaseDatabase.getInstance().getReference("streetStall");

        initViews(view);
        return view;
    }

    private void initViews(View view) {
        fab = view.findViewById(R.id.fab);
        textView_noData = view.findViewById(R.id.textView_noData);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        if (user.getUserType() == UserType.User) {
            fab.show();
        }
        fab.setOnClickListener(this);

        populateData();
    }

    private void populateData() {
        fireBaseDb.view(databaseReference.orderByChild("streetStallName"), StreetStall.class, this);
    }

    private boolean isStreetStallAlreadyExists(String streetStallName) {
        for (int i = 0; i < streetStallList.size(); i++) {
            if (streetStallList.get(i).getStreetStallName().equals(streetStallName)) {
                return true;
            }
        }
        return false;
    }

    private void uploadImage(final StreetStall streetStall) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        progressDialog.setCancelable(false);


        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + streetStall.getStreetStallImageUrl());
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                progressDialog.hide();
                Util.showToast(getContext(), "File Uploaded");
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        streetStall.setStreetStallImageUrl(imageUri.toString());
                        uploadStreetStall(streetStall);

                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.hide();
                        Util.showToast(getContext(), e.getMessage());
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        //displaying percentage in progress dialog
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });
    }

    private void uploadStreetStall(StreetStall streetStall) {
        fireBaseDb.insert(databaseReference, streetStall.getStreetStallId(), streetStall);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                showCustomDialog(R.layout.dialog_add_street_stall);
                break;
            case R.id.textView_uploadImage:
            case R.id.imageView_stallImage:
                if (permission.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    openFileChooser();
                    Util.showToast(getActivity(), "Already Permitted");
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.PERMISSION_CODE);
                }
                break;
        }
    }

    private void showCustomDialog(int layoutId) {

        AlertDialog.Builder customDialog = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(layoutId, null);

        imageView_stallImage = view.findViewById(R.id.imageView_stallImage);
        TextView textView_uploadImage = view.findViewById(R.id.textView_uploadImage);

        final EditText editText_streetStallName = view.findViewById(R.id.editText_stallName);
        final EditText editText_streetStallLocation = view.findViewById(R.id.editText_stallLocation);
        final EditText editText_streetStallInfo = view.findViewById(R.id.editText_streetStallInfo);

        final RadioButton radioButton_veg = view.findViewById(R.id.radioButton_veg);

        textView_uploadImage.setOnClickListener(this);
        imageView_stallImage.setOnClickListener(this);

        customDialog.setView(view)
                .setCancelable(true)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!isStreetStallAlreadyExists(editText_streetStallName.getText().toString())) {

                            StreetStall streetStall = new StreetStall(
                                    databaseReference.push().getKey(),
                                    editText_streetStallName.getText().toString().toLowerCase().replace(' ', '_'),
                                    editText_streetStallName.getText().toString(), editText_streetStallLocation.getText().toString(),
                                    radioButton_veg.isChecked(),
                                    editText_streetStallInfo.getText().toString());
                            uploadImage(streetStall);


                            dialog.dismiss();
                        } else {
                            Util.showToast(getContext(), "Street Stall Already Exists");
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();


        customDialog.show();

    }

    @Override
    public void onItemClicked(StreetStall streetStall) {
        startActivity(new Intent(getContext(), StreetStallDetailActivity.class).putExtra("streetStall", streetStall));
    }

    @Override
    public void onItemLongClicked(View view, StreetStall streetStall) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser();
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Util.showToast(getContext(), "Some Features might not work");
            }
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                imageView_stallImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSuccess(List list) {
        streetStallList = list;
        if (!list.isEmpty()) {
            textView_noData.setVisibility(View.GONE);
        } else {
            textView_noData.setVisibility(View.VISIBLE);
        }
        adapter = new RecyclerViewAdapter(this, R.layout.item_street_stall, list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onFailure(String message) {

    }
}
