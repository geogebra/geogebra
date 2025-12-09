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
 * Length of a GeoText object.
 * 
 * @author Michael
 */

public class AlgoTextLength extends AlgoElement {

	private GeoText text; // input
	private GeoNumeric length; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param text
	 *            text
	 */
	public AlgoTextLength(Construction cons, String label, GeoText text) {
		super(cons);
		this.text = text;

		length = new GeoNumeric(cons);

		setInputOutput();
		compute();
		length.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Length;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = text;

		setOnlyOutput(length);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getLength() {
		return length;
	}

	@Override
	public final void compute() {
		if (text.isDefined()) {
			length.setValue(text.getTextStringSafe().length());
		} else {
			length.setUndefined();
		}
	}

}
