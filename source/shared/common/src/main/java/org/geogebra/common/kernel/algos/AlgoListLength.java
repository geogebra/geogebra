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

/**
 * Length of a GeoList object.
 * 
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoListLength extends AlgoElement {

	private GeoList geoList; // input
	private GeoNumeric length; // output

	/**
	 * @param cons
	 *            construction
	 * @param geoList
	 *            list
	 */
	public AlgoListLength(Construction cons, GeoList geoList) {
		super(cons);
		this.geoList = geoList;

		length = new GeoNumeric(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Length;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geoList;

		setOnlyOutput(length);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getLength() {
		return length;
	}

	@Override
	public final void compute() {
		if (geoList.isDefined() || geoList.isUndefinedMatrix()) {
			length.setValue(geoList.size());
		} else {
			length.setUndefined();
		}
	}

}
