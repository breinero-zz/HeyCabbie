package websocket;

import org.bson.Document;

/**
 * Created by brein on 5/15/2016.
 */
public interface Handler {

    void Handle(final Document msg);
    String getName();
    String getMessage();
}
