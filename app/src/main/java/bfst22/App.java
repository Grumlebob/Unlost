package bfst22;

import bfst22.view.SplashScreen;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        new SplashScreen(primaryStage);
    }
}
