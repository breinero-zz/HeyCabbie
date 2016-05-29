package com.bryanreinero.bitcoin.inflight;

import com.bryanreinero.bitcoin.Transaction;
import com.bryanreinero.bitcoin.TransactionInput;
import com.bryanreinero.bitcoin.TransactionOutput;
import com.bryanreinero.lambda.ViewBuilder;
import org.bson.Document;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by brein on 5/29/2016.
 */
public class SpendInFlightAggregator implements ViewBuilder<Document, String> {

    private Map<String, Long> totals = new HashMap<>();

    @Override
    public void takeLog(  Document doc ) {

        Transaction transaction = convert( doc );

        transaction.getInputs().forEach(
                input -> {
                    Long coin = input.getValue();

                    if( totals.containsKey( input.getAddress() ) )
                        coin += totals.get( input.getAddress() );

                    totals.put( input.getAddress(), coin );
                }
        );
    }

    @Override
    public String getView() {
        StringBuffer buf = new StringBuffer("{");
        buf.append( "\n\t\"spending\": [" );

        Iterator<Map.Entry<String, Long>> it = totals.entrySet().iterator();
        while( it.hasNext() ) {
            Map.Entry<String, Long> e = it.next();
            buf.append( "\n\t\t{ address: " + e.getKey() + ", amount: "+ e.getValue()+" }" );
            if( it.hasNext() ) buf.append(",");
        }
        buf.append("\n\t]\n}");
        return buf.toString();
    }

    private static Transaction convert( final Document doc ) {
        String hash = doc.getString( "hash" );
        Integer tx_index = doc.getInteger( "tx_index");
        Integer time = doc.getInteger( "time" );

        Transaction t = new Transaction(
                hash,
                tx_index,
                time
        );

        ( (List<Document>) doc.get( "inputs" )).forEach(
                input -> {
                    t.addInput( convertInput( input ) );
                }
        );

        ( ( List<Document> ) doc.get( "out" )).forEach(
                output -> {
                    t.addOutput( convertOutput( output ) );
                }
        );

        return t;
    }

    private static TransactionInput convertInput(Document doc ) {

        Document i = (Document)doc.get("prev_out");
        String address = i.getString( "addr" );
        Long value;
        try {
            value = i.getLong("value");
        } catch( ClassCastException e ) {
            value = i.getInteger( "value" ).longValue();
        }
        String script = i.getString( "script" );
        Integer index = i.getInteger( "tx_index" );

        return new TransactionInput(
                address,
                value,
                script,
                index
        );
    }

    private static TransactionOutput convertOutput(Document out ) {

        String address = out.getString( "addr" );
        Long value;
        try {
            value = out.getLong("value");
        } catch( ClassCastException e ) {
            value = out.getInteger( "value" ).longValue();
        }
        String script = out.getString( "script" );
        Integer index = out.getInteger( "index" );

        return new TransactionOutput(
                address,
                value,
                script,
                index
        );
    }
}
