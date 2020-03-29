package ui;

import static org.lwjgl.glfw.GLFW.*;

public class AppWindow extends Window {

	@Override
	public void onKey(long window, int key, int scancode, int action, int mods) {
		if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
			glfwSetWindowShouldClose(window, true);
	}
}
