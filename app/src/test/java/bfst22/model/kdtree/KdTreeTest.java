package bfst22.model.kdtree;

import bfst22.controller.SplashScreenController;
import bfst22.model.MapData;
import bfst22.model.address.AddressBook;
import bfst22.model.graphiccomponents.DrawCoordinate;
import bfst22.model.graphiccomponents.DrawType;
import bfst22.model.graphiccomponents.MultiPolygon;
import bfst22.model.graphiccomponents.Way;
import bfst22.model.graphiccomponents.CanvasText;
import bfst22.model.osm.MapDataParser;

import bfst22.controller.DebugMenuController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The nearest neighbour and range query test only work with the data set small.osm
 */
class KdTreeTest {

    public  static boolean setupIsDone = false;
    MapData mapData;
    public static MapDataParser mapDataParser;

    public static KdTree kdtreeCanvasTexts;
    public static KdTree kdtreeWays;
    public static KdTree kdtreeMultiPolygons;

    public static List<CanvasText> canvasTexts;
    public static AddressBook addressBook;
    public static Map<DrawType, List<Way>> wayMap;
    public static Map<DrawType, List<MultiPolygon>> multiPolygonMap;

    @BeforeEach
    void setUp() throws XMLStreamException, IOException, ClassNotFoundException, InterruptedException {
        if (setupIsDone){
            return;
        }
        SplashScreenController.createKDTreeOnlyForWays = false;
        mapDataParser = new MapDataParser("data/small.osm");

        mapData = MapData.getInstance();

        kdtreeCanvasTexts = mapData.getKdTreeCanvasTexts();
        kdtreeWays = mapData.getKdTreeWays();
        kdtreeMultiPolygons = mapData.getKdTreeMultiPolygons();

        canvasTexts = mapData.getCanvasTexts();
        addressBook = mapData.getAddressBook();
        wayMap = mapData.getWayMap();
        multiPolygonMap = mapData.getMultiPolygonMap();
        setupIsDone = true;
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * Test that the number of leaves in the tree equals the size of the input.
     */
    @Test
    void getNumOfLeaves() {
        int lengthofWayMap = 0;
        for(Map.Entry<DrawType, List<Way>> entry : wayMap.entrySet()){
            lengthofWayMap += entry.getValue().size();
        }
        double expectedlength = lengthofWayMap;
        int actualLength = kdtreeWays.getNumOfLeaves();
        assertEquals(expectedlength, actualLength);
    }

    /**
     * Uses data set small.osm for this test
     */
    @Test
    void nearestNeighbour() {
        float x = 7.05056905f;
        float y = -55.670658f;
        assertEquals("Dronningensgade", kdtreeWays.nearestNeighbour(x, y).getText());

        float x2 = 7.04863042f;
        float y2 = -55.666360f;
        assertEquals("Ørestads Boulevard", kdtreeWays.nearestNeighbour(x2, y2).getText());
    }

    /**
     * Uses data set small.osm for this test.
     */
    @Test
    void rangeQuery() {
        DebugMenuController.ignoreZoomslevels = true;
        float minX = 7.0338783f;
        float maxX = 7.065322f;
        float minY = -55.686596f;
        float maxY = -55.657665f;
        Map<DrawType, Set<DrawCoordinate>> rangequeryAllWays = kdtreeWays.rangeQuery(minX, maxY, maxX , minY);
        mapData.getBoundingBox().setCoords(maxY, maxX, minY, minX);
        for(Map.Entry<DrawType, List<Way>> entry : wayMap.entrySet()){
            for(Way way : entry.getValue()){
                if(mapData.getBoundingBox().isInsideBoundingWith1Point(way)) {
                    if (!rangequeryAllWays.get(entry.getKey()).contains(way)) {
                        fail();
                    }
                }
            }
        }

        float minX1 = 7.0474186f;
        float maxX1 = 7.0525093f;
        float minY1 = -55.67432f;
        float maxY1 = -55.669388f;
        Map<DrawType, Set<DrawCoordinate>> rangequerySomeWays = kdtreeWays.rangeQuery(minX1, maxY1, maxX1 , minY1);
        mapData.getBoundingBox().setCoords(maxY1, maxX1, minY1, minX1);
        for (Map.Entry<DrawType, List<Way>> entry : wayMap.entrySet()){
            for(Way way : entry.getValue()){
                if(mapData.getBoundingBox().isInsideBoundingWith1Point(way)){
                    if(!rangequerySomeWays.get(entry.getKey()).contains(way)){
                        fail();
                    }
                }
            }
        }

        mapData.getBoundingBox().setCoords(maxY, maxX, minY, minX);
        boolean pass = false;
        for (Map.Entry<DrawType, List<Way>> entry : wayMap.entrySet()){
            for(Way way : entry.getValue()){
                if(mapData.getBoundingBox().isInsideBoundingWith1Point(way)){
                    if(!rangequerySomeWays.get(entry.getKey()).contains(way)){
                        pass = true;
                    }
                }
            }
        }
        if(!pass) fail();

        mapData.getBoundingBox().setCoords(maxY1, maxX1, minY1, minX1);
        Map<DrawType, Set<DrawCoordinate>> rangequerySomeWays1 = kdtreeWays.rangeQuery(minX, maxY, maxX , minY);
        for (Map.Entry<DrawType, List<Way>> entry : wayMap.entrySet()){
            for(Way way : entry.getValue()){
                if(mapData.getBoundingBox().isInsideBoundingWith1Point(way)){
                    if(!rangequerySomeWays1.get(entry.getKey()).contains(way)){
                        fail();
                    }
                }
            }
        }


        Set<DrawCoordinate> rangequeryAllWaysAsSet = kdtreeWays.rangeQueryAsSet(minX, maxY, maxX , minY);
        mapData.getBoundingBox().setCoords(maxY, maxX, minY, minX);
        for(Map.Entry<DrawType, List<Way>> entry : wayMap.entrySet()){
            for(Way way : entry.getValue()){
                if(mapData.getBoundingBox().isInsideBoundingWith1Point(way)) {
                    if (!rangequeryAllWaysAsSet.contains(way)) {
                        fail();
                    }
                }
            }
        }

        Set<DrawCoordinate> rangequerySomeWaysAsSet = kdtreeWays.rangeQueryAsSet(minX1, maxY1, maxX1 , minY1);
        mapData.getBoundingBox().setCoords(maxY1, maxX1, minY1, minX1);
        for (Map.Entry<DrawType, List<Way>> entry : wayMap.entrySet()){
            for(Way way : entry.getValue()){
                if(mapData.getBoundingBox().isInsideBoundingWith1Point(way)){
                    if(!rangequerySomeWaysAsSet.contains(way)){
                        fail();
                    }
                }
            }
        }

        mapData.getBoundingBox().setCoords(maxY, maxX, minY, minX);
        pass = false;
        for (Map.Entry<DrawType, List<Way>> entry : wayMap.entrySet()){
            for(Way way : entry.getValue()){
                if(mapData.getBoundingBox().isInsideBoundingWith1Point(way)){
                    if(!rangequerySomeWaysAsSet.contains(way)){
                        pass = true;
                    }
                }
            }
        }
        if(!pass) fail();

        mapData.getBoundingBox().setCoords(maxY1, maxX1, minY1, minX1);
        Set<DrawCoordinate> rangequerySomeWaysAsSet1 = kdtreeWays.rangeQueryAsSet(minX, maxY, maxX , minY);
        for (Map.Entry<DrawType, List<Way>> entry : wayMap.entrySet()){
            for(Way way : entry.getValue()){
                if(mapData.getBoundingBox().isInsideBoundingWith1Point(way)){
                    if(!rangequerySomeWaysAsSet1.contains(way)){
                        fail();
                    }
                }
            }
        }
    }

    @Test
    void lineIntersectsRect() {
        //Line
        float lineFromX = -1f;
        float lineFromY = 4f;
        float lineToX = 2f;
        float lineToY = 0f;

        //Rectangle
        float minX = 1f;
        float minY = 1;
        float maxX = 4f;
        float maxY = 3f;
        boolean expected = true;
        assertEquals(expected, KdTree.lineIntersectsRect(lineFromX, lineFromY, lineToX, lineToY, minX, minY, maxX, maxY));
    }

    @Test
    void rectIntersectsRect() {
        float rectMinX = 5.0f;
        float rectMinY = -1.0f;
        float rectMaxX = 7.0f;
        float rectMaxY = -4.0f;

        float rectMin2X = 5.0f;
        float rectMin2Y = 1.0f;
        float rectMax2X = 7.0f;
        float rectMax2Y = 0.0f;

        boolean expected = false;
        boolean actual = KdTree.rectIntersectsRect(rectMinX, rectMinY, rectMaxX, rectMaxY, rectMin2X, rectMin2Y, rectMax2X, rectMax2Y);
        assertEquals(expected, actual);

        float rectMinX2 = 4.0f;
        float rectMinY2 = -5.0f;
        float rectMaxX2 = 8.0f;
        float rectMaxY2 = -7.0f;


        boolean expected2 = false;
        boolean actual2 = KdTree.rectIntersectsRect(rectMinX, rectMinY, rectMaxX, rectMaxY, rectMinX2, rectMinY2, rectMaxX2, rectMaxY2);
        assertEquals(expected2, actual2);

    }

    @Test
    void doIntersect() {
        //Line
        float lineFromX = 2.0f;
        float lineFromY = 4.0f;

        float lineToX = 1.5f;
        float lineToY = 2.0f;

        //Point
        float pointFromX = 2.5f;
        float pointFromY = 2.5f;

        float pointToX = 2.5f;
        float pointToY = 2.5f;
        boolean expected = false;
        assertEquals(expected, KdTree.doIntersect(lineFromX, lineFromY,lineToX, lineToY, pointFromX, pointFromY, pointToX, pointToY));

        //Line2
        float lineFromX1 = 2.0f;
        float lineFromY1 = 4.0f;

        float lineToX1 = 1.5f;
        float lineToY1 = 2.0f;

        //Point2
        float pointFromX1 = 2.5f;
        float pointFromY1 = 2.5f;

        float pointToX1 = 1.5f;
        float pointToY1 = 3.5f;
        boolean expected1 = true;
        assertEquals(expected1, KdTree.doIntersect(lineFromX1, lineFromY1,lineToX1, lineToY1, pointFromX1, pointFromY1, pointToX1, pointToY1));

        //Line3
        float lineFromX2 = 1.5f;
        float lineFromY2 = -3.0f;

        float lineToX2 = 2.0f;
        float lineToY2 = -1.0f;

        //Point3
        float pointFromX2 = 2.5f;
        float pointFromY2 = 2.5f;

        float pointToX2 = 1.5f;
        float pointToY2 = -1.5f;
        boolean expected2 = true;
        assertEquals(expected2, KdTree.doIntersect(lineFromX2, lineFromY2,lineToX2, lineToY2, pointFromX2, pointFromY2, pointToX2, pointToY2));


        //Line4
        float lineFromX3 = 1.5f;
        float lineFromY3 = -3.0f;

        float lineToX3 = 2.0f;
        float lineToY3 = -1.0f;

        //Point4
        float pointFromX3 = 2.5f;
        float pointFromY3 = -2.5f;

        float pointToX3 = 2.5f;
        float pointToY3 = -2.5f;
        boolean expected3 = false;
        assertEquals(expected3, KdTree.doIntersect(lineFromX3, lineFromY3,lineToX3, lineToY3, pointFromX3, pointFromY3, pointToX3, pointToY3));

        //Line5
        float lineFromX4 = 1.5f;
        float lineFromY4 = -3.0f;

        float lineToX4 = 2.0f;
        float lineToY4 = -1.0f;

        //Point5
        float pointFromX4 = 2.5f;
        float pointFromY4 = -2.5f;

        float pointToX4 = 1.0f;
        float pointToY4 = -4.5f;
        boolean expected4 = false;
        assertEquals(expected4, KdTree.doIntersect(lineFromX4, lineFromY4,lineToX4, lineToY4, pointFromX4, pointFromY4, pointToX4, pointToY4));
    }

    @Test
    void distanceFromPointToLine() {
        //Point
        float aX = 1f;
        float aY = 3f;

        //Line
        float lineFromX = -1f;
        float lineFromY = 4f;

        float lineToX = 3f;
        float lineToY = 4f;
        double expected = 1.0;
        double actual = KdTree.distanceFromPointToLine(aX, aY, lineFromX, lineFromY, lineToX, lineToY);
        assertEquals(expected, actual);

        //Point
        float bX = 3f;
        float bY = 1f;

        //Line
        float lineFrom1X = 3f;
        float lineFrom1Y = 0f;

        float lineTo1X = 5f;
        float lineTo1Y = -1f;

        double expected1 = 1.0;
        double actual1 = KdTree.distanceFromPointToLine(bX, bY, lineFrom1X, lineFrom1Y, lineTo1X, lineTo1Y);
        assertEquals(expected1, actual1);

        //Line
        float lineFrom2X = 6f;
        float lineFrom2Y = 1f;

        float lineTo2X = 6f;
        float lineTo2Y = 1f;

        double expected2 = 3.0;
        double actual2 = KdTree.distanceFromPointToLine(bX, bY, lineFrom2X, lineFrom2Y, lineTo2X, lineTo2Y);
        assertEquals(expected2, actual2);

    }

    /**
     * Test that the number of nodes in the tree equals twice the size of the input minus 1
     */
    @Test
    void getSize() {
        int numOfWays = 0;
        for(Map.Entry<DrawType, List<Way>> entry : wayMap.entrySet()){
            numOfWays += entry.getValue().size();
        }
        double expectedSize = numOfWays;
        expectedSize *= 2;
        expectedSize -= 1;
        int actualSize = kdtreeWays.getSize();
        assertEquals(expectedSize, actualSize);

        int numOfMultiPolygons = 0;
        for(Map.Entry<DrawType, List<MultiPolygon>> entry : multiPolygonMap.entrySet()){
            numOfMultiPolygons += entry.getValue().size();
        }
        double expectedSize1 = numOfMultiPolygons;
        expectedSize1 *= 2;
        expectedSize1 -= 1;
        int actualSize1 = kdtreeMultiPolygons.getSize();
        assertEquals(expectedSize1, actualSize1);

        int numOfCanvastexts = 0;
        numOfCanvastexts += canvasTexts.size();
        double expectedSize2 = numOfCanvastexts;
        expectedSize2 *= 2;
        expectedSize2 -= 1;
        int actualSize2 = kdtreeCanvasTexts.getSize();
        assertEquals(expectedSize2, actualSize2);
    }
}




