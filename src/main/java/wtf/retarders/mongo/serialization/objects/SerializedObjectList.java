package wtf.retarders.mongo.serialization.objects;

import com.google.gson.JsonObject;
import lombok.Getter;
import wtf.retarders.mongo.MongoHandler;
import wtf.retarders.mongo.serialization.conversion.Conversion;
import wtf.retarders.mongo.serialization.SerializationHandler;
import wtf.retarders.mongo.serialization.conversion.ConversionHandler;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SerializedObjectList {

    private List<SerializedObject<?>> serializedObjectList;

    public SerializedObjectList() {
        this.serializedObjectList = new ArrayList<>();
    }

    public SerializedObjectList(String string) {
        this.serializedObjectList = new ArrayList<>();

        JsonObject object = MongoHandler.get().getSerializationHandler().getJsonParser().parse(string).getAsJsonObject();
        object.entrySet().forEach(entry -> this.addObject(entry.getKey(), entry.getValue()));
    }

    public SerializedObjectList(Document document) {
        this.serializedObjectList = new ArrayList<>();

        document.forEach(this::addObject);
    }

    /**
     * adds and converts an object
     *
     * @param path   the path for the object
     * @param object the object
     * @return the current instance of the SerializedObjectList
     * @see Conversion
     */
    public SerializedObjectList addObject(String path, Object object) {
        ConversionHandler conversionHandler = MongoHandler.get().getSerializationHandler().getConversionHandler();
        Conversion<?> conversion = null;

        if (object != null && object.getClass() != null && conversionHandler.findConverter(object.getClass()) != null) {
            conversion = conversionHandler.findConverter(object.getClass());
        }

        if (conversion != null) {
            this.serializedObjectList.add(new SerializedObject<>(path, conversion.toString(object)));
        } else {
            this.serializedObjectList.add(new SerializedObject<>(path, object));
        }

        return this;
    }

    /**
     * finds an object inside of a list
     *
     * @param path the path of the object
     * @return the object
     */
    public Object findObject(String path) {
        SerializedObject<?> serializedObject = this.serializedObjectList.stream()
                .filter(obj -> obj.getString().equals(path))
                .findFirst().orElse(null);

        Object object = null;

        if (serializedObject != null) {
            object = serializedObject.getObject();

            ConversionHandler conversionHandler = MongoHandler.get().getSerializationHandler().getConversionHandler();
            Conversion<?> conversion = conversionHandler.findConverter(object.getClass());

            if (conversion != null) {
                object = conversion.fromString(object.toString());
            }
        }

        return object;
    }

    /**
     * transfers an SerializedObjectList to a JsonObject
     *
     * @return the JsonObject
     */
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        this.serializedObjectList.forEach(object1 -> object.addProperty(object1.getString(), String.valueOf(object1.getObject())));
        return object;
    }

    /**
     * transfers a SerializedObjectList to a Document
     *
     * @return the Document
     */
    public Document toDocument() {
        Document document = new Document();
        this.serializedObjectList.forEach(object -> document.put(object.getString(), object.getObject()));

        return document;
    }
}