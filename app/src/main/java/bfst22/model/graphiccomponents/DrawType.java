package bfst22.model.graphiccomponents;

import javafx.scene.paint.Color;

public enum DrawType {

    //Those in the top are drawn first. It's not good to have building in the top, because other things might be drawn upon it
    //NON-CITY AREAS - FILL
    COASTLINE(Color.rgb(242, 239, 233), 0.00003f, 0.0, true, 1),
    WATER(Color.rgb(170, 211, 223), 0.00003f, 0.0, true, 4),
    WOOD(Color.rgb(130, 158, 119), 0.00003f, 0.0, true, 2),
    FOREST(Color.rgb(173, 209, 158), 0.00003f, 0.0, true, 2),
    GRASSLAND(Color.rgb(205, 235, 176), 0.00003f, 0.0, true, 3),
    SCRUB(Color.rgb(200, 215, 171), 0.00003f, 0.0, true, 3),
    HEATH(Color.rgb(214, 217, 159), 0.00003f, 0.0, true, 2),
    BEACH(Color.rgb(255, 241, 186), 0.00003f, 0.0, true, 2),
    MUD(Color.rgb(255, 241, 186), 0.00003f, 0.0, true, 5),
    WETLANDS(Color.rgb(173, 220, 158), 0.00003f, 0.0, true, 5),
    //CITY AREAS - FILL
    RESIDENTIAL(Color.rgb(225, 225, 225), 0.00003f, 0.0, true, 2),
    COMMERCIAL(Color.rgb(255, 213, 208), 0.00003f, 0.0, true, 2),
    INDUSTRIAL(Color.rgb(235, 219, 233), 0.00003f, 0.0, true, 2),
    CONSTRUCTION(Color.rgb(199, 199, 180), 0.00003f, 0.0, true, 2),
    CEMETERY(Color.rgb(139, 184, 144), 0.00003f, 0.0, true, 2),
    QUARRY(Color.rgb(197, 195, 195), 0.00003f, 0.0, true, 2),
    SPORTSCENTRE(Color.rgb(223, 252, 226), 0.00003f, 0.0, true, 2),
    FARMLAND(Color.rgb(238, 240, 213), 0.00003f, 0.0, true, 2),
    LEISURE(Color.rgb(224, 252, 227), 0.00003f, 0.0, true, 2),
    NATURERESERVE(Color.rgb(123, 187, 162, 0.2f), 0.00003f, 0.0, false, 2),
    LANDUSERARE(Color.ROSYBROWN, 0.00003f, 0.0, true, 2),
    BRIDGE(Color.rgb(184, 184, 184), 0.000010f, 0.0, true, 2),
    PITCH(Color.rgb(170, 224, 203), 0.00003f, 0.0, true, 2),
    //TRANSPORTATION AREA - FILL
    PARKING(Color.rgb(238, 238, 238), 0.00003f, 0.0, true, 9),
    BUSSTATION(Color.rgb(233, 231, 226), 0.00003f, 0.0, true, 10),
    // --- LINES START HERE --
    //MISC LINES - NO ENERGY SUPPLIES, SEEMS RARE
    PLATFORM(Color.rgb(181, 181, 182), 0.00005f, 0.0, false, 9),
    PARKINGLINE(Color.rgb(181, 181, 182), 0.0000005f, 0.0, false, 9),
    BARRIER(Color.rgb(194, 192, 189), 0.00002f, 0.0, false, 9),
    HEDGE(Color.rgb(167, 207, 153), 0.00002f, 0.0, false, 9),
    WATERBARRIER(Color.rgb(171, 172, 172), 0.00002f, 0.0, false, 9),
    POWERLINE(Color.rgb(156, 156, 155), 0.000012f, 0.0, false, 9),
    //WATERWAYS
    RIVERWAY(Color.rgb(171, 212, 224), 0.00005f, 0.0, false, 7),
    FERRY(Color.rgb(102, 102, 255), 0.000005f, 0.000010, false, 5),
    //RAILWAYS
    RAIL(Color.rgb(123, 123, 123), 0.000005f, 0.000010, false, 5),
    SUBWAY(Color.rgb(100, 100, 100, 0.5), 0.000012f, 0.00010, false, 5), //Skal måske tegnes sent
    //Roads and ways for non-motorized vehicles
    PIER(Color.rgb(242, 239, 233), 0.00005f, 0.0, false, 10),
    RACEWAY(Color.rgb(255, 192, 202), 0.00005f, 0.0, false, 11),
    AEROWAY(Color.rgb(187, 187, 204), 0.00015f, 0.0, false, 5),
    LEISURETRACK(Color.rgb(170, 224, 203), 0.00003f, 0.000002, false, 11),
    HIGHWAYTRACK(Color.rgb(201, 162, 85), 0.00002f, 0.00006, false, 9),
    STEPS(Color.rgb(250, 138, 123), 0.000006f, 0.000009, false, 9),
    PATH(Color.rgb(248, 169, 159), 0.000003f, 0.000009, false, 9),
    CYCLEWAY(Color.rgb(69, 69, 250), 0.000002f, 0.00001, false, 8),
    BRIDLEWAY(Color.rgb(92, 171, 90), 0.00003f, 0.00010, false, 7),
    //CITY ROADS - LINES
    PEDESTRIAN(Color.rgb(221, 221, 233), 0.00003f, 0.0, false, 12),
    LIVINGROAD(Color.rgb(237, 237, 237), 0.00003f, 0.0, false, 12),
    SERVICEROAD(Color.rgb(254, 254, 254), 0.00003f, 0.0, false, 12),
    RESIDENTIALROAD(Color.rgb(254, 254, 254), 0.00005f, 0.0, false, 12),
    ///MAJOR ROADS - LINES
    QUATERNARY(Color.rgb(254, 254, 254), 0.00005f, 0.0, false, 7),
    TERTIARY(Color.rgb(254, 254, 254), 0.00005f, 0.0, false, 7),
    SECONDARY(Color.rgb(249, 250, 202), 0.00005f, 0.0, false, 5),
    PRIMARY(Color.rgb(253, 221, 179), 0.00005f, 0.0, false, 4),
    TRUNK(Color.rgb(251, 192, 172), 0.0003f, 0.0, false, 3),
    MOTORWAYLINK(Color.rgb(233, 144, 161), 0.0002f, 0.0, false, 2),
    MOTORWAY(Color.rgb(233, 144, 161), 0.00013f, 0.0, false, 2),
    // --- LINES END HERE --
    //TOP PRIORITY STUFF - DRAW LAST:
    BUILDING(Color.rgb(216, 208, 201), 0.00003f, 0.0, true, 11),
    APARTMENTS(Color.rgb(216, 208, 201), 0.00005f, 0.0, true, 11),
    PEDESTRIANAREA(Color.rgb(221, 221, 232), 0.00003f, 0.0, true, 2),
    MILITARY(Color.rgb(255, 192, 202, 0.12), 0.00003f, 0, true, 3),
    DEFAULT(Color.rgb(241, 0, 240), 0.00001f, 0.00000, true, 5),

    //SHOULD NEVER BE DRAWN
    IGNORE(Color.rgb(0,0,0), 0.1f, 0.0, true, 1);

    private final Color color;
    private final float lineWidth;
    private final double lineDashes;
    private final boolean fill;
    private final int zoomRequired;

    DrawType(Color color, float lineWidth, double lineDashes, boolean fill, int zoomRequired) {
        this.color = color;
        this.lineWidth = lineWidth;
        this.lineDashes = lineDashes;
        this.fill = fill;
        this.zoomRequired = zoomRequired;
    }

    public Color getColor() {
        return color;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public double getLineDashes() {
        return lineDashes;
    }

    public boolean isFill() {
        return fill;
    }


    public int getZoomRequired() {
        return switch (zoomRequired) {
            case 1 -> 0;
            case 2 -> 100;
            case 3 -> 200;
            case 4 -> 320;
            case 5 -> 650;
            case 6 -> 1200;
            case 7 -> 2750;
            case 8 -> 5676;
            case 9 -> 11650;
            case 10 -> 24000;
            case 11 -> 38600;
            case 12 -> 90000;
            case 13 -> 170000;
            case 14 -> 375000;
            case 15 -> 870000;
            default -> Integer.MAX_VALUE;
        };
    }

}
