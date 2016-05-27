package com.bryanreinero.lambda;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Indexes.descending;

/**
 * Created by brein on 5/25/2016.
 */
public class SnapshotCache {

    private final Map<Integer, Snapshot> cache = new HashMap();
    private final MongoCollection<Document> snapshots;
    private final Integer interval;
    private final Method convertMethod;

    public SnapshotCache(MongoCollection<Document> snapshots, Integer interval, Method convertMethod ) {
        this.snapshots = snapshots;
        this.interval = interval;
        this.convertMethod = convertMethod;
    }

    static Integer getLastSnapshotTS( Integer ts, Integer interval ) {
        return ( ( ts / interval ) * interval );
    }

    public Snapshot getSnapshot( Integer timestamp ) throws InvocationTargetException, IllegalAccessException {

        Integer target = this.getLastSnapshotTS( timestamp, interval );

        if( cache.containsKey( target ) )
            return cache.get( target );

        else {
            final Snapshot[] snap = new Snapshot[1];

            for (Document doc : snapshots.find(lte("_id", target)).sort(descending("_id")).limit(1))
                snap[0] = (Snapshot)convertMethod.invoke( null, new Object[] { doc } );

            cache.put( target, snap[0] );
            return snap[0];
        }
    }

    public void evict( Integer key ) {
        cache.remove( key );
    }
}
