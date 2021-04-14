/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.cas;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.statistics.FitAlgo;

/**
 * returns coefficients of a Polynomial as a list
 * 
 * Uses CAS sometimes, eg Coefficients[x^n] so needs "implements UsesCAS"
 * 
 * @author Michael Borcherds
 */
public class AlgoCoefficients extends AlgoElement implements UsesCAS {

	private GeoFunction f; // input
	private GeoList g; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 */
	public AlgoCoefficients(Construction cons, String label, GeoFunction f) {
		this(cons, f);
		g.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param f
	 *            function
	 */
	public AlgoCoefficients(Construction cons, GeoFunction f) {
		super(cons);
		this.f = f;

		g = new GeoList(cons);
		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Coefficients;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = f;

		setOutputLength(1);
		setOutput(0, g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting function
	 */
	public GeoList getResult() {
		return g;
	}

	@Override
	public final void compute() {
		if (!f.isDefined()) {
			g.setUndefined();
			return;
		}

		Function inFun = f.getFunction();

		ExpressionNode expression = inFun.getExpression();
		if (expression.isSecret()) {
			g.setUndefined();
			return;
		}

		// check if it's a polynomial & get coefficients
		PolyFunction poly = inFun.expandToPolyFunction(expression,
				false, false);
		g.clear();
		g.setDefined(true);
		if (poly != null) {
			double[] coeffs = poly.getCoeffs();
			for (int i = coeffs.length - 1; i >= 0; i--) {
				g.add(new GeoNumeric(cons, coeffs[i]));
			}
		} else if (f.getParentAlgorithm() instanceof FitAlgo) {
			FitAlgo fitAlgo = (FitAlgo) f.getParentAlgorithm();
			double[] coeffs = fitAlgo.getCoeffs();
			for (int i = coeffs.length - 1; i >= 0; i--) {
				g.add(new GeoNumeric(cons, coeffs[i]));
			}
		} else {
			ArrayList<Double> constants = extractConstants(expression);
			for (Double coeff: constants) {
				g.add(new GeoNumeric(cons, coeff));
			}
		}
	}

	private ArrayList<Double> extractConstants(ExpressionNode expression) {
		ArrayList<Double> constants = new ArrayList<>();
		expression.inspect(new Inspecting() {
			@Override
			public boolean check(ExpressionValue v) {
				if (v instanceof MyDouble && v.isConstant()) {
					constants.add(v.evaluateDouble());
				}
				return false;
			}
		});
		return constants;
	}

}
