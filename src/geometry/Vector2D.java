package geometry;

import java.util.Objects;

import math.FpUtil;

// 2-dimensional mathematical vector.
public class Vector2D extends Object {
	public final double x;
	public final double y;
	
	public Vector2D() {
		this(0, 0);
	}
	
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2D(Point2D from, Point2D to) {
		this(to.x - from.x, to.y - from.y);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		Vector2D otherVec = (Vector2D) other;
		return FpUtil.fpEqual(x, otherVec.x) &&
				FpUtil.fpEqual(y, otherVec.y);
	}

	@Override
	public int hashCode() {
		return Objects.hash(FpUtil.fpHashCode(x), FpUtil.fpHashCode(y));
	}

	public Vector2D copy() {
		return new Vector2D(x, y);
	}
	
	public boolean isZeroVector() {
		return lengthSquared() == 0;
	}
	
	// Faster to calculate than the length because taking the square root can be avoided. 
	public double lengthSquared() {
		return dot(this);
	}
	
	public double length() {
		return Math.sqrt(lengthSquared());
	}
	
	public Vector2D normalize() {
		double len = length();
		if (len == 0.0)
			return copy();
		return scale(1.0 / len);
	}
	
	public Vector2D scale(double factor) {
		return new Vector2D(x * factor, y * factor);
	}
	
	// Calculates the dot product with a given vector.
	// Also called inner or scalar product.
	// Meaning:
	//   v.dot(w) - The length of the projection of v onto w.
	// Properties:
	//   v.dot(w) == 0 => v and w are perpendicular
	//   v.dot(w) > 0  => angle between v and w is acute, i.e abs(angle) < 90
	//   v.dot(w) < 0  => angle between v and w is obtuse, i.e abs(angle) > 90
	// Source:
	//   http://geomalgorithms.com/vector_products.html
	public double dot(Vector2D w) {
		return x * w.x + y * w.y;
	}
	
	// Calculates the perp dot product with a given vector.
	// Also called external or outer product.
	// Named because it is the same as the dot product of the perpendicular vector
	// to the first vector and the second vector: perpDot(v, w) = dot(perp(v), w)
	// Meaning:
	//   v.perpDot(w) - The signed length of the 3D cross product between v and w.
	// Properties:
	//   v.perpDot(w) == 0 => v and w have same or opposite directions
	//   v.perpDot(w) > 0  =>
	// 		cartesian CS: w is ccw of v when facing into direction of v
	// 		screen CS   : w is cw of v when facing into direction of v
	//   v.perpDot(w) < 0  =>
	// 		cartesian CS: w is cw of v when facing into direction of v
	// 		screen CS   : w is ccw of v when facing into direction of v
	// Other usage:
	//   Gives the (signed) area of the 2D parallelogram spanned by 'this' and the
	//   given vector.
	// Source:
	//   http://geomalgorithms.com/vector_products.html
	public double perpDot(Vector2D w) {
		return x * w.y - y * w.x;
	}
	
	// Checks if a given vector is perpendicular to 'this'.
	public boolean isPerpendicular(Vector2D w) {
		return dot(w) == 0;
	}
	
	// Checks if a given vector is orthogonal to 'this'.
	// Orthogonal describes the same concept as perpendicular but can be applied to
	// other geometric objects, too. For lines it is the same as perpendicular.
	public boolean isOrthogonal(Vector2D w) {
		return isPerpendicular(w);
	}
	
	// Checks if a given vector has the same direction as 'this'.
	public boolean hasSameDirection(Vector2D w) {
		return isParallel(w) && hasAcuteAngle(w);
	}

	// Checks if a given vector is parallel to 'this'.
	// Could be pointing in the same or opposite direction.
	public boolean isParallel(Vector2D w) {
		return FpUtil.fpEqual(perpDot(w), 0.0);
	}
	
	// Checks if the angle between 'this' and a given vector is < 90.
	public boolean hasAcuteAngle(Vector2D w) {
		return FpUtil.fpGreater(dot(w), 0.0);
	}
	
	// Checks if the angle between 'this' and a given vector is > 90.
	public boolean hasObtuseAngle(Vector2D w) {
		return FpUtil.fpLess(dot(w), 0.0);
	}
	
	// Checks if a given vector is counter-clockwise of 'this' when facing into
	// the direction of 'this'. This depends on the coordinate system used.
	public boolean isCcw(Vector2D w, CoordSystem cs) {
		if (cs == CoordSystem.SCREEN)
			return FpUtil.fpLess(perpDot(w), 0.0);
		return FpUtil.fpGreater(perpDot(w), 0.0);
	}

	public boolean isCcw(Vector2D w) {
		// Default to screen coord system.
		return isCcw(w, CoordSystem.SCREEN);
	}
	
	// Checks if a given vector is clockwise of 'this' when facing into the
	// direction of 'this'. This depends on the coordinate system used.
	public boolean isCw(Vector2D w, CoordSystem cs) {
		if (cs == CoordSystem.SCREEN)
			return FpUtil.fpGreater(perpDot(w), 0.0);
		return FpUtil.fpLess(perpDot(w), 0.0);
	}

	public boolean isCw(Vector2D w) {
		// Default to screen coord system.
		return isCw(w, CoordSystem.SCREEN);
	}
	
	// Returns a vector that is a clockwise perpendicular to 'this'. This depends
	// on the coordinate system used.
	public Vector2D cwNormal(CoordSystem cs) {
		if (cs == CoordSystem.SCREEN)
			return new Vector2D(-y, x);
		return new Vector2D(y, -x);
	}
	
	public Vector2D cwNormal() {
		// Default to screen coord system.
		return cwNormal(CoordSystem.SCREEN);
	}
	
	// Returns a vector that is a counter-clockwise perpendicular to 'this'. This
	// depends on the coordinate system used. 
	public Vector2D ccwNormal(CoordSystem cs) {
		if (cs == CoordSystem.SCREEN)
			return new Vector2D(y, -x);
		return new Vector2D(-y, x);
	}
	
	public Vector2D ccwNormal() {
		// Default to screen coord system.
		return ccwNormal(CoordSystem.SCREEN);
	}
}
