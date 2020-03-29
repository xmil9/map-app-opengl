package map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import geometry.Point2D;
import geometry.PoissonDiscSampling;
import geometry.Polygon2D;
import geometry.Rect2D;
import geometry.Triangle2D;
import geometry.VoronoiTesselation;
import geometry.VoronoiTile;
import math.MathUtil;


// Generates the layout of tiles for a map.
public class MapGeometryGenerator {

	public static class Spec {
		public final Rect2D bounds;
		// Minimal distance of randomly generated sample points.
		// Smaller distance => smaller and more tiles.
		public final double minSampleDistance;
		// Number of tested candidates for generated sample points.
		// Larger number => more evenly spaced sample points but slower.
		public final int numSampleCandidates;
		
		public Spec(Rect2D bounds, double minSampleDist, int numCandidates) {
			this.bounds = bounds;
			this.minSampleDistance = minSampleDist;
			this.numSampleCandidates = numCandidates;
		}
	}

	///////////////
	
	private Map.Representation rep;
	private final Spec spec;

	public MapGeometryGenerator(Map map, Spec spec) {
		this.rep = new Map.Representation();
		this.spec = spec;
	}

	// Uses random sample points to generate the geometry.
	public Map.Representation generate(Random rand) {
		List<Point2D> seeds = generateTileSeeds(spec.bounds, spec.minSampleDistance,
				spec.numSampleCandidates, rand);
		makeMapGeometry(new VoronoiTesselation(seeds, spec.bounds));
		return rep;
	}
	
	// Uses given sample points to generate the geometry.
	public Map.Representation generate(List<Point2D> samplePoints) {
		makeMapGeometry(new VoronoiTesselation(samplePoints, spec.bounds));
		return rep;
	}
	
	// Generates tile seeds within given bounds.
	private static List<Point2D> generateTileSeeds(Rect2D bounds, double minSampleDist,
			int numCandidates, Random rand) {
		PoissonDiscSampling sampler =
				new PoissonDiscSampling(bounds, minSampleDist, numCandidates, rand);
		return sampler.generate();
	}
	
	// Constructs the map's geometry for a given tesselation of the mapped area.  
	private void makeMapGeometry(VoronoiTesselation tess) {
		List<VoronoiTile> tessTiles = tess.tesselate();
		makeMapTiles(tessTiles);
		populateTileNeighbors(tess.getTriangulation());
		populateNodeNeighbors(tessTiles);
	}
	
	// Constructs the tiles that the map is segmented into from given tiles of
	// a tesselation.
	private void makeMapTiles(List<VoronoiTile> tessTiles) {
		for (var tessTile : tessTiles)
			makeMapTile(tessTile);
	}
	
	// Constructs a map tile from a tesselation tile.
	private void makeMapTile(VoronoiTile tessTile) {
		MapTile tile = new MapTile(tessTile.seed, tessTile.outline);
		tile.setNodes(makeTileNodes(tessTile.outline));
		rep.addTile(tile);
	}
	
	// Contructs the map nodes that define map properties at each vertex of a
	// tile's shape.
	private List<MapNode> makeTileNodes(Polygon2D shape) {
		List<MapNode> tileNodes = new ArrayList<MapNode>();
		
		for (int i = 0; i < shape.countVertices(); ++i) {
			Point2D pt = shape.vertex(i);
			
			// Check if a map node exists already at this point.
			MapNode node = rep.findNodeAt(pt);
			if (node == null) {
				node = new MapNode(pt);
				rep.addNode(node);
			}
			
			tileNodes.add(node);
		}
		
		return tileNodes;
	}
	
	// Populates the data structure that holds information about which tiles neighbor
	// each other.
	private void populateTileNeighbors(List<Triangle2D> triangulation) {
		// Each vertex of a triangle corresponds to a tile seed. The triangle edges
		// connect neighboring tiles. Mark the tiles of all triangle vertices as
		// connected to each other.
		for (var triangle : triangulation) {
			connectTilesAt(triangle.vertex(0), triangle.vertex(1));
			connectTilesAt(triangle.vertex(1), triangle.vertex(2));
			connectTilesAt(triangle.vertex(2), triangle.vertex(0));
		}
	}
	
	// Marks two map tiles at given locations as neighbors.
	private void connectTilesAt(Point2D a, Point2D b) {
		MapTile tileA = rep.findTileAt(a);
		MapTile tileB = rep.findTileAt(b);
		if (tileA != null && tileB != null) {
			tileA.addNeighbor(tileB);
			tileB.addNeighbor(tileA);
		}
	}
	
	// Populates the data structure that holds information about which nodes neighbor
	// each other.
	private void populateNodeNeighbors(List<VoronoiTile> tessTiles) {
		// Each vertex of a Voronoi tile's outline corresponds to a map node. The
		// border edges connect neighboring nodes. Mark the nodes of neighboring
		// vertices as connected to each other.
		for (var tessTile : tessTiles) {
			int numVertices = tessTile.outline.countVertices();
			for (int i = 0; i < numVertices; ++i) {
				Point2D vertex = tessTile.outline.vertex(i);
				Point2D next = tessTile.outline.vertex(
						MathUtil.cyclicNext(i, numVertices));
				connectNodesAt(vertex, next);
			}
		}
	}
	
	// Marks two map nodes at given locations as neighbors.
	private void connectNodesAt(Point2D a, Point2D b) {
		MapNode nodeA = rep.findNodeAt(a);
		MapNode nodeB = rep.findNodeAt(b);
		if (nodeA != null && nodeB != null) {
			nodeA.addNeighbor(nodeB);
			nodeB.addNeighbor(nodeA);
		}
	}
}
