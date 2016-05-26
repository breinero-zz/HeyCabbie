package com.bryanreinero.taxi.distance;

import com.bryanreinero.taxi.RunningTotal;
import com.bryanreinero.lambda.Snapshot;
import org.bson.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by brein on 5/23/2016.
 */
public class SnapToDocConverter {

    public static Document convert(Snapshot<RunningTotal> snapshot) {

        Document doc = new Document( "_id", snapshot.getEndTime() );
        doc.put( "start", snapshot.getStartTime() );


        Set<Document> totals = new HashSet<>();
        snapshot.getRunningTotals().forEach( total ->
                {
                    Document d = new Document( "taxi", total.getId() );
                    d.put( "latitude", total.getLat() );
                    d.put( "longitude", total.getLng() );
                    d.put( "total", total.getTotal() );
                    totals.add( d );
                }
        );
        doc.put( "totals", totals );
        return doc;
    }
}
