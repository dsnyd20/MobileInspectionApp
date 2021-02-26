package com.example.mobilefieldinspector.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "inspections")
public class InspectionEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String inspectionName;
    private String inspectionDate;
    private String note;
    private int railcarId;
    private int shopId;

    //When creating a inspection and assigning the values individually
    @Ignore
    public InspectionEntity() {
    }

    //When a inspection already exists and we want to edit it
    public InspectionEntity(int id, String inspectionName,String inspectionDate, String note, int railcarId, int shopId) {
        this.id = id;
        this.inspectionName = inspectionName;
        this.inspectionDate = inspectionDate;
        this.note = note;
        this.railcarId = railcarId;
        this.shopId = shopId;
    }


    //When creating a inspection but wanting the id to be generated automatically
    @Ignore
    public InspectionEntity(String inspectionName, String inspectionDate, String note, int railcarId, int shopId) {
        this.inspectionName = inspectionName;
        this.inspectionDate = inspectionDate;
        this.note = note;
        this.railcarId = railcarId;
        this.shopId = shopId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInspectionName() {
        return inspectionName;
    }

    public void setInspectionName(String inspectionName) {
        this.inspectionName = inspectionName;
    }

    public String getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(String inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getRailcarId() {
        return railcarId;
    }

    public void setRailcarId(int railcarId) {
        this.railcarId = railcarId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    @Override
    public String toString() {
        return inspectionName;
    }

    public boolean isValid() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        if (inspectionName.isEmpty() || inspectionDate.isEmpty() || note.isEmpty()) {
            return false;
        }

        try {
            // Make sure the dates are in the correct format
            Date inspection = dateFormat.parse(inspectionDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
