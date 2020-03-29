package map;

import java.util.ArrayList;
import java.util.List;

import geometry.Point2D;

// Holds map properties for a location on the map.
public class MapNode extends Object {

	public final Point2D pos;
	private double elevation = -1;
	// Array neighboring nodes.
	private List<MapNode> neighbors = new ArrayList<MapNode>();
	
	public MapNode(Point2D pos) {
		this.pos = pos;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		MapNode otherNode = (MapNode) other;
		return pos.equals(otherNode.pos);
	}
	
	// Adds a given node as a neighboring node.
	public void addNeighbor(MapNode node) {
		if (!neighbors.contains(node))
			neighbors.add(node);
	}

	public int countNeighbors() {
		return neighbors.size();
	}

	// Returns a neighboring node given by its index.
	public MapNode neighbor(int idx) {
		return neighbors.get(idx);
	}
	
	public double elevation() {
		return elevation;
	}
	
	public void setElevation(double elevation) {
		this.elevation = elevation;
	}
}
