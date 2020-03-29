package geometry;

import java.util.ArrayList;
import java.util.List;

import math.FpUtil;

// Algorithm to cut a convex polygon by a line. 
public class ConvexPolygonLineCut2D {

	// Sides that a point can be on relative to a line.
	private enum Side {
		NONE, LEFT, RIGHT, CENTER;
	}
	
	// Cuts a convex polygon with an infinite line.
	public static List<Polygon2D> cut(Polygon2D poly, InfiniteLine2D l) {
		List<Polygon2D> res = new ArrayList<Polygon2D>();
		
		// Create the output polygons.
		Polygon2D leftPoly = new Polygon2D();
		Polygon2D rightPoly = new Polygon2D();
		// Keep track of whether any points lie strictly each side. Helps identifying
		// cases where the line only touches the polygon.
		boolean haveStrictlyLeftPoints = false;
		boolean haveStrictlyRightPoints = false;
		// Keep track of changes from one side of the line to another.
		Side side = Side.NONE;
		Side prevSide = Side.NONE;
		
		// Place the polygon vertices into two separate polygons depending on whether
		// a vertex is left or right of the cut line.
		for (int i = 0; i < poly.countVertices(); ++i) {
			Point2D pt = poly.vertex(i);

			// First determine the side the current vertex is on.
			prevSide = side;
			side = calcSideOfLine(l, pt);			
			
			// If the vertices switched from one side of the line to the other, we need
			// to find the intersection point and add it to both output polygons. Since
			// we are building the output polygons as we traverse the vertices the
			// intersection point has to be added to the outout polygons before the
			// current vertex otherwise the vertices will be out of order.
			if (wasLineCrossed(prevSide, side)) {
				LineSegment2D edge = new LineSegment2D(poly.vertex(i - 1), pt);
				Point2D isectPt = intersectLines(l, edge);
				if (isectPt != null) {
					leftPoly.addVertex(isectPt);
					rightPoly.addVertex(isectPt);
				}
			}
			
			// Now we can add the current vertex to the output polygons depending on
			// which side of the line it is on.
			if (side == Side.LEFT) {
				leftPoly.addVertex(pt);
				haveStrictlyLeftPoints = true;
			} else if (side == Side.RIGHT) {
				rightPoly.addVertex(pt);
				haveStrictlyRightPoints = true;
			} else {
				leftPoly.addVertex(pt);
				rightPoly.addVertex(pt);
			}
		}
		
		// Process the edge that closes the polygon (only for non-degenerate polygons).
		if (poly.countVertices() > 2) {
			Point2D pt = poly.vertex(0);
			prevSide = side;
			side = calcSideOfLine(l, pt);
			
			if (wasLineCrossed(prevSide, side)) {
				LineSegment2D edge = new LineSegment2D(
						poly.vertex(poly.countVertices() - 1), pt);
				Point2D isectPt = intersectLines(l, edge);
				if (isectPt != null) {
					leftPoly.addVertex(isectPt);
					rightPoly.addVertex(isectPt);
				}
			}
		}
		
		// Prepare the list of output polygons.
		// Special case: The input polygon was empty.
		if (leftPoly.countVertices() == 0 && rightPoly.countVertices() == 0) {
			res.add(leftPoly);
		}
		// Special case: The entire (degenerate) polygon lies on the line.
		else if (!haveStrictlyLeftPoints && !haveStrictlyRightPoints) {
			res.add(leftPoly);
		}
		// Normal case: Polygon is not degenerate.
		// Keep the output polygons that have any dedicated (not shared) points. 
		else {
			if (leftPoly.countVertices() > 0 && haveStrictlyLeftPoints)
				res.add(leftPoly);
			if (rightPoly.countVertices() > 0 && haveStrictlyRightPoints)
				res.add(rightPoly);
		}
		
		return res;
	}
	
	// Calculates which side of a line a given point is on.
	private static Side calcSideOfLine(InfiniteLine2D l, Point2D pt) {
		double perpDotResult = l.direction().perpDot(
				new Vector2D(l.anchorPoint(), pt));
		if (FpUtil.fpLess(perpDotResult, 0))
			return Side.LEFT;
		else if (FpUtil.fpGreater(perpDotResult, 0))
			return Side.RIGHT;
		return Side.CENTER;
	}
	
	// Checks if the intersecting line was crossed by two consecutive vertices.
	private static boolean wasLineCrossed(Side prev, Side now) {
		return (now == Side.LEFT && prev == Side.RIGHT) ||
				(now == Side.RIGHT && prev == Side.LEFT);
	}
	
	// Returns the intersection point of two lines or null. Assumes the lines
	// intersect at one point.
	private static Point2D intersectLines(InfiniteLine2D a, LineSegment2D b) {
		LineIntersection2D.Result isect = LineIntersection2D.intersect(a, b);
		if (isect.type == LineIntersection2D.IntersectionType.POINT)
			return (Point2D) isect.intersection;
		return null;
	}
}
