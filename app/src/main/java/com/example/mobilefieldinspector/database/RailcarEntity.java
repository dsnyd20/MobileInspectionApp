package com.example.mobilefieldinspector.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "railcars")
public class RailcarEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String runningNumber;
    private String carType;
    private String builtDate;

    //When creating a railcar and assigning the values individually
    @Ignore
    public RailcarEntity() {
    }

    //When a railcar already exists and we want to edit it
    public RailcarEntity(int id, String runningNumber, String carType, String builtDate) {
        this.id = id;
        this.runningNumber = runningNumber;
        this.carType = carType;
        this.builtDate = builtDate;
    }

    //When creating a railcar but wanting the id to be generated automatically
    @Ignore
    public RailcarEntity(String runningNumber, String carType, String builtDate) {
        this.runningNumber = runningNumber;
        this.carType = carType;
        this.builtDate = builtDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRunningNumber() {
        return runningNumber;
    }

    public void setRunningNumber(String runningNumber) {
        this.runningNumber = runningNumber;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getBuiltDate() {
        return builtDate;
    }

    public void setBuiltDate(String builtDate) {
        this.builtDate = builtDate;
    }

    @Override
    public String toString() {
        return runningNumber;
    }

    public boolean isValid() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        if (runningNumber.isEmpty() || carType.isEmpty() || builtDate.isEmpty()) {
            return false;
        }

        try {
            // Make sure the dates are in the correct format
            Date built = dateFormat.parse(builtDate);

            // Built date is in the future
            if (DateConverter.nowDate() < DateConverter.toTimestamp(built)) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
