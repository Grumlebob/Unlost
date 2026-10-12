package bfst22.view;

import bfst22.controller.FileBrowserController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class FileBrowser {
    FileBrowserController controller;

    /**
     * Creates the FileBrowser stage, and shows it to the user.
     * @throws IOException if the specified fxml path is invalid.
     */
    public FileBrowser() throws IOException {
        Stage fileBrowserStage = new Stage();
        var loader = new FXMLLoader(getClass().getClassLoader().getResource("bfst22/FileBrowser.fxml"));
        fileBrowserStage.setScene(new Scene(loader.load()));
        fileBrowserStage.setTitle("Choose a file for the map");
        fileBrowserStage.getIcons().add(new Image(Objects.requireNonNull(View.class.getClassLoader().getResourceAsStream("logo.png"))));


        controller = loader.getController();
        controller.init(fileBrowserStage);
        fileBrowserStage.showAndWait();
    }

    public FileBrowserController getFileBrowserController() {
        return controller;
    }

}
