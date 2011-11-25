package geogebra.common.kernel;

import geogebra.common.util.LaTeXCache;

public abstract class AbstractKernel {
	
	final public static int ANGLE_RADIANT = 1;
	final public static int ANGLE_DEGREE = 2;
	final public static int COORD_CARTESIAN = 3;
	final public static int COORD_POLAR = 4;	 
	final public static int COORD_COMPLEX = 5;
	final public static double PI_2 = 2.0 * Math.PI;
	final public static double PI_HALF =  Math.PI / 2.0;
	final public static double SQRT_2_HALF =  Math.sqrt(2.0) / 2.0;
	final public static double PI_180 = Math.PI / 180;
	final public static double CONST_180_PI = 180 / Math.PI;

	
	/** standard precision */ 
	public final static double STANDARD_PRECISION = 1E-8;
	public final static double STANDARD_PRECISION_SQRT = 1E-4;
	
	/** minimum precision */
	public final static double MIN_PRECISION = 1E-5;
	private final static double INV_MIN_PRECISION = 1E5; 

	/** maximum reasonable precision */
	public final static double MAX_PRECISION = 1E-12;
	
	/** current working precision */
	public static double EPSILON = STANDARD_PRECISION;
	public static double EPSILON_SQRT = STANDARD_PRECISION_SQRT;

	/** maximum precision of double numbers */
	public final static double MAX_DOUBLE_PRECISION = 1E-15;
	/** reciprocal of maximum precision of double numbers */
	public final static double INV_MAX_DOUBLE_PRECISION = 1E15;	
	
	/** if x is nearly zero, 0.0 is returned,
	 *  else x is returned
	 */
	final public static double chop(double x) {
		if (isZero(x))
			return 0.0d;
		else
			return x;
	}
	
	/** is abs(x) < epsilon ? */
	final public static boolean isZero(double x) {
		return -EPSILON < x && x < EPSILON;
	}

	final static boolean isZero(double[] a) {
		for (int i = 0; i < a.length; i++) {
			if (!isZero(a[i]))
				return false;
		}
		return true;
	}

	final public static boolean isInteger(double x) {
		if (x > 1E17)
			return true;
		else
			return isEqual(x, Math.round(x));		
	}

	/**
	 * Returns whether x is equal to y	 
	 * infinity == infinity returns true eg 1/0	 
	 * -infinity == infinity returns false	 eg -1/0
	 * -infinity == -infinity returns true
	 * undefined == undefined returns false eg 0/0	 
	 */
	final public static boolean isEqual(double x, double y) {	
		if (x == y) // handles infinity and NaN cases
			return true;
		else
			return x - EPSILON <= y && y <= x + EPSILON;
	}
	
	final public static boolean isEqual(double x, double y, double eps) {		
		if (x == y) // handles infinity and NaN cases
			return true;
		else
		return x - eps < y && y < x + eps;
	}
	
	/**
	 * Returns whether x is greater than y	 	 
	 */
	final public static boolean isGreater(double x, double y) {
		return x > y + EPSILON;
	}
	
	/**
	 * Returns whether x is greater than y	 	 
	 */
	final public static boolean isGreater(double x, double y,double eps) {
		return x > y + eps;
	}
	/**
	 * Returns whether x is greater than or equal to y	 	 
	 */
	final public static boolean isGreaterEqual(double x, double y) {
		return x + EPSILON > y;
	}

    final public static double convertToAngleValue(double val) {
		if (val > EPSILON && val < PI_2) return val;
		
    	double value = val % PI_2; 
		if (isZero(value)) {
			if (val < 1.0) value = 0.0;
			else value = PI_2; 
		}
    	else if (value < 0.0)  {
    		value += PI_2;
    	} 
    	return value;
    }

   public abstract LaTeXCache newLaTeXCache();

}
