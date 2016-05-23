package com.bryanreinero.taxi.fare;

import com.bryanreinero.taxi.webapp.Snapshot;
import com.bryanreinero.taxi.webapp.ViewBuilder;
import com.bryanreinero.taxi.TaxiLog;

import java.util.*;

/**
 * Created by brein on 5/15/2016.
 */
public class FareBuilder  implements ViewBuilder<TaxiLog, Snapshot<Fare> > {

    private Map<String, Fare> faresInProgress = new HashMap<String, Fare>();
    private List<Fare> fares = new ArrayList<Fare>();

    @Override
    public void takeLog( TaxiLog log ) {
        Fare fare = faresInProgress.get( log.getId() );

        if ( fare == null ) {

            if( log.getFare() ) return;

            fare = new Fare( log.getId(), log.getTimestamp() );
            fare.addLeg( log );
            faresInProgress.put( log.getId(), fare );
        }
        else {
            if( log.getFare() )
                fare.addLeg( log );

            else {
                fares.add( fare );
                faresInProgress.remove( fare.getId() );
            }
        }
    }

    @Override
    public Snapshot<Fare> getView() { System.out.println( "You complete me" ); return null;};

    public Iterator<Fare> getFares() {
        return fares.iterator();
    }


}
