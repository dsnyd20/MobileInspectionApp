package com.example.mobilefieldinspector.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ShopDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertShop(ShopEntity shopEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ShopEntity> shops);

    @Delete
    void deleteShop(ShopEntity shopEntity);

    @Query("SELECT * FROM shops WHERE id = :id")
    ShopEntity getShopById(int id);

    @Query("SELECT * FROM shops ORDER BY shopName ASC")
    LiveData<List<ShopEntity>> getAll();

    @Query("DELETE FROM shops")
    int deleteAll();

    @Query("SELECT COUNT(*) FROM shops")
    int getCount();

    @Query("SELECT * FROM shops ORDER BY shopName ASC")
    List<ShopEntity> getShopsForAdapterPosition();
}

