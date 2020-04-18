//
// The code in this file is based on code from:
//   https://github.com/lwjglgamedev/lwjglbook
// The original code has been modified to suit this project.
//

package view;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryStack;

import types.Triple;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private final int id;
    public final int width;
    public final int height;

    public Texture(String fileName) throws Exception {
        var result = loadTextureFromFile(fileName);
        this.id = result.a;
        this.width = result.b;
        this.height = result.c;
    }

    public Texture(ByteBuffer imageBuffer) throws Exception {
        var result = loadTextureFromBuffer(imageBuffer);
        this.id = result.a;
        this.width = result.b;
        this.height = result.c;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public int id() {
        return id;
    }

    private static Triple<Integer, Integer, Integer> loadTextureFromFile(String fileName)
    		throws Exception {
        int width;
        int height;
        ByteBuffer buf;

        // Load Texture file
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buf = stbi_load(fileName, w, h, channels, 4);
            if (buf == null) {
                throw new Exception("Image file [" + fileName  + "] not loaded: " + stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }
        
        int texId = createTexture(buf, width, height);
        stbi_image_free(buf);
        
        return new Triple<Integer, Integer, Integer>(texId, width, height);
    }

    private static Triple<Integer, Integer, Integer> loadTextureFromBuffer(
    		ByteBuffer imageBuffer) throws Exception {
        int width;
        int height;
        ByteBuffer buf;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buf = stbi_load_from_memory(imageBuffer, w, h, channels, 4);
            if (buf == null) {
                throw new Exception("Image data not loaded: " + stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        int texId = createTexture(buf, width, height);
        stbi_image_free(buf);

        return new Triple<Integer, Integer, Integer>(texId, width, height);
    }
    
    private static int createTexture(ByteBuffer buf, int width, int height) {
        // Create a new OpenGL texture
        int textureId = glGenTextures();
        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        // Settings that can be used instead of a mipmap (see below) to scale the
        // image. The settings basically specify that the nearest pixel should be
        // used when a object surface position does not exactly map to an image
        // pixel. 
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Upload the texture data.
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA,
        		GL_UNSIGNED_BYTE, buf);
        
        // Generate mipmap. A mipmap is a set of generated images at different
        // resolutions that are used for scaled drawing of the image. 
        glGenerateMipmap(GL_TEXTURE_2D);

        return textureId;
    }
    
    public void cleanup() {
        glDeleteTextures(id);
    }
}
