/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDistancePointLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationSolver;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;

/**
 *
 * @author Markus
 */
public class AlgoDistancePointObject extends AlgoElement
		implements DistanceAlgo {

	private static final double MAX_INTERVAL = 10000;
	private static final double MIN_INTERVAL = 200;
	private static final int FINE_TUNE_STEPS = 15;

	private GeoPointND P; // input
	private GeoElementND g; // input
	private GeoNumeric dist; // output
	private AlgoClosestPoint closePt;

	/**
	 * Distance between point and object.
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param P
	 *            point
	 * @param g
	 *            object
	 */
	public AlgoDistancePointObject(Construction cons, String label,
			GeoPointND P, GeoElementND g) {
		super(cons);
		this.P = P;
		this.g = g;
		dist = new GeoNumeric(cons);
		closePt = getKernel().getAlgoDispatcher().getNewAlgoClosestPoint(cons,
				(Path) g, P);
		cons.removeFromConstructionList(closePt);
		setInputOutput(); // for AlgoElement

		// compute length
		compute();
		dist.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Distance;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_DISTANCE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) P;
		input[1] = g.toGeoElement();

		setOutputLength(1);
		setOutput(0, dist);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public GeoNumeric getDistance() {
		return dist;
	}

	GeoPointND getP() {
		return P;
	}

	GeoElementND getg() {
		return g;
	}

	// calc length of vector v
	@Override
	public final void compute() {
		if (closePt != null) {
			dist.setValue(closePt.getP().distance(P));
		} else {
			dist.setValue(g.distance(P));
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("DistanceOfAandB",
				"Distance between %0 and %1", P.getLabel(tpl),
				g.getLabel(tpl));
	}

	/**
	 * Other classes are invited to use this method.
	 * 
	 * @param function
	 *            Function
	 * @param x
	 *            x-coord of point
	 * @param y
	 *            y-coord of point
	 * @return val such as the point (val, function(val)) is closest to point
	 *         (x, y)
	 */
	public static final double getClosestFunctionValueToPoint(Function function,
			double x, double y) {
		// Algorithm inspired by
		// http://bact.mathcircles.org/files/Winter2011/CM2_Posters/TPham_BACTPoster.pdf
		Kernel kernel = function.getKernel();
		PolyFunction polyFunction = function
				.expandToPolyFunction(function.getExpression(), false, true);
		if (polyFunction != null) {
			return closestValPoly(polyFunction, x, y, kernel);
		}
		if (!Double.isFinite(function.value(x))) {
			double xLeft = getClosestDefined(function, x, y, -1);
			double xRight = getClosestDefined(function, x, y, 1);
			if (MyMath.distanceSquaredToFunctionAt(function, x, y, xLeft)
				< MyMath.distanceSquaredToFunctionAt(function, x, y, xRight)) {
					return xLeft;
			} else {
				return xRight;
			}
		}
		// non polynomial case
		FunctionVariable fVar = function.getFunctionVariable();
		Function deriv = function.getDerivative(1, true);
		// replace derivatives' function variable with functions'
		// we need this, so our new function created below, can be evaluated
		deriv.traverse(Traversing.Replacer
				.getReplacer(deriv.getFunctionVariable(), fVar));
		// build expression 2*(x - a) + 2(f(x) - b)f'(x) where a and b are the
		// coordinates of point
		ExpressionNode expr = new ExpressionNode(kernel, fVar, Operation.MINUS,
				new MyDouble(kernel, x));
		expr = expr.multiply(2);
		ExpressionNode expr2 = new ExpressionNode(kernel,
				function.getExpression(), Operation.MINUS,
				new MyDouble(kernel, y));
		expr2 = expr2.multiplyR(deriv.getExpression());
		expr2 = expr2.multiply(2);
		expr = expr.plus(expr2);
		Function func = new Function(expr, fVar);
		func.initFunction();
		// upper estimate: distance to point (x,f(x))
		double minSq = MyMath.distanceSquaredToFunctionAt(function, x, y, x);
		double minAt = x;
		double min = Math.sqrt(minSq);
		// calculate root; can only yield better distance than min if it's in [x-min, x+min]
		double[] roots = AlgoRoots.findRoots(func, x - min, x + min,
				(int) MyMath.clamp(20 * min, MIN_INTERVAL, MAX_INTERVAL));
		if (roots == null || roots.length == 0) {
			return minAt;
		}
		for (double root : roots) {
			double val = MyMath.distanceSquaredToFunctionAt(function, x, y,
					root);
			if (DoubleUtil.isGreater(minSq, val)) {
				minSq = val;
				minAt = root;
			}
		}
		return minAt;
	}

	private static double getClosestDefined(Function function,
			double x, double y, double direction) {
		for (double offset = direction * 0.1; Math.abs(offset) < Kernel.INV_MAX_DOUBLE_PRECISION;
			 offset *= 2) {
			if (!Double.isNaN(function.value(x + offset))) {
				return fineTuneClosestDefined(function, x + offset / 2, x + offset, x, y);
			}
		}
		return Double.NaN;
	}

	private static double fineTuneClosestDefined(Function function,
			double from, double to, double x, double y) {
		double x1 = from;
		double x2 = to;
		for (int i = 0; i < FINE_TUNE_STEPS; i++) {
			double d1 = MyMath.distanceSquaredToFunctionAt(function, x, y, x2);
			double midpoint = (x1 + x2) / 2;
			double d2 = MyMath.distanceSquaredToFunctionAt(function, x, y, midpoint);
			if (d1 < d2 || Double.isNaN(d2)) {
				x1 = midpoint;
			} else {
				x2 = midpoint;
			}
		}
		return x2;
	}

	/**
	 * Find all local extrema and pick the closest one.
	 * 
	 * @param polyFunction
	 *            polynomial
	 * @param x
	 *            source x-coord
	 * @param y
	 *            source y-coord
	 * @param kernel
	 *            kernel
	 * @return x value for closest point on function
	 */
	public static double closestValPoly(PolyFunction polyFunction, double x,
			double y, Kernel kernel) {
		PolyFunction polyDervi = polyFunction.getDerivative();
		// calculate coeffs for 2*(x - a) + 2(f(x) - b)f'(x) where a and b
		// are the coordinates of point
		// expanding it gives 2x - 2a + 2*f(x)*f'(x) - 2*b*f'(x)
		double[] funCoeffs = polyFunction.getCoeffs();
		double[] derivCoeffs = polyDervi.getCoeffs();
		int n = funCoeffs.length - 1;
		int m = derivCoeffs.length - 1;
		double[] eq = new double[(m + n < 1) ? 2 : m + n + 1];
		// calculate 2*f(x)*f'(x)
		for (int i = 0; i < eq.length; i++) { // c_i
			for (int j = Math.max(0, i - m); j <= Math.min(i, n); j++) { // sum
				eq[i] += 2 * funCoeffs[j] * derivCoeffs[i - j];
			}
		}
		// add -2*b*f'(x)
		for (int i = 0; i <= m; i++) {
			eq[i] += (-2) * y * derivCoeffs[i];
		}
		// add 2x - 2a
		eq[1] += 2;
		eq[0] -= 2 * x;
		// new polynomial coeffs in eq
		// calculate the roots and find the minimum
		EquationSolver solver = new EquationSolver();
		int nrOfRoots = solver.polynomialRoots(eq, false);
		if (nrOfRoots == 0) {
			return Double.NaN;
		}
		int k = 0;
		double min = MyMath.distanceSquaredToFunctionAt(polyFunction, x, y, eq[0]);
		for (int i = 1; i < nrOfRoots; i++) {
			double val = MyMath.distanceSquaredToFunctionAt(polyFunction, x, y,
					eq[i]);
			if (DoubleUtil.isGreater(min, val)) {
				min = val;
				k = i;
			}
		}
		return eq[k];
	}

}
