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
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.MyMath;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoDistancePointObject extends AlgoElement implements
		DistanceAlgo {

	private GeoPointND P; // input
	private GeoElement g; // input
	private GeoNumeric dist; // output
	private AlgoClosestPoint closePt;

	public AlgoDistancePointObject(Construction cons, String label,
			GeoPointND P, GeoElement g) {
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
		input[1] = g;

		setOutputLength(1);
		setOutput(0, dist);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getDistance() {
		return dist;
	}

	GeoPointND getP() {
		return P;
	}

	GeoElement getg() {
		return g;
	}

	// calc length of vector v
	@Override
	public final void compute() {
		if (closePt != null)
			dist.setValue(closePt.getP().distance(P));
		else
			dist.setValue(g.distance(P));
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("DistanceOfAandB", P.getLabel(tpl),
				g.getLabel(tpl));
	}

	private static final double INTERVAL_START = 30;
	private static final double INTERVAL_GROWTH = 2;
	private static final double MAX_INTERVAL = 10000;

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
	public static final double getClosestFunctionValueToPoint(
			Function function, double x, double y) {
		// Algorithm inspired by
		// http://bact.mathcircles.org/files/Winter2011/CM2_Posters/TPham_BACTPoster.pdf
		Kernel kernel = function.getKernel();
		PolyFunction polyFunction = function.expandToPolyFunction(
				function.getExpression(), false, true);
		if (polyFunction != null) {
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
			EquationSolver solver = new EquationSolver(kernel);
			int nrOfRoots = solver.polynomialRoots(eq, false);
			if (nrOfRoots == 0) {
				return Double.NaN;
			}
			int k = 0;
			double min = MyMath.distancePointFunctionAt(polyFunction, x, y,
					eq[0]);
			for (int i = 1; i < nrOfRoots; i++) {
				double val = MyMath.distancePointFunctionAt(polyFunction, x, y,
						eq[i]);
				if (Kernel.isGreater(min, val)) {
					min = val;
					k = i;
				}
			}
			return eq[k];
		}
		// non polynomial case
		FunctionVariable fVar = function.getFunctionVariable();
		Function deriv = function.getDerivative(1, true);
		// replace derivatives' function variable with functions'
		// we need this, so our new function created below, can be evaluated
		deriv.traverse(Traversing.Replacer.getReplacer(
				deriv.getFunctionVariable(), fVar));
		// build expression 2*(x - a) + 2(f(x) - b)f'(x) where a and b are the
		// coordinates of point
		ExpressionNode expr = new ExpressionNode(kernel, fVar, Operation.MINUS,
				new MyDouble(kernel, x));
		expr = expr.multiply(2);
		ExpressionNode expr2 = new ExpressionNode(kernel,
				function.getExpression(), Operation.MINUS, new MyDouble(kernel,
						y));
		expr2 = expr2.multiplyR(deriv.getExpression());
		expr2 = expr2.multiply(2);
		expr = expr.plus(expr2);
		// calculate root
		Function func = new Function(expr, fVar);
		GeoFunction geoFunc = new GeoFunction(kernel.getConstruction(), func);
		double[] roots;
		double left = INTERVAL_START;
		double right = INTERVAL_START;
		while ((roots = AlgoRoots.findRoots(geoFunc, x - left, y + right,
				(int) ((left + right) * 10))) == null
				&& Kernel.isGreater(MAX_INTERVAL, left)) {
			left *= INTERVAL_GROWTH;
			right *= INTERVAL_GROWTH;
		}
		if (roots == null || roots.length == 0) {
			return Double.NaN;
		}
		int k = 0;
		double min = MyMath.distancePointFunctionAt(function, x, y, roots[0]);
		for (int i = 1; i < roots.length; i++) {
			double val = MyMath.distancePointFunctionAt(function, x, y,
					roots[i]);
			if (Kernel.isGreater(min, val)) {
				min = val;
				k = i;
			}
		}
		return roots[k];
	}

	// TODO Consider locusequability
}
