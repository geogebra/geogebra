package org.apache.commons.math.util;

public class FastMath {

	public static double abs(double d) {
		return Math.abs(d);
	}

	public static double sqrt(double d) {
		return Math.sqrt(d);
	}

	public static double ulp(double x) {
	       if (Double.isInfinite(x)) {
	            return Double.POSITIVE_INFINITY;
	        }
	        return abs(x - Double.longBitsToDouble(Double.doubleToLongBits(x) ^ 1));
	}

	public static double max(int m, int n) {
		return Math.max(m, n);
	}

	public static double max(double m, double n) {
		return Math.max(m, n);
	}

}
