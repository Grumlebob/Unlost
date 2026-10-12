package bfst22.controller;

import bfst22.model.graphiccomponents.BoundingBox;
import bfst22.model.graphiccomponents.DrawToggles;
import bfst22.model.graphiccomponents.DrawType;
import bfst22.view.MapCanvas;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class DebugMenuController implements Serializable {
    public static final long serialVersionUID = 8732549823465L;

    @FXML
    private VBox checkboxHolder;

    @FXML
    private VBox debugWindow;

    @FXML
    private Label repaint;

    @FXML
    private Label zoomLabel;

    @FXML
    private Label totalDrawablesLabel;

    @FXML
    private Label msDijkstra;

    @FXML
    private Label edgesExploredLabel;

    @FXML
    private Label totalVerticesLabel;

    @FXML
    private Label totalEdgesLabel;

    @FXML
    private Label graphBuildLabel;

    @FXML
    private Label edgesInShortestPath;
    @FXML
    private Label pathInSPTLabel;

    @FXML
    private Label mapParsedLabel;

    @FXML
    private Label inBoundingLabel;

    @FXML
    private Label mouseCoords;
    @FXML
    private Label mouseCoordsY;

    @FXML
    public List<CheckBox> boxes;

    @FXML
    private ToggleButton extraPathfinding;

    @FXML
    private ToggleButton drawMultipolygons;
    public static boolean showMultipolygons = true;
    @FXML
    private ToggleButton drawBestPath;
    public static boolean showBestPath = true;
    @FXML
    private ToggleButton drawCanvasText;
    public static boolean showCanvasText = true;
    @FXML
    private ToggleButton drawCoastlines;
    public static boolean showCoastlines = true;
    @FXML
    private ToggleButton drawEdgesExplored;
    public static boolean showEdgesExplored = false;
    @FXML
    private ToggleButton drawWayText;
    public static boolean showWayText = false;
    @FXML
    private ToggleButton drawWays;
    public static boolean showWays = true;

    @FXML
    private ToggleButton ignoreZoomsToggle;
    public static boolean ignoreZoomslevels = false;
    @FXML
    private ToggleButton drawBoundingboxes;
    public static boolean showBoundingboxes = false;
    @FXML
    private ToggleButton marioButton;
    public static boolean showMarioMode = false;

    @FXML
    private ToggleButton drawSPT;
    public static boolean showSPT = false;
    @FXML
    private ToggleButton drawHeading;
    public static boolean showHeading = false;

    @FXML
    private ToggleButton draw500MeterMode;
    public static boolean show500MeterMode = false;

    @FXML
    private ToggleButton hoverInfoButton;
    public static boolean showHoverInfo = false;

    @FXML
    private ToggleButton kdTreeStructureButton;
    public static boolean showKDTreeStructure = false;

    @FXML
    private ToggleButton kdTreeNearestNeighbourButton;
    public static boolean showNearestNeighbour = false;

    @FXML
    private ToggleButton kdTreeRangeQueryButton;
    public static boolean isUsingKDTreeRangeQuery = false;

    boolean visible = false;

    public static boolean nearestBool;
    public static boolean kdTreeLoaded;

    public ControllerMediator controllerMediator;

    @FXML
    void initialize() {
        fillCheckboxHolder();
        saveCheckboxes();
        controllerMediator = ControllerMediator.getInstance();
        controllerMediator.registerController(this);
        kdTreeLoaded = false;
        nearestBool = false;
        debugWindow.setVisible(visible);
        if(SplashScreenController.createKDTreeOnlyForWays) {
            kdTreeStructureButton.setVisible(false);
            kdTreeNearestNeighbourButton.setVisible(false);
            kdTreeRangeQueryButton.setVisible(false);
            hoverInfoButton.setVisible(false);
        } else {
            drawWays.setSelected(false);
            showWays = drawWays.isSelected();
            kdTreeRangeQueryButton.setSelected(true);
            isUsingKDTreeRangeQuery = kdTreeRangeQueryButton.isSelected();
        }

    }

    public void toggleKDTreeStructure() {
        if (SplashScreenController.createKDTreeOnlyForWays) return;
        showKDTreeStructure = kdTreeStructureButton.isSelected();
        controllerMediator.controller.canvas.repaint();
    }

    public void toggleKDTreeNearest() {
        if (SplashScreenController.createKDTreeOnlyForWays) return;
        showNearestNeighbour = kdTreeNearestNeighbourButton.isSelected();
        controllerMediator.controller.canvas.repaint();
    }

    public void toggleIsUsingKDTreeRangeQuery() {
        if (SplashScreenController.createKDTreeOnlyForWays) return;
        isUsingKDTreeRangeQuery = kdTreeRangeQueryButton.isSelected();
        controllerMediator.controller.canvas.repaint();

    }


    @FXML
    public void fillCheckboxHolder() {
        boxes = new ArrayList<>();
        for (var type : DrawType.values()) {
            CheckBox c = new CheckBox(String.valueOf(type));
            c.setSelected(true);
            boxes.add(c);
            checkboxHolder.getChildren().add(c);
        }
    }

    public void roadCheckBox() {
        String[] types = "MOTORWAYLINK,MOTORWAY,TERTIARY,QUATERNARY,TRUNK,RESIDENTIALROAD,SERVICEROAD,LIVINGROAD,BRIDLEWAY,CYCLEWAY,PATH,STEPS,HIGHWAYTRACK,PLATFORM,PEDESTRIAN,PRIMARY,SECONDARY".split("[ ,]+");
        for (CheckBox c : boxes) {
            for (String str : types) {
                if (c.getText().equalsIgnoreCase(str))
                    c.setSelected(true);
            }
        }
        saveCheckboxes();
    }

    public void saveCheckboxes() {
        DrawToggles dt = DrawToggles.getInstance();
        for (CheckBox c : boxes) {
            dt.drawToggles.put(c.getText(), c.isSelected());
        }
    }

    public void clearCheckboxes() {
        for (CheckBox c : boxes) {
            c.setSelected(false);
        }
        saveCheckboxes();
    }

    public void markCheckboxes() {
        for (CheckBox c : boxes) {
            c.setSelected(true);
        }
        saveCheckboxes();
    }

    public void showMultipolygons() {
        showMultipolygons = drawMultipolygons.isSelected();
        controllerMediator.controller.canvas.repaint();
    }
    public void showCoastlines() {
        showCoastlines = drawCoastlines.isSelected();
        controllerMediator.controller.canvas.repaint();
    }
    public void showWays() {
        showWays = drawWays.isSelected();
        controllerMediator.controller.canvas.repaint();
    }
    public void showSPT() {
        showSPT = drawSPT.isSelected();
        controllerMediator.controller.canvas.repaint();
    }

    public void menuToggle500mMode() {
        draw500MeterMode.setSelected(!draw500MeterMode.isSelected());
        show500MeterMode();
    }
    public void show500MeterMode() {
        show500MeterMode = draw500MeterMode.isSelected();
        controllerMediator.controller.canvas.repaint();
    }


    public void showHeading() {
        showHeading = drawHeading.isSelected();
        controllerMediator.controller.canvas.repaint();
    }

    public void showCanvasText() {
        showCanvasText = drawCanvasText.isSelected();
        controllerMediator.controller.canvas.repaint();
    }
    public void showWayText() {
        showWayText = drawWayText.isSelected();
        controllerMediator.controller.canvas.repaint();
    }
    public void showBestPath() {
        showBestPath = drawBestPath.isSelected();
        controllerMediator.controller.canvas.repaint();
    }
    public void showEdgesExplored() {
        showEdgesExplored = drawEdgesExplored.isSelected();
        controllerMediator.controller.canvas.repaint();
    }

    public void toggleIgnoreZoomLevel() {
        ignoreZoomslevels = ignoreZoomsToggle.isSelected();
        controllerMediator.controller.canvas.repaint();
    }
    public void showBoundingboxes() {
        showBoundingboxes = drawBoundingboxes.isSelected();
        controllerMediator.controller.canvas.repaint();
    }

    public void menuToggleMarioMode() {
        marioButton.setSelected(!marioButton.isSelected());
        showMarioMode();
    }

    public void showMarioMode() {
        showMarioMode = marioButton.isSelected();
        controllerMediator.controller.canvas.repaint();
    }

    public void showHoverInfo() {
        showHoverInfo = hoverInfoButton.isSelected();
        controllerMediator.controller.canvas.repaint();
    }


    public void extraPathfindingSettings() {
        AddressMenuController.showExtraSettings = extraPathfinding.isSelected();
        controllerMediator.getAddressMenuController().updateUI();
        controllerMediator.controller.canvas.repaint();
    }


    public void setRenderExtraMargin() {
        BoundingBox.setRenderExtraMargin(!BoundingBox.isRenderExtraMargin());
        controllerMediator.controller.canvas.repaint();
    }

    public void changeExtraMargin10() {
        MapCanvas.extraMargin = 10f;
    }

    public void changeExtraMargin20() {
        MapCanvas.extraMargin = 5f;
    }

    public void changeExtraMargin33() {
        MapCanvas.extraMargin = 3f;
    }

    public void changeExtraMargin40() {
        MapCanvas.extraMargin = 2.5f;
    }

    public void setZoomLabel(String s) {
        zoomLabel.setText(s);
    }
    public void setEdgesExploredLabel(String s) {
        edgesExploredLabel.setText(s);
    }
    public void setEdgesInShortestPath(String s) {
        edgesInShortestPath.setText(s);
    }

    public void setPathInSPTLabel(String s) {
        pathInSPTLabel.setText(s);
    }
    public void setMapParsedLabel(String s) {
        mapParsedLabel.setText(s);
    }

    public void setMsDijkstra(String s) {
        msDijkstra.setText(s);
    }

    public void setTotalEdgesLabel(String s) {
        totalEdgesLabel.setText(s);
    }

    public void setTotalVerticesLabel(String s) {
        totalVerticesLabel.setText(s);
    }

    public void setGraphBuildLabel(String s) {
        graphBuildLabel.setText(s);
    }

    public void setMouseCoords(String x, String y) {
        mouseCoords.setText(x);
        mouseCoordsY.setText(y);
    }

    public void setRepaintLabel(String s) {
        repaint.setText(s);
    }

    public void setTotalLabel(String s) {
        totalDrawablesLabel.setText(s);
    }

    public void setInBoundingLabel(String s) {
        inBoundingLabel.setText(s);
    }


    public void toggleVisible() {
        visible = !visible;
        debugWindow.setVisible(visible);
    }

}