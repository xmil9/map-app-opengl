package geometry;


// Common functionality for different line types.
public abstract class Line2D extends Object {
	
	// Result data for checking if a point is on a line.
	public static class PointOnLineResult {
		// Is the point on the line?
		public final boolean isOnLine;
		// The value with which the line vector needs to be scaled to yield the
		// point.
		public final double parametricValue;
		
		public PointOnLineResult() {
			this.isOnLine = false;
			this.parametricValue = 0.0;
		}
		public PointOnLineResult(double parametricVal) {
			this.isOnLine = true;
			this.parametricValue = parametricVal;
		}
	}

	// Point that anchors the line in the coordinate system. For line types that
	// have a start point it is guaranteed to be the start point.
	protected final Point2D anchor;
	// Direction of line. Whether the length of the direction vector has meaning
	// is up to each derived class. 
	protected final Vector2D dir;
	
	// Ctor to initialize the final members.
	protected Line2D(Point2D anchor, Vector2D direction) {
		this.anchor = anchor;
		this.dir = direction;
	}
	
	// Creates a copy of line object.
	public abstract Line2D copy();

	// Checks if the line degenerates into a point.
	public boolean isPoint() {
		return dir.isZeroVector();
	}
	
	public Point2D anchorPoint() {
		return anchor;
	}
	
	public abstract boolean hasStartPoint();
	// Returns start point of line or null if it doesn't have one.
	public abstract Point2D startPoint();
	public abstract boolean hasEndPoint();
	// Returns end point of line or null if it doesn't have one.
	public abstract Point2D endPoint();

	public Vector2D direction() {
		return dir;
	}
	
	// Checks if a given point is on the line.
	public PointOnLineResult isPointOnLine(Point2D pt) {
		// Default to negative result. Subclasses should customize this method.
		return new PointOnLineResult();
	}
	
	// Checks if a given point is on the infinite extension of the line.
	public PointOnLineResult isPointOnInfiniteLine(Point2D pt) {
		double parametricVal = calcParametricValue(pt);
		if (parametricVal == Double.MAX_VALUE)
			return new PointOnLineResult();
		return new PointOnLineResult(parametricVal);
	}
	
	// Calculates the parametric value of a given point along the line.
	// Returns 'Double.MAX_VALUE' if the point is not on the line.
	public double calcParametricValue(Point2D pt) {
		final double NOT_FOUND = Double.MAX_VALUE;
		
		if (isPoint())
			return (pt.equals(anchor)) ? 0.0 : NOT_FOUND;
		
		Vector2D v = new Vector2D(anchor, pt);
		if (!v.isParallel(dir))
			return NOT_FOUND;
		
		// length != 0 is assured by checking whether line is a point above.
		double parametricVal = v.length() / dir.length();
		if (!v.hasSameDirection(dir))
			parametricVal *= -1;
		
		return parametricVal;
	}
	
	// Returns the point at a given parametric value along the line.
	public Point2D calcPointAt(double parametricVal) {
		Vector2D v = dir.scale(parametricVal);
		return anchor.offset(v);
	}
	
	// Checks if a given line is parallel to this line.
	public boolean isParallel(Line2D line) {
		return dir.isParallel(line.direction().normalize());
	}
	
	// Checks if a given line is on the same inifinite line as this line.
	public boolean isCoincident(Line2D line) {
		return isParallel(line) && isPointOnInfiniteLine(line.anchor).isOnLine;
	}
	
	// Intersects given line with this line.
	public LineIntersection2D.Result intersect(Line2D other) {
		return LineIntersection2D.intersect(this, other);
	}
}
