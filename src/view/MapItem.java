//
// The code in this file is based on code from:
//   https://github.com/lwjglgamedev/lwjglbook
// The original code has been modified to suit this project.
//

package view;

import org.joml.Vector3f;

public class MapItem extends RenderedItem {

	private final Mesh shape;
	private Material material;
	private Vector3f pos;
	private float scale;
	// Rotation angles in radians.
	private Vector3f rot;

	
	public MapItem(Mesh shape, Material material) {
		this.shape = shape;
		this.material = material;
		this.pos = new Vector3f(0, 0, 0);
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
		return new Vector3f(pos.x, pos.y, pos.z);
	}
	
    @Override
	public void setPosition(float x, float y, float z) {
		pos.x = x;
		pos.y = y;
		pos.z = z;
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
	
	public void setRotation(float degX, float degY, float degZ) {
		rot.x = (float) Math.toRadians(degX);
		rot.y = (float) Math.toRadians(degY);
		rot.z = (float) Math.toRadians(degZ);
	}
	
    @Override
	public void cleanup() {
		Texture tex = material.texture();
		if (tex != null)
			tex.cleanup();
		
		shape.cleanup();
	}
}
