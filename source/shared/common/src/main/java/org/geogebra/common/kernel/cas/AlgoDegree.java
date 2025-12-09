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

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Degree of a polynomial
 * 
 * Uses CAS sometimes, eg Degree[x^n] so needs "implements UsesCAS"
 * 
 * @author Michael Borcherds
 */
public class AlgoDegree extends AlgoElement implements UsesCAS {

	private GeoFunction f; // input
	private GeoNumeric num; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 */
	public AlgoDegree(Construction cons, String label, GeoFunction f) {
		super(cons);
		this.f = f;

		num = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement
		compute();
		num.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Degree;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = f;

		setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return degree as number
	 */
	public GeoNumeric getResult() {
		return num;
	}

	@Override
	public final void compute() {
		if (!f.isDefined()) {
			num.setUndefined();
			return;
		}

		Function inFun = f.getFunction();

		// check if it's a polynomial & get coefficients
		PolyFunction poly = inFun.expandToPolyFunction(inFun.getExpression(),
				false, false);

		if (poly != null) {
			num.setValue(poly.getDegree());
			return;
		}

		// not a polynomial
		num.setUndefined();
	}

}
