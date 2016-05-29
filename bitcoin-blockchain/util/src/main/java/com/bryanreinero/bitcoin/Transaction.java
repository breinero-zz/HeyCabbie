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
    private final Integer block_height;
    private final Long tx_index;


    private Integer ver;
    private Integer size;
    private String relayed_by = null;

    public Transaction( String hash, Integer block_height, Long tx_index, Integer ver, Integer size) {
        this.hash = hash;
        this.block_height = block_height;
        this.tx_index = tx_index;
        this.ver = ver;
        this.size = size;
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

    public Integer getBlock_height() {
        return block_height;
    }

    public Long getTx_index() {
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
