package com.example.mobilefieldinspector.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobilefieldinspector.database.AppRepository;
import com.example.mobilefieldinspector.database.ShopEntity;

import java.util.List;

public class ShopListViewModel extends AndroidViewModel {
    public LiveData<List<ShopEntity>> mShops;
    private AppRepository mRepository;

    public ShopListViewModel(@NonNull Application application) {
        super(application);

        mRepository = AppRepository.getInstance(application.getApplicationContext());
        mShops = mRepository.mShops;
    }

    public void addSampleData() {
        mRepository.addSampleData();
    }

    public void deleteAllData() {
        mRepository.deleteAllData();
    }
}
