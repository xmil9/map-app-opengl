//
// The code in this file is based on code from:
//   https://github.com/lwjglgamedev/lwjglbook
// The original code has been modified to suit this project.
//

package view.scene;

import org.joml.Vector4f;

public class Material {

    private static final Vector4f DEFAULT_COLOR = new Vector4f(0.8f, 0.8f, 0.8f, 1.0f);
    private Vector4f ambientColor = DEFAULT_COLOR;
    private Vector4f diffuseColor = DEFAULT_COLOR;
    private Vector4f specularColor = DEFAULT_COLOR;
    private Texture texture = null;
    private float reflectance = 0;
    
    public Material(Vector4f color, float reflectance) {
        this(color, color, color, reflectance);
    }

    public Material(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor,
    		float reflectance) {
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.reflectance = reflectance;
    }

    public Material(Texture texture) {
        this.texture = texture;
        this.reflectance = 0;
    }

    public Material(Texture texture, float reflectance) {
        this.texture = texture;
        this.reflectance = reflectance;
    }

    public Vector4f ambientColor() {
        return ambientColor;
    }

    public void setAmbientColor(Vector4f ambientColor) {
        this.ambientColor = ambientColor;
    }

    public Vector4f diffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Vector4f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public Vector4f specularColor() {
        return specularColor;
    }

    public void setSpecularColor(Vector4f specularColor) {
        this.specularColor = specularColor;
    }

    public boolean isTextured() {
        return this.texture != null;
    }

    public Texture texture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public float reflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }
}