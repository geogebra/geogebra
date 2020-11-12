/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDependentLine.java
 *
 * Created on 29. Oktober 2001
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import java.util.Arrays;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;

/**
 *
 * @author mathieu
 */
public class AlgoDependentQuadric3D extends AlgoElement3D {

	private Equation equation;
	private ExpressionValue[] ev = new ExpressionValue[10]; // input
	private GeoQuadric3D quadric; // output

	private double[] coeffs;

	/**
	 * Creates new AlgoDependentPlane
	 * 
	 * @param cons
	 *            construction
	 * @param equ
	 *            equation
	 */
	public AlgoDependentQuadric3D(Construction cons, Equation equ) {
		super(cons, false); // don't add to construction list yet
		equation = equ;
		Polynomial lhs = equ.getNormalForm();

		ev[0] = lhs.getCoefficient("xx");
		ev[1] = lhs.getCoefficient("yy");
		ev[2] = lhs.getCoefficient("zz");
		ev[3] = lhs.getConstantCoefficient();

		// further will be divided by 2
		ev[4] = lhs.getCoefficient("xy");
		ev[5] = lhs.getCoefficient("xz");
		ev[6] = lhs.getCoefficient("yz");
		ev[7] = lhs.getCoefficient("x");
		ev[8] = lhs.getCoefficient("y");
		ev[9] = lhs.getCoefficient("z");

		coeffs = new double[10];

		// check coefficients
		for (int i = 0; i < 10; i++) {
			if (!ev[i].inspect(Inspecting.dynamicGeosFinder)) {
				ev[i] = ev[i].evaluate(StringTemplate.defaultTemplate);
			}

			// check that coefficient is a number: this may throw an exception
			ExpressionValue eval = ev[i]
					.evaluate(StringTemplate.defaultTemplate);
			((NumberValue) eval).getDouble();
		}

		// if we get here, all is ok: let's add this algorithm to the
		// construction list
		cons.addToConstructionList(this, false);

		quadric = new GeoQuadric3D(cons);
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();

	}

	@Override
	public Algos getClassName() {
		return Algos.Expression;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = equation.getGeoElementVariables(
				SymbolicMode.NONE);

		setOnlyOutput(quadric);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return the plane
	 */
	public GeoQuadric3D getQuadric() {
		return quadric;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		for (int i = 0; i < 4; i++) {
			coeffs[i] = ev[i].evaluateDouble();
		}
		for (int i = 4; i < 10; i++) {
			coeffs[i] = ev[i].evaluateDouble() / 2;
		}
		if (!Arrays.equals(coeffs, quadric.getFlatMatrix())) {
			quadric.setMatrix(coeffs);
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return equation.toString(tpl);
	}

}
