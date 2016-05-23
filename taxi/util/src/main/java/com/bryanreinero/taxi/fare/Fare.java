package com.bryanreinero.taxi.fare;

import com.bryanreinero.taxi.TaxiLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brein on 5/15/2016.
 */
public class Fare {

    private final String id;
    private final Integer start;
    private Integer end;
    private List<List<Double>> route = new ArrayList<List<Double>>();

    public Fare(String id, Integer start) {
        this.id = id;
        this.start = start;
        end = start;
    }

    public void addLeg( TaxiLog log ) {
        end = log.getTimestamp();
        List<Double> coords = new ArrayList<Double>();
        coords.add( log.getLongitude() );
        coords.add( log.getLattitude() );
        route.add( coords );
    }

    public void addLeg( List<Double> path ) {
         route.add( path );
    }

    public String getId() {
        return id;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }

    public List<List<Double>> getRoute() {
        return route;
    }

}
