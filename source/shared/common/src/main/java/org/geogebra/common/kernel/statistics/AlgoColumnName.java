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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * Returns the column name of a GeoElement as a GeoText.
 * 
 * @author Markus
 */
public class AlgoColumnName extends AlgoElement {

	private GeoElement geo; // input
	private GeoText text; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param geo
	 *            spreadsheet cell
	 */
	public AlgoColumnName(Construction cons, String label, GeoElement geo) {
		super(cons);
		this.geo = geo;

		text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();
		text.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.ColumnName;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geo;

		setOnlyOutput(text);
		setDependencies(); // done by AlgoElement
	}

	public GeoText getGeoText() {
		return text;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		String col = GeoElementSpreadsheet.getSpreadsheetColumnName(
				geo.getLabel(StringTemplate.defaultTemplate));

		if (col == null) {
			text.setUndefined();
		} else {
			text.setTextString(col);
		}
	}

}
