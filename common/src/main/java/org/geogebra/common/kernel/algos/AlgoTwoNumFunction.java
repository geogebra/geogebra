/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * Area of polygon P[0], ..., P[n]
 *
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Parent algorithm for commands that are functions of R^2 -&gt; R
 * 
 * @author Markus Hohenwarter
 */
public abstract class AlgoTwoNumFunction extends AlgoElement {

	protected GeoNumberValue a; // input
	protected GeoNumberValue b; // input
	protected GeoNumeric num; // output

	protected AlgoTwoNumFunction(Construction cons, String label,
			GeoNumberValue a, GeoNumberValue b) {
		this(cons, a, b);
		num.setLabel(label);
	}

	protected AlgoTwoNumFunction(Construction cons, GeoNumberValue a,
			GeoNumberValue b) {
		super(cons);
		this.a = a;
		this.b = b;
		num = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = a.toGeoElement();
		input[1] = b.toGeoElement();

		super.setOutputLength(1);
		super.setOutput(0, num);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return num;
	}
}
