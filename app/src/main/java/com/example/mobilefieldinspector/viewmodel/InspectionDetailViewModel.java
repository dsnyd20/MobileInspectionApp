package com.example.mobilefieldinspector.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobilefieldinspector.database.AppRepository;
import com.example.mobilefieldinspector.database.InspectionEntity;
import com.example.mobilefieldinspector.database.PhotoEntity;
import com.example.mobilefieldinspector.database.RailcarEntity;
import com.example.mobilefieldinspector.database.ShopEntity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class InspectionDetailViewModel extends AndroidViewModel {
    public MutableLiveData<InspectionEntity> mLiveInspection =
            new MutableLiveData<>();
    public LiveData<List<ShopEntity>> allShops;
    public LiveData<List<PhotoEntity>> mPhotos;
    private List<ShopEntity> adapterShops;
    public RailcarEntity mRailcar;
    public ShopEntity mShop;
    private Executor executor = Executors.newSingleThreadExecutor();
    private AppRepository mRepository;
    public int inspectionCheck = 1;
    public String inspectionShare;
    private InspectionEntity inspection;


    public InspectionDetailViewModel(@NonNull Application application) {
        super(application);

        mRepository = AppRepository.getInstance(application.getApplicationContext());
        allShops = mRepository.getAllShops();
        mPhotos = mRepository.getAllPhotosForInspection(0);
        inspectionShare = "Save inspection before sharing.";
    }

    public void loadData(final int inspectionId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                inspection = mRepository.getInspectionById(inspectionId);
                mLiveInspection.postValue(inspection);
                mRailcar = mRepository.getRailcarById(inspection.getRailcarId());
                mShop = mRepository.getShopById(inspection.getShopId());

            }
        });
        mPhotos = mRepository.getAllPhotosForInspection(inspectionId);
    }


    public int getPosition(@Nullable ShopEntity item) {
        adapterShops = mRepository.getShopsForAdapterPosition();
        for (ShopEntity shop : adapterShops) {
            if (shop.toString().equals(item.toString())) {
                return adapterShops.indexOf(shop);
            }
        }
        return -1;
    }

    public String getInspectionShare(){
        InspectionEntity currentInspection = mLiveInspection.getValue();
        inspectionShare = "Inspection Report for " + mRailcar.getRunningNumber() + ":\n"
                + "Inspection Performed at " + mShop.getShopName() + ", " + mShop.getShopAddress() + " on " + currentInspection.getInspectionDate() + ".\n"
                + currentInspection.getInspectionName() + ": " + currentInspection.getNote() + "\n";

        return inspectionShare;
    }

    public int saveInspection(final int railcarId, final int shopId, final String inspectionDate, String note, String inspectionName) {
        InspectionEntity inspection = mLiveInspection.getValue();
        InspectionEntity tempInspection = new InspectionEntity(inspectionName.trim(),inspectionDate.trim(), note.trim(), railcarId, shopId);
        if (!tempInspection.isValid()) {
            return 0;
        }
        if (inspection == null) {
            //This is the only query allowed on the main thread
            inspectionCheck =  mRepository.getAllInspectionsForRailcarCheck(railcarId, shopId, inspectionDate, 0);
            if (inspectionCheck > 0) {
                Log.d("TAG", "saveInspection: " + railcarId + " " + shopId + " " + inspectionDate +" " + " 0 appeared "+ inspectionCheck + " times in the database.");
                return 2;
            }
            inspection = tempInspection;
        } else {
            //This is the only query allowed on the main thread
            inspectionCheck =  mRepository.getAllInspectionsForRailcarCheck(railcarId, shopId, inspectionDate, inspection.getId());
            if (inspectionCheck > 0) {
                Log.d("TAG", "saveInspection: " + railcarId + " " + shopId + " " + inspectionDate +" " + inspection.getId()+" appeared "+ inspectionCheck + " times in the database.");
                return 2;
            }
            inspection.setInspectionName(inspectionName);
            inspection.setRailcarId(railcarId);
            inspection.setShopId(shopId);
            inspection.setInspectionDate(inspectionDate.trim());
            inspection.setNote(note.trim());
        }
        mRepository.insertInspection(inspection);
        return 1;
    }

    public LiveData<List<ShopEntity>> getAllShops() {
        return allShops;
    }

    public void deleteInspection() {
        mRepository.deleteInspection(mLiveInspection.getValue());
    }
}
