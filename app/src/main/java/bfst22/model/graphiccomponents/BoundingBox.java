package bfst22.model.graphiccomponents;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class BoundingBox extends DrawCoordinate implements Serializable {
    public static final long serialVersionUID = 12345676543L;

    public static float renderExtraMarginX;
    public static float renderExtraMarginY;
    public static boolean renderExtraMargin = false;

    public void setCoords(float maxY, float maxX, float minY, float minX) {
        //Used for drawing a MaxBorder around the whole osmLoad
        coords = new float[4];
        coords[0] = maxX;
        coords[1] = maxY;
        coords[2] = minX;
        coords[3] = minY;

        //Used for setting maxCoords on the current screen once
        //In the BoundingBox class MinX current screen coords are continuously updated
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public void reduceMaxBorder(float reduceX, float reduceY) {
        renderExtraMarginX = Math.abs(reduceX);
        renderExtraMarginY = Math.abs(reduceY);
        //Make maxBorder less than the screens maxBorder so that you can see things disappear
        if (renderExtraMargin) {
            minX = minX + renderExtraMarginX;
            maxX = maxX - renderExtraMarginX;
            minY = minY + renderExtraMarginY;
            maxY = maxY - renderExtraMarginY;
        }
    }


    @Override
    public void draw(GraphicsContext gc) {
        //Sets margins for our maxBorder
        //Drawn our maxBorder on the whole OSMLoad
        if (renderExtraMargin) {
            gc.setLineWidth(0.0005f);
            gc.setLineDashes(0);
            gc.beginPath();
            gc.setStroke(Color.rgb(50, 200, 255));
            gc.moveTo(coords[0], coords[1]); //right down
            gc.lineTo(coords[0], coords[3]); //right up
            gc.lineTo(coords[2], coords[3]); //left up
            gc.lineTo(coords[2], coords[1]); //left down
            gc.lineTo(coords[0], coords[1]); //right down
            gc.stroke();
            //Draws our maxBorder on the screen
            gc.beginPath();
            gc.setLineWidth(0.000004f);
            gc.setStroke(Color.rgb(180, 125, 255));
            gc.moveTo(maxX, maxY); //right down
            gc.lineTo(maxX, minY); //right up
            gc.lineTo(minX, minY); //left up
            gc.lineTo(minX, maxY); //left down
            gc.lineTo(maxX, maxY); //right down
            gc.stroke();
            //Writes text on the corner to show them
            gc.setFont(new Font("Verdana", 0.0001));
            gc.setFill(Color.BLACK);
            gc.fillText(("maxX \n" + maxX + "\n maxY\n" + maxY), maxX, maxY); //right down
            gc.fillText(("maxX \n" + maxX + "\n minY\n" + minY), maxX, minY); //right up
            gc.fillText(("minX \n" + minX + "\n maxY\n" + maxY), minX, maxY); //left down
            gc.fillText(("minX \n" + minX + "\n minY\n" + minY), minX, minY); //left up
        }
    }



    @Override
    public Iterator<List<Float>> iterator() {
        return null;
    }


    public boolean isInsideBoundingWith1Point(DrawCoordinate draw) {
        //So long as 1 of the point are true, then draw variabel has a coordinate in the bounding box and should be drawn
        if (minX < draw.getMinX() && draw.getMinX() < maxX) {
            if (minY < draw.getMinY() && draw.getMinY() < maxY) return true;
            if (minY < draw.getMaxY() && draw.getMaxY() < maxY) return true;
        }
        if (minX < draw.getMaxX() && draw.getMaxX() < maxX) {
            if (minY < draw.getMinY() && draw.getMinY() < maxY) return true;
            if (minY < draw.getMaxY() && draw.getMaxY() < maxY) return true;
        }
        return false;
    }

    public static void setRenderExtraMargin(boolean renderExtraMargin) {
        BoundingBox.renderExtraMargin = renderExtraMargin;
    }

    public static boolean isRenderExtraMargin() {
        return renderExtraMargin;
    }

    @Override
    public float[] getCoords() {
        return coords;
    }

}
