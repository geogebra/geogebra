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
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoText;

public class AlgoUnicodeToLetter extends AlgoElement {

	protected GeoNumberValue a; // input
	protected GeoText text; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param a
	 *            unicode number
	 */
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

		setOnlyOutput(text);
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
