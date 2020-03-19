package wtf.retarders.mongo.serialization.conversion.impl;

import wtf.retarders.mongo.serialization.conversion.Conversion;

import java.util.UUID;

public class UUIDConversion implements Conversion<UUID> {

    @Override
    public String toString(Object object) {
        return object.toString();
    }

    @Override
    public UUID fromString(String string) {
        return UUID.fromString(string);
    }

    @Override
    public Class<UUID> getType() {
        return UUID.class;
    }

}