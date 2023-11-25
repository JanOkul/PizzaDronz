package uk.ac.ed.inf.OutputClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A GEO JSON Feature class. For formatting the GEO JSON.
 */
public class Feature {
    @JsonProperty
    private String type;

    @JsonProperty
    private Properties properties;
    @JsonProperty
    private Geometry geometry;

    /**
     * A GEO JSON Feature class.
     *
     * @param geometry The geometry that is within this feature, will always be a LineString.
     */
    public Feature(Geometry geometry) {
        this.properties = new Properties();
        setGeometry(geometry);
        type = "Feature";
    }

    /**
     * Sets the geometry of the feature.
     *
     * @param geometry The Geometry of the feature which
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
