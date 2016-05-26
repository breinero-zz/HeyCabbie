package com.bryanreinero.taxi.fare;

import com.bryanreinero.taxi.TaxiLog;
import com.bryanreinero.lambda.Snapshot;
import com.bryanreinero.lambda.ViewBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by brein on 5/21/2016.
 */
public class FareBuilderWithSnap implements ViewBuilder<TaxiLog, String> {

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
    public String getView() {

        StringBuffer buf = new StringBuffer();

        buf.append( "{ \"fares\": [");

        Iterator< Map.Entry<String, Fare> > it = faresInProgress.entrySet().iterator();
        while(it.hasNext() ) {

            Fare f = it.next().getValue();
            buf.append("{  \"taxi\": \""+f.getId()+"\"," );
            buf.append("\n\"start\": "+f.getStart()+",");
            buf.append("\n\"end\": "+f.getEnd()+"," );

            buf.append("\"route\": [");
            Iterator<List<Double>> iterator = f.getRoute().iterator();
            while( iterator.hasNext() ) {
                List<Double> list = iterator.next();
                buf.append("[ " + list.get(0) + ", " + list.get(1) + " ]");
                if( iterator.hasNext() )
                    buf.append(",");
            }
            buf.append("\n]\n}");
            if( it.hasNext() )
                buf.append(",");
        }

        buf.append("\n\t]\n}");
        return buf.toString();
    }
}
