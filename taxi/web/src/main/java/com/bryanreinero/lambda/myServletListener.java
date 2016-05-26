package com.bryanreinero.lambda;

import com.bryanreinero.firehose.metrics.SampleSet;
import com.bryanreinero.taxi.codec.TaxiLogCodec;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * Created by brein on 5/22/2016.
 */

/**
 * Application Lifecycle Listener implementation class myServletListener
 *
 */
public class myServletListener implements ServletContextListener {

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event) {

        ServletContext sc = event.getServletContext();

        CodecRegistry registry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs( new TaxiLogCodec() )
        );

        MongoClientOptions options = MongoClientOptions.builder().codecRegistry( registry ).build();
        String dbHost = sc.getInitParameter("mongoHost");
        String dbPort = sc.getInitParameter("mongoPort");
        sc.setAttribute(
                "mongoclient",
                new MongoClient( new ServerAddress( dbHost, Integer.parseInt(dbPort)), options )
        );

        SampleSet stats = new SampleSet();
        //stats.start();
        sc.setAttribute( "stats", stats );
    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }

}