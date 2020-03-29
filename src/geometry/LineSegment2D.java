package geometry;

import java.util.Objects;

// A line segment has finite start and end points.
public class LineSegment2D extends Line2D {
	
	public LineSegment2D(Point2D start, Point2D end) {
		this(start, new Vector2D(start, end));
	}

	public LineSegment2D(Point2D start, Vector2D direction) {
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
		LineSegment2D otherLine = (LineSegment2D) other;
		return anchor.equals(otherLine.anchor) && dir.equals(otherLine.dir);
	}

	@Override
	public int hashCode() {
		// To generate different hash for different types of lines with the
		// same data.
		final int SEGMENT_TAG = 7;
		return Objects.hash(SEGMENT_TAG, anchor, dir);
	}

	@Override
	public Line2D copy() {
		return new LineSegment2D(anchor, dir);
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
		return true;
	}

	@Override
	public Point2D endPoint() {
		return anchor.offset(dir);
	}
	
	public Point2D midPoint() {
		return startPoint().offset(dir.scale(0.5));
	}
	
	public double length() {
		return dir.length();
	}

	public double lengthSquared() {
		return dir.lengthSquared();
	}
	
	@Override
	public PointOnLineResult isPointOnLine(Point2D pt) {
		double parametricVal = calcParametricValue(pt);
		if (parametricVal == Double.MAX_VALUE)
			return new PointOnLineResult();
		if (parametricVal < 0.0 || parametricVal > 1.0)
			return new PointOnLineResult();
		return new PointOnLineResult(parametricVal);
	}
}
