package com.bryanreinero.bitcoin;

/**
 * Created by brein on 5/28/2016.
 */
public class TransactionInput {

    private final String hash;
    private final Double value;
    private final String script;
    private final Integer index;


    public TransactionInput(String hash, Double value, String script, Integer index) {
        this.hash = hash;
        this.value = value;
        this.script = script;
        this.index = index;
    }

    public String getHash() {
        return hash;
    }

    public Double getValue() {
        return value;
    }

    public String getScript() {
        return script;
    }

    public Integer getIndex() {
        return index;
    }
}
