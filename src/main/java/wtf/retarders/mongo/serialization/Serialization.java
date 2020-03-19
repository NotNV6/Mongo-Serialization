package wtf.retarders.mongo.serialization;

import wtf.retarders.mongo.serialization.objects.SerializedObjectList;
import wtf.retarders.mongo.serialization.objects.SerializedObject;

public interface Serialization<T> {

    /**
     * makes the object a SerializedObjectList
     *
     * @param t the object
     * @return the SerializedObjectList
     * @see SerializedObject
     */
    SerializedObjectList toSerializedList(T t);

    /**
     * makes a SerializedObjectList an Object
     *
     * @param list the list to transfer
     * @return the Object
     * @see SerializedObject
     */
    T fromSerializedList(SerializedObjectList list);

    /**
     * get the type of the serialization
     *
     * @return the type
     */
    Class<T> getType();

}