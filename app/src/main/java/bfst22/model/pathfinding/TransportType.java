package bfst22.model.pathfinding;

import bfst22.model.graphiccomponents.DrawCoordinate;
import bfst22.model.graphiccomponents.DrawType;

import java.util.Arrays;
import java.util.HashSet;

import static bfst22.model.graphiccomponents.DrawType.*;
import static bfst22.controller.AddressMenuController.transportTypeChosen;

/**
 * Class assist filtering of transportable roads, used for pathfinding and nearest neighbour
 */
public enum TransportType {
    WALK(
            new HashSet<>(Arrays.asList(
                    PRIMARY, SECONDARY, TERTIARY, QUATERNARY, RESIDENTIALROAD, SERVICEROAD, LIVINGROAD,
                    PATH, STEPS, HIGHWAYTRACK, PLATFORM, PEDESTRIAN, HIGHWAYTRACK, CYCLEWAY, FERRY
            ))
    ),
    BICYCLE(
            new HashSet<>(Arrays.asList(
                    PRIMARY, SECONDARY, TERTIARY, QUATERNARY, RESIDENTIALROAD, SERVICEROAD, LIVINGROAD,
                    BRIDLEWAY, CYCLEWAY, PATH
            ))
    ),
    CAR(
            new HashSet<>(Arrays.asList(
                    PRIMARY, SECONDARY, TERTIARY, QUATERNARY, RESIDENTIALROAD, SERVICEROAD, LIVINGROAD,
                    MOTORWAY, MOTORWAYLINK, TRUNK
            ))
    );

    /**
     * List of allowed roads for the transport type.
     */
    private final HashSet<DrawType> allowedRoads;

    TransportType(HashSet<DrawType> allowedRoads) {
        this.allowedRoads = allowedRoads;
    }

    public boolean transportAllowed(DrawType query) {
        return this.allowedRoads.contains(query);
    }

    /**
     * Used for KD-Tree Nearest Neighbour search
     * @return List of allowed roads for the transport type.
     */
    public static boolean transportAllowedDrawCoordinate(DrawCoordinate dc) {
        return transportTypeChosen.allowedRoads.contains(dc.drawType);

    }

}
