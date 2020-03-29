package geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import math.MathUtil;


// A closed polygon shape.
public class Polygon2D extends Object {
	
	private List<Point2D> vertices;
	
	public Polygon2D() {
		vertices = new ArrayList<Point2D>();
	}
	
	public Polygon2D(Point2D pt) {
		this();
		vertices.add(pt);
	}
	
	public Polygon2D(Collection<Point2D> points) {
		vertices = new ArrayList<Point2D>(points);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		Polygon2D otherPoly = (Polygon2D) other;
		return vertices.equals(otherPoly.vertices);
	}

	@Override
	public int hashCode() {
		return vertices.hashCode();
	}
	
	public int countVertices() {
		return vertices.size();
	}
	
	public Point2D vertex(int idx) {
		return vertices.get(idx);
	}
	
	public void setVertex(int idx, Point2D pt) {
		vertices.set(idx, pt);
	}
	
	// Checks if a given point is a vertex of the polygon.
	public boolean hasVertex(Point2D pt) {
		for (Point2D v : vertices)
			if (v.equals(pt))
				return true;
		return false;
	}

	public void addVertex(Point2D pt) {
		vertices.add(pt);
	}
	
	public void insertVertex(Point2D pt, int idx) {
		vertices.add(idx, pt);
	}
	
	public int countEdges() {
		if (vertices.size() == 1)
			return 0;
		return vertices.size();
	}
	
	public LineSegment2D edge(int idx) {
		if (idx == countEdges() - 1)
			return new LineSegment2D(vertices.get(idx), vertices.get(0));
		return new LineSegment2D(vertices.get(idx), vertices.get(idx + 1));
	}
	
	public Rect2D bounds() {
		return GeometryUtil.calcBoundingBox(vertices);
	}
	
	public Polygon2D reversed() {
		Polygon2D rev = new Polygon2D();
		for (int i = vertices.size() - 1; i >= 0; --i)
			rev.addVertex(vertex(i).copy());
		return rev;
	}
	
	public boolean isConvex() {
		return GeometryUtil.isConvexPath(vertices);
	}

	// Checks whether a given point is inside a given convex polygon. Points on the
	// polygon's edges are considered 'inside'.
	public static boolean isPointInsideConvexPolygon(Point2D pt, Polygon2D poly) {
		// Special cases.
		if (poly.countVertices() == 0)
			return false;
		if (poly.countVertices() == 1)
			return poly.vertex(0).equals(pt);

		// If the point is inside the convex polygon all vectors between the point
		// and the vertices of the polygon must wind around the point in a continuous
		// cw or ccw manner, i.e. the orientation between vectors will not change
		// from cw to ccw or vice-versa. We have to also detect if the points is
		// on one of the polygon's edges.
		MathUtil.Sign polyOrientation = MathUtil.Sign.NONE;
		
		int numVert = poly.countVertices();
		for (int i = 0; i < numVert; ++i) {
			Vector2D v = new Vector2D(pt, poly.vertex(i));
			int next = MathUtil.cyclicNext(i, numVert);
			Vector2D w = new Vector2D(pt, poly.vertex(next));
			
			MathUtil.Sign curOrientation = MathUtil.sign(v.perpDot(w));

			if (curOrientation == MathUtil.Sign.NONE &&
					poly.edge(i).isPointOnLine(pt).isOnLine) {
				// The point is on an edge.
				return true;
			}

			if (polyOrientation == MathUtil.Sign.NONE) {
				// Init orientation of polygon.
				polyOrientation = curOrientation;
			} else if (polyOrientation != curOrientation) {
				// Change in orientation - point is outside.
				return false;
			}
		}

		// No changes in orientation - point is inside.
		return true;
	}
}
