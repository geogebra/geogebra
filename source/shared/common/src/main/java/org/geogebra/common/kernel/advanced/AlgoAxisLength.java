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
