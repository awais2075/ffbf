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
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
    private TextView textView_uploadImage;

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
    private FirebaseStorage firebaseStorage;

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
        firebaseStorage = FirebaseStorage.getInstance();
        initViews(view);
        return view;
    }

    /*Initializing Views defined in xml associated with this Fragment*/
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
            if (streetStallList.get(i).getStreetStallName().equalsIgnoreCase(streetStallName)) {
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

        final StorageReference storageReference = firebaseStorage.getReference().child("images/" + streetStall.getStreetStallImageUrl());
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                progressDialog.hide();
                Util.showToast(getContext(), "File Uploaded");
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        streetStall.setStreetStallImageUrl(uri.toString());
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
        //streetStall.setStreetStallInfo(getString(R.string.dummy_text));
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
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.PERMISSION_CODE);
                }
                break;
        }
    }

    private void showCustomDialog(int layoutId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(layoutId, null);

        imageView_stallImage = view.findViewById(R.id.imageView_stallImage);
        textView_uploadImage = view.findViewById(R.id.textView_uploadImage);

        final EditText editText_streetStallName = view.findViewById(R.id.editText_stallName);
        final EditText editText_streetStallLocation = view.findViewById(R.id.editText_stallLocation);
        final EditText editText_streetStallInfo = view.findViewById(R.id.editText_streetStallInfo);

        final RadioButton radioButton_veg = view.findViewById(R.id.radioButton_veg);

        final TextView textView_add = view.findViewById(R.id.textView_add);
        final TextView textView_cancel = view.findViewById(R.id.textView_cancel);


        builder.setView(view)
                .setCancelable(true);

        final AlertDialog customDialog = builder.create();
        customDialog.show();

        textView_uploadImage.setOnClickListener(this);
        imageView_stallImage.setOnClickListener(this);


        textView_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText_streetStallName.getText().toString()) || TextUtils.isEmpty(editText_streetStallLocation.getText().toString()) || TextUtils.isEmpty(editText_streetStallInfo.getText().toString()) || imageUri == null) {
                    Util.showToast(getContext(), "Input fields shouldn't be empty....");
                } else {
                    if (!isStreetStallAlreadyExists(editText_streetStallName.getText().toString().trim())) {

                        StreetStall streetStall = new StreetStall(
                                databaseReference.push().getKey(),
                                editText_streetStallName.getText().toString().toLowerCase().replace(' ', '_'),
                                editText_streetStallName.getText().toString(), editText_streetStallLocation.getText().toString(),
                                radioButton_veg.isChecked(),
                                editText_streetStallInfo.getText().toString());
                        uploadImage(streetStall);


                        customDialog.dismiss();
                    } else {
                        Util.showToast(getContext(), "Street Stall Already Exists");
                    }
                }
            }
        });
        textView_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });

    }

    /**
     * Return Object whenever user clicks an item from List(RecyclerView)
     */
    @Override
    public void onItemClicked(StreetStall streetStall) {
        startActivity(new Intent(getContext(), StreetStallDetailActivity.class).putExtra("streetStall", streetStall));
    }

    /**
     * Returns object with view whenever user make a long click on item from List(RecyclerView)
     * <p>
     * Show Popup Menu on LongClicked
     * to Delete specific item
     */
    @Override
    public void onItemLongClicked(View view, final StreetStall streetStall) {
        if (user.getUserType() == UserType.Admin) {
            PopupMenu popup = new PopupMenu(getContext(), view);
            popup.getMenuInflater().inflate(R.menu.option_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.item_delete:
                            showAlertDialog("Are you sure to delete this Street Stall", streetStall);
                            break;
                    }
                    return true;
                }
            });
            popup.show();

        }
    }

    private void showAlertDialog(String title, final StreetStall streetStall) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setCancelable(false)
                .setTitle(title)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fireBaseDb.delete(databaseReference, streetStall.getStreetStallId(), streetStall);
                        firebaseStorage.getReferenceFromUrl(streetStall.getStreetStallImageUrl()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Util.showToast(getContext(), "Image File Deleted");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Util.showToast(getContext(), "Image Deleted Failure : " + e.getMessage());
                            }
                        });
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser();
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Util.showToast(getContext(), "Some Features might not work");

                openAppSettings();
            }
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
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
                textView_uploadImage.setVisibility(View.GONE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*Declared in Interface FirebaseOperations to get List of Items whenever view function is called*/
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
