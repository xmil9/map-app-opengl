//
// The code in this file is based on code from:
//   https://github.com/lwjglgamedev/lwjglbook
// The original code has been modified to suit this project.
//

package view;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

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
    }

    public int vaoId() {
        return vaoId;
    }

    public int countVertices() {
    	// The number of drawn vertices is determined by the number of vertex indices.
        return indexVbo.count();
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
}
