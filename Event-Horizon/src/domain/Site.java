package domain;

import java.util.ArrayList;

public class Site {

    private long id;
    private Region region;
    private final ArrayList<Building> buildings = new ArrayList<>();

    // API received 일때는 총괄 or 표제부
    private double grossFloorArea;
    private int buildingNumber;

    private Error error;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Error getError() {
        return error;
    }

    public int getBuildingNumber() {
        return buildingNumber;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public ArrayList<Building> getBuildings() {
        return buildings;
    }

    public void addBuilding(Building building) {
        this.buildings.add(building);
    }


    public double getGrossFloorArea() {
        return grossFloorArea;
    }


    public double getGrossFloorArea(int a) {
        double grossFloorArea = 0;
        for (Building building : buildings) {
            grossFloorArea += building.getGrossFloorArea();
        }
        return grossFloorArea;
    }

    public void setGrossFloorArea(double grossFloorArea) {
        this.grossFloorArea = grossFloorArea;
    }

    public Error getErrorMessages() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
    public void setBuildingNumber(int buildingNumber) {
        this.buildingNumber = buildingNumber;
    }
}
