package com.example.exploresagradafamilia.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.exploresagradafamilia.Sightplace;

import java.util.List;

@Dao
interface SightplaceDAO {
    // The selected on conflict strategy ignores a new CardItem
    // if it's exactly the same as one already in the list.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addSightplace(Sightplace sightplace);

    @Transaction
    @Query("SELECT * from Sightplace ORDER BY sightplace_id DESC")
    LiveData<List<Sightplace>> getSightplaces();

    @Transaction
    @Query("UPDATE Sightplace SET sightplace_archived =:archived WHERE sightplace_id =:id")
    void setArchived(int id, boolean archived);
}
