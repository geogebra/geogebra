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
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;

public class AlgoTextToUnicode extends AlgoElement {

	protected GeoText text; // input
	protected GeoList list; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param text
	 *            text
	 */
	public AlgoTextToUnicode(Construction cons, String label, GeoText text) {
		super(cons);
		this.text = text;

		list = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();

		list.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.TextToUnicode;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = text;

		setOnlyOutput(list);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return list;
	}

	@Override
	public void compute() {
		String t = text.getTextString();

		if (t == null) {
			list.setUndefined();
			return;
		}

		list.setDefined(true);
		list.clear();

		int size = t.length();

		if (size == 0) {
			return;
		}

		for (int i = 0; i < size; i++) {
			GeoNumeric num = new GeoNumeric(cons);
			num.setValue(t.charAt(i));
			list.add(num); // num.copy());
		}
	}

}
