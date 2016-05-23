package com.bryanreinero.taxi;

import com.bryanreinero.taxi.webapp.LogReplayer;
import com.bryanreinero.taxi.codec.TaxiLogCodec;
import com.bryanreinero.taxi.distance.DistanceTotals;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * Created by brein on 5/16/2016.
 */
public class Taxi {

    private MongoClient mongo;
    private final MongoDatabase taxiDB;
    private final MongoCollection<TaxiLog> taxiLogs;
    private DistanceTotals totals = new DistanceTotals();

    private final LogReplayer<TaxiLog> replay;


    public Taxi () {

        CodecRegistry registry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs( new TaxiLogCodec() )
        );

        MongoClientOptions options = MongoClientOptions.builder().codecRegistry( registry ).build();
        this.mongo =  new MongoClient(  new ServerAddress( "localhost"), options );
        taxiDB =  mongo.getDatabase( "taxi" );
        taxiLogs = taxiDB.getCollection("taxilogs", TaxiLog.class );

        replay = new LogReplayer<TaxiLog>( taxiLogs );

        replay.addBuilder( totals );
    }


    public void replayLogs ( Integer startTime, Integer endTime ) {
        replay.replayLogs( startTime, endTime );
        totals.getView();
    }

    public static void main ( String[] args ) {
        Taxi taxi = new Taxi();
        taxi.replayLogs( 1211025051, 1211151000 );
        //taxi.replayLogs( 1213089934, 0 );
    }
}
