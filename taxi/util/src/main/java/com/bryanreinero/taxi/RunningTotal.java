package com.bryanreinero.taxi;

/**
 * Created by brein on 5/18/2016.
 */
public class RunningTotal {

    private final String id;
    Double lat, lng;
    Double total;

    public RunningTotal( String id ) {
        this.id = id;
    }

    public Double getLat() {
            return lat;
        }

    public void setLat(Double lat) {
            this.lat = lat;
        }

    public Double getLng() {
            return lng;
        }

    public void setLng(Double lng) {
            this.lng = lng;
        }

    public Double getTotal() {
            return total;
        }

    public void setTotal(Double total) {
            this.total = total;
        }

    public String getId() {
        return id;
    }
}
