package math;

import java.util.Objects;


public class FpUtil {

	///////////////
	
	// Floating point threshold for equality of two floating point values.
	public static final double defaultFpThres = 0.0000001;
	private static double globalFpThres = defaultFpThres;
	
	public static double globalFpThreshold() {
		return globalFpThres;
	}
	
	public static void setGlobalFpThreshold(double thres) {
		globalFpThres = thres;
		truncationFactor = calcTruncationFactor(thres);
	}
	
	///////////////

	// Inverse of fp threshold. Used to generate hash codes for fp values.
	private static double truncationFactor = calcTruncationFactor(globalFpThres);

	private static double calcTruncationFactor(double fpThreshold) {
		return 1.0 / fpThreshold;
	}
	
	// Returns a hash code for a given floating point value. Attempts to account
	// for the equality threshold of fp values.
	// Note: Does not work because it is always possible to find two fp values
	// that compare equal but fall into separate hash buckets.
	// Better not to use hash-based collections for fp values or be aware of the
	// limitations.
	public static long fpHashCode(double fp) {
		return Objects.hash(Math.round(fp * truncationFactor));
	}

	// Overload for given fp threshold.
	public static long fpHashCode(double fp, double thres) {
		double truncFactor = calcTruncationFactor(thres);
		return Objects.hash(Math.round(fp * truncFactor));
	}
	
	///////////////
	
	// Floating point comparisions.
	
	public static boolean fpEqual(double a, double b, double thres) {
		return Math.abs(a - b) <= thres; 
	}
	
	public static boolean fpEqual(double a, double b) {
		// Should call overload taking a threshold but since this is
		// a very hot function it is considerably faster to do the
		// calculation right here.
		return Math.abs(a - b) <= globalFpThres;
	}

	public static boolean fpLess(double a, double b, double thres) {
		// Check that a is smaller than b by at least the threshold value
		// because within the threashold they would still be considered
		// equal.
		return a - b < -thres; 
	}
	
	public static boolean fpLess(double a, double b) {
		// Should call overload taking a threshold but since this is
		// a very hot function it is considerably faster to do the
		// calculation right here.
		return a - b < -globalFpThres;
	}

	public static boolean fpLessEqual(double a, double b, double thres) {
		// Check that b is larger than a by at least the threshold value.
		// because within the threashold they would still be considered
		// equal.
		return a - b <= thres;
	}
	
	public static boolean fpLessEqual(double a, double b) {
		// Should call overload taking a threshold but since this is
		// a very hot function it is considerably faster to do the
		// calculation right here.
		return a - b <= globalFpThres;
	}

	public static boolean fpGreater(double a, double b, double thres) {
		// Check that a is larger than b by at least the threshold value
		// because within the threashold they would still be considered
		// equal.
		return a - b > thres;
	}
	
	public static boolean fpGreater(double a, double b) {
		// Should call overload taking a threshold but since this is
		// a very hot function it is considerably faster to do the
		// calculation right here.
		return a - b > globalFpThres;
	}

	public static boolean fpGreaterEqual(double a, double b, double thres) {
		// Check that b is smaller than a by at least the threshold value.
		// because within the threashold they would still be considered
		// equal.
		return a - b >= -thres;
	}
	
	public static boolean fpGreaterEqual(double a, double b) {
		// Should call overload taking a threshold but since this is
		// a very hot function it is considerably faster to do the
		// calculation right here.
		return a - b >= -globalFpThres;
	}
}
