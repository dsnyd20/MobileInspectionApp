package com.example.mobilefieldinspector.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "shops")
public class ShopEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String shopName;
    private String shopAddress;
    private String shopPhone;

    //When creating a shop and assigning the values individually
    @Ignore
    public ShopEntity() {
    }

    //When a shop already exists and we want to edit it
    public ShopEntity(int id, String shopName, String shopAddress, String shopPhone) {
        this.id = id;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.shopPhone = shopPhone;
    }

    //When creating a shop but wanting the id to be generated automatically
    @Ignore
    public ShopEntity(String shopName, String shopAddress, String shopPhone) {
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.shopPhone = shopPhone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    @Override
    public String toString() {
        return shopName;
    }

    public boolean isValid() {
        if (shopName.isEmpty() || shopAddress.isEmpty() || shopPhone.isEmpty()) {
            return false;
        }

        return true;
    }
}
