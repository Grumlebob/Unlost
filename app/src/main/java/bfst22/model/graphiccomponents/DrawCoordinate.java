package bfst22.model.graphiccomponents;

import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class DrawCoordinate implements Comparable<DrawCoordinate>, Serializable {
    public static final long serialVersionUID = 345827659L;

    protected float minX, maxX, minY, maxY;
    protected float[] coords;
    public DrawType drawType;

    public float[] getCoords(){return null;}

    public void draw(GraphicsContext gc){}

    public Iterator<List<Float>> iterator(){return null;}

    public float getMinX() {
        return minX;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public void setCoords(float[] coordinates) {
        this.coords = coordinates;
    }

    public String getText() {
        return "text";
    }
    public boolean isPartOfMultiPolygon() {
        return false;
    }

    @Override
    public int compareTo(DrawCoordinate other){
        return Integer.compare(this.drawType.ordinal(), other.drawType.ordinal());
    }

}
