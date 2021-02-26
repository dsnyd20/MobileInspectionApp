package com.example.mobilefieldinspector.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;

@Entity(tableName = "photos")
public class PhotoEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int inspectionId;
    private String photoNote;
    private String imageUri;


    //When creating a inspection and assigning the values individually
    @Ignore
    public PhotoEntity() {
    }

    //When a inspection already exists and we want to edit it
    public PhotoEntity(int id, int inspectionId,String photoNote, String imageUri) {
        this.id = id;
        this.inspectionId = inspectionId;
        this.photoNote = photoNote;
        this.imageUri = imageUri;
    }

    //When creating a inspection but wanting the id to be generated automatically
    @Ignore
    public PhotoEntity(int inspectionId,String photoNote, String imageUri) {
        this.inspectionId = inspectionId;
        this.photoNote = photoNote;
        this.imageUri = imageUri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInspectionId() {
        return inspectionId;
    }

    public void setInspectionId(int inspectionId) {
        this.inspectionId = inspectionId;
    }

    public String getPhotoNote() {
        return photoNote;
    }

    public void setPhotoNote(String photoNote) {
        this.photoNote = photoNote;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }


    @Override
    public String toString() {
        return "PhotoEntity{" +
                "id=" + id +
                ", inspectionId=" + inspectionId +
                ", photoNote='" + photoNote + '\'' +
                ", image=" + imageUri +
                '}';
    }

    public boolean isValid() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        if (imageUri == null || photoNote.trim().isEmpty() || imageUri.trim().isEmpty()) {
            return false;
        }

        return true;
    }
}
