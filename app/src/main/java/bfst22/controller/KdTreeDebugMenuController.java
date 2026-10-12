package bfst22.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class KdTreeDebugMenuController {
    @FXML
    private VBox kdtreeDebugWindow;

    @FXML
    private Label totalQueryTimeLabel;

    @FXML
    private Label queryDrawTime;

    @FXML
    private Label renderedFromQueryLabel;

    @FXML
    private Label nnTimeLabel;

    @FXML
    private Label totalBuildTimeLabel;

    @FXML
    private Label totalInsertTimeLabel;

    @FXML
    private Label waysSizeLabel;

    @FXML
    private Label multipolygonsSizeLabel;

    @FXML
    private Label querySeachTimeWays;

    @FXML
    private Label renderedFromQueryLabelWays;

    @FXML
    private Label querySearchTimeMultiPolygons;

    @FXML
    private Label renderedFromQueryLabelMultipolygons;

    @FXML
    private Label querySearchTimeCanvastexts;

    @FXML
    private Label renderedFromQueryCanvastexts;

    @FXML
    private Label canvastextsSizeLabel;

    @FXML
    private Label wayMapDrawTime;

    @FXML
    private Label numOfElementsWays;

    @FXML
    private Label numOfElementsCanvastexts;

    @FXML
    private Label numOfElementsMultiPolygons;

    @FXML
    private Label inputSizeCanvastexts;

    @FXML
    private Label inputSizeWays;

    @FXML
    private Label inputSizeMultipolygons;

    @FXML
    private Label depthMultipolygons;

    @FXML
    private Label depthCanvastexts;

    @FXML
    private Label depthWays;

    @FXML
    private Label minNumOfElementsWays;

    @FXML
    private Label maxNumOfElementsWays;

    @FXML
    private Label minNumOfElementsCanvastexts;

    @FXML
    private Label maxNumOfElementsCanvastexts;

    @FXML
    private Label minNumOfElementsMultipolygons;

    @FXML
    private Label maxNumOfElementsMultipolygons;

    @FXML
    private ToggleGroup queryType;

    boolean usingQueryAsSet;

    boolean visible = false;

    public ControllerMediator controllerMediator;

    @FXML
    void initialize(){
        controllerMediator = ControllerMediator.getInstance();
        controllerMediator.registerKdTreeController(this);
    }

    public void updateTotalQueryTime(String s){
        totalQueryTimeLabel.setText(s);
    }

    public void updateQueryDrawTime(String s){
        queryDrawTime.setText(s);
    }

    public void updateRenderedFromQuery(String s){
        renderedFromQueryLabel.setText(s);
    }

    public void updateNNTime(String s){
        nnTimeLabel.setText(s);
    }

    public void updateTotalBuildTimeLabel(String s){
        totalBuildTimeLabel.setText(s);
    }

    public void updateTotalInsertTimeLabel(String s){
        totalInsertTimeLabel.setText(s);
    }

    public void updateWaysSizeLabel(String s){
        waysSizeLabel.setText(s);
    }

    public void updateMultipolygonsSizeLabel(String s){
        multipolygonsSizeLabel.setText(s);
    }

    public void updateQuerySearchTimeWays(String s){
        querySeachTimeWays.setText(s);
    }

    public void updateRenderedFromQueryLabelWays(String s){
        renderedFromQueryLabelWays.setText(s);
    }

    public void updateQuerySearchTimeMultiPolygons(String s){
        querySearchTimeMultiPolygons.setText(s);
    }

    public void updateRenderedFromQueryLabelMultipolygons(String s){
        renderedFromQueryLabelMultipolygons.setText(s);
    }

    public void updateQuerySearchTimeCanvastexts(String s){
        querySearchTimeCanvastexts.setText(s);
    }

    public void updateRenderedFromQueryCanvastexts(String s){
        renderedFromQueryCanvastexts.setText(s);
    }

    public void updateCanvastextsSizeLabel(String s){
        canvastextsSizeLabel.setText(s);
    }

    public void updateWayMapDrawTime(String s){
        wayMapDrawTime.setText(s);
    }

    public void updateNumOfElementsWays(String s){
        numOfElementsWays.setText(s);
    }

    public void updateNumOfElementsCanvastexts(String s){
        numOfElementsCanvastexts.setText(s);
    }

    public void updateNumOfElementsMultiPolygons(String s){
        numOfElementsMultiPolygons.setText(s);
    }

    public void updateInputSizeCanvastexts(String s){
        inputSizeCanvastexts.setText(s);
    }

    public void updateInputSizeWays(String s){
        inputSizeWays.setText(s);
    }

    public void updateInputSizeMultipolygons(String s){
        inputSizeMultipolygons.setText(s);
    }

    public void updateDepthMultipolygons(String s){
        depthMultipolygons.setText(s);
    }

    public void updateDepthCanvastexts(String s){
        depthCanvastexts.setText(s);
    }

    public void updateDepthWays(String s){
        depthWays.setText(s);
    }

    public void updateMinNumOfElementsWays(String s){
        minNumOfElementsWays.setText(s);
    }

    public void updateMaxNumOfElementsWays(String s){
        maxNumOfElementsWays.setText(s);
    }

    public void updateMinNumOfElementsCanvastexts(String s){
        minNumOfElementsCanvastexts.setText(s);
    }

    public void updateMaxNumOfElementsCanvastexts(String s){
        maxNumOfElementsCanvastexts.setText(s);
    }

    public void updateMinNumOfElementsMultipolygons(String s){
        minNumOfElementsMultipolygons.setText(s);
    }

    public void updateMaxNumOfElementsMultipolygons(String s){
        maxNumOfElementsMultipolygons.setText(s);
    }

    public void toggleQueryType(){
        switch (((RadioButton) queryType.getSelectedToggle()).getText()) {
            case "Map" -> usingQueryAsSet = false;
            case "Set" -> usingQueryAsSet = true;
        }
    }

    public void toggleVisible() {
        visible = !visible;
        kdtreeDebugWindow.setVisible(visible);
    }

    public boolean isUsingQueryAsSet() {
        return usingQueryAsSet;
    }
}
