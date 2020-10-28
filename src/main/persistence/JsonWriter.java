package persistence;

import model.CourseList;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/*
Represents a writer that writes course list information to a file
 */

public class JsonWriter {
    private String destination;
    private PrintWriter writer;
    private static final int TAB = 4;

    // EFFECTS: constructs writer to write to destination file
    // based on the method of the same name from JsonSerializationDemo
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer
    // based on the method of the same name from JsonSerializationDemo
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    // EFFECTS: writes JSON representation of course list to file
    // based on the method of the same name from JsonSerializationDemo
    // TODO: modifies clause?
    public void writeCourseList(CourseList list) {
        JSONObject listAsJson = list.toJson();
        writer.print(listAsJson.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    // based on the method of the same name from JsonSerializationDemo
    public void close() {
        writer.close();
    }

}
