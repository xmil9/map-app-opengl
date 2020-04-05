package view;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;

import app.Util;

public class MapScene {

	private List<RenderedItem> items = new ArrayList<RenderedItem>();
	private Shader shader;

	public MapScene() throws Exception {
		shader = makeSceneShader();
	}
	
	private static Shader makeSceneShader() throws Exception {
	    Shader shader = new Shader();
	    shader.createVertexShader(Util.loadResource("/view/MapSceneVertex.vs"));
	    shader.createFragmentShader(Util.loadResource("/view/MapSceneFragment.fs"));
	    shader.link();
        shader.createUniform("projectionMatrix");
        shader.createUniform("modelViewMatrix");
        return shader;
	}
	
	public void addItem(RenderedItem item) {
		items.add(item);
	}
    
    public void render(Matrix4f projMat, Matrix4f viewMat) {
        shader.bind();
        shader.setUniform("projectionMatrix", projMat);
        
        for (RenderedItem item : items) {
            shader.setUniform("modelViewMatrix", item.concatTransformation(viewMat));
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
