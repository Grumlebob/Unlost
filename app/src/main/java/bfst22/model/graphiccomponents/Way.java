package bfst22.model.graphiccomponents;

import bfst22.model.osm.OSMNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Way extends DrawCoordinate implements Serializable {
    public static final long serialVersionUID = 4212345L;
    private List<OSMNode> nodes;
    public final String wayName;
    boolean partOfMultiPolygon;

    //Pathfinding:
    public final boolean walkAllowed;
    public final boolean bicycleAllowed;
    public final boolean onewayCar;
    public final boolean onewayBicycle;
    public final boolean access;
    public final float maxSpeed;
    public int speedReducingItems;


    public Way(
            List<OSMNode> nodes,
            DrawType drawType,
            String wayName,
            float maxSpeed,
            int speedReducingItems,
            boolean walkAllowed,
            boolean bicycleAllowed,
            boolean onewayCar,
            boolean onewayBicycle,
            boolean access
    ) {
        this.nodes = new ArrayList<>(nodes);
        this.drawType = drawType;
        this.wayName = wayName;
        this.maxSpeed = calculateMaxSpeedAfterSpeedReduction(maxSpeed);
        this.speedReducingItems = speedReducingItems;
        this.walkAllowed = walkAllowed;
        this.bicycleAllowed = bicycleAllowed;
        this.onewayCar = onewayCar;
        this.onewayBicycle = onewayBicycle;
        this.access = access;
        setCoords();
    }

    /**
     * Overloaded constructor made for creating multipolygons where the extra information is useless
     *
     */
    public Way(List<OSMNode> nodes) {
        this.nodes = nodes;
        this.drawType = DrawType.DEFAULT;
        this.wayName = null;
        this.maxSpeed = 0.0f;
        this.speedReducingItems = 0;
        this.walkAllowed = false;
        this.bicycleAllowed = false;
        this.onewayCar = false;
        this.onewayBicycle = false;
        this.access = false;
        setCoords();
    }


    @Override
    public float[] getCoords() {
        return coords;
    }

    public String getWayName() {
        return wayName == null ? "Unnamed road" : wayName;

    }


    public void drawWayText(GraphicsContext gc) {
        if (wayName != null) {
            gc.setFont(new Font("Verdana", 0.00004f));
            gc.setFill(Color.BLACK);
            gc.fillText(wayName, coords[0], coords[1]);
        }
    }


    public void drawNNWayWithStats(GraphicsContext gc, boolean allInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(wayName);
        if (allInfo) {
            sb.append("\nDrawtype: ").append(String.valueOf(drawType).toLowerCase());
            sb.append("\nMaxspeed: ").append(maxSpeed);
            sb.append("\nWalkAllowed: ").append(walkAllowed);
            sb.append("\nBicycleAllowed: ").append(bicycleAllowed);
            sb.append("\nisOnewayCar: ").append(onewayCar);
            sb.append("\nisOnewayBicycle: ").append(onewayBicycle);
            sb.append("\nAccess: ").append(access);
            sb.append("\nSpeed reducing items: ").append(speedReducingItems);
        }
        gc.setFont(new Font("Verdana", 0.00008f));
        gc.setFill(Color.BLACK);
        gc.fillText(sb.toString(), coords[0], coords[1]);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.beginPath();
        gc.moveTo(coords[0], coords[1]);
        for (var i = 2; i < coords.length; i += 2) {
            gc.lineTo(coords[i], coords[i + 1]);
        }
        if (drawType.isFill()) gc.fill();
        else gc.stroke();
    }


    public void drawBorder(GraphicsContext gc) {
        gc.setLineWidth(0.000005f);
        gc.setStroke(Color.rgb(100, 200, 150));
        gc.beginPath();
        gc.moveTo(maxX, maxY); //right down
        gc.lineTo(maxX, minY); //right up
        gc.lineTo(minX, minY); //left up
        gc.lineTo(minX, maxY); //left down
        gc.lineTo(maxX, maxY); //right down
        gc.stroke();
    }

    @Override
    public Iterator<List<Float>> iterator() {
        return new Iterator<>() {
            boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public List<Float> next() {
                hasNext = false;
                List<Float> coordList = new ArrayList<>();
                for (float coord : coords) {
                    coordList.add(coord);
                }
                return coordList;
            }
        };
    }

    public void setCoords() {
        int counter = 0;
        coords = new float[nodes.size() * 2];
        //If they are null, max values won't get updated. Therefore init with max values of first node in the osmway.
        maxY = nodes.get(0).latY;
        maxX = nodes.get(0).lonX;
        minY = nodes.get(0).latY;
        minX = nodes.get(0).lonX;
        for (OSMNode node : nodes) {
            maxX = Float.max(maxX, node.lonX);
            maxY = Float.max(maxY, node.latY);
            minX = Float.min(minX, node.lonX);
            minY = Float.min(minY, node.latY);

            coords[counter++] = node.lonX;
            coords[counter++] = node.latY;
            //If first init, set maxcoords, so they can be updated, if higher coords are found.
            if (counter == 2) {
                maxY = coords[1];
                maxX = coords[0];
                minY = coords[1];
                minX = coords[0];
            }
        }
    }

    public float calculateMaxSpeedAfterSpeedReduction(float maxSpeed) {
        if (speedReducingItems <= 5) {
            return maxSpeed * (1 - (speedReducingItems * 0.05f));
        } else {
            return maxSpeed * 0.75f;
        }
    }

    public List<OSMNode> getNodes() {
        return nodes;
    }

    public void append(Way thisWay, Way thatWay) {
        List<OSMNode> appendedNodes = new ArrayList<>();
        appendedNodes.addAll(thisWay.getNodes());
        appendedNodes.addAll(thatWay.getNodes());

        nodes = appendedNodes;
        setCoords();
    }

    public void reverse() {
        Collections.reverse(nodes);
    }

    @Override
    public String getText() {
        return wayName;
    }

    public void setPartOfMultiPolygon(boolean partOfMultiPolygon) {
        this.partOfMultiPolygon = partOfMultiPolygon;
    }

    @Override
    public boolean isPartOfMultiPolygon() {
        return partOfMultiPolygon;
    }
}
