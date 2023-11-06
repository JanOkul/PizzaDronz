package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.*;

import java.util.*;


public class FlightPath {
    private final double[] DIRECTIONS;
    private final int DIRECTIONS_SIZE;
    private final LngLatHandling LLHandle;


    public FlightPath() {
        DIRECTIONS = new double[]{0.0, 22.5, 45.0, 67.5, 90.0, 112.5, 135.0, 157.5, 180.0, 202.5, 225.0, 247.5, 270.0, 292.5, 315.0, 337.5};
        DIRECTIONS_SIZE = 16;
        LLHandle = new LngLatHandling();
    }

    /**
     *  For a given order and a list of restaurants, generate a flight path,
     *  which is an array of coordinated of type LngLat.
     *
     * @param order The order to generate a flight path for.
     * @param restaurants List of currently supported restaurants.
     * @param no_fly_zones A list of zones the drone cannot fly in.
     * @param central_region The region of the UoE central area.
     *
     */
    public LngLat[] GenerateFlightPath(Order order, Restaurant[] restaurants, NamedRegion[] no_fly_zones, NamedRegion central_region) {
        LngLat starting_position = new LngLat((-3.186874), 55.944494);

        // Find restaurant position.
        Pizza target_pizza = order.getPizzasInOrder()[0];
        LngLat restaurant_location = null;

        for (Restaurant restaurant : restaurants) {
            for (Pizza menu_item : restaurant.menu()) {
                if (Objects.equals(target_pizza.name(), menu_item.name())) {
                    restaurant_location = restaurant.location();
                    break;
                }
            }
            // This should not be possible with order validation class but still here for redundancy.
            if (restaurant_location == null) {
                System.err.println("No restaurant found");
                return new LngLat[0];
            }


        }
        return AStar(starting_position, restaurant_location, no_fly_zones);
    }

    private LngLat[] AStar(LngLat start_position, LngLat end_position, NamedRegion[] no_fly_zones) {

        LngLat current_position = start_position;
        ArrayList<LngLat> path = new ArrayList<>();
        ArrayList<LngLat> neighbours = getNeighbours(current_position);
        LngLat best_position = current_position;
        double best_heuristic = LLHandle.distanceTo(current_position, end_position);

        while (!LLHandle.isCloseTo(current_position, end_position)) {

            for (int i = 0; i < neighbours.size(); i++) {
                LngLat neighbour = neighbours.get(i);
                double neighbour_heuristic = heuristic(neighbour, end_position, no_fly_zones);

                if (neighbour_heuristic < best_heuristic) {
                    best_position = neighbour;
                    best_heuristic = neighbour_heuristic;

                    current_position = best_position;
                    // Add best move
                    path.add(best_position);

                    neighbours.clear();
                    neighbours.addAll(getNeighbours(current_position));
                }


            }
        }
        System.out.println(end_position);
        return path.toArray(new LngLat[0]);
    }

    /** PLEASE REMEMBER TO ACCOUNT FOR NOT BEING ABLE TO LEAVE THE CENTRAL AREA
     *  Essentially the LngLat distance to function, extended with a check to see if in a no-fly zone.
     * @param potential_next_position The potential next best move for the drone.
     * @param destination The restaurant location
     * @param regions A list of no-fly zones for the drone to avoid
     * @return The distance to the destination, or infinity if the drone is about to enter a no-fly zone.
     */
    private double heuristic(LngLat potential_next_position, LngLat destination, NamedRegion[] regions) {

        // If the drone is about to enter a no-fly zone the heuristic of this move should be infinity.
        for (NamedRegion region: regions) {
            if (LLHandle.isInRegion(potential_next_position, region)) {
                return Double.POSITIVE_INFINITY;
            }
        }
        return LLHandle.distanceTo(potential_next_position, destination);   // Returns distance to restaurant
    }

    /**
     *  Given a position, returns all the possible position the drone could end up after a move.
     * @param position position to get neighbours of.
     * @return One move neighbours of position.
     */
    private ArrayList<LngLat> getNeighbours(LngLat position) {
        ArrayList<LngLat> neighbours = new ArrayList<>();
        for (double direction: DIRECTIONS) {
            neighbours.add(LLHandle.nextPosition(position, direction));
        }
        return neighbours;
    }
}
