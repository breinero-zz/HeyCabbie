package com.bryanreinero.taxi.webapp;

/**
 * Created by brein on 5/21/2016.
 */
public interface Converter <T, V> {
    V convert( T item );
}
