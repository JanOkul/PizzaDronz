package uk.ac.ed.inf.OutputClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Properties {
    @JsonProperty
    private int size;

    public Properties() {
        this.size = 0;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
