package view;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class RenderedItem {
	
	public void render() {
        Texture texture = material().texture();
        if (texture != null) {
            // Activate first texture bank
            glActiveTexture(GL_TEXTURE0);
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.id());
        }
        
        // Bind to the VAO
        glBindVertexArray(shape().vaoId());
        
        // Draw the vertices
        glDrawElements(GL_TRIANGLES, shape().countVertices(), GL_UNSIGNED_INT, 0);
        
        // Restore state
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
	}

    // Concatenates a given transformation with the item's transformation.
	public Matrix4f concatTransformation(Matrix4f mat) {
        Vector3f rot = rotation();
        return new Matrix4f().set(mat).translate(position()).
            rotateX(-rot.x).rotateY(-rot.y).rotateZ(-rot.z).
                scale(scale());
	}
	
	public abstract Mesh shape();
	public abstract Material material();
	public abstract Vector3f position();
	public abstract void setPosition(float x, float y, float z);
	public abstract Vector3f rotation();
	public abstract float scale();
	public abstract void cleanup();
}
