package com.bryanreinero.taxi.fare;

import com.bryanreinero.taxi.TaxiLog;
import com.bryanreinero.taxi.webapp.Snapshot;
import com.bryanreinero.taxi.webapp.ViewBuilder;
import com.bryanreinero.taxi.TaxiLog;
import com.mongodb.client.MongoCollection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brein on 5/21/2016.
 */
public class InProgressSnapGen implements ViewBuilder<TaxiLog, FareSnapshot> {

    private Map<String, Fare> faresInProgress = new HashMap<String, Fare>();

    private boolean started = false;
    private final Integer interval;
    private final MongoCollection snapshots;
    private Integer nextSnapTS;

    public InProgressSnapGen( Integer interval, MongoCollection snapshots  ) {
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
            Snapshot<Fare> snapshot = new Snapshot( log.getTimestamp() , nextSnapTS  );
            faresInProgress.values().forEach( fare -> snapshot.add( fare ) );
            snapshots.insertOne( SnapshotConverter.convert(snapshot) );
            nextSnapTS += interval;
        }


        Fare fare = faresInProgress.get( log.getId() );

        if ( fare == null ) {

            if( !log.getFare() ) return;

            fare = new Fare( log.getId(), log.getTimestamp() );
            fare.addLeg( log );
            faresInProgress.put( log.getId(), fare );
        }
        else {
            if( log.getFare() )
                fare.addLeg( log );
            else {
                faresInProgress.remove( fare.getId() );
            }
        }
    }

    @Override
    public FareSnapshot getView() { return null; }
}
