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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Parametric derivative of a curve c. The parametric derivative of a curve c(t)
 * = (x(t), y(t)) is defined as (x(t), y'(t)/x'(t)).
 * 
 * @author Markus Hohenwarter
 */
public class AlgoParametricDerivative extends AlgoElement implements UsesCAS {

	private GeoCurveCartesian curve; // in
	private GeoCurveCartesian paramDeriv; // out

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param curve
	 *            curve
	 */
	public AlgoParametricDerivative(Construction cons, String label,
			GeoCurveCartesian curve) {
		this(cons, curve);
		paramDeriv.setLabel(label);
	}

	private AlgoParametricDerivative(Construction cons,
			GeoCurveCartesian curve) {
		super(cons);
		this.curve = curve;
		paramDeriv = (GeoCurveCartesian) curve.copyInternal(cons);

		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.ParametricDerivative;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = curve;

		setOnlyOutput(paramDeriv);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return parametric derivative
	 */
	public GeoCurveCartesian getParametricDerivative() {
		return paramDeriv;
	}

	@Override
	final public void compute() {
		if (!curve.isDefined()) {
			paramDeriv.setUndefined();
			return;
		}

		// compute parametric derivative
		// (x(t), y'(t)/x'(t))
		paramDeriv.setParametricDerivative(curve);
	}

	@Override
	final public String toString(StringTemplate tpl) {

		return getLoc().getPlainDefault("ParametricDerivativeOfA",
				"Parametric Derivative of %0",
				curve.toGeoElement().getLabel(tpl));
	}
}
