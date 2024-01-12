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
 * Take a substring from a text
 * 
 * @author Michael Borcherds
 * @version 2008-03-04
 */

public class AlgoTakeString extends AlgoElement {

	private GeoText inputText; // input
	private GeoNumeric n;
	private GeoNumeric m; // input
	private GeoText outputText; // output
	private int size;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputText
	 *            text
	 * @param m
	 *            start index (1 based)
	 * @param n
	 *            end index (1 based)
	 */
	public AlgoTakeString(Construction cons, String label, GeoText inputText,
			GeoNumeric m, GeoNumeric n) {
		super(cons);
		this.inputText = inputText;
		this.m = m;
		this.n = n;

		outputText = new GeoText(cons);
		outputText.setIsTextCommand(true);

		setInputOutput();
		compute();
		outputText.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Take;
	}

	@Override
	protected void setInputOutput() {

		if (n != null) {
			input = new GeoElement[3];
			input[2] = n;
		} else {
			input = new GeoElement[2];
		}
		input[0] = inputText;
		input[1] = m;
		setOnlyOutput(outputText);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting text
	 */
	public GeoText getResult() {
		return outputText;
	}

	@Override
	public final void compute() {

		if (!m.isDefined() || (n != null && !n.isDefined())
				|| inputText == null) {
			outputText.setTextString("");
			return;
		}

		String str = inputText.getTextString();

		if (str == null) {
			outputText.setUndefined();
			return;
		}

		size = str.length();
		int start = (int) m.getDouble();
		double nVal = n == null ? size : n.getDouble();
		int end = (int) nVal;

		if (nVal == 0 && inputText.isDefined() && start > 0 && start <= size) {
			outputText.setTextString("");
			return;
		}

		if (!inputText.isDefined() || size == 0 || start <= 0 || end > size
				|| start > end) {
			outputText.setUndefined();
			return;
		}

		outputText.setTextString(str.substring(start - 1, end));

	}

}
