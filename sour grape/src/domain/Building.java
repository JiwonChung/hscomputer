package domain;

public class Building {
    private Region region;
    private Long undergroundFloor;
    private Long groundFloor;
    private Long grossFloorArea;
    private Long buildingArea;

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Long getUndergroundFloor() {
        return undergroundFloor;
    }

    public void setUndergroundFloor(Long undergroundFloor) {
        this.undergroundFloor = undergroundFloor;
    }

    public Long getGroundFloor() {
        return groundFloor;
    }

    public void setGroundFloor(Long groundFloor) {
        this.groundFloor = groundFloor;
    }

    public Long getGrossFloorArea() {
        return grossFloorArea;
    }

    public void setGrossFloorArea(Long grossFloorArea) {
        this.grossFloorArea = grossFloorArea;
    }

    public Long getBuildingArea() {
        return buildingArea;
    }

    public void setBuildingArea(Long buildingArea) {
        this.buildingArea = buildingArea;
    }
}
