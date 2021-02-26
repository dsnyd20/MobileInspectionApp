package com.example.mobilefieldinspector.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {ShopEntity.class, RailcarEntity.class, InspectionEntity.class, PhotoEntity.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    public static final String  DATABASE_NAME = "AppDatabase.db";
    private static volatile AppDatabase instance;
    private static final Object LOCK = new Object();

    public abstract ShopDao shopDao();
    public abstract RailcarDao railcarDao();
    public abstract InspectionDao inspectionDao();
    public abstract PhotoDao photoDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
                }
            }
        }
        return instance;
    }
}
