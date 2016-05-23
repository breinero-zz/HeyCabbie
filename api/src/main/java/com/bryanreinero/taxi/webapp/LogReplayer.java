package com.bryanreinero.taxi.webapp;

import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by brein on 5/18/2016.
 */
public class LogReplayer <T>{

    private final Set<ViewBuilder> builders = new HashSet<ViewBuilder>();
    private final MongoCollection<T> logs;

    public LogReplayer(MongoCollection<T> logs) {
        this.logs = logs;
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

        //System.out.println( "Generating start: "+startTime+" end: "+endTime );
        // logs.find( and( lte( "ts", endTime ), gt( "ts", startTime ) ) ).sort( ascending(
        //        //"ts" )).forEach( handleLog );

        logs.find(
                Filters.and( Filters.lte( "_id.ts", endTime ), Filters.gt( "_id.ts", startTime ) )
        ).forEach( handleLog );
    }
}
