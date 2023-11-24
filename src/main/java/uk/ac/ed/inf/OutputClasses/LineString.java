package uk.ac.ed.inf.OutputClasses;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
public class LineString {

    @JsonProperty
    private final String type;
    @JsonProperty
    private ArrayList<ArrayList<Double>> coordinates;

    public LineString() {

        this.type = "LineString";
        this.coordinates = new ArrayList<>();
    }

    public void addCoordinates(ArrayList<Double> coordinates) {
        this.coordinates.add(coordinates);
    }

    public ArrayList<ArrayList<Double>> getCoordinates() {
        return this.coordinates;
    }
}
