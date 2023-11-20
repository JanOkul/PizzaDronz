package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.ac.ed.inf.ilp.data.LngLat;

public class FlightPath {
    private final String orderNo;
    private final double fromLongitude;
    private final double fromLatitude;
    private final double angle;
    private final double toLongitude;
    private final double toLatitude;
    private final int ticksSinceStartOfCalculation;

    public FlightPath(String orderNo, LngLat from, double angle, LngLat to, int ticksSinceStartOfCalculation) {
        this.orderNo = orderNo;
        this.fromLongitude = from.lng();
        this.fromLatitude = from.lat();
        this.angle = angle;
        this.toLongitude = to.lng();
        this.toLatitude = to.lat();
        this.ticksSinceStartOfCalculation = ticksSinceStartOfCalculation;
    }

    @JsonProperty
    public String getOrderNo() {
        return orderNo;
    }

    @JsonProperty
    public double getFromLongitude() {
        return fromLongitude;
    }

    @JsonProperty
    public double getFromLatitude() {
        return fromLatitude;
    }

    @JsonProperty
    public double getAngle() {
        return angle;
    }

    @JsonProperty
    public double getToLongitude() {
        return toLongitude;
    }

    @JsonProperty
    public double getToLatitude() {
        return toLatitude;
    }

    @JsonProperty
    public int getTicksSinceStartOfCalculation() {
        return ticksSinceStartOfCalculation;
    }
}
