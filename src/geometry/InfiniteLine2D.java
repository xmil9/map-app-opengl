package geometry;

import java.util.Objects;

// An infinite line has an anchor point and extends infinitely to both sides of
// a given direction.
public class InfiniteLine2D extends Line2D {
	
	public InfiniteLine2D(Point2D anchor, Vector2D direction) {
		super(anchor, direction);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		InfiniteLine2D otherLine = (InfiniteLine2D) other;
		return anchor.equals(otherLine.anchor) && dir.equals(otherLine.dir);
	}

	@Override
	public int hashCode() {
		// To generate different hash for different types of lines with the
		// same data.
		final int INFINITE_TAG = 13;
		return Objects.hash(INFINITE_TAG, anchor, dir);
	}

	@Override
	public Line2D copy() {
		return new InfiniteLine2D(anchor, dir);
	}
	
	@Override
	public boolean hasStartPoint() {
		return false;
	}
	
	@Override
	public Point2D startPoint() {
		return null;
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
		return new PointOnLineResult(parametricVal);
	}
}
