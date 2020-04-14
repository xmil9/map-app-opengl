package app;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import geometry.Rect2D;
import map.Map;
import map.MapGeometryGenerator;
import map.PerlinTopography;
import types.Pair;
import ui.AppWindow;
import ui.InputProcessor;
import view.Camera;
import view.DirectionalLight;
import view.Hud;
import view.MapItem;
import view.MapMeshBuilder;
import view.MapScene;
import view.Material;
import view.Mesh;
import view.PointLight;
import view.Renderer;
import view.Scene;
import view.Skybox;
import view.SpotLight;
import view.TileColorPolicy;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11.glGetIntegerv;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.IntBuffer;
import java.util.Random;

// Main application.
public class App {

	///////////////
	
	public class Spec {
		public Long randSeed = 1234567890L;
		
		// View specs.
		public int viewWidth = 1700;
		public int viewHeight = 1200;
		public TileColorPolicy tileColorPolicy = TileColorPolicy.ASSIGN_TILE_SEED_COLORS;
		public boolean haveBeaches = false;
		// Measure for steepness of map features.
		// Larger => steeper map.
		// Smaller => shallower map.
		public float elevScale3D = 50f;
		// Ratio of elevation at which the surface is located.
		// Closer to 0.0 => Lower surface, less water
		// Closer to 1.0 => Higher surface, more water
		public float surfaceElevRatio3D = 0.6f;
		
		// Model specs.
		public int mapWidth = 500;
		public int mapHeight = 500;
		// Smaller distance => smaller and more tiles.
		public double minSampleDistance = 1;
		// More candidates => more evenly spaced sample points but slower generation.
		public int numSampleCandidates = 20;
		// More octaves => Wider and wider areas are affected by values of
		// individual noise values of higher octave passes. Leads to zoomed in
		// appearance on features of the map.
		public int numOctaves = 9;
		// Larger persistence => Larger and smoother features.
		// Smaller persistence => Smaller and choppier features.
		public double persistence = 2;
	}
	
	// Creates a model spec from an app-wide spec.
	private static Map.Spec makeModelSpec(Spec appSpec) {
		Rect2D bounds = new Rect2D(0, 0, appSpec.mapWidth, appSpec.mapHeight);
		return new Map.Spec(
				new MapGeometryGenerator.Spec(bounds, appSpec.minSampleDistance,
						appSpec.numSampleCandidates),
				new PerlinTopography.Spec(bounds, appSpec.numOctaves,
						appSpec.persistence));
	}
	
	private static MapMeshBuilder.Spec makeMeshBuilderSpec(Spec spec) {
		float elevRange3D = spec.elevScale3D / (float) Math.max(
				spec.mapWidth, spec.mapHeight);
		return new MapMeshBuilder.Spec(spec.tileColorPolicy, elevRange3D,
				spec.surfaceElevRatio3D, spec.haveBeaches);
	}
	
	///////////////
	
	private AppWindow wnd = new AppWindow();
	private InputProcessor input = new InputProcessor();
	private Renderer renderer = new Renderer();
	private Camera camera = new Camera();
	private Scene scene;
	private MapScene mapScene;
	private Skybox skybox;
	private Hud hud;
	private Spec spec;
	// App-wide random number generator. Must be used everywhere to guarantee
	// deterministic map generation.
	private Random rand;

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
		setupSpec();
		setupRandomization();
		setupGlfw();
		setupWindow();
		setupContext();
		setupScene();
		setupMapScene();
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

	private void setupSpec() {
		spec = new Spec();
	}
	
	private void setupRandomization() {
		// Generate a random seed if none is given. 
		if (spec.randSeed == null)
			spec.randSeed = Math.abs(new Random().nextLong());
		rand = new Random(spec.randSeed);
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
		wnd.create(spec.viewWidth, spec.viewHeight, "The Map App");
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
	
	private void setupScene() throws Exception {
		scene = new Scene();
	}
	
	private void setupMapScene() throws Exception {
		mapScene = new MapScene();
	}
	
	private void setupHud() throws Exception {
		String seedInfo = "Map seed: " + spec.randSeed; 
		hud = new Hud(seedInfo);		
	}
	
	private void setupSkybox() throws Exception {
		 skybox = new Skybox("/models/skybox.obj", "res/textures/skybox.png");
		 skybox.setAmbientLight(scene.ambientLight());
		 skybox.setScale(40.0f);
	}
	
	private void computeMap() throws Exception	{
		map.Map map = new map.Map(makeModelSpec(spec), rand);
		map.generate();
		Mesh mapMesh = new MapMeshBuilder(map, makeMeshBuilderSpec(spec)).build();
		Vector4f mapColor = new Vector4f(0.4f, 0.2f, 0.8f, 1.0f);
		float mapReflectance = 0.3f;
        MapItem mapItem = new MapItem(mapMesh, new Material(mapColor, mapReflectance));
        mapItem.setPosition(-50, -40, -180);
        mapItem.setRotation(00, 0, 0);
        mapItem.setScale(100f);
		mapScene.addItem(mapItem);
		
//		float cubeReflectance = 0.3f;
//        MapItem cube = new MapItem(
//        		ObjectLoader.loadMesh("/models/cube.obj"),
//        		new Material(
//        				new Texture("res/textures/grassblock.png"),
//        				cubeReflectance));
//        cube.setPosition(0, 0, -10);
//        cube.setRotation(10, 20, 20);
//        cube.setScale(0.8f);
//		scene.addItem(cube);
//
//		float bunnyReflectance = 0.3f;
//		Vector4f bunnyColor = new Vector4f(0.4f, 0.2f, 0.8f, 1.0f);
//		MapItem bunny = new MapItem(
//				ObjectLoader.loadMesh("/models/bunny.obj"),
//        		new Material(bunnyColor, bunnyReflectance));
//		bunny.setPosition(5, 5, -10);
//		bunny.setRotation(10, 20, 20);
//		bunny.setScale(1.0f);
//		scene.addItem(bunny);
	}
	
	private void loop() throws Exception {
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!wnd.shouldClose()) {
			input.process();
			resize();
			updateCamera(input);
			renderer.render(scene, mapScene, skybox, hud, wnd, camera);
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
        final float CAMERA_POS_STEP = 0.2f;
    	Vector3f camKeyDelta = input.cameraKeyDelta();
        camera.movePosition(
        		camKeyDelta.x * CAMERA_POS_STEP,
        		camKeyDelta.y * CAMERA_POS_STEP,
        		camKeyDelta.z * CAMERA_POS_STEP);

        // Update camera based on mouse.          
        if (input.isRightButtonPressed()) {
        	final float MOUSE_SENSITIVITY = 0.2f;
            Vector2f camMouseDelta = input.mouseDelta();
            camera.moveRotation(
            		camMouseDelta.x * MOUSE_SENSITIVITY,
            		camMouseDelta.y * MOUSE_SENSITIVITY,
            		0);
        }
        
        // Update point light based on keys.
//    	Vector3f lightDelta = input.lightKeyDelta();
//        camera.movePosition(lightDelta.x, lightDelta.y, lightDelta.z);
    }
	
	public static void main(String[] args) {
		try {
			new App().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
