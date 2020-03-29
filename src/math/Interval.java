package math;

import java.util.Objects;

// Represents a (closed) interval between two values.
public class Interval extends Object {

	public final double a;
	public final double b;
	
	public Interval(double a, double b) {
		// Normalize.
		this.a = a <= b ? a : b;
		this.b = a <= b ? b : a;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		Interval otherIval = (Interval) other;
		return FpUtil.fpEqual(a, otherIval.a) &&
				FpUtil.fpEqual(b, otherIval.b);
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, b);
	}
	
	public Interval copy() {
		return new Interval(a, b);
	}
	
	public double length() {
		return b - a;
	}
	
	public boolean isEmpty() {
		return FpUtil.fpEqual(length(), 0.0); 
	}
	
	public boolean contains(double val) {
		return FpUtil.fpGreaterEqual(val, a) &&
				FpUtil.fpLessEqual(val, b);
	}
	
	public Interval intersect(Interval other) {
		Interval first = (a <= other.a) ? this : other;
		Interval second = (a <= other.a) ? other : this;
		
		if (first.b < second.a) {
			// Disjoint.
			return null;
		} else if (first.b >= second.b) {
			// Fully contained.
			return second;
		}
		// Overlapping.
		return new Interval(second.a, first.b);
	}
	
	public Interval unite(Interval other) {
		double min = Math.min(a, Math.min(other.a, other.b));
		double max = Math.max(b, Math.max(other.a, other.b));
		return new Interval(min, max);
	}
}
