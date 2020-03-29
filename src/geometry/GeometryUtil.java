package geometry;

import java.util.List;

import math.FpUtil;
import math.MathUtil;

// Geometry related utility functionality.
public class GeometryUtil {

	// Calculates a minimal rectangle around given points.
	public static Rect2D calcBoundingBox(Point2D[] points) {
		if (points.length == 0)
			return new Rect2D();
		if (points.length == 1)
			return new Rect2D(points[0], points[0]);
		
		Rect2D bounds = new Rect2D(points[0], points[1]);
		
		for (int i = 2; i < points.length; ++i) {
			Point2D pt = points[i];
			if (pt.x < bounds.left())
				bounds.setLeft(pt.x);
			if (pt.y < bounds.top())
				bounds.setTop(pt.y);
			if (pt.x > bounds.right())
				bounds.setRight(pt.x);
			if (pt.y > bounds.bottom())
				bounds.setBottom(pt.y);
		}
		
		return bounds;
	}

	// Overload for array list.
	public static Rect2D calcBoundingBox(List<Point2D> points) {
		return calcBoundingBox(points.toArray(new Point2D[points.size()]));
	}

	// Checks if given points form a convex path.
	// Convex path - All edges bend in the same direction and don't cross.
	public static boolean isConvexPath(List<Point2D> path) {
		if (path.size() <= 3)
			return true;
		
		Vector2D prev = null;
		Vector2D next = null;
		Orientation orientation = Orientation.None;
		
		for (int i = 0; i < path.size(); ++i) {
			int nextIdx = MathUtil.cyclicNext(i, path.size());
			prev = next;
			next = new Vector2D(path.get(i), path.get(nextIdx));
			if (prev == null)
				continue;
			
			double val = prev.perpDot(next);
			if (FpUtil.fpEqual(val, 0))
				continue;

			if (orientation == Orientation.None)
				orientation = FpUtil.fpGreater(val, 0) ?
						Orientation.Cw : Orientation.Ccw;
			
			// Orientation cannot change for convex paths.
			Orientation edgeOrientation = FpUtil.fpGreater(val, 0) ?
					Orientation.Cw : Orientation.Ccw; 
			if (edgeOrientation != orientation)
				return false;
		}

		return true;
	}
}
