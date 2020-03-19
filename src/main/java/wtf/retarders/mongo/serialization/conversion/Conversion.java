package wtf.retarders.mongo.serialization.conversion;

public interface Conversion<T> {

    /**
     * converts an Object to a String
     *
     * @param object the object to be converted
     * @return the converted String
     */
    String toString(Object object);

    /**
     * converts a String to an Object
     *
     * @param string the string to be converted
     * @return the converted Object
     */
    T fromString(String string);

    /**
     * get the type of the serialization
     *
     * @return the type
     */
    Class<T> getType();

}