//
// The code in this file is based on code from:
//   https://github.com/lwjglgamedev/lwjglbook
// The original code has been modified to suit this project.
//

package view.ui;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import view.FontTexture;
import view.Material;
import view.Mesh;
import view.Texture;
import view.Utils;

public class TextItem extends UIItem {

    private static final float ZPOS = 0.0f;
    private static final int VERTICES_PER_CHAR = 4;
    private String text;
    private Vector3f pos;
    private final FontTexture fontTex;
    private Mesh shape;
    private final Material material;
    
    public TextItem(String text, FontTexture fontTexture) throws Exception {
        super();
        this.text = text;
        this.pos = new Vector3f(0, 0, 0);
        this.fontTex = fontTexture;
        this.shape = makeShape();
        this.material = new Material(this.fontTex.texture());
    }
    
    public String text() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
        shape.cleanup();
        shape = makeShape();
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
    	float lastCharWidth = (text.length() > 0) ?
    			fontTex.charInfo(text.charAt(text.length()-1)).width() : 0;
    	return (shape.dimensions().x + lastCharWidth) * scale();
    }
	
    @Override
    public float height() {
    	return (shape.dimensions().y + fontTex.height()) * scale();
    }
    
    private Mesh makeShape() {
        List<Float> positions = new ArrayList<>();
        List<Float> textCoords = new ArrayList<>();
        float[] normals   = new float[0];
        List<Integer> indices   = new ArrayList<>();
        char[] characters = text.toCharArray();
        int numChars = characters.length;

        float startx = 0;
        for(int i=0; i<numChars; i++) {
            FontTexture.CharInfo charInfo = fontTex.charInfo(characters[i]);
            
            // Build a character tile composed by two triangles.
            
            // Left-top vertex.
            positions.add(startx);
            positions.add(0.0f);
            positions.add(ZPOS);
            textCoords.add( (float)charInfo.startX() / (float)fontTex.width());
            textCoords.add(0.0f);
                        
            // Left-bottom vertex.
            positions.add(startx);
            positions.add((float)fontTex.height());
            positions.add(ZPOS);
            textCoords.add((float)charInfo.startX() / (float)fontTex.width());
            textCoords.add(1.0f);

            // Right-bottom vertex.
            positions.add(startx + charInfo.width());
            positions.add((float)fontTex.height());
            positions.add(ZPOS);
            textCoords.add((float)(charInfo.startX() + charInfo.width() )/ (float)fontTex.width());
            textCoords.add(1.0f);

            // Right-top vertex.
            positions.add(startx + charInfo.width());
            positions.add(0.0f);
            positions.add(ZPOS);
            textCoords.add((float)(charInfo.startX() + charInfo.width() )/ (float)fontTex.width());
            textCoords.add(0.0f);
            
            // Add indices for both triangles.
            // 1: left-top, left-bottom, right-bottom
            indices.add(i*VERTICES_PER_CHAR);
            indices.add(i*VERTICES_PER_CHAR + 1);
            indices.add(i*VERTICES_PER_CHAR + 2);
            // 2: right-top, left-top, right-bottom
            indices.add(i*VERTICES_PER_CHAR + 3);
            indices.add(i*VERTICES_PER_CHAR);
            indices.add(i*VERTICES_PER_CHAR + 2);
            
            startx += charInfo.width();
        }
        
        return new Mesh(
        		Utils.toFloatArray(positions),
        		normals,
        		Utils.toIntArray(indices),
        		Utils.toFloatArray(textCoords),
        		null);
    }
}
