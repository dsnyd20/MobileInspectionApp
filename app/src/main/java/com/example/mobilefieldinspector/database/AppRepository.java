package com.example.mobilefieldinspector.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.mobilefieldinspector.utilities.SampleData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppRepository {
    private static AppRepository ourInstance;

    public LiveData<List<ShopEntity>> mShops;
    public LiveData<List<RailcarEntity>> mRailcars;
    public LiveData<List<InspectionEntity>> mInspections;
    public LiveData<List<PhotoEntity>> mPhotos;
    private AppDatabase mDb;
    private Executor executor = Executors.newSingleThreadExecutor();

    public static AppRepository getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new AppRepository(context);
        }
        return ourInstance; }

    private AppRepository(Context context) {
        mDb = AppDatabase.getInstance(context);
        mShops = getAllShops();
        mRailcars = getAllRailcars();
        mInspections = getAllInspections();
        mPhotos = getAllPhotos();
    }

    public void addSampleData() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.shopDao().insertAll(SampleData.getShops());
                mDb.railcarDao().insertAll(SampleData.getRailcars());
                mDb.inspectionDao().insertAll(SampleData.getInspections());
            }
        });
    }

    public void deleteAllData() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.shopDao().deleteAll();
                mDb.railcarDao().deleteAll();
                mDb.inspectionDao().deleteAll();
                mDb.photoDao().deleteAll();
            }
        });
    }

    public LiveData<List<ShopEntity>> getAllShops() {
        return mDb.shopDao().getAll();
    }

    public LiveData<List<RailcarEntity>> getAllRailcars() { return mDb.railcarDao().getAll(); }

    public LiveData<List<InspectionEntity>> getAllInspections() { return  mDb.inspectionDao().getAll(); }

    public LiveData<List<PhotoEntity>> getAllPhotos() { return mDb.photoDao().getAll(); }

    public void insertShop(final ShopEntity shop) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.shopDao().insertShop(shop);
            }
        });
    }

    public void insertRailcar(final RailcarEntity railcar) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.railcarDao().insertRailcar(railcar);
            }
        });
    }

    public void insertInspection(final InspectionEntity inspection) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.inspectionDao().insertInspection(inspection);
            }
        });
    }

    public void insertPhoto(final PhotoEntity photo) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.photoDao().insertPhoto(photo);
            }
        });
    }

    public ShopEntity getShopById(int shopId) {
        return mDb.shopDao().getShopById(shopId);
    }

    public RailcarEntity getRailcarById(int railcarId) { return mDb.railcarDao().getRailcarById(railcarId); }

    public InspectionEntity getInspectionById(int inspectionId) { return mDb.inspectionDao().getInspectionById(inspectionId); }

    public PhotoEntity getPhotoById(int photoId) { return  mDb.photoDao().getPhotoById(photoId); }

    public void deleteShop(final ShopEntity shop) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.shopDao().deleteShop(shop);
            }
        });
    }

    public void deleteRailcar(final RailcarEntity railcar) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.railcarDao().deleteRailcar(railcar);
            }
        });
    }

    public void deleteInspection(final InspectionEntity inspection) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.inspectionDao().deleteInspection(inspection);
            }
        });
    }

    public void deletePhoto(final PhotoEntity photo) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.photoDao().deletePhoto(photo);
            }
        });
    }

    public LiveData<List<InspectionEntity>> getAllInspectionsForRailcar(int railcarId) {
        return mDb.inspectionDao().getAllInspectionsForRailcar(railcarId);
    }

    public LiveData<List<PhotoEntity>> getAllPhotosForInspection(int inspectionId) {
        return mDb.photoDao().getAllPhotosByInspectionId(inspectionId);
    }

    public int getShopInspectionCount (int shopId){
        return mDb.inspectionDao().getShopInspectionCount(shopId);
    }

    public LiveData<List<InspectionEntity>> getAllInspectionsForShop(int shopId) {
        return mDb.inspectionDao().getAllInspectionsForShop(shopId);
    }

    public int getAllInspectionsForRailcarCheck(int railcarId, int shopId, String inspectionDate, int id) {
        return mDb.inspectionDao().getAllInspectionsForRailcarCheck(railcarId, shopId, inspectionDate, id);
    }

    public List<ShopEntity> getShopsForAdapterPosition(){
        return mDb.shopDao().getShopsForAdapterPosition();
    }
}

