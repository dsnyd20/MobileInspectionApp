package com.example.mobilefieldinspector.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InspectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInspection(InspectionEntity inspectionEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<InspectionEntity> inspections);

    @Delete
    void deleteInspection(InspectionEntity inspectionEntity);

    @Query("SELECT * FROM inspections WHERE id = :id")
    InspectionEntity getInspectionById(int id);

    @Query("SELECT * FROM inspections ORDER BY inspectionDate ASC")
    LiveData<List<InspectionEntity>> getAll();

    @Query("DELETE FROM inspections")
    int deleteAll();

    @Query("SELECT COUNT(*) FROM inspections")
    int getCount();

    @Query("SELECT railcarId FROM inspections WHERE id = :inspectionId")
    int getRailcarByInspectionId(int inspectionId);

    @Query("SELECT * FROM inspections WHERE railcarId = :railcarId")
    LiveData<List<InspectionEntity>> getAllInspectionsForRailcar(int railcarId);

    @Query("SELECT COUNT(*) FROM inspections WHERE shopId = :shopId")
    int getShopInspectionCount(int shopId);

    @Query("SELECT * FROM inspections WHERE shopId = :shopId")
    LiveData<List<InspectionEntity>> getAllInspectionsForShop(int shopId);

    @Query("SELECT COUNT(*) FROM inspections WHERE railcarId = :railcarId AND inspectionDate = :inspectionDate AND shopId<>:shopId AND id<>:id")
    int getAllInspectionsForRailcarCheck(int railcarId, int shopId,String inspectionDate, int id);
}
