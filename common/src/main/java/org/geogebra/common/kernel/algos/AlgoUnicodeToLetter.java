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
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoText;

public class AlgoUnicodeToLetter extends AlgoElement {

	protected GeoNumberValue a; // input
	protected GeoText text; // output

	public AlgoUnicodeToLetter(Construction cons, String label,
			GeoNumberValue a) {
		super(cons);
		this.a = a;

		text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();

		text.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.UnicodeToLetter;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = a.toGeoElement();

		super.setOutputLength(1);
		super.setOutput(0, text);
		setDependencies(); // done by AlgoElement
	}

	public GeoText getResult() {
		return text;
	}

	@Override
	public final void compute() {

		char ss = (char) a.getDouble();
		text.setTextString(ss + "");
	}

	
}
