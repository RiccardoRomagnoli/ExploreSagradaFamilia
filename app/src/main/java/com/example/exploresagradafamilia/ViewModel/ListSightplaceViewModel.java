package com.example.exploresagradafamilia.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.exploresagradafamilia.Database.SightplaceRepository;
import com.example.exploresagradafamilia.Sightplace;

import java.util.List;

/**
 * The ViewModel class is designed to store and manage UI-related data in a lifecycle conscious way.
 * The ViewModel class allows data to survive configuration changes such as screen rotations.
 * <p>
 * The data stored by ViewModel are not for long term. (Until activity is destroyed)
 */
public class ListSightplaceViewModel extends AndroidViewModel {

    private LiveData<List<Sightplace>> item_list;

    public ListSightplaceViewModel(@NonNull Application application) {
        super(application);
        SightplaceRepository repository = new SightplaceRepository(application);
        item_list = repository.getItems();
    }

    public LiveData<List<Sightplace>> getItems() {
        return item_list;
    }
}
