package view;

import java.nio.*;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL15.*;

public class VertexIndexVbo {

	public final int id;
	private int numElems = 0;
	
	public VertexIndexVbo() {
		id = glGenBuffers();
	}
	
	public void setIndices(int[] indices) {
		numElems = indices.length;
		
        IntBuffer buffer = null;
        try {
        	buffer = MemoryUtil.memAllocInt(indices.length);
        	buffer.put(indices).flip();
	        
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
	        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);            
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
