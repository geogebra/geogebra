/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoAngleConic.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Converts a number to an angle.
 */
public class AlgoAngleNumeric extends AlgoElement {

	private GeoNumeric num; // input
	private GeoAngle angle; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param num
	 *            converted number
	 */
	public AlgoAngleNumeric(Construction cons, String label, GeoNumeric num) {
		super(cons);
		this.num = num;
		angle = new GeoAngle(cons);
		setInputOutput(); // for AlgoElement
		compute();
		angle.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Angle;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = num;

		setOnlyOutput(angle);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting angle
	 */
	public GeoAngle getAngle() {
		return angle;
	}

	// compute conic's angle
	@Override
	public final void compute() {
		// copy number to angle
		angle.setValue(num.value);
	}

}
