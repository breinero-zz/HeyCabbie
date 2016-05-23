package com.bryanreinero.taxi;

/**
 * Created by brein on 5/15/2016.
 */
public class TaxiLog {

    private final String id;
    private final Double lattitude, longitude;
    private final Boolean fare;
    private final Integer timestamp;

    public TaxiLog(String id, Double lattitude, Double longitude, Boolean fare, Integer timestamp ) {
        this.id = id;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.fare = fare;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public Double getLattitude() {
        return lattitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Boolean getFare() {
        return fare;
    }

    public Integer getTimestamp() {
        return timestamp;
    }
}
