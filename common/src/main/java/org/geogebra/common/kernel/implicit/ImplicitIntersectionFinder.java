package org.geogebra.common.kernel.implicit;

import java.util.List;
import java.util.ListIterator;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Collection of static methods to find intersection of implicit with line, function, conic and
 * other implicit curve
 * 
 * @author GSoCImplicitCurve2015
 *
 */
public final class ImplicitIntersectionFinder {

	/**
	 * Default sampling interval
	 */
	public static final int SAMPLE_SIZE_2D = 1521;

	/**
	 * Default size of output
	 */
	public static final int OUTPUT_SIZE = 100;
	/**
	 * Root Precision
	 */
	private static final double EPS = Kernel.STANDARD_PRECISION_SQRT;

	// FIXME 0.1 and -0.1 both are different of root of x^10 = 0 with satisfied
	// standard precision accuracy and root accuracy condition
	private static final double ROOT_ACCURACY = 1e-3;

	/**
	 * We want a very accurate solution
	 */
	private static final double ACCURACY = Kernel.STANDARD_PRECISION;

	private static final double DECAY_RATE = 0.9;

	private static final double MOMENT_RATE = 0.92;

	private static final double MIN_LAMBDA = 0.0001;
	
	private ImplicitIntersectionFinder() {
		// utility class
	}

	/**
	 * 
	 * @param fun1
	 *            first function
	 * @param fun2
	 *            second function
	 * @param xMin
	 *            maximum x value
	 * @param yMin
	 *            minimum y value
	 * @param xMax
	 *            minimum x value
	 * @param yMax
	 *            maximum y value
	 * @param samples
	 *            maximum number of samples for initial points
	 * @param outputs
	 *            maximum size of output
	 * @param vals
	 *            points at which two functions intersect. The output size is
	 *            bounded above by parameter samples.
	 * 
	 */
	public static void findIntersections(FunctionNVar fun1, FunctionNVar fun2,
			double xMin, double yMin, double xMax, double yMax, int samples,
			int outputs, List<double[]> vals) {

		double[] params = new double[] { xMin, yMin, xMax, yMax };

		List<Coords> guess = GeoImplicitCurve.probableInitialPoints(fun1, fun2,
				params, samples);
		boolean derivative = false;
		try {
			FunctionVariable x = fun1.getFunctionVariables()[0];
			FunctionVariable y = fun1.getFunctionVariables()[1];
			FunctionVariable x2 = fun2.getFunctionVariables()[0];
			FunctionVariable y2 = fun2.getFunctionVariables()[1];
			FunctionNVar[] f = new FunctionNVar[6];
			f[0] = fun1;
			f[1] = fun2;
			f[2] = fun1.getDerivativeNoCAS(x, 1);
			f[3] = fun1.getDerivativeNoCAS(y, 1);
			f[4] = fun2.getDerivativeNoCAS(x2, 1);
			f[5] = fun2.getDerivativeNoCAS(y2, 1);
			derivative = true;
			intersections(f, params, guess, outputs, vals);
			return;
		} catch (Exception ex) {
			Log.debug(ex);
			if (derivative) {
				// Log.debug("Derivative exists, but failed to find intersection
				// using Newton's method");
				return;
			}
			// Log.debug("Some functions are not differentiable");
			// Log.debug("Trying to find intersections using Broyden's method");
			intersects(fun1, fun2, params, guess, outputs, vals);
		}
	}

	/**
	 * Damped newton's method with Armijo's line search
	 * 
	 * @param f
	 *            {f1(x,y), f2(x,y), f1'(x), f1'(y), f2'(x), f2'(y)}
	 * @param params
	 *            {xMin, yMin, xMax, yMax}
	 * @param guess
	 *            rough coordinates
	 * @param outputs
	 *            number of samples in output
	 * @param vals
	 *            output array
	 */
	static void intersections(FunctionNVar[] f, double[] params,
			List<Coords> guess, int outputs, List<double[]> vals) {
		double f1, f2, jx1, jx2, jy1, jy2, det, x, y;
		double delta1, delta2, lambda = 1.0, dx = 0.0, dy = 0.0;
		double[] evals;
		double moment;
		boolean add = true;

		// papers suggest that Newton's method converges in at most 2n
		// steps for linear equation, n being number of variables

		int maxStep = 12, minStep = 4, smooth, n = 0;
		for (int i = 0; i < guess.size() && n < outputs; i++) {
			evals = guess.get(i).val;

			if (!MyDouble.isFinite(evals[0]) || !MyDouble.isFinite(evals[1])) {
				continue;
			}

			f1 = f[0].evaluate(evals);
			f2 = f[1].evaluate(evals);
			// More efficient but less accurate way to find sqrt(f1^2+f2^2)
			smooth = 0;
			delta1 = Math.abs(f1) + Math.abs(f2);
			for (int j = 0; j < maxStep && smooth < minStep; j++) {
				x = evals[0];
				y = evals[1];
				// evaluate Jacobians
				jx1 = f[2].evaluate(evals);
				jy1 = f[3].evaluate(evals);
				jx2 = f[4].evaluate(evals);
				jy2 = f[5].evaluate(evals);

				// determinant
				det = jx1 * jy2 - jx2 * jy1;

				// check singularity
				if (DoubleUtil.isZero(det)) {
					break;
				}

				// find deviation
				dx = (jy1 * f2 - jy2 * f1) / det;
				dy = (jx2 * f1 - jx1 * f2) / det;
				lambda = 1.0;
				moment = 1.0;
				// Armijo line search with some simple tweaks
				do {
					evals[0] = x + lambda * dx;
					evals[1] = y + lambda * dy;
					f1 = f[0].evaluate(evals);
					f2 = f[1].evaluate(evals);
					delta2 = Math.abs(f1) + Math.abs(f2);
					lambda *= moment * DECAY_RATE;
					moment *= MOMENT_RATE;
				} while ((lambda > MIN_LAMBDA) && (delta2 > delta1));

				if (delta2 > delta1) {
					// the function in not converging even for lambda ~ 0.0
					break;
				}
				delta1 = delta2;
				if (DoubleUtil.isZero(delta1, ACCURACY)) {
					smooth++;
				}
			}

			if (!DoubleUtil.isZero(delta1, ACCURACY)) {
				// unfortunately our guess was very bad, repeat with other guess
				continue;
			}

			// check whether root is within view bound
			add = (evals[0] >= params[0]) && (evals[0] <= params[2])
					&& (evals[1] >= params[1] && evals[1] <= params[3]);

			// check if we have already calculated the same root
			if (add) {
				insert(new double[] { evals[0], evals[1] }, vals);
			}
		}
	}

	/**
	 * @param pair
	 *            intersection to be inserted
	 * @param pairs
	 *            ordered intersection
	 */
	static void insert(double[] pair, List<double[]> pairs) {
		ListIterator<double[]> it = pairs.listIterator();
		double eps = ROOT_ACCURACY; // find good value...
		while (it.hasNext()) {
			double[] p = it.next();
			if (DoubleUtil.isGreater(p[0], pair[0], eps)) {
				it.previous();
				break;
			}
			if (DoubleUtil.isEqual(p[0], pair[0], eps)) {
				if (DoubleUtil.isGreater(p[1], pair[1], eps)) {
					it.previous();
					break;
				}
				if (DoubleUtil.isEqual(p[1], pair[1], eps)) {
					return; // do not add
				}
			}
		}
		it.add(pair);
	}

	/**
	 * Find the intersections between two curves using Broyden's method
	 * 
	 * @param fn1
	 *            first function
	 * @param fn2
	 *            second function
	 * @param params
	 *            {xMin, yMin, xMax, yMax}
	 * @param outputs
	 *            number of samples in output
	 * @param guess
	 *            initial guesses
	 * @param vals
	 *            intersection between functions
	 */
	public static void intersects(final FunctionNVar fn1,
			final FunctionNVar fn2, double[] params, List<Coords> guess,
			final int outputs, List<double[]> vals) {

		boolean add;

		double[] evals;
		double jx1, jy1, jx2, jy2, delta1, delta2, det, Dx, Dy, dx, dy;
		double x, y, f1, f2, fPrev1, fPrev2, df1, df2, lambda, norm, moment;

		int steps = 10, size = guess.size();

		for (int i = 0; i < size; i++) {
			evals = guess.get(i).val;

			if (!MyDouble.isFinite(evals[0]) || !MyDouble.isFinite(evals[1])) {
				continue;
			}

			f1 = fn1.evaluate(evals);
			f2 = fn2.evaluate(evals);

			delta1 = Math.abs(f1) + Math.abs(f2);

			if (!DoubleUtil.isZero(delta1, ACCURACY)) {

				x = evals[0];
				y = evals[1];

				jx1 = finiteDiffX(fn1, x, y);
				jy1 = finiteDiffY(fn1, x, y);
				jx2 = finiteDiffX(fn2, x, y);
				jy2 = finiteDiffY(fn2, x, y);

				for (int j = 0; j < steps; j++) {

					fPrev1 = f1;
					fPrev2 = f2;

					det = jx1 * jy2 - jx2 * jy1;

					if (DoubleUtil.isZero(det)) {
						break;
					}

					dx = (jy1 * f2 - jy2 * f1) / det;
					dy = (jx2 * f1 - jx1 * f2) / det;
					lambda = 1.0;
					moment = 1.0;

					do {

						evals[0] = x + lambda * dx;
						evals[1] = y + lambda * dy;

						f1 = fn1.evaluate(evals);
						f2 = fn2.evaluate(evals);

						delta2 = Math.abs(f1) + Math.abs(f2);

						lambda *= moment * DECAY_RATE;
						moment *= MOMENT_RATE;

					} while (delta2 >= delta1 && lambda > MIN_LAMBDA);

					if (delta2 >= delta1 || DoubleUtil.isZero(delta2, ACCURACY)) {
						delta1 = delta2;
						break;
					}

					delta1 = delta2;

					df1 = f1 - fPrev1;
					df2 = f2 - fPrev2;

					dx = evals[0] - x;
					dy = evals[1] - y;

					norm = dx * dx + dy * dy;

					if (DoubleUtil.isZero(norm)) {
						break;
					}

					Dx = (df1 - dx * jx1 - dy * jy1) / norm;
					Dy = (df2 - dx * jx2 - dy * jy2) / norm;

					jx1 = jx1 + dx * Dx;
					jy1 = jy1 + dy * Dx;
					jx2 = jx2 + dx * Dy;
					jy2 = jy2 + dy * Dy;

					x = evals[0];
					y = evals[1];
				}
			}

			if (!DoubleUtil.isZero(delta1, ACCURACY)) {
				// unfortunately our guess was very bad, repeat with other guess
				continue;
			}

			// check whether root is within view bound
			add = (evals[0] >= params[0]) && (evals[0] <= params[2])
					&& (evals[1] >= params[1] && evals[1] <= params[3]);

			if (add) {
				insert(new double[] { evals[0], evals[1] }, vals);
			}
		}
	}

	private static double finiteDiffX(FunctionNVar func, double x, double y) {
		double[] eval = { x - EPS, y };
		double left, right;
		left = func.evaluate(eval);
		eval[0] = x + EPS;
		right = func.evaluate(eval);
		return (right - left) / (2 * EPS);
	}

	private static double finiteDiffY(FunctionNVar func, double x, double y) {
		double[] eval = { x, y - EPS };
		double left, right;
		left = func.evaluate(eval);
		eval[1] = y + EPS;
		right = func.evaluate(eval);
		return (right - left) / (2 * EPS);
	}
}
