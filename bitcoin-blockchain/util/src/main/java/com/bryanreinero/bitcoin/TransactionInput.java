package com.bryanreinero.bitcoin;

/**
 * Created by brein on 5/28/2016.
 */
public class TransactionInput {

    private final String address;
    private final Long value;
    private final String script;
    private final Integer index;


    public TransactionInput(String address, Long value, String script, Integer index) {
        this.address = address;
        this.value = value;
        this.script = script;
        this.index = index;
    }

    public String getAddress() { return address; }

    public Long getValue() {
        return value;
    }

    public String getScript() {
        return script;
    }

    public Integer getIndex() {
        return index;
    }
}
