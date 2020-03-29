package map;

import java.util.ArrayList;
import java.util.List;

public class Continent {

	// Number of initially allocated land nodes in continent.
	public final int allocatedSize;
	private List<MapNode> nodes;
	
	public Continent(int size) {
		this.allocatedSize = size;
		this.nodes = new ArrayList<MapNode>(size);
	}
	
	public int size() {
		return nodes.size();
	}
	
	public void addNode(MapNode node) {
		nodes.add(node);
	}
}
