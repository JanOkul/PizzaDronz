package uk.ac.ed.inf.OutputClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * A GeoJSON Geometry class. For formatting the GEO JSON.
 */
public class Geometry {

    @JsonProperty
    private final String type;
    @JsonProperty
    private ArrayList<ArrayList<Double>> coordinates;

    /**
     * Creates a new Geometry instance that will always be a linestring
     */
    public Geometry() {

        this.type = "LineString";
        this.coordinates = new ArrayList<>();
    }

    /**
     * Adds the coordinates of the flight path to the geometry
     *
     * @param coordinates The drones coordinate as a list of lists, size n*2.
     */
    public void addCoordinates(ArrayList<Double> coordinates) {
        this.coordinates.add(coordinates);
    }

}
