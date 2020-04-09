//
// The code in this file is based on code from:
//   https://github.com/lwjglgamedev/lwjglbook
// The original code has been modified to suit this project.
//

package view;

import java.nio.*;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class ColorVbo {

	public final int id;
	private int numElems = 0;
	
	public ColorVbo() {
		id = glGenBuffers();
	}
	
	public void setColors(float[] colors, int attribIdx) {
		numElems = colors.length;
		
        FloatBuffer buffer = null;
        try {
            buffer = MemoryUtil.memAllocFloat(colors.length);
            buffer.put(colors).flip();
	        
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
