package bfst22.controller;

import java.io.Serializable;

//https://stackoverflow.com/questions/22178533/communication-between-two-javafx-controllers
public class ControllerMediator implements Serializable {
    public static final long serialVersionUID = 2834756923L;

    private DebugMenuController debugMenuController;

    private AddressMenuController AddressMenuController;

    private KdTreeDebugMenuController kdTreeDebugMenuController;

    public Controller controller;

    public void setMainController(Controller c) {
        controller = c;
    }

    public AddressMenuController getAddressMenuController() {
        return AddressMenuController;
    }

    public void panToAddress(float x, float y) {
        controller.canvas.panToAddress(x, y);
    }

    public DebugMenuController getDebugMenuController() {
        return debugMenuController;
    }

    public KdTreeDebugMenuController getKdTreeDebugMenuController() {
        return kdTreeDebugMenuController;
    }

    public void registerController(DebugMenuController controller) {
        debugMenuController = controller;
    }

    public void registerAddressController(AddressMenuController controller) {
        AddressMenuController = controller;
    }

    public void registerKdTreeController(KdTreeDebugMenuController controller){
        kdTreeDebugMenuController = controller;
    }

    /**
     * Everything below here is in support of Singleton pattern
     */
    private ControllerMediator() {
    }

    public static ControllerMediator getInstance() {
        return ControllerMediatorHolder.INSTANCE;
    }

    private static class ControllerMediatorHolder {
        private static final ControllerMediator INSTANCE = new ControllerMediator();
    }


}