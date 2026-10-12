package bfst22.controller;

import bfst22.model.osm.MapDataParser;
import bfst22.view.FileBrowser;
import bfst22.view.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class SplashScreenController {

    Stage primaryStage;

    @FXML
    Button loadBornholmButton;

    @FXML
    Button loadAnotherFileButton;

    @FXML
    FlowPane buttonFlowPane;

    @FXML
    Label provideFileLabel;

    @FXML
    ToggleButton toggleKDTreeButton;
    public static boolean createKDTreeOnlyForWays = true;

    @FXML
    public void initialize() {

    }

    public void onToggleKDTree() {
        createKDTreeOnlyForWays = toggleKDTreeButton.isSelected();
    }

    public void onLoadBornholm() {
        loadMap("data/bornholm.zip");
    }

    public void onLoadFyn() {
        loadMap("data/fyn.zip");
    }

    public void onLoadAnotherFile() throws IOException {
        var fileBrowser = new FileBrowser();
        String filePath = fileBrowser.getFileBrowserController().getFilePath();

        if(filePath != null) {
            if(!(filePath.endsWith(".zip") || filePath.endsWith(".osm") || filePath.endsWith(".map"))) {
                provideFileLabel.setTextAlignment(TextAlignment.CENTER);
                provideFileLabel.setText("Please provide either a .map, .osm or .zip file");
            }
            else {
                loadMap(filePath);
            }
        }
        else {
            provideFileLabel.setTextAlignment(TextAlignment.CENTER);
            provideFileLabel.setText("Please Provide a file for the map to load...");
        }
    }

    public void loadMap(String filepath) {

        try {
            new MapDataParser(filepath);
            new View(primaryStage);
        }
        catch(IOException | InterruptedException | XMLStreamException | ClassNotFoundException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR, exception.getMessage()
                    + " The Program will now close.");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> System.exit(0));
        }
    }

    public void init(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

}
