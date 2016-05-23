package com.bryanreinero.taxi;

/**
 * Created by brein on 5/15/2016.
 */
public class Haversine {

    public static final int radiusOfEarth = 6371000;

    public static Double getDistance( Double startLat, Double endLat, Double startLong, Double endLong ) {

        Double theta1 = Math.toRadians( startLat );
        Double theta2 = Math.toRadians( endLat );
        Double deltaPhi = Math.toRadians(endLat-startLat );
        Double deltaLambda = Math.toRadians(endLong-startLong);

        Double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) +
                Math.cos(theta1) * Math.cos(theta2) *
                        Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return radiusOfEarth * c;
    }
}
