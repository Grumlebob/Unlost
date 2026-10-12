package bfst22.view;

import bfst22.controller.ControllerMediator;
import bfst22.controller.SplashScreenController;
import bfst22.model.MapData;
import bfst22.model.Utility;
import bfst22.model.address.Address;
import bfst22.model.graphiccomponents.*;
import bfst22.model.pathfinding.Digraph;
import bfst22.model.pathfinding.Dijkstra;
import bfst22.model.pathfinding.Edge;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.util.*;

import static bfst22.controller.AddressMenuController.*;
import static bfst22.controller.Controller.*;
import static bfst22.controller.DebugMenuController.*;
import static bfst22.model.osm.MapDataParser.drawtypesLoaded;
import static bfst22.model.osm.MapDataParser.totalLoadingTime;
import static bfst22.model.pathfinding.Dijkstra.isClickMode;
import static bfst22.model.pathfinding.Dijkstra.wayChangesInRoute;
import static javafx.scene.paint.Color.ORANGE;

public class MapCanvas extends Canvas {
    private final Affine trans = new Affine();
    private GraphicsContext gc;
    public static double zoomInt;
    public static float extraMargin;
    public int renderedOnScreen;
    public int renderedOnSceenAllQueries;
    public int renderedOnScreenQueryWays;
    public int renderedOnScreenMultipolygons;
    public int renderedOnScreenCanvastexts;
    public MapData mapData = MapData.getInstance();
    public BoundingBox boundingBox = mapData.getBoundingBox();
    public DrawToggles drawToggles = DrawToggles.getInstance();
    public ControllerMediator controllerMediator = ControllerMediator.getInstance();
    public DrawCoordinate nearestNeighbour;

    public Map<DrawType, Set<DrawCoordinate>> queryWays;
    public Map<DrawType, Set<DrawCoordinate>> queryMultipolygons;

    public Set<DrawCoordinate> queryWaysAsSet;
    public Set<DrawCoordinate> queryMultipolygonsAsSet;
    public List<DrawCoordinate> querySorted;
    public List<DrawCoordinate> queryWaysSorted;
    public List<DrawCoordinate> queryMultipolygonsSorted;

    //SOURCE: https://freepngimg.com/png/20698-mario-transparent-background
    private final Image imgMarioRunning = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("marioRun.png")));
    //SOURCE: https://www.softicons.com/game-icons/super-mario-icons-by-sandro-pereira/paper-bowser-icon
    private final Image imgBowser = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("bowser.png")));
    //SOURCE:https://iconarchive.com/show/super-mario-icons-by-ph03nyx/Goomba-icon.html
    private final Image imgGoomba = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("goomba.png")));
    //SOURCE: https://iconarchive.com/show/super-mario-icons-by-ph03nyx/Flower-Fire-icon.html
    private final Image imgFlowerFire = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("FlowerFire.png")));
    //SOURCE: https://iconarchive.com/show/super-mario-icons-by-ph03nyx/Bullet-Bill-icon.html
    private final Image imgBulletBill = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("BulletBill.png")));
    //SOURCE: https://iconarchive.com/show/super-mario-icons-by-ph03nyx/Lakitu-icon.html
    private final Image imgLakitu = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("Lakitu.png")));
    //SOURCE: https://icon-library.com/icon/point-of-interest-icon-3.html
    private final Image poiImage = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("poi.jpg")));



    public void init() throws InterruptedException {
        zoomInt = 1;
        extraMargin = 5;
        renderedOnSceenAllQueries = 0;
        renderedOnScreenQueryWays = 0;
        renderedOnScreenMultipolygons = 0;
        renderedOnScreenCanvastexts = 0;
        pan(-mapData.getMinX(), -mapData.getMinY());
        zoom(640 / (mapData.getMaxX() - mapData.getMinX()), 0, 0);
        int total = (mapData.getCanvasTexts().size() +
                mapData.getAddressBook().size() + drawtypesLoaded + mapData.getCoastlines().size());
        controllerMediator.getDebugMenuController().setTotalLabel(" " + total);
        controllerMediator.getDebugMenuController().setTotalLabel(" " + total);
        controllerMediator.getDebugMenuController().setGraphBuildLabel(" " + Digraph.graphBuildTime);
        controllerMediator.getDebugMenuController().setTotalEdgesLabel(" " + mapData.getGraph().getEdges());
        controllerMediator.getDebugMenuController().setTotalVerticesLabel(" " + mapData.getGraph().getVertices());
        controllerMediator.getDebugMenuController().setMapParsedLabel("" + totalLoadingTime);
        if(!SplashScreenController.createKDTreeOnlyForWays) {
            controllerMediator.getKdTreeDebugMenuController().updateTotalBuildTimeLabel("" + (mapData.getKdTreeWays().getBuildTimeInMilliSeconds() + mapData.getKdTreeMultiPolygons().getBuildTimeInMilliSeconds()) + " ms");
            controllerMediator.getKdTreeDebugMenuController().updateTotalInsertTimeLabel("" + (mapData.getKdTreeWays().getInsertTimeInMilliSeconds() + mapData.getKdTreeMultiPolygons().getInsertTimeInMilliSeconds()) + " ms");
            controllerMediator.getKdTreeDebugMenuController().updateWaysSizeLabel("" + mapData.getKdTreeWays().getSize() + " nodes");
            controllerMediator.getKdTreeDebugMenuController().updateMultipolygonsSizeLabel("" + mapData.getKdTreeMultiPolygons().getSize() + " nodes");
            controllerMediator.getKdTreeDebugMenuController().updateCanvastextsSizeLabel("" + mapData.getKdTreeCanvasTexts().getSize() + " nodes");
            controllerMediator.getKdTreeDebugMenuController().updateNumOfElementsWays("" + mapData.getKdTreeWays().getNumOfElements());
            controllerMediator.getKdTreeDebugMenuController().updateNumOfElementsCanvastexts("" + mapData.getKdTreeCanvasTexts().getNumOfElements());
            controllerMediator.getKdTreeDebugMenuController().updateNumOfElementsMultiPolygons("" + mapData.getKdTreeMultiPolygons().getNumOfElements());
            controllerMediator.getKdTreeDebugMenuController().updateInputSizeCanvastexts("" + mapData.getKdTreeCanvasTexts().getInputSize());
            controllerMediator.getKdTreeDebugMenuController().updateInputSizeWays("" + mapData.getKdTreeWays().getInputSize());
            controllerMediator.getKdTreeDebugMenuController().updateInputSizeMultipolygons("" + mapData.getKdTreeMultiPolygons().getInputSize());
            controllerMediator.getKdTreeDebugMenuController().updateDepthMultipolygons("" + mapData.getKdTreeMultiPolygons().getTreeDepth());
            controllerMediator.getKdTreeDebugMenuController().updateDepthCanvastexts("" + mapData.getKdTreeCanvasTexts().getTreeDepth());
            controllerMediator.getKdTreeDebugMenuController().updateDepthWays("" + mapData.getKdTreeWays().getTreeDepth());
            controllerMediator.getKdTreeDebugMenuController().updateMinNumOfElementsWays("" + mapData.getKdTreeWays().getMinNumberOfElementsInLeafNode());
            controllerMediator.getKdTreeDebugMenuController().updateMaxNumOfElementsWays("" + mapData.getKdTreeWays().getMaxNumberOfElementsInLeafNode());
            controllerMediator.getKdTreeDebugMenuController().updateMinNumOfElementsCanvastexts("" + mapData.getKdTreeCanvasTexts().getMinNumberOfElementsInLeafNode());
            controllerMediator.getKdTreeDebugMenuController().updateMaxNumOfElementsCanvastexts("" + mapData.getKdTreeCanvasTexts().getMaxNumberOfElementsInLeafNode());
            controllerMediator.getKdTreeDebugMenuController().updateMinNumOfElementsMultipolygons("" + mapData.getKdTreeMultiPolygons().getMinNumberOfElementsInLeafNode());
            controllerMediator.getKdTreeDebugMenuController().updateMaxNumOfElementsMultipolygons("" + mapData.getKdTreeMultiPolygons().getMaxNumberOfElementsInLeafNode());

        }
        this.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            Point2D currentMouseHover = mouseToModel(new Point2D(e.getX(), e.getY()));
            var prev =  nearestNeighbour;
            long startTime = System.nanoTime();
            nearestNeighbour = mapData.getKdTreeWays().nearestNeighbour((float)currentMouseHover.getX(), (float)currentMouseHover.getY());
            if (nearestNeighbour != null)drawNNText(gc, nearestNeighbour.getText());
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000;
            controllerMediator.getKdTreeDebugMenuController().updateNNTime(duration + " ms");
            if (prev != nearestNeighbour) repaint();
        });

        queryMultipolygonsSorted = new ArrayList<>();
        queryWaysSorted = new ArrayList<>();
        querySorted = new ArrayList<>();
        repaint();
    }

    public void repaint(){
        if (mapData == null) return;
//        if(!MapDataParser.isLoaded) return;
        var time = -System.nanoTime();
        gc = getGraphicsContext2D();
        gc.setTransform(new Affine());
        gc.setFill(Color.rgb(170, 211, 223));
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);

        convertCurrentScreenToBoundingBox();

        // DRAW COASTLINE
        if (showCoastlines && mapData.getCoastlines().size() > 0) {
            gc.setFill(DrawType.COASTLINE.getColor());
            gc.setStroke(DrawType.COASTLINE.getColor());
            gc.setLineWidth(DrawType.COASTLINE.getLineWidth());
            for (DrawCoordinate coastLine : mapData.getCoastlines()) {
                coastLine.draw(gc);
                renderedOnScreen++;
            }
        }

        //DRAW FOR BOUNDING BOX
        boundingBox.draw(gc);

        //DRAW FOR WAYS
        if (showWays) {
            long startTime = System.nanoTime();
            for (DrawType type : DrawType.values()) {
                if(type == DrawType.COASTLINE) continue;
                if (drawToggles.drawToggles.get(type.toString()) && renderZoomLevel(type)) { //&& type != DrawType.DEFAULT
                    gc.setFill(type.getColor());
                    gc.setStroke(type.getColor());
                    gc.setLineWidth(type.getLineWidth());
                    gc.setLineDashes(type.getLineDashes());
                    for (Way way : mapData.wayIterable(type)) {
                        if (boundingBox.isInsideBoundingWith1Point(way)) {
                            way.draw(gc);
                            renderedOnScreen++;
                        }
                        if (showBoundingboxes && boundingBox.isInsideBoundingWith1Point(way)) {
                            renderedOnScreen++;
                            way.drawBorder(gc);
                        }
                    }
                }
                //DRAW PÅ MULTIPOLYGONS
                if (showMultipolygons) {
                    for (MultiPolygon multipolygon : mapData.multiPolygonIterable(type)) {
                        if (boundingBox.isInsideBoundingWith1Point(multipolygon) && renderZoomLevel(type)) {
                            multipolygon.draw(gc);
                            renderedOnScreen++;
                        }
                    }
                }
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000;
            controllerMediator.getKdTreeDebugMenuController().updateWayMapDrawTime("" + duration);
        }

        //KD TREE RANGE QUERY
        if(isUsingKDTreeRangeQuery) {
            long queryWaysDuration = 0;
            long queryMulitipolygonsDuration = 0;
            long repaintDuration = 0;
            if(controllerMediator.getKdTreeDebugMenuController().isUsingQueryAsSet()) {
                long queryWaysTime = System.nanoTime();
                queryWaysAsSet = mapData.getKdTreeWays().rangeQueryAsSet(boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMaxX(), boundingBox.getMinY());
                long queryWaysTimeEnd = System.nanoTime();
                queryWaysDuration = (queryWaysTimeEnd - queryWaysTime) / 1000000;

                long queryMultiPolygonsTime = System.nanoTime();
                queryMultipolygonsAsSet = mapData.getKdTreeMultiPolygons().rangeQueryAsSet(boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMaxX(), boundingBox.getMinY());
                long queryMultiPolygonsTimeEnd = System.nanoTime();
                queryMulitipolygonsDuration = (queryMultiPolygonsTimeEnd - queryMultiPolygonsTime) / 1000000;

                long repaintStartTime = System.nanoTime();
                querySorted.addAll(queryWaysAsSet);
                querySorted.addAll(queryMultipolygonsAsSet);
                Collections.sort(querySorted);
                DrawType currentDrawType = DrawType.DEFAULT;
                for(DrawCoordinate dc : querySorted){
                    if(dc.drawType != currentDrawType){
                        currentDrawType = dc.drawType;
                        gc.setFill(currentDrawType.getColor());
                        gc.setStroke(currentDrawType.getColor());
                        gc.setLineWidth(currentDrawType.getLineWidth());
                        gc.setLineDashes(currentDrawType.getLineDashes());
                    }
                    dc.draw(gc);
                    renderedOnScreen++;
                    renderedOnSceenAllQueries++;
                    if(dc instanceof Way){
                        renderedOnScreenQueryWays++;
                    }
                    else if(dc instanceof MultiPolygon){
                        renderedOnScreenMultipolygons++;
                    }
                }
                long repaintEndTime = System.nanoTime();
                repaintDuration = (repaintEndTime - repaintStartTime) / 1000000;

                queryWaysAsSet = null;
                queryMultipolygonsAsSet = null;
                queryWaysSorted.clear();
                queryMultipolygonsSorted.clear();
                querySorted.clear();
            }
            else {
                long queryWaysTime = System.nanoTime();
                queryWays = mapData.getKdTreeWays().rangeQuery(boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMaxX(), boundingBox.getMinY());
                long queryWaysTimeEnd = System.nanoTime();
                queryWaysDuration = (queryWaysTimeEnd - queryWaysTime) / 1000000;
                queryMultipolygons = mapData.getKdTreeMultiPolygons().rangeQuery(boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMaxX(), boundingBox.getMinY());
                long repaintStartTime = System.nanoTime();
                for (DrawType type : DrawType.values()) {
                    if (renderZoomLevel(type)) { //&& type != DrawType.DEFAULT
                        if (type == DrawType.COASTLINE) continue;
                        gc.setFill(type.getColor());
                        gc.setStroke(type.getColor());
                        gc.setLineWidth(type.getLineWidth());
                        gc.setLineDashes(type.getLineDashes());
                        if (queryWays.containsKey(type)) {
                            if (queryWays.get(type).isEmpty() == false) {
                                for (DrawCoordinate way : queryWays.get(type)) {
                                    way.draw(gc);
                                    renderedOnScreen++;
                                    renderedOnSceenAllQueries++;
                                    renderedOnScreenQueryWays++;
                                }
                            }
                        }
                        if (showMultipolygons) {
                            if (queryMultipolygons.containsKey(type)) {
                                for (DrawCoordinate polygon : queryMultipolygons.get(type)) {
                                    polygon.draw(gc);
                                    renderedOnScreen++;
                                    renderedOnSceenAllQueries++;
                                    renderedOnScreenMultipolygons++;
                                }
                            }
                        }
                    }
                }
                long repaintEndTime = System.nanoTime();
                repaintDuration = (repaintEndTime - repaintStartTime) / 1000000;
                queryWays = null;
                queryMultipolygons = null;

            }


            controllerMediator.getKdTreeDebugMenuController().updateTotalQueryTime("" + (queryWaysDuration + queryMulitipolygonsDuration) + " ms" );
            controllerMediator.getKdTreeDebugMenuController().updateQueryDrawTime("" + repaintDuration + " ms");

            controllerMediator.getKdTreeDebugMenuController().updateQuerySearchTimeWays("" + queryWaysDuration + " ms");
            controllerMediator.getKdTreeDebugMenuController().updateRenderedFromQueryLabelWays(""+ renderedOnScreenQueryWays);
            controllerMediator.getKdTreeDebugMenuController().updateQuerySearchTimeMultiPolygons("" + queryMulitipolygonsDuration + " ms");

            controllerMediator.getKdTreeDebugMenuController().updateRenderedFromQueryLabelMultipolygons("" + renderedOnScreenMultipolygons);
            }


        //WAY TEXT
        for (DrawType type : DrawType.values()) {
            for (Way way : mapData.wayIterable(type)) {
                if (showWayText && boundingBox.isInsideBoundingWith1Point(way)) {
                    renderedOnScreen++;
                    way.drawWayText(gc);
                }
            }
        }

        //DRAW FOR CANVASTEXT
        if (showCanvasText) {
            if (showWays) {
                List<CanvasText> canvasTexts = mapData.getCanvasTexts();
                for (CanvasText c : canvasTexts) {
                    if (renderZoomLevelText(c)) {
                        if (boundingBox.isInsideBoundingWith1Point(c)) {
                            renderedOnScreen++;
                            renderedOnSceenAllQueries++;
                            renderedOnScreenCanvastexts++;
                            c.draw(gc);
                            c.drawScalingText(gc, (1.0 / Math.sqrt(trans.determinant())) * 1);
                        }
                    }
                }
            }
            if(isUsingKDTreeRangeQuery){
                long queryTimeStart = System.nanoTime();
                Set<DrawCoordinate> canvasTexts = mapData.getKdTreeCanvasTexts().rangeQueryAsSet(boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMaxX(), boundingBox.getMinY());
                long queryTimeEnd = System.nanoTime();
                long duration = (queryTimeEnd - queryTimeStart) / 1000000;
                for(DrawCoordinate dc : canvasTexts){
                    CanvasText c = (CanvasText) dc;
                    renderedOnScreen++;
                    renderedOnSceenAllQueries++;
                    renderedOnScreenCanvastexts++;
                    c.draw(gc);
                    c.drawScalingText(gc, (1.0 / Math.sqrt(trans.determinant())) * 1);
                }
                controllerMediator.getKdTreeDebugMenuController().updateQuerySearchTimeCanvastexts("" + duration + " ms");
                controllerMediator.getKdTreeDebugMenuController().updateRenderedFromQueryCanvastexts("" + renderedOnScreenCanvastexts);
            }
        }


        //DIJKSTRA EDGES EXPLORED
        if (Dijkstra.edgesExplored != null && showEdgesExplored) {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(0.00006);
            for (Edge e : Dijkstra.edgesExplored) {
                gc.beginPath();
                gc.moveTo(e.getFrom().lonX, e.getFrom().latY);
                gc.lineTo(e.getTo().lonX, e.getTo().latY);
                gc.stroke();
            }
        }
        //DRAW SPT
        if (showSPT) {
            mapData.getPathfinder().drawSPT(gc);
        }
        //DIJKSTRA BEST PATH
        if (Dijkstra.shortestPath != null && showBestPath && Dijkstra.hasValidPath) {
            if (zoomInt < 10000) gc.setLineWidth(0.001);
            else if (zoomInt < 35000) gc.setLineWidth(0.0003);
            else gc.setLineWidth(0.00007);
            if (!show500MeterMode) {
                gc.beginPath();
                gc.setStroke(ORANGE);
                if (!isClickMode) gc.moveTo(addressFromX, addressFromY);
                else gc.moveTo(lastMouseX, lastMouseY);
                for (Edge e : Dijkstra.shortestPath) {
                    gc.lineTo(e.getTo().lonX, e.getTo().latY);
                }
                if (!isClickMode) gc.lineTo(addressToX, addressToY);
                else gc.lineTo(toCoordsX, toCoordsY);
                gc.stroke();
            }
            else {
                for (Edge e : Dijkstra.shortestPath) {
                    gc.beginPath();
                    gc.setStroke(e.colorOnCanvas);
                    gc.moveTo(e.getFrom().lonX, e.getFrom().latY);
                    gc.lineTo(e.getTo().lonX, e.getTo().latY);
                    gc.stroke();
                }
            }
        }
        //Draw SEARCHED ADDRESSES
        drawSearchedAddress(gc);

        //Draw Point Of Interest
        gc.setFill(Color.rgb(0, 145, 200));
        for (Address poi : mapData.getAddressBook().getPointOfInterestList()) {
            gc.drawImage(poiImage, poi.lonX, poi.latY, (1.0 / Math.sqrt(trans.determinant()) * 40), (1.0 / Math.sqrt(trans.determinant()) * 40));
            gc.fillText(poi.toString(),poi.lonX, poi.latY);
        }

        if (!showMarioMode) {
            //Draw SEARCHED ADDRESSES
            drawSearchedAddress(gc);
            //Draw LeftClick
            if (zoomInt < 2000) gc.setFont(new Font("Arial", 0.01));
            else if (zoomInt < 4000) gc.setFont(new Font("Arial", 0.005));
            else if (zoomInt < 15000) gc.setFont(new Font("Arial", 0.003));
            else if (zoomInt < 25000) gc.setFont(new Font("Arial", 0.002));
            else if (zoomInt < 35000) gc.setFont(new Font("Arial", 0.001));
            else if (zoomInt < 55000) gc.setFont(new Font("Arial", 0.0005));
            else gc.setFont(new Font("Arial", 0.0002));
            gc.setFill(Color.rgb(0, 166, 0));
            gc.fillText("❢",0.99998f* lastMouseX, lastMouseY); //offset to get img closer to center of mouse
            //Shift LeftClick
            gc.fillText("⚑", toCoordsX, toCoordsY);
        }

        //KDTREE STRUCTURE
        if(showKDTreeStructure){
            gc.setLineWidth(0.000008);
            List<Float> cords = mapData.getKdTreeWays().getSplittingLines();
            gc.setStroke(Color.RED);
            for (int i = 0; i < cords.size() - 2; i += 4){
                gc.beginPath();
                gc.moveTo(cords.get(i), cords.get(i + 1));
                gc.lineTo(cords.get(i + 2), cords.get(i + 3));
                gc.stroke();
            }
        }

        //KD TREE NEAREST NEIGHBOUR AND WAY STATS
        if(nearestNeighbour != null && showNearestNeighbour) {
            Way way = (Way) nearestNeighbour;
            gc.setStroke(Color.LIGHTGREEN);
            gc.setLineWidth(0.00007);
            gc.beginPath();
            for (int i = 0; i < nearestNeighbour.getCoords().length - 2; i += 2) {
                gc.lineTo(nearestNeighbour.getCoords()[i], nearestNeighbour.getCoords()[i + 1]);
                gc.lineTo(nearestNeighbour.getCoords()[i + 2], nearestNeighbour.getCoords()[i + 3]);
            }
            gc.stroke();
            way.drawNNWayWithStats(gc, showHoverInfo);
        }


        //DRAW SCALABAR
        drawScalaBar(gc);

        //Draw Waychanges in route to see heading//
        if (wayChangesInRoute != null && showHeading) {
            gc.setLineWidth(0.00007);
            for (Edge e : wayChangesInRoute) {
                gc.beginPath();
                gc.setStroke(Color.BLUE);
                gc.lineTo(e.getFrom().lonX, e.getFrom().latY);
                gc.stroke();
                gc.beginPath();
                gc.setStroke(Color.PURPLE);
                gc.lineTo(e.getTo().lonX, e.getTo().latY);
                gc.stroke();
            }
        }

        //MARIOMODE
        if (showMarioMode) {
            //MARIO-enemies ON WAY CHANGES
            if (wayChangesInRoute != null) {
                int counter = 0;
                for (Edge edge : wayChangesInRoute) {
                    counter++;
                    if (counter == 1) { //START MARIO
                        gc.drawImage(imgMarioRunning, edge.getFrom().lonX, edge.getFrom().latY, (1.0 / Math.sqrt(trans.determinant()) * 40), (1.0 / Math.sqrt(trans.determinant()) * 40));
                    } else if (counter == wayChangesInRoute.size()) { //END BOWSER
                        gc.drawImage(imgBowser, edge.getFrom().lonX, edge.getFrom().latY, (1.0 / Math.sqrt(trans.determinant()) * 40), (1.0 / Math.sqrt(trans.determinant()) * 40));
                    } else { //BETWEEN Enemies:
                        if (wayChangesInRoute.size() >= 5 && counter % 5 != 0) {
                            continue; //Don't print too many enemies.
                        }
                        if (counter % 4 == 0)
                            gc.drawImage(imgGoomba, edge.getFrom().lonX, edge.getFrom().latY, (1.0 / Math.sqrt(trans.determinant()) * 25), (1.0 / Math.sqrt(trans.determinant()) * 25));
                        else if (counter % 4 == 1)
                            gc.drawImage(imgFlowerFire, edge.getFrom().lonX, edge.getFrom().latY, (1.0 / Math.sqrt(trans.determinant()) * 25), (1.0 / Math.sqrt(trans.determinant()) * 25));
                        else if (counter % 4 == 2)
                            gc.drawImage(imgBulletBill, edge.getFrom().lonX, edge.getFrom().latY, (1.0 / Math.sqrt(trans.determinant()) * 25), (1.0 / Math.sqrt(trans.determinant()) * 25));
                        else if (counter % 4 == 3)
                            gc.drawImage(imgLakitu, edge.getFrom().lonX, edge.getFrom().latY, (1.0 / Math.sqrt(trans.determinant()) * 25), (1.0 / Math.sqrt(trans.determinant()) * 25));
                    }
                }
            }
        }

        time += System.nanoTime();
        controllerMediator.getDebugMenuController().setRepaintLabel(" " + (long) (time / 1e6));
        controllerMediator.getDebugMenuController().setInBoundingLabel(" " + renderedOnScreen);
        controllerMediator.getKdTreeDebugMenuController().updateRenderedFromQuery("" + renderedOnSceenAllQueries);

        renderedOnScreen = 0;
        renderedOnSceenAllQueries = 0;
        renderedOnScreenQueryWays = 0;
        renderedOnScreenMultipolygons = 0;
        renderedOnScreenCanvastexts = 0;
    }

    public void drawNNText(GraphicsContext gc, String s){
        float y = boundingBox.getMaxY()- (((boundingBox.getMaxY() - boundingBox.getMinY()) / 10));
        float x = boundingBox.getMinX() + (((boundingBox.getMaxX() - boundingBox.getMinX()) / 10));
        gc.beginPath();
        gc.setFont(new Font("Gill Sans", (1/Math.sqrt(trans.determinant())*16)));
        gc.fillText(s, x, y);
        gc.stroke();
    }



    public void drawScalaBar(GraphicsContext gc)  {
        float scalabarStartX = boundingBox.getMaxX()- (((boundingBox.getMaxX() - boundingBox.getMinX()) / 5));
        float scalabarEndX = boundingBox.getMaxX()- (((boundingBox.getMaxX() - boundingBox.getMinX()) / 3));
        float scalabarStartY = boundingBox.getMaxY()- (((boundingBox.getMaxY() - boundingBox.getMinY()) / 10));
        gc.beginPath();
        gc.setLineWidth(1/Math.sqrt(trans.determinant())*7);
        gc.setStroke(Color.rgb(20, 20, 20));
        gc.lineTo(scalabarStartX, scalabarStartY);
        gc.lineTo(scalabarEndX, scalabarStartY);
        gc.setFont(new Font("Gill Sans", (1/Math.sqrt(trans.determinant())*16)));
        gc.setFill(Color.BLACK);
        float meters = Utility.calculateDistance(scalabarStartX,scalabarStartY,scalabarEndX,scalabarStartY);
        String distanceOutput = " ";
        if (meters > 1000) {
            meters /= 1000;
            distanceOutput += String.format("%.2f", meters) + " km";
        } else {
            distanceOutput += (int)meters + " m";
        }
        gc.fillText(distanceOutput, scalabarStartX, scalabarStartY);
        gc.stroke();
    }

    public void convertCurrentScreenToBoundingBox() {
        //Credits til ex-TA Lasse Faurby for matematikken
        double minX = trans.getTx() / Math.sqrt(trans.determinant());
        double minY = (-trans.getTy()) / Math.sqrt(trans.determinant());
        double maxX = getWidth() - minX;
        double maxY = getHeight() - minY;
        Point2D toScreenCoordsMin = mouseToModel(new Point2D(minX, minY));
        Point2D toScreenCoordsMax = mouseToModel(new Point2D(maxX, maxY));
        //Sætter skærmens max til vores Boundingbox
        BoundingBox boundingBox = mapData.getBoundingBox();
        boundingBox.setMinX((float) toScreenCoordsMin.getX());
        boundingBox.setMaxX((float) toScreenCoordsMax.getX());
        boundingBox.setMinY((float) toScreenCoordsMin.getY());
        boundingBox.setMaxY((float) toScreenCoordsMax.getY());
        //Sætter maxBorder margin 20 %, så man kan se ting ikke blive tegnet i siden, og ved det virker
        boundingBox.reduceMaxBorder(((float) (toScreenCoordsMin.getX() - toScreenCoordsMax.getX()) / extraMargin), ((float) (toScreenCoordsMin.getY() - toScreenCoordsMax.getY()) / extraMargin));
    }

    //zoomer til center af addressen.
    public void panToAddress(float x, float y) {

        BoundingBox boundingBox = mapData.getBoundingBox();
        double panX = (((boundingBox.getMaxX() + boundingBox.getMinX()) / 2) - x) * zoomInt;
        double panY = (((boundingBox.getMaxY() + boundingBox.getMinY()) / 2) - y) * zoomInt;
        pan(panX, panY);
    }

    public void pan(double dx, double dy) {

        trans.prependTranslation(dx, dy);
        repaint();
    }

    public void zoom(double factor, double x, double y) {

        trans.prependTranslation(-x, -y);
        trans.prependScale(factor, factor);
        trans.prependTranslation(x, y);
        zoomInt *= factor;
        repaint();
    }

    public Point2D mouseToModel(Point2D point) {
        try {
            return trans.inverseTransform(point);
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException(e);
        }
    }

    public static double getZoomInt() {
        return zoomInt;
    }

    public static boolean renderZoomLevel(DrawCoordinate dc) {
        if(dc instanceof CanvasText c){
            return c.getZoomRequired() < getZoomInt();
        }
        else {
            if (ignoreZoomslevels) return true;
            return dc.drawType.getZoomRequired() < getZoomInt();
        }
    }

    public static boolean renderZoomLevel(DrawType drawType) {
        if (ignoreZoomslevels) return true;
        return drawType.getZoomRequired() < getZoomInt();
    }

    public boolean renderZoomLevelText(CanvasText canvasText) {
        //if (ignoreZoomslevels) return true;
        return canvasText.getZoomRequired() < getZoomInt();
    }
}