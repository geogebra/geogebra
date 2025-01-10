package org.geogebra.common.util;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoFractionText;
import org.geogebra.common.util.debug.crashlytics.CrashlyticsLogger;

public class DoubleUtil {

	/**
	 * @param x
	 *            number
	 * @return whether fractional part of the number is zero within current
	 *         precision (false for +/-Infinity, NaN
	 */
	final public static boolean isInteger(double x) {
	
		if (Double.isInfinite(x) || Double.isNaN(x)) {
			return false;
		}
	
		if (x > 1E17 || x < -1E17) {
			return true;
		}
		return DoubleUtil.isEqual(x, Math.round(x));
	}

	/**
	 * Check difference is less than a constant
	 * 
	 * infinity == infinity returns true eg 1/0
	 * 
	 * -infinity == infinity returns false eg -1/0
	 * 
	 * -infinity == -infinity returns true
	 * 
	 * undefined == undefined returns false eg 0/0
	 * 
	 * @return whether x is equal to y
	 * 
	 * 
	 */
	final public static boolean isEqual(double a, double b) {
		if (a == b) {
			return true;
		}
		return ((a - Kernel.STANDARD_PRECISION) <= b)
				&& (b <= (a + Kernel.STANDARD_PRECISION));
	}

	/**
	 * 
	 * check if a point is zero, see #5202
	 * 
	 * @param e
	 *            epsilon
	 * @param x
	 *            point x
	 * @param y
	 *            point y
	 * @param z
	 *            point z
	 * 
	 * @return whether x, y, z are all zero
	 */
	final public static boolean isEpsilon(double e, double x, double y,
			double z) {
		double eAbs = Math.abs(e);
		return !(eAbs > Kernel.STANDARD_PRECISION || eAbs > Math.abs(x) * Kernel.STANDARD_PRECISION
				|| eAbs > Math.abs(y) * Kernel.STANDARD_PRECISION
				|| eAbs > Math.abs(z) * Kernel.STANDARD_PRECISION);
	}

	/**
	 * 
	 * check if a point is zero, see #5202
	 * 
	 * @param e
	 *            epsilon
	 * @param x
	 *            point x
	 * @param y
	 *            point y
	 * @return whether x and y are both zero
	 */
	final public static boolean isEpsilon(double e, double x, double y) {
		double eAbs = Math.abs(e);
		return !(eAbs > Kernel.STANDARD_PRECISION || eAbs > Math.abs(x) * Kernel.STANDARD_PRECISION
				|| eAbs > Math.abs(y) * Kernel.STANDARD_PRECISION);
	}

	/** @return is abs(val) &lt; STANDARD_PRECISION ? */
	public static boolean isZero(double val) {
		return (-Kernel.STANDARD_PRECISION < val) && (val < Kernel.STANDARD_PRECISION);
	}

	/** @return is abs(val) &lt; epsilon ? */
	public static boolean isZero(double val, double eps) {
		return (-eps < val) && (val < eps);
	}

	/**
	 * if x is nearly zero, 0.0 is returned, else x is returned
	 * 
	 * @param x
	 *            input
	 * @return 0.0 if x is nearly zero
	 */
	final public static double chop(double x) {
		if (isZero(x)) {
			return 0.0d;
		}
		return x;
	}

	/**
	 * @param x
	 *            first compared number
	 * @param y
	 *            second compared number
	 * @param eps
	 *            maximum difference
	 * @return whether the x-eps &lt; y &lt; x+eps
	 */
	final public static boolean isEqual(double x, double y, double eps) {
		return x == y || ((x - eps) < y) && (y < (x + eps));
	}

	/**
	 * Check difference is small, proportional to numbers
	 * @param x first number
	 * @param y second number
	 * @return x == y, with standard precision
	 */
	public static boolean isRatioEqualTo1(double x, double y) {
		return isRatioEqualTo1(x, y, Kernel.STANDARD_PRECISION);
	}

	/**
	 * Check difference is small, proportional to numbers
	 * @param x first number
	 * @param y second number
	 * @param precision precision to use when comparing
	 * @return x == y, with the given precision
	 */
	public static boolean isRatioEqualTo1(double x, double y, double precision) {
		if (x == y) {
			return true;
		}

		double eps = precision * Math.min(Math.abs(x), Math.abs(y));

		return ((x - eps) <= y) && (y <= (x + eps));
	}

	/**
	 * @param a
	 *            array of numbers
	 * @return whether all given numbers are zero within current precision
	 */
	final static boolean isZero(double[] a) {
		for (int i = 0; i < a.length; i++) {
			if (!isZero(a[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * check if e is zero in comparison to x
	 * 
	 * @param e
	 *            e
	 * @param x
	 *            x
	 * @return whether e is zero compared to x
	 */
	final public static boolean isEpsilonToX(double e, double x) {
		return Math.abs(e) < Math.abs(x) * Kernel.STANDARD_PRECISION;
	}

	/**
	 * Returns whether x is greater than y
	 * 
	 * @param x
	 *            first compared number
	 * @param y
	 *            second compared number
	 * @return x &gt; y + STANDARD_PRECISION
	 */
	final public static boolean isGreater(double x, double y) {
		return x > (y + Kernel.STANDARD_PRECISION);
	}

	/**
	 * 
	 * @param x
	 *            first value
	 * @param y
	 *            second value
	 * @return 0 if x ~ y ; -1 if x &lt; y ; 1 if x &gt; y
	 */
	final public static int compare(double x, double y) {
		if (isGreater(x, y)) {
			return 1;
		}
	
		if (isGreater(y, x)) {
			return -1;
		}
	
		return 0;
	}

	/**
	 * Returns whether x is greater than y
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param eps
	 *            tolerance
	 * @return true if x &gt; y + eps
	 */
	final public static boolean isGreater(double x, double y, double eps) {
		return x > (y + eps);
	}

	/**
	 * Returns whether x is greater than or equal to y
	 * 
	 * @param x
	 *            tested number
	 * @param y
	 *            upper bound
	 * @return x + standard precision &gt; y
	 */
	final public static boolean isGreaterEqual(double x, double y) {
		return (x + Kernel.STANDARD_PRECISION) > y;
	}

	/**
	 * 
	 * check if e is zero in comparison to eps and x
	 * 
	 * @param e
	 *            e
	 * @param x
	 *            x
	 * @param eps
	 *            precision
	 * @return whether e is zero compared to x and eps
	 */
	final public static boolean isEpsilonWithPrecision(double e, double x, double eps) {
		double eAbs = Math.abs(e);
		return !(eAbs > eps || eAbs > Math.abs(x) * eps);
	}

	/**
	 * check if e is zero in comparison to STANDARD_PRECISION and x
	 * 
	 * @param e
	 *            e
	 * @param x
	 *            x
	 * @return whether e is zero compared to STANDARD_PRECISION and x
	 */
	final public static boolean isEpsilon(double e, double x) {
		return isEpsilonWithPrecision(e, x, Kernel.STANDARD_PRECISION);
	}

	/**
	 * Checks if x is close (Kernel.MIN_PRECISION) to a decimal fraction, eg
	 * 2.800000000000001. If it is, the decimal fraction eg 2.8 is returned,
	 * otherwise x is returned.
	 * 
	 * @param val
	 *            input number
	 * @param precision
	 *            specifies how many decimals digits are accepted in results --
	 *            e.g. 0.001 to allow three digits
	 * @return input number; rounded with given precision if the rounding error
	 *         is less than this kernel's minimal precision
	 */
	
	public static double checkDecimalFraction(double val, double precision) {
		double prec = Math.pow(10,
				Math.floor(Math.log(Math.abs(precision)) / Math.log(10)));
	
		double fracVal = val * Kernel.INV_MIN_PRECISION;
		double roundVal = Math.round(fracVal);

		if (isEqual(fracVal, roundVal, Kernel.STANDARD_PRECISION * prec)) {
			return roundVal / Kernel.INV_MIN_PRECISION;
		}
		return val;
	}

	/**
	 * @param val
	 *            real number
	 * @return close decimal fraction or val if there is not one
	 */
	public static double checkDecimalFraction(double val) {
		double fracVal = val * Kernel.INV_MIN_PRECISION;
		double roundVal = Math.round(fracVal);

		if (isEqual(fracVal, roundVal, Kernel.STANDARD_PRECISION)) {
			return roundVal / Kernel.INV_MIN_PRECISION;
		}
		return val;
	}

	/**
	 * Checks if x is very close (1E-8) to an integer. If it is, the integer
	 * value is returned, otherwise x is returned.
	 * 
	 * @param x
	 *            real number
	 * @return x rounded to an integer if close
	 */
	final public static double checkInteger(double x) {
		double roundVal = Math.round(x);
		if (Math.abs(x - roundVal) < Kernel.STANDARD_PRECISION) {
			return roundVal;
		}
		return x;
	}

	/**
	 * 
	 * @param val
	 *            raw value
	 * @return angle
	 */
	final public static double convertToAngleValue(double val) {
		if ((val > Kernel.STANDARD_PRECISION) && (val < Kernel.PI_2)) {
			return val;
		}
	
		double value = val % Kernel.PI_2;
		if (isZero(value)) {
			if (val < 1.0) {
				value = 0.0;
			} else {
				value = Kernel.PI_2;
			}
		} else if (value < 0.0) {
			value += Kernel.PI_2;
		}
		return value;
	}

	/**
	 * 
	 * checks root like 0.29999998880325357 1) Check if there's a hole at 0.3 -&gt;
	 * return NaN 2) Check if 0.3 is a better root -&gt; return 0.3 3) otherwise return
	 * root
	 * 
	 * @param root potential root (of f) to check
	 * @param f    function with root
	 * @return root / better root / NaN
	 */
	public static double checkRoot(double root, UnivariateFunction f) {
		// change eg 0.29999998880325357 to 0.3
		// https://www.geogebra.org/m/bqn4esdr
		double root2 = DoubleUtil.checkDecimalFraction(root, 10000000);

		double rootVal = f.value(root);
		double root2Val = f.value(root2);

		// Log.debug("root " + root + " " + rootVal);
		// Log.debug("root2 " + root2 + " " + root2Val);

		if (!Double.isFinite(rootVal) || !Double.isFinite(root2Val)) {
			// hole near/at root
			return Double.NaN;
		}

		if (Math.abs(root2Val) < Math.abs(rootVal)) {
			// rounded root is more accurate -> use that
			return root2;
		}
		
		// now try slower check for eg 1/3
		double[] polishedRoot = AlgoFractionText.decimalToFraction(root, Kernel.STANDARD_PRECISION);
		if (polishedRoot[1] != 0 && Math.abs(polishedRoot[0]) < 999
				&& Math.abs(polishedRoot[1]) < 20) {
			root2 = polishedRoot[0] / polishedRoot[1];
			root2Val = f.value(root2);
			if (!Double.isFinite(root2Val)) {
				// hole near/at root
				return Double.NaN;
			}
			
			if (Math.abs(root2Val) < Math.abs(rootVal)) {
				// rounded root is more accurate -> use that
				return root2;
			}
		}

		// default: return original root
		return root;
	}

	/**
	 * 
	 * checks min value like 0.29999998880325357 Check if 0.3 is a better minimum -&gt;
	 * return 0.3 otherwise return root
	 * 
	 * @param root potential max (of f) to check
	 * @param f    function with root
	 * @return root / better min
	 */
	public static double checkMin(double root, UnivariateFunction f) {

		// change 12.34000000001 to 12.34
		double betterVal = DoubleUtil.checkDecimalFraction(root, 10000000);

		// check if betterVal is actually better
		if (f.value(betterVal) <= f.value(root)) {
			// use new one
			return betterVal;
		}

		// original value is better
		return root;

	}

	/**
	 * 
	 * checks max value like 0.29999998880325357 Check if 0.3 is a better maximum -&gt;
	 * return 0.3 otherwise return root
	 * 
	 * @param root potential max (of f) to check
	 * @param f    function with root
	 * @return root / better min
	 */
	public static double checkMax(double root, UnivariateFunction f) {

		// change 12.34000000001 to 12.34
		double betterVal = DoubleUtil.checkDecimalFraction(root, 10000000);

		// check if betterVal is actually better
		if (f.value(betterVal) >= f.value(root)) {
			// use new one
			return betterVal;
		}

		// original value is better
		return root;

	}

    /**
     *
     * @param x number
     * @return x rounded to 1/2/5 * 10^digits
     */
    final public static double round125(double x) {
        double pot = getPowerOfTen(x);
        int n = (int) (x / pot);
        if (n >= 5) {
            return 5 * pot;
        }
        if (n >= 2) {
            return 2 * pot;
        }
        return pot;
    }

    /**
     *
     * @param x number
     * @return 10^n where x = v * 10^n with 1 &lt;= v&lt; 10
     */
    final public static double getPowerOfTen(double x) {
        return Math.pow(10, (int) Math.floor(Math.log(x) / Math.log(10)));
    }

	/**
	 * Copy of Double.hashCode, used for Android 4.4 compatibility
	 * 
	 * @param val
	 *            value
	 * @return hash
	 */
	public static int hashCode(double val) {
		long bits = Double.doubleToLongBits(val);
		return (int) (bits ^ (bits >>> 32));
	}

	/**
	 * Create a range of doubles from min to max with the given step
	 * @param min minimum
	 * @param max &gt;= min
	 * @param step &gt; 0
	 * @return {min} if min &gt;= max,
	 *       {min, min + step, min + 2*step, ..., min + i*step, max}
	 *       otherwise, where min + i*step is the greatest such number
	 *       that is smaller than max
	 */
	public static double[] range(double min, double max, double step) {
		// To any future developer who wants to simplify this code:
		// please double-triple check what you are doing, floating
		// point numbers are _not_ easy to handle (APPS-158, APPS-1824)

		int length = (int) ((max - min) / step);
		if (min + length * step < max - Kernel.STANDARD_PRECISION) {
			length++;
		}
		if (length <= 0) {
			return new double[] {min};
		}

		CrashlyticsLogger.log("DoubleUtil.range called with min = " + min + ", max = " + max
				+ ", step = " + step + ". Array length = " + length + ".");

		double[] result = new double[length + 1];
		for (int i = 0; i < result.length; i++) {
			result[i] = checkDecimalFraction(min + i * step);
		}
		result[result.length - 1] = max;

		return result;
	}
}
