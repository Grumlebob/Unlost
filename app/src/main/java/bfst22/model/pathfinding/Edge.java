package bfst22.model.pathfinding;

import bfst22.model.osm.OSMNode;
import bfst22.controller.AddressMenuController;
import javafx.scene.paint.Color;

import java.io.Serializable;

import static bfst22.model.pathfinding.TransportType.*;

/**
 * This class is heavily inspired by the class Edge Weighted Directed Graph from
 * see <a href="https://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * <p>
 * The {@code Edge} class represents a weighted edge in an {@link Digraph}.
 * Each edge consists of two OSMNodes,from and to and a real-value weight, and its restrictions.
 * <p>
 */

public class Edge implements Serializable {
    public static final long serialVersionUID = 34566234L;
    private final OSMNode from;
    private final OSMNode to;
    public final boolean carAllowed;
    public final boolean bicycleAllowed;
    public final boolean walkAllowed;
    private final double distance; //Meters to travel edge
    private final double carWeight; //Minutes to travel edge
    public String name;
    public Color colorOnCanvas;

    /**
     * Initializes a directed edge from vertex {@code v} to vertex {@code w} with
     * the given distance, weights and restrictions.
     */
    public Edge(OSMNode from, OSMNode to, double distance, double carWeight, boolean carAllowed, boolean bicycleAllowed, boolean walkAllowed, String name) {
        if (Double.isNaN(carWeight)) throw new IllegalArgumentException("Weight is NaN");
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.carWeight = carWeight;
        this.carAllowed = carAllowed;
        this.bicycleAllowed = bicycleAllowed;
        this.walkAllowed = walkAllowed;
        this.name = name;
    }

    /**
     * Returns the tail vertex of the directed edge.
     *
     */
    public OSMNode getFrom() {
        return from;
    }

    /**
     * Returns the head vertex of the directed edge.
     */
    public OSMNode getTo() {
        return to;
    }

    /**
     * Returns the weight of the directed edge.
     */
    public double getWeight() {
        if (!AddressMenuController.isFastestPath) {
            return distance;
        }
        if (AddressMenuController.transportTypeChosen == CAR) {
            return carWeight;
        }
        //If user overrides default settings, with dev tools.
        //Bike
        if (AddressMenuController.transportTypeChosen == BICYCLE) {
            return ((distance / 1000) / 15) * 60;
        }
        //Walk
        else return ((distance / 1000) / 5) * 60;
    }


    /**
     * For every 500 meter of the path, change color.
     * To give visual display of distance
     */
    public void calculateColorOnCanvas(float distance) {
        int draw = (int) (distance/500);
        int green= draw%3==1 ? 110 : 175;
        int blue= draw%3==2 ? 160 : 90;
        colorOnCanvas = Color.rgb(255,green,blue);
    }

    /**
     * @return the distance of the edge between from and to, vertices.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Returns a string representation of the directed edge.
     *
     */
    public String toString() {
        return from + "->" + to + " " + String.format("%5.2f", carWeight);
    }
}
