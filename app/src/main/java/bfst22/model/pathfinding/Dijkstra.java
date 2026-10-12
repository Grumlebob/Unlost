package bfst22.model.pathfinding;

import bfst22.controller.Controller;
import bfst22.controller.ControllerMediator;
import bfst22.model.MapData;
import bfst22.model.Utility;
import bfst22.model.graphiccomponents.Way;
import bfst22.model.osm.OSMNode;
import bfst22.controller.AddressMenuController;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import static bfst22.controller.Controller.*;
import static bfst22.model.pathfinding.TransportType.*;
import static bfst22.controller.AddressMenuController.*;
import static javafx.scene.paint.Color.*;

/**
 * This class is heavily inspired by the class DijkstraSP from
 * <a href="https://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * <p>
 * The {@code Dijkstra} class represents a data type for solving the
 * single-source shortest paths problem in edge-weighted digraphs
 * where the edge weights are non-negative.
 * <p>
 * This implementation uses <em>Dijkstra's algorithm</em> with a
 * <em>binary heap</em>. The runtime takes
 * &Theta;(<em>E</em> log <em>V</em>) time in the worst case,
 * where <em>V</em> is the number of vertices and <em>E</em> is
 * the number of edges. Each instance method takes &Theta;(1) time.
 * It uses &Theta;(<em>V</em>) extra space (not including the
 * edge-weighted digraph).
 * <p>
 * This correctly computes shortest paths if all arithmetic performed is
 * without floating-point rounding error or arithmetic overflow.
 * This is the case if none of the intermediate results exceeds 2<sup>52</sup>.
 * Since all intermediate results are sums of edge weights, they are bounded
 * by <em>V C</em>, where <em>V</em> is the number of vertices and <em>C</em>
 * is the maximum weight of any edge.
 * <p>
 */
public class Dijkstra implements Serializable {
    public static final long serialVersionUID = 8978927685L;
    private double[] distTo;          // distTo[v] = distance  of shortest s->v path
    private Edge[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
    private IndexMinPQ<Double> pq;    // priority queue of vertices
    private final Digraph graph;
    public ControllerMediator controllerMediator = ControllerMediator.getInstance();
    MapData mapData = MapData.getInstance();
    public static ArrayList<Edge> shortestPath;
    public static ArrayList<Edge> edgesExplored;
    public static ArrayList<Edge> wayChangesInRoute;
    public static ArrayList<String> routeInText;
    public static String timeToArrive = "";
    public static String distanceToArrive = "";
    public static String routePlanForClick = "";
    public boolean freePathfindingPossible;
    public int nodeStart;
    public int nodeEnd;
    public long time;
    public static boolean isClickMode = false;
    public static boolean hasValidPath = false;
    public boolean isTesting = false;


    public Dijkstra(Digraph graph) {
        this.graph = graph;
//        controllerMediator = ControllerMediator.getInstance();
    }

    /**
     * Starts pathfinding from address searches.
     */
    public void runDijkstraAddress() {
        if (addressFromX == 0 || addressToX == 0) return;
        time = -System.nanoTime();
        isClickMode = false;
        Way fromWay = (Way) mapData.getKdTreeWays().nearestNeighbour(AddressMenuController.addressFromX, AddressMenuController.addressFromY);
        Way toWay = (Way) mapData.getKdTreeWays().nearestNeighbour(AddressMenuController.addressToX, AddressMenuController.addressToY);
        freePathfindingPossible = nodeStart == graph.vertexToId.get(fromWay.getNodes().get(fromWay.getNodes().size() / 2));
        nodeStart = graph.vertexToId.get(fromWay.getNodes().get(fromWay.getNodes().size() / 2));
        nodeEnd = graph.vertexToId.get(toWay.getNodes().get(toWay.getNodes().size() / 2));
        start();
    }

    /**
     * Starts pathfinding from Ctrl+click and Shift+click coordinates.
     */
    public void runDijkstraMouse() {
        if (toCoordsX == 0 || lastMouseX == 0) return;
        time = -System.nanoTime();
        isClickMode = true;
        Way fromWay = (Way) mapData.getKdTreeWays().nearestNeighbour(Controller.lastMouseX, Controller.lastMouseY);
        Way toWay = (Way) mapData.getKdTreeWays().nearestNeighbour(Controller.toCoordsX, Controller.toCoordsY);
        freePathfindingPossible = nodeStart == graph.vertexToId.get(fromWay.getNodes().get(fromWay.getNodes().size() / 2));
        nodeStart = graph.vertexToId.get(fromWay.getNodes().get(fromWay.getNodes().size() / 2));
        nodeEnd = graph.vertexToId.get(toWay.getNodes().get(toWay.getNodes().size() / 2));
        start();
        StringBuilder sb = new StringBuilder();
        for (String s : routeInText) {
            sb.append(s);
        }
        if (shortestPath != null && hasValidPath) sb.append("You have reached your destination");
        else sb.append("No path found, try a new location");
        routePlanForClick = (String.valueOf(sb));
    }

    /**
     * Starts pathfinding without javafx, to help with testing
     */
    public void testingPathfinding(int transportmode, int pathfindingMode) {
        time = -System.nanoTime();
        isClickMode = true;
        isTesting = true;
        if (transportmode == 1) transportTypeChosen = CAR;
        else if (transportmode == 2) transportTypeChosen = BICYCLE;
        else if (transportmode == 3) transportTypeChosen = WALK;
        if (pathfindingMode == 1) AddressMenuController.isAStar = false;
        else if (pathfindingMode == 2) AddressMenuController.isAStar = true;

        Way fromWay = (Way) mapData.getKdTreeWays().nearestNeighbour(Controller.lastMouseX, Controller.lastMouseY);
        Way toWay = (Way) mapData.getKdTreeWays().nearestNeighbour(Controller.toCoordsX, Controller.toCoordsY);
        freePathfindingPossible = nodeStart == graph.vertexToId.get(fromWay.getNodes().get(fromWay.getNodes().size() / 2));
        nodeStart = graph.vertexToId.get(fromWay.getNodes().get(fromWay.getNodes().size() / 2));
        nodeEnd = graph.vertexToId.get(toWay.getNodes().get(toWay.getNodes().size() / 2));
        start();
    }

    /**
     * Begins the algorithm, but first it checks if the path it searches for,
     * has already been found in previous calculation. This is unless
     * the edges have changed their weights.
     */
    public void start() {
        shortestPath = new ArrayList<>();
        routeInText = new ArrayList<>();
        wayChangesInRoute = new ArrayList<>();
        hasValidPath = false;
        if (hasPathTo(nodeEnd) && freePathfindingPossible && !blockFreeDijkstra) {
            if (!isTesting) controllerMediator.getDebugMenuController().setPathInSPTLabel("\u2713");
        } else {
            if (!isTesting) controllerMediator.getDebugMenuController().setPathInSPTLabel("\u2717");
            edgesExplored = new ArrayList<>();
            findPath(nodeStart, nodeEnd);
        }
        pathTo(graph.convertVertexToID(graph.idToVertex.get(nodeEnd)));
        time += System.nanoTime();
        if (!isTesting) {
            controllerMediator.getDebugMenuController().setMsDijkstra(" " + (long) (time / 1e6));
            controllerMediator.getDebugMenuController().setEdgesExploredLabel(" " + edgesExplored.size());
            int pct = (int) (100 * ((float) shortestPath.size() / (float) edgesExplored.size()));
            controllerMediator.getDebugMenuController().setEdgesInShortestPath(" " + shortestPath.size() + " of % " + pct);
        }
    }

    /**
     * Computes a shortest-paths tree from the source vertex {@code s} to every other
     * vertex in the edge-weighted digraph {@code G}, until it reaches
     * target vertex {@code t} and terminates.
     *
     * @param s the source vertex
     * @param t the target vertex
     */
    public void findPath(int s, int t) {
        if (s == -1) {
            System.out.println("the source vertex could not be found");
            return;
        }
        if (t == -1) {
            System.out.println("the target vertex could not be found");
            return;
        }
        shortestPath.clear();
        edgesExplored.clear();
        routeInText.clear();

        distTo = new double[graph.getVertices()];
        edgeTo = new Edge[graph.getVertices()];

        for (int v = 0; v < graph.getVertices(); v++)
            distTo[v] = Float.POSITIVE_INFINITY;
        distTo[s] = 0.0f;

        pq = new IndexMinPQ<>(graph.getVertices());
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            if (v == t)
                return; //If we reached target stop, otherwise it will explore everything.
            for (Edge e : graph.adj(v)) {
                if (transportTypeChosen == CAR)
                    if (e.carAllowed)
                        relax(e, t);
                if (transportTypeChosen == BICYCLE)
                    if (e.bicycleAllowed)
                        relax(e, t);
                if (transportTypeChosen == WALK)
                    if (e.walkAllowed)
                        relax(e, t);
            }
        }
    }


    /**
     * A* works by using an heuristic.
     * <p>
     * If fastest path then euclidean distance of bird flight divided by highest
     * maxspeed optainable by transport type.
     * <p>
     * If shortest path then euclidean distance of bird flight is used
     *
     * @param currentVertex vertex inspected in Relax()
     * @param targetVertex  goal vertex
     */
    double aStarHeuristic(OSMNode currentVertex, OSMNode targetVertex) {
        //Hvis vi kører dijkstra, så bare returner 0
        if (!AddressMenuController.isAStar) return 0.0f;
        if (!AddressMenuController.isFastestPath) {
            return Utility.calculateDistance(currentVertex.lonX, currentVertex.latY, targetVertex.lonX, targetVertex.latY);
        } else {
            if (transportTypeChosen == CAR) {
                return (Utility.calculateDistanceDouble(currentVertex.lonX, currentVertex.latY, targetVertex.lonX, targetVertex.latY) / 130f) * 60;
            } else if (transportTypeChosen == BICYCLE) {
                return (Utility.calculateDistanceDouble(currentVertex.lonX, currentVertex.latY, targetVertex.lonX, targetVertex.latY) / 15f) * 60;
            } else if (transportTypeChosen == WALK)
                return (Utility.calculateDistanceDouble(currentVertex.lonX, currentVertex.latY, targetVertex.lonX, targetVertex.latY) / 5f) * 60;
        }
        return 0.0f;
    }


    /**
     * Will update distTo and edgeTo if an edge has a better path to a vertex.
     * Will also the update priority queue
     *
     * @param e            edge to be relaxed
     * @param targetVertex target vertex ised for A*
     */
    private void relax(Edge e, int targetVertex) {
        edgesExplored.add(e);
        int currentVertex = graph.convertVertexToID(e.getFrom());
        int adjVertex = graph.convertVertexToID(e.getTo());
        if (currentVertex == -1 || adjVertex == -1) {
            System.out.println("no path exist between these points ...");
            return;
        }
        if (distTo[adjVertex] > distTo[currentVertex] + e.getWeight()) {
            distTo[adjVertex] = distTo[currentVertex] + e.getWeight();
            edgeTo[adjVertex] = e;
            double priority = distTo[adjVertex] + aStarHeuristic(graph.idToVertex.get(adjVertex), graph.idToVertex.get(targetVertex));
            if (pq.contains(adjVertex)) pq.decreaseKey(adjVertex, priority);
            else pq.insert(adjVertex, priority);
        }
    }


    /**
     * Draws the current SPT calculated by A* or Dijkstra
     */
    public void drawSPT(GraphicsContext gc) {
        if (edgeTo != null) {
            gc.setStroke(rgb(255, 100, 100));
            gc.setLineWidth(0.00007f);
            gc.beginPath();
            for (Edge e : edgeTo) {
                if (e == null) continue;
                gc.moveTo(e.getFrom().lonX, e.getFrom().latY);
                gc.lineTo(e.getTo().lonX, e.getTo().latY);
            }
            gc.stroke();
        }
    }


    /**
     * Returns true if there is a path from the source vertex {@code s} to vertex {@code v}.
     *
     * @param v the destination vertex
     * @return {@code true} if there is a path from the source vertex
     * {@code s} to vertex {@code v}; {@code false} otherwise
     */
    public boolean hasPathTo(int v) {
        if (distTo == null) return false;
        return distTo[v] < Float.POSITIVE_INFINITY;
    }

    /**
     * Updates a shortest path from the source vertex {@code s} to vertex {@code v}
     *
     * @param goalVertex the destination vertex
     */
    public void pathTo(int goalVertex) {
        hasValidPath = hasPathTo(goalVertex);
        for (Edge e = edgeTo[goalVertex]; e != null; e = edgeTo[graph.convertVertexToID(e.getFrom())]) {
            shortestPath.add(e);
        }
        Collections.reverse(shortestPath); //Because for loop started on goal vertex.
        updateRouteInText();
    }


    /**
     * Updates the textual display of the route.
     * Will look at headings, times, distance and names.
     */
    public void updateRouteInText() {
        if (distTo == null) return;
        routeInText.clear();
        if (wayChangesInRoute != null) wayChangesInRoute.clear();
        if (hasValidPath) {
            float wayDistance = 0.0f;  //Meters
            float wayTravel = 0.0f;
            float totalDistance = 0.0f; //Meters
            float totalTravelTime = 0.0f;
            String prevName = "Start";
            for (Edge e : shortestPath) {
                wayDistance += e.getDistance();
                totalDistance += e.getDistance();

                e.calculateColorOnCanvas(totalDistance);
                //If shortest path, we need to convert distance to minutes.
                if (!AddressMenuController.isFastestPath) {
                    int speed = 0;
                    if (AddressMenuController.transportTypeChosen == BICYCLE) speed = 15; //Km/h
                    else if (AddressMenuController.transportTypeChosen == WALK) speed = 5; //Km/h
                    wayTravel += ((e.getDistance() / 1000) / speed) * 60;
                } else {
                    wayTravel += e.getWeight();
                }
                //if same way, just add path duration and length, till we reach a new way.
                if (prevName.equals(e.name)) {
                    continue;
                }
                wayChangesInRoute.add(e);
                wayTravel += 1f / 60f;//1 extra sec to simulate slowing down and changing waysegment
                totalTravelTime += wayTravel;
                //totalDistance += wayDistance;
                String kmh = String.format("%.1f", (wayDistance / 1000) / wayTravel * 60);
                String paddedName = String.format("%-20s", prevName);
                String paddedDistance = String.format("%-5s", (int) wayDistance + "m");
                String paddedKmh = String.format("%-4s", kmh);
                routeInText.add(calcRotationAngleInDegrees(e.getFrom().lonX, -e.getFrom().latY, e.getTo().lonX, -e.getTo().latY) + " " + paddedName + " " + paddedDistance + " - avg: " + paddedKmh + "km/h" + "\n");
                wayDistance = 0.0f;
                wayTravel = 0.0f;
                prevName = e.name;
            }
            //KMH
            String kmh = String.format("%.2f", (totalDistance / 1000) / (totalTravelTime / 60)); //totaldistance er i meter 1700 er 1.7km og totaltraveltime er i minutter
            //DISTANCE LABEL
            distanceToArrive = String.valueOf(totalDistance);
            String distanceOutput = " ";
            if (totalDistance > 1000) {
                totalDistance /= 1000;
                distanceOutput += String.format("%.2f", totalDistance) + " km";
            } else {
                distanceOutput += (int) totalDistance + " m";
            }
            if (!isTesting) controllerMediator.getAddressMenuController().setDistanceLabel(distanceOutput);

            //TIME TO TRAVEL LABEL
            timeToArrive = String.valueOf(totalTravelTime);
            String timeOutput = " ";
            if (totalTravelTime >= 60) {
                int minutes = (int) (totalTravelTime % 60);
                int hours = (int) (totalTravelTime / 60);
                timeOutput += "h: " + hours + " m: " + minutes;
            } else {
                timeOutput += "" + (int) totalTravelTime + " min";
            }
            if (!isTesting)
                controllerMediator.getAddressMenuController().setTravelTimeLabel((timeOutput + "   Speed:  " + kmh + " km/h"));
        }
    }

    /**
     * Calculates the angle from last Node in end of first way to first node in second way in degrees.
     * The return should range from [0,360), rotating CLOCKWISE,
     * 0 and 360 degrees represents NORTH
     * 90 degrees represents EAST
     * 180 degrees represents SOUTH
     * 270 degrees represents WEST
     * <p>
     * Credits to: https://stackoverflow.com/questions/9970281/java-calculating-the-angle-between-two-points-in-degrees
     *
     * @param fromX,fromY Point we are rotating around.
     * @param toX,toY     Point we want to calcuate the angle to.
     * @return angle in degrees.  This is the angle from centerPt to targetPt.
     */
    public static char calcRotationAngleInDegrees(float fromX, float fromY, float toX, float toY) {
        // calculate the angle theta from the deltaY and deltaX values
        // (atan2 returns radians values from [-PI,PI])
        // 0 currently points EAST.
        // NOTE: By preserving Y and X param order to atan2,  we are expecting
        // a CLOCKWISE angle direction.
        double theta = Math.atan2(toY - fromY, toX - fromX);

        // rotate the theta angle clockwise by 90 degrees
        // (this makes 0 point NORTH)
        // NOTE: adding to an angle rotates it clockwise.
        // subtracting would rotate it counter-clockwise
        theta += Math.PI / 2.0;

        // convert from radians to degrees
        // this will give you an angle from [0->270],[-180,0]
        double angle = Math.toDegrees(theta);

        // convert to positive range [0-360)
        // since we want to prevent negative angles, adjust them now.
        // we can assume that atan2 will not return a negative value
        // greater than one partial rotation
        if (angle < 0) {
            angle += 360;
        }
        if (angle > 30 && angle < 150) return '\u21b1'; //Right
        else if (angle > 210 && angle <= 330) return '\u21b0'; //Left
        else return '\u2191'; //Straight ahead
    }
}
