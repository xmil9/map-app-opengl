//
// The code in this file is based on code from:
//   https://github.com/lwjglgamedev/lwjglbook
// The original code has been modified to suit this project.
//

package view.light;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SceneLighting {
	
    private static final float SPECULAR_POWER = 10f;
	private final Vector3f ambientLight;
	private final PointLight pointLight;
	private final SpotLight spotLight;
    private final DirectionalLight directionalLight;
    
    public SceneLighting(
    		Vector3f ambientLight,
    		PointLight pointLight,
    		SpotLight spotLight,
    		DirectionalLight directionalLight) {
    	this.ambientLight = ambientLight;
    	this.pointLight = pointLight;
    	this.spotLight = spotLight;
    	this.directionalLight = directionalLight;
    }
    
    public float specularPower() {
    	return SPECULAR_POWER;
    }
    
    public Vector3f ambientLight() {
    	return ambientLight;
    }

    public PointLight pointLight(Matrix4f mat) {
    	return pointLight.transform(mat);
    }
    
    public SpotLight spotLight(Matrix4f mat) {
    	return spotLight.transform(mat);
    }
    
    public DirectionalLight directionalLight(Matrix4f mat) {
    	return directionalLight.transform(mat);
    }
}
