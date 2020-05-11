package com.example.exploresagradafamilia;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.exploresagradafamilia.ViewModel.ListSightplaceViewModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //insert the layout in the Activity
        setContentView(R.layout.activity_main);
        TextView hw = findViewById(R.id.textView);

        ListSightplaceViewModel model = new ViewModelProvider(this).get(ListSightplaceViewModel.class);
        //when the list of the items changed, the adapter gets the new list.
        model.getItems().observe(this, sightplaces -> {
            hw.setText(Integer.toString(sightplaces.size()));
        });

        //the application is just started (never gone on onStop())
        if (savedInstanceState == null) {
            //Utility.insertFragment(this, new HomeFragment(), FRAGMENT_TAG);
        }
    }
}
