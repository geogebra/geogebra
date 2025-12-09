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
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * Take last n objects from a text
 * 
 * @author Michael Borcherds
 * @version 2008-03-04
 */

public class AlgoLastString extends AlgoFirstString {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param inputText
	 *            input text
	 * @param n
	 *            number of chars (null for 1)
	 */
	public AlgoLastString(Construction cons, String label, GeoText inputText,
			GeoNumeric n) {
		super(cons, label, inputText, n);
	}

	@Override
	public Commands getClassName() {
		return Commands.Last;
	}

	@Override
	protected String getString(String str, int outsize) {
		return str.substring(size - outsize);
	}

}
