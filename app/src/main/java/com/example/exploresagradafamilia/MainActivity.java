package com.example.exploresagradafamilia;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.exploresagradafamilia.Beacons.BeaconUtility;
import com.example.exploresagradafamilia.Permissions.PermissionUtility;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    FragmentTransaction fragmentTransaction;
    BeaconUtility beaconUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //the application is just started (never gone on onStop())
        if (savedInstanceState == null) {
            Utility.insertFragment(this, new MapFragment(), MapFragment.MAP_FRAGMENT_TAG);
        }
        beaconUtility = new BeaconUtility(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconUtility.startService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        beaconUtility.stopService();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment.getTag() != MapFragment.MAP_FRAGMENT_TAG) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionUtility.PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    beaconUtility.startService();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.funcionality_limited);
                    builder.setMessage(getString(R.string.location_not_granted) +
                            getString(R.string.cannot_discover_beacons));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog -> {/*DO NOTHING*/});
                    builder.show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PermissionUtility.REQUEST_ENABLE_BLUETOOTH: {
                if (!PermissionUtility.isBluetoothEnabled(this)) {
                    PermissionUtility.askToTurnOnBluetooth(this);
                } else if (!PermissionUtility.isLocationEnabled(this)) {
                    PermissionUtility.askToTurnOnLocation(this);
                }
                break;
            }
            case PermissionUtility.REQUEST_ENABLE_POSITION: {
                if (!PermissionUtility.isLocationEnabled(this)) {
                    PermissionUtility.askToTurnOnLocation(this);
                } else if (!PermissionUtility.isBluetoothEnabled(this)) {
                    PermissionUtility.askToTurnOnBluetooth(this);
                }
                break;
            }
        }
    }
}
