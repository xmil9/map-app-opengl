package geometry;

import java.util.Comparator;
import java.util.Objects;

import math.FpUtil;

// Represents a point in 2D space.
public class Point2D extends Object {
	public final double x;
	public final double y;
	
	public Point2D() {
		this(0, 0);
	}
	
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		Point2D otherPt = (Point2D) other;
		return FpUtil.fpEqual(x, otherPt.x) &&
				FpUtil.fpEqual(y, otherPt.y);
	}

	// Returns a Comparator object that compares Point2D objects by their
	// x-coordinates and if they are equal by their y-coordinate.
	public static Comparator<Point2D> makeXYComparator() {
		return new Comparator<Point2D>() {         
		    @Override         
		    public int compare(Point2D a, Point2D b) {
		    	if (FpUtil.fpLess(a.x, b.x))
		    		return -1;
		    	if (FpUtil.fpEqual(a.x, b.x)) {
			    	if (FpUtil.fpLess(a.y, b.y))
			    		return -1;
			    	if (FpUtil.fpEqual(a.y, b.y))
			    		return 0;
		    		
		    	}
		    	return 1;         
		    }
		};
	}
	
	// Returns a Comparator object that compares Point2D objects by their
	// x-coordinates.
	public static Comparator<Point2D> makeXComparator() {
		return new Comparator<Point2D>() {         
		    @Override         
		    public int compare(Point2D a, Point2D b) {
		    	if (FpUtil.fpLess(a.x, b.x))
		    		return -1;
		    	if (FpUtil.fpEqual(a.x, b.x))
		    		return 0;
		      return 1;         
		    }     
		};
	}
	
	// Caution: Because of the threshold-based equality for Point2D objects
	// it is always possible to find two points that fall into separate hash
	// buckets but compare equal.
	// Better not to use hash-based collections for Point2D objects.
	@Override
	public int hashCode() {
		return Objects.hash(FpUtil.fpHashCode(x), FpUtil.fpHashCode(y));
	}
	
	@Override
	public String toString() {
		return "(" + Double.toString(x) + ", " + Double.toString(y) + ")";
	}
	
	public Point2D copy() {
		return new Point2D(x, y);
	}
	
	public Point2D offset(double dx, double dy) {
		return new Point2D(x + dx, y + dy);
	}
	
	public Point2D offset(Vector2D v) {
		return offset(v.x, v.y);
	}
	
	public Point2D scale(double factor) {
		return new Point2D(x * factor, y * factor);
	}
	
	// Returns distance between given points.
	// Because it uses a sqrt the calculation is slow. To compare distances
	// it is faster to compare their squared values.
	public static double distance(Point2D a, Point2D b) {
		return Math.sqrt(distanceSquared(a, b));
	}
	
	// Returns squared distance between given points.
	public static double distanceSquared(Point2D a, Point2D b) {
		double dx = b.x - a.x;
		double dy = b.y - a.y;
		return dx * dx + dy * dy;
	}
}
