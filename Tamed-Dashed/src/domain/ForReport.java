package domain;

public class ForReport {
    private Region region;
    private Error error;
    private double edubuilGrossFloorArea, landRegisteredGrossFloorArea;
    private int edubuilBuildingNumber, landRegisteredBuildingNumber;

    private double grossFloorAreaDifference, buildingNumberDifference;

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public double getEdubuilGrossFloorArea() {
        return edubuilGrossFloorArea;
    }

    public void setEdubuilGrossFloorArea(double edubuilGrossFloorArea) {
        this.edubuilGrossFloorArea = edubuilGrossFloorArea;
    }

    public double getLandRegisteredGrossFloorArea() {
        return landRegisteredGrossFloorArea;
    }

    public void setLandRegisteredGrossFloorArea(double landRegisteredGrossFloorArea) {
        this.landRegisteredGrossFloorArea = landRegisteredGrossFloorArea;
    }

    public int getEdubuilBuildingNumber() {
        return edubuilBuildingNumber;
    }

    public void setEdubuilBuildingNumber(int edubuilBuildingNumber) {
        this.edubuilBuildingNumber = edubuilBuildingNumber;
    }

    public int getLandRegisteredBuildingNumber() {
        return landRegisteredBuildingNumber;
    }

    public void setLandRegisteredBuildingNumber(int landRegisteredBuildingNumber) {
        this.landRegisteredBuildingNumber = landRegisteredBuildingNumber;
    }

    public double getGrossFloorAreaDifference() {
        return grossFloorAreaDifference;
    }

    public void setGrossFloorAreaDifference(double grossFloorAreaDifference) {
        this.grossFloorAreaDifference = grossFloorAreaDifference;
    }

    public double getBuildingNumberDifference() {
        return buildingNumberDifference;
    }

    public void setBuildingNumberDifference(double buildingNumberDifference) {
        this.buildingNumberDifference = buildingNumberDifference;
    }
}

