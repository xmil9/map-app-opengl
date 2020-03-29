package view;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import app.Util;

public class Scene {

	private List<RenderedItem> items = new ArrayList<RenderedItem>();
	private SceneLighting lighting = new SceneLighting();
	private Shader shader;

	public Scene() throws Exception {
		shader = makeSceneShader();
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
	
	public void addItem(RenderedItem item) {
		items.add(item);
	}
	
	public Vector3f ambientLight() {
		return lighting.ambientLight();
	}
	
    public void setAmbientLight(Vector3f light) {
    	lighting.setAmbientLight(light);
    }
    
    public void setPointLight(PointLight light) {
    	lighting.setPointLight(light);
    }
    
    public void setSpotLight(SpotLight light) {
    	lighting.setSpotLight(light);
    }
    
    public void setDirectionalLight(DirectionalLight light) {
    	lighting.setDirectionalLight(light);
    }
    
    public void render(Matrix4f projMat, Matrix4f viewMat) {
        shader.bind();
        shader.setUniform("projectionMatrix", projMat);
        shader.setUniform("texture_sampler", 0);
        shader.setUniform("ambientLight", lighting.ambientLight());
        shader.setUniform("specularPower", lighting.specularPower());
        shader.setUniform("pointLight", lighting.pointLight(viewMat));
        shader.setUniform("spotLight", lighting.spotLight(viewMat));
        shader.setUniform("directionalLight", lighting.directionalLight(viewMat));
        
        for (RenderedItem item : items) {
            shader.setUniform("modelViewMatrix", item.concatTransformation(viewMat));
            shader.setUniform("material", item.material());
            item.render();
        }
        
        shader.unbind();
    }
    
    public void cleanup() {
		if (shader != null)
			shader.cleanup();
		for (RenderedItem item : items)
			item.cleanup();
    }
}
