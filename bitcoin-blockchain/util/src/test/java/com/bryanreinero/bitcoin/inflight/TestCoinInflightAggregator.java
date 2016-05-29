package com.bryanreinero.bitcoin.inflight;

import com.bryanreinero.firehose.metrics.Interval;
import com.bryanreinero.firehose.metrics.SampleSet;
import com.bryanreinero.lambda.LogReplayer;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Created by brein on 5/28/2016.
 */
public class TestCoinInflightAggregator {

    private final MongoDatabase bitcoinDB;
    private final MongoCollection<Document> transactions;


    private final SampleSet stats = new SampleSet();

    private final LogReplayer<Document> replay;

    public TestCoinInflightAggregator(MongoClient mongo) {
        bitcoinDB = mongo.getDatabase( "bitcoin" );
        this.transactions = bitcoinDB.getCollection( "unconfirmed" );
        this.replay = new LogReplayer<>( transactions );

    }


    public void getAllCoinInFlight () {

        replay.addBuilder( new CoinInFlightAggegator() );
        Long time = System.currentTimeMillis() / 1000L;
        try (  Interval t = stats.set("logReplay") ) {
            replay.replayLogs( 0, time.intValue() );
        }
    }


    public static void main( String[] args ) {

    }
}
