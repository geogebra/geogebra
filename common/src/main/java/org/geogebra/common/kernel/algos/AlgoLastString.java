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
