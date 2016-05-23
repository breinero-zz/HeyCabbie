package com.bryanreinero.taxi.webapp;


import com.bryanreinero.firehose.metrics.Interval;
import com.bryanreinero.firehose.metrics.SampleSet;
import com.bryanreinero.taxi.TaxiLog;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.bryanreinero.taxi.distance.DistanceSnapshotConverter.convert;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Indexes.descending;

/**
 * Created by brein on 5/22/2016.
 */
public class DistanceServlet extends HttpServlet {

    private String name;

    private MongoCollection<TaxiLog> taxilogs;
    private MongoCollection<Document> snapshots;

    private SampleSet stats;

    private LogReplayer<TaxiLog>replay;
    Constructor<?> ctor, converterCtor;
    Method convertMethod;

    private String loadSnapOpName;

    @Override
    public void init(ServletConfig config) throws ServletException {

        name = config.getServletName();

        MongoClient client = (MongoClient) config.getServletContext().getAttribute( "mongoclient" );
        MongoDatabase db = client.getDatabase( config.getInitParameter( "db" ) );

        taxilogs = db.getCollection( config.getInitParameter( "collection" ), TaxiLog.class ) ;
        replay = new LogReplayer( taxilogs );
        snapshots = db.getCollection( config.getInitParameter( "snapshots" ) ) ;

        loadSnapOpName = config.getInitParameter( "snapshotOperationName" );

        stats = (SampleSet) config.getServletContext().getAttribute( "stats" );

        try {
            Class<?> builderClazz = Class.forName(
                    config.getInitParameter("SnapBuilderClassName" )
            );

            ctor = builderClazz.getConstructor( Snapshot.class  );


        } catch ( ClassNotFoundException | NoSuchMethodException e) {
            throw new ServletException( name+" servlet failed to initialize. ", e );
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // Get the requested time
        String ts = req.getParameter( "t" );

        Integer endTime = Math.toIntExact(System.currentTimeMillis() / 1000);
        if( ts != null )
            endTime = Integer.parseInt( ts);

        final Snapshot[] snap = new Snapshot[1];

        try {
            try(  Interval t = stats.set( loadSnapOpName ) ) {
                for (Document doc : snapshots.find(lte("_id", endTime)).sort(descending("_id")).limit(1)) {
                    snap[0] = convert(doc);
                }
            }

            ViewBuilder agg = (ViewBuilder) ctor.newInstance(snap[0]);
            replay.addBuilder(agg);
            Integer start = 0;
            if (snap[0] != null)
                start = snap[0].getEndTime();

            try ( Interval x = stats.set("logReplay")) {
                replay.replayLogs(start, endTime);
            }

            resp.setContentType("text/plain");
            resp.getWriter().println(agg.getView());
        } catch (IllegalAccessException  | InstantiationException | InvocationTargetException e) {
            throw new IOException( name+"can not service request", e  );
        }
    }
}
