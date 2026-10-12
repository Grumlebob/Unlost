package bfst22.view;

import bfst22.controller.Controller;
import bfst22.controller.SplashScreenController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SplashScreen {

    public SplashScreen(Stage primaryStage) throws IOException {
        primaryStage.show();
        var loader = new FXMLLoader(getClass().getClassLoader().getResource("bfst22/SplashScreen.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        SplashScreenController controller = loader.getController();
        controller.init(primaryStage);
        primaryStage.setTitle("Unlost");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(View.class.getClassLoader().getResourceAsStream("logo.png"))));
        //primaryStage.setMaximized(true);
        primaryStage.setMinWidth(860);
        primaryStage.setMinHeight(600);
        //primaryStage.setMaximized(true);
    }
}
