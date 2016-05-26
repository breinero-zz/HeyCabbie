package com.bryanreinero.taxi.fare;

import com.bryanreinero.lambda.Snapshot;
import org.bson.Document;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by brein on 5/21/2016.
 */
public class SnapshotConverter  {

    public static Document convert( Snapshot<Fare> snapshot ) {

        Document doc = new Document( "_id", snapshot.getEndTime() );
        doc.put( "start", snapshot.getStartTime() );


        Set<Document> fares = new HashSet<>();
        snapshot.getRunningTotals().forEach( fare ->
                {
                    Document d = new Document( "taxi", fare.getId() );
                    d.put( "start", fare.getStart() );
                    d.put( "end", fare.getEnd() );
                    d.put( "route", fare.getRoute() );
                    fares.add( d );
                }
        );

        doc.put( "fares", fares);
        return doc;
    }

    public static Snapshot<Fare> convert(Document doc) {

        Snapshot<Fare> fares = new Snapshot<>( doc.getInteger( "start" ), doc.getInteger( "_id" ) );

        ( (List<Document>) doc.get( "fares" )).forEach(
                fare -> {
                    Fare f = new Fare(
                            fare.getString( "taxi" ),
                            fare.getInteger( "start" )
                    );
                    ( (List<List<Double>>)fare.get( "route" )).forEach(
                            point -> {
                                f.addLeg(point);
                            }
                    );

                    fares.add( f );
                }
        );
        return fares;
    }

    public static String convertToGEOSON( Snapshot<Fare> snapshot ){
        StringBuffer buf = new StringBuffer();

        buf.append("{ \"type\": \"MultiLineString\",\n" +
                "\t\"coordinates\": [" );

        Iterator<Fare> fares = snapshot.getRunningTotals().iterator();
        while ( fares.hasNext() ) {
            buf.append( "\n\t\t[ " );

            Fare fare = fares.next();

            Iterator<List<Double>> route = fare.getRoute().iterator();
            while( route.hasNext()  ) {
                buf.append( "\n\t\t\t[ " );
                Iterator<Double> points = route.next().iterator();
                buf.append(points.next() + ", " );
                buf.append(points.next() );
                buf.append( " ]" );

                if( route.hasNext() )
                    buf.append("," );
            }
            buf.append( "\n\t\t]");
            if ( fares.hasNext() )
                buf.append(",");
        }
        buf.append( "\n\t]\n}" );
        return buf.toString();
    }
}
