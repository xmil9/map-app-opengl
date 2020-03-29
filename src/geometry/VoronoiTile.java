package geometry;


public class VoronoiTile {
	public final Point2D seed;
	public final Polygon2D outline;
	
	public VoronoiTile(Point2D seed, Polygon2D border) {
		this.seed = seed;
		this.outline = border;
	}
	
	public int countVertices() {
		if (outline == null)
			return 0;
		return outline.countVertices();
	}
	
	public boolean hasVertex(Point2D pt) {
		if (outline == null)
			return false;
		return outline.hasVertex(pt);
	}
}
