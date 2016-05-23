package com.bryanreinero.taxi.fare;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by brein on 5/21/2016.
 */
public class FareSnapshot  {

    Integer start, end;

    Set<Fare> fares = new HashSet<>();

    public FareSnapshot(Integer timestamp, Integer nextSnapTS) {
    }

    public void add(Fare fare) {
        fares.add( fare );
    }
}
