package com.bryanreinero.taxi.distance;

import com.bryanreinero.taxi.Haversine;
import com.bryanreinero.taxi.RunningTotal;
import com.bryanreinero.taxi.TaxiLog;
import com.bryanreinero.taxi.webapp.Snapshot;
import com.bryanreinero.taxi.webapp.ViewBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brein on 5/18/2016.
 */
public class DistanceAggregator implements ViewBuilder <TaxiLog, String> {

    private Map<String, RunningTotal> runningTotals = new HashMap<>();

    private int count = 0;

    public DistanceAggregator(Snapshot<RunningTotal> snapshot ) {
        if( snapshot != null )
            snapshot.getRunningTotals().forEach(
                total -> runningTotals.put( total.getId(), total )
        );
    }

    @Override
    public void takeLog(TaxiLog log) {

        RunningTotal total = runningTotals.get( log.getId() ) ;

        count++;

        if( total  == null  ) {
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
    public String getView() {

        StringBuffer buf = new StringBuffer();

        for( Map.Entry<String, RunningTotal> e : runningTotals.entrySet() ) {
            buf.append( "taxi: " + e.getKey() + ", dist: "+ e.getValue().getTotal()+"\n" );
        }
        return buf.toString();
    }
}
