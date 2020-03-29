package geometry;

import math.FpUtil;

// Algorithm to intersect convex polygons.
public class ConvexPolygonIntersection2D {

	// Indicates which polygon's edge is inside the other polygon.
	enum InsideFlag {
		PInside,
		QInside,
		Unknown
	}
	
	// Traversal along a given poygon starting at a given point.
	private static class Traversal {
	
		private Polygon2D poly;
		private int ptIdx;
		private Point2D curPt;
		private LineSegment2D curEdge;
		// Inside flag value for this polygon.
		private final InsideFlag insideFlag;
		
		public Traversal(Polygon2D poly, int start, InsideFlag inside) {
			this.poly = poly;
			this.ptIdx = start;
			this.curPt = poly.vertex(ptIdx);
			this.curEdge = poly.edge(edgeIndex(ptIdx));
			this.insideFlag = inside;
		}
		
		public Point2D point() {
			return curPt;
		}
		
		public LineSegment2D edge() {
			return curEdge;
		}

		// Advance to next point and edge.
		public void advance() {
			ptIdx = (ptIdx + 1) % poly.countVertices();
			curPt = poly.vertex(ptIdx);
			curEdge = poly.edge(edgeIndex(ptIdx));
		}
		
		// If this traversed polygon is the inside one, add its current point to
		// the output.
		public void collectPointIfInside(InsideFlag curInside, Polygon2D out) {
			if (curInside == insideFlag)
				addUniquePoint(out, curPt);			
		}
		
		// Checks if a given point lies on the side of the current edge that is
		// towards the inside of the (ccw) polygon. Being on the edge is also
		// considered 'inside'. 
		public boolean isPointOnInside(Point2D pt) {
			Vector2D v = new Vector2D(curEdge.startPoint(), pt);
			return FpUtil.fpLessEqual(curEdge.direction().perpDot(v), 0.0);
		}
		
		public boolean isEdgeCcwOrCollinear(LineSegment2D e) {
			return FpUtil.fpLessEqual(curEdge.direction().perpDot(e.direction()), 0);
		}
		
		// Returns the index of the edge that the algorithm associates with a
		// given point index.
		private int edgeIndex(int ptIdx) {
			return (ptIdx != 0) ? ptIdx - 1 : poly.countEdges() - 1;
		}
	}
	
	// Intersects two convex polygons.
	// Source:
	// https://www.cs.jhu.edu/~misha/Spring16/ORourke82.pdf
	public static Polygon2D intersect(Polygon2D PIn, Polygon2D QIn) {
		Polygon2D resultPoly = new Polygon2D();

		// Special cases.
		if (PIn.countVertices() == 0 || QIn.countVertices() == 0)
			return resultPoly;
		if (PIn.countVertices() == 1)
			return intersectWithPoint(PIn.vertex(0), QIn);
		if (QIn.countVertices() == 1)
			return intersectWithPoint(QIn.vertex(0), PIn);
		if (PIn.countVertices() == 2)
			return intersectWithLine(PIn.edge(0), QIn);
		if (QIn.countVertices() == 2)
			return intersectWithLine(QIn.edge(0), PIn);
		if (!PIn.isConvex() || !QIn.isConvex())
			return resultPoly;

		Polygon2D P = makeCcw(PIn);
		Polygon2D Q = makeCcw(QIn);
		
		int maxIter = 2 * (P.countEdges() + Q.countEdges());
		int numIter = 0;
		
		Point2D firstIsectPt = null;
		int firstIsectFoundIter = -1;
		
		Traversal p = new Traversal(P, 1, InsideFlag.PInside);
		Traversal q = new Traversal(Q, 1, InsideFlag.QInside);
		InsideFlag curInside = InsideFlag.Unknown; 

		while (numIter <= maxIter) {
			// Find intersection.
			LineIntersection2D.Result isect =
					LineIntersection2D.intersect(p.edge(), q.edge());
			// Collinear edges, i.e. intersections that result in a line, are
			// interpreted as 'no intersection'.
			if (isect.type == LineIntersection2D.IntersectionType.POINT) {
				Point2D isectPt = (Point2D) isect.intersection;
				if (firstIsectPt == null) {
					// Keep track of first intersection and the iteration it was
					// found in to detect a complete loop around the polygons.
					firstIsectPt = isectPt;
					firstIsectFoundIter = numIter;
				} else if (isectPt.equals(firstIsectPt) &&
						firstIsectFoundIter != numIter - 1) {
					// First intersection reached again. Stop.
					return resultPoly;
				}
				
				addUniquePoint(resultPoly, isectPt);
				
				if (q.isPointOnInside(p.point()))
					curInside = InsideFlag.PInside;
				else
					curInside = InsideFlag.QInside;
			}
			
			// Advance.
			advance(p, q, curInside, resultPoly);
			++numIter;
		}
		
		// The polygons either don't intersect at all or one is completely within
		// the other.
		if (Polygon2D.isPointInsideConvexPolygon(p.point(), Q))
			return P;
		else if (Polygon2D.isPointInsideConvexPolygon(q.point(), P))
			return Q;
		return new Polygon2D();
	}
	
	// Intersects a given point with a given polygon and returns the result as
	// a polygon.
	private static Polygon2D intersectWithPoint(Point2D pt, Polygon2D poly) {
		Polygon2D resultPoly = new Polygon2D();
		if (Polygon2D.isPointInsideConvexPolygon(pt, poly))
			resultPoly.addVertex(pt);
		return resultPoly; 
	}
	
	// Intersects a given line with a given polygon and returns the result as
	// a polygon.
	private static Polygon2D intersectWithLine(Line2D line, Polygon2D poly) {
		Polygon2D resultPoly = new Polygon2D();

		// Find intersections of the line and the polygon's edges.
		for (int i = 0; i < poly.countEdges(); ++i) {
			LineIntersection2D.Result isect =
					LineIntersection2D.intersect(line, poly.edge(i));
			if (isect.type == LineIntersection2D.IntersectionType.POINT) {
				addUniquePoint(resultPoly, (Point2D) isect.intersection);
			} else if (isect.type == LineIntersection2D.IntersectionType.LINE_SEGMENT) {
				LineSegment2D isectLine = (LineSegment2D) isect.intersection;
				addUniquePoint(resultPoly, isectLine.startPoint());
				addUniquePoint(resultPoly, isectLine.endPoint());
			}
		}
		
		// Add the line end points that are inside the polygon to the result.
		// Note that for the case of two intersection points we know already
		// that none of the line end points can be inside the polygon.
		int numVert = resultPoly.countVertices();
		if (numVert == 0 || numVert == 1) {
			Point2D vert = (numVert == 1) ? resultPoly.vertex(0) : null;
			Point2D lineStart = line.startPoint(); 
			Point2D lineEnd = line.endPoint(); 

			if (!lineStart.equals(vert) &&
					Polygon2D.isPointInsideConvexPolygon(lineStart, poly)) {
				insertUniquePoint(resultPoly, lineStart, 0);
			}
			if (!lineEnd.equals(vert) &&
					Polygon2D.isPointInsideConvexPolygon(lineEnd, poly)) {
				addUniquePoint(resultPoly, lineEnd);
			}
		}
		
		return resultPoly; 
	}
	
	// Changes the orientation of a given polygon to counter-clockwise.
	private static Polygon2D makeCcw(Polygon2D poly) {
		if (!isCcw(poly))
			return poly.reversed();
		return poly;
	}
	
	// Checks if a given convex polygon's orientation is counter-clockwise.
	// Assumes the polygon is not degenerate.
	private static boolean isCcw(Polygon2D poly) {
		// Since we know it's a convex polygon we only have to check the
		// orientation of the first two edges.
		return poly.edge(0).direction().isCcw(poly.edge(1).direction());
	}
	
	// Adds a given point to a given polygon if the polygon does not contain it already.
	private static void addUniquePoint(Polygon2D poly, Point2D pt) {
		if (!poly.hasVertex(pt))
			poly.addVertex(pt);
	}
	
	// Inserts a given point to a given polygon if the polygon does not contain it
	// already.
	private static void insertUniquePoint(Polygon2D poly, Point2D pt, int idx) {
		if (!poly.hasVertex(pt))
			poly.insertVertex(pt, idx);
	}
	
	// Advances the traversal state of one of the polygons.  
	private static void advance(Traversal p, Traversal q, InsideFlag curInside,
			Polygon2D out) {
		Traversal rear = null;
		if (q.isEdgeCcwOrCollinear(p.edge()))
			rear = q.isPointOnInside(p.point()) ? q : p;
		else
			rear = p.isPointOnInside(q.point()) ? p : q;
		
		rear.collectPointIfInside(curInside, out);
		rear.advance();
	}
}
