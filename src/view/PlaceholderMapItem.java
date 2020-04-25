package view;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PlaceholderMapItem extends MapItem {

	public PlaceholderMapItem(Mesh shape, Material material) {
		super(shape, material);
	}

    // Concatenates a given transformation with the item's transformation.
	@Override
	public Matrix4f concatTransformation(Matrix4f mat) {
		Vector3f dim = shape().dimensions();
        Vector3f rot = rotation();
        // Order of operations is from last to first.
        // For the placeholder item we want to move its center to the origin
        // first so it rotates around its center.
        return new Matrix4f()
        		.set(mat)
        		.translate(position())
        		.rotateX(-rot.x).rotateY(-rot.y).rotateZ(-rot.z)
        		.scale(scale())
        		.translate(-dim.x/2f, -dim.y/2f, -dim.z/2f);
	}
	
}
