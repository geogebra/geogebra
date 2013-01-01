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
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.commands.Commands;
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

	/**
	 * @param cons construction
	 * @param label label for output
	 * @param curve curve
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

	// TODO Consider locusequability
	
	@Override
	final public String toString(StringTemplate tpl) {
                
	    return app.getPlain("ParametricDerivativeOfA",curve.toGeoElement().getLabel(tpl));
    }
}
