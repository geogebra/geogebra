/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Iteration[ f(x), x0, n ]
 * 
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoIteration extends AlgoElement {

	private GeoFunction f; // input
	private GeoNumberValue startValue, n;
	private GeoElement startValueGeo, nGeo;
	private GeoNumeric result; // output

	public AlgoIteration(Construction cons, String label, GeoFunction f,
			GeoNumberValue startValue, GeoNumberValue n) {
		super(cons);
		this.f = f;
		this.startValue = startValue;
		startValueGeo = startValue.toGeoElement();
		this.n = n;
		nGeo = n.toGeoElement();

		result = new GeoNumeric(cons);

		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Iteration;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = f;
		input[1] = startValueGeo;
		input[2] = nGeo;

		super.setOutputLength(1);
		super.setOutput(0, result);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return result;
	}

	@Override
	public final void compute() {
		if (!f.isDefined() || !startValueGeo.isDefined() || !nGeo.isDefined()) {
			result.setUndefined();
			return;
		}

		int iterations = (int) Math.round(n.getDouble());
		if (iterations < 0) {
			result.setUndefined();
			return;
		}

		// perform iteration f(f(f(...(startValue))))
		double val = startValue.getDouble();
		for (int i = 0; i < iterations; i++) {
			val = f.evaluate(val);
		}
		result.setValue(val);
	}

	// TODO Consider locusequability

}
