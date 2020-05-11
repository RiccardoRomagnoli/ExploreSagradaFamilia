package com.example.exploresagradafamilia.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.exploresagradafamilia.Database.SightplaceRepository;
import com.example.exploresagradafamilia.Sightplace;


/**
 * The ViewModel class is designed to store and manage UI-related data in a lifecycle conscious way.
 * The ViewModel class allows data to survive configuration changes such as screen rotations.
 * <p>
 * The data stored by ViewModel are not for long term. (Until activity is destroyed)
 * <p>
 * This ViewModel is linked to the addFragment, so it has only the addItem method
 */
public class AddSightplaceViewModel extends AndroidViewModel {

    private SightplaceRepository repository;

    public AddSightplaceViewModel(@NonNull Application application) {
        super(application);
        repository = new SightplaceRepository(application);
    }

    public void addItem(Sightplace item) {
        repository.addSightplace(item);
    }
}
