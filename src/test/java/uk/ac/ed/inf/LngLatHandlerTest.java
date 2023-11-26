package uk.ac.ed.inf;

import junit.framework.TestCase;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;



public class LngLatHandlerTest extends TestCase {

    // distanceTo tests
    public void testDistanceFromGSEdinburghToGSGlasgow() {
        LngLatHandler handler = new LngLatHandler();
        LngLat georgeSqEdinburgh = new LngLat( -3.188787017635369, 55.94364525819063);
        LngLat georgeSqGlasgow = new LngLat(-4.250172395304061, 55.861165602825174);
        double distance = 1.0645852776917943;
        assertEquals(distance, handler.distanceTo(georgeSqEdinburgh, georgeSqGlasgow));
    }

    public void testDistanceWithInvalidLngLat() {
        LngLatHandler handler = new LngLatHandler();
        LngLat invalidLngLat = new LngLat(200, 200);
        assertEquals(Double.NaN, handler.distanceTo(invalidLngLat, invalidLngLat));
    }

    // isCloseTo tests
    public void testIsCloseTo() {
        LngLatHandler handler = new LngLatHandler();
        LngLat appletonTower = new LngLat(-3.186874, 55.944494);
        LngLat closePoint = new LngLat(-3.186768,55.944600);
        assertTrue(handler.isCloseTo(appletonTower, closePoint));
    }

    public void testIsPointCloseWhenDistanceIsTolerance() {
        LngLatHandler handler = new LngLatHandler();
        LngLat appletonTower = new LngLat(-3.186874, 55.944494);
        LngLat closePoint = new LngLat(-3.186724,55.944644);
        assertFalse(handler.isCloseTo(appletonTower, closePoint));
    }

    // isInRegion tests
    public void testIsInRegion() {
        LngLatHandler handler = new LngLatHandler();
        LngLat point = new LngLat(0, 0);
        NamedRegion region = new NamedRegion("test", new LngLat[]{new LngLat(-1, -1), new LngLat(1, -1), new LngLat(1, 1), new LngLat(-1, 1)});
        assertTrue(handler.isInRegion(point, region));
    }

    public void testNotInRegion() {
        LngLatHandler handler = new LngLatHandler();
        LngLat point = new LngLat(2, 2);
        NamedRegion region = new NamedRegion("test", new LngLat[]{new LngLat(-1, -1), new LngLat(1, -1), new LngLat(1, 1), new LngLat(-1, 1)});
        assertFalse(handler.isInRegion(point, region));
    }

    public void testInRegionPointOnEdge() {
        LngLatHandler handler = new LngLatHandler();
        LngLat point = new LngLat(-1, 0);
        NamedRegion region = new NamedRegion("test", new LngLat[]{new LngLat(-1, -1), new LngLat(1, -1), new LngLat(1, 1), new LngLat(-1, 1)});
        assertTrue(handler.isInRegion(point, region));
    }

    public void testIsInRegionPointInCorner() {
        LngLatHandler handler = new LngLatHandler();
        LngLat point = new LngLat(-1, 1);
        NamedRegion region = new NamedRegion("test", new LngLat[]{new LngLat(-1, -1), new LngLat(1, -1), new LngLat(1, 1), new LngLat(-1, 1)});
        assertTrue(handler.isInRegion(point, region));
    }

    // nextPosition tests
    public void testFindingNextPosition() {
        LngLatHandler handler = new LngLatHandler();
        LngLat startPosition = new LngLat(-3.186874, 55.944494);
        double moveEast = 0;
        LngLat expectedPosition = new LngLat(startPosition.lng() + SystemConstants.DRONE_MOVE_DISTANCE, startPosition.lat());
        assertEquals(expectedPosition, handler.nextPosition(startPosition, moveEast));
    }

    public void testFindingNextPositionWithInvalidLngLat() {
        LngLatHandler handler = new LngLatHandler();
        LngLat startPosition = new LngLat(200, 200);
        double moveEast = 0;
        try {
            handler.nextPosition(startPosition, moveEast);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("LngLatHandler - nextPosition: Invalid longitude and " +
                    "latitude values in: " + startPosition, e.getMessage());
        }
    }

}