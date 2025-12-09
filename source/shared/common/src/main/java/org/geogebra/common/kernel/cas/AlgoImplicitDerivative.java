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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ArbitraryConstantRegistry;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;

/**
 * Algorithm for ImplicitDerivative[f(x,y)]
 *
 */
public class AlgoImplicitDerivative extends AlgoElement implements UsesCAS {

	private GeoFunctionNVar result;
	private FunctionalNVar functional;
	private ArbitraryConstantRegistry arbconst = new ArbitraryConstantRegistry(this);

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param functional
	 *            function of two variables
	 */
	public AlgoImplicitDerivative(Construction cons, String label,
			FunctionalNVar functional) {
		super(cons);
		this.functional = functional;
		this.result = new GeoFunctionNVar(cons);
		setInputOutput();
		compute();
		result.setLabel(label);

	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(result);
		if (functional instanceof GeoFunctionNVar) {
			input = new GeoElement[] { (GeoFunctionNVar) functional };
		}
		if (functional instanceof GeoFunction) {
			input = new GeoElement[] { (GeoFunction) functional };
		}
		setDependencies();

	}

	@Override
	public void compute() {
		StringTemplate tpl = StringTemplate.prefixedDefault;
		StringBuilder sb = new StringBuilder(80);
		sb.append("ImplicitDerivative(");
		sb.append(functional.toValueString(tpl));
		sb.append(")");

		try {
			String functionOut = kernel.evaluateCachedGeoGebraCAS(sb.toString(),
					arbconst);
			if (functionOut == null || functionOut.length() == 0) {
				result.setUndefined();
			} else {
				// read result back into function
				result.set(kernel.getAlgebraProcessor()
						.evaluateToFunctionNVar(functionOut, true, false));
			}
		} catch (Throwable e) {
			result.setUndefined();
		}
	}

	@Override
	public Commands getClassName() {
		return Commands.ImplicitDerivative;
	}

	/**
	 * @return resulting derivative
	 */
	public GeoFunctionNVar getResult() {
		return result;
	}

}
