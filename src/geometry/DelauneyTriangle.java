package geometry;

import java.util.Objects;

import math.FpUtil;


//Triangle extended with additional information and an interface
//customized for operations needed for Delauney triangulation. 
public class DelauneyTriangle extends Object {
	
	private final Triangle2D triangle;
	private final Circle2D circumcircle;
	// Optimization: Cache bounds of triangle to speed up finding vertices. 
	private final Rect2D bounds;
	// Optimization: Cache squared radius of circumcircle to speed checks
	// for point in circumcircle. 
	private final double radiusSquared;
	
	public DelauneyTriangle(Triangle2D t) throws GeometryException {
		triangle = t;
		circumcircle = t.calcCircumcircle();
		bounds = GeometryUtil.calcBoundingBox(t.vertexArray());
		radiusSquared = circumcircle.radius * circumcircle.radius;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		DelauneyTriangle otherTr = (DelauneyTriangle) other;
		return triangle.equals(otherTr.triangle) &&
				circumcircle.equals(otherTr.circumcircle);
	}

	@Override
	public int hashCode() {
		return Objects.hash(triangle, circumcircle);
	}
	
	public DelauneyTriangle copy() {
		try {
			return new DelauneyTriangle(triangle.copy());
		} catch (GeometryException e) {
			return null;
		}
	}
	
	public Triangle2D triangle() {
		return triangle.copy();
	}
	
	public Point2D vertex(int idx) {
		return triangle.vertex(idx);
	}
	
	public int findVertex(Point2D pt) {
		if (!bounds.isPointInRect(pt))
			return -1;
		for (int i = 0; i < 3; ++i)
			if (triangle.vertex(i).equals(pt))
				return i;
		return -1;
	}
	
	public LineSegment2D edge(int idx) {
		return triangle.edge(idx);
	}
	
	public boolean isPointInCircumcircle(Point2D pt) {
		return FpUtil.fpLessEqual(
				Point2D.distanceSquared(pt, circumcircle.center), radiusSquared);
	}
	
	public Point2D circumcenter() {
		return circumcircle.center;
	}
	
	public double circumcircleRadius() {
		return circumcircle.radius;
	}
}
