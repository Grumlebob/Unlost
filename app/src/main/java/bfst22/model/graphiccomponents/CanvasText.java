package bfst22.model.graphiccomponents;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static bfst22.view.MapCanvas.zoomInt;


public class CanvasText extends DrawCoordinate implements Serializable {

    @Serial
    private static final long serialVersionUID = 4567865L;

    private final String text;
    private final String place;

    public CanvasText(String text, String place) {
        this.text = text;
        this.place = place;
    }

    public void setCoords(float x, float y) {
        this.coords = new float[]{x,y};
        minX = x;
        maxX = x;
        maxY = y;
        minY = y;
    }

    /**
     * https://wiki.openstreetmap.org/wiki/OSM_tags_for_routing
     * @return zoom level required to be rendered on screen
     */
    public int getZoomRequired() {
        if (this.place == null) return 150000;
        return switch (this.place) {
            case "continent" -> zoomInt > 25000 ? Integer.MAX_VALUE : 1000;
            case "country" -> zoomInt > 25000 ? Integer.MAX_VALUE : 1000;
            case "state" -> zoomInt > 25000 ? Integer.MAX_VALUE : 1000;
            case "region" -> zoomInt > 25000 ? Integer.MAX_VALUE : 1000; // 500 km
            case "county" -> zoomInt > 25000 ? Integer.MAX_VALUE : 1000; // 200 km
            case "city" -> zoomInt > 25000 ? Integer.MAX_VALUE : 2500; // 10 km
            case "town" -> zoomInt > 25000 ? Integer.MAX_VALUE : 5000; // 5 km
            case "village" -> zoomInt > 25000 ? Integer.MAX_VALUE : 7500; //1 km
            case "hamlet" -> zoomInt > 35000 ? Integer.MAX_VALUE : 7500; //300 m
            case "isolated_dwelling" -> zoomInt > 50000 ? Integer.MAX_VALUE : 10000;
            case "islet" -> zoomInt > 50000 ? Integer.MAX_VALUE : 10000;
            case "island" -> zoomInt > 50000 ? Integer.MAX_VALUE : 10000;
            case "suburb" -> zoomInt > 50000 ? Integer.MAX_VALUE : 10000;
            case "plot" -> zoomInt > 75000 ? Integer.MAX_VALUE : 25000;
            case "neighbourhood" -> zoomInt > 75000 ? Integer.MAX_VALUE : 25000;
            case "farm" -> zoomInt > 75000 ? Integer.MAX_VALUE : 25000;
            case "quarter" -> zoomInt > 75000 ? Integer.MAX_VALUE : 25000;
            case "locality" -> zoomInt > 75000 ? Integer.MAX_VALUE : 25000;
            default -> 150000;
        };
    }

    @Override
    public float[] getCoords() {
        return coords;
    }


    public void drawScalingText(GraphicsContext gc, double scaling) {
        double multiplier;
        if (zoomInt >75000) multiplier = 12;
        else {
            multiplier = switch (this.place) {
                case "continent" -> 25;
                case "country" -> 25;
                case "state" -> 25;
                case "island" -> 25;
                case "region" -> 25; // 500 km
                case "county" -> 25; // 200 km
                case "city" -> 25; // 10 km
                case "town" -> 20; // 5 km
                case "village" -> 20; //1 km
                case "hamlet" -> 15; //300 m
                case "isolated_dwelling" -> 15;
                case "islet" -> 15;
                case "plot" -> 15;
                case "neighbourhood" -> 15;
                case "farm" -> 15;
                case "quarter" -> 15;
                case "suburb" -> 15;
                case "locality" -> 15;
                default -> 15;
            };
        }
        gc.setFont(new Font("Gill Sans", scaling*multiplier));
        gc.setFill(Color.BLACK);
        gc.fillText(text, maxX, maxY);
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
                List<Float> coordsList = new ArrayList<>();
                for (float coord : coords) {
                    coordsList.add(coord);
                }
                return coordsList;
            }
        };
    }

    @Override
    public String getText() {
        return text;
    }
}
