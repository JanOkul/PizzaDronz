package uk.ac.ed.inf.OutputClasses;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.ac.ed.inf.ilp.data.LngLat;

/**
 * A move to move represented as a class. For formatting JSON.
 */
public class FlightPath {
    private final String orderNo;
    private final double fromLongitude;
    private final double fromLatitude;
    private final double angle;
    private final double toLongitude;
    private final double toLatitude;

    /**
     * Creates a new instance of the FlightPath class, all information must be passed in now.
     *
     * @param orderNo The order number that the drone is delivering.
     * @param from    The position the drone came from.
     * @param angle   The angle the drone moves in from the 'from' position.
     * @param to      The position the drone ends up in after moving 'from' with 'angle'.
     */
    public FlightPath(String orderNo, LngLat from, double angle, LngLat to) {
        this.orderNo = orderNo;
        this.fromLongitude = from.lng();
        this.fromLatitude = from.lat();
        this.angle = angle;
        this.toLongitude = to.lng();
        this.toLatitude = to.lat();
    }

    /**
     * Returns orde
     */
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

}
