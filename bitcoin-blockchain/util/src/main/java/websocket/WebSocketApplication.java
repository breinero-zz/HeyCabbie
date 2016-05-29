package websocket;

import com.mongodb.MongoClient;
import org.bson.Document;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by brein on 5/2/2016.
 */
@WebSocket
public class WebSocketApplication {

    private Session session;

    private  final SslContextFactory sslContextFactory;
    private  final Resource keyStoreResource;
    private  final org.eclipse.jetty.websocket.client.WebSocketClient client;
    private final String  uri;

    private final CountDownLatch latch= new CountDownLatch(1);

    private final Map<String, Handler> handlers = new HashMap<String, Handler>();

    public WebSocketApplication(String uri ) {

        keyStoreResource = Resource.newResource(this.getClass().getResource("/truststore.jks"));
        sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStoreResource(keyStoreResource);
        sslContextFactory.setKeyStorePassword("password");
        sslContextFactory.setKeyManagerPassword("password");
        client = new org.eclipse.jetty.websocket.client.WebSocketClient(sslContextFactory);
        this.uri = uri;
    }

    public void run() throws Exception {
        client.start();
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        client.connect( this, new URI( uri ), request);
        latch.await();
    }

    public void sendMessage ( String message ) throws Exception {
        try {
            session.getRemote().sendString( message );
        } catch (IOException e) {
            throw new Exception( "Could not send message "+message+" to "+uri );
        }
    }

    public void addHandler( Handler handler ) throws Exception {

        handlers.put( handler.getName(), handler );

        try {
            latch.await();
            session.getRemote().sendString( handler.getMessage() );
        } catch ( IOException | InterruptedException e) {
            throw new Exception( "Failed to send message: "+handler.getMessage(), e );
        }
    }

    @OnWebSocketMessage
    public void onText(Session session, String message) throws IOException {

        //System.out.println("Message received from server:" + message);
        final Document doc =  Document.parse( message ) ;

        Handler handler = handlers.get( doc.getString( "op" ) );
        if( handler == null )
            System.out.println( "Recieved unhandled message. "+message);
        try {
            handler.Handle( doc );
        } catch ( Exception e ) {
            throw new IOException( "Failed to handle text message. ", e );
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Connected to server");
        this.session=session;
        latch.countDown();
    }

    @OnWebSocketClose
    public void onClose( int statusCode, String reason ) {
        System.out.printf("Connection closed: %d - %s%n",statusCode,reason);
        this.session = null;
        //this.latch.countDown(); // trigger latch
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public static void main(String[] args) {

        final MongoClient mongoClient = new MongoClient();

        WebSocketApplication client = new WebSocketApplication( "wss://ws.blockchain.info/inv" );
        try {
            client.run();
            //client.addHandler( new StatusHandler() );
            client.addHandler( new BlockHandler( mongoClient ) );
            //client.addHandler( new UnconfirmedTransactionHandler( mongoClient) );

            client.sendMessage( "{\"op\":\"ping_block\"}" );


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
