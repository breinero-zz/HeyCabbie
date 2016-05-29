package com.bryanreinero.bitcoin;

/**
 * Created by brein on 5/28/2016.
 */
public class TransactionOutput {

    private final String hash;
    private final String address;
    private final Double value;
    private final String script;


    public TransactionOutput(String hash, String address, Double value, String script, Integer index) {
        this.hash = hash;
        this.address = address;
        this.value = value;
        this.script = script;
    }

    public String getHash() {
        return hash;
    }

    public String getAddress() { return address; }

    public Double getValue() {
        return value;
    }

    public String getScript() {
        return script;
    }
}
