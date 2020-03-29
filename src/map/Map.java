package map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import geometry.Point2D;
import geometry.Polygon2D;

public class Map {

	public static class Spec {
		public final MapGeometryGenerator.Spec geom;
		public final PerlinTopography.Spec topo;
		
		public Spec(MapGeometryGenerator.Spec geom, PerlinTopography.Spec topo) {
			this.geom = geom;
			this.topo = topo;
		}
	}
	
	///////////////
	
	// Data structures that hold the map data.
	// Kept in separate class to allow generator objects to work with it more esily.
	public static class Representation {
		// Master collection of tiles that map is made from.
		private List<MapTile> tiles;
		// Lookup of tiles by their seed location.
		private java.util.Map<Point2D, MapTile> tileLookup;
		// Master collection of unique nodes defining the shape of all tiles. A node shared
		// between tiles is only listed once.
		private List<MapNode> nodes;
		// Lookup of nodes by their locations.
		private java.util.Map<Point2D, MapNode> nodeLookup;
		
		public Representation() {
			tiles = new ArrayList<MapTile>();
			tileLookup = new TreeMap<Point2D, MapTile>(Point2D.makeXYComparator());
			nodes = new ArrayList<MapNode>();
			nodeLookup = new TreeMap<Point2D, MapNode>(Point2D.makeXYComparator());
		}
		
		// Adds a given tile.
		public void addTile(MapTile tile) {
			tiles.add(tile);
			tileLookup.put(tile.seed, tile);
		}
		
		public int countTiles() {
			return tiles.size();
		}
		
		public MapTile tile(int idx) {
			return tiles.get(idx);
		}
		
		// Returns the tile whose seed is located at a given position.
		public MapTile findTileAt(Point2D pos) {
			return  tileLookup.get(pos);
		}

		// Adds a given node.
		public void addNode(MapNode node) {
			nodes.add(node);
			nodeLookup.put(node.pos, node);
		}
		
		public int countNodes() {
			return nodes.size();
		}
		
		public MapNode node(int idx) {
			return nodes.get(idx);
		}
		
		// Returns the node that is located at a given position.
		public MapNode findNodeAt(Point2D pos) {
			return nodeLookup.get(pos);
		}
	}
	
	///////////////
	
	private final Spec spec;
	private final Random rand;
	private Representation rep;

	public Map(Spec spec, Random rand) {
		this.spec = spec;
		this.rand = rand;
		this.rep = new Representation();
	}

	// Generates the map tiles.
	public void generate() {
		generateGeometry();
		generateTopography();
	}

	public double width() {
		return spec.geom.bounds.width();
	}

	public double height() {
		return spec.geom.bounds.height();
	}
	
	// Returns the number of tiles in the map.
	public int countTiles() {
		return rep.tiles.size();
	}

	public MapTile tile(int idx) {
		return rep.tile(idx);
	}

	public MapNode node(int idx) {
		return rep.node(idx);
	}
	
	// Returns the shapes of all tiles.
	public List<Polygon2D> tileShapes() {
		List<Polygon2D> shapes = new ArrayList<Polygon2D>(rep.tiles.size());
		for (var tile : rep.tiles)
			shapes.add(tile.shape);
		return shapes;
	}
	
	// Generates the tile layout of the map.
	private void generateGeometry() {
		MapGeometryGenerator gen = new MapGeometryGenerator(this, spec.geom);
		rep = gen.generate(rand);
	}
	
	// Generates the node elevations.
	private void generateTopography() {
		PerlinTopography gen = new PerlinTopography(spec.topo, rand);
//		ContinentBasedTopography gen = new ContinentBasedTopography(spec.topo, rand);
		gen.generate(rep);
	}
}
