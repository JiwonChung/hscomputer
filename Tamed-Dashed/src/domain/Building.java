package domain;

public class Building {
    private Region region;
    private int undergroundFloor;
    private int groundFloor;
    private double grossFloorArea;

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public int getUndergroundFloor() {
        return undergroundFloor;
    }

    public void setUndergroundFloor(int undergroundFloor) {
        this.undergroundFloor = undergroundFloor;
    }

    public int getGroundFloor() {
        return groundFloor;
    }

    public void setGroundFloor(int groundFloor) {
        this.groundFloor = groundFloor;
    }

    public double getGrossFloorArea() {
        return grossFloorArea;
    }

    public void setGrossFloorArea(double grossFloorArea) {
        this.grossFloorArea = grossFloorArea;
    }
}
