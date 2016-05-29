package com.bryanreinero.bitcoin.inflight;

import com.bryanreinero.bitcoin.Transaction;
import com.bryanreinero.lambda.ViewBuilder;
import org.bson.Document;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by brein on 5/28/2016.
 */
public class CoinInFlightAggegator implements ViewBuilder<Transaction, String> {

    private Map<String, Double> totals = new HashMap<>();

    @Override
    public void takeLog( Transaction transaction ) {

        transaction.getOutputs().forEach(
                output -> {
                    Double coin = output.getValue();

                    if( totals.containsKey( output.getAddress() ) )
                        coin += totals.get( output.getAddress() );

                    totals.put( output.getAddress(), coin );
                }
        );
    }

    @Override
    public String getView() {
        StringBuffer buf = new StringBuffer("{");
        buf.append( "\n\t\"encumberances\": [" );

        Iterator<Map.Entry<String, Double>> it = totals.entrySet().iterator();
        while( it.hasNext() ) {
            Map.Entry<String, Double> e = it.next();
            buf.append( "\n\t\t{ address: " + e.getKey() + ", amount: "+ e.getValue()+" }" );
            if( it.hasNext() ) buf.append(",");
        }
        buf.append("\n\t]\n}");
        return buf.toString();
    }

    private static Transaction convert( final Document doc ) {
        String hash = doc.getString( "hash" );
        Transaction t = null;
        return null;
    }
}
