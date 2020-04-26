//
// The code in this file is based on code from:
//   https://github.com/lwjglgamedev/lwjglbook
// The original code has been modified to suit this project.
//

package view.light;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class DirectionalLight {
    
    private Vector3f color;
    private Vector3f direction;
    private float intensity;

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    public DirectionalLight(DirectionalLight light) {
        this(new Vector3f(light.color()), new Vector3f(light.direction()), light.intensity());
    }

    public Vector3f color() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f direction() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public float intensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
    
    public DirectionalLight transform(Matrix4f mat) {
        Vector4f transformedDir4 = new Vector4f(direction, 0);
        transformedDir4.mul(mat);

        DirectionalLight transformed = new DirectionalLight(this);
        transformed.setDirection(new Vector3f(transformedDir4.x, transformedDir4.y, transformedDir4.z));
        return transformed;
    }
}