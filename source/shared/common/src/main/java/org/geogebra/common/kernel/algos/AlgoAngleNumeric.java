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
