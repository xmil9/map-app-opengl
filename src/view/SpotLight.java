package view;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class SpotLight {

    private PointLight pointLight;
    private Vector3f coneDirection;
    private float cutOff;

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutOffAngle) {
        this.pointLight = pointLight;
        this.coneDirection = coneDirection;
        setCutOffAngle(cutOffAngle);
    }

    public SpotLight(SpotLight spotLight) {
        this(new PointLight(spotLight.pointLight()),
                new Vector3f(spotLight.coneDirection()),
                0);
        setCutOff(spotLight.cutOff());
    }

    public PointLight pointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public Vector3f coneDirection() {
        return coneDirection;
    }

    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }

    public float cutOff() {
        return cutOff;
    }

    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
    }
    
    public final void setCutOffAngle(float cutOffAngle) {
        this.setCutOff((float)Math.cos(Math.toRadians(cutOffAngle)));
    }
    
    public SpotLight transform(Matrix4f mat) {
        Vector4f transformedConeDir = new Vector4f(coneDirection, 0);
        transformedConeDir.mul(mat);

    	return new SpotLight(
    			pointLight.transform(mat),
        		new Vector3f(transformedConeDir.x, transformedConeDir.y, transformedConeDir.z),
        		cutOff);
    }
}
