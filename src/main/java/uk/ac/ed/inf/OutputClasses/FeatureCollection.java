package uk.ac.ed.inf.OutputClasses;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class FeatureCollection {
    @JsonProperty
    private String type;

    @JsonProperty
    private ArrayList<Feature> features;

    public FeatureCollection() {
        this.type = "FeatureCollection";
        this.features = new ArrayList<>();

    }
    public void addFeature(Feature feature) {
        this.features.add(feature);
    }

}
