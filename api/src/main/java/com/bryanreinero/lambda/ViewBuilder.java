package com.bryanreinero.lambda;

/**
 * Created by brein on 5/17/2016.
 */
public interface ViewBuilder <T, V> {
    void takeLog( T log );
     V getView();
}
