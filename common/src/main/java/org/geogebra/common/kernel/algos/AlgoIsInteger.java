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
