package bfst22.model.osm;

import bfst22.controller.SplashScreenController;
import bfst22.model.MapData;
import bfst22.model.graphiccomponents.DrawCoordinate;
import bfst22.model.graphiccomponents.DrawType;
import bfst22.model.address.AddressBook;
import bfst22.model.address.Address;
import bfst22.model.graphiccomponents.CanvasText;
import bfst22.model.graphiccomponents.Way;
import bfst22.model.graphiccomponents.MultiPolygon;
import bfst22.model.kdtree.KdTree;
import bfst22.model.graphiccomponents.BoundingBox;
import bfst22.model.pathfinding.Digraph;
import bfst22.model.pathfinding.Dijkstra;
import bfst22.model.pathfinding.TransportType;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import static bfst22.model.graphiccomponents.DrawType.*;

public class MapDataParser {

    MapData mapData = MapData.getInstance();
    public static int drawtypesLoaded = 0;
    public static long totalLoadingTime = 0;
    private int numberOfTransportableWays = 0;
    private int numberOfTransportableNodes = 0;

    public MapDataParser(String filename) throws IOException, XMLStreamException, FactoryConfigurationError, ClassNotFoundException {
        var time = -System.nanoTime();

        if (filename.endsWith(".zip")) {
            var zip = new ZipInputStream(new FileInputStream(filename));
            var entry = zip.getNextEntry();
            if (entry == null) throw new IllegalArgumentException("No file could be found");
            if (entry.getName().endsWith(".map"))
                loadOBJ(new ObjectInputStream(zip));
            else if (entry.getName().endsWith(".osm"))
                loadOSM(zip);

        } else if (filename.endsWith(".osm")) {
            loadOSM(new FileInputStream(filename));
        } else if (filename.endsWith(".map")) {
            try (var input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
                loadOBJ(input);
            }
        }

        time += System.nanoTime();
        System.out.println("Time to load: "+(time / 1e6));
        System.out.println("number Of Transportable Ways = " + numberOfTransportableWays);
        System.out.println("number Of Transportable Nodes = " + numberOfTransportableNodes);
        totalLoadingTime = (long) (time / 1e6);
        //System.out.println("CanvasTextsSize: " + mapData.getCanvasTexts().size());
        //System.out.println("address size: " + mapData.getAddressBook().size());
        //System.out.println("drawtype Map: " + drawtypesLoaded);
//        if (!filename.endsWith(".map")) save(filename);
    }

    public void save(String basename) throws IOException {
        String name = basename.split("[.]")[0];

        var fileOut = new FileOutputStream(name +".map");

        var out = new ObjectOutputStream(fileOut);

        long time = -System.nanoTime();
        System.out.println("Initialising saving the file");
        out.writeObject(mapData.getMultiPolygonMap());
        out.writeObject(mapData.getWayMap());
        out.writeObject(mapData.getCoastlines());
        out.writeObject(mapData.getCanvasTexts());

        out.writeObject(mapData.getKdTreeWays());
        out.writeObject(mapData.getKdTreeMultiPolygons());
        out.writeObject(mapData.getKdTreeCanvasTexts());
        out.writeObject(mapData.getKdTreeTransportableRoads());

        out.writeObject(mapData.getAddressBook());
        out.writeObject(mapData.getBoundingBox());
        out.writeFloat(mapData.getMinX());
        out.writeFloat(mapData.getMinY());
        out.writeFloat(mapData.getMaxX());
        out.writeFloat(mapData.getMaxY());

        //constructor ends here
        out.writeObject(mapData.getGraph());
        out.writeObject(mapData.getPathfinder());
        time += System.nanoTime();
        System.out.println("Everything saved successfully! it took " +(time/1e6)+ "ms");


    }

    private void loadOBJ(ObjectInputStream in) throws IOException, ClassNotFoundException {
        mapData.init(
                (Map<DrawType, List<MultiPolygon>>) in.readObject(),
                (Map<DrawType, List<Way>>) in.readObject(),
                (List<DrawCoordinate>) in.readObject(),
                (List<CanvasText>) in.readObject(),
                (KdTree<Way>) in.readObject(),
                (KdTree<MultiPolygon>) in.readObject(),
                (KdTree<CanvasText>) in.readObject(),
                (KdTree<Way>) in.readObject(),
                (AddressBook) in.readObject(),
                (BoundingBox) in.readObject(),
                in.readFloat(),
                in.readFloat(),
                in.readFloat(),
                in.readFloat()
        );
        mapData.setGraph((Digraph) in.readObject());
        mapData.setPathfinder((Dijkstra) in.readObject());

    }

    private void loadOSM(InputStream input) throws XMLStreamException, FactoryConfigurationError {
        var reader = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedInputStream(input));

        Map<DrawType, List<Way>> wayMap = new EnumMap<>(DrawType.class);
        Map<DrawType, List<MultiPolygon>> multiPolygonMap = new EnumMap<>(DrawType.class);
        for (var type : DrawType.values()) {
            wayMap.put(type, new ArrayList<>());
            multiPolygonMap.put(type, new ArrayList<>());
        }
        List<DrawCoordinate> coastlines = new ArrayList<>();
        List<CanvasText> canvasTexts = new ArrayList<>();
        AddressBook addressBook = new AddressBook();
        HashSet<OSMNode> speedReducingItems = new HashSet<>();

        KdTree<CanvasText> kdTreeCanvasTexts;
        KdTree<Way> kdTreeWays;
        KdTree<MultiPolygon> kdTreeMultiPolygon;
        KdTree<Way> kdTreeTransportableRoads;

        BoundingBox boundingBox = new BoundingBox();

        float minY = 0, minX = 0, maxY = 0, maxX = 0;


        Map<Long, OSMNode> id2node = new HashMap<>();
        var id2way = new HashMap<Long, Way>();
        var nodes = new ArrayList<OSMNode>();

        var relationMembers = new ArrayList<Way>();
        long relID = 0;
        float maxSpeed = 0f;
        var drawType = DEFAULT;
        boolean isMultipolygon = false;

        OSMNode currentNode = null;

        String canvasTextName = null;
        String canvasTextPlace = null;

        Address.Builder addressBuilder = null;

        //For pathfinding
        boolean walkAllowed = true;
        boolean bicycleAllowed = true;
        boolean onewayCar = false;
        boolean onewayBicycle = false;
        boolean access = true;

        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    var name = reader.getLocalName();

                    switch (name) {
                        case "bounds":
                            maxY = -Float.parseFloat(reader.getAttributeValue(null, "minlat"));//MENINGEN DET ER BYTTET RUNDT!
                            minX = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "minlon"));
                            minY = -Float.parseFloat(reader.getAttributeValue(null, "maxlat")); //MENINGEN DET ER BYTTET RUNDT!!
                            maxX = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "maxlon"));
                            boundingBox.setCoords(maxY, maxX, minY, minX);
                            break;
                        case "node":
                            var id = Long.parseLong(reader.getAttributeValue(null, "id"));
                            var lat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
                            var lon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
                            currentNode = new OSMNode(0.56f * lon, -lat);
                            id2node.put(id, currentNode);
                            break;
                        case "nd":
                            var ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            nodes.add(id2node.get(ref));
                            break;
                        case "way":
                            relID = Long.parseLong(reader.getAttributeValue(null, "id"));
                            break;
                        case "tag":
                            String key = reader.getAttributeValue(null, "k");
                            String value = reader.getAttributeValue(null, "v");

                            if (key.startsWith("addr:")) {
                                if (addressBuilder == null) {
                                    addressBuilder = new Address.Builder();
                                }
                            }
                            if ("name".equals(key)) {
                                canvasTextName = value;
                            }

                            if ("place".equals(key)) {
                                canvasTextPlace = value;
                            }
                            switch (key) {
                                case "addr:street":
                                    addressBuilder.street(value);
                                    break;
                                case "addr:housenumber":
                                    addressBuilder.house(value);
                                    break;
                                case "addr:city":
                                    addressBuilder.city(value);
                                    break;
                                case "addr:postcode":
                                    addressBuilder.postcode(value);
                                    break;
                                case "type":
                                    if ("multipolygon".equals(value)) {
                                        isMultipolygon = true;
                                    }
                                    break;
                                case "place":
                                    drawType = switch (value) {
                                        case "island", "archipelago", "islet" -> COASTLINE;
                                        default -> drawType;
                                    };
                                case "route":
                                    drawType = switch (value) {
                                        case "ferry" -> FERRY;
                                        default -> drawType;
                                    };
                                    break;
                                case "highway":

                                    drawType = switch (value) {
                                        //MAJOR ROADS:
                                        case "motorway" -> MOTORWAY;
                                        case "motorway_link" -> MOTORWAYLINK;
                                        case "trunk", "trunk_link" -> TRUNK;
                                        case "primary", "primary_link" -> PRIMARY;
                                        case "secondary", "secondary_link" -> SECONDARY;
                                        case "tertiary", "tertiary_link" -> TERTIARY;
                                        case "unclassified" -> QUATERNARY;
                                        case "raceway" -> RACEWAY;
                                        //CITY ROADS:
                                        case "residential" -> RESIDENTIALROAD;
                                        case "service" -> SERVICEROAD;
                                        case "living_street" -> LIVINGROAD;
                                        //Roads and ways for non-motorized vehicles:
                                        case "bridleway" -> BRIDLEWAY;
                                        case "cycleway" -> CYCLEWAY;
                                        case "footway", "path" -> PATH;
                                        case "steps" -> STEPS;
                                        case "track" -> HIGHWAYTRACK;
                                        case "platform" -> PLATFORM;
                                        case "pedestrian" -> PEDESTRIAN;
                                        default -> drawType;
                                    };

                                    if (value.equals("traffic_signals")) {
                                        speedReducingItems.add(currentNode);
                                    }
                                    if (value.equals("stop")) {
                                        speedReducingItems.add(currentNode);
                                    }
                                    break;
                                case "traffic_calming":
                                    speedReducingItems.add(currentNode);
                                    break;
                                case "bridge":
                                    drawType = BRIDGE;
                                    break;
                                case "railway":
                                    drawType = switch (value) {
                                        case "rail", "light_rail", "construction", "disused", "tram", "preserved", "miniature", "monorail" -> RAIL;
                                        case "subway" -> SUBWAY;
                                        case "platform" -> PLATFORM;
                                        default -> drawType;
                                    };
                                    break;
                                case "barrier":
                                    drawType = switch (value) {
                                        case "city_wall", "retaining_wall ", "wall", "fence", "chain", "guard_rail", "handrail", "ditch" -> BARRIER;
                                        case "hedge" -> HEDGE;
                                        default -> drawType;
                                    };
                                    if (value.equals("toll_booth")) {
                                        speedReducingItems.add(currentNode);
                                    }
                                    break;
                                case "waterway":
                                    drawType = switch (value) {
                                        case "river", "canal", "stream", "ditch", "drain" -> RIVERWAY;
                                        case "weir", "dam" -> WATERBARRIER;
                                        case "riverbank", "dock" -> WATER;
                                        default -> drawType;
                                    };
                                    break;
                                case "aeroway":
                                    drawType = switch (value) {
                                        case "runway", "taxiway" -> AEROWAY;
                                        default -> drawType;
                                    };
                                    break;
                                case "man_made":
                                    drawType = switch (value) {
                                        case "pier" -> PIER;
                                        case "breakwater", "groyne" -> WATERBARRIER;
                                        case "cutline" -> BRIDLEWAY;
                                        case "bridge" -> BRIDGE;
                                        case "wastewater_plant" -> INDUSTRIAL;
                                        default -> drawType;
                                    };
                                    break;
                                case "amenity":
                                    drawType = switch (value) {
                                        case "marketplace" -> COMMERCIAL;
                                        case "parking_space" -> PARKINGLINE;
                                        case "parking", "bicycle_parking", "motorcycle_parking", "taxi" -> PARKING;
                                        case "bus_station", "ferry_terminal" -> BUSSTATION;
                                        default -> drawType;
                                    };
                                    break;
                                case "landuse":
                                    drawType = switch (value) {
                                        case "farmland" -> FARMLAND;
                                        case "basin", "reservoir", "salt_pond" -> WATER;
                                        case "residential" -> RESIDENTIAL;
                                        case "commercial", "retail" -> COMMERCIAL;
                                        case "industrial", "railway" -> INDUSTRIAL;
                                        case "cemetery" -> CEMETERY;
                                        case "military" -> MILITARY;
                                        case "quarry" -> QUARRY;
                                        case "construction", "landfill" -> CONSTRUCTION;
                                        case "forest" -> WOOD;
                                        case "allotments", "flowerbed", "farmyard", "grass", "meadow", "orchard", "vineyard", "greenfield", "greenhouse_horticulture", "plant_nursery", "recreation_ground", "village_green" -> FOREST;
                                        case "brownfield", "depot", "garages", "religious" -> LANDUSERARE;
                                        default -> drawType;
                                    };
                                    break;
                                case "leisure":
                                    drawType = switch (value) {
                                        case "track" -> LEISURETRACK;
                                        case "sports_centre" -> SPORTSCENTRE;
                                        case "pitch" -> PITCH;
                                        case "swimming_pool", "marina", "ice_rink" -> WATER;
                                        case "nature_reserve" -> NATURERESERVE;
                                        case "dog_park", "park", "miniature_golf", "garden", "stadium", "playground", "outdoor_seating", "summer_camp", "golf_course" -> LEISURE;
                                        default -> drawType;
                                    };
                                    break;
                                case "power":
                                    drawType = switch (value) {
                                        case "line", "minor_line" -> POWERLINE;
                                        case "generator" -> INDUSTRIAL;
                                        default -> drawType;
                                    };
                                    break;
                                case "natural":
                                    drawType = switch (value) {
                                        case "water", "reef", "glacier" -> WATER;
                                        case "wood" -> WOOD;
                                        case "wetland" -> WETLANDS;
                                        case "grassland" -> GRASSLAND;
                                        case "heath" -> HEATH;
                                        case "beach", "shoal", "sand", "bare_rock" -> BEACH;
                                        case "mud", "scree", "shingle" -> MUD;
                                        case "coastline" -> COASTLINE;
                                        case "scrub", "shrub" -> SCRUB;
                                        case "tree_row" -> BRIDLEWAY;
                                        case "bay", "peninsula" -> IGNORE;
                                        default -> drawType;
                                    };
                                    break;
                                case "building":
                                    drawType = switch (value) {
                                        case "apartments" -> APARTMENTS;
                                        default -> BUILDING;
                                    };
                                    break;
                                //Everything with traffic info
                                case "maxspeed":
                                    final String regexMaxSpeed = "^([0-9][\\.0-9]+?)(?:[ ]?(?:km\\/h|kmh|kph|mph|knots))?$";
                                    final Pattern pattern = Pattern.compile(regexMaxSpeed);
                                    Matcher matcher = pattern.matcher(value);
                                    if (matcher.find())
                                        maxSpeed = Float.parseFloat(matcher.group(1));
                                    break;
                                case "source:maxspeed":
                                    maxSpeed = switch (value) {
                                        case "DK:rural" -> 80f;
                                        case "DK:urban" -> 50f;
                                        default -> 0f;
                                    };
                                    break;
                                case "oneway":
                                    onewayCar = switch (value) {
                                        case "yes" -> true;
                                        default -> false;
                                    };
                                    break;
                                case "oneway:bicycle":
                                    onewayBicycle = switch (value) {
                                        case "yes" -> true;
                                        default -> false;
                                    };
                                    break;
                                case "junction":
                                    switch (value) {
                                        case "roundabout" -> {
                                            onewayCar = true;
                                            onewayBicycle = true;
                                        }
                                        default -> {
                                            onewayCar = false;
                                            onewayBicycle = false;
                                        }
                                    }
                                    break;
                                case "access":
                                    switch (value) {
                                        case "no" -> access = false;
                                        default -> access = true;
                                    }
                                    break;
                                case "foot":
                                    if (value.equals("no")) walkAllowed = false;
                                    break;
                                case "bicycle":
                                    if (value.equals("no")) bicycleAllowed = false;
                                    break;
                            }
                            break;
                        case "member":
                            ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            var elm = id2way.get(ref);
                            if (elm != null) relationMembers.add(elm);
                            break;
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    switch (reader.getLocalName()) {
                        case "node" -> {
                            if (addressBuilder != null && addressBuilder.isValid()) {
                                addressBuilder.coords(currentNode.lonX, currentNode.latY);
                                Address address = addressBuilder.build();
                                addressBook.addAddress(address);
                            }
                            if (canvasTextName != null) {
                                CanvasText canvasText = new CanvasText(canvasTextName, canvasTextPlace);
                                canvasText.setCoords(currentNode.lonX, currentNode.latY);
                                canvasTexts.add(canvasText);
                            }
                            addressBuilder = null;
                            canvasTextName = null;
                            canvasTextPlace = null;
                        }
                        case "way" -> {
                            drawtypesLoaded++;


                            if (maxSpeed == 0) {
                                maxSpeed = switch (drawType) {
                                    case MOTORWAY -> 120;
                                    case MOTORWAYLINK -> 95; //not fully true, but I want A* to better find motorways
                                    case TRUNK -> 90;
                                    case PRIMARY -> 80;
                                    case SECONDARY, TERTIARY -> 70;
                                    case QUATERNARY -> 60;
                                    case RESIDENTIALROAD -> 40;
                                    case LIVINGROAD -> 20;
                                    case SERVICEROAD -> 35;
                                    default -> 50;
                                };
                            }

                            int speedreducers = 0;

                            for (var node : nodes) {
                                if (speedReducingItems.contains(node))
                                    speedreducers++;
                            }

                            var way = new Way(nodes, drawType, canvasTextName, maxSpeed, speedreducers, walkAllowed, bicycleAllowed, onewayCar, onewayBicycle, access);
                            if (drawType == COASTLINE)
                                coastlines.add(way);

                            id2way.put(relID, way);
                            if (drawType != DEFAULT && drawType != IGNORE) wayMap.get(drawType).add(way);


                            nodes.clear();
                            isMultipolygon = false;
                            drawType = DEFAULT;
                            maxSpeed = 0;
                            addressBuilder = null;
                            canvasTextName = null;
                            canvasTextPlace = null;
                            walkAllowed = true;
                            bicycleAllowed = true;
                            onewayCar = false;
                            onewayBicycle = false;
                            access = true;
                        }
                        case "relation" -> {
                            if (!relationMembers.isEmpty() && isMultipolygon && drawType != IGNORE) {
                                MultiPolygon multiPolygon;
                                if (drawType == COASTLINE) {
                                    multiPolygon = new MultiPolygon(relationMembers);

                                    coastlines.add(multiPolygon);

                                    for (Way way : relationMembers) {
                                        if (way.drawType == COASTLINE) way.setPartOfMultiPolygon(true);
                                    }

                                    relationMembers.clear();
                                    isMultipolygon = false;
                                    drawType = DEFAULT;
                                    addressBuilder = null;
                                    break;
                                }
                                drawtypesLoaded++;


                                multiPolygon = new MultiPolygon(relationMembers);


                                if (drawType != DEFAULT) multiPolygon.setDrawType(drawType);
                                else multiPolygon.setDrawType(multiPolygon.findDrawType(relationMembers));
                                if (multiPolygon.drawType != DEFAULT) {
                                    multiPolygonMap.get(multiPolygon.getDrawType()).add(multiPolygon);
                                    for (Way way : relationMembers) {
                                        if (way.drawType == multiPolygon.drawType) way.setPartOfMultiPolygon(true);
                                    }
                                }
                            }
                            relationMembers.clear();
                            isMultipolygon = false;
                            drawType = DEFAULT;
                            addressBuilder = null;
                        }
                    }
                    break;
            }
        }

        //Remove ways, that are primary parts of multipolygons
        for (List<Way> list : wayMap.values()) {
            for (Way way : list) {
                if (way.isPartOfMultiPolygon()) {
                    drawtypesLoaded--;
                }
            }
            list.removeIf(Way::isPartOfMultiPolygon);
        }

        coastlines.removeIf(DrawCoordinate::isPartOfMultiPolygon);

        if (!SplashScreenController.createKDTreeOnlyForWays) {
            List<Way> allWays = new ArrayList<>();
            for (Map.Entry<DrawType, List<Way>> entry : wayMap.entrySet()) {
                allWays.addAll(entry.getValue());
            }

            List<MultiPolygon> allMultiPolygons = new ArrayList<>();
            for (Map.Entry<DrawType, List<MultiPolygon>> entry : multiPolygonMap.entrySet()) {
                allMultiPolygons.addAll(entry.getValue());
            }

            kdTreeWays = new KdTree<>(allWays);
            kdTreeCanvasTexts = new KdTree<>(canvasTexts);
            kdTreeMultiPolygon = new KdTree<>(allMultiPolygons);
            kdTreeTransportableRoads = null;
        } else {
            List<DrawCoordinate> transportableRoads = new ArrayList<>();
            for (Map.Entry<DrawType, List<Way>> entry : wayMap.entrySet()) {
                for (Way way : entry.getValue())
                    if (TransportType.transportAllowedDrawCoordinate(way)) transportableRoads.add(way);
            }

            kdTreeWays = null;
            kdTreeCanvasTexts = null;
            kdTreeMultiPolygon = null;
            kdTreeTransportableRoads = new KdTree(transportableRoads);
        }

        if (speedReducingItems != null) speedReducingItems.clear();

        mapData.init(multiPolygonMap,
                wayMap,
                coastlines,
                canvasTexts,
                kdTreeWays,
                kdTreeMultiPolygon,
                kdTreeCanvasTexts,
                kdTreeTransportableRoads,
                addressBook,
                boundingBox,
                minX, minY, maxX, maxY
        );
    }

}
