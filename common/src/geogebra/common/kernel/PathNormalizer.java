package geogebra.common.kernel;


/**
 * Normalized path that uses a path parameter in range [0,1].
 * 
 * @author Markus Hohenwarter
 */
public class PathNormalizer {

	

	

	/**
	 * Converts path parameter value tn from range [0, 1] to [min, max].
	 * 
	 * @param pn
	 *            parameter value in [0,1]
	 * @param min
	 *            of range [min, max]
	 * @param max
	 *            of range [min, max]
	 * @return parameter value in [min, max]
	 */
	public static double toParentPathParameter(double pn, double min, double max) {
		double tn=pn;
		// for Points as Paths (min=max=0)
		if (min == max)
			return min;

		if (tn < 0)
			tn = 0;
		else if (tn > 1)
			tn = 1;

		if (min == Double.NEGATIVE_INFINITY) {
			if (max == Double.POSITIVE_INFINITY) {
				// [0,1] -> (-infinite, +infinite)
				// first: (0,1) -> (-1,1), then use infFunction(-1 ... 1)
				return infFunction(2 * tn - 1);
			}

			// [0,1] -> (-infinite, max_param]
			// max_param + infFunction(-1 ... 0)
			return max + infFunction(tn - 1);
		}
		if (max == Double.POSITIVE_INFINITY) {
			// [0,1] -> [min_param, +infinite)
			// min_param + infFunction(0 ... 1)
			return min + infFunction(tn);
		}
		// [0,1] -> [min_param, max_param]
		return (1 - tn) * min + tn * max;
	}

	/**
	 * Converts path parameter value t from range [min, max] to [0, 1].
	 * 
	 * @param p
	 *            parameter to be normalized
	 * 
	 * @param min
	 *            of range [min, max]
	 * @param max
	 *            of range [min, max]
	 * @return parameter value in [0,1]
	 */
	public static double toNormalizedPathParameter(double p, double min,
			double max) {
		double t= p;
		// for Points as Paths (min=max=0)
		if (min == max) {
			return 0;
		}

		if (t < min) {
			t = min;
		} else if (t > max) {
			t = max;
		}

		if (min == Double.NEGATIVE_INFINITY) {
			if (max == Double.POSITIVE_INFINITY) {
				// (-infinite, +infinite) -> [0,1]
				if (t == Double.NEGATIVE_INFINITY)
					return 0;
				else if (t == Double.POSITIVE_INFINITY)
					return 1;
				else {
					// (-infinite, +infinite) -> (0,1)
					// solve for tn: t = infFunction(2*tn - 1);
					return 0.5 + 0.5 * inverseInfFunction(t);
				}
			}
			// (-infinite, max] -> [0,1]
			if (t == Double.NEGATIVE_INFINITY) {
				return 0;
			}
			// solve for tn: t = max + infFunction(tn - 1);
			return 1 + inverseInfFunction(t - max);
		}
		if (max == Double.POSITIVE_INFINITY) {
			// [min, +infinite) -> [0,1]
			// solve for tn: t = min + infFunction(tn)
			return inverseInfFunction(t - min);
		}
		// [min, max] -> [0,1]
		// solve for tn: t = (1 - tn) * min + tn * max;
		return (t - min) / (max - min);
	}



	/**
	 * Function t: (-1, 1) -> (-inf, +inf)
	 * 
	 * @param t parameter from (-1,1)
	 * @return parameter in (-1,1) to be mapped into all reals
	 */
	public static double infFunction(double t) {
		return t / (1 - Math.abs(t));
	}

	/**
	 * Function z: (-inf, +inf) -> (-1, 1)
	 * 
	 * @param z arbitrary parameter
	 * @return arbitrary parameter to be mapped into (-1,1)
	 */
	public static double inverseInfFunction(double z) {
		if (z >= 0) {
			return z / (1 + z);
		}
		return z / (1 - z);
	}

	







	

	/*
	 * TEST
	 */
	// public static void main(String[] args) {
	//
	// for (int i=0; i<10; i++) {
	// double t = 2*Math.random()-1;
	// System.out.println("t = " + t);
	//
	// double tn = toNormalizedPathParameter(t, Double.NEGATIVE_INFINITY,
	// Double.POSITIVE_INFINITY);
	// double t2= toParentPathParameter(tn, Double.NEGATIVE_INFINITY,
	// Double.POSITIVE_INFINITY);
	// System.out.println("\ttn = " + tn + ", error: " + (t2-t));
	//
	// tn = toNormalizedPathParameter(t, Double.NEGATIVE_INFINITY, 3);
	// t2= toParentPathParameter(tn, Double.NEGATIVE_INFINITY, 3);
	// System.out.println("\ttn = " + tn + ", error: " + (t2-t));
	//
	// tn = toNormalizedPathParameter(t, -7, Double.POSITIVE_INFINITY);
	// t2= toParentPathParameter(tn, -7, Double.POSITIVE_INFINITY);
	// System.out.println("\ttn = " + tn + ", error: " + (t2-t));
	//
	// tn = toNormalizedPathParameter(t, -5, 9);
	// t2= toParentPathParameter(tn, -5, 9);
	// System.out.println("\ttn = " + tn + ", error: " + (t2-t));
	// }
	//
	// }

}
