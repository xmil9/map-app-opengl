package geometry;

import java.util.Objects;

// Two nested circles. Area between circles is part of the ring.
// Area inside the inner circles is not part of the ring.
public class Ring2D {

	private final Circle2D inner;
	private final Circle2D outer;
	
	public Ring2D(Point2D center, double innerRadius, double outerRadius) {
		outer = new Circle2D(center,
				(outerRadius >= innerRadius) ? outerRadius : innerRadius);
		inner = new Circle2D(center,
				(outerRadius >= innerRadius) ? innerRadius : outerRadius);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		Ring2D otherRing = (Ring2D) other;
		return inner.equals(otherRing.inner) &&
				outer.equals(otherRing.outer);
	}

	@Override
	public int hashCode() {
		return Objects.hash(inner, outer);
	}
	
	public Ring2D copy() {
		return new Ring2D(inner.center, inner.radius, outer.radius);
	}
	
	public Point2D center() {
		return inner.center;
	}
	
	public double innerRadius() {
		return inner.radius;
	}
	
	public double outerRadius() {
		return outer.radius;
	}
	
	public Rect2D bounds() {
		return new Rect2D(inner.center.x - outer.radius,
				inner.center.y - outer.radius,
				inner.center.x + outer.radius,
				inner.center.y + outer.radius);
	}
	
	public Ring2D offset(Vector2D v) {
		return new Ring2D(inner.center.offset(v), inner.radius, outer.radius);
	}
	
	// Checks if a given point is in the ring (between or on the circles).
	public boolean isPointInRing(Point2D pt) {
		return outer.isPointInCircle(pt) && !inner.isPointInsideCircle(pt);
	}
}
