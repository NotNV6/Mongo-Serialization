package wtf.retarders.mongo.serialization.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SerializedObject<T> {

    private String string;
    private T object;

}