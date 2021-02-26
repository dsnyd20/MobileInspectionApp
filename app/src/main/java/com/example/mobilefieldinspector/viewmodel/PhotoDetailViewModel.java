package com.example.mobilefieldinspector.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.mobilefieldinspector.database.AppRepository;
import com.example.mobilefieldinspector.database.PhotoEntity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PhotoDetailViewModel extends AndroidViewModel {
    public MutableLiveData<PhotoEntity> mLivePhoto =
            new MutableLiveData<>();
    private Executor executor = Executors.newSingleThreadExecutor();
    private AppRepository mRepository;


    public PhotoDetailViewModel(@NonNull Application application) {
        super(application);

        mRepository = AppRepository.getInstance(application.getApplicationContext());
    }

    public void loadData(final int photoId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                PhotoEntity photo = mRepository.getPhotoById(photoId);
                mLivePhoto.postValue(photo);
            }
        });
    }

    public int savePhoto(String imageUri, int inspectionId, String note) {
        PhotoEntity photo = mLivePhoto.getValue();
        PhotoEntity tempPhoto = new PhotoEntity(inspectionId, note, imageUri);
        if (!tempPhoto.isValid()) {
            return 0;
        }

        if (photo == null) {
            photo = tempPhoto;
        } else {
            photo.setImageUri(imageUri);
            photo.setInspectionId(inspectionId);
            photo.setPhotoNote(note.trim());
        }
        mRepository.insertPhoto(photo);
        return 1;
    }

    public void deletePhoto() {
        mRepository.deletePhoto(mLivePhoto.getValue());
    }
}
