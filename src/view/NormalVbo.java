//
// The code in this file is based on code from:
//   https://github.com/lwjglgamedev/lwjglbook
// The original code has been modified to suit this project.
//

package view;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

public class NormalVbo {

	public final int id;
	private int numElems = 0;
	
	public NormalVbo() {
		id = glGenBuffers();
	}
	
	public void setNormals(float[] normals, int attribIdx) {
		numElems = normals.length;
		
        FloatBuffer buffer = null;
        try {
            buffer = MemoryUtil.memAllocFloat(normals.length);
            buffer.put(normals).flip();
	        
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
