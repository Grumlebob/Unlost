package bfst22.view;

import bfst22.controller.Controller;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;

public class View {
    public View(Stage primaryStage) throws IOException, InterruptedException {
        primaryStage.show();
        var loader = new FXMLLoader(getClass().getClassLoader().getResource("bfst22/View.fxml"));
        primaryStage.setScene(loader.load());
        Controller controller = loader.getController();
        controller.init();
        primaryStage.setMaximized(true);
    }
}
