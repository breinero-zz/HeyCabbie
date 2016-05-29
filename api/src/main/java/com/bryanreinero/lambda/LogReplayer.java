package com.bryanreinero.lambda;

import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by brein on 5/18/2016.
 */
public class LogReplayer <T>{

    private final Set<ViewBuilder> builders = new HashSet<ViewBuilder>();
    private final MongoCollection<T> logs;
    private Document query = null;

    public LogReplayer(MongoCollection<T> logs) {
        this.logs = logs;
    }
    public LogReplayer(MongoCollection<T> logs, Document query ) {
        this.logs = logs;
        this.query = query;
    }

    public void addBuilder( ViewBuilder b ) {
        builders.add( b );
    }

    private Block<T> handleLog = new Block<T>() {
        @Override
        public void apply(final T log ) {
            builders.forEach( builder -> builder.takeLog( log ) );
        }
    };

    public void replayLogs ( Integer startTime, Integer endTime   ) {

        if( query == null )
        logs.find(
                Filters.and( Filters.lte( "_id.ts", endTime ), Filters.gt( "_id.ts", startTime ) )
        ).forEach( handleLog );

        else {
            logs.find( query ).forEach( handleLog );
        }
    }

}
