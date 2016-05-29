package com.bryanreinero.bitcoin.inflight;

import com.bryanreinero.firehose.metrics.Interval;
import com.bryanreinero.firehose.metrics.SampleSet;
import com.bryanreinero.lambda.LogReplayer;
import com.bryanreinero.lambda.ViewBuilder;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Created by brein on 5/29/2016.
 */
public class TestTotalSpendAggregator {

    private final MongoDatabase bitcoinDB;
    private final MongoCollection<Document> transactions;

    private final SampleSet stats = new SampleSet();
    private final LogReplayer<Document> replay;

    public TestTotalSpendAggregator(MongoClient mongo) {
        bitcoinDB = mongo.getDatabase( "bitcoin" );
        this.transactions = bitcoinDB.getCollection( "transactions" );
        this.replay = new LogReplayer<>( transactions, new Document() );
    }

    public void getAllCoinInFlight () {

        Long time = System.currentTimeMillis() / 1000L;
        final ViewBuilder<Document, String> aggregator =  new SpendInFlightAggregator() ;
        replay.addBuilder(aggregator);
        try (  Interval t = stats.set("logReplay") ) {
            replay.replayLogs( 0, time.intValue() );
        }
        System.out.println ( aggregator.getView() );
    }

    public static void main( String[] args ){
        MongoClient client = new MongoClient();
        TestTotalSpendAggregator test = new TestTotalSpendAggregator( client );
        test.getAllCoinInFlight();
    }


}
