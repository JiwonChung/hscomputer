package domain;

import java.util.ArrayList;

public class Site {

    private Region region;
    private ArrayList<Building> buildings = new ArrayList<>();
    private String errorMessages = "";

    private int buildingNumber;
    private double grossFloorArea;


    public ArrayList<Building> addNewBuilding(Building building) {
        buildings.add(building);
        return buildings;
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

    public void setBuilding(ArrayList<Building> buildings) {
        this.buildings = buildings;
    }

    public void setBuilding(Building building) {
        this.buildings.add(building);
    }

    public String getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(String errorMessages) {
        this.errorMessages = errorMessages;
    }

    public void addErrorMessages(String errorMessages) {
        this.errorMessages += ("\n" + errorMessages);
    }

    public int getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(int buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public void setBuildingNumber() {
        buildingNumber = this.buildings.size();
    }


}
