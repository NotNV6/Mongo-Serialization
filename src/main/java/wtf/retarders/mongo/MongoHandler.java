package wtf.retarders.mongo;


import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import wtf.retarders.mongo.serialization.Serialization;
import wtf.retarders.mongo.serialization.objects.SerializedObjectList;
import wtf.retarders.mongo.serialization.SerializationHandler;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
@Setter
public class MongoHandler {

    private static MongoHandler instance;

    private final String username, password, host, database;
    private final int port;
    private final MongoClient client;
    private final MongoDatabase mongoDatabase;
    private final SerializationHandler serializationHandler;

    /**
     * the main constructor
     *
     * @param username username of the user for the database
     * @param password password for the database
     * @param host     ip address of the database
     * @param database authentication database
     * @param port     port of the database
     */
    public MongoHandler(String username, String password, String host, String database, int port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.database = database;
        this.port = port;

        if (password == null || password.isEmpty()) {
            client = new MongoClient(host, port);
        } else {
            client = new MongoClient(new ServerAddress(host, port), Collections.singletonList(MongoCredential.createCredential(username, database, password.toCharArray())));
        }

        this.serializationHandler = new SerializationHandler();
        this.mongoDatabase = client.getDatabase(this.database);
    }

    /**
     * save an object with a registered serialization
     *
     * @param object         the object to be saved
     * @param uuid           the UUID of the object
     * @param collectionName the name of the collection
     * @param <T>            the type of the object
     */
    public <T> void save(T object, UUID uuid, String collectionName) {
        SerializationHandler serializationHandler = this.serializationHandler;
        Serialization<T> serializer = (Serialization<T>) serializationHandler.findSerialization(object.getClass());
        MongoCollection collection = this.getCollection(collectionName);

        collection.replaceOne(Filters.eq("uuid", uuid.toString()), serializer.toSerializedList(object).toDocument(), new ReplaceOptions().upsert(true));
    }


    /**
     * get serialized objects in a mongo collection
     *
     * @param collectionName the collection name
     * @return the serializedobject list list
     */
    public List<SerializedObjectList> getSerializedObjects(String collectionName) {
        MongoCollection collection = this.getCollection(collectionName);
        List<SerializedObjectList> serializedObjectList = new ArrayList<>();

        collection.find().forEach((Consumer<? extends Document>) document -> serializedObjectList.add(new SerializedObjectList(document)));

        return serializedObjectList;
    }

    /**
     * get objects from a collection
     *
     * @param classType      the type of the class of the contents
     * @param collectionName the collection name
     * @param <T>            the type of the object
     * @return the object
     */
    public <T> List<T> getObjects(Class<T> classType, String collectionName) {
        List<T> toReturn = new ArrayList<>();
        SerializationHandler serializationHandler = this.serializationHandler;
        Serialization<?> serializer = serializationHandler.findSerialization(classType);

        this.getSerializedObjects(collectionName).forEach(list -> toReturn.add((T) serializer.fromSerializedList(list)));

        return toReturn;
    }

    /**
     * Get an Object from the client
     *
     * @param classType      the type of the class
     * @param collectionName the name of the collection
     * @param uuid           the uuid of the document
     * @param <T>            the type of the object
     * @return the object
     */
    public <T> T getObject(Class<T> classType, String collectionName, UUID uuid) {
        return this.getObjects(classType, collectionName).stream()
                .filter(object -> {
                    try {
                        return object.getClass().getMethod("getUuid").invoke(object).equals(uuid);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                    return false;
                }).findAny().orElse(null);
    }

    /**
     * Get a SerializedObject from the client
     *
     * @param collectionName the name of the collection
     * @param uuid           the uuid of the document
     * @param <T>            the type of the object
     * @return the object
     */
    @SuppressWarnings("unchecked")
    public <T> T getSerializedObject(String collectionName, UUID uuid) {
        return (T) this.getSerializedObjects(collectionName).stream()
                .filter(object -> object.findObject("uuid").equals(uuid.toString()))
                .findAny().orElse(null);
    }

    /**
     * Get a MongoCollection from the MongoClient
     *
     * @param name the name of the collection
     * @return the MongoCollection
     * @see MongoClient
     */
    public MongoCollection<Document> getCollection(String name) {
        if (mongoDatabase.getCollection(name) == null)
            mongoDatabase.createCollection(name);

        return mongoDatabase.getCollection(name);
    }

    public static MongoHandler get() {
        return instance;
    }
}