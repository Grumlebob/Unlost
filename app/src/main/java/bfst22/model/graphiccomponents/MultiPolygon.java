package bfst22.model.graphiccomponents;

import bfst22.model.osm.OSMNode;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.*;

/**
 * MultiPolygon extends DrawCoordinate, and consists of a list of ways or "rings".
 */
public class MultiPolygon extends DrawCoordinate implements Serializable {
    public static final long serialVersionUID = 1325234;
    List<Way> members;
    public ArrayList<ArrayList<Float>> coordsList;

    /**
     * Sets the DrawType to default. Members is initialized and the helper method assignMultiPolygonRings is called.
     * The coords are also initialized.
     * @param rel The parsed members of this MultiPolygon relation.
     */
    public MultiPolygon(ArrayList<Way> rel) {
        drawType = DrawType.DEFAULT;
        members = new ArrayList<>();
        assignMultipolygonRings(rel);

        //If they are null, max values won't get updated. Therefore init with max values of first node in the osmway.
        maxY = rel.get(0).getMaxY();
        maxX = rel.get(0).getMaxX();
        minY = rel.get(0).getMinY();
        minX = rel.get(0).getMinX();

        coordsList = new ArrayList<>();

        initCoords();
    }

    /**
     * This loops through the relationmembers and checks whether they are connected to any of the other.
     * If they are connected they are appended to each other and reversed as needed.
     * If a way has the same start and end node, it is considered a complete ring, and added to the members list.
     *
     * @param rel the RelationMembers parsed from the MapDataParser.
     */
    private void assignMultipolygonRings(ArrayList<Way> rel) {
        boolean[] complete = new boolean[rel.size()];

        for (int i = 0; i < rel.size(); i++) {
            if (complete[i]) continue;

            Way way = new Way(new ArrayList<>(rel.get(i).getNodes()));
            OSMNode firstNode = way.getNodes().get(0);
            OSMNode lastNode = way.getNodes().get(way.getNodes().size() - 1);

            if (firstNode.equals(lastNode)) {
                members.add(way);
                complete[i] = true;
                continue;
            }
            int itemsChanged = 1;
            while(!firstNode.equals(lastNode) && itemsChanged > 0) {
                itemsChanged = 0;
                for (int j = 0; j < rel.size(); j++) {
                    if(i == j || complete[j]) continue;

                    firstNode = way.getNodes().get(0);
                    lastNode = way.getNodes().get(way.getNodes().size() - 1);

                    Way otherWay = new Way(new ArrayList<>(rel.get(j).getNodes()));
                    OSMNode otherFirstNode = otherWay.getNodes().get(0);
                    OSMNode otherLastNode = otherWay.getNodes().get(otherWay.getNodes().size() - 1);

                    if (lastNode.equals(otherFirstNode)) {
                        way.append(way, otherWay);
                        complete[j] = true;
                        itemsChanged++;
                    } else if (lastNode.equals(otherLastNode)) {
                        otherWay.reverse();
                        way.append(way, otherWay);
                        complete[j] = true;
                        itemsChanged++;
                    } else if (firstNode.equals(otherFirstNode)) {
                        otherWay.reverse();
                        way.append(otherWay, way);
                        complete[j] = true;
                        itemsChanged++;
                    } else if (firstNode.equals(otherLastNode)) {
                        way.append(otherWay, way);
                        complete[j] = true;
                        itemsChanged++;
                    }
                }
                firstNode = way.getNodes().get(0);
                lastNode = way.getNodes().get(way.getNodes().size() - 1);

                if (firstNode.equals(lastNode)) {
                    members.add(way);
                    complete[i] = true;
                }
            }
        }
    }

    /**
     * Utilizes a TreeMap as a frequencyCounter. Finds the most frequent DrawType of the multipolygons relationmembers.
     *
     * @param rel The members from the MapDataParser.
     * @return Returns the most frequent DrawType.
     */
    public DrawType findDrawType(ArrayList<Way> rel) {
        TreeMap<DrawType, Integer> drawTypeCounter = new TreeMap<>();
        for (Way way : rel) {
            DrawType wayDrawType = way.drawType;
            if (wayDrawType == DrawType.DEFAULT) continue;
            if (drawTypeCounter.containsKey(wayDrawType)) {
                drawTypeCounter.put(wayDrawType, drawTypeCounter.get(wayDrawType) + 1);
            } else drawTypeCounter.put(wayDrawType, 1);
        }
        if (!drawTypeCounter.isEmpty()) {
            DrawType maxDrawType = DrawType.DEFAULT;
            int max = drawTypeCounter.values().stream().max(Integer::compare).get();
            for (Map.Entry<DrawType, Integer> entry : drawTypeCounter.entrySet()) {
                if(entry.getValue() == max) maxDrawType = entry.getKey();
            }

            if (drawTypeCounter.get(maxDrawType) > 0) return maxDrawType;
        }
        return DrawType.DEFAULT;
    }

    /**
     * Initializes the coords based on the members (rings).
     */
    private void initCoords() {
        for (int i = 0; i < members.size(); i++) {
            coordsList.add(new ArrayList<>());
            if (maxY < members.get(i).getMaxY()) {
                maxY = members.get(i).getMaxY();
            }
            if (maxX < members.get(i).getMaxX()) {
                maxX = members.get(i).getMaxX();
            }
            if (minY > members.get(i).getMinY()) {
                minY = members.get(i).getMinY();
            }
            if (minX > members.get(i).getMinX()) {
                minX = members.get(i).getMinX();
            }
            for (int j = 0; j < members.get(i).getCoords().length; j++)
                coordsList.get(i).add(members.get(i).getCoords()[j]);
        }
        members = null;
    }

    @Override
    public float[] getCoords() {
        return coords;
    }

    /**
     * For each ring, a new path is traced. MultiPolygons are always areas, and can therefore always be filled.
     * @param gc The graphic context of the MapCanvas
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.beginPath();
        for (ArrayList<Float> wayCoords : coordsList) {
            for (int i = 0; i < wayCoords.size(); i += 2) {
                float x = wayCoords.get(i);
                float y = wayCoords.get(i + 1);

                if (i == 0) gc.moveTo(x, y);
                else gc.lineTo(x, y);
            }
        }
        gc.fill();
    }

    /**
     * Each list in the iterator represent a ring.
     * @return an iterator of lists of floats.
     */
    @Override
    public Iterator<List<Float>> iterator() {
        return new Iterator<>() {
            int index = 0;
            @Override
            public boolean hasNext() {
                return index < coordsList.size();
            }

            @Override
            public List<Float> next() {
                return coordsList.get(index++);
            }
        };
    }

    public DrawType getDrawType() {
        return drawType;
    }

    public void setDrawType(DrawType drawtype) {
        this.drawType = drawtype;
    }
}
