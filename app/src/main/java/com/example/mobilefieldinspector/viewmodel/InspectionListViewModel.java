package com.example.mobilefieldinspector.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobilefieldinspector.database.AppRepository;
import com.example.mobilefieldinspector.database.InspectionEntity;

import java.util.List;

public class InspectionListViewModel extends AndroidViewModel {
    public LiveData<List<InspectionEntity>> mInspections;
    private AppRepository mRepository;

    public InspectionListViewModel(@NonNull Application application) {
        super(application);

        mRepository = AppRepository.getInstance(application.getApplicationContext());
        mInspections = mRepository.mInspections;
    }

    public void addSampleData() {
        mRepository.addSampleData();
    }

    public void deleteAllData() {
        mRepository.deleteAllData();
    }
}
