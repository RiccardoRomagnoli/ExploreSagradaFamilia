package com.example.exploresagradafamilia.Permissions;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.exploresagradafamilia.R;
import com.example.exploresagradafamilia.Utility;

public class PermissionUtility {

    public static final int PERMISSION_REQUEST_STORAGE = 4;
    public static final int REQUEST_ENABLE_BLUETOOTH = 3;
    public static final int REQUEST_ENABLE_POSITION = 2;
    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    /**
     * Check permesso scrittura/lettura memoria esterna >= M
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void askForStoragePermissions(AppCompatActivity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.writing_access_needed);
        builder.setMessage(R.string.grant_writing_access);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(dialog -> {
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_STORAGE);
        });
        builder.show();
    }

    /**
     * Check permesso localizzazione >= M
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void askForLocationPermissions(AppCompatActivity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.location_access_needed);
        builder.setMessage(R.string.grant_location_access);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(dialog -> {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_COARSE_LOCATION);
        });
        builder.show();
    }

    /**
     * Richiesta localizzazione con apertura impostazioni
     */
    public static void askToTurnOnLocation(AppCompatActivity activity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setMessage(R.string.location_disabled);
        dialog.setPositiveButton(R.string.location_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivityForResult(myIntent, REQUEST_ENABLE_POSITION);
            }
        });
        dialog.show();
    }

    /**
     * Check localizzazione attiva
     */
    public static boolean isLocationEnabled(AppCompatActivity activity) {
        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        boolean networkLocationEnabled = false;
        boolean gpsLocationEnabled = false;
        try {
            networkLocationEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            gpsLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Log.d("UTILITY", "Excepción al obtener información de localización");
        }
        return networkLocationEnabled || gpsLocationEnabled;
    }

    /**
     * Richiesta bluetooth con apertura impostazioni
     */
    public static void askToTurnOnBluetooth(AppCompatActivity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
    }

    /**
     * Check Bt attivo
     */
    public static boolean isBluetoothEnabled(AppCompatActivity activity) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Utility.showToastMessage(activity, activity.getString(R.string.not_support_bluetooth_msg));
            return true;
        }
        if (mBluetoothAdapter.isEnabled()) {
            return true;
        }

        return false;
    }
}
