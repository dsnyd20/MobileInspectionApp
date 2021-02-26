package com.example.mobilefieldinspector.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobilefieldinspector.database.AppRepository;
import com.example.mobilefieldinspector.database.InspectionEntity;
import com.example.mobilefieldinspector.database.RailcarEntity;
import com.example.mobilefieldinspector.database.ShopEntity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RailcarDetailViewModel extends AndroidViewModel {
    public MutableLiveData<RailcarEntity> mLiveRailcar =
            new MutableLiveData<>();
    private Executor executor = Executors.newSingleThreadExecutor();
    private AppRepository mRepository;
    public LiveData<List<InspectionEntity>> mInspections;

    public RailcarDetailViewModel(@NonNull Application application) {
        super(application);

        mRepository = AppRepository.getInstance(application.getApplicationContext());
        mInspections = mRepository.getAllInspectionsForRailcar(0);
    }

    public void loadData(final int railcarId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                RailcarEntity railcar = mRepository.getRailcarById(railcarId);
                mLiveRailcar.postValue(railcar);
            }
        });

        mInspections = mRepository.getAllInspectionsForRailcar(railcarId);
    }

    public int saveRailcar(String runningNumber, String carType, String builtDate) {
        RailcarEntity railcar = mLiveRailcar.getValue();
        RailcarEntity tempRailcar = new RailcarEntity(runningNumber.trim(), carType.trim(), builtDate.trim());
        if (!tempRailcar.isValid()) {
            return 0;
        }
        if (railcar == null) {
            railcar = tempRailcar;
        } else {
            railcar.setRunningNumber(runningNumber.trim());
            railcar.setCarType(carType.trim());
            railcar.setBuiltDate(builtDate.trim());
        }
        mRepository.insertRailcar(railcar);
        return 1;
    }

    public void deleteRailcar() {
        mRepository.deleteRailcar(mLiveRailcar.getValue());
    }
}
