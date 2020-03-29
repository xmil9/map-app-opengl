package math;

import java.lang.Math;
import java.util.Random;

// Mathematical utility functions. 
public class MathUtil {
	
	///////////////
	
	// Ranges.
	
	// Limits a value to given min and max values. 
	public static double clampToRange(double val, double min, double max) {
		// Normalize.
		if (min > max) {
			double tmp = min;
			min = max;
			max = tmp;
		}

		// Clamp.
		if (val < min)
			val = min;
		if (val > max)
			val = max;

		assert min <= val && val <= max;
		return val;
	}

	// Shifts a given value into the range of given min and max values. The
	// value will end up in the same position within the range.
	// Example:
	//   780 shift into [0, 360] => 60
	public static double shiftIntoRange(double val, double min, double max) {
		// Normalize.
		if (min > max) {
			double tmp = min;
			min = max;
			max = tmp;
		}
		
		// Guard against div by zero.
		double len = max - min;
		if (len == 0.0)
			return val;
		
		// Shift.
		if (val < min)
			val = max - (min - val) % len;
		if (val > max)
			val = min + (val - min) % len;
		
		assert min <= val && val <= max;
		return val;
	}
	
	// Returns the next index for a given index. Cycles around to zero when index
	// passes the end of the range.
	public static int cyclicNext(int curIdx, int numElems) {
		if (curIdx < numElems - 1)
			return ++curIdx;
		return 0;
	}
	
	///////////////
	
	// Angle unit conversions.
	
	public static double degreesFromRadians(double rad) {
		return rad * 180.0 / Math.PI;
	}
	
	public static double radiansFromDegrees(double deg) {
		return deg * Math.PI / 180.0;
	}

	///////////////
	
	// Sign
	
	public enum Sign {
		POSITIVE,
		NEGATIVE,
		NONE
	}
	
	public static Sign sign(double val) {
		if (FpUtil.fpGreater(val, 0))
			return Sign.POSITIVE;
		else if (FpUtil.fpLess(val, 0))
			return Sign.NEGATIVE;
		return Sign.NONE;
	}
	
	///////////////
	
	// Random
	
	// Returns random value with approx Gaussian distribution in range [min, max].
	// The distribution is only approx because a Gaussian distribution has no
	// limits (extreme values are just very unlikely). This function clamps values
	// 3 times the standard distribution (1.0) away from the median (0.0).
	public static double randGaussian(Random rand, double min, double max) {
		double val = rand.nextGaussian();
		// Limit value to [0, 6].
		double clamped = clampToRange(val + 3, 0, 6);
		// Map value to [0, 1].
		double normed = clamped / 6.0;
		// Map to given range.
		return min + (max - min) * normed;
	}
}
