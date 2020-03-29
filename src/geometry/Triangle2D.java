package geometry;

import java.util.Arrays;

import math.MathUtil;


// 2-dimensional triangle.
// Vertices are arranged in ccw order.
public class Triangle2D extends Object {
	
	private final Point2D[] vertices = new Point2D[3];
	
	public Triangle2D() {
		this(new Point2D(), new Point2D(), new Point2D());
	}
	
	public Triangle2D(Point2D a, Point2D b, Point2D c) {
		boolean isCcw = new Vector2D(a, b).isCcw(new Vector2D(b, c));
		vertices[0] = a;
		vertices[1] = isCcw ? b : c;
		vertices[2] = isCcw ? c : b;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		Triangle2D otherTr = (Triangle2D) other;
		return Arrays.equals(vertices, otherTr.vertices);
	}

	@Override
	public int hashCode() {
		return vertices.hashCode();
	}
	
	public Triangle2D copy() {
		return new Triangle2D(vertices[0].copy(), vertices[1].copy(), vertices[2].copy());
	}
	
	public Point2D vertex(int idx) {
		return vertices[idx];
	}
	
	// Checks if a given point is a vertex of the triangle.
	public boolean hasVertex(Point2D pt) {
		return vertices[0].equals(pt) || vertices[1].equals(pt) || vertices[2].equals(pt);
	}
	
	public LineSegment2D edge(int idx) {
		return new LineSegment2D(vertices[idx], vertices[MathUtil.cyclicNext(idx, 3)]);
	}
	
	// Returns array of the triangle's vertices. 
	public Point2D[] vertexArray() {
		return vertices;
	}
	
	// Checks if the triangle degenerates into a point.
	public boolean isPoint() {
		return vertices[0].equals(vertices[1]) && vertices[0].equals(vertices[2]);
	}
	
	public boolean isLine() {
		if (isPoint())
			return false;
		LineSegment2D side01 = new LineSegment2D(vertices[0], vertices[1]);
		return vertices[0].equals(vertices[1]) || side01.isPointOnInfiniteLine(vertices[2]).isOnLine;
	}
	
	public boolean isDegenerate() {
		return isPoint() || isLine();
	}
	
	public double area() {
		if (isDegenerate())
			return 0.0;
		// The perp dot product gives the area of the parallelogram spanned by two
		// vectors. Half of that is the area of the triangle that is formed by the
		// two vectors and a line connecting their endpoints.
		Vector2D v = new Vector2D(vertices[0], vertices[1]);
		Vector2D w = new Vector2D(vertices[0], vertices[2]);
		return Math.abs(v.perpDot(w)) / 2.0;
	}
	
	// Calculates the circumcircle of the triangle. The circumcircle is the circle
	// that goes through all three points of the triangle.
	public Circle2D calcCircumcircle() throws GeometryException {
		if (isPoint())
			return new Circle2D(vertices[0], 0.0);
		if (isLine())
			return null;

		Point2D center = calcCircumcenter();
		double radius = new Vector2D(center, vertices[0]).length();
		return new Circle2D(center, radius);
	}
	
	// Calculates the circumcenter of the triangle. The circumcenter is the center
	// of the triangle's circumcircle.
	// Source:
	// https://www.geeksforgeeks.org/program-find-circumcenter-triangle-2
	private Point2D calcCircumcenter() throws GeometryException {
		// The circumcenter of the triangle is point where all the perpendicular
		// bisectors of the sides of the triangle intersect.
		// It is enough to intersect two of the three bisectors.
		
		LineSegment2D side01 = new LineSegment2D(vertices[0], vertices[1]);
		InfiniteLine2D bisector01 = new InfiniteLine2D(side01.midPoint(),
				side01.direction().ccwNormal());
		LineSegment2D side12 = new LineSegment2D(vertices[1], vertices[2]);
		InfiniteLine2D bisector12 = new InfiniteLine2D(side12.midPoint(),
				side12.direction().ccwNormal());

		LineIntersection2D.Result isect = bisector01.intersect(bisector12);
		if (isect.type != LineIntersection2D.IntersectionType.POINT)
			throw new GeometryException(
					"Triangle circumcircle - Failed to calculate circumcenter.");
		
		return (Point2D) isect.intersection;
	}
}
