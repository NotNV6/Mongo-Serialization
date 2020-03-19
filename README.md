# Mongo-Serialization
Simple serializer for MongoDB, can also be used for other saving types.

# Usage
Serializer example: 
```java
public class ExampleSerializer implements Serialization<Example> {
    
    @Override
    public SerializedObjectList toSerializedList(Example example) {
        return new SerializedObjectList()
            .addObject(new SerializedObject("uuid", example.getUuid()))
            .addObject(new SerializedObject("name", example.getName()));
    }
    
    @Override
    public Example fromSerializedList(SerializedObjectList list) { 
        UUID uuid = UUID.fromString((String) list.findObject("uuid"));
        String name = (String) list.findObject("name");        
    
        return new Example(uuid, name);
    }
    

    @Override
    public Class<Example> getType() { return Example.class; }
}
```

Register the serializer:
```java
MongoHandler.get().getSerializationHandler().registerSerializer(new ExampleSerializer());
```

Saving:
```java
MongoHandler.get().save(example, example.getUuid(), "examples");
```

Loading:
```java
Example example = MongoHandler.get().getObject(Example.class, "examples", uuid);
```