package websocket;

import org.bson.Document;

/**
 * Created by brein on 5/15/2016.
 */
public class StatusHandler implements Handler {

    @Override
    public void Handle(Document msg) {
        System.out.println( msg );
    }

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getMessage() {
        return null;
    }
}
