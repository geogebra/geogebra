/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoEccentricity.java
 *
 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;

/**
 *
 * @author Michael
 */
public class AlgoEccentricity extends AlgoElement {

	private GeoConicND c; // input
	private GeoNumeric num; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param arg
	 *            conic
	 */
	public AlgoEccentricity(Construction cons, String label, GeoConicND arg) {
		super(cons);
		this.c = arg;
		num = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement
		compute();
		num.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Eccentricity;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = c;

		setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getEccentricity() {
		return num;
	}

	GeoConicND getConic() {
		return c;
	}

	// set excentricity
	@Override
	public final void compute() {
		switch (c.type) {
		case GeoConicNDConstants.CONIC_CIRCLE:
			num.setValue(0.0);
			break;

		case GeoConicNDConstants.CONIC_PARABOLA:
			num.setValue(1.0);
			break;

		case GeoConicNDConstants.CONIC_HYPERBOLA:
		case GeoConicNDConstants.CONIC_ELLIPSE:
			num.setValue(c.eccentricity);
			break;

		default:
			num.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("EccentricityOfA", "Eccentricity of %0",
				c.getLabel(tpl));
	}

}
