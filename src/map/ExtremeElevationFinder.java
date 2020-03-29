package map;

import geometry.Point2D;
import geometry.Rect2D;

// Collects min and max elevation values.
public class ExtremeElevationFinder {

	public double max = -2;
	public Point2D maxPos;
	public double min = 2;
	public Point2D minPos;
	public Rect2D bounds;
	
	// Finds min/max elevation for given tile.
	public void find(MapTile tile) {
		max = -2;
		maxPos = null;
		min = 2;
		minPos = null;
		
		double boundsLeft = Double.MAX_VALUE;
		double boundsTop = Double.MAX_VALUE;
		double boundsRight = Double.MIN_VALUE;
		double boundsBottom = Double.MIN_VALUE;
		
		int numNodes = tile.countNodes();
		for (int i = 0; i < numNodes; ++i) {
			MapNode node = tile.node(i);
			double elev = node.elevation();
			if (elev > max) {
				max = elev;
				maxPos = node.pos;
			}
			if (elev < min) {
				min = elev;
				minPos = node.pos;
			}
			
			// Calc bounds in same loop.
			if (node.pos.x < boundsLeft)
				boundsLeft = node.pos.x;
			if (node.pos.x > boundsRight)
				boundsRight = node.pos.x;
			if (node.pos.y < boundsTop)
				boundsTop = node.pos.y;
			if (node.pos.y > boundsBottom)
				boundsBottom = node.pos.y;
		}

		if (max > 1)
			max = 1;
		if (min < -1)
			min = -1;

		bounds = new Rect2D(boundsLeft, boundsTop, boundsRight, boundsBottom);
	}
}
