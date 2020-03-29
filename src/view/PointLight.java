package view;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PointLight {

    ////////////////
    
    public static class Attenuation {

        public float constant;
        public float linear;
        public float exponent;

        public Attenuation(float constant, float linear, float exponent) {
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }
    }

    ////////////////
    
    private Vector3f color;
    private Vector3f position;
    protected float intensity;
    private Attenuation attenuation;
    
    public PointLight(Vector3f color, Vector3f position, float intensity) {
        attenuation = new Attenuation(1, 0, 0);
        this.color = color;
        this.position = position;
        this.intensity = intensity;
    }

    public PointLight(Vector3f color, Vector3f position, float intensity, Attenuation attenuation) {
        this(color, position, intensity);
        this.attenuation = attenuation;
    }

    public PointLight(PointLight pointLight) {
        this(new Vector3f(pointLight.color()), new Vector3f(pointLight.position()),
                pointLight.intensity(), pointLight.attenuation());
    }

    public Vector3f color() {
        return new Vector3f(color);
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f position() {
        return new Vector3f(position);
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float intensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Attenuation attenuation() {
        return new Attenuation(attenuation.constant, attenuation.linear,
        		attenuation.exponent);
    }

    public void setAttenuation(Attenuation attenuation) {
        this.attenuation = attenuation;
    }
    
    public PointLight transform(Matrix4f mat) {
        Vector4f lightPos4 = new Vector4f(position(), 1);
        lightPos4.mul(mat);
        
        PointLight tranformed = new PointLight(this);
        tranformed.setPosition(new Vector3f(lightPos4.x, lightPos4.y, lightPos4.z));
        return tranformed;
    }
}