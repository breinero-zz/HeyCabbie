package com.bryanreinero.taxi.codec;

import com.bryanreinero.taxi.TaxiLog;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * Created by brein on 5/15/2016.
 */
public class TaxiLogCodec implements Codec<TaxiLog> {

    @Override
    public TaxiLog decode(BsonReader reader, DecoderContext decoderContext) {


            TaxiLog log = null;
            reader.readStartDocument();
            reader.readStartDocument();
            Integer ts = reader.readInt32("ts");
            String taxi = reader.readString("taxi");
            reader.readEndDocument();
            Double lattitude = reader.readDouble("latitude");
            Double longitude = reader.readDouble("longitude");
            Integer fareInt = reader.readInt32("fare");
            //Integer timestamp = reader.readInt32("ts");
            reader.readEndDocument();
            ;

            Boolean fare = false;
            if (fareInt == 1)
                fare = true;

            return new TaxiLog(taxi, lattitude, longitude, fare, ts );

    }

    @Override
    public void encode(BsonWriter writer, TaxiLog value, EncoderContext encoderContext) {
        writer.writeString( value.getId() );
        writer.writeDouble( value.getLattitude() );
        writer.writeDouble( value.getLongitude() );
        writer.writeBoolean( value.getFare() );
        writer.writeInt32( value.getTimestamp() );
        writer.flush();
    }

    @Override
    public Class<TaxiLog> getEncoderClass() {
        return TaxiLog.class;
    }
}
