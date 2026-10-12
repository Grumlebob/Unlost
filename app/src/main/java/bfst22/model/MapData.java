package bfst22.model;

import bfst22.controller.SplashScreenController;
import bfst22.model.address.AddressBook;
import bfst22.model.graphiccomponents.*;
import bfst22.model.kdtree.KdTree;
import bfst22.model.osm.OSMNode;
import bfst22.model.pathfinding.Digraph;
import bfst22.model.pathfinding.Dijkstra;
import bfst22.model.pathfinding.TransportType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static bfst22.model.pathfinding.Digraph.graphBuildTime;

public class MapData implements Serializable {
    public static final long serialVersionUID = 246345754L;
    //SINGLETON
    private static MapData instance;
    private List<DrawCoordinate> coastlines;
    private Map<DrawType, List<MultiPolygon>> multiPolygonMap;
    private Map<DrawType, List<Way>> wayMap;
    private List<CanvasText> canvasTexts;
    private AddressBook addressBook;

    private Digraph graph;
    private Dijkstra pathfinder;

    private KdTree<Way> kdTreeWays;
    private KdTree<MultiPolygon> kdTreeMultiPolygons;
    private KdTree<CanvasText> kdTreeCanvasTexts;
    private KdTree<Way> kdTreeTransportableRoads;

    private BoundingBox boundingBox;

    //BORDER COORDS
    private float minX, minY, maxX, maxY;

    public static MapData getInstance() {
        if (instance == null)
            instance = new MapData();
        return instance;
    }

    private MapData() {
//Constructor shouldn't be used.
    }

    public void init(
            Map<DrawType, List<MultiPolygon>> multiPolygonMap,
            Map<DrawType, List<Way>> wayMap,
            List<DrawCoordinate> coastlines,
            List<CanvasText> canvasTexts,
            KdTree<Way> kdTreeWays,
            KdTree<MultiPolygon> kdTreeMultiPolygons,
            KdTree<CanvasText> kdTreeCanvasTexts,
            KdTree<Way> kdTreeTransportableRoads,
            AddressBook addressBook,
            BoundingBox boundingBox,
            float minX, float minY, float maxX, float maxY
    ) {

        this.wayMap = wayMap;
        this.multiPolygonMap = multiPolygonMap;
        this.coastlines = coastlines;
        this.canvasTexts = canvasTexts;
        this.kdTreeWays = kdTreeWays;
        this.kdTreeMultiPolygons = kdTreeMultiPolygons;
        this.kdTreeCanvasTexts = kdTreeCanvasTexts;
        this.kdTreeTransportableRoads = kdTreeTransportableRoads;
        this.addressBook = addressBook;
        this.boundingBox = boundingBox;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.graph = new Digraph();
        makeDigraph();
        graph.getEdges();
    }

    public void makeDigraph() {
        var time = -System.nanoTime();
        TransportType car = TransportType.CAR;
        TransportType bicycle = TransportType.BICYCLE;
        TransportType walk = TransportType.WALK;
        OSMNode previous, current = null;
        for (var type : DrawType.values()) {
            if (!car.transportAllowed(type)
                    && !bicycle.transportAllowed(type)
                    && !walk.transportAllowed(type)) continue;
            //First EVERY vertex is built
            for (Way way : wayIterable(type)) {
                for (var node : way.getNodes()) {
                    if (node != null) {
                        graph.addVertex(node);
                    }
                }
            }
            //Then Edges are made
            for (Way way : wayIterable(type)) {
                //Check correct way from the drawtype
                boolean carAllowed = car.transportAllowed(type);
                boolean bicycleAllowed = bicycle.transportAllowed(type);
                boolean walkAllowed = walk.transportAllowed(type);
                //Set special restrictions on the way
                boolean bicycleSpecialRestriction = way.bicycleAllowed;
                boolean walkRestrictionWalkAllowed = way.walkAllowed;
                //Is OneWay
                boolean onewayCar = way.onewayCar;
                boolean onewayBicycle = way.onewayBicycle;

                boolean totalCarAllowed = carAllowed && !onewayCar;

                boolean totalBicycleAllowed = bicycleAllowed && bicycleSpecialRestriction && !onewayBicycle;

                boolean totalWalkAllowed = walkAllowed && walkRestrictionWalkAllowed;

                previous = null;
                current = null;
                for (int i = 0; i < way.getNodes().size(); i++) {
                    previous = current;
                    current = way.getNodes().get(i);
                    if (previous == null || !way.access) continue;
                    //Edge from ->to
                    graph.addEdge(previous, current, way.maxSpeed, carAllowed, bicycleAllowed, walkAllowed, way.getWayName());
                    //Edge to -> from
                    if (!totalCarAllowed && !totalBicycleAllowed && !totalWalkAllowed) continue;
                    graph.addEdge(current, previous, way.maxSpeed, totalCarAllowed, totalBicycleAllowed, totalWalkAllowed, way.getWayName());
                }
            }
        }
        time += System.nanoTime();
        graphBuildTime = (long) (time / 1e6);
        pathfinder = new Dijkstra(graph);
    }

    public void makeDijkstraFromAddress() {
        pathfinder.runDijkstraAddress();
    }

    public void makeDijkstraFromClick() {
        pathfinder.runDijkstraMouse();
    }

    public void makeDijkstraFromTesting(int transportmode, int pathfindingMode) {
        pathfinder.testingPathfinding(transportmode, pathfindingMode);
    }

    public Iterable<MultiPolygon> multiPolygonIterable(DrawType type) {
        return multiPolygonMap.get(type);
    }

    public Iterable<Way> wayIterable(DrawType type) {
        return wayMap.get(type);
    }

    public Map<DrawType, List<Way>> getWayMap() {
        return wayMap;
    }

    public void setWayList(Map<DrawType, List<Way>> wayMap) {
        this.wayMap = wayMap;
    }

    public List<DrawCoordinate> getCoastlines() {
        return coastlines;
    }

    public AddressBook getAddressBook() {
        return addressBook;
    }

    public Digraph getGraph() {
        return graph;
    }

    public void setGraph(Digraph graph) {
        this.graph = graph;
    }

    public Dijkstra getPathfinder() {
        return pathfinder;
    }

    public void setPathfinder(Dijkstra pathfinder) {
        this.pathfinder = pathfinder;
    }

    public List<CanvasText> getCanvasTexts() {
        return canvasTexts;
    }


    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public KdTree getKdTreeCanvasTexts() {
        return kdTreeCanvasTexts;
    }


    public KdTree getKdTreeWays() {
        if (!SplashScreenController.createKDTreeOnlyForWays) {
            return kdTreeWays;
        }
        return kdTreeTransportableRoads;
    }


    public KdTree getKdTreeMultiPolygons() {
        return kdTreeMultiPolygons;
    }

    public KdTree getKdTreeTransportableRoads() {
        return kdTreeTransportableRoads;
    }

    public Map<DrawType, List<MultiPolygon>> getMultiPolygonMap() {
        return multiPolygonMap;
    }


    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMaxY() {
        return maxY;
    }
}
