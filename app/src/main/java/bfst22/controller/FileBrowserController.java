package bfst22.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileBrowserController {

    @FXML
    Button okButton, browseButton, cancelButton;

    @FXML
    TextField filePathField;

    Stage fileBrowserStage;
    String filePath;

    public void init(Stage fileBrowser) {
        this.fileBrowserStage = fileBrowser;
    }

    @FXML
    public void onBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Map File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Map Files", "*.map", "*.osm", "*.zip"));
        File selectedFile = fileChooser.showOpenDialog(fileBrowserStage);
        if(selectedFile != null) filePathField.setText(selectedFile.getPath());
    }

    @FXML
    public void onOK() {
        String entry = filePathField.getText();
        if (entry.length() > 0) {
            filePath = entry;
        }
        fileBrowserStage.close();

    }

    @FXML
    public void onCancel() {
        fileBrowserStage.close();
    }

    public String getFilePath() {
        return filePath;
    }
}
