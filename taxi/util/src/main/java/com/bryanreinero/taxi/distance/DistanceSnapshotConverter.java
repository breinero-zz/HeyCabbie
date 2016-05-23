package com.bryanreinero.taxi.distance;

import com.bryanreinero.taxi.RunningTotal;
import com.bryanreinero.taxi.webapp.Snapshot;
import org.bson.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by brein on 5/19/2016.
 */
public class DistanceSnapshotConverter {

    public static Document convert( Snapshot<RunningTotal> snapshot) {

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

    public static Snapshot convert( Document doc ) {
        Integer start = doc.getInteger("start");
        Integer end = doc.getInteger("_id");

        Snapshot snapshot = new Snapshot( start, end );

        ( (List<Document>) doc.get( "totals" )).forEach(
                total -> {
                    RunningTotal rt = new RunningTotal( total.getString("taxi") );
                    rt.setLat( total.getDouble("latitude"));
                    rt.setLng( total.getDouble("longitude"));
                    rt.setTotal( total.getDouble("total"));
                    snapshot.add( rt );
                }
        );
        return snapshot;
    }
}
