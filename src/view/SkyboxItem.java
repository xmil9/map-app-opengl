package view;

import org.joml.Vector3f;

public class SkyboxItem extends RenderedItem {

	private final Mesh shape;
	private final Material material;
	private float scale;
	// Rotation angles in radians.
	private Vector3f rot;
	
    public SkyboxItem(String objModel, String textureFile) throws Exception {
        this.shape = ObjectLoader.loadMesh(objModel);
        this.material = new Material(new Texture(textureFile));
		this.scale = 1;
		this.rot = new Vector3f(0, 0, 0);
    }

    @Override
    public Mesh shape() {
    	return shape;
    }
	
    @Override
	public Material material() {
		return material;
	}

    @Override
	public Vector3f position() {
    	// Always at origin (around the camera).
		return new Vector3f(0, 0, 0);
	}
	
    @Override
	public void setPosition(float x, float y, float z) {
	}
	
    @Override
	public float scale() {
		return scale;
	}
	
    public void setScale(float scale) {
    	this.scale = scale;
    }
    
    @Override
	public Vector3f rotation() {
		return new Vector3f(rot.x, rot.y, rot.z);
	}
	
    @Override
	public void cleanup() {
		Texture tex = material.texture();
		if (tex != null)
			tex.cleanup();
		
		shape.cleanup();
	}
}
