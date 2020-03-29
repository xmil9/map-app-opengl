package ui;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

import types.Pair;

// UI window.
// Encapsulates the GLFW details of the window implementation.
public class Window {

	// Handle to window.
	private long handle = 0;

	public void create(int width, int height, String title) throws Exception {
		handle = glfwCreateWindow(width, height, title, NULL, NULL);
		if (handle == NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		setupCallbacks();
	}
	
	public void destroy() {
		glfwFreeCallbacks(handle);
		glfwDestroyWindow(handle);
	}

	public long handle() {
		return handle;
	}
	
	public boolean shouldClose() {
		return haveWindow() && glfwWindowShouldClose(handle);
	}
	
	public void setShouldClose(boolean shouldClose) {
		if (haveWindow())
			glfwSetWindowShouldClose(handle, shouldClose);
	}
	
	public boolean haveWindow() {
		return handle != 0;
	}
	
	public void show() {
		glfwShowWindow(handle);
	}
	
	public void show(boolean show) {
		if (show)
			show();
		else
			hide();
	}
	
	public void hide() {
		glfwHideWindow(handle);
	}
	
	public Pair<Integer, Integer> position() {
		if (!haveWindow())
			return new Pair<Integer, Integer>(0, 0);
		
		try (MemoryStack stack = stackPush()) {
			IntBuffer x = stack.mallocInt(1);
			IntBuffer y = stack.mallocInt(1);
			glfwGetWindowPos(handle, x, y);
			return new Pair<Integer, Integer>(x.get(0), y.get(0));
		}
	}
	
	public void setPosition(int x, int y) {
		if (!haveWindow())
			return;
		glfwSetWindowPos(handle, x, y);
	}
	
	public Pair<Integer, Integer> size() {
		if (!haveWindow())
			return new Pair<Integer, Integer>(0, 0);
		
		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			glfwGetWindowSize(handle, w, h);
			return new Pair<Integer, Integer>(w.get(0), h.get(0));
		}
	}
	
	public int width( ) {
		if (!haveWindow())
			return 0;
		
		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			glfwGetWindowSize(handle, w, h);
			return w.get(0);
		}
	}
	
	public int height( ) {
		if (!haveWindow())
			return 0;
		
		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			glfwGetWindowSize(handle, w, h);
			return h.get(0);
		}
	}
	
	public void setSize(int w, int h) {
		if (!haveWindow())
			return;
		glfwSetWindowSize(handle, w, h);
	}
	
    public void update() {
        glfwSwapBuffers(handle);
        glfwPollEvents();
    }
	
    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(handle, keyCode) == GLFW_PRESS;
    }
    
	private void setupCallbacks() {
		glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
			onKey(window, key, scancode, action, mods);
		});
	}
	
	public void onKey(long window, int key, int scancode, int action, int mods) {
	}
}
