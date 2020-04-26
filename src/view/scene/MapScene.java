package view.scene;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;

import io.IOUtil;
import view.render.RenderedItem;
import view.render.Shader;

public class MapScene {

	private List<RenderedItem> items = new ArrayList<RenderedItem>();
	private Shader shader;

	public MapScene() throws Exception {
		shader = makeSceneShader();
	}
	
	private static Shader makeSceneShader() throws Exception {
	    Shader shader = new Shader();
	    shader.createVertexShader(IOUtil.loadResource("/view/scene/MapSceneVertexShader.vs"));
	    shader.createFragmentShader(IOUtil.loadResource("/view/scene/MapSceneFragmentShader.fs"));
	    shader.link();
        shader.createUniform("projectionMatrix");
        shader.createUniform("modelViewMatrix");
        return shader;
	}
	
	public void addItem(RenderedItem item) {
		if (item != null)
			items.add(item);
	}
    
	public void removeItem(RenderedItem item) {
		if (item != null)
			items.remove(item);
	}
	
	public int countItems() {
		return items.size();
	}
	
	public void clear() {
		cleanupItems();
		items.clear();
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
		cleanupItems();
    }
    
    private void cleanupItems() {
		for (RenderedItem item : items)
			item.cleanup();
    }
}
