/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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

	private GeoCurveCartesian curve, paramDeriv;

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

	private AlgoParametricDerivative(Construction cons, GeoCurveCartesian curve) {
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

		setOutputLength(1);
		setOutput(0, paramDeriv);
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

		return getLoc().getPlain("ParametricDerivativeOfA",
				curve.toGeoElement().getLabel(tpl));
	}
}
