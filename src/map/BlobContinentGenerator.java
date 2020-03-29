package map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Generates continents by growing them randomly outward from a seed node.
// Leads to blob looking shapes for large maps.
public class BlobContinentGenerator
implements ContinentBasedTopography.ContinentGenerator {

	private final Random rand;
	private Map.Representation rep;
	
	public BlobContinentGenerator(Random rand) {
		this.rand = rand;
	}

	@Override
	public void setMap(Map.Representation rep) {
		this.rep = rep;
	}
	
	@Override
	public void generate(Continent continent) {
		MapNode nextNode = findSeedNode();
		if (nextNode == null)
			return;
		assignNode(continent, nextNode);
		
		// Pool of nodes that are used to grow the continent from.
		List<MapNode> growthPool = new ArrayList<MapNode>();
		growthPool.add(nextNode);
		
		while (continent.size() < continent.allocatedSize) {
			nextNode = findUnassignedNode(growthPool);
			if (nextNode == null)
				return;
			assignNode(continent, nextNode);
			growthPool.add(nextNode);
		}
	}
	
	// Finds an unassigned node on the map.
	private MapNode findSeedNode() {
		int attemptsLeft = 100;
		while (--attemptsLeft > 0) {
			int nodeIdx = rand.nextInt(rep.countNodes());
			if (isUnassignedNode(rep.node(nodeIdx)))
				return rep.node(nodeIdx);
		}
		return null;
	}
	
	// Finds an unassigned neighbor of a node from a given pool of nodes.
	private MapNode findUnassignedNode(List<MapNode> nodePool) {
		while (!nodePool.isEmpty()) {
			int nodeIdx = rand.nextInt(nodePool.size());
			MapNode node = nodePool.get(nodeIdx);
			for (int i = 0; i < node.countNeighbors(); ++i) {
				MapNode neighbor = node.neighbor(i);
				if (isUnassignedNode(neighbor))
					return neighbor;
			}
			// No unassigned neighbors left. Remove node from pool.
			nodePool.remove(nodeIdx);
		}
		return null;
	}
	
	private static boolean isUnassignedNode(MapNode node) {
		return node.elevation() < 0;
	}
	
	private static void assignNode(Continent continent, MapNode node) {
		node.setElevation(1);
		continent.addNode(node);
	}
}
