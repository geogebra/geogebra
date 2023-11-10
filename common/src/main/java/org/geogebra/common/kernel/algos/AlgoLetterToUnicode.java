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
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * Convert single letter text to unicode number.
 */
public class AlgoLetterToUnicode extends AlgoElement {

	protected GeoText text; // input
	protected GeoNumeric num; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param text
	 *            letter as text
	 */
	public AlgoLetterToUnicode(Construction cons, String label, GeoText text) {
		super(cons);
		this.text = text;

		num = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		compute();

		num.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.LetterToUnicode;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = text;

		setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return unicode number
	 */
	public GeoNumeric getResult() {
		return num;
	}

	@Override
	public void compute() {
		String t = text.getTextString();
		if (t == null || t.length() != 1) {
			num.setUndefined();
		} else {
			num.setValue(t.charAt(0));
		}

	}

}
