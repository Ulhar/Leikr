package leikr.controls;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import java.util.HashMap;
import leikr.Engine;
import leikr.customProperties.CustomSystemProperties;

/**
 *
 * @author tor
 */
public class LeikrController implements ControllerListener {

    HashMap<Object, Boolean> buttons = new HashMap<>();
    HashMap<Object, Object> btnCodes = new HashMap<>();

    private static LeikrController instance;
    private static LeikrController instanceTwo;

    public static LeikrController getLeikrControllerListenerA() {
        if (instance == null) {
            instance = new LeikrController();
        }
        return instance;
    }

    public static LeikrController getLeikrControllerListenerB() {
        if (instanceTwo == null) {
            instanceTwo = new LeikrController();
        }
        return instanceTwo;
    }

    public LeikrController() {
        buttons.put(Engine.BTN.A, false);
        buttons.put(Engine.BTN.B, false);
        buttons.put(Engine.BTN.X, false);
        buttons.put(Engine.BTN.Y, false);
        buttons.put(Engine.BTN.LEFT_BUMPER, false);
        buttons.put(Engine.BTN.RIGHT_BUMPER, false);
        buttons.put(Engine.BTN.SELECT, false);
        buttons.put(Engine.BTN.START, false);

        buttons.put(Engine.BTN.LEFT, false);
        buttons.put(Engine.BTN.RIGHT, false);
        buttons.put(Engine.BTN.UP, false);
        buttons.put(Engine.BTN.DOWN, false);

        btnCodes.put(CustomSystemProperties.A, Engine.BTN.A);
        btnCodes.put(CustomSystemProperties.B, Engine.BTN.B);
        btnCodes.put(CustomSystemProperties.X, Engine.BTN.X);
        btnCodes.put(CustomSystemProperties.Y, Engine.BTN.Y);
        btnCodes.put(CustomSystemProperties.LEFT_BUMPER, Engine.BTN.LEFT_BUMPER);
        btnCodes.put(CustomSystemProperties.RIGHT_BUMPER, Engine.BTN.RIGHT_BUMPER);
        btnCodes.put(CustomSystemProperties.SELECT, Engine.BTN.SELECT);
        btnCodes.put(CustomSystemProperties.START, Engine.BTN.START);
    }

    //engine api for returning boolean status of button presses on snes style controller
    public boolean button(Engine.BTN button) {
        return (boolean) buttons.get(button);
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        if (CustomSystemProperties.DEBUG) {
            System.out.println(controller.getName() + " : " + buttonCode);
        }
        buttons.replace(btnCodes.get(buttonCode), true);
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        if (CustomSystemProperties.DEBUG) {
            System.out.println(controller.getName() + " : " + buttonCode);
        }
        buttons.replace(btnCodes.get(buttonCode), false);
        return false;
    }

    //Keypad
    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        //Legacy codes: axis 0 = x axis -1 = left 1 = right
        //Legacy codes: axis 1 = y axis -1 = up 1 = down
        if (CustomSystemProperties.DEBUG) {
            System.out.println(controller.getName() + " : " + axisCode + " | " + value);
        }
        if ((int) value == 0) {
            buttons.replace(Engine.BTN.UP, false);
            buttons.replace(Engine.BTN.DOWN, false);
            buttons.replace(Engine.BTN.LEFT, false);
            buttons.replace(Engine.BTN.RIGHT, false);
        }

        if (axisCode == CustomSystemProperties.VERTICAL_AXIS) {
            if (value == CustomSystemProperties.DOWN) {
                buttons.replace(Engine.BTN.DOWN, true);
            }
            if (value == CustomSystemProperties.UP) {
                buttons.replace(Engine.BTN.UP, true);
            }
        }
        if (axisCode == CustomSystemProperties.HORIZONTAL_AXIS) {
            if (value == CustomSystemProperties.RIGHT) {
                buttons.replace(Engine.BTN.RIGHT, true);
            }
            if (value == CustomSystemProperties.LEFT) {
                buttons.replace(Engine.BTN.LEFT, true);
            }
        }

        return false;
    }

    //Unused methods
    @Override
    public void connected(Controller controller) {
    }

    @Override
    public void disconnected(Controller controller) {
        System.out.println("Controller lost...");
    }

    @Override
    public boolean povMoved(Controller cntrlr, int i, PovDirection pd) {
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller cntrlr, int i, boolean bln) {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller cntrlr, int i, boolean bln) {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller cntrlr, int i, Vector3 vctr) {
        return false;
    }

}