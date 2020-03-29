package app;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import types.Pair;
import ui.AppWindow;
import ui.InputProcessor;
import view.Camera;
import view.DirectionalLight;
import view.Hud;
import view.MapItem;
import view.Material;
import view.ObjectLoader;
import view.PointLight;
import view.Renderer;
import view.Scene;
import view.Skybox;
import view.SpotLight;
import view.Texture;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11.glGetIntegerv;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.IntBuffer;

// Main application.
public class App {

	private AppWindow wnd = new AppWindow();
	private InputProcessor input = new InputProcessor();
	private Renderer renderer = new Renderer();
	private Camera camera = new Camera();
	private Scene scene = new Scene();
	private Skybox skybox;
	private Hud hud;

	public void run() {
		try {
			setup();
			computeMap();
			loop();
		} catch (Exception e) {
			e.printStackTrace();
		}

		cleanup();
	}

	private void setup() throws Exception {
		setupGlfw();
		setupWindow();
		setupContext();
		setupLights();
		setupHud();
		setupSkybox();
		input.setup(wnd);
		renderer.setup(wnd);
		wnd.show();
	}

	private void cleanup() {
		renderer.cleanup();
		hud.cleanup();
		scene.cleanup();
		skybox.cleanup();
		cleanupWindow();
		cleanupGlfw();
	}

	private void setupGlfw() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
	}

	private void cleanupGlfw() {
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void setupWindow() throws Exception {
		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		wnd.create(1000, 700, "The Map App");
		var wndSize = wnd.size();
		wnd.setPosition((vidmode.width() - wndSize.a) / 2, (vidmode.height() - wndSize.b) / 2);
	}

	private void cleanupWindow() {
		wnd.destroy();
	}

	private void setupContext() {
		// Make the OpenGL context current
		glfwMakeContextCurrent(wnd.handle());
		// Enable v-sync
		glfwSwapInterval(1);
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
	}

	private void setupLights() {
		setupAmbientLight();
        setupPointLight();
        setupSpotLight();
        setupDirectionalLight();
	}
	
	private void setupAmbientLight() {
        scene.setAmbientLight(new Vector3f(0.8f, 0.8f, 0.8f));
	}
	
	private void setupPointLight() {
        Vector3f color = new Vector3f(1, 0, 1);
        Vector3f position = new Vector3f(0, 0, 1);
        float intensity = 20.0f;
        PointLight light = new PointLight(color, position, intensity);
        light.setAttenuation(new PointLight.Attenuation(0.0f, 0.0f, 1.0f));
        scene.setPointLight(light);
	}
	
	private void setupSpotLight() {
        Vector3f color = new Vector3f(1, 1, 1);
		Vector3f position = new Vector3f(0, 0.0f, 10f);
        float intensity = 0.0f;
        PointLight pointLight = new PointLight(color, position, intensity);
        pointLight.setAttenuation(new PointLight.Attenuation(0.0f, 0.0f, 0.02f));
        
        Vector3f coneDir = new Vector3f(0, 0, -1);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        scene.setSpotLight(new SpotLight(pointLight, coneDir, cutoff));
	}
	
	private void setupDirectionalLight() {
        Vector3f color = new Vector3f(1, 1, 1);
		Vector3f position = new Vector3f(-1, 0, 0);
        float intensity = 0.7f;
        scene.setDirectionalLight(new DirectionalLight(color, position, intensity));
	}
	
	private void setupHud() throws Exception {
		hud = new Hud("Hello World.");		
	}
	
	private void setupSkybox() throws Exception {
		 skybox = new Skybox("/models/skybox.obj", "res/textures/skybox.png");
		 skybox.setAmbientLight(scene.ambientLight());
		 skybox.setScale(40.0f);
	}
	
	private void computeMap() throws Exception	{
			float cubeReflectance = 0.3f;
	        MapItem map = new MapItem(
	        		ObjectLoader.loadMesh("/models/cube.obj"),
	        		new Material(
	        				new Texture("res/textures/grassblock.png"),
	        				cubeReflectance));
			map.setPosition(0, 0, -10);
			map.setRotation(10, 20, 20);
			map.setScale(0.8f);
			scene.addItem(map);

			float bunnyReflectance = 0.3f;
			Vector4f bunnyColor = new Vector4f(0.4f, 0.2f, 0.8f, 1.0f);
			MapItem map2 = new MapItem(
					ObjectLoader.loadMesh("/models/bunny.obj"),
	        		new Material(bunnyColor, bunnyReflectance));
			map2.setPosition(5, 5, -10);
			map2.setRotation(10, 20, 20);
			map2.setScale(1.0f);
			scene.addItem(map2);
	}
	
	private void loop() throws Exception {
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!wnd.shouldClose()) {
			input.process();
			resize();
			updateCamera(input);
			renderer.render(scene, skybox, hud, wnd, camera);
			wnd.update();
		}
	}
    
	private void resize() {
		resizeViewport();
        hud.resize(wnd);
	}
    
	private void resizeViewport() {
        var wndSize = wnd.size();
        var viewportSize = viewportSize();
        if (!wndSize.equals(viewportSize))
            glViewport(0, 0, wndSize.a, wndSize.b);
	}
	
    private static Pair<Integer, Integer> viewportSize() {
		try (MemoryStack stack = stackPush()) {
			IntBuffer buf = stack.mallocInt(4);
			// Get x, y, width, and height of viewport.
			glGetIntegerv(GL_VIEWPORT, buf);
			return new Pair<Integer, Integer>(buf.get(2), buf.get(3));
		}
    }
	
    private void updateCamera(InputProcessor input) {
        // Update camera based on keys.          
        final float CAMERA_POS_STEP = 0.05f;
    	Vector3f camKeyDelta = input.cameraKeyDelta();
        camera.movePosition(camKeyDelta.x * CAMERA_POS_STEP, camKeyDelta.y * CAMERA_POS_STEP,
        		camKeyDelta.z * CAMERA_POS_STEP);

        // Update camera based on mouse.          
        if (input.isRightButtonPressed()) {
        	final float MOUSE_SENSITIVITY = 0.2f;
            Vector2f camMouseDelta = input.mouseDelta();
            camera.moveRotation(camMouseDelta.x * MOUSE_SENSITIVITY,
            		camMouseDelta.y * MOUSE_SENSITIVITY, 0);
        }
        
        // Update point light based on keys.
    	Vector3f lightDelta = input.lightKeyDelta();
        camera.movePosition(lightDelta.x, lightDelta.y, lightDelta.z);
    }
	
	public static void main(String[] args) {
		try {
			new App().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
