package persistence;

import org.json.JSONObject;

public interface Writable {

    // EFFECTS: returns this as JSON object
    // TODO: give credit
    JSONObject toJson();
}
