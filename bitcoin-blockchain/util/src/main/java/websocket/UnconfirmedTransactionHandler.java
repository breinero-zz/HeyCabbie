package websocket;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.List;

/**
 * Created by brein on 5/15/2016.
 */
public class UnconfirmedTransactionHandler implements Handler {

    private final String name = "utx";
    private final String message = "{\"op\":\"unconfirmed_sub\"}";
    private final MongoClient client;
    private static final String collectionName = "transactions";
    private static final String databaseName = "bitcoin";

    private final MongoCollection collection;


    public UnconfirmedTransactionHandler(MongoClient client) {
        this.client = client;
        collection = client.getDatabase( databaseName ).getCollection( collectionName );
    }

    @Override
    public void Handle(Document msg) {
        Document x = (Document) msg.get( "x" );
        String hash = x.getString( "hash" );

        ((List<Document>) x.get( "inputs" )).forEach(
                input -> {
                        // make sure that all values are longs
                        Long value = x.getLong( "value" );
                        x.put( "value ", value );
                        x.put( "_id", hash );
                }
        );
        collection.insertOne( x );
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
