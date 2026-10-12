package bfst22.controller;

import bfst22.model.MapData;
import bfst22.model.address.AddressBook;
import bfst22.model.address.Address;
import bfst22.model.address.PointOfInterest;
import bfst22.model.pathfinding.TransportType;
import bfst22.model.pathfinding.Dijkstra;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.Optional;

import static bfst22.model.pathfinding.TransportType.*;
import static bfst22.model.pathfinding.Dijkstra.*;
import static bfst22.view.MapCanvas.zoomInt;

/**
 * Controls the address menu. Is initialized by the AddressMenu.fxml file.
 */
public class AddressMenuController implements Serializable {
    public static final long serialVersionUID = 8237645894235L;

    @FXML
    private Label distanceLabel;
    @FXML
    private Label travelTimeLabel;
    @FXML
    private TextArea routeTextField;
    @FXML
    private ToggleGroup transport;
    @FXML
    private ToggleGroup dijkstramode;
    @FXML
    private ToggleGroup pathmode;

    @FXML
    private ListView<Address> listOutput;

    @FXML
    private ObservableList<Address> obslist;

    @FXML
    private TextField searchTextField;

    @FXML
    private TextField searchTextFieldTo;

    @FXML
    private VBox addressMenu;

    @FXML
    private VBox routeBox;

    @FXML
    private ScrollPane addressMatchesList;

    @FXML
    private VBox addressExtraSettings;

    public static boolean showExtraSettings;

    boolean visible;

    boolean isSearchingFrom = true;
    public static float addressFromX;
    public static float addressFromY;
    public static float addressToX;
    public static float addressToY;

    public static TransportType transportTypeChosen = CAR;
    public static boolean isAStar = true;
    public static boolean isFastestPath = true;
    public static boolean blockFreeDijkstra = false;

    public ControllerMediator controllerMediator;
    public AddressBook addressBook;

    MapData mapData = MapData.getInstance();

    @FXML
    void initialize() {
        controllerMediator = ControllerMediator.getInstance();
        controllerMediator.registerAddressController(this);
        addressBook = MapData.getInstance().getAddressBook();
        obslist = FXCollections.observableArrayList();
        listOutput.getItems().setAll(obslist);
        routeBox.setVisible(false);
        addressMatchesList.setVisible(false);
        addressExtraSettings.setVisible(false);
        addressFromX = 0;
        addressFromY = 0;
        addressToX = 0;
        addressToY = 0;
    }

    public void updateUI() {
        addressExtraSettings.setVisible(showExtraSettings);
    }

    public void reverseSearches() {
        String tempAddress = searchTextField.getText();
        float tempX = addressFromX;
        float tempY = addressFromY;

        addressFromX = addressToX;
        addressFromY = addressToY;
        searchTextField.setText(searchTextFieldTo.getText());

        addressToX = tempX;
        addressToY = tempY;
        searchTextFieldTo.setText(tempAddress);
        getAddressRouteText();
        controllerMediator.controller.canvas.repaint();
    }

    @FXML
    void searchInput() {
        isSearchingFrom = true;
        searchAddress(searchTextField);
    }

    @FXML
    void searchInputTo() {
        isSearchingFrom = false;
        searchAddress(searchTextFieldTo);
    }

    private void searchAddress(TextField searchTextField) {
        String input = searchTextField.getText().toLowerCase();
        obslist.clear();
        if(input.length() > 1) {
            obslist.addAll(addressBook.searchTSTByPrefix(input));
            listOutput.getItems().setAll(obslist);
            addressMatchesList.setVisible(true);
        }
    }


    @FXML
    void searchPointOfInterest() {
        obslist.clear();
        obslist.addAll(addressBook.getPointOfInterestList());
        if (obslist.size() == 0) {
            obslist.add(new PointOfInterest(0,0,"You haven't added any point of interest"," "));
            obslist.add(new PointOfInterest(0,0,"You can add a point of interests by right clicking"," "));
        }
        listOutput.getItems().setAll(obslist);
        addressMatchesList.setVisible(true);
    }

    public void setCoordsOfFromAddress() {
        ObservableList<Address> a = listOutput.getSelectionModel().getSelectedItems();
        if (a != null && a.get(0).lonX > 5 && a.get(0).latY < 15) {
            if (isSearchingFrom) {
                addressFromX = a.get(0).lonX;
                addressFromY = a.get(0).latY;
                searchTextField.setText(a.get(0).toString());
                controllerMediator.panToAddress(addressFromX, addressFromY);
            } else {
                addressToX = a.get(0).lonX;
                addressToY = a.get(0).latY;
                searchTextFieldTo.setText(a.get(0).toString());
                controllerMediator.panToAddress(addressToX, addressToY);
            }
        }
        mapData.makeDijkstraFromAddress();
        controllerMediator.controller.canvas.repaint();
    }

    public static void drawSearchedAddress(GraphicsContext gc) {
        if (zoomInt < 2000) gc.setFont(new Font("Arial", 0.01));
        else if (zoomInt < 4000) gc.setFont(new Font("Arial", 0.005));
        else if (zoomInt < 15000) gc.setFont(new Font("Arial", 0.003));
        else if (zoomInt < 25000) gc.setFont(new Font("Arial", 0.002));
        else if (zoomInt < 35000) gc.setFont(new Font("Arial", 0.001));
        else if (zoomInt < 55000) gc.setFont(new Font("Arial", 0.0005));
        else gc.setFont(new Font("Arial", 0.0002));
        gc.setFill(Color.rgb(35, 130, 255));
        gc.fillText("❢", addressFromX, addressFromY);
        //Shift LeftClick
        gc.fillText("⚑", addressToX, addressToY);
    }

    public void getTransportType() {
        transportTypeChosen = switch (((RadioButton) transport.getSelectedToggle()).getText()) {
            case "Car" -> CAR;
            case "Bike" -> BICYCLE;
            case "Walk" -> WALK;
            default -> CAR;
        };
        //Sets default toggles - can be manually changed in UI.
        //If pathmode changes, then block free dijkstra setting for this search,
        //to ensure a new SPT is generated, with new weights.
        boolean beforePathmode = AddressMenuController.isFastestPath;
        AddressMenuController.isFastestPath =
                transportTypeChosen != BICYCLE &&
                transportTypeChosen != WALK;
        AddressMenuController.isAStar = true;
        boolean afterPathmode = AddressMenuController.isFastestPath;
        blockFreeDijkstra = beforePathmode != afterPathmode;
        if (isClickMode) {
            mapData.makeDijkstraFromClick();
            getClickRouteText();
        }
        else {
            mapData.makeDijkstraFromAddress();
            getAddressRouteText();
        }
        blockFreeDijkstra = false;
    }

    public void getAStarmode() {
        isAStar = switch (((RadioButton) dijkstramode.getSelectedToggle()).getText()) {
            case "A*" -> true;
            case "Dijkstra" -> false;
            default -> true;
        };
        updateNewRouteWithNoFreeSPT();
    }

    public void getPathmode() {
        isFastestPath = switch (((RadioButton) pathmode.getSelectedToggle()).getText()) {
            case "Fastest" -> true;
            case "Shortest" -> false;
            default -> true;
        };
        updateNewRouteWithNoFreeSPT();
    }
    public void updateNewRouteWithNoFreeSPT() {
        blockFreeDijkstra = true;
        if (isClickMode) {
            mapData.makeDijkstraFromClick();
            getClickRouteText();
        }
        else {
            mapData.makeDijkstraFromAddress();
            getAddressRouteText();
        }
        blockFreeDijkstra = false;
    }


    public void getAddressRouteText() {
       mapData.makeDijkstraFromAddress();
        routeTextField.clear();
        if (routeInText == null) return;
        StringBuilder sb = new StringBuilder();
        for (String s : routeInText) {
            sb.append(s);
        }
        if (Dijkstra.hasValidPath) sb.append("You have reached your destination");
        else sb.append("No path found, try a new location");
        routeTextField.setText(String.valueOf(sb));
        routeBox.setVisible(true);
        controllerMediator.controller.canvas.repaint();
    }

    public void getClickRouteText() {
        routeTextField.clear();
        if (routePlanForClick == null) return;
        routeBox.setVisible(true);
        routeTextField.setText(routePlanForClick);
        controllerMediator.controller.canvas.repaint();
    }

    public void hideAddresses() {
        routeBox.setVisible(false);
        addressMatchesList.setVisible(false);
    }

    public void setDistanceLabel(String s) {
        distanceLabel.setText(s);
    }

    public void setTravelTimeLabel(String s) {
        travelTimeLabel.setText(s);
    }

    public void toggleVisible() {
        visible = !visible;
        addressMenu.setVisible(visible);
    }

    //https://stackoverflow.com/questions/31556373/javafx-dialog-with-2-input-fields
    public void createPointOfInterest() {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add Point Of Interest");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("Name");
        TextField description = new TextField();
        description.setPromptText("Description");

        gridPane.add(new Label("Name:"), 0, 0);
        gridPane.add(name, 1, 0);
        gridPane.add(new Label("Description:"), 2, 0);
        gridPane.add(description, 3, 0);

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(name::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(name.getText(), description.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            PointOfInterest poi = new PointOfInterest(Controller.rightMouseX,Controller.rightMouseY,pair.getKey(),pair.getValue());
            addressBook.addPointOfInterest(poi);
        });
    }
}
