package websocket;

import org.bson.Document;

/**
 * Created by brein on 5/15/2016.
 */
public class LatestBlockHandler implements Handler {
    @Override
    public void Handle(Document msg) {
        System.out.println( msg );
    }

    @Override
    public String getName() {
        return "block";
    }

    @Override
    public String getMessage() {
        return  "{\"op\":\"ping_block\"}";
    }
}
