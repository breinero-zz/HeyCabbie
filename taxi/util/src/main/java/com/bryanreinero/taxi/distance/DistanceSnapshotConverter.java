package com.bryanreinero.taxi.distance;

import com.bryanreinero.taxi.RunningTotal;
import com.bryanreinero.lambda.Snapshot;
import org.bson.Document;

import java.util.List;

/**
 * Created by brein on 5/19/2016.
 */
public class DistanceSnapshotConverter {

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
