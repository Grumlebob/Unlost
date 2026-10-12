package bfst22.controller;

import bfst22.model.MapData;
import bfst22.view.MapCanvas;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class Controller {

    private Point2D lastMouse;
    public static float lastMouseX;
    public static float lastMouseY;
    public static float toCoordsX;
    public static float toCoordsY;
    public static float rightMouseX;
    public static float rightMouseY;
    public ControllerMediator controllerMediator = ControllerMediator.getInstance();
    MapData mapData = MapData.getInstance();


    @FXML
    public MapCanvas canvas;
    @FXML
    public MenuBar menuBar;

    public void init() throws InterruptedException {
        toCoordsX = 0;
        toCoordsY = 0;
        lastMouseX = 0;
        lastMouseY = 0;
//        controllerMediator = ControllerMediator.getInstance();
        controllerMediator.setMainController(this);
        menuBar.setFocusTraversable(true);
        canvas.init();
    }

    @FXML
    private void onScroll(ScrollEvent e) {
        var factor = e.getDeltaY();
        canvas.zoom(Math.pow(1.003, factor), e.getX(), e.getY());
        controllerMediator.getDebugMenuController().setZoomLabel((String.valueOf((int) MapCanvas.getZoomInt())));
    }

    @FXML
    private void onMouseDragged(MouseEvent e) {
        var dx = e.getX() - lastMouse.getX();
        var dy = e.getY() - lastMouse.getY();
        canvas.pan(dx, dy);
        lastMouse = new Point2D(e.getX(), e.getY());
    }


    @FXML
    private void onMousePressed(MouseEvent e) {
        lastMouse = new Point2D(e.getX(), e.getY());
        controllerMediator.
                getDebugMenuController().
                setMouseCoords(String.valueOf(canvas.mouseToModel(lastMouse).getX()), String.valueOf(canvas.mouseToModel(lastMouse).getY()));
        if (e.isControlDown() && e.isPrimaryButtonDown()) {
            lastMouseX = (float) canvas.mouseToModel(lastMouse).getX();
            lastMouseY = (float) canvas.mouseToModel(lastMouse).getY();
            mapData.makeDijkstraFromClick();
            controllerMediator.getAddressMenuController().getClickRouteText();
        } else if (e.isShiftDown() && e.isPrimaryButtonDown()) {
            toCoordsX = (float) canvas.mouseToModel(lastMouse).getX();
            toCoordsY = (float) canvas.mouseToModel(lastMouse).getY();
            mapData.makeDijkstraFromClick();
            controllerMediator.getAddressMenuController().getClickRouteText();
        } else if (e.isSecondaryButtonDown()) {
            rightMouseX = (float) canvas.mouseToModel(lastMouse).getX();
            rightMouseY = (float) canvas.mouseToModel(lastMouse).getY();
            controllerMediator.getAddressMenuController().createPointOfInterest();
        }
        canvas.requestFocus();
        canvas.repaint();
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.D) {
            controllerMediator.getDebugMenuController().toggleVisible();
        } else if (keyEvent.getCode() == KeyCode.A) {
            controllerMediator.getAddressMenuController().toggleVisible();

        } else if (keyEvent.getCode() == KeyCode.B) {
            controllerMediator.getDebugMenuController().setRenderExtraMargin();
        }
        else if(keyEvent.getCode() == KeyCode.K){
            controllerMediator.getKdTreeDebugMenuController().toggleVisible();
        }
    }

    public void handleToggleDebug(ActionEvent actionEvent) {
        controllerMediator.getDebugMenuController().toggleVisible();
    }

    public void handleToggleMarioMode(ActionEvent actionEvent) {
        controllerMediator.getDebugMenuController().menuToggleMarioMode();
    }

    public void handleToggle500mMode(ActionEvent actionEvent) {
        controllerMediator.getDebugMenuController().menuToggle500mMode();
    }

}
