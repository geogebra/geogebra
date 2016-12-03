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
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;

public class AlgoUnicodeToText extends AlgoElement {

	protected GeoList list; // input
	protected GeoText text; // output

	public AlgoUnicodeToText(Construction cons, String label, GeoList list) {
		super(cons);
		this.list = list;

		text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();

		text.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.UnicodeToText;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = list;

		super.setOutputLength(1);
		super.setOutput(0, text);
		setDependencies(); // done by AlgoElement
	}

	public GeoText getResult() {
		return text;
	}

	@Override
	public void compute() {

		int size = list.size();

		if (size == 0) {
			text.setTextString("");
			return;
		}

		String s = "";

		for (int i = 0; i < size; i++) {
			GeoElement geo = list.get(i);

			if (geo.isGeoNumeric()) {
				s += (char) ((GeoNumeric) geo).getDouble();
			}
		}
		text.setTextString(s);
	}

	
}
