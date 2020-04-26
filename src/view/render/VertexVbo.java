//
// The code in this file is based on code from:
//   https://github.com/lwjglgamedev/lwjglbook
// The original code has been modified to suit this project.
//

package view.render;

import java.nio.*;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class VertexVbo {

	public final int id;
	private int numElems = 0;
	
	public VertexVbo() {
		id = glGenBuffers();
	}
	
	public void setVertices(float[] vertices, int attribIdx) {
		numElems = vertices.length;
		
        FloatBuffer buffer = null;
        try {
            buffer = MemoryUtil.memAllocFloat(vertices.length);
            buffer.put(vertices).flip();
	        
            glBindBuffer(GL_ARRAY_BUFFER, id);
	        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);            
	        glEnableVertexAttribArray(attribIdx);
	        glVertexAttribPointer(attribIdx, 3, GL_FLOAT, false, 0, 0);
        } finally {
            if (buffer  != null)
                MemoryUtil.memFree(buffer);
        }
	}
	
	public int count() {
		return numElems;
	}
	
	public void delete() {
        glDeleteBuffers(id);
	}
}
