package com.bryanreinero.taxi.fare;

import com.bryanreinero.taxi.webapp.Snapshot;
import com.bryanreinero.taxi.webapp.ViewBuilder;
import com.bryanreinero.taxi.TaxiLog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brein on 5/21/2016.
 */
public class FareBuilderWithSnap implements ViewBuilder<TaxiLog, Snapshot<Fare>> {

    private Integer start, end;

    private Map<String, Fare> faresInProgress = new HashMap<String, Fare>();

    public FareBuilderWithSnap( Snapshot<Fare> snapshot ) {
        if( snapshot != null ) {
            this.start = snapshot.getStartTime();
            this.end = snapshot.getEndTime();

            snapshot.getTotals().forEach(
                    fare -> {
                        faresInProgress.put(fare.getId(), fare);
                    }
            );
        }
    }

    @Override
    public void takeLog(TaxiLog log) {

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
    public Snapshot<Fare> getView() {

        Snapshot<Fare> snapshot = new Snapshot<>( start, end );

        for( Map.Entry<String, Fare> e : faresInProgress.entrySet() )
            snapshot.add( e.getValue() );

        return snapshot;
    }
}
