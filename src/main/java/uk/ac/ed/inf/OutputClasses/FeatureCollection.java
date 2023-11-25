package uk.ac.ed.inf.OutputClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * A GEO JSON Feature Collection. For formatting the GEO JSON.
 */
public class FeatureCollection {
    @JsonProperty
    private String type;

    @JsonProperty
    private ArrayList<Feature> features;

    /**
     * Creates a new instance of Feature Collection.
     */
    public FeatureCollection() {
        this.type = "FeatureCollection";
        this.features = new ArrayList<>();

    }

    /**
     * Adds in a feature, wil always be one feature.
     *
     * @param feature The feature being added.
     */
    public void addFeature(Feature feature) {
        this.features.add(feature);
    }

}