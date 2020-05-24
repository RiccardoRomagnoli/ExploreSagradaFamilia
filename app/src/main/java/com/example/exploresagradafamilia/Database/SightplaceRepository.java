package com.example.exploresagradafamilia.Database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.exploresagradafamilia.Sightplace;

import java.util.List;

public class SightplaceRepository {
    private SightplaceDAO SightplaceDAO;
    private LiveData<List<Sightplace>> sightplace_list;

    public SightplaceRepository(Application application) {
        SightplaceDatabase db = SightplaceDatabase.getDatabase(application);
        SightplaceDAO = db.SightplaceDAO();
        sightplace_list = SightplaceDAO.getSightplaces();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Sightplace>> getItems() {
        return sightplace_list;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void addSightplace(final Sightplace Sightplace) {
        SightplaceDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SightplaceDAO.addSightplace(Sightplace);
            }
        });
    }

    public void setArchived(final int id) {
        SightplaceDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SightplaceDAO.setArchived(id, true);
            }
        });
    }
//
//    public int isArchived(final int id) {
//        return SightplaceDAO.isArchived(id);
//    }
}
