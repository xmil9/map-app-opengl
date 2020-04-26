//
// The code in this file is based on code from:
//   https://github.com/lwjglgamedev/lwjglbook
// The original code has been modified to suit this project.
//

package view;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;

import view.ui.Window;

import static org.lwjgl.glfw.GLFW.*;

public class InputProcessor {

	private Window wnd;
	// Change in camera position based on the key controls.
	private Vector3f cameraKeyDelta = new Vector3f();
	// Change in point light position based on the key controls.
	private Vector3f lightKeyDelta = new Vector3f();
	// Mouse-based state.
	private Vector2d prevMousePos = new Vector2d(-1, -1);
	private Vector2d curMousePos = new Vector2d(0, 0);
	private boolean isMouseInWnd = false;
	private boolean isLeftButtonPressed = false;
	private boolean isRightButtonPressed = false;
	private Vector2f mouseDelta = new Vector2f();
	
	public void setup(Window wnd) {
		this.wnd = wnd;
		setupCallbacks();
	}
	
	private void setupCallbacks() {
        glfwSetCursorPosCallback(wnd.handle(), (wndHandle, xpos, ypos) -> {
        	curMousePos.x = xpos;
        	curMousePos.y = ypos;
        });
        glfwSetCursorEnterCallback(wnd.handle(), (wndHandle, entered) -> {
        	isMouseInWnd = entered;
        });
        glfwSetMouseButtonCallback(wnd.handle(), (wndHandle, button, action, mode) -> {
        	isLeftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
        	isRightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
	}
	
	public void process() {
		if (wnd == null)
			return;
			
		processKeys();
		processMouse();
	}
	
	private void processKeys() {
        cameraKeyDelta.set(0, 0, 0);
        if (wnd.isKeyPressed(GLFW_KEY_W))
            cameraKeyDelta.z = -1;
        else if (wnd.isKeyPressed(GLFW_KEY_S))
            cameraKeyDelta.z = 1;
        
        if (wnd.isKeyPressed(GLFW_KEY_A))
            cameraKeyDelta.x = -1;
        else if (wnd.isKeyPressed(GLFW_KEY_D))
            cameraKeyDelta.x = 1;
        
        if (wnd.isKeyPressed(GLFW_KEY_Q))
            cameraKeyDelta.y = -1;
        else if (wnd.isKeyPressed(GLFW_KEY_E))
            cameraKeyDelta.y = 1;
        
        lightKeyDelta.set(0, 0, 0);
        if (wnd.isKeyPressed(GLFW_KEY_N))
        	lightKeyDelta.z = 0.1f;
        else if (wnd.isKeyPressed(GLFW_KEY_M))
        	lightKeyDelta.z = -0.1f;
	}

    public void processMouse() {
    	mouseDelta.set(0, 0);

        if (prevMousePos.x > 0 && prevMousePos.y > 0 && isMouseInWnd) {
            double dx = curMousePos.x - prevMousePos.x;
            double dy = curMousePos.y - prevMousePos.y;
            boolean rotateX = dx != 0;
            boolean rotateY = dy != 0;
            if (rotateX)
            	mouseDelta.y = (float) dx;
            if (rotateY)
            	mouseDelta.x = (float) dy;
        }
        
        prevMousePos.x = curMousePos.x;
        prevMousePos.y = curMousePos.y;
    }
    
    public Vector3f cameraKeyDelta() {
    	return cameraKeyDelta;
    }
    
    public Vector3f lightKeyDelta() {
    	return lightKeyDelta;
    }
    
    public Vector2f mouseDelta() {
    	return mouseDelta;
    }

    public boolean isLeftButtonPressed() {
        return isLeftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return isRightButtonPressed;
    }
    
    public Vector2d mousePosition() {
    	return curMousePos;
    }
    
    public MouseState mouseState() {
    	return new MouseState(curMousePos, isLeftButtonPressed, isRightButtonPressed);
    }
}
