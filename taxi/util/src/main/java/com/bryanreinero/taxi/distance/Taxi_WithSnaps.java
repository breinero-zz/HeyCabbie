package com.bryanreinero.taxi.distance;

import com.bryanreinero.firehose.metrics.Format;
import com.bryanreinero.firehose.metrics.Interval;
import com.bryanreinero.firehose.metrics.SampleSet;
import com.bryanreinero.taxi.RunningTotal;
import com.bryanreinero.taxi.TaxiLog;
import com.bryanreinero.taxi.codec.TaxiLogCodec;
import com.bryanreinero.lambda.LogReplayer;
import com.bryanreinero.lambda.Snapshot;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.Random;

import static com.bryanreinero.taxi.distance.DistanceSnapshotConverter.convert;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Indexes.descending;

/**
 * Created by brein on 5/19/2016.
 */
public class Taxi_WithSnaps {

    private MongoClient mongo;
    private final MongoDatabase taxiDB;
    private final MongoCollection<TaxiLog> taxiLogs;
    private final MongoCollection<Document> distanceSnapshots;

    private final SampleSet stats = new SampleSet();

    private final LogReplayer<TaxiLog> replay;

    public Taxi_WithSnaps () {

        CodecRegistry registry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs( new TaxiLogCodec() )
        );

        MongoClientOptions options = MongoClientOptions.builder().codecRegistry( registry ).build();
        this.mongo =  new MongoClient(  new ServerAddress( "localhost"), options );
        taxiDB =  mongo.getDatabase( "taxi" );
        taxiLogs = taxiDB.getCollection("taxilogs", TaxiLog.class );
        distanceSnapshots = taxiDB.getCollection( "distSnap600" );

        replay = new LogReplayer<>( taxiLogs );


    }

    public void replayLogs ( Integer endTime  ) {

        final Snapshot<RunningTotal>[] snap = new Snapshot[1];

        try (  Interval t = stats.set("loadSnapshot") ) {
            for (Document doc : distanceSnapshots.find(lte("_id", endTime)).sort(descending("_id")).limit(1)) {
                snap[0] = convert( doc );
            }
        }

        replay.addBuilder( new DistanceAggregator(snap[0]) );

        Integer start = 0;
        if( snap[0] != null )
            start = snap[0].getEndTime();

        try (  Interval t = stats.set("logReplay") ) {
            replay.replayLogs( start, endTime );
        }
    }

    public String report() {
        return Format.report( stats );
    }

    public static void main ( String[] args ) {
        Taxi_WithSnaps taxi = new Taxi_WithSnaps();

        Random rand = new Random();
        Integer ts = (int)( System.currentTimeMillis() / 1000  );
        //for( int i = 0; i < 10; i++ )
        //    taxi.replayLogs(rand.nextInt(ts));


        taxi.replayLogs( 1211019599 );
        String entry = taxi.report();

        System.out.println( taxi.report() );
    }
}
