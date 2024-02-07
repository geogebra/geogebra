/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoAxisFirstLength.java
 *
 * Created on 30. August 2001, 21:37
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
 * @author Markus
 */
public class AlgoAxisLength extends AlgoElement {

	private GeoConicND c; // input
	private GeoNumeric num; // output
	private int axisId;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param arg
	 *            conic
	 * @param axisId
	 *            0 for major, 1 for minor
	 */
	public AlgoAxisLength(Construction cons, String label,
			GeoConicND arg, int axisId) {
		super(cons);
		this.c = arg;
		this.axisId = axisId;
		num = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement
		compute();
		num.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return axisId == 0 ? Commands.FirstAxisLength
				: Commands.SecondAxisLength;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = c;

		setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getLength() {
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
		case GeoConicNDConstants.CONIC_HYPERBOLA:
		case GeoConicNDConstants.CONIC_ELLIPSE:
			num.setValue(c.getHalfAxis(axisId));
			break;

		default:
			num.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		if (axisId == 1) {
			return getLoc().getPlainDefault("SecondAxisLengthOfA",
					"Length of %0's semi-minor axis", c.getLabel(tpl));
		}
		return getLoc().getPlainDefault("FirstAxisLengthOfA",
				"Length of %0's semi-major axis", c.getLabel(tpl));

	}

}
