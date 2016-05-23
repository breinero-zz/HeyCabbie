package com.bryanreinero.taxi.distance;

import com.bryanreinero.taxi.Haversine;
import com.bryanreinero.taxi.RunningTotal;
import com.bryanreinero.taxi.TaxiLog;
import com.bryanreinero.taxi.webapp.Snapshot;
import com.bryanreinero.taxi.webapp.ViewBuilder;
import com.bryanreinero.taxi.Haversine;
import com.bryanreinero.taxi.RunningTotal;
import com.bryanreinero.taxi.TaxiLog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brein on 5/17/2016.
 */
public class DistanceTotals implements ViewBuilder<TaxiLog, Snapshot<RunningTotal>> {

    @Override
    public void takeLog(TaxiLog log) {

        RunningTotal total = null;

        if( ( total = runningTotals.get( log.getId() ) ) == null  ) {
            total = new RunningTotal( log.getId() );
            total.setLat( log.getLattitude() );
            total.setLng( log.getLongitude() );
            total.setTotal( 0d );
            runningTotals.put( log.getId(), total );
        }
        else {
            Double dist = Haversine.getDistance(
                    log.getLattitude(), total.getLat(), log.getLongitude(), total.getLng()
            );
            total.setTotal( total.getTotal() + dist );
            total.setLat( log.getLattitude() );
            total.setLng( log.getLongitude() );
        }
    }

    @Override
    public Snapshot<RunningTotal> getView() {

        for( Map.Entry<String, RunningTotal> e : runningTotals.entrySet() ) {
            System.out.println( "taxi: " + e.getKey() + ", dist: "+ e.getValue().getTotal() );

        }
        return null;
    }

    private Map<String, RunningTotal> runningTotals = new HashMap<>();
}
