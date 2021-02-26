package com.example.mobilefieldinspector.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobilefieldinspector.database.AppRepository;
import com.example.mobilefieldinspector.database.RailcarEntity;

import java.util.List;

public class RailcarListViewModel extends AndroidViewModel {
    public LiveData<List<RailcarEntity>> mRailcars;
    private AppRepository mRepository;

    public RailcarListViewModel(@NonNull Application application) {
        super(application);

        mRepository = AppRepository.getInstance(application.getApplicationContext());
        mRailcars = mRepository.mRailcars;
    }

    public void addSampleData() {
        mRepository.addSampleData();
    }

    public void deleteAllData() {
        mRepository.deleteAllData();
    }
}
