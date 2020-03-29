package geometry;

import java.util.Objects;

// A line ray has a start point and extends infinitely into a given direction.
public class LineRay2D extends Line2D {
	
	public LineRay2D(Point2D start, Vector2D direction) {
		super(start, direction);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		LineRay2D otherLine = (LineRay2D) other;
		return anchor.equals(otherLine.anchor) && dir.equals(otherLine.dir);
	}

	@Override
	public int hashCode() {
		// To generate different hash for different types of lines with the
		// same data.
		final int RAY_TAG = 11;
		return Objects.hash(RAY_TAG, anchor, dir);
	}

	@Override
	public Line2D copy() {
		return new LineRay2D(anchor, dir);
	}
	
	@Override
	public boolean hasStartPoint() {
		return true;
	}
	
	@Override
	public Point2D startPoint() {
		return anchor;
	}
	
	@Override
	public boolean hasEndPoint() {
		return false;
	}

	@Override
	public Point2D endPoint() {
		return null;
	}
	
	@Override
	public PointOnLineResult isPointOnLine(Point2D pt) {
		double parametricVal = calcParametricValue(pt);
		if (parametricVal == Double.MAX_VALUE)
			return new PointOnLineResult();
		if (parametricVal < 0.0)
			return new PointOnLineResult();
		return new PointOnLineResult(parametricVal);
	}
}
