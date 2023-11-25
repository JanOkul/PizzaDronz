package uk.ac.ed.inf.OutputClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A requirement for GEO JSON, as a property is required, name was chosen and set to null. For formatting the GEO JSON.
 */
public class Properties {
    @JsonProperty
    private String name;

    /**
     * Creates a new instance of Properties.
     */
    public Properties() {
        this.name = null;
    }

}