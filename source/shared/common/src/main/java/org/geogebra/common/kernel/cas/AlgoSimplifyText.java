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

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.editor.share.util.Unicode;

/**
 * Parses the input as a function, simplifies it and returns as text
 * 
 * @author Michael
 */
public class AlgoSimplifyText extends AlgoElement {

	private GeoText textIn; // input
	private GeoText text; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param textIn
	 *            input text
	 */
	public AlgoSimplifyText(Construction cons, String label, GeoText textIn) {
		super(cons);
		this.textIn = textIn;

		text = new GeoText(cons);
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();
		text.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Simplify;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		input = new GeoElement[1];
		input[0] = textIn;

		setOnlyOutput(text);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting text
	 */
	public GeoText getGeoText() {
		return text;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {

		// eg Simplify["+1x++x--x+-1x-+1x++x"]

		String ret = textIn.getTextStringSafe();
		// ++ -> +
		ret = ret.replaceAll("\\+ *\\+", "+");
		// -- -> +
		ret = ret.replaceAll("- *-", "+");
		// +- -> -
		ret = ret.replaceAll("\\+ *-", "-");
		// -+ -> -
		ret = ret.replaceAll("- *\\+", "-");
		// +1x -> +x
		ret = ret.replaceAll("\\+ *1 *x", "+x");
		// -1x -> -x
		ret = ret.replaceAll("- *1 *x", "-x");

		// replace "+" with " + "
		// needs to be called twice for eg x+x+x+x
		ret = ret.replaceAll("([^ ])\\+([^ ])", "$1 + $2");
		ret = ret.replaceAll("([^ ])\\+([^ ])", "$1 + $2");

		// replace "-" with " - "
		// needs to be called twice for eg x-x-x-x
		ret = ret.replaceAll("([^ ])\\-([^ ])", "$1 - $2");
		ret = ret.replaceAll("([^ ])\\-([^ ])", "$1 - $2");

		// replace "=" with " = "
		// needs to be called twice for eg x=x=x=x
		ret = ret.replaceAll("([^ ])\\=([^ ])", "$1 = $2");
		ret = ret.replaceAll("([^ ])\\=([^ ])", "$1 = $2");

		// remove + and 1 at the start
		if (ret.charAt(0) == '+') {
			ret = ret.substring(1);
		}
		if (ret.startsWith("1x")) {
			ret = ret.substring(1);
		}

		// replace "-" with unicode minus
		ret = ret.replaceAll(" -", " " + Unicode.MINUS);

		text.setTextString(ret);
	}

}
