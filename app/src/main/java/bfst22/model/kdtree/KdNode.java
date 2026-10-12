package bfst22.model.kdtree;

import bfst22.model.graphiccomponents.DrawCoordinate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * The nodes that make up the KdTree
 */
public class KdNode<Key extends DrawCoordinate> implements Serializable {
    public static final long serialVersionUID = 8979546123L;
    private final Set<Key> keys;
    public KdNode<Key> right;
    public KdNode<Key> left;
    public float splittingValue;
    public int depth;
    public boolean isLeafNode;

    /**
     * Initializes a KdNode with its left and right children being null
     */
    public KdNode() {
        keys = new HashSet<>();
        right = null;
        left = null;
        depth = 0;
        splittingValue = 0;
        isLeafNode = false;
    }

    public void addKey(Key key) {
        keys.add(key);
    }

    public Set<Key> getKeys() {
        return keys;
    }
}
