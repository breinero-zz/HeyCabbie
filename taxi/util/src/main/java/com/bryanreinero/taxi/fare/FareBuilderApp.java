package com.bryanreinero.taxi.fare;

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
 * Created by brein on 5/21/2016.
 */
public class FareBuilderApp {

    private MongoClient mongo;
    private final MongoDatabase taxiDB;
    private final MongoCollection<TaxiLog> taxiLogs;
    private final LogReplayer<TaxiLog> replay;

    public FareBuilderApp() {
        CodecRegistry registry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs( new TaxiLogCodec() )
        );

        MongoClientOptions options = MongoClientOptions.builder().codecRegistry( registry ).build();
        this.mongo =  new MongoClient(  new ServerAddress( "localhost"), options );
        taxiDB =  mongo.getDatabase( "taxi" );
        taxiLogs = taxiDB.getCollection("taxilogs", TaxiLog.class );

        replay = new LogReplayer<>( taxiLogs );
        Integer interval = 600;
        replay.addBuilder(
                    new InProgressSnapGen( interval , taxiDB.getCollection("fareSnap" + interval) )
        );
    }

    public static void main( String[] args ) {
        FareBuilderApp snappy = new FareBuilderApp();
        //snappy.generate( 1213089934, 1211018404 );
        snappy.generate( 1211019600, 1211019000 );
    }

    private void generate( Integer start, Integer end ) {
        replay.replayLogs(end, start);
    }
}
