package app;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import geometry.Rect2D;
import map.Map;
import map.MapGenerator;
import map.MapGeometryGenerator;
import map.PerlinTopography;
import math.RandomGenerator;
import types.Pair;
import view.Camera;
import view.DirectionalLight;
import view.GrayscaleColorTheme;
import view.InputProcessor;
import view.MapColorPolicy;
import view.MapColorTheme;
import view.MapGenerationTask;
import view.MapItem;
import view.MapMeshBuilder;
import view.MapScene;
import view.Material;
import view.Mesh;
import view.NodeElevationColorTheme;
import view.PlaceholderMapItem;
import view.PointLight;
import view.Renderer;
import view.Scene;
import view.SceneLighting;
import view.SeedElevationColorTheme;
import view.Skybox;
import view.SpotLight;
import view.ui.AppWindow;
import view.ui.UI;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11.glGetIntegerv;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.IntBuffer;
import java.util.Random;

// Main application.
public class App implements UI.UIEventHandler {

	///////////////
	
	public static class Spec {
		public Long randSeed = 1234567890L;
		
		// View specs.
		public int viewWidth = 1700;
		public int viewHeight = 1000;
		public MapColorPolicy mapColorPolicy = MapColorPolicy.SeedElevationColors;
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
	
	private static MapMeshBuilder.Spec makeMeshBuilderSpec(Spec spec, Random rand) {
		float elevRange = calcElevationRange(spec);
		return new MapMeshBuilder.Spec(
				makeColorTheme(spec, rand),
				elevRange,
				spec.surfaceElevRatio3D,
				spec.haveBeaches);
	}
	
	private static MapColorTheme makeColorTheme(Spec spec, Random rand) {
		float elevMin = calcElevationMin(spec);
		float elevRange = calcElevationRange(spec);
		
		switch (spec.mapColorPolicy) {
		case NodeElevationColors:
			return new NodeElevationColorTheme(elevMin, elevRange, spec.surfaceElevRatio3D,
					spec.haveBeaches);
		default:
		case SeedElevationColors:
			return new SeedElevationColorTheme(elevMin, elevRange, spec.surfaceElevRatio3D,
					spec.haveBeaches);
		case GrayscaleColors:
			return new GrayscaleColorTheme(rand);
		}
	}
	
	private static float calcElevationRange(Spec spec) {
		return spec.elevScale3D / Math.max(spec.mapWidth, spec.mapHeight);
	}

	private static float calcElevationMin(Spec spec) {
		return -calcElevationRange(spec) / 2f;
	}
	
	///////////////
	
	private Spec spec;
	// App-wide random number generator. Must be used everywhere to guarantee
	// deterministic map generation.
	private RandomGenerator randGen;
	private AppWindow wnd = new AppWindow();
	private InputProcessor input = new InputProcessor();
	private Renderer renderer = new Renderer();
	private Camera camera = new Camera();
	private Scene scene;
	private MapScene mapScene;
	private Skybox skybox;
	private UI ui;
	private MapGenerationTask mapGen = new MapGenerationTask();
	private MapItem placeholderItem;

	public void run() {
		try {
			setup();
			loop();
		} catch (Exception e) {
			e.printStackTrace();
		}

		cleanup();
	}

	private void setup() throws Exception {
		setupSpec();
		setupRandomization();
		startMapGeneration();
		setupGlfw();
		setupWindow();
		setupContext();
		setupScene();
		setupMapScene();
		setupUI();
		setupSkybox();
		input.setup(wnd);
		renderer.setup(wnd);
		setupPlaceholderItem();
		wnd.show();
	}

	private void cleanup() {
		renderer.cleanup();
		ui.cleanup();
		scene.cleanup();
		skybox.cleanup();
		cleanupWindow();
		cleanupGlfw();
	}

	private void setupSpec() {
		spec = new Spec();
	}
	
	private void setupRandomization() {
		if (spec.randSeed != 0)
			randGen = new RandomGenerator(spec.randSeed);
		else
			randGen = new RandomGenerator();
	}
	
	private void startMapGeneration() {
		if (ui != null) {
			ui.setSeedInfo(makeSeedInfo(randGen.seed()));
			ui.setStatusText("Generating map...");
			ui.enable(false);
		}
		
		if (mapScene != null) {
			mapScene.clear();
			mapScene.addItem(placeholderItem);
		}		
		
		mapGen.start(makeModelSpec(spec), randGen.rand());
	}
	
	private void finishMapGeneration() {
		mapScene.removeItem(placeholderItem);

		createMapItem();
		mapGen.clean();

		if (ui != null) {
			ui.setStatusText("");
			ui.enable(true);
		}
	}
	
	private void checkMapGeneration() {
		if (!mapGen.hasStarted())
			return;
		
		if (mapGen.hasFinished())
			finishMapGeneration();
		else
			animatePlaceholderMap();
	}
	
	private void createMapItem() {
		Mesh mapMesh = new MapMeshBuilder(
				mapGen.map(),
				makeMeshBuilderSpec(spec, randGen.rand())
				).build();
		Vector4f mapColor = new Vector4f(0.4f, 0.2f, 0.8f, 1.0f);
		float mapReflectance = 0.3f;
        MapItem mapItem = new MapItem(mapMesh, new Material(mapColor, mapReflectance));
        mapItem.setPosition(-50, -30, -180);
        mapItem.setRotation(0, 0, 0);
        mapItem.setScale(100f);
		mapScene.addItem(mapItem);
	}
	
	private void setupPlaceholderItem() {
		placeholderItem = createPlaceholderItem();
		mapScene.addItem(placeholderItem);
	}
	
	private static MapItem createPlaceholderItem() {
		Spec placeholderSpec = new Spec();
		placeholderSpec.randSeed = 1000L;
		placeholderSpec.viewWidth = 0;
		placeholderSpec.viewHeight = 0;
		placeholderSpec.mapColorPolicy = MapColorPolicy.GrayscaleColors;
		placeholderSpec.haveBeaches = false;
		placeholderSpec.elevScale3D = 2f;
		placeholderSpec.surfaceElevRatio3D = 0.5f;
		placeholderSpec.mapWidth = 20;
		placeholderSpec.mapHeight = 20;
		placeholderSpec.minSampleDistance = 1;
		placeholderSpec.numSampleCandidates = 10;
		placeholderSpec.numOctaves = 6;
		placeholderSpec.persistence = 0.8f;
		
		Random placeholderRand = new Random(placeholderSpec.randSeed);
		
		MapGenerator placeholderGen = new MapGenerator(
				makeModelSpec(placeholderSpec),
				placeholderRand);
		placeholderGen.run();
		
		Mesh mapMesh = new MapMeshBuilder(
				placeholderGen.map(),
				makeMeshBuilderSpec(placeholderSpec, placeholderRand)
				).build();
		
		Vector4f mapColor = new Vector4f(0.4f, 0.2f, 0.8f, 1.0f);
		float mapReflectance = 0.0f;
        
		PlaceholderMapItem placeholderItem = new PlaceholderMapItem(
        		mapMesh,
        		new Material(mapColor, mapReflectance));
        placeholderItem.setPosition(0, -30, -180);
        placeholderItem.setRotation(0, 0, 0);
        placeholderItem.setScale(40f);
        
        return placeholderItem;
	}
	
	private void animatePlaceholderMap() {
		Vector3f rot = placeholderItem.rotation();
		placeholderItem.setRotation(
				rot.x + (float) Math.toRadians(0.0f),
				rot.y + (float) Math.toRadians(0.5f),
				rot.z + (float) Math.toRadians(0.0f));
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

	private static SceneLighting makeLighting() {
		return new SceneLighting(
				makeAmbientLight(),
				makePointLight(),
				makeSpotLight(),
				makeDirectionalLight());
	}
	
	private static Vector3f makeAmbientLight() {
        return new Vector3f(0.8f, 0.8f, 0.8f);
	}
	
	private static PointLight makePointLight() {
        Vector3f color = new Vector3f(1, 0, 1);
        Vector3f position = new Vector3f(0, 0, 1);
        float intensity = 20.0f;
        PointLight light = new PointLight(color, position, intensity);
        light.setAttenuation(new PointLight.Attenuation(0.0f, 0.0f, 1.0f));
        return light;
	}
	
	private static SpotLight makeSpotLight() {
        Vector3f color = new Vector3f(1, 1, 1);
		Vector3f position = new Vector3f(0, 0.0f, 10f);
        float intensity = 0.0f;
        PointLight pointLight = new PointLight(color, position, intensity);
        pointLight.setAttenuation(new PointLight.Attenuation(0.0f, 0.0f, 0.02f));
        
        Vector3f coneDir = new Vector3f(0, 0, -1);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        return new SpotLight(pointLight, coneDir, cutoff);
	}
	
	private static DirectionalLight makeDirectionalLight() {
        Vector3f color = new Vector3f(1, 1, 1);
		Vector3f position = new Vector3f(-1, 0, 0);
        float intensity = 0.7f;
        return new DirectionalLight(color, position, intensity);
	}
	
	private void setupScene() throws Exception {
		scene = new Scene();
		scene.setLighting(makeLighting());
	}
	
	private void setupMapScene() throws Exception {
		mapScene = new MapScene();
	}
	
	private void setupUI() throws Exception {
		ui = new UI(makeSeedInfo(spec.randSeed), this);
		ui.enable(!mapGen.hasStarted());
		ui.setStatusText(mapGen.hasStarted() ? "Generating map..." : "");
	}
	
	private static String makeSeedInfo(long seed) {
		return "Map seed: " + seed;
	}
	
	private void setupSkybox() throws Exception {
		 skybox = new Skybox("/models/skybox.obj", "res/textures/skybox.png");
		 skybox.setAmbientLight(scene.ambientLight());
		 skybox.setScale(40.0f);
	}
	
	private void loop() throws Exception {
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!wnd.shouldClose()) {
			input.process();
			resize();
			processUI();
			updateCamera(input);
			checkMapGeneration();
			renderer.render(scene, mapScene, skybox, ui, wnd, camera);
			wnd.update();
		}
	}
    
	private void resize() {
		resizeViewport();
        ui.resize(wnd);
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
	
    private void processUI() {
    	ui.processMouse(input.mouseState());
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
	
    public void onReset()
    {
    	randGen.reset();
    	startMapGeneration();
    }
    
	public static void main(String[] args) {
		try {
			new App().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
