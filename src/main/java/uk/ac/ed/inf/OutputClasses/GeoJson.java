package uk.ac.ed.inf.OutputClasses;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class GeoJson {

    public static class FeatureCollection {
        @JsonProperty
        private String type = "FeatureCollection";

        @JsonProperty
        private ArrayList<Feature> features;

        // Standard getters and setters
        // ...
    }

    public static class Feature {
        @JsonProperty
        private String type = "LineString";

        @JsonProperty
        private LineString coordinates;

        // Standard getters and setters
        // ...
    }

    public static class LineString {
        @JsonProperty("type")
        private String type = "LineString";

        @JsonProperty("coordinates")
        private List<List<Double>> coordinates; // List of longitude, latitude pairs

        // Standard getters and setters
        // ...
    }

}
