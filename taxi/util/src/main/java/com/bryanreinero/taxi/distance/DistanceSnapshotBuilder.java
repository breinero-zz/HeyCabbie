package com.bryanreinero.taxi.distance;

import com.bryanreinero.taxi.Haversine;
import com.bryanreinero.taxi.RunningTotal;
import com.bryanreinero.taxi.TaxiLog;
import com.bryanreinero.lambda.Snapshot;
import com.bryanreinero.lambda.ViewBuilder;
import com.mongodb.client.MongoCollection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brein on 5/18/2016.
 */
public class DistanceSnapshotBuilder implements ViewBuilder<TaxiLog, Snapshot<RunningTotal>>  {

    private boolean started = false;
    private final Integer interval;
    private final MongoCollection snapshots;
    private Integer nextSnapTS;

    public DistanceSnapshotBuilder(Integer interval, MongoCollection snapshots ) {
        this.interval = interval;
        this.snapshots = snapshots;
        this.nextSnapTS = interval;
    }

    @Override
    public void takeLog(TaxiLog log) {

        if( !started ) {
            // integer division
            nextSnapTS = ( ( log.getTimestamp() / interval ) * interval ) + interval;
            System.out.println( "First snap at "+nextSnapTS+", interval: "+interval );
            started = true;
        }

        if( log.getTimestamp() >  nextSnapTS ) {
            System.out.println( "writing snapshot at "+nextSnapTS );
            Snapshot<RunningTotal> snapshot = new Snapshot( log.getTimestamp() , nextSnapTS  );
            runningTotals.values().forEach( rt -> snapshot.add( rt ) );
            snapshots.insertOne( SnapToDocConverter.convert(snapshot) );
            nextSnapTS += interval;
        }

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
    public Snapshot<RunningTotal> getView() { return null; }

    private Map<String, RunningTotal> runningTotals = new HashMap<>();

}
