/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Returns the Column of a GeoElement.
 * 
 * @author Michael
 */
public class AlgoColumn extends AlgoElement {

	private GeoElement geo; // input
	private GeoNumeric num; // output

	public AlgoColumn(Construction cons, String label, GeoElement geo) {
		super(cons);
		this.geo = geo;

		num = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();
		num.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Column;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geo;

		super.setOutputLength(1);
		super.setOutput(0, num);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return num;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		GPoint p = geo.getSpreadsheetCoords();
		if (p != null)
			num.setValue(p.x + 1);
		else
			num.setUndefined();
	}

	
}
