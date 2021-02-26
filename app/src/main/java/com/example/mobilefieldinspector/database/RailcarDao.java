package com.example.mobilefieldinspector.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RailcarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRailcar(RailcarEntity railcarEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RailcarEntity> railcars);

    @Delete
    void deleteRailcar(RailcarEntity railcarEntity);

    @Query("SELECT * FROM railcars WHERE id = :id")
    RailcarEntity getRailcarById(int id);

    @Query("SELECT * FROM railcars ORDER BY runningNumber ASC")
    LiveData<List<RailcarEntity>> getAll();

    @Query("DELETE FROM railcars")
    int deleteAll();

    @Query("SELECT COUNT(*) FROM railcars")
    int getCount();
}
