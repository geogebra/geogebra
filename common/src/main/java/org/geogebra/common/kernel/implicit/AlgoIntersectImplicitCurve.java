package org.geogebra.common.kernel.implicit;

import java.util.List;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoIntersect;
import org.geogebra.common.kernel.algos.AlgoRoots;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.plugin.Operation;

/**
 * Algorithm to find intersection of implicit with line, function, conic and
 * other implicit curve
 * 
 * @author GSoCImplicitCurve2015
 *
 */
public class AlgoIntersectImplicitCurve extends AlgoIntersect {
	
	/**
	 * Default sampling interval
	 */
	public static final int SAMPLE_SIZE_2D = 1521;

	/**
	 * Default size of output
	 */
	public static final int OUTPUT_SIZE = 100;
	/**
	 * Sample size
	 */
	private static final int SAMPLE_SIZE = 100;

	/**
	 * Root Precision
	 */
	private static final double EPS = Kernel.MIN_PRECISION;

	/**
	 * First Equation {@link GeoImplicitCurve}
	 */
	private GeoImplicitCurve curve;
	/**
	 * Second Equation: Line, GeoConic, Function, Implicit Curve
	 */
	private GeoElement equation;
	/**
	 * Type of equation
	 */
	private EquationType equationType;
	/**
	 * output labels
	 */
	@SuppressWarnings("unused")
	private String[] labels;
	/**
	 * Solutions of the equations
	 */
	private OutputHandler<GeoPoint> outputs;
	/**
	 * Point counts
	 */
	private int outputLen;
	/**
	 * Find the intersection between {@link GeoImplicitCurve} and line y = 0,
	 * i.e. find roots of the {@link GeoImplicitCurve}
	 * 
	 * @param cons
	 *            {@link Construction}
	 * @param labels
	 *            Labels
	 * @param curve
	 *            {@link GeoImplicitCurve}
	 */
	public AlgoIntersectImplicitCurve(Construction cons, String[] labels,
			GeoImplicitCurve curve) {
		this(cons, labels, curve, null, EquationType.ROOT);
	}

	/**
	 * Find intersection between {@link GeoImplicitCurve} and {@link GeoLine}
	 * 
	 * @param cons
	 *            {@link Construction}
	 * @param labels
	 *            labels
	 * @param curve
	 *            implicit curve
	 * @param line
	 *            line
	 */
	public AlgoIntersectImplicitCurve(Construction cons, String[] labels,
			GeoImplicitCurve curve, GeoLine line) {
		this(cons, labels, curve, line, EquationType.LINE);
	}

	/**
	 * Find intersection between {@link GeoImplicitCurve} and {@link GeoConic}
	 * 
	 * @param cons
	 *            {@link Construction}
	 * @param labels
	 *            labels
	 * @param curve
	 *            curve
	 * @param conic
	 *            {@link GeoConic}
	 */
	public AlgoIntersectImplicitCurve(Construction cons, String[] labels,
			GeoImplicitCurve curve, GeoConic conic) {
		this(cons, labels, curve, conic, EquationType.CONIC);
	}

	/**
	 * Find intersections between {@link GeoImplicitCurve} and
	 * {@link GeoFunction}
	 * 
	 * @param cons
	 *            {@link Construction}
	 * @param labels
	 *            Labels
	 * @param curve
	 *            {@link GeoImplicitCurve}
	 * @param func
	 *            {@link GeoFunction}
	 */
	public AlgoIntersectImplicitCurve(Construction cons, String[] labels,
			GeoImplicitCurve curve, GeoFunction func) {
		this(cons, labels, curve, func, EquationType.FUNCTION);
	}

	/**
	 * Find intersections between two {@link GeoImplicitCurve}
	 * 
	 * @param cons
	 *            {@link Construction}
	 * @param labels
	 *            Labels
	 * @param curve
	 *            {@link GeoImplicitCurve}
	 * @param impCurve
	 *            {@link GeoImplicitCurve}
	 */
	public AlgoIntersectImplicitCurve(Construction cons, String[] labels,
			GeoImplicitCurve curve, GeoImplicitCurve impCurve) {
		this(cons, labels, curve, impCurve, EquationType.IMPLICIT_CURVE);
	}

	private AlgoIntersectImplicitCurve(Construction cons, String[] labels,
			GeoImplicitCurve curve, GeoElement eqn, EquationType equationType) {

		super(cons);

		this.equationType = equationType;
		this.equation = eqn;
		this.curve = curve;
		this.labels = labels;

		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = curve;
		input[1] = equation;

		outputs = new OutputHandler<GeoPoint>(new elementFactory<GeoPoint>() {
			public GeoPoint newElement() {
				GeoPoint p = new GeoPoint(cons);
				p.setParentAlgorithm(AlgoIntersectImplicitCurve.this);
				return p;
			}
		});

		setDependencies();
	}

	@Override
	public void compute() {

		if (curve == null || !curve.isDefined()) {
			outputs.adjustOutputSize(0);
			return;
		}

		if (equationType != EquationType.ROOT
				&& (equation == null || !equation.isDefined())) {
			outputs.adjustOutputSize(0);
			return;
		}

		this.outputLen = 0;

		switch (equationType) {
		case LINE:
			GeoLine l = (GeoLine) equation;
			intersectLine(l.getX(), l.getY(), l.getZ());
			break;
		case FUNCTION:
			GeoFunction f = (GeoFunction) equation;
			intersect(curve.getExpression(),
					f.getFunctionExpression().getCopy(kernel), true);
			break;
		case ROOT:
			intersectLineY(0);
			break;
		case CONIC:
			intersectConic((GeoConic) equation);
			break;
		case IMPLICIT_CURVE:
			intersectCurves();
			break;
		}

		if (this.outputLen == 0) {
			outputs.adjustOutputSize(0);
		}
	}

	private void intersectCurves() {
		double[][] roots = findIntersections(curve,
				(GeoImplicitCurve) equation, SAMPLE_SIZE_2D, OUTPUT_SIZE);
		if (roots == null || roots.length == 0) {
			return;
		}
		outputLen = roots.length;
		outputs.adjustOutputSize(outputLen);
		for (int i = 0; i < outputLen; i++) {
			outputs.getElement(i).setCoords(roots[i][0], roots[i][1], 1.0);
		}
	}

	private void intersectLine(double a, double b, double c) {
		if (a == 0 && b == 0) {
			return;
		} else if (a == 0) {
			intersectLineY(-c / b);
			return;
		} else if (b == 0) {
			intersectLineX(-c / a);
			return;
		} else {
			FunctionVariable xVar = new FunctionVariable(kernel, "x");
			ExpressionNode exp = new ExpressionNode(kernel, xVar,
					Operation.MULTIPLY, new MyDouble(kernel, -a / b));
			intersect(curve.getExpression(), exp.subtract(c / b), true);
		}
	}

	private void intersectLineX(double c) {
		ExpressionNode exp = new ExpressionNode(kernel, c);
		intersect(curve.getExpression(), exp, false);
	}

	private void intersectLineY(double c) {
		ExpressionNode exp = new ExpressionNode(kernel, c);
		intersect(curve.getExpression(), exp, true);
	}

	private void intersectConic(GeoConic conic) {
		if (conic.isDegenerate() && conic.isLineConic()) {
			GeoLine[] l = conic.lines;
			for (int i = 0; i < l.length; i++) {
				if (l[i] != null && l[i].isDefined()) {
					intersectLine(l[i].getX(), l[i].getY(), l[i].getZ());
				}
			}
			return;
		} else if (conic.isDegenerate()) {
			GeoPoint pt = conic.getSinglePoint();
			if (pt != null && pt.isDefined() && curve.isOnPath(pt)) {
				outputLen++;
				outputs.adjustOutputSize(outputLen);
				outputs.getElement(outputLen - 1).setCoordsFromPoint(pt);
			}
			return;
		}
		double[] m = conic.getMatrix();

		for (int i = 3; i < 6; i++) {
			m[i] *= 2;
		}
		FunctionVariable x = new FunctionVariable(kernel, "x");
		ExpressionNode num = new ExpressionNode(kernel, x);
		ExpressionNode den = new ExpressionNode(kernel, x);
		if (m[0] == 0.0 && m[1] == 0.0) {
			den = den.multiply(m[3]).plus(m[5]);
			num = num.multiply(m[4]).plus(m[2]);
			num = num.multiply(-1.0).divide(den);
			intersect(curve.getExpression(), num, false);
			return;
		} else if (m[0] == 0.0) {
			num = num.multiply(m[1]).plus(m[5]).multiply(x).plus(m[2]);
			den = den.multiply(m[3]).plus(m[4]);
			num = num.multiply(-1.0).divide(den);
			intersect(curve.getExpression(), num, false);
		} else if (m[1] == 0.0) {
			num = num.multiply(m[0]).plus(m[4]).multiply(x).plus(m[2]);
			den = den.multiply(m[3]).plus(m[5]);
			num = num.multiply(-1.0).divide(den);
			intersect(curve.getExpression(), num, false);
		} else {
			GeoImplicitCurve curve1 = new GeoImplicitCurve(cons);
			conic.toGeoImplicitCurve(curve1);
			equation = curve1;
			intersectCurves();
			equation = conic;
		}
	}

	private void intersect(FunctionNVar func, ExpressionNode repl, boolean replY) {
		if (!replY) {
			intersectRepX(func, repl);
			return;
		}

		FunctionVariable y = func.getFunctionVariables()[1];
		ExpressionNode exp = func.getExpression().getCopy(kernel);

		exp.replace(y, repl);
		exp.simplifyConstantIntegers();

		Function fn = new Function(exp);

		double[] roots = AlgoRoots.findRoots(new GeoFunction(cons, fn),
				kernel.getViewsXMin(curve), kernel.getViewsXMax(curve),
				SAMPLE_SIZE);

		if (roots == null || roots.length == 0) {
			return;
		}

		fn = new Function(repl);
		fn.initFunction();

		int n = outputLen;
		outputLen += roots.length;
		outputs.adjustOutputSize(outputLen);

		for (int i = 0; i < roots.length; i++) {
			double py = fn.evaluate(roots[i]);
			outputs.getElement(n + i).setCoords(roots[i], py, 1.0);
		}
	}

	private void intersectRepX(FunctionNVar func, ExpressionNode repl) {

		FunctionVariable x = func.getFunctionVariables()[0];
		FunctionVariable y = func.getFunctionVariables()[1];

		ExpressionNode exp = func.getExpression().getCopy(kernel);

		exp.replace(x, repl);
		exp.replace(y, x);
		exp.simplifyConstantIntegers();

		Function fn = new Function(exp);

		double[] roots = AlgoRoots.findRoots(new GeoFunction(cons, fn),
				kernel.getViewsYMin(curve), kernel.getViewsYMax(curve),
				SAMPLE_SIZE);

		if (roots == null || roots.length == 0) {
			return;
		}

		fn = new Function(repl);
		fn.initFunction();

		int n = outputLen;
		outputLen += roots.length;
		outputs.adjustOutputSize(outputLen);

		for (int i = 0; i < roots.length; i++) {
			double px = fn.evaluate(roots[i]);
			outputs.getElement(n + i).setCoords(px, roots[i], 1.0);
		}
	}

	/**
	 * @param c1
	 *            first curve
	 * @param c2
	 *            second curve
	 * @param samples
	 *            maximum number of samples for initial points
	 * @param outputs
	 *            maximum size of output
	 * @return points at which two implicit curves intersects
	 */
	public static double[][] findIntersections(GeoImplicitCurve c1,
			GeoImplicitCurve c2, int samples, int outputs) {

		double[] b1 = c1.getKernel().getViewBoundsForGeo(c1);
		double[] b2 = c2.getKernel().getViewBoundsForGeo(c2);

		double xMin = Math.max(b1[0], b2[0]);
		double yMin = Math.max(b1[2], b2[2]);
		double xMax = Math.min(b1[1], b2[1]);
		double yMax = Math.min(b1[3], b2[3]);

		double[] params = new double[] { xMin, yMin, xMax, yMax };

		List<Coords> guess = GeoImplicitCurve.probableInitialPoints(c1, c2, samples);

		if (c1.hasDerivative() && c2.hasDerivative()) {
			FunctionNVar[] f = new FunctionNVar[6];

			f[0] = c1.getExpression();
			f[1] = c2.getExpression();

			f[2] = c1.getDerivativeX();
			f[3] = c1.getDerivativeY();
			f[4] = c2.getDerivativeX();
			f[5] = c2.getDerivativeY();
			return intersections(f, params, guess, outputs);
		}

		FunctionNVar fn1 = c1.getExpression();
		FunctionNVar fn2 = c2.getExpression();

		return intersects(fn1, fn2, params, guess, outputs);
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
	 * @return points at which two functions intersect. The output size is
	 *         bounded above by parameter samples.
	 * 
	 */
	public static double[][] findIntersections(FunctionNVar fun1,
			FunctionNVar fun2, double xMin, double yMin, double xMax,
			double yMax, int samples, int outputs) {

		double[] params = new double[] { xMin, yMin, xMax, yMax };

		List<Coords> guess = GeoImplicitCurve.probableInitialPoints(fun1, fun2, params, samples);
		boolean derivative = false;
		try {
			FunctionVariable x = fun1.getFunctionVariables()[0];
			FunctionVariable y = fun1.getFunctionVariables()[1];
			FunctionNVar[] f = new FunctionNVar[6];
			f[0] = fun1;
			f[1] = fun2;
			f[2] = fun1.getDerivativeNoCAS(x, 1);
			f[3] = fun1.getDerivativeNoCAS(y, 1);
			f[4] = fun2.getDerivativeNoCAS(x, 1);
			f[5] = fun2.getDerivativeNoCAS(y, 1);
			derivative = true;
			return intersections(f, params, guess, outputs);
		} catch (Exception ex) {
			if (derivative) {
				// App.debug("Derivative exists, but failed to find intersection using Newton's method");
				return null;
			}
			// App.debug("Some functions are not differentiable");
			// App.debug("Trying to find intersections using Broyden's method");
			return intersects(fun1, fun2, params, guess, outputs);
		}
	}

	/**
	 * Damped newton's method with Armijo's line search
	 * 
	 * @param f
	 *            {f1(x,y), f2(x,y), f1'(x), f1'(y), f2'(x), f2'(y)}
	 * @param params
	 *            {xMin, yMin, xMax, yMax}
	 * @param outputs
	 *            number of samples in output
	 * @return
	 */
	private static double[][] intersections(FunctionNVar[] f, double[] params,
			List<Coords> guess, int outputs) {

		double[][] out = new double[outputs][2];

		double f1, f2, jx1, jx2, jy1, jy2, det, x, y;
		double delta1, delta2, lamda, dx, dy, evals[];

		boolean add = true;

		// papers suggest that Newton's method converges in at most 2n
		// steps, n being number of variables
		int maxStep = 8, n = 0;
		for (int i = 0; i < guess.size() && n < outputs; i++) {
			evals = guess.get(i).val;
			
			if (!MyDouble.isFinite(evals[0]) || !MyDouble.isFinite(evals[1])) {
				continue;
			}
			
			f1 = f[0].evaluate(evals);
			f2 = f[1].evaluate(evals);
			// More efficient but less accurate way to find sqrt(f1^2+f2^2)
			delta1 = Math.abs(f1) + Math.abs(f2);
			
			for (int j = 0; j < maxStep && !Kernel.isZero(delta1, EPS); j++) {
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
				if (Kernel.isZero(det)) {
					break;
				}

				// find deviation
				dx = (jy1 * f2 - jy2 * f1) / det;
				dy = (jx2 * f1 - jx1 * f2) / det;
				lamda = 1.0;
				// Armijo line search
				do {
					evals[0] = x + lamda * dx;
					evals[1] = y + lamda * dy;
					f1 = f[0].evaluate(evals);
					f2 = f[1].evaluate(evals);
					delta2 = Math.abs(f1) + Math.abs(f2);
					lamda *= 0.5;
				} while ((lamda > 0.05) && (delta2 > delta1));

				if (delta2 > delta1) {
					// the function in not converging even for lamda ~ 0.5
					break;
				}
				
				delta1 = delta2;
			}

			if (!Kernel.isZero(delta1, EPS)) {
				// unfortunately our guess was very bad, repeat with other guess
				continue;
			}
			
			// check whether root is within view bound
			add = (evals[0] >= params[0]) && (evals[0] <= params[2])
					&& (evals[1] >= params[1] && evals[1] <= params[3]);

			// check if we have already calculated the same root
			for (int j = 0; j < n && add; j++) {
				add = !Kernel.isEqual(out[j][0], evals[0], EPS)
						|| !Kernel.isEqual(out[j][1], evals[1], EPS);
			}

			if(add) {
				out[n][0] = evals[0];
				out[n][1] = evals[1];
				n++;
			}
		}
		double[][] out1 = new double[n][2];
		System.arraycopy(out, 0, out1, 0, n);

		return out1;
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
	 * @return intersection between functions
	 */
	public static double[][] intersects(final FunctionNVar fn1,
			final FunctionNVar fn2, double[] params, List<Coords> guess,
			final int outputs) {

		boolean add;

		double[] evals;
		double[][] out = new double[outputs][2];
		double jx1, jy1, jx2, jy2, delta1, delta2, det, Dx, Dy, dx, dy;
		double x, y, f1, f2, fPrev1, fPrev2, df1, df2, lamda, norm;

		int steps = 10, size = guess.size(), n = 0;

		for (int i = 0; i < size; i++) {
			evals = guess.get(i).val;

			if (!MyDouble.isFinite(evals[0]) || !MyDouble.isFinite(evals[1])) {
				continue;
			}

			f1 = fn1.evaluate(evals);
			f2 = fn2.evaluate(evals);

			delta1 = Math.abs(f1) + Math.abs(f2);

			if (!Kernel.isZero(delta1, EPS)) {

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

					if (Kernel.isZero(det)) {
						break;
					}

					dx = (jy1 * f2 - jy2 * f1) / det;
					dy = (jx2 * f1 - jx1 * f2) / det;
					lamda = 1.0;

					do {

						evals[0] = x + lamda * dx;
						evals[1] = y + lamda * dy;

						f1 = fn1.evaluate(evals);
						f2 = fn2.evaluate(evals);

						delta2 = Math.abs(f1) + Math.abs(f2);

						lamda *= 0.5;

					} while (delta2 >= delta1 && lamda > 0.005);

					if (delta2 >= delta1 || Kernel.isZero(delta2, EPS)) {
						delta1 = delta2;
						break;
					}

					delta1 = delta2;

					df1 = f1 - fPrev1;
					df2 = f2 - fPrev2;

					dx = evals[0] - x;
					dy = evals[1] - y;

					norm = dx * dx + dy * dy;

					if (Kernel.isZero(norm)) {
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

			if (!Kernel.isZero(delta1, EPS)) {
				// unfortunately our guess was very bad, repeat with other guess
				continue;
			}

			// check whether root is within view bound
			add = (evals[0] >= params[0]) && (evals[0] <= params[2])
					&& (evals[1] >= params[1] && evals[1] <= params[3]);

			// check if we have already calculated the same root
			for (int j = 0; j < n && add; j++) {
				add = !Kernel.isEqual(out[j][0], evals[0], EPS)
						|| !Kernel.isEqual(out[j][1], evals[1], EPS);
			}

			if (add) {
				out[n][0] = evals[0];
				out[n][1] = evals[1];
				n++;
			}
		}

		double[][] out1 = new double[n][2];
		System.arraycopy(out, 0, out1, 0, n);

		return out1;
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

	@Override
	public GeoPoint[] getIntersectionPoints() {
		return outputs.getOutput(new GeoPoint[outputs.size()]);
	}

	@Override
	protected GeoPoint[] getLastDefinedIntersectionPoints() {
		return getIntersectionPoints();
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Intersect;
	}

	/**
	 * Set output labels
	 * 
	 * @param labels
	 *            labels
	 */
	public void setLabels(String[] labels) {
		outputs.setLabels(labels);
		update();
	}

	private static enum EquationType {
		ROOT, LINE, CONIC, FUNCTION, IMPLICIT_CURVE;
	}
}
