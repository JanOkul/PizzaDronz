package uk.ac.ed.inf.Flight;

import junit.framework.TestCase;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;

import java.time.LocalDate;
import java.util.ArrayList;

public class FlightDataHandlerTest extends TestCase {
    public void testFlight() {
        FlightDataHandler flightDataHandler = new FlightDataHandler();
        Order order = new Order("0",
                LocalDate.now(),
                OrderStatus.VALID_BUT_NOT_DELIVERED,
                OrderValidationCode.NO_ERROR,
                100,
                new Pizza[]{new Pizza("a", 0)},
                null
        );

        ArrayList<Double> expectedPath = new ArrayList<>();
        expectedPath.add(0.0);
        expectedPath.add(0.0);
        expectedPath.add(0.0);
        expectedPath.add(0.0);
        expectedPath.add(0.0);
        expectedPath.add(999.0);
        expectedPath.add(180.0);
        expectedPath.add(180.0);
        expectedPath.add(180.0);
        expectedPath.add(180.0);
        expectedPath.add(180.0);
        expectedPath.add(999.0);

        Restaurant testRest = new Restaurant("test",
                new LngLat(SystemConstants.DRONE_MOVE_DISTANCE * 5, 0),
                null,
                new Pizza[]{new Pizza("a", 0)}
        );

        Restaurant[] testRestList = new Restaurant[]{testRest};

        ArrayList<Double> angles = flightDataHandler.calculateAngles(order,
                testRestList,
                new NamedRegion[0],
                new NamedRegion("test",
                        new LngLat[]{new LngLat(10,10)}),
                new LngLat(0,0))
                ;

        assertEquals(expectedPath, angles);
    }
    public void testImpossibleFlightPath() {
        FlightDataHandler flightDataHandler = new FlightDataHandler();
        Order order = new Order("0",
                LocalDate.now(),
                OrderStatus.VALID_BUT_NOT_DELIVERED,
                OrderValidationCode.NO_ERROR,
                100,
                new Pizza[]{new Pizza("a", 0)},
                null
        );

        Restaurant testRest = new Restaurant("test",
                new LngLat(SystemConstants.DRONE_MOVE_DISTANCE * 5, 0),
                null,
                new Pizza[]{new Pizza("a", 0)}
        );

        Restaurant[] testRestList = new Restaurant[]{testRest};

        double distance = SystemConstants.DRONE_MOVE_DISTANCE;
        NamedRegion[] noFlyZones = new NamedRegion[]{new NamedRegion("test",
                new LngLat[]{
                        new LngLat(distance*4, distance),
                        new LngLat(distance*6, distance),
                        new LngLat(distance*6, -distance),
                        new LngLat(distance*4, -distance)
                })};

        ArrayList<Double> angles = flightDataHandler.calculateAngles(order,
                testRestList,
                noFlyZones,
                new NamedRegion("test", new LngLat[]{new LngLat(10,10)}),
                new LngLat(0,0));

        assertEquals(new ArrayList<>(), angles);
    }
}