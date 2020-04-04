package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Vector3f;

import geometry.Point2D;
import geometry.Triangle2D;

public class MapMeshBuilder {

	private final map.Map map;
	// 3D x coordinate is the 2D x coordinate.
	private final float meshMinX = 0;
	private final float meshSizeX = 1;
	// 3D y coordinate is the elevation of 2D map points.
	private final float meshMinY = -0.5f;
	private final float meshSizeY = 0.5f;
	// 3D z coordinate is the 2D y coordinate.
	private final float meshMinZ = 0;
	private final float meshSizeZ = 1;
	
	public MapMeshBuilder(map.Map map) {
		this.map = map;
	}
	
	public Mesh build() {
		Random rand = new Random(1234567890);
		
		List<Float> vertices = new ArrayList<Float>();
		List<Float> normals = new ArrayList<Float>();
		List<Integer> indices = new ArrayList<Integer>();
		List<Float> colors = new ArrayList<Float>();

		int numTris = map.countTriangles();
		for (int i = 0; i < numTris; ++i) {
			Triangle2D tri = map.triangle(i);
			for (int j = 0; j < 3; ++j ) {
				Point2D pt = tri.vertex(j);
				// 2D x -> 3D x
				float x = interpolateX(pt.x);
				vertices.add(x);
				// 2D elevation -> 3D y
				float y = interpolateY(generateElevation(rand));
				vertices.add(y);
				// 2D y -> 3D z
				float z = interpolateZ(pt.y);
				vertices.add(z);

				// Right now each vertex has its own instance. 
				indices.add(indices.size());
			}
			
			// Calc normal of triangle and use for each of its vertices.
			// This is just temporary!
			
			int pt3xIdx = vertices.size() - 3;
			int pt2xIdx = pt3xIdx - 3;
			int pt1xIdx = pt2xIdx - 3;
			Vector3f v = new Vector3f(
					vertices.get(pt1xIdx) - vertices.get(pt2xIdx),
					vertices.get(pt1xIdx + 1) - vertices.get(pt2xIdx + 1),
					vertices.get(pt1xIdx + 2) - vertices.get(pt2xIdx + 2));
			Vector3f w = new Vector3f(
					vertices.get(pt3xIdx) - vertices.get(pt2xIdx),
					vertices.get(pt3xIdx + 1) - vertices.get(pt2xIdx + 1),
					vertices.get(pt3xIdx + 2) - vertices.get(pt2xIdx + 2));
			Vector3f normal = new Vector3f();
			v.cross(w, normal);
			normal.normalize();
			
			for (int j = 0; j < 3; ++j) {
				normals.add(normal.x);
				normals.add(normal.y);
				normals.add(normal.z);
			}
			
			// Set color for each vertex of the triangle.
			for (int j = 0; j < 9; ++j)
				colors.add(genColorComponent(rand));
		}
		
		return new Mesh(
				Utils.listToArray(vertices),
				Utils.listToArray(normals),
				indices.stream().mapToInt(i -> i).toArray(),
				null,
				null);
	}
	
	private double generateElevation(Random rand) {
		return 0;
//		return -0.1 + 0.2 * rand.nextFloat();
	}
	
	private float genColorComponent(Random rand) {
		return rand.nextFloat();
	}
	
	private float interpolateX(double x2D) {
		return meshMinX + meshSizeX * (float) (x2D / map.width()); 
	}
	
	private float interpolateY(double elev2D) {
		double elevMin = -1;
		double elevRange = 2; 
		return meshMinY + meshSizeY * (float) ((elev2D - elevMin) / elevRange); 
	}
	
	private float interpolateZ(double y2D) {
		return meshMinZ + meshSizeZ * (float) (y2D / map.height()); 
	}
}
