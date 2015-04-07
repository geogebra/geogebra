/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
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
 * Returns the name of a GeoElement as a GeoText.
 * 
 * @author Markus
 * @version
 */
public class AlgoColumnName extends AlgoElement {

	private GeoElement geo; // input
	private GeoText text; // output

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

		super.setOutputLength(1);
		super.setOutput(0, text);
		setDependencies(); // done by AlgoElement
	}

	public GeoText getGeoText() {
		return text;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		String col = GeoElementSpreadsheet.getSpreadsheetColumnName(geo
				.getLabel(StringTemplate.defaultTemplate));

		if (col == null)
			text.setUndefined();
		else
			text.setTextString(col);
	}

	// TODO Consider locusequability
}
