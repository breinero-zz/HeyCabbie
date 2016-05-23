package com.bryanreinero.taxi.fare;

import com.bryanreinero.firehose.metrics.Format;
import com.bryanreinero.firehose.metrics.Interval;
import com.bryanreinero.firehose.metrics.SampleSet;
import com.bryanreinero.taxi.webapp.LogReplayer;
import com.bryanreinero.taxi.webapp.Snapshot;
import com.bryanreinero.taxi.TaxiLog;
import com.bryanreinero.taxi.codec.TaxiLogCodec;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.Random;

import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Indexes.descending;

/**
 * Created by brein on 5/21/2016.
 */
public class TaxIFareView {

    private MongoClient mongo;
    private final MongoDatabase taxiDB;
    private final MongoCollection<TaxiLog> taxiLogs;
    private final MongoCollection<Document> snapshots;

    private final SampleSet stats = new SampleSet();

    private final LogReplayer<TaxiLog> replay;

    public TaxIFareView() {

        CodecRegistry registry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs( new TaxiLogCodec() )
        );

        MongoClientOptions options = MongoClientOptions.builder().codecRegistry( registry ).build();
        this.mongo =  new MongoClient(  new ServerAddress( "localhost"), options );
        taxiDB =  mongo.getDatabase( "taxi" );
        taxiLogs = taxiDB.getCollection("taxilogs", TaxiLog.class );
        snapshots = taxiDB.getCollection( "fareSnap600" );

        replay = new LogReplayer<>( taxiLogs );
    }

    public void replayLogs ( Integer endTime  ) {

        final Snapshot<Fare>[] snap = new Snapshot[1];

        try (  Interval t = stats.set("loadSnapshot") ) {
            for (Document doc : snapshots.find(lte("_id", endTime)).sort(descending("_id")).limit(1))
                snap[0] = SnapshotConverter.convert(doc);
        }

        // this is not right
        replay.addBuilder( new InProgressSnapGen( 600, snapshots) );

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
        TaxIFareView taxi = new TaxIFareView();

        Random rand = new Random();
        Integer ts = (int)( System.currentTimeMillis() / 1000  );
        for( int i = 0; i < 10; i++ )
            taxi.replayLogs(rand.nextInt(ts));

        System.out.println( taxi.report() );
    }
}
