package geometry;

import math.Interval;
import math.FpUtil;
import types.Pair;

// Calculates intersections between all line types. 
public class LineIntersection2D {

	// Possible outcomes of intersecting two lines.
	public enum IntersectionType {
		NONE, POINT, LINE_SEGMENT, LINE_RAY, INFINITE_LINE
	}
	
	// Result data for intersecting two lines.
	public static class Result {
		public final IntersectionType type;
		// The 2D geometric object that represents the intersection. The intersection
		// type determines the type of the intersection object.
		public final Object intersection;
		
		public Result() {
			this.type = IntersectionType.NONE;
			this.intersection = null;
		}
		public Result(IntersectionType type, Object intersection) {
			this.type = type;
			this.intersection = intersection;
		}
		public static Result noIntersection() {
			return new Result();
		}
	}
	
	private static double NEG_INF = -Double.MAX_VALUE;
	private static double POS_INF = Double.MAX_VALUE;
	
	// Intersects two given lines.
	public static Result intersect(Line2D a, Line2D b) {
		if (a.isPoint() || b.isPoint())
			return intersectDegenerateLines(a, b);
		
		if (a.isCoincident(b)) {
			return intersectCoincidentLines(a, b);
		} else if (a.isParallel(b)) {
			// The lines are parallel but not coincident.
			return Result.noIntersection();
		}
		
		return intersectSkewLines(a, b);
	}

	// Intersects two given lines where at least one line degenerated into a point.
	private static Result intersectDegenerateLines(Line2D a, Line2D b) {
		if (a.isPoint()) {
			// Also covers case of both lines being points.
			return intersectPointLine(a.anchorPoint(), b);
		}
		
		assert b.isPoint();
		return intersectPointLine(b.anchorPoint(), a);
	}
	
	// Returns the result for the reduced problem of intersecting a point with a line.
	private static Result intersectPointLine(Point2D pt, Line2D line) {
		boolean isOnLine = line.isPointOnLine(pt).isOnLine;
		if (isOnLine)
			return new Result(IntersectionType.POINT, pt);
		return Result.noIntersection();
	}
	
	// Intersects two given lines that are coincident (lie on the same infinite line). 
	private static Result intersectCoincidentLines(Line2D a, Line2D b) {
		// Use the parametric values of the lines' start and end points to
		// create intervals that represent the lines on the infinite line
		// that they share. Express the interval of line b with respect to
		// line a, so that the parametric value ranges can be compared.
		// When line b is infinite or a ray watch out to choose the correct
		// inifinite parametric values depending on whether b is in the same
		// or the opposite direction of a. 
		double begin = a.hasStartPoint() ? 0.0 : NEG_INF;
		double end = a.hasEndPoint() ? 1.0 : POS_INF;
		Interval aIval = new Interval(begin, end);
		
		boolean hasSameDir = b.direction().hasSameDirection(a.direction());
		begin = b.hasStartPoint() ? a.calcParametricValue(b.startPoint())
				: hasSameDir ? NEG_INF : POS_INF;
		end = b.hasEndPoint() ? a.calcParametricValue(b.endPoint())
				: hasSameDir ? POS_INF : NEG_INF;
		Interval bIval = new Interval(begin, end);
		
		// Calculate the overlap of the two intervals.
		Interval overlap = aIval.intersect(bIval);
		
		IntersectionType isectType = determineCoincidentIntersectionType(overlap); 
		return new Result(isectType,
				makeConcidentIntersection(isectType, overlap, a));
	}
	
	// Determines the intersetion type from a given overlap interval of
	// two coincident lines.
	private static IntersectionType determineCoincidentIntersectionType(
			Interval overlap) {
		if (overlap == null)
			return IntersectionType.NONE;

		int numInfiniteEnds = 0;
		if (overlap.a == NEG_INF)
			++numInfiniteEnds;
		if (overlap.b == POS_INF)
			++numInfiniteEnds;

		switch (numInfiniteEnds) {
		case 0:
			return FpUtil.fpEqual(overlap.a, overlap.b) ?
					IntersectionType.POINT : IntersectionType.LINE_SEGMENT;
		case 1:
			return IntersectionType.LINE_RAY;
		case 2:
			return IntersectionType.INFINITE_LINE;
		}
		
		assert false;
		return IntersectionType.NONE;
	}

	// Creates the geometric shape for the intersection of two coincident lines
	// based on the given intersection type, the parametric values of the
	// intersetion shape and the reference line for the parametric values.
	private static Object makeConcidentIntersection(IntersectionType type,
			Interval parametricEndpoints, Line2D line) {
		switch (type) {
		case NONE:
			return null;
		case POINT:
			return line.calcPointAt(parametricEndpoints.a);
		case LINE_SEGMENT:
			return new LineSegment2D(line.calcPointAt(parametricEndpoints.a),
					line.calcPointAt(parametricEndpoints.b));
		case LINE_RAY:
			if (parametricEndpoints.a == NEG_INF) {
				return new LineRay2D(line.calcPointAt(parametricEndpoints.b),
						line.direction().scale(-1));
				
			} else {
				assert parametricEndpoints.b == POS_INF;
				return new LineRay2D(line.calcPointAt(parametricEndpoints.a),
						line.direction());
			}
		case INFINITE_LINE:
			return new InfiniteLine2D(line.anchorPoint(), line.direction());
		}
		
		assert false;
		return null;
	}
	
	// Intersects two given lines that have no special relation to each other.
	// Source: http://geomalgorithms.com/a05-_intersect-1.html
	private static Result intersectSkewLines(Line2D a, Line2D b) {
		Pair<Double, Double> paramVals = calcParametricIntersectionPointValues(a, b);
		
		// The lines only intersect if the parametric values of the intersection
		// points lie on each line (not beyond the start or end points).
		if (isParametricPointOnLine(paramVals.a, a) &&
				isParametricPointOnLine(paramVals.b, b)) {
			return new Result(IntersectionType.POINT, a.calcPointAt(paramVals.a));
		}
		return Result.noIntersection();
	}
	
	// Calculates the parametric values of the intersection point for two
	// given lines.
	// Source: http://geomalgorithms.com/a05-_intersect-1.html
	private static Pair<Double, Double> calcParametricIntersectionPointValues(
			Line2D a, Line2D b) {
		assert !a.isParallel(b);
		
		Vector2D u = a.direction();
		Vector2D v = b.direction();
		Vector2D w = new Vector2D(b.anchorPoint(), a.anchorPoint());
		
		// Calc parametric values of the intersection point relative to each
		// line.
		// The divisions are safe because the denominators would only be zero
		// if the lines were parallel which should be checked before calling
		// this method.
		double paramValA = (v.y * w.x - v.x * w.y) / v.perpDot(u);
		double paramValB = u.perpDot(w) / u.perpDot(v);
		
		return new Pair<Double, Double>(Double.valueOf(paramValA),
				Double.valueOf(paramValB));
	}
	
	// Checks if the point for given parametric value is on the line and not beyond
	// the start or end points.
	private static boolean isParametricPointOnLine(double parametricVal, Line2D line) {
		if (line.hasStartPoint() && FpUtil.fpLess(parametricVal, 0))
			return false;
		if (line.hasEndPoint() && FpUtil.fpGreater(parametricVal, 1))
			return false;
		return true;
	}
}
