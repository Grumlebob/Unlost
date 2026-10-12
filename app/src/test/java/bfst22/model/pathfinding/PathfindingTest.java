package bfst22.model.pathfinding;

import bfst22.controller.Controller;
import bfst22.controller.ControllerMediator;
import bfst22.model.MapData;
import bfst22.model.osm.MapDataParser;
import bfst22.model.osm.OSMNode;
import bfst22.view.MapCanvas;
import bfst22.controller.AddressMenuController;
import bfst22.controller.DebugMenuController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static bfst22.controller.Controller.*;
import static bfst22.model.pathfinding.Dijkstra.hasValidPath;
import static bfst22.model.pathfinding.Dijkstra.shortestPath;
import static bfst22.controller.AddressMenuController.*;
import static org.junit.jupiter.api.Assertions.*;

//Testing suite:
class PathfindingTest {
    public static MapData mapData;
    public static MapDataParser mapDataParser;
    public static Controller controller;
    public static MapCanvas mapcanvas;
    public static AddressMenuController addressMenuController;
    public static DebugMenuController debugMenuController;
    public static boolean setupHasRun = false;

    @BeforeEach
    void setup() throws XMLStreamException, IOException, ClassNotFoundException {
        // only run setup once.
        if (setupHasRun) return;

        mapDataParser = new MapDataParser("data/small.osm");
        mapcanvas = new MapCanvas();
        controller = new Controller();
        addressMenuController = new AddressMenuController();
        debugMenuController = new DebugMenuController();

        mapData = MapData.getInstance();

        ControllerMediator.getInstance().registerAddressController(addressMenuController);
        ControllerMediator.getInstance().registerController(debugMenuController);
        setupHasRun = true;
    }


    @Test
    void validPath() {
        //Mouse coords
        lastMouseX = 7.055863f;
        lastMouseY= -55.664932f;
        toCoordsX= 7.0425167f;
        toCoordsY= -55.67685f;
        //Address Coords
        addressFromX  = 7.055863f;
        addressFromY = -55.664932f;
        addressToX = 7.0425167f;
        addressToY = -55.67685f;

        //Run once to make sure testing mode is on.
        mapData.makeDijkstraFromTesting(1,1);
        assertTrue(mapData.getPathfinder().isTesting);

        //Called from click method
        mapData.makeDijkstraFromClick();
        assertTrue(hasValidPath);
        //Called from address method
        mapData.makeDijkstraFromAddress();
        assertTrue(hasValidPath);

        //-----Run with all combinations------
        //Transportmode = 1 = CAR
        //with Dijkstra
        AddressMenuController.isFastestPath = false;
        mapData.makeDijkstraFromTesting(1,1);
        assertTrue(hasValidPath);
        AddressMenuController.isFastestPath = true;
        mapData.makeDijkstraFromTesting(1,1);
        assertTrue(hasValidPath);
        //TestingMode = 1 = CAR
        //with A*
        AddressMenuController.isFastestPath = false;
        mapData.makeDijkstraFromTesting(1,2);
        assertTrue(hasValidPath);
        AddressMenuController.isFastestPath = true;
        mapData.makeDijkstraFromTesting(1,2);
        assertTrue(hasValidPath);
        //testingMode = 2 = BICYCLE;
        //with Dijkstra
        AddressMenuController.isFastestPath = true;
        mapData.makeDijkstraFromTesting(2,1);
        assertTrue(hasValidPath);
        AddressMenuController.isFastestPath = false;
        mapData.makeDijkstraFromTesting(2,1);
        assertTrue(hasValidPath);
        //testingMode = 2 = BICYCLE;
        //with A*
        AddressMenuController.isFastestPath = true;
        mapData.makeDijkstraFromTesting(2,2);
        assertTrue(hasValidPath);
        AddressMenuController.isFastestPath = false;
        mapData.makeDijkstraFromTesting(2,2);
        assertTrue(hasValidPath);
        //testingMode = 3 = WALK;
        //with Dijkstra
        AddressMenuController.isFastestPath = true;
        mapData.makeDijkstraFromTesting(3,1);
        assertTrue(hasValidPath);
        AddressMenuController.isFastestPath = false;
        mapData.makeDijkstraFromTesting(3,1);
        assertTrue(hasValidPath);
        //testingMode = 3 = WALK;
        //with A*
        AddressMenuController.isFastestPath = true;
        mapData.makeDijkstraFromTesting(3,2);
        assertTrue(hasValidPath);
        AddressMenuController.isFastestPath = false;
        mapData.makeDijkstraFromTesting(3,2);
        assertTrue(hasValidPath);

        //helper
        assertEquals(-1,mapData.getGraph().convertVertexToID(new OSMNode(5,-55)));
    }

    @Test
    public void notValidPath() {
        //Serviceroad connected to pedestrian road.
        //Therefore are coords with no car path, but will have walk/bike path.
        //To show that a possible non-valid route, can also be valid
        lastMouseX = 7.04064149362952f;
        lastMouseY= -55.66788630829346f;
        toCoordsX= 7.05695191772017f;
        toCoordsY= -55.67253986639209f;
        //Car
        AddressMenuController.isFastestPath = true;
        mapData.makeDijkstraFromTesting(1,1);
        assertFalse(hasValidPath);
        mapData.makeDijkstraFromTesting(1,2);
        assertFalse(hasValidPath);
        //Bike
        AddressMenuController.isFastestPath = false;
        mapData.makeDijkstraFromTesting(2,1);
        assertTrue(hasValidPath);
        mapData.makeDijkstraFromTesting(2,2);
        assertTrue(hasValidPath);
        //Walk
        AddressMenuController.isFastestPath = false;
        mapData.makeDijkstraFromTesting(3,1);
        assertTrue(hasValidPath);
        mapData.makeDijkstraFromTesting(3,2);
        assertTrue(hasValidPath);
    }

    @Test
    public void noNegativeEdgeWeights() {
        //Mouse coords
        lastMouseX = 7.055863f;
        lastMouseY= -55.664932f;
        toCoordsX= 7.0425167f;
        toCoordsY= -55.67685f;

        boolean onlyPositiveWeights = true;

        AddressMenuController.isFastestPath = true;
        mapData.makeDijkstraFromTesting(1,2);
        for(Edge s: shortestPath) {
            if (s.getWeight() >= 0) continue;
            else onlyPositiveWeights = false;
        }
        assertTrue(shortestPath.get(0).toString().length()>0);
        AddressMenuController.isFastestPath = false;
        mapData.makeDijkstraFromTesting(2,2);
        for(Edge s: shortestPath) {
            if (s.getWeight() >= 0) continue;
            else onlyPositiveWeights = false;
        }
        AddressMenuController.isFastestPath = false;
        mapData.makeDijkstraFromTesting(3,2);
        for(Edge s: shortestPath) {
            if (s.getWeight() >= 0) continue;
            else onlyPositiveWeights = false;
        }

        assertTrue(onlyPositiveWeights);
        assertTrue(mapData.getGraph().getEdges()>5000);
    }

    @Test
    public void routeNotTooLong() {
        lastMouseX = 7.055863f;
        lastMouseY= -55.664932f;
        toCoordsX= 7.0425167f;
        toCoordsY= -55.67685f;

        //DIJKSTRA: CAR - BIKE - WALK
        //CAR
        AddressMenuController.isFastestPath = true;
        mapData.makeDijkstraFromTesting(1,1);
        //Has been seen at 63
        assertTrue(shortestPath.size()<100);
        //BIKE
        AddressMenuController.isFastestPath = false;
        mapData.makeDijkstraFromTesting(2,1);
        //Has been seen at 63
        assertTrue(shortestPath.size()<100);
        //WALK
        AddressMenuController.isFastestPath = false;
        mapData.makeDijkstraFromTesting(3,1);
        //Has been seen at 63
        assertTrue(shortestPath.size()<100);

        //A*: CAR - BIKE - WALK
        //CAR
        AddressMenuController.isFastestPath = true;
        mapData.makeDijkstraFromTesting(1,2);
        assertTrue(shortestPath.size()<100);
        //BIKE
        AddressMenuController.isFastestPath = false;
        mapData.makeDijkstraFromTesting(2,2);
        assertTrue(shortestPath.size()<100);
        //WALK
        AddressMenuController.isFastestPath = false;
        mapData.makeDijkstraFromTesting(3,2);
        assertTrue(shortestPath.size()<100);
    }

    @Test
    public void directionTest() {
        //RIGHT
        assertEquals('\u21b1',Dijkstra.calcRotationAngleInDegrees(1,1,2,1));
        assertEquals('\u21b1',Dijkstra.calcRotationAngleInDegrees(1,1,2,2));
        //LEFT
        assertEquals('\u21b0',Dijkstra.calcRotationAngleInDegrees(2,2,1,2));
        assertEquals('\u21b0',Dijkstra.calcRotationAngleInDegrees(2,2,1,1));
        //STRAIGHT
        assertEquals('\u2191',Dijkstra.calcRotationAngleInDegrees(1,1,1,2));
        assertEquals('\u2191',Dijkstra.calcRotationAngleInDegrees(1,2,1,1));
        assertEquals('\u2191',Dijkstra.calcRotationAngleInDegrees(1,-2,2,-5));
    }

    @Test
    public void drawTests() {
        mapData.getPathfinder().drawSPT(mapcanvas.getGraphicsContext2D());
    }

    @AfterEach
    void end(){

    }
}