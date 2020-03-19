package wtf.retarders.mongo.serialization.conversion;

import wtf.retarders.mongo.serialization.conversion.impl.UUIDConversion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConversionHandler {

    private final List<Conversion<?>> converters = new ArrayList<>();

    public ConversionHandler() {
        this.converters.addAll(Collections.singletonList(
                new UUIDConversion()
        ));
    }

    /**
     * Register a converter
     *
     * @param converter the converter
     */
    public void registerConverter(Conversion<?> converter) {
        this.converters.add(converter);
    }

    /**
     * finds a converter
     *
     * @param clazz the type of the object to convert
     * @return the converter
     */
    public Conversion<?> findConverter(Class<?> clazz) {
        return this.converters.stream()
                .filter(converter -> converter.getType().equals(clazz))
                .findFirst().orElse(null);
    }
}
