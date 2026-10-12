
package bfst22.model.kdtree;

import bfst22.model.graphiccomponents.DrawCoordinate;
import bfst22.model.graphiccomponents.DrawType;
import bfst22.model.pathfinding.TransportType;
import bfst22.view.MapCanvas;

import java.io.Serializable;
import java.util.*;

/**
 * Supports methods such as nearest neighbour and range query. The latter
 * does not fully work as intended because it returns elements that lie outside
 * the range of the query - specifically on the y-axis
 *
 * The method: public static boolean doIntersect(float p1X, float p1Y, float q1X, float q1Y, float p2X, float p2Y, float q2X, float q2Y)
 * is not our original work. Credits of this implementation goes to Princi Singh (contributor at Geeks For Geeks)
 * url: https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/ Last Updated 21/10/2021, Last visited 11/05/2022
 *
 * The method: static int orientation(float pX, float pY, float qX, float qY, float rX, float rY)
 * is not our original work. Credits of this implementation goes to Arnav Kr. Mandal (contributor at Geeks For Geeks)
 * url: https://www.geeksforgeeks.org/orientation-3-ordered-points/ Last updated 01/09/2021, Last visited 11/05/2021
 *
 * The method: static boolean onSegment(float pX, float pY, float qX, float qY, float rX, float rY)
 * is not our original work. Credits of this implementation goes to Princi Singh (contributor at Geeks For Geeks)
 * url: https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/ Last Updated 21/10/2021, Last visited 11/05/2022
 *
 * The method: static float distanceFromPointToLine(float pointX, float pointY, float lineFromX, float lineFromY, float lineToX, float lineToY)
 * Credits of this implementation goes to user Joshua at StackOverflow.
 * url: https://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment Last edited 20/05/2020, Last visited 11/05/2022
 */
public class KdTree<Key extends DrawCoordinate> implements Serializable {
    public static final long serialVersionUID = 9879456651L;
    KdNode<Key> first;
    private int depthTracker;
    private int numOfLeaves;
    private int size;
    private int depth;
    private long numOfElements;
    private long inputSize;
    private long minNumberOfElementsInLeafNode;
    private long maxNumberOfElementsInLeafNode;
    Key currentClosest;
    float closestDistance;
    float minX, minY, maxX, maxY;

    long buildTimeInMilliSeconds;
    long insertTimeInMilliSeconds;
    long nearestNeighbourInMilliSeconds;

    Map<DrawType, Set<Key>> query;
    Set<Key> queryAsSet;
    List<Float> splittingLines;
    List<Float> seachedRectangles;

    /**
     * Builds a KdTree on the given list
     */
    public KdTree(List<Key> list) {
        long startTimeBuild = System.nanoTime();
        setup(list);
        build(list);
        long endTimeBuild = System.nanoTime();
        buildTimeInMilliSeconds = (endTimeBuild - startTimeBuild) / 1000000; //milliseconds

        long startTimeInsert = System.nanoTime();
        insert(list);
        long endTimeInsert = System.nanoTime();
        insertTimeInMilliSeconds = (endTimeInsert - startTimeInsert) / 1000000;
    }

    /**
     * Assigns values that the tree will use
     * @param list the list given in the constructor
     */
    private void setup(List<Key> list) {
        first = null;
        size = 0;
        numOfLeaves = 0;
        inputSize = list.size();
        depthTracker = 0;
        splittingLines = new ArrayList<>();
        seachedRectangles = new ArrayList<>();
        currentClosest = null;
        numOfElements = 0;
        minNumberOfElementsInLeafNode = Long.MAX_VALUE;
        maxNumberOfElementsInLeafNode = 0;

        minX = Float.MAX_VALUE;
        maxY = Float.MAX_VALUE;
        maxX = Float.MIN_VALUE;
        minY = Float.MAX_VALUE;
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                minX = list.get(i).getMinX();
                minY = list.get(i).getMinY();
                maxX = list.get(i).getMaxX();
                maxY = list.get(i).getMaxY();
            }
            if (list.get(i).getMinX() < minX) {
                minX = list.get(i).getMinX();
            }
            if (list.get(i).getMaxY() < maxY) {
                maxY = list.get(i).getMaxY();
            }
            if (list.get(i).getMaxX() > maxX) {
                maxX = list.get(i).getMaxX();
            }
            if (list.get(i).getMinY() > minY) {
                minY = list.get(i).getMinY();
            }
        }
        splittingLines.add(minX);
        splittingLines.add(minY);
        splittingLines.add(maxX);
        splittingLines.add(minY);

        splittingLines.add(maxX);
        splittingLines.add(minY);
        splittingLines.add(maxX);
        splittingLines.add(maxY);

        splittingLines.add(maxX);
        splittingLines.add(maxY);
        splittingLines.add(minX);
        splittingLines.add(maxY);

        splittingLines.add(minX);
        splittingLines.add(maxY);
        splittingLines.add(minX);
        splittingLines.add(minY);

        query = new HashMap<>();
        for(DrawType drawType : DrawType.values()){
            query.put(drawType, new HashSet<>());
        }
        queryAsSet = new HashSet<>();
    }

    /**
     * Build the data structure, without inserting elements
     * @param list the list of objects that the tree will use to generate nodes and splitting values
     */
    private void build(List<Key> list) {
        depthTracker = 0;
        first = build(list, true, 0, list.size() - 1, minX, minY, maxX, maxY, depthTracker);
    }

    private KdNode<Key> build(List<Key> list, boolean isComparingXValues, int from, int to, float rectminX, float rectminY, float rectmaxX, float rectmaxY, int depthTracker) {
        int treeDepth = Integer.MAX_VALUE;
        if (to <= from || depthTracker == treeDepth) {
            numOfLeaves++;
            size++;
            KdNode<Key> leafNode = new KdNode<>();
            leafNode.depth = depthTracker;
            if(depthTracker > depth){
                depth = depthTracker;
            }
            leafNode.isLeafNode = true;
            return leafNode;
        }
        KdNode<Key> current = null;
        try {
            current = new KdNode<>();
            size++;
            DrawCoordinate splittingKey = getMiddleKey(list, isComparingXValues, from, to);
            current.splittingValue = (isComparingXValues) ? splittingKey.getMaxX() : splittingKey.getMaxY();
            if(isComparingXValues){
                splittingLines.add(current.splittingValue);
                splittingLines.add(rectminY);

                splittingLines.add(current.splittingValue);
                splittingLines.add(rectmaxY);
            }
            else {
                splittingLines.add(rectminX);
                splittingLines.add(current.splittingValue);

                splittingLines.add(rectmaxX);
                splittingLines.add(current.splittingValue);
            }
            current.depth = depthTracker;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        int mid = from + (((to - 1) - from) / 2);
        if(isComparingXValues){
            current.left = build(list, false, from, mid, rectminX, rectminY, current.splittingValue, rectmaxY, depthTracker + 1);
            current.right = build(list, false, mid + 1, to, current.splittingValue, rectminY, rectmaxX, rectmaxY, depthTracker + 1);
        }
        else {
            current.right = build(list, true, from, mid, rectminX, rectminY, rectmaxX, current.splittingValue, depthTracker + 1);
            current.left = build(list, true, mid + 1, to, rectminX, current.splittingValue, rectmaxX, rectmaxY, depthTracker + 1);
        }
        return current;
    }

    /**
     * Inserts the elements into the data structure
     * @param list the list of objects to be inserted
     */
    public void insert(List<Key> list) {
        for (Key drawCoordinate : list) {
            Iterator<List<Float>> it = drawCoordinate.iterator();
            while (it.hasNext()) {
                List<Float> coords = it.next();
                if (coords.size() == 2) {
                    insert(first, drawCoordinate, coords.get(0), coords.get(1), coords.get(0), coords.get(1), minX, minY, maxX, maxY, true);
                    continue;
                }
                for (int j = 0; j < coords.size() - 2; j += 2) {
                    insert(first, drawCoordinate, coords.get(j), coords.get(j + 1), coords.get(j + 2), coords.get(j + 3), minX, minY, maxX, maxY, true);
                }
            }
        }
    }

    private void insert(KdNode<Key> current, Key key, float lineFromX, float lineFromY, float lineToX, float lineToY, float rectMinX, float rectMinY, float rectMaxX, float rectMaxY, boolean isComparingXValues) {
        if (current.isLeafNode) {
            if(!current.getKeys().contains(key)){
                numOfElements++;
            }
            current.addKey(key);
            if(current.getKeys().size() > maxNumberOfElementsInLeafNode){
                maxNumberOfElementsInLeafNode = current.getKeys().size();
            }
            if(current.getKeys().size() < minNumberOfElementsInLeafNode){
                minNumberOfElementsInLeafNode = current.getKeys().size();
            }
            return;
        }
        if (isComparingXValues) {
            if (lineIntersectsRect(lineFromX, lineFromY, lineToX, lineToY, rectMinX, rectMinY, current.splittingValue, rectMaxY)) {
                insert(current.left, key, lineFromX, lineFromY, lineToX, lineToY, rectMinX, rectMinY, current.splittingValue, rectMaxY, false);
            }
            if (lineIntersectsRect(lineFromX, lineFromY, lineToX, lineToY, current.splittingValue, rectMinY, rectMaxX, rectMaxY)) {
                insert(current.right, key, lineFromX, lineFromY, lineToX, lineToY, current.splittingValue, rectMinY, rectMaxX, rectMaxY, false);
            }
        } else {
            if (lineIntersectsRect(lineFromX, lineFromY, lineToX, lineToY, rectMinX, rectMinY, rectMaxX, current.splittingValue)) {
                insert(current.right, key, lineFromX, lineFromY, lineToX, lineToY, rectMinX, rectMinY, rectMaxX, current.splittingValue, true);
            }
            if (lineIntersectsRect(lineFromX, lineFromY, lineToX, lineToY, rectMinX, current.splittingValue, rectMaxX, rectMaxY)) {
                insert(current.left, key, lineFromX, lineFromY, lineToX, lineToY, rectMinX, current.splittingValue, rectMaxX, rectMaxY, true);
            }
        }

    }

    /**
     * Finds the road closest to the given coordinates.
     * @param x longitude
     * @param y latitude
     * @return The closest road
     */
    public Key nearestNeighbour(float x, float y) {
        long startTime = System.nanoTime();
        closestDistance = Float.MAX_VALUE;
        Key dc = nearestNeighbour(x, y, first, true);
        long endTime = System.nanoTime();
        nearestNeighbourInMilliSeconds = (endTime - startTime) / 1000000;
        return dc;
    }

    private Key nearestNeighbour(float pointX, float pointY, KdNode<Key> current, boolean isComparingXValues) {
        if (current.isLeafNode) {
            for (Key key : current.getKeys()) {
                if (TransportType.transportAllowedDrawCoordinate(key)) {
                    Iterator<List<Float>> it = key.iterator();
                    while(it.hasNext()) {
                        List<Float> cords = it.next();
                        if (cords.size() == 2) {
                            if (distanceFromPointToLine(pointX, pointY, cords.get(0), cords.get(1), cords.get(0), cords.get(1)) < closestDistance) {
                                closestDistance = distanceFromPointToLine(pointX, pointY, cords.get(0), cords.get(1), cords.get(0), cords.get(1));
                                currentClosest = key;
                            }
                            continue;
                        }
                        for (int j = 0; j < cords.size() - 2; j += 2) {
                            if (distanceFromPointToLine(pointX, pointY, cords.get(j), cords.get(j + 1), cords.get(j + 2), cords.get(j + 3)) < closestDistance) {
                                closestDistance = distanceFromPointToLine(pointX, pointY, cords.get(j), cords.get(j + 1), cords.get(j + 2), cords.get(j + 3));
                                currentClosest =  key;
                            }
                        }
                    }
                }
            }
        }
        if (!current.isLeafNode) {
            if (isComparingXValues) {
                if (current.splittingValue <= pointX) {
                    currentClosest = nearestNeighbour(pointX, pointY, current.right, false);
                    if (Math.abs(pointX - current.splittingValue) <= closestDistance) {
                        currentClosest = nearestNeighbour(pointX, pointY, current.left, false);
                    }
                } else {
                    currentClosest = nearestNeighbour(pointX, pointY, current.left, false);
                    if (Math.abs(pointX - current.splittingValue) <= closestDistance) {
                        currentClosest = nearestNeighbour(pointX, pointY, current.right, false);
                    }
                }
            } else {
                if (current.splittingValue <= pointY) {
                    currentClosest = nearestNeighbour(pointX, pointY, current.right, true);
                    if (Math.abs(pointY - current.splittingValue) <= closestDistance) {
                        currentClosest = nearestNeighbour(pointX, pointY, current.left, true);
                    }
                } else {
                    currentClosest = nearestNeighbour(pointX, pointY, current.left, true);
                    if (Math.abs(pointY - current.splittingValue) <= closestDistance) {
                        currentClosest = nearestNeighbour(pointX, pointY, current.right, true);
                    }
                }
            }
        }
        return currentClosest;
    }

    /**
     * Returns a Map of the objects in the given range. The keys are inserted in the correct drawing order
     * such that looping through the Map and drawing each set one after another will look correct
     * @param queryMinX lowest longitude
     * @param queryMaxY highest latitude
     * @param queryMaxX highest longitude
     * @param queryMinY lowest latitude
     * @return The query
     */
    public Map<DrawType, Set<Key>> rangeQuery(float queryMinX, float queryMaxY, float queryMaxX, float queryMinY){
        for(Map.Entry<DrawType, Set<Key>> entry : query.entrySet()){
            entry.getValue().clear();
        }
        seachedRectangles = new ArrayList<>();
        rangeQuery(first, queryMinX, queryMaxY, queryMaxX, queryMinY, minX, minY, maxX, maxY, true, false);
        return query;
    }

    /**
     * Return a Set of all objects in the given range. The Set needs to be sorted in order to ensure
     * correct drawing order
     * @param queryMinX lowest longitude
     * @param queryMaxY highest latitude
     * @param queryMaxX highest longitude
     * @param queryMinY lowest latitude
     * @return The unsorted query
     */
    public Set<Key> rangeQueryAsSet(float queryMinX, float queryMaxY, float queryMaxX, float queryMinY){
        queryAsSet.clear();
        seachedRectangles = new ArrayList<>();
        rangeQuery(first, queryMinX, queryMaxY, queryMaxX, queryMinY, minX, minY, maxX, maxY, true, true);
        return queryAsSet;
    }


    private void rangeQuery(KdNode<Key> current, float queryMinX, float queryMinY, float queryMaxX, float queryMaxY, float treeMinX, float treeMinY, float treeMaxX, float treeMaxY, boolean isComparingXValues, boolean usingSetQuery){
        if(current.isLeafNode){
            if (usingSetQuery) {
                insertIntoQuery(current, queryAsSet);
            } else {
                insertIntoQuery(current, query);
            }
            return;
        }
        if(isComparingXValues){
            if(rectIntersectsRect(treeMinX, treeMinY, current.splittingValue, treeMaxY, queryMinX, queryMinY, queryMaxX, queryMaxY)){
                rangeQuery(current.left, queryMinX, queryMinY, queryMaxX, queryMaxY, treeMinX, treeMinY, current.splittingValue, treeMaxY, false, usingSetQuery);
            }
            if(rectIntersectsRect(current.splittingValue, treeMinY, treeMaxX, treeMaxY, queryMinX, queryMinY, queryMaxX, queryMaxY)){
                rangeQuery(current.right, queryMinX, queryMinY, queryMaxX, queryMaxY, current.splittingValue, treeMinY, treeMaxX, treeMaxY, false, usingSetQuery);
            }
        }
        else {
            if(rectIntersectsRect(treeMinX, treeMinY, treeMaxX, current.splittingValue, queryMinX, queryMinY, queryMaxX, queryMaxY)){
                rangeQuery(current.right, queryMinX, queryMinY, queryMaxX, queryMaxY, treeMinX, treeMinY, treeMaxX, current.splittingValue, true, usingSetQuery);
            }
            if(rectIntersectsRect(treeMinX, current.splittingValue, treeMaxX, treeMaxY, queryMinX, queryMinY, queryMaxX, queryMaxY)){
                rangeQuery(current.left, queryMinX, queryMinY, queryMaxX, queryMaxY, treeMinX, current.splittingValue, treeMaxX, treeMaxY, true, usingSetQuery);
            }
        }
    }

    private void insertIntoQuery(KdNode<Key> current, Map<DrawType, Set<Key>> queryMap){
        for(Key key : current.getKeys()){
            if(MapCanvas.renderZoomLevel(key)) {
                queryMap.get(key.drawType).add(key);
            }
        }
    }

    private void insertIntoQuery(KdNode<Key> current, Set<Key> querySet){
        for(Key key : current.getKeys()){
            if(MapCanvas.renderZoomLevel(key)) {
                querySet.add(key);
            }
        }
    }

    /**
     * Sorts the list in the given interval based on X or Y coordinates, and returns middle element in that interval
     * @param list the list of DrawCoordinate
     * @param isComparingXValues sort on x or y-values
     * @param from lower bound
     * @param to upper bound
     * @return The middle key
     */
    private DrawCoordinate getMiddleKey(List<Key> list, boolean isComparingXValues, int from, int to) {
        List<Key> sublist = list.subList(from, to);

        if(isComparingXValues){
            sublist.sort((o1, o2) -> Float.compare(o1.getMaxX(), o2.getMaxX()));
        }
        else {
            sublist.sort((o1, o2) -> Float.compare(o1.getMaxY(), o2.getMaxY()));
        }

        int mid = (((to - 1) - from) / 2);
        return sublist.get(mid);
    }

    public static boolean lineIntersectsRect(float lineFromX, float lineFromY, float lineToX, float lineToY, float rect1X, float rect1Y, float rect2X, float rect2Y) {
        float minY = Math.min(rect1Y, rect2Y);
        float maxY = Math.max(rect1Y, rect2Y);
        float minX = Math.min(rect1X, rect2X);
        float maxX = Math.max(rect1X, rect2X);

        //If one of the start and end points of line segment are inside the rectangle
        if (minX <= lineFromX && lineFromX <= maxX && minY <= lineFromY && lineFromY <= maxY) {
            return true;
        }
        if (minX <= lineToX && lineToX <= maxX && minY <= lineToY && lineToY <= maxY) {
            return true;
        }

        //If the line segment intersects a side of the rectangle
        if (doIntersect(lineFromX, lineFromY, lineToX, lineToY, rect1X, rect1Y, rect2X, rect1Y)) {
            return true;
        }
        if (doIntersect(lineFromX, lineFromY, lineToX, lineToY, rect1X, rect2Y, rect2X, rect2Y)) {
            return true;
        }
        if (doIntersect(lineFromX, lineFromY, lineToX, lineToY, rect1X, rect1Y, rect1X, rect2Y)) {
            return true;
        }
        return doIntersect(lineFromX, lineFromY, lineToX, lineToY, rect2X, rect1Y, rect2X, rect2Y);
    }

    /**
     * Checks whether rect1 and rect2 overlap
     * @param rect1MinX min x values of rectangle 1
     * @param rect1MinY min y value of rectangle 1
     * @param rect1MaxX max x value of rectangle 1
     * @param rect1MaxY max y values of ractangle 1
     * @param rect2MinX min x value of rectangle 2
     * @param rect2MinY min y value of rectangle 2
     * @param rect2MaxX max x value of rectangle 2
     * @param rect2MaxY max y values of ractangle 2
     */
    public static boolean rectIntersectsRect(float rect1MinX, float rect1MinY, float rect1MaxX, float rect1MaxY, float rect2MinX, float rect2MinY, float rect2MaxX, float rect2MaxY){
        if(rect2MaxX <= rect1MinX || rect1MaxX <= rect2MinX){
            return false;
        }
        return !(rect2MinY <= rect1MaxY) && !(rect1MinY <= rect2MaxY);
    }

    /**
     * This is not our original work. Credits of this implementation goes to Princi Singh (contributor at Geeks For Geeks)
     * url: https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/ Last Updated 21/10/2021, Last visited 11/05/2022
     * Checks whether two line segments intersect.
     */
    public static boolean doIntersect(float p1X, float p1Y, float q1X, float q1Y, float p2X, float p2Y, float q2X, float q2Y) {
        // Find the four orientations needed for general and
        // special cases
        int o1 = orientation(p1X, p1Y, q1X, q1Y, p2X, p2Y);
        int o2 = orientation(p1X, p1Y, q1X, q1Y, q2X, q2Y);
        int o3 = orientation(p2X, p2Y, q2X, q2Y, p1X, p1Y);
        int o4 = orientation(p2X, p2Y, q2X, q2Y, q1X, q1Y);

        // General case
        if (o1 != o2 && o3 != o4) {
            //System.out.println("INTERSECTION");
            return true;
        }

        // Special Cases
        // p1, q1 and p2 are collinear and p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1X, p1Y, p2X, p2Y, q1X, q1Y)) {
            return true;
        }

        // p1, q1 and q2 are collinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1X, p1Y, q2X, q2Y, q1X, q1Y)) {
            return true;
        }

        // p2, q2 and p1 are collinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2X, p2Y, p1X, p1Y, q2X, q2Y)) {
            return true;
        }
        // p2, q2 and q1 are collinear and q1 lies on segment p2q2
        return o4 == 0 && onSegment(p2X, p2Y, q1X, q1Y, q2X, q2Y);// Doesn't fall in any of the above cases
    }

    /**
     * This is not our original work. Credits of this implementation goes to Arnav Kr. Mandal (contributor at Geeks For Geeks)
     * url: https://www.geeksforgeeks.org/orientation-3-ordered-points/ Last updated 01/09/2021, Last visited 11/05/2021
     * Finds the orientation of a triangle formed by three points. Used for checking if line segments intersect
     */
    static int orientation(float pX, float pY, float qX, float qY, float rX, float rY) {
        // See https://www.geeksforgeeks.org/orientation-3-ordered-points/
        // for details of below formula.
        float val = (qY - pY) * (rX - qX) - (qX - pX) * (rY - qY);
        int intVal = (int) val;

        if (intVal == 0) return 0; // collinear

        return (intVal > 0) ? 1 : 2; // clock or counterclock wise
    }

    /**
     * This is not our original work. Credits of this implementation goes to Princi Singh (contributor at Geeks For Geeks)
     * url: https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/ Last Updated 21/10/2021, Last visited 11/05/2022
     * Checks whether the point b lines on the line segment ac
     */
    static boolean onSegment(float pX, float pY, float qX, float qY, float rX, float rY) {
        return qX <= Math.max(pX, rX) && qX >= Math.min(pX, rX) &&
                qY <= Math.max(pY, rY) && qY >= Math.min(pY, rY);
    }

    /**
     * This is not our original work. Credits of this implementation goes to user Joshua at StackOverflow.
     * url: https://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment Last edited 20/05/2020, Last visited 11/05/2022
     * Finds distance between a point and a line segment
     */
    static float distanceFromPointToLine(float pointX, float pointY, float lineFromX, float lineFromY, float lineToX, float lineToY) {
        float A = pointX - lineFromX;
        float B = pointY - lineFromY;
        float C = lineToX - lineFromX;
        float D = lineToY - lineFromY;

        float dot = A * C + B * D;
        float len_sq = C * C + D * D;
        float param = -1.0f;
        if (len_sq != 0) {
            param = dot / len_sq;
        }

        float xx, yy;

        if (param < 0) {
            xx = lineFromX;
            yy = lineFromY;
        } else if (param > 1) {
            xx = lineToX;
            yy = lineFromY;
        } else {
            xx = lineFromX + param * C;
            yy = lineFromY + param * D;
        }
        float dx = pointX - xx;
        float dy = pointY - yy;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public int getSize() {
        return size;
    }

    /**
     * The lines that visualize the KdTree when drawn.
     * @return A sequence of 4 values define a line. In order to draw correctly iterate with an increment of +4
     */
    public List<Float> getSplittingLines() {
        return splittingLines;
    }

    public int getTreeDepth() {
        return depth;
    }

    public long getNumOfElements(){
        return numOfElements;
    }

    public long getBuildTimeInMilliSeconds() {
        return buildTimeInMilliSeconds;
    }

    public long getInsertTimeInMilliSeconds() {
        return insertTimeInMilliSeconds;
    }

    public long getInputSize() {
        return inputSize;
    }

    public long getMinNumberOfElementsInLeafNode() {
        return minNumberOfElementsInLeafNode;
    }

    public long getMaxNumberOfElementsInLeafNode() {
        return maxNumberOfElementsInLeafNode;
    }

    public int getNumOfLeaves() {
        return numOfLeaves;
    }
}
