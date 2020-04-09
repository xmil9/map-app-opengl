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

public class TextureCoordVbo {

	public final int id;
	private int numElems = 0;
	
	public TextureCoordVbo() {
		id = glGenBuffers();
	}
	
	public void setTextureCoords(float[] texCoords, int attribIdx) {
		numElems = texCoords.length;
		
        FloatBuffer buffer = null;
        try {
            buffer = MemoryUtil.memAllocFloat(texCoords.length);
            buffer.put(texCoords).flip();
	        
            glBindBuffer(GL_ARRAY_BUFFER, id);
	        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);            
	        glEnableVertexAttribArray(attribIdx);
	        glVertexAttribPointer(attribIdx, 2, GL_FLOAT, false, 0, 0);
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
