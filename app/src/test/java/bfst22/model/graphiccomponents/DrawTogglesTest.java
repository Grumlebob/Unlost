package bfst22.model.graphiccomponents;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DrawTogglesTest {

    @Test
    void getInstance() {
        DrawToggles drawToggles = DrawToggles.getInstance();
        drawToggles.drawToggles.put("PATH",true);
        assertTrue(drawToggles.drawToggles.get("PATH"));
    }
}