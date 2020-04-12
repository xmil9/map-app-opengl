package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Vector3f;

import geometry.Point2D;
import geometry.Triangle2D;
import map.MapNode;
import map.MapTile;
import types.Triple;

public class MapMeshBuilder {

	public static class Spec {
		public TileColorPolicy colorPolicy;
		public float elevRange3D;
		public float surfaceElevRatio3D;
		public boolean haveBeaches;

		public Spec(TileColorPolicy colorPolicy, float elevRange3D,
				float surfaceElevRatio3D, boolean haveBeaches) {
			this.colorPolicy = colorPolicy;
			this.elevRange3D = elevRange3D;
			this.surfaceElevRatio3D = surfaceElevRatio3D;
			this.haveBeaches = haveBeaches;
		}
	}
	
	///////////////
	
	private final map.Map map;
	private final Spec spec;
	// Min value and range of the 3D x coordinates.
	// 2D x coordinates are mapped to 3D x coordinates.
	private final float meshMinX = 0;
	private final float meshSizeX = 1;
	// Min value and range of the 3D y coordinates.
	// 2D elevation values are mapped to 3D y coordinates.
	private final float meshMinY;
	private final float meshSizeY;
	// Min value and range of the 3D z coordinates.
	// 2D y coordinates are mapped to 3D z coordinates.
	private final float meshMinZ = 0;
	private final float meshSizeZ = 1;
	
	public MapMeshBuilder(map.Map map, Spec spec) {
		this.map = map;
		this.spec = spec;
		this.meshMinY = -spec.elevRange3D / 2;
		this.meshSizeY = spec.elevRange3D;
	}
	
	public Mesh buildFromVoronoiTiles() {
		// Coordinates of all 3D vertices in x, y, z order.
		// The order of the vertices does not matter. The rendering is
		// determined by the indices array.
		List<Float> vertices = new ArrayList<Float>();
		// Coordinates of the 3D normals in x, y, z order.
		// Order of the normals has to match the order of the vertices
		// in the 'vertices' array.
		List<Float> normals = new ArrayList<Float>();
		// Indices of the vertices to render. Each index refers to the
		// vertex's index in the vertices array (the vertex's x coord
		// index divided by 3).
		List<Integer> indices = new ArrayList<Integer>();
		// Colors for the vertices. Each color is a rgb tuple.  
		// Order of the colors has to match the order of the vertices
		// in the 'vertices' array.
		List<Float> colors = new ArrayList<Float>();

		int numTiles = map.countTiles();
		for (int i = 0; i < numTiles; ++i) {
			MapTile tile = map.tile(i);
			
			// Add tile seed to 3D vertices. 
			int seedIdx = addVertexCoords(tile.seed, tile.elevation(), vertices);
			
			// Add tile nodes to 3D vertices. 
			int firstNodeIdx = nextVertexIndex(vertices);
			int numNodes = tile.countNodes();
			for (int j = 0; j < numNodes; ++j) {
				MapNode node = tile.node(j);
				addVertexCoords(node.pos, node.elevation(), vertices);
			}
			int lastNodeIdx = lastVertexIndex(vertices);
			
			// Create a ccw triangle for each segment between nodes and
			// the seed. Because the nodes are arranged in ccw order already
			// in the tile, defining the triangles in ccw order is simple.
			for (int j = firstNodeIdx; j < lastNodeIdx; ++j) {
				indices.add(j);
				indices.add(j + 1);
				indices.add(seedIdx);
			}
			// Process last segment from last node to first.
			indices.add(lastNodeIdx);
			indices.add(firstNodeIdx);
			indices.add(seedIdx);
			
			addTileNormals(vertices, seedIdx, normals);
			addTileColors(vertices, seedIdx, colors);
		}
		
		return new Mesh(
				Utils.toFloatArray(vertices),
				Utils.toFloatArray(normals),
				Utils.toIntArray(indices),
				null,
				Utils.toFloatArray(colors));
	}
	
	public Mesh buildFromTriangulation() {
		Random rand = new Random(1234567890);
		
		// Coordinates of all 3D vertices in x, y, z order.
		// The order of the vertices does not matter. The rendering is
		// determined by the indices array.
		List<Float> vertices = new ArrayList<Float>();
		// Coordinates of the 3D normals in x, y, z order.
		// Order of the normals has to match the order of the vertices
		// in the 'vertices' array.
		List<Float> normals = new ArrayList<Float>();
		// Indices of the vertices to render. Each index refers to the
		// vertex's index in the vertices array (the vertex's x coord
		// index divided by 3).
		List<Integer> indices = new ArrayList<Integer>();
		// Colors for the vertices. Each color is a rgb tuple.  
		// Order of the colors has to match the order of the vertices
		// in the 'vertices' array.
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
			Vector3f normal = calcTriangleNormal(vertices, vertices.size() - 9);
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
				Utils.toFloatArray(vertices),
				Utils.toFloatArray(normals),
				Utils.toIntArray(indices),
				null,
				Utils.toFloatArray(colors));
	}
	
	private Vector3f calcTriangleNormal(List<Float> vertices, int firstVertexIdx) {
		int pt1Idx = firstVertexIdx;
		int pt2Idx = pt1Idx + 3;
		int pt3Idx = pt2Idx + 3;
		
		Vector3f v = new Vector3f(
				vertices.get(pt1Idx) - vertices.get(pt2Idx),
				vertices.get(pt1Idx + 1) - vertices.get(pt2Idx + 1),
				vertices.get(pt1Idx + 2) - vertices.get(pt2Idx + 2));
		Vector3f w = new Vector3f(
				vertices.get(pt3Idx) - vertices.get(pt2Idx),
				vertices.get(pt3Idx + 1) - vertices.get(pt2Idx + 1),
				vertices.get(pt3Idx + 2) - vertices.get(pt2Idx + 2));
		
		Vector3f normal = new Vector3f();
		v.cross(w, normal);
		normal.normalize();
		return normal;
	}
	
	private int addVertexCoords(Point2D pt, double elev, List<Float> vertices) {
		// 2D x -> 3D x
		vertices.add(interpolateX(pt.x));
		// 2D elevation -> 3D y
		vertices.add(interpolateY(elev));
		// 2D y -> 3D z
		vertices.add(interpolateZ(pt.y));
		return lastVertexIndex(vertices);
	}
	
	private int addNormalCoords(Vector3f normal, List<Float> normals) {
		normals.add(normal.x);
		normals.add(normal.y);
		normals.add(normal.z);
		return lastVertexIndex(normals);
	}
	
	private void addTileNormals(List<Float> vertices, int seedIdx, List<Float> normals) {
		int firstNodeIdx = seedIdx + 1;
		int lastNodeIdx = lastVertexIndex(vertices);
		
		Vector3f seed = makeVertex3D(vertices, seedIdx);
		List<Vector3f> nodes = new ArrayList<Vector3f>();
		for (int i = firstNodeIdx; i <= lastNodeIdx; ++i)
			nodes.add(makeVertex3D(vertices, i));
		
		// Seed normal.
		Vector3f normal = new Vector3f();
		Vector3f seedToNodeVec = new Vector3f();
		Vector3f seedToNextNodeVec = new Vector3f();
		Vector3f segmNormal = new Vector3f();
		for (int i = 0; i < nodes.size(); ++i) {
			Vector3f node = nodes.get(i); 
			seedToNodeVec.set(node.x - seed.x, node.y - seed.y, node.z - seed.z);
			
			Vector3f nextNode = nodes.get((i + 1) % nodes.size()); 
			seedToNextNodeVec.set(nextNode.x - seed.x, nextNode.y - seed.y,
					nextNode.z - seed.z);
			
			seedToNodeVec.cross(seedToNextNodeVec, segmNormal);
			normal.add(segmNormal.normalize());
		}
		addNormalCoords(normal.normalize(), normals);
		
		// Node normals.
		Vector3f nodeToNextVec = new Vector3f();
		Vector3f nodeToSeedVec = new Vector3f();
		Vector3f nodeToPrevVec = new Vector3f();
		for (int i = 0; i < nodes.size(); ++i) {
			normal.set(0, 0, 0);
			Vector3f node = nodes.get(i);
			
			Vector3f nextNode = nodes.get((i + 1) % nodes.size());
			nodeToNextVec.set(nextNode.x - node.x, nextNode.y - node.y,
					nextNode.z - node.z);
			
			nodeToSeedVec.set(seed.x - node.x, seed.y - node.y, seed.z - node.z);
			
			Vector3f prevNode = nodes.get((i == 0) ? nodes.size() - 1 : i - 1);
			nodeToPrevVec.set(prevNode.x - node.x, prevNode.y - node.y,
					prevNode.z - node.z);
			
			nodeToNextVec.cross(nodeToSeedVec, segmNormal);
			normal.add(segmNormal.normalize());
			nodeToSeedVec.cross(nodeToPrevVec, segmNormal);
			normal.add(segmNormal.normalize());
			addNormalCoords(normal.normalize(), normals);
		}
	}
	
	private void addTileColors(List<Float> vertices, int seedIdx,
			List<Float> colors) {
		switch (spec.colorPolicy) {
		case BLEND_TILE_NODE_COLORS:
			addTileColorsByNode(vertices, seedIdx, colors);
			break;
		default:
		case ASSIGN_TILE_SEED_COLORS:
			addTileColorsBySeed(vertices, seedIdx, colors);
			break;
		}
	}
	
	private void addTileColorsByNode(List<Float> vertices, int seedIdx,
			List<Float> colors) {
		int lastNodeIdx = lastVertexIndex(vertices);
		
		for (int i = seedIdx; i <= lastNodeIdx; ++i) {
			int yCoordIdx = i * 3 + 1;
			Triple<Float, Float, Float> color = interpolateColor(vertices.get(yCoordIdx));
			colors.add(color.a);
			colors.add(color.b);
			colors.add(color.c);
		}
	}
	
	private void addTileColorsBySeed(List<Float> vertices, int seedIdx,
			List<Float> colors) {
		int seedYCoordIdx = seedIdx * 3 + 1;
		Triple<Float, Float, Float> seedColor = interpolateColor(vertices.get(seedYCoordIdx));

		int lastNodeIdx = lastVertexIndex(vertices);
		for (int i = seedIdx; i <= lastNodeIdx; ++i) {
			colors.add(seedColor.a);
			colors.add(seedColor.b);
			colors.add(seedColor.c);
		}
	}
	
	private Vector3f makeVertex3D(List<Float> vertices, int vertexIdx) {
		int coordIdx = vertexIdx * 3;
		return new Vector3f(
				vertices.get(coordIdx),
				vertices.get(coordIdx + 1),
				vertices.get(coordIdx + 2));
	}
	
	private static int nextVertexIndex(List<Float> vertices) {
		return vertices.size() / 3;
	}

	// Returns -1 when there are no vertices.
	private static int lastVertexIndex(List<Float> vertices) {
		return nextVertexIndex(vertices) - 1;
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
		double elevMin2D = -1;
		double elevRange2D = 2; 
		return meshMinY + meshSizeY * (float) ((elev2D - elevMin2D) / elevRange2D); 
	}
	
	private float interpolateZ(double y2D) {
		return meshMinZ + meshSizeZ * (float) (y2D / map.height()); 
	}
	
	// Returns a color for a given elevation.
	// The range of the given elevation is [meshMinY, meshMinY + meshSizeY].
	private Triple<Float, Float, Float> interpolateColor(float elev) {
		float maxElev = meshMinY + meshSizeY;
		float surfaceElev = meshMinY + spec.surfaceElevRatio3D * meshSizeY;
		float landRange = maxElev - surfaceElev;
		float beachElev = surfaceElev + 0.05f * landRange;
		float humidElev = surfaceElev + 0.33f * landRange;
		float aridElev = surfaceElev + 0.66f * landRange;
		float rockyElev = surfaceElev + 0.90f * landRange;
		
		float r = 0;
		float g = 0;
		float b = 0;
		if (elev < surfaceElev) {
			// Blue water.
			float elevMin = meshMinY;
			float elevRange = surfaceElev - elevMin;
			r = 0;
			g = 0.5f * (elev - elevMin) / elevRange;
			b = 1;
		} else if (elev < beachElev && spec.haveBeaches) {
			// Yellow beaches.
			float elevMin = surfaceElev;
			float elevRange = beachElev - elevMin;
			r = 1f;
			g = 0.95f - 0.08f * (elev - elevMin) / elevRange;
			b = 0.5f;
		} else if (elev < humidElev) {
			// Green vegetation.
			float elevMin = spec.haveBeaches ? beachElev : surfaceElev;
			float elevRange = humidElev - elevMin;
			r = 0.03f;
			g = 0.34f +  0.3f * ((elev - elevMin) / elevRange);
			b = 0;
		} else if (elev < aridElev) {
			// Brown grassland.
			float elevMin = humidElev;
			float elevRange = aridElev - elevMin;
			r = 0.86f;
			g = 0.8f - 0.3f * ((elev - elevMin) / elevRange);
			b = 0;
		} else if (elev < rockyElev) {
			// Gray rocks.
			float elevMin = aridElev;
			float elevRange = rockyElev - elevMin;
			float gray = 0.75f - 0.5f * ((elev - elevMin) / elevRange);
			r = gray;
			g = gray;
			b = gray;
		} else {
			// White snow.
			float elevMin = rockyElev;
			float elevRange = maxElev - elevMin;
			float gray = 0.85f + 0.15f * ((elev - elevMin) / elevRange);
			r = gray;
			g = gray;
			b = gray;
		}
		
		return new Triple<Float, Float, Float>(r, g, b);
	}
}
