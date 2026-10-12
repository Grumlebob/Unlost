package bfst22.model.pathfinding;

import bfst22.model.Utility;
import bfst22.model.osm.OSMNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is heavily inspired by the class DijkstraSP from
 * <a href="https://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *  <p>
 * The {@code EdgeWeightedDigraph} class represents an edge-weighted
 * digraph of vertices named 0 through <em>V</em> - 1, where each
 * directed edge is of type {@link Edge} and has a real-valued weight.
 * It supports the following four primary operations: add a directed edge
 * to the digraph and iterate over all of edges incident from a given vertex,
 * and adding a vertex with an osmNodes, and means to convert lat lon to ID's
 * Parallel edges and self-loops are permitted.
 * <p>
 * This implementation uses an <em>adjacency-lists representation</em>, which
 * is a vertex-indexed array of edges.
 * All instance methods take &Theta;(1) time. (Though, iterating over
 * the edges returned by {@link #adj(int)} takes time proportional
 * to the size of the array.
 * Constructing an empty edge-weighted digraph with <em>V</em> vertices
 * takes &Theta;(<em>V</em>) time; constructing an edge-weighted digraph
 * with <em>E</em> edges and <em>V</em> vertices takes
 * &Theta;(<em>E</em> + <em>V</em>) time.
 * <p>
 */
public class Digraph implements Serializable {
    public static long serialVersionUID = 12345432L;

    private int vertices;                // number of vertices in this digraph
    private int edges;                      // number of edges in this digraph
    public static long graphBuildTime;

    private final ArrayList<ArrayList<Edge>> adj; //Adjacency list for vertex v
    public HashMap<OSMNode, Integer> vertexToId;
    public HashMap<Integer, OSMNode> idToVertex;

    public Digraph() {
        vertices = 0;
        edges = 0;
        vertexToId = new HashMap<>();
        adj = new ArrayList<>();
        idToVertex = new HashMap<>();
    }

    /**
     * Creates a vertex if it doesn't exist already.
     *
     */
    public void addVertex(OSMNode node) {
        if (node == null) throw new NullPointerException("Cannot add Vertex: OSMNode is null!");
        if (vertexToId.containsKey(node)) return;
        vertexToId.put(node, vertices);
        adj.add(vertices, new ArrayList<>());
        idToVertex.put(vertices, node);
        vertices++;
    }

    /**
     * Adds edge between two vertices, with its distance,weight and other restrictions.
     */
    public void addEdge(OSMNode nodeFrom, OSMNode nodeTo, float maxSpeed, boolean carAllowed, boolean bicycleAllowed, boolean walkAllowed, String name) {
        if (nodeFrom == null) throw new NullPointerException("nodeFrom is null!");
        if (nodeTo == null) throw new NullPointerException("nodeTo is null!");
        OSMNode from = idToVertex.get(getVertexToId().get(nodeFrom));
        OSMNode to = idToVertex.get(getVertexToId().get(nodeTo));
        double distance = Utility.calculateDistanceDouble(from.lonX,from.latY,to.lonX,to.latY);
        double carWeight = ((distance / 1000) / maxSpeed) * 60;
        adj.get(vertexToId.get(nodeFrom)).add(new Edge(from, to, distance, carWeight, carAllowed, bicycleAllowed, walkAllowed, name));
        edges++;
    }

    /**
     * Returns the number of vertices in this edge-weighted digraph.
     *
     */
    public int getVertices() {
        return vertices;
    }


    /**
     * @param node Node with X, Y coords
     * @return the ID of the vertex in the Directed Graph
     */
    public int convertVertexToID(OSMNode node) {
        if (node == null) throw new NullPointerException("Vertex could not be converted to ID: Vertex is null!");
        if (vertexToId.containsKey(node))
            return vertexToId.get(node);
        return -1;
    }

    /**
     * Returns the number of edges in this edge-weighted digraph.
     *
     */
    public int getEdges() {
        return edges;
    }

    /**
     * @return Hashmap with key of Node and value of their index in the Graph
     */
    public HashMap<OSMNode, Integer> getVertexToId() {
        return vertexToId;
    }

    /**
     * Returns the directed edges incident from vertex {@code v}.
     *
     * @param v the vertex
     * @return the directed edges incident from vertex {@code v} as an Iterable
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<Edge> adj(int v) {
        return adj.get(v);
    }

}