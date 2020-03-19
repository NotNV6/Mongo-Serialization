package wtf.retarders.mongo.serialization;

import com.google.gson.JsonParser;
import lombok.Data;
import wtf.retarders.mongo.serialization.conversion.ConversionHandler;

import java.util.ArrayList;
import java.util.List;

@Data
public class SerializationHandler {

    private final ConversionHandler conversionHandler;

    private final JsonParser jsonParser = new JsonParser();
    private final List<Serialization<?>> serializationList = new ArrayList<>();

    public SerializationHandler() {
        this.conversionHandler = new ConversionHandler();
    }

    /**
     * Register a serializer
     *
     * @param serializer the serializer
     */
    public void registerSerializer(Serialization<?> serializer) {
        this.serializationList.add(serializer);
    }

    /**
     * Find a serialization by a class
     *
     * @param clazz the class of the serialization
     * @param <T>   the type of the serializer
     * @return the serializer
     */
    @SuppressWarnings("unchecked")
    public <T> Serialization<T> findSerialization(Class<T> clazz) {
        return (Serialization<T>) this.serializationList.stream()
                .filter(serialization -> serialization.getType().equals(clazz))
                .findFirst().orElse(null);
    }
}