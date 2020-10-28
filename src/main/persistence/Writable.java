package persistence;

import org.json.JSONObject;

public interface Writable {

    // EFFECTS: returns this as JSON object
    // based on the method of the same name from JsonSerializationDemo
    JSONObject toJson();
}
