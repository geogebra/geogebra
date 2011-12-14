/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;

/**
 * Parametric derivative of a curve c. The parametric derivative of a 
 * curve c(t) = (x(t), y(t)) is defined as (x(t), y'(t)/x'(t)).
 * 
 * @author Markus Hohenwarter
 */
public class AlgoParametricDerivative extends AlgoElement {

	private GeoCurveCartesian curve, paramDeriv;

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

	public String getClassName() {
		return "AlgoParametricDerivative";
	}

	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = curve;
		
		setOutputLength(1);
		setOutput(0, paramDeriv);
		setDependencies(); // done by AlgoElement
	}
	
	public GeoCurveCartesian getParametricDerivative() {
		return paramDeriv;
	}

	final public void compute() {
		if (!curve.isDefined()) {
			paramDeriv.setUndefined();
			return;
		}
		
		// compute parametric derivative
		// (x(t), y'(t)/x'(t))
		paramDeriv.setParametricDerivative(curve);
	}
}
