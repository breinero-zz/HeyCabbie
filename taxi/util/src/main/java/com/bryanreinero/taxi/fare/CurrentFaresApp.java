package com.bryanreinero.taxi.fare;

import com.bryanreinero.firehose.metrics.Interval;
import com.bryanreinero.firehose.metrics.SampleSet;
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

import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Indexes.descending;

/**
 * Created by brein on 5/21/2016.
 */
public class CurrentFaresApp {

    private MongoClient mongo;
    private final MongoDatabase taxiDB;
    private LogReplayer<TaxiLog> replay;
    private FareBuilderWithSnap builder;
    private final SampleSet samples = new SampleSet();

    public CurrentFaresApp() {
        CodecRegistry registry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs( new TaxiLogCodec() )
        );

        MongoClientOptions options = MongoClientOptions.builder().codecRegistry( registry ).build();
        this.mongo =  new MongoClient(  new ServerAddress( "localhost"), options );
        taxiDB =  mongo.getDatabase( "taxi" );
    }

    public void replayLogs ( Integer endTime  ) {

        MongoCollection<Document> snapshots = taxiDB.getCollection( "fareSnap600" );

        Snapshot<Fare>[] snap = new Snapshot[1];

        try (  Interval t = samples.set("loadSnapshot") ) {
            for (Document doc : snapshots.find(lte("_id", endTime)).sort(descending("_id")).limit(1)) {
                snap[0] = SnapshotConverter.convert( doc );
            }
        }

        replay = new LogReplayer<>( taxiDB.getCollection("taxilogs", TaxiLog.class ) );
        builder = new FareBuilderWithSnap( snap[0] );
        replay.addBuilder( builder );

        Integer start = 0;
        if( snap[0] != null )
            start = snap[0].getEndTime();

        try (  Interval t = samples.set("logReplay") ) {
            replay.replayLogs( start, endTime );
        }
    }

    public void report() {

        //System.out.println( SnapshotConverter.convertToGEOSON( builder.getView() ) );
    }

    public static void main( String[] args ) {
        CurrentFaresApp app = new CurrentFaresApp();

        Random rand = new Random();
        Integer ts = (int)( System.currentTimeMillis() / 1000  );
        //for( int i = 0; i < 10; i++ )
           // app.replayLogs(rand.nextInt(ts));
        app.replayLogs( 1211020199 );

        app.report();
    }
}
