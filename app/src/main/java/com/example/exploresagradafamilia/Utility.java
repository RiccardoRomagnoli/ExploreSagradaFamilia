package com.example.exploresagradafamilia;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Utility {


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

    /**
     * Method called to save the image pf sightpalce in a dir of the application
     *
     * @return Uri of image saved
     */
    public static String saveImage(AppCompatActivity activity, String major, String minor, String title, Bitmap bitmap) throws IOException {
        Log.d("IMAGE", bitmap.toString());
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + activity.getString(R.string.app_name);
        File outputDir = new File(path);
        outputDir.mkdirs();
        String fileName = major + "-" + minor + "-" + title.replace(" ", "") + ".jpg";

        OutputStream out = null;
        File file = new File(path + File.separator + fileName);
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("IMAGE", e.getMessage());
        }
        path = file.getPath();
        Uri bmpUri = Uri.parse("file://" + path);
        return bmpUri.getPath();
    }

    public static Bitmap getImageFromUri(AppCompatActivity activity, String imageUri) {
        Log.d("IMAGE", imageUri);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), Uri.parse("file://" + imageUri));
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}
