package view;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SceneLighting {
	
    private static final float SPECULAR_POWER = 10f;
	private Vector3f ambientLight;
	private PointLight pointLight;
	private SpotLight spotLight;
    private DirectionalLight directionalLight;
    
    public float specularPower() {
    	return SPECULAR_POWER;
    }
    
    public Vector3f ambientLight() {
    	return ambientLight;
    }
    
    public void setAmbientLight(Vector3f light) {
    	ambientLight = light;
    }

    public PointLight pointLight(Matrix4f mat) {
    	return pointLight.transform(mat);
    }
    
    public void setPointLight(PointLight light) {
    	pointLight = light;
    }
    
    public SpotLight spotLight(Matrix4f mat) {
    	return spotLight.transform(mat);
    }
    
    public void setSpotLight(SpotLight light) {
    	spotLight = light;
    }
    
    public DirectionalLight directionalLight(Matrix4f mat) {
    	return directionalLight.transform(mat);
    }
    
    public void setDirectionalLight(DirectionalLight light) {
    	directionalLight = light;
    }
}
