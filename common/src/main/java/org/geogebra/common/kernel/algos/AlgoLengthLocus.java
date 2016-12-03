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
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Length of a GeoLocus object.
 */
public class AlgoLengthLocus extends AlgoElement {

	private GeoLocus locus; // input
	private GeoNumeric length; // output

	public AlgoLengthLocus(Construction cons, String label, GeoLocus locus) {
		super(cons);
		this.locus = locus;

		length = new GeoNumeric(cons);

		setInputOutput();
		compute();
		length.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Length;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = locus;

		super.setOutputLength(1);
		super.setOutput(0, length);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getLength() {
		return length;
	}

	@Override
	public final void compute() {
		if (locus.isDefined())
			length.setValue(locus.getPointLength());
		else
			length.setUndefined();
	}

	

}
