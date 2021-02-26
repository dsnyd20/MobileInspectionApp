package com.example.mobilefieldinspector.utilities;

import com.example.mobilefieldinspector.database.InspectionEntity;
import com.example.mobilefieldinspector.database.RailcarEntity;
import com.example.mobilefieldinspector.database.ShopEntity;

import java.util.ArrayList;
import java.util.List;

public class SampleData {
    public static List<ShopEntity> getShops() {
        List<ShopEntity> shops = new ArrayList<>();
        shops.add(new ShopEntity(1, "Premier Rail - Dallas", "123 Snyder Dr, Dallas, Tx", "555-1234"));
        shops.add(new ShopEntity(2, "Premier Rail - Niles", "324 Daniel Ave, Niles, Mi", "555-5454"));
        shops.add(new ShopEntity(3, "Contract Shop - Seattle", "789 Leslie Ln, Seattle, Wa", "555-9432"));
        return shops;
    }

    public static List<RailcarEntity> getRailcars() {
        List<RailcarEntity> railcars = new ArrayList<>();
        railcars.add(new RailcarEntity(1, "DJSX123456", "Tanker", "1990/05/16"));
        railcars.add(new RailcarEntity(2, "DJSX657321", "Freight", "2001/08/22"));
        railcars.add(new RailcarEntity(3, "PRCX300500", "Tanker", "2019/03/19"));
        return railcars;
    }

    public static List<InspectionEntity> getInspections() {
        List<InspectionEntity> inspections = new ArrayList<>();
        inspections.add(new InspectionEntity(1, "Busted Coupler","2000/12/24", "Tanker is in good shape.", 1, 1));
        inspections.add(new InspectionEntity(2, "Routine Inspection","2010/11/05", "Not as good since previous inspection.", 1, 2));
        inspections.add(new InspectionEntity(3, "FRA Audit","2006/10/11", "Freight cars don't have much to inspect.", 2, 3));
        return inspections;
    }
}
