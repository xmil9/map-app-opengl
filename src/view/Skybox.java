package view;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Skybox {

	private SkyboxItem item;
	private Vector3f ambientLight;
	private Shader shader;
	
	public Skybox(String objModel, String textureFile) throws Exception {
		item = new SkyboxItem(objModel, textureFile);
		shader = makeSkyBoxShader();
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
	
	public void setScale(float scale) {
		if (item != null)
			item.setScale(scale);
	}
	
	public void setAmbientLight(Vector3f light) {
		ambientLight = light;
	}
	
    public void render(Matrix4f projMat, Matrix4f viewMat) {
		if (item == null)
			return;
		
    	float prev30 = viewMat.m30();
    	float prev31 = viewMat.m31();
    	float prev32 = viewMat.m32();
    	// For skybox remove translation from transformation. 
        viewMat.m30(0);
        viewMat.m31(0);
        viewMat.m32(0);
    	
        shader.bind();
        shader.setUniform("projectionMatrix", projMat);
        shader.setUniform("texture_sampler", 0);
        shader.setUniform("modelViewMatrix", item.concatTransformation(viewMat));
        shader.setUniform("ambientLight", ambientLight);
        
        item.render();
        
        shader.unbind();
        
        viewMat.m30(prev30);
        viewMat.m31(prev31);
        viewMat.m32(prev32);
    }	
    
    public void cleanup() {
		if (shader != null)
			shader.cleanup();
    	if (item != null)
    		item.cleanup();
    }
}
