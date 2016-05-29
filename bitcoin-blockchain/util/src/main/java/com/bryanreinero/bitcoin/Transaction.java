package com.bryanreinero.bitcoin;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by brein on 5/27/2016.
 */
public class Transaction {

    private final Set<TransactionOutput> outputs = new HashSet<>();
    private final Set<TransactionInput> inputs = new HashSet<>();
    private final String hash;
    private final Integer tx_index;
    private final Integer time;

    private Integer ver;
    private Integer size;
    private String relayed_by = null;

    public Transaction( String hash, Integer tx_index, Integer time ) {
        this.hash = hash;
        this.tx_index = tx_index;
        this.time = time;
    }

    public Set<TransactionOutput> getOutputs() {
        return outputs;
    }

    public void addOutput( TransactionOutput o ) {
        outputs.add( o );
    }

    public Set<TransactionInput> getInputs() {
        return inputs;
    }

    public void addInput( TransactionInput i ) {
        inputs.add( i );
    }

    public String getHash() {
        return hash;
    }

    public Integer getTx_index() {
        return tx_index;
    }

    public Integer getVer() {
        return ver;
    }

    public void setVer(Integer ver) {
        this.ver = ver;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getRelayed_by() {
        return relayed_by;
    }

    public void setRelayed_by(String relayed_by) {
        this.relayed_by = relayed_by;
    }
}
