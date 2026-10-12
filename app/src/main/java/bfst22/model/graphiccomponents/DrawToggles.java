package bfst22.model.graphiccomponents;

import java.util.HashMap;

public class DrawToggles {

    private static DrawToggles instance;
    public HashMap<String, Boolean> drawToggles = new HashMap<>();

    //Singleton
    public static DrawToggles getInstance() {
        if (instance == null) {
            instance = new DrawToggles();
        }
        return instance;
    }
}
