package com.bryanreinero.bitcoin;

/**
 * Created by brein on 5/28/2016.
 */
public class TransactionOutput {

    private final String address;
    private final Long value;
    private final String script;
    private final Integer index;


    public TransactionOutput( String address, Long value, String script, Integer index ) {
        this.address = address;
        this.value = value;
        this.script = script;
        this.index = index;
    }

    public String getAddress() { return address; }

    public Long getValue() {
        return value;
    }

    public Integer getIndex() {
        return index;
    }

    public String getScipt() { return script; }
}
