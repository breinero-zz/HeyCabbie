package websocket;

import com.mongodb.MongoClient;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOneModel;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by brein on 5/15/2016.
 */
public class BlockHandler implements Handler {

    private final String op = "block";
    private final String message = "{\"op\":\"blocks_sub\"}";
    private final MongoClient client;
    private static final String blockCollName = "blocks";
    private static final String xActionsCollName = "transactions";
    private static final String databaseName = "bitcoin";

    private final MongoCollection blocks, transactions;

    public BlockHandler( MongoClient client) {
        this.client = client;
        blocks = client.getDatabase( databaseName ).getCollection( blockCollName );
        transactions = client.getDatabase( databaseName ).getCollection( xActionsCollName );
    }

    @Override
    public void Handle(Document msg) {
        blocks.insertOne( msg );

        Document x = (Document) msg.get( "x" );
        String blockHash = msg.getString( "hash" );
        List<Integer> transactionIDs = (List<Integer>)x.get( "txIndexes" );


        List<UpdateOneModel> updates = new ArrayList<>();
        // update all unconfirmed transactions that appear in this block
        for( int i = 0; i < transactionIDs.size(); i++ ) {
            Document query = new Document("index", transactionIDs.get(i));
            Document update = new Document(
                    "$set",
                    new Document("block", blockHash)
            );
            updates.add(new UpdateOneModel<Document>(query, update));
        }

        BulkWriteResult result = transactions.bulkWrite( updates );

        if ( result.getModifiedCount() != transactionIDs.size() ) {
            System.out.println("Missing " + (transactionIDs.size() - result.getModifiedCount()) + " transactions");
        }
    }

    @Override
    public String getName() {
        return op;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
