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
