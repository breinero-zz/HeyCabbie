package com.bryanreinero.taxi.distance;

import com.bryanreinero.taxi.TaxiLog;
import com.bryanreinero.taxi.codec.TaxiLogCodec;
import com.bryanreinero.taxi.webapp.LogReplayer;
import com.bryanreinero.taxi.TaxiLog;
import com.bryanreinero.taxi.codec.TaxiLogCodec;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * Created by brein on 5/19/2016.
 */
public class DistanceSnapshotGenerator {

    private MongoClient mongo;
    private final MongoDatabase taxiDB;
    private final MongoCollection<TaxiLog> taxiLogs;
    private final MongoCollection distanceSnapshots;
    private final LogReplayer<TaxiLog> replay;

    public DistanceSnapshotGenerator() {
        CodecRegistry registry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs( new TaxiLogCodec() )
        );

        MongoClientOptions options = MongoClientOptions.builder().codecRegistry( registry ).build();
        this.mongo =  new MongoClient(  new ServerAddress( "localhost"), options );
        taxiDB =  mongo.getDatabase( "taxi" );
        taxiLogs = taxiDB.getCollection("taxilogs", TaxiLog.class );
        distanceSnapshots = taxiDB.getCollection( "distanceSnapshots" );
;

        replay = new LogReplayer<>( taxiLogs );
        for( int i = 144; i <= 144; i+=48 )  {
            Integer interval = ( 86400 / i  );
            replay.addBuilder(
                    new DistanceSnapshotBuilder( interval , taxiDB.getCollection("distSnap" + interval))
            );
        }

    }

    public static void main( String[] args ) {
        DistanceSnapshotGenerator snappy = new DistanceSnapshotGenerator();
        snappy.generate( 1213089934, 1211018404 );
        //snappy
    }

    private void generate( Integer start, Integer end ) {
        replay.replayLogs(end, start);
    }
}
