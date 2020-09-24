package com.example.exploresagradafamilia.Beacons;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.exploresagradafamilia.ListFragment;
import com.example.exploresagradafamilia.Permissions.PermissionUtility;
import com.example.exploresagradafamilia.R;
import com.example.exploresagradafamilia.Utility;
import com.example.exploresagradafamilia.ViewModel.ArchiveSightplaceViewModel;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BeaconUtility implements BeaconConsumer, RangeNotifier {

    public static final String TAG_BEACON = "TAG_BEACON";
    private static final long DEFAULT_SCAN_PERIOD_MS = 6000l;
    private static final String IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String ALL_BEACONS_REGION = "AllBeaconsRegion";
    private static final String UUID = "c721e46c-2f3c-4cb6-bc28-dd4ed2073c2b";

    // Per interagire con i beacon
    private BeaconManager mBeaconManager;
    // Criterio di ricerca beacon
    private Region mRegion;
    List<Identifier> identifiers;
    AppCompatActivity activity;
    ArchiveSightplaceViewModel model;

    public BeaconUtility(AppCompatActivity activity) {
        this.activity = activity;
        mBeaconManager = BeaconManager.getInstanceForApplication(activity);
        // Protocollo beacon
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(IBEACON_LAYOUT));
        identifiers = new ArrayList<>();
        mRegion = new Region(ALL_BEACONS_REGION, identifiers);

        model = new ViewModelProvider(activity).get(ArchiveSightplaceViewModel.class);
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            // Ricerca beacon inclusi nell'oggetto regionale
            mBeaconManager.startRangingBeaconsInRegion(mRegion);
        } catch (RemoteException e) {
            Log.d(TAG_BEACON, "Eccezzione nell'attivare la ricerca di beacons " + e.getMessage());
        }
        mBeaconManager.addRangeNotifier(this);
    }

    @Override
    public Context getApplicationContext() {
        return activity;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        activity.unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return activity.bindService(intent, serviceConnection, i);
    }

    /**
     * Metodo invocato ogni DEFAULT_SCAN_PERIOD_MS
     * <p>
     * id1 = UUID
     * id2 = Major
     * id3 = Minor
     * rssi = distance
     */
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        if (beacons.size() == 0) {
            //Utility.showToastMessage(activity, activity.getString(R.string.no_beacons_detected));
        }

        for (Beacon beacon : beacons) {
            if (Utility.checkBeaconUUID(activity, beacon.getId3().toInt())) {
                if (!Utility.isArchived(activity, beacon.getId3().toInt())) {
                    model.archiveItem(beacon.getId3().toInt());

                    //insert fragment if not present, if present shift position
                    if (Utility.isListFragmentOnTop(activity)) {
                        Utility.selectElementInSlider(activity, beacon.getId3().toInt());
                    } else {
                        Utility.insertFragmentWithId(activity, new ListFragment(), ListFragment.FRAGMENT_LIST, beacon.getId3().toInt());
                    }
                } else {
                    //If already archived inform that you are nead a sightplace
                    Utility.showSightplaceInfo(activity, beacon.getId3().toInt());
                }
            }
        }
    }

    public void startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Richiesta permessi (se non già ottenuti)
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                PermissionUtility.askForLocationPermissions(activity);
            } else { // Permessi ottenuti
                checkServices();
                startDetectingBeacons();
            }
        } else { // Versione < 6
            checkServices();
            startDetectingBeacons();
        }
    }

    public void stopService() {
        stopDetectingBeacons();
    }

    private void stopDetectingBeacons() {
        try {
            mBeaconManager.stopMonitoringBeaconsInRegion(mRegion);
            Utility.showToastMessage(activity, activity.getString(R.string.stop_looking_for_beacons));
        } catch (RemoteException e) {
            Log.d(TAG_BEACON, "Eccezione alla terminazione del servizio" + e.getMessage());
        }
        mBeaconManager.removeAllRangeNotifiers();
        mBeaconManager.unbind(this);
    }

    /**
     * Inizio detecting beacons
     */
    private void startDetectingBeacons() {
        // Periodo di scan
        mBeaconManager.setForegroundScanPeriod(DEFAULT_SCAN_PERIOD_MS);
        // Associazione callback quando il servizio di ricerca  è attivo
        mBeaconManager.bind(this);
    }

    private void checkServices() {
        if (!PermissionUtility.isLocationEnabled(activity)) {
            PermissionUtility.askToTurnOnLocation(activity);
        } else if (!PermissionUtility.isBluetoothEnabled(activity)) {
            PermissionUtility.askToTurnOnBluetooth(activity);
        }
    }
}
