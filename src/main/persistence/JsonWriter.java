package persistence;

import model.CourseList;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class JsonWriter {
    private String destination;
    private PrintWriter writer;
    private static final int TAB = 4;

    // EFFECTS: constructs writer to write to destination file
    // TODO: give credit
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer
    // TODO: give credit, add exceptions
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    // EFFECTS: writes JSON representation of course list to file
    // TODO: give credit, add exceptions
    // TODO: modifies clause?
    public void writeCourseList(CourseList list) {
        JSONObject listAsJson = list.toJson();
        writer.print(listAsJson.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    // TODO: give credit, add exceptions
    public void close() {
        writer.close();
    }

}
