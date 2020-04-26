//
// The code in this file is based on code from:
//   https://github.com/lwjglgamedev/lwjglbook
// The original code has been modified to suit this project.
//

package view.scene;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

import org.joml.Vector3f;

import view.render.ColorVbo;
import view.render.NormalVbo;
import view.render.TextureCoordVbo;
import view.render.VertexIndexVbo;
import view.render.VertexVbo;

public class Mesh {

	private final int vaoId;
	// Collection of all unique vertices. The vertices are unordered, i.e.
	// they don't define shapes. They are just a pile of vertices. 
	private final VertexVbo vertexVbo;
	private final NormalVbo normalVbo;
	// Ordered collection of indices into the vertex collection. The indices
	// define the actual shapes.
	private final VertexIndexVbo indexVbo;
	private final ColorVbo colorVbo;
	private final TextureCoordVbo texCoordVbo;
	private Vector3f dim;

    public Mesh(float[] vertices, float[] normals, int[] vertexIndices,
    		float[] texCoords, float[] colors) {
    	vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vertexVbo = new VertexVbo();
        vertexVbo.setVertices(vertices, 0);
        
        if (texCoords != null) {
        	texCoordVbo = new TextureCoordVbo();
            texCoordVbo.setTextureCoords(texCoords, 1);
            colorVbo = null;
        } else if (colors != null) {
            colorVbo = new ColorVbo();
            colorVbo.setColors(colors, 1);
            texCoordVbo = null;
        } else {
        	colorVbo = null;
        	texCoordVbo = null;
        }
        
        if (normals != null) {
	        normalVbo = new NormalVbo();
	        normalVbo.setNormals(normals, 2);
        } else {
        	normalVbo = null;
        }
        
        indexVbo = new VertexIndexVbo();
        indexVbo.setIndices(vertexIndices);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        
        dim = calcDimensions(vertices);
    }

    public int vaoId() {
        return vaoId;
    }

    public int countVertices() {
    	// The number of drawn vertices is determined by the number of vertex indices.
        return indexVbo.count();
    }

    public Vector3f dimensions() {
    	return dim;
    }
    
    public void cleanup() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        vertexVbo.delete();
        if (texCoordVbo != null)
        	texCoordVbo.delete();
        if (colorVbo != null)
        	colorVbo.delete();
        if (normalVbo != null)
        	normalVbo.delete();
        indexVbo.delete();
        
        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
    
    private static Vector3f calcDimensions(float[] vertices) {
    	Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    	Vector3f max = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
    	
    	for (int i = 0; i < vertices.length; i += 3) {
    		if (vertices[i] < min.x)
    			min.x = vertices[i]; 
    		if (vertices[i] > max.x)
    			max.x = vertices[i]; 

    		if (vertices[i+1] < min.y)
    			min.y = vertices[i+1]; 
    		if (vertices[i+1] > max.y)
    			max.y = vertices[i+1]; 

    		if (vertices[i+2] < min.z)
    			min.z = vertices[i+2]; 
    		if (vertices[i+2] > max.z)
    			max.z = vertices[i+2]; 
    	}
    	
    	return max.sub(min);
    }
}
