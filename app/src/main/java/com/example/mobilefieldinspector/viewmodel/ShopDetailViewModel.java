package com.example.mobilefieldinspector.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobilefieldinspector.database.AppRepository;
import com.example.mobilefieldinspector.database.InspectionEntity;
import com.example.mobilefieldinspector.database.ShopEntity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ShopDetailViewModel extends AndroidViewModel {
    public MutableLiveData<ShopEntity> mLiveShop =
            new MutableLiveData<>();
    private Executor executor = Executors.newSingleThreadExecutor();
    private AppRepository mRepository;
    public int shopUsage = 0;
    public LiveData<List<InspectionEntity>> mInspections;

    public ShopDetailViewModel(@NonNull Application application) {
        super(application);

        mRepository = AppRepository.getInstance(application.getApplicationContext());
        mInspections = mRepository.getAllInspectionsForShop(0);
    }

    public void loadData(final int shopId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ShopEntity shop = mRepository.getShopById(shopId);
                mLiveShop.postValue(shop);
                shopUsage = mRepository.getShopInspectionCount(shopId);
            }
        });

        mInspections = mRepository.getAllInspectionsForShop(shopId);
    }

    public int saveShop(String shopName, String shopAddress, String shopPhone) {
        ShopEntity shop = mLiveShop.getValue();
        ShopEntity tempShop = new ShopEntity(shopName.trim(), shopAddress.trim(), shopPhone.trim());
        if (!tempShop.isValid()) {
            return 0;
        }
        if (shop == null) {
            shop = tempShop;
        } else {
            shop.setShopName(shopName.trim());
            shop.setShopAddress(shopAddress.trim());
        }
        mRepository.insertShop(shop);
        return 1;
    }

    public void deleteShop() {
        mRepository.deleteShop(mLiveShop.getValue());
    }
}

