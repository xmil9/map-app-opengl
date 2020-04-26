package view.ui;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import types.ConversionUtil;
import view.input.MouseState;
import view.scene.Material;
import view.scene.Mesh;
import view.scene.Texture;

public class ImageButtonItem extends UIItem {

	///////////////
	
	public static interface EventHandler {
		public abstract void onPressed();
	}
	
	///////////////
	
	public static class Textures {
		public final Texture normal;
		public final Texture focused;
		public final Texture pressed;
		public final Texture disabled;
		
		public Textures(Texture normal, Texture focused, Texture pressed,
				Texture disabled) {
			this.normal = normal;
			this.focused = focused;
			this.pressed = pressed;
			this.disabled = disabled;
		}
		
		public Textures(Texture normal, Texture focused, Texture pressed) {
			this(normal, focused, pressed, null);
		}
		
		public Textures(Texture normal, Texture focused) {
			this(normal, focused, null, null);
		}
		
		public Textures(Texture normal) {
			this(normal, null, null, null);
		}
	}
	
	///////////////
	
    private static final float ZPOS = 0.0f;
    private Vector3f pos;
    private final Textures imageTexs;
    private Mesh shape;
    private final Material material;
    private final EventHandler callbacks;
    
    public ImageButtonItem(Textures texs, EventHandler callbacks) throws Exception {
        super();
        this.pos = new Vector3f(0, 0, 0);
        this.imageTexs = texs;
        this.shape = makeShape();
        this.material = new Material(texs.normal);
        this.callbacks = callbacks;
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
	public Vector3f rotation() {
		return new Vector3f(0, 0, 0);
	}
	
    @Override
	public float scale() {
		return 1f;
	}
    
    @Override
	public void cleanup() {
		Texture tex = material.texture();
		if (tex != null)
			tex.cleanup();
		
		shape.cleanup();
	}
	
    @Override
    public float width() {
    	return shape.dimensions().x * scale();
    }
	
    @Override
    public float height() {
    	return shape.dimensions().y * scale();
    }
    
    @Override
    public void enable(boolean enable) {
    	super.enable(enable);
    	material.setTexture(enable ? imageTexs.normal : imageTexs.disabled);
    }
    
    @Override
    public void onMouseEntered(MouseState curState) {
    	if (isEnabled())
    		material.setTexture(imageTexs.focused);
    }
    
    @Override
    public void onMouseExited(MouseState curState) {
    	if (isEnabled())
    		material.setTexture(imageTexs.normal);
    }
    
    @Override
    public void onMouseMoved(MouseState curState) {
    	if (isEnabled())
	    	material.setTexture(curState.leftButtonDown ?
	    			imageTexs.pressed : imageTexs.focused);
    }
    
    @Override
    public void onMouseButtonDown(MouseState.Button button, MouseState curState) {
    	if (isEnabled()) {
	    	if (button == MouseState.Button.Left) {
		    	material.setTexture(imageTexs.pressed);
	    	}
    	}
    }
    
    @Override
    public void onMouseButtonUp(MouseState.Button button, MouseState curState) {
    	if (isEnabled()) {
	    	if (button == MouseState.Button.Left) {
		    	material.setTexture(imageTexs.focused);
		    	callbacks.onPressed();
	    	}
    	}
    }
    
    private Mesh makeShape() {
        List<Float> positions = new ArrayList<>();
        List<Float> texCoords = new ArrayList<>();
        float[] normals = new float[0];
        List<Integer> indices = new ArrayList<>();

        // Build a square tile composed by two triangles.
        
        // Left-top vertex.
        positions.add(0.0f);
        positions.add(0.0f);
        positions.add(ZPOS);
        texCoords.add(0.0f);
        texCoords.add(0.0f);
                    
        // Left-bottom vertex.
        positions.add(0.0f);
        positions.add((float) imageTexs.normal.height());
        positions.add(ZPOS);
        texCoords.add(0.0f);
        texCoords.add(1.0f);

        // Right-bottom vertex.
        positions.add((float) imageTexs.normal.width());
        positions.add((float) imageTexs.normal.height());
        positions.add(ZPOS);
        texCoords.add(1.0f);
        texCoords.add(1.0f);

        // Right-top vertex.
        positions.add((float) imageTexs.normal.width());
        positions.add(0.0f);
        positions.add(ZPOS);
        texCoords.add(1.0f);
        texCoords.add(0.0f);
        
        // Add indices for both triangles.
        // 1: left-top, left-bottom, right-bottom
        indices.add(0);
        indices.add(1);
        indices.add(2);
        // 2: right-top, left-top, right-bottom
        indices.add(3);
        indices.add(0);
        indices.add(2);
        
        return new Mesh(
        		ConversionUtil.toFloatArray(positions),
        		normals,
        		ConversionUtil.toIntArray(indices),
        		ConversionUtil.toFloatArray(texCoords),
        		null);
    }
}
