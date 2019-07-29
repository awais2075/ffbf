package com.android.ffbf.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


public class Permission {
    private Context context;
    private int permissionCode;

    public Permission(Context context, int permissionCode) {
        this.context = context;
        this.permissionCode = permissionCode;
    }

    public boolean checkPermission(String permission) {
        int result = ActivityCompat.checkSelfPermission(context, permission);
        switch (result) {
            case PackageManager.PERMISSION_GRANTED:
                return true;
            case PackageManager.PERMISSION_DENIED:
                return false;
            default:
                return false;
        }
    }

    public void requestPermission(String permission) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, permissionCode);

    }

}
