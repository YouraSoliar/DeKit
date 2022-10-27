package com.example.mlbirds;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BirdsDao {

    @Query("SELECT * FROM birds")
    LiveData<List<Bird>> getBirds();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add (Bird bird);

    @Query("DELETE FROM birds WHERE id=:id")
    void remove(int id);
}
