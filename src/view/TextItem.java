package view;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

public class TextItem extends RenderedItem {

    private static final float ZPOS = 0.0f;
    private static final int VERTICES_PER_QUAD = 4;
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
            
            // Build a character tile composed by two triangles
            
            // Left Top vertex
            positions.add(startx); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add( (float)charInfo.getStartX() / (float)fontTex.width());
            textCoords.add(0.0f);
            indices.add(i*VERTICES_PER_QUAD);
                        
            // Left Bottom vertex
            positions.add(startx); // x
            positions.add((float)fontTex.height()); //y
            positions.add(ZPOS); //z
            textCoords.add((float)charInfo.getStartX() / (float)fontTex.width());
            textCoords.add(1.0f);
            indices.add(i*VERTICES_PER_QUAD + 1);

            // Right Bottom vertex
            positions.add(startx + charInfo.getWidth()); // x
            positions.add((float)fontTex.height()); //y
            positions.add(ZPOS); //z
            textCoords.add((float)(charInfo.getStartX() + charInfo.getWidth() )/ (float)fontTex.width());
            textCoords.add(1.0f);
            indices.add(i*VERTICES_PER_QUAD + 2);

            // Right Top vertex
            positions.add(startx + charInfo.getWidth()); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add((float)(charInfo.getStartX() + charInfo.getWidth() )/ (float)fontTex.width());
            textCoords.add(0.0f);
            indices.add(i*VERTICES_PER_QUAD + 3);
            
            // Add indices por left top and bottom right vertices
            indices.add(i*VERTICES_PER_QUAD);
            indices.add(i*VERTICES_PER_QUAD + 2);
            
            startx += charInfo.getWidth();
        }
        
        float[] posArr = Utils.listToArray(positions);
        float[] textCoordsArr = Utils.listToArray(textCoords);
        int[] indicesArr = indices.stream().mapToInt(i->i).toArray();
        return new Mesh(posArr, normals, indicesArr, textCoordsArr, null);
    }
}
