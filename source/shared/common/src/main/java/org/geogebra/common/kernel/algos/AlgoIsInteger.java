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
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.DoubleUtil;

/**
 * Returns whether an object is an integer
 * 
 * @author Michael Borcherds
 * @version 2008-03-06
 */

public class AlgoIsInteger extends AlgoElement {

	private GeoNumeric inputGeo; // input
	private GeoBoolean outputBoolean; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputGeo
	 *            number
	 */
	public AlgoIsInteger(Construction cons, String label, GeoNumeric inputGeo) {
		super(cons);
		this.inputGeo = inputGeo;

		outputBoolean = new GeoBoolean(cons);

		setInputOutput();
		compute();
		outputBoolean.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.IsInteger;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputGeo;

		setOnlyOutput(outputBoolean);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return boolean result
	 */
	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {
		outputBoolean.setValue(DoubleUtil.isInteger(inputGeo.getDouble()));
	}

}
