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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * Converts a number to ordinal as text.
 */
public class AlgoOrdinal extends AlgoElement {

	private GeoNumeric n; // input
	private GeoText text; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param n
	 *            number
	 */
	public AlgoOrdinal(Construction cons, String label, GeoNumeric n) {
		super(cons);
		this.n = n;

		text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();

		text.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Ordinal;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = n;

		setOnlyOutput(text);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting text
	 */
	public GeoText getResult() {
		return text;
	}

	@Override
	public void compute() {

		if (!n.isDefined()) {
			text.setTextString("");
			text.setUndefined();
			return;
		}

		double num = n.getDouble();

		if (num < 0 || Double.isNaN(num) || Double.isInfinite(num)) {
			text.setTextString("");
			text.setUndefined();
			return;
		}

		text.setTextString(getLoc().getLanguage().getOrdinalNumber((int) num));

	}

}
