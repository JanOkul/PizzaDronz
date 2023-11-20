package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.*;

import java.util.*;


public class FlightPathHandler {
    private final double[] DIRECTIONS;
    private final LngLatHandler LLHandle;


    public FlightPathHandler() {
        DIRECTIONS = new double[]{0.0, 22.5, 45.0, 67.5, 90.0, 112.5, 135.0, 157.5, 180.0, 202.5, 225.0, 247.5, 270.0, 292.5, 315.0, 337.5};
        LLHandle = new LngLatHandler();
    }

    /**
     * For a given order and a list of restaurants, generate a flight path,
     * which is an array of coordinated of type LngLat.
     *
     * @param order          The order to generate a flight path for.
     * @param restaurants    List of currently supported restaurants.
     * @param no_fly_zones   A list of zones the drone cannot fly in.
     * @param central_region The region of the UoE central area.
     */

    // todo : pass in time since start of calculation.
    public LngLat[] GenerateFlightPath(Order order, Restaurant[] restaurants, NamedRegion[] no_fly_zones, NamedRegion central_region) {
        LngLat starting_position = new LngLat((-3.186874), 55.944494);

        // Find restaurant position.
        Pizza target_pizza = order.getPizzasInOrder()[0];   // As all pizzas are from the same restaurant, check first.
        LngLat restaurant_location = null;

        // Finds the restaurant that the pizza is from.
        for (Restaurant restaurant : restaurants) {
            ArrayList<Pizza> menu = new ArrayList<>(Arrays.asList(restaurant.menu()));
            if (menu.contains(target_pizza)) {
                restaurant_location = restaurant.location();
                break;
            }
        }

        // Generate path to restaurant
        ArrayList<FlightPath> path = AStar(starting_position, restaurant_location, no_fly_zones);

        // todo : Modify code to fit json spec, correctly create a set of flight path classes.
        // As the return journey is the reverse of the journey to the restaurant, reverse the path.
//        ArrayList<LngLat> reversed_path = new ArrayList<LngLat>(path);
//        Collections.reverse(reversed_path);
//        path.add(new LngLat(999,999)); // Hover move to indicate arrival at restaurant.
//        path.addAll(reversed_path);
//        path.add(new LngLat(999,999)); // Hover move to indicate arrival to appleton.

        return path.toArray(new LngLat[0]);
    }

    /**
     * A slightly modified version of the A* algorithm, which does not use a 2d grid, but rather generates a set
     * of moves around the drone (neighbours) and calculates the best move and repeats. Additionally, there is no f score.
     *
     * @param start_position The starting position of the drone.
     * @param end_position   The end position of the drone.
     * @param no_fly_zones   A list of zones the drone cannot fly in.
     * @return A list of positions the drone must move to.
     */
    private ArrayList<FlightPath> AStar(LngLat start_position, LngLat end_position, NamedRegion[] no_fly_zones) {
        HashMap<LngLat, LngLat> came_from = new HashMap<>();    // Path of Drone.
        HashMap<LngLat, Double> g_score   = new HashMap<>();    // Cost of getting to a position.
        PriorityQueue<LngLat> open_set;                         // Positions in queue to be evaluated.
        LngLat current;                                         // Current position being evaluated.
        double tentative_g_score;                               // G score of current position.


        // Uses the euclidean distance as the priority in the priority queue.
        open_set = new PriorityQueue<>(Comparator.comparingDouble(pos -> heuristic(pos, end_position)));
        open_set.add(start_position);
        g_score.put(start_position, 0.0);

        // If there are elements to evaluate. If there are no elements then there is no path.
        while (!open_set.isEmpty()) {
            current = open_set.poll();

            // Exit condition.
            if (LLHandle.isCloseTo(current, end_position)) {
                return reconstructPath(came_from, current);
            }

            // In traditional A*, a grid is used, but as a drone has infinite distance, the "grid" is the directions it can move.
            for (LngLat neighbour: getNeighbours(current)) {
                // Skip past move if it is not legal. Stops program from looping and throwing a heap overflow.
                if (!legalMove(neighbour, no_fly_zones)) {
                    continue;
                }

                tentative_g_score = getGScore(current, g_score) + heuristic(current, neighbour);

                if (tentative_g_score < getGScore(neighbour, g_score)) {
                    came_from.put(neighbour, current);
                    g_score.put(neighbour, tentative_g_score);
                    if (!open_set.contains(neighbour)) {
                        open_set.add(neighbour);
                    }
                }
            }
        }
        System.err.println("No path found");
        return new ArrayList<>();
    }

    /**
     *  Returns the g score of a position, as this A* implementation has no 2d grid, it is impossible to store the
     *  g score in a 2d array, therefore an initialisation of g score for all members of the grid impossible, so
     *  this function replicates an infinite grid of g scores, yes I am very proud of this.
     * @param position position to get g score of.
     * @param score g score hashmap.
     * @return  G score of position, if position is not explored then return infinity.
     */
    private double getGScore(LngLat position, HashMap<LngLat, Double> score) {
        if (score.containsKey(position)) {
            return score.get(position);
        }
        return Double.POSITIVE_INFINITY;
    }


    /** PLEASE REMEMBER TO ACCOUNT FOR NOT BEING ABLE TO LEAVE THE CENTRAL AREA
     *  The euclidean distance of a potential move to the destination.
     * @param potential_next_position The potential next best move for the drone.
     * @param destination The restaurant location
     * @return The distance to the destination.
     */
    private double heuristic(LngLat potential_next_position, LngLat destination) {
        return LLHandle.distanceTo(potential_next_position, destination);
    }

    /**
     *  Returns a list of all the possible moves the drone can make.
     * @param position The current position of the drone.
     * @return A list of all the possible moves the drone can make.
     */
    private ArrayList<LngLat> getNeighbours(LngLat position) {
        ArrayList<LngLat> neighbours = new ArrayList<>();

        for (double angle: DIRECTIONS) {
            neighbours.add(LLHandle.nextPosition(position, angle));
        }
        return neighbours;
    }

    /**
     * Reconstructs the path from the start to the end.
     *
     * @param parent  A hashmap of the parent of each position.
     * @param current The current position.
     * @return The path from the start to the end.
     */
    private ArrayList<FlightPath> reconstructPath(Map<LngLat, LngLat> parent, LngLat current) {
        ArrayList<FlightPath> path = new ArrayList<>();
        while (parent.containsKey(current)) {
            path.add(null); // todo: add flight path
            current = parent.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Checks if a move is legal.
     * @param position The position to check.
     * @param no_fly_zones A list of no-fly zones.
     * @return True if the move is legal, false otherwise.
     */
    private boolean legalMove(LngLat position, NamedRegion[] no_fly_zones) {
        for (NamedRegion region: no_fly_zones) {
            if (LLHandle.isInRegion(position, region)) {
                return false;
            }
        }
        return true;
    }

}


