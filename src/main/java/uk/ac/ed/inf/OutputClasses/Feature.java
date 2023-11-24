package uk.ac.ed.inf.OutputClasses;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Feature {
    @JsonProperty
    private String type;

    @JsonProperty
    private Properties properties;
    @JsonProperty
    private LineString geometry;

    public Feature(LineString geometry) {
        this.properties = new Properties();
        setGeometry(geometry);
        type = "Feature";
    }

    public void setGeometry(LineString geometry) {
        this.geometry = geometry;
        this.properties.setSize(geometry.getCoordinates().size());
    }
}
