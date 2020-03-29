package map;

import java.util.ArrayList;
import java.util.List;

import geometry.Point2D;
import geometry.Polygon2D;
import geometry.Rect2D;

// Defines a tile on the map.
public class MapTile extends Object {

	public final Point2D seed;
	public final Polygon2D shape;
	public final Rect2D bounds;
	// Nodes for each point in the tile's shape. Ordered ccw.
	private List<MapNode> nodes;
	// Neighboring tiles.
	private List<MapTile> neighbors = new ArrayList<MapTile>();
	private double elevation = -1;
	
	public MapTile(Point2D seed, Polygon2D shape) {
		this.seed = seed;
		this.shape = shape;
		this.bounds = shape.bounds();
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		MapTile otherTile = (MapTile) other;
		return seed.equals(otherTile.seed);
	}
	
	// Sets the map nodes for each point of the tile's shape.
	// Caller is responsible to make sure the node's locations correspond
	// to points on the tile's shape.
	public void setNodes(List<MapNode> nodes) {
		this.nodes = nodes;
	}

	public int countNodes() {
		return nodes.size();
	}

	// Returns a node in the tile's outline.
	public MapNode node(int idx) {
		return nodes.get(idx);
	}
	
	// Adds a given tile as a neighboring tile.
	public void addNeighbor(MapTile neighbor) {
		if (!neighbors.contains(neighbor))
			neighbors.add(neighbor);
	}

	public int countNeighbors() {
		return neighbors.size();
	}

	// Returns a neighboring tile.
	public MapTile neighbor(int idx) {
		return neighbors.get(idx);
	}
	
	public double elevation() {
		return elevation;
	}
	
	public void setElevation(double val) {
		elevation = val;
	}
	
	public Rect2D bounds() {
		return bounds.copy();
	}
}
