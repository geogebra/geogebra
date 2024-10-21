/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;

/**
 * Returns the Column of a GeoElement.
 * 
 * @author Michael
 */
public class AlgoRow extends AlgoElement {

	private GeoElement geo; // input
	private GeoNumeric num; // output

	/**
	 * Creates new row algo
	 */
	public AlgoRow(Construction cons, String label, GeoElement geo) {
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
		return Commands.Row;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geo;

		setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the row number of the cell
	 * 
	 * @return row number of the cell
	 */
	public GeoNumeric getResult() {
		return num;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		SpreadsheetCoords p = geo.getSpreadsheetCoords();
		if (p != null) {
			num.setValue(p.row + 1);
		} else {
			num.setUndefined();
		}
	}

}
