package com.example.exploresagradafamilia;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.exploresagradafamilia.Beacons.BeaconUtility;
import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;
import java.util.Objects;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Utility {

    public static final int REQUEST_ENABLE_BLUETOOTH = 3;
    public static final int REQUEST_ENABLE_POSITION = 2;
    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    public static void insertFragment(AppCompatActivity activity, Fragment fragment, String tag) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment, tag)
                .commit();
    }

    /**
     * @param activity
     * @param fragment
     * @param tag
     * @param id       minor ID
     */
    public static void insertFragmentWithId(AppCompatActivity activity, Fragment fragment, String tag, int id) {
        Bundle args = new Bundle();
        args.putInt("ID", id);
        fragment.setArguments(args);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment, tag)
                .commit();
    }

    public static Bitmap getImageBitmap(String base64) {
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public static void setSettingsIntent(AppCompatActivity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }

    /**
     * @param activity
     * @param id       MInor ID
     */
    //to use if the slide fragment is on top
    public static void selectElementInSlider(AppCompatActivity activity, int id) {
        FragmentManager manager = activity.getSupportFragmentManager();
        ListFragment listFragment = (ListFragment) manager.findFragmentByTag(ListFragment.FRAGMENT_LIST);
        if (listFragment != null) {
            Log.d("Utility", "Select item in list");
            listFragment.selectElement(id);
        }
    }

    public static void showToastMessage(AppCompatActivity activity, String message) {
        Toast toast = Toast.makeText(activity, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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

    public static boolean isArchived(AppCompatActivity activity, int id) {
        FragmentManager manager = activity.getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) manager.findFragmentByTag(MapFragment.MAP_FRAGMENT_TAG);
        if (mapFragment != null) {
            Log.d("UTILITY", "get archived");
            return mapFragment.isArchived(id) == 1;
        } else {
            Log.d("UTILITY", "List Sightplaces not accessible");
            return true;
        }
    }

    public static boolean isListFragmentOnTop(AppCompatActivity activity) {
        FragmentManager manager = activity.getSupportFragmentManager();
        ListFragment listFragment = (ListFragment) manager.findFragmentByTag(ListFragment.FRAGMENT_LIST);
        return listFragment != null;
    }

    /**
     * debug implementation check if minor exist in list
     *
     * @param UUID
     * @return true if ok
     */
    public static boolean checkBeaconUUID(AppCompatActivity activity, int UUID) {
        FragmentManager manager = activity.getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) manager.findFragmentByTag(MapFragment.MAP_FRAGMENT_TAG);
        if (mapFragment != null) {
            Log.d("UTILITY", "get archived");
            return mapFragment.isArchived(UUID) >= 0;
        } else {
            Log.d("UTILITY", "List Sightplaces not accessible");
            return false;
        }
    }

    public static void showSightplaceInfo(AppCompatActivity activity, int minor_id) {
        FragmentManager manager = activity.getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) manager.findFragmentByTag(MapFragment.MAP_FRAGMENT_TAG);
        if (mapFragment != null) {
            Snackbar.make(
                    activity.findViewById(R.id.snackbar_position),
                    activity.getString(R.string.archived, mapFragment.getTitle(minor_id)),
                    Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.snackbar_position)
                    .addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            mapFragment.dismissTooltip(minor_id);
                        }
                    }).show();

            mapFragment.showTooltip(minor_id);
        } else {
            Log.d("UTILITY", "List Sightplaces not accessible");
        }

    }

}
