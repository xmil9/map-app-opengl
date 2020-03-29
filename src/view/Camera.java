package view;

import org.joml.Vector3f;

public class Camera {

    private final Vector3f position;

    private final Vector3f rotation;

    public Camera() {
        position = new Vector3f();
        rotation = new Vector3f();
    }

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3f position() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void movePosition(float dx, float dy, float dz) {
        if ( dz != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * dz;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * dz;
        }
        if ( dx != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * dx;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * dx;
        }
        position.y += dy;
    }

    public Vector3f rotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void moveRotation(float dx, float dy, float dz) {
        rotation.x += dx;
        rotation.y += dy;
        rotation.z += dz;
    }
}