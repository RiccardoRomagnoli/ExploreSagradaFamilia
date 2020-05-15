package com.example.exploresagradafamilia;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.exploresagradafamilia.ViewModel.ListSightplaceViewModel;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    private static final String MAP_FRAGMENT_TAG = "MAP_FRAGMENT_TAG";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //the application is just started (never gone on onStop())
        if (savedInstanceState == null) {
            Utility.insertFragment(this, new MapFragment(), MAP_FRAGMENT_TAG);
        }

//        ListSightplaceViewModel model = new ViewModelProvider(this).get(ListSightplaceViewModel.class);
//        //when the list of the items changed, the adapter gets the new list.
//        model.getItems().observe(this, sightplaces -> {
//            //hw.setText(Integer.toString(sightplaces.size()));
//        });
    }
}
