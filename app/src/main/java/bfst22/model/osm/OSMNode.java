package bfst22.model.osm;

import java.io.Serializable;
import java.util.Objects;

public class OSMNode implements Serializable {
    public static final long serialVersionUID = 9082413L;
    final public float latY, lonX;

    public OSMNode(float lonX, float latY) {
        this.lonX = lonX;
        this.latY = latY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latY, lonX);
    }

    @Override
    public boolean equals(Object that) {
        if (that == null) return false;
        if (that.getClass() != this.getClass()) return false;
        return this.lonX == ((OSMNode) that).lonX && this.latY == ((OSMNode) that).latY;
    }

}
