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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * Take first n objects from a list
 * 
 * @author Michael Borcherds
 * @version 2008-03-04
 */

public class AlgoFirstString extends AlgoElement {

	protected GeoText inputText; // input
	protected GeoNumeric n; // input
	protected GeoText outputText; // output
	protected int size;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputText
	 *            input
	 * @param n
	 *            number of characters (null for 1)
	 */
	public AlgoFirstString(Construction cons, String label, GeoText inputText,
			GeoNumeric n) {
		super(cons);
		this.inputText = inputText;
		this.n = n;

		outputText = new GeoText(cons);
		outputText.setIsTextCommand(true);

		setInputOutput();
		compute();
		outputText.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.First;
	}

	@Override
	protected void setInputOutput() {

		if (n != null) {
			input = new GeoElement[2];
			input[0] = inputText;
			input[1] = n;
		} else {
			input = new GeoElement[1];
			input[0] = inputText;
		}

		setOnlyOutput(outputText);
		setDependencies(); // done by AlgoElement
	}

	public GeoText getResult() {
		return outputText;
	}

	@Override
	public void compute() {
		String str = inputText.getTextString();

		if (str == null) {
			outputText.setUndefined();
			return;
		}

		size = str.length();
		int outsize = n == null ? 1 : (int) n.getDouble();

		if (!inputText.isDefined() || size == 0 || outsize < 0
				|| outsize > size) {
			outputText.setUndefined();
			return;
		}

		if (outsize == 0) {
			outputText.setTextString(""); // return empty string
		} else {
			outputText.setTextString(getString(str, outsize));
		}
	}

	protected String getString(String str, int outsize) {
		return str.substring(0, outsize);
	}

}
