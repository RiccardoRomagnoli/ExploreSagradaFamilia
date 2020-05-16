package com.example.exploresagradafamilia;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.exploresagradafamilia.ViewModel.ArchiveSightplaceViewModel;
import com.example.exploresagradafamilia.ViewModel.ListSightplaceViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MainActivity activity;
    private MapView mapView;
    ExtendedFloatingActionButton extFab;
    private List<Sightplace> sightplacesList = new ArrayList<>();
    private boolean firstLoad = true;
    Object syncToken = new Object();
    // Create an Icon object for the markers
    Icon icon_toarchive;
    Icon icon_archived;
    Snackbar snackbar;

    MapboxMap mapboxMap = null;
    private BidiMap<Integer, Marker> markers;

    private static final LatLng BOUND_CORNER_NW = new LatLng(41.403931, 2.173907);
    private static final LatLng BOUND_CORNER_SE = new LatLng(41.403221, 2.174800);
    private static final LatLngBounds RESTRICTED_BOUNDS_AREA = new LatLngBounds.Builder()
            .include(BOUND_CORNER_NW)
            .include(BOUND_CORNER_SE)
            .build();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
        IconFactory iconFactory = IconFactory.getInstance(activity);
        icon_toarchive = iconFactory.fromResource(R.drawable.binoculars_blue);
        icon_archived = iconFactory.fromResource(R.drawable.trophy_fancy);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(activity, getString(R.string.access_token));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void roomObserver(List<Sightplace> sightplaces) {
        sightplacesList = sightplaces;
        if (firstLoad) {
            firstLoad = false;
            markers = new DualHashBidiMap<>();
            addMarkers();
        } else {
            sightplacesList.forEach(itemChanged -> {
                if (markers.get(itemChanged.getId()).getIcon().equals(icon_toarchive) && itemChanged.getArchived()) {
                    //place archived
                    markers.get(itemChanged.getId()).setIcon(icon_archived);
                }
            });
        }
    }

    private void addMarkers() {
        if (mapboxMap != null) {
            //add markers to map
            sightplacesList.forEach(item -> {
                MarkerOptions tempMark = new MarkerOptions()
                        .position(new LatLng(item.getLatitude(), item.getLongitude()))
                        .icon(item.getArchived() ? icon_archived : icon_toarchive)
                        .title(item.getTitle());
                Marker marker = mapboxMap.addMarker(tempMark);
                markers.put(item.getId(), marker);
            });
        } else {
            Toast.makeText(activity, "ROOM FASTER THAN MAP READY...", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView mTitle = view.findViewById(R.id.toolbar_title);
        mTitle.setText(getString(R.string.page_title));

        //map
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //fab
        extFab = view.findViewById(R.id.extFab);
        extFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(activity, "FAB TOUCH", Toast.LENGTH_LONG).show();
            }
        });

        //snackbar
        snackbar = Snackbar.make(
                view.findViewById(R.id.snackbar_position),
                R.string.not_archived,
                Snackbar.LENGTH_LONG).setAnchorView(R.id.snackbar_position);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.LIGHT, style -> {
            // Map is set up and the style has loaded. Now you can add data or make other map adjustments.
            mapboxMap.setLatLngBoundsForCameraTarget(RESTRICTED_BOUNDS_AREA);
        });

        ArchiveSightplaceViewModel modelArchive = new ViewModelProvider(activity).get(ArchiveSightplaceViewModel.class);

        this.mapboxMap.setOnMarkerClickListener(markerClicked -> {
            //marker listener
            if (sightplacesList.stream().filter(item -> item.getId() == markers.getKey(markerClicked)).collect(Collectors.toList()).get(0).getArchived()) {
                //place aready reached

                return true;
            } else {
                //to archive
                snackbar.show();
                //imodelArchive.archiveItem(markers.getKey(markerClicked));
                return false;
            }
        });

        //when the list of the items changed, the adapter gets the new list.
        ListSightplaceViewModel model = new ViewModelProvider(activity).get(ListSightplaceViewModel.class);
        model.getItems().observe(activity, this::roomObserver);
    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
