package view;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

import app.Util;
import types.Pair;
import ui.Window;

public class Renderer {

	// Projection settings.
	private static final float FOV = (float) Math.toRadians(60.0);
	private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private Matrix4f projMat3D;
    private Matrix4f projMat2D = new Matrix4f();
    private Matrix4f viewMat = new Matrix4f();
	private Shader sceneShader;
	private Shader hudShader;
	private Shader skyboxShader;
	
	public void setup(Window wnd) throws Exception {
		sceneShader = makeSceneShader();
		hudShader = makeHudShader();
		skyboxShader = makeSkyBoxShader();
		
	    var wndSize = wnd.size();
        float aspectRatio = (float) wndSize.a / wndSize.b;
        projMat3D = make3DProjectionMatrix(aspectRatio);

        // Set the background color.
		glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		glEnable(GL_DEPTH_TEST);
		
		// Support for transparency.
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);		
	}
	
	private static Shader makeSceneShader() throws Exception {
	    Shader shader = new Shader();
	    shader.createVertexShader(Util.loadResource("/view/SceneVertex.vs"));
	    shader.createFragmentShader(Util.loadResource("/view/SceneFragment.fs"));
	    shader.link();
        shader.createUniform("projectionMatrix");
        shader.createUniform("modelViewMatrix");
        shader.createUniform("texture_sampler");
        shader.createMaterialUniform("material");
        shader.createUniform("specularPower");
        shader.createUniform("ambientLight");
        shader.createPointLightUniform("pointLight");
        shader.createPointLightUniform("pointLight");
        shader.createSpotLightUniform("spotLight");
        shader.createDirectionalLightUniform("directionalLight");
        return shader;
	}

    private static Shader makeSkyBoxShader() throws Exception {
	    Shader shader = new Shader();
        shader.createVertexShader(Utils.loadResource("/view/SkyboxVertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/view/SkyboxFragment.fs"));
        shader.link();
        shader.createUniform("projectionMatrix");
        shader.createUniform("modelViewMatrix");
        shader.createUniform("texture_sampler");
        shader.createUniform("ambientLight");
        return shader;
    }
	
	private static Shader makeHudShader() throws Exception {
	    Shader shader = new Shader();
	    shader.createVertexShader(Util.loadResource("/view/HudVertex.vs"));
	    shader.createFragmentShader(Util.loadResource("/view/HudFragment.fs"));
	    shader.link();
        shader.createUniform("projModelMatrix");
        shader.createUniform("color");
        return shader;
	}
	
	public void cleanup() {
		if (sceneShader != null)
			sceneShader.cleanup();
		if (hudShader != null)
			hudShader.cleanup();
	}

    public void render(Scene scene, Skybox skybox, Hud hud, Window wnd, Camera cam) {
        clear();
        
        update2DProjectionMatrix(wnd);
        updateViewMatrix(cam);
        
        renderScene(scene);
        renderSkybox(skybox);
        renderHud(hud);
    }

    private void renderScene(Scene scene) {
        scene.render(sceneShader, projMat3D, viewMat);
    }

    private void renderSkybox(Skybox skybox) {
        skybox.render(skyboxShader, projMat3D, viewMat);
    }
    
    private void renderHud(Hud hud) {
        hud.render(hudShader, projMat2D);
    }
    
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    
    private static Matrix4f make3DProjectionMatrix(float aspectRatio) {
    	return new Matrix4f().setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }
    
    // Transformation for camera position and orientation.
    private Matrix4f updateViewMatrix(Camera cam) {
        Vector3f cameraPos = cam.position();
        Vector3f rotation = cam.rotation();

        viewMat.identity();
        viewMat.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
            .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        viewMat.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMat;
    }
    
    // Creates matrix for 2D orthographic projection. 
    private Matrix4f update2DProjectionMatrix(Window wnd) {
    	Pair<Integer, Integer> wndSize = wnd.size();
    	return projMat2D.setOrtho2D(0, wndSize.a, wndSize.b, 0);
    }
}
