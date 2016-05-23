package com.bryanreinero.taxi.webapp;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by brein on 5/18/2016.
 */
public class Snapshot <T> {
    private final Integer startTime;
    private final Integer endTime;
    private final Set<T> totals = new HashSet<>();

    public Snapshot(Integer startTime, Integer endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void add( T total ){
        totals.add( total );
    }

    public Iterable<T> getRunningTotals() {
        return totals;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public Set<T> getTotals() {
        return totals;
    }
}
