/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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
			if (!ev[i].any(Inspecting::isDynamicGeoElement)) {
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
