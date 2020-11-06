/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.math.BigDecimal;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MinusOne;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

/**
 * Fit a Polynomial exactly to a set of coordinates. Unstable above about 12
 * coords adapted from AlgoPolynomialFromFunction
 * 
 * @author Michael Borcherds
 */
public class AlgoPolynomialFromCoordinates extends AlgoElement {

	private GeoList inputList; // input
	private GeoFunction g; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            points
	 */
	public AlgoPolynomialFromCoordinates(Construction cons, String label,
			GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		g = new GeoFunction(cons);
		setInputOutput(); // for AlgoElement
		compute();
		g.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Polynomial;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		super.setOutputLength(1);
		super.setOutput(0, g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return polynomial function
	 */
	public GeoFunction getPolynomial() {
		return g;
	}

	// ON CHANGE: similar code is in AlgoTaylorSeries
	@Override
	public final void compute() {
		if (!inputList.isDefined()) {
			g.setUndefined();
			return;
		}

		setFromPoints(g, inputList);

	}

	/**
	 * @param g
	 *            function
	 * @param inputList
	 *            pointlist
	 */
	public static void setFromPoints(GeoFunction g, GeoList inputList) {
		int n = inputList.size();

		if (n < 2) { // can't draw a unique polynomial through 0 or 1 points!
			g.setUndefined();
			return;
		}

		double[] x = new double[n];
		double[] y = new double[n];
		double[] xy = new double[2];

		// copy inputList into two arrays
		for (int i = 0; i < n; i++) {
			GeoElement geo = inputList.get(i);
			if (geo instanceof GeoPoint) {
				GeoPoint listElement = (GeoPoint) inputList.getCached(i);
				listElement.getInhomCoords(xy);
				x[i] = xy[0];
				y[i] = xy[1];
			} else {
				g.setUndefined();
				return;
			}
		}

		boolean[] remove = new boolean[n];
		for (int i = 0; i < n - 1; i++) {
			remove[i] = false;
		}

		// check all the x-coordinates are different
		for (int i = 0; i < n - 1; i++) {
			for (int j = i + 1; j < n; j++) {
				if (x[i] == x[j]) {
					if (y[i] == y[j]) { // two equal points, remove one

						remove[j] = true;
					} else {
						g.setUndefined();
						return;
					}
				}
			}
		}

		// remove duplicates at end of list
		while (remove[n - 1]) {
			n--;
		}

		// remove duplicates in the middle;
		if (n > 2) {
			for (int i = n - 2; i > 0; i--) {
				if (remove[i]) {
					x[i] = x[n - 1];
					y[i] = y[n - 1];
					n--;
				}
			}
		}

		// calculate the coefficients
		double[] cof = new double[n];
		try {
			// if (n < 15)
			// polcoe(x, y, n, cof);
			// // Michael Borcherds 2008-03-09 added polcoeBig
			// else

			// now always use polcoeBig as it's much more accurate even for eg
			// eg Polynomial[ (4.18, 5.2365368), (4.178999999999999,
			// 5.238777266100002), (4.181, 5.234293825899999) ]
			// note: PolynomialFunctionLagrangeForm is only slightly better than
			// polcoe
			polcoeBig(x, y, n, cof);
		} catch (Exception e) {
			g.setUndefined();
			return;
		}
		// build polynomial
		Function polyFun = buildPolyFunctionExpression(g.getKernel(), cof);

		if (polyFun == null) {
			g.setUndefined();
			return;
		}

		g.setFunction(polyFun);
		g.setDefined(true);

	}

	/**
	 * @param kernel
	 *            kernel
	 * @param cof
	 *            coefficients
	 * @return function
	 */
	public static Function buildPolyFunctionExpression(Kernel kernel,
			double[] cof) {
		int n = cof.length;
		ExpressionNode poly = null; // expression for the expanded polynomial
		FunctionVariable fVar = new FunctionVariable(kernel);
		double coeff;
		for (int k = n - 1; k >= 0; k--) {
			coeff = cof[k];
			if (Double.isNaN(coeff) || Double.isInfinite(coeff)) {
				return null;
			} else if (coeff == 0) {
				continue; // this part vanished
			}

			boolean negativeCoeff = coeff < 0;

			// build the expression x^k
			ExpressionValue powerExp;
			switch (k) {
			case 0:
				powerExp = null;
				break;

			case 1:
				powerExp = fVar;
				break;

			default:
				powerExp = new ExpressionNode(kernel, fVar, Operation.POWER,
						new MyDouble(kernel, k));
			}

			// build the expression
			// (coeff) * x^k
			ExpressionValue partExp;
			MyDouble coeffMyDouble = null;
			// check for poly != null rather than k != n-1 in case the leading
			// coefficient was 0, eg FitPoly[{(1,-1),(0,0),(-1,-1),(2,-4)},3]
			if (DoubleUtil.isEqual(coeff, 1.0)
					|| (poly != null && DoubleUtil.isEqual(coeff, -1.0))) {
				if (powerExp == null) {
					partExp = new MyDouble(kernel, 1.0);
				} else {
					partExp = powerExp;
				}
			} else {
				coeffMyDouble = coeff == -1 ? new MinusOne(kernel) : new MyDouble(kernel, coeff);
				if (powerExp == null) {
					partExp = coeffMyDouble;
				} else {
					partExp = new ExpressionNode(kernel, coeffMyDouble,
							Operation.MULTIPLY, powerExp);
				}
			}

			// add part to series
			if (poly == null) {
				poly = new ExpressionNode(kernel, partExp);
			} else {
				if (negativeCoeff) {
					if (coeffMyDouble != null) {
						coeffMyDouble.set(-coeff); // change sign
					}
					poly = new ExpressionNode(kernel, poly, Operation.MINUS,
							partExp);
				} else {
					poly = new ExpressionNode(kernel, poly, Operation.PLUS,
							partExp);
				}
			}
		}

		// all coefficients were 0, we've got f(x) = 0
		if (poly == null) {
			poly = new ExpressionNode(kernel, new MyDouble(kernel, 0));
		}
		// polynomial Function
		Function polyFun = new Function(poly, fVar);
		return polyFun;
	}

	private static void polcoeBig(double[] xx, double[] yy, int n,
			double[] coff) {
	// Given arrays x[0..n-1] and y[0..n-1] containing a tabulated function yi
	// = f(xi), this routine
	// returns an array of coefficients cof[0..n], such that yi = Sigma cofj.xj
	// adapted from Numerical Recipes chap 3.5
		BigDecimal[] x = new BigDecimal[n];
		BigDecimal[] y = new BigDecimal[n];
		BigDecimal[] cof = new BigDecimal[n];
		BigDecimal[] s = new BigDecimal[n];
		int k, j, i;
		BigDecimal minusone = new BigDecimal(-1.0d);
		BigDecimal phi, ff, b;
		for (i = 0; i < n; i++) {
			x[i] = new BigDecimal(xx[i]);
			y[i] = new BigDecimal(yy[i]);
		}
		// double[] s = new double[n];
		for (i = 0; i < n; i++) {
			s[i] = cof[i] = BigDecimal.ZERO;
		}
		s[n - 1] = x[0].multiply(minusone);
		for (i = 1; i < n; i++) {
			for (j = n - 1 - i; j < n - 1; j++) {
				s[j] = s[j].subtract(x[i].multiply(s[j + 1]));
			}
			s[n - 1] = s[n - 1].subtract(x[i]);
		}
		for (j = 0; j < n; j++) {
			phi = new BigDecimal((double) n);
			for (k = n - 1; k > 0; k--) {
				BigDecimal kk = new BigDecimal((double) k);
				phi = (kk.multiply(s[k])).add(x[j].multiply(phi));
			}

			// must specify a scale, otherwise 1/2 gives 1 not 0.5
			ff = y[j].divide(phi, 50, BigDecimal.ROUND_HALF_UP);

			b = BigDecimal.ONE;
			for (k = n - 1; k >= 0; k--) {
				cof[k] = cof[k].add(b.multiply(ff));
				b = s[k].add(x[j].multiply(b));
			}
		}

		for (i = 0; i < n; i++) {
			coff[i] = cof[i].doubleValue();
		}

	}

}
