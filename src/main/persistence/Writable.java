package persistence;

import org.json.JSONObject;

/*
Represents an object whose data can be written to a JSON file
 */

public interface Writable {

    // EFFECTS: returns this as JSON object
    // based on the method of the same name from JsonSerializationDemo
    JSONObject toJson();
}
